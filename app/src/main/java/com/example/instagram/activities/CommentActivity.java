package com.example.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.adapters.CommentAdapter;
import com.example.instagram.models.CommentData;
import com.example.instagram.R;
import com.example.instagram.models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CircleImageView imageView;
    private EditText editText;
    private TextView post;
    private List<CommentData> commentDataList;
    private CommentAdapter adapter;

    private String postId, userId;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        toolbar = findViewById(R.id.commentToolbar_Id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        commentDataList = new ArrayList<>();

        recyclerView = findViewById(R.id.commentRecycler_Id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new CommentAdapter(getApplicationContext(), commentDataList);
        recyclerView.setAdapter(adapter);


        imageView = findViewById(R.id.commentProPic_Id);
        editText = findViewById(R.id.commentText_Id);
        post = findViewById(R.id.postButton_Id);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        userId = intent.getStringExtra("userId");

        getProImage();
        readComments();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText().toString().equals("")) {
                    editText.setHint("Field can't be empty");
                    editText.setHintTextColor(getResources().getColor(R.color.comment));
                    editText.setFocusable(true);
                } else {
                    postComment();
                }

            }
        });
    }

    private void postComment() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("comments").child(postId);

        HashMap<String, String> commentMap = new HashMap<>();
        commentMap.put("comment", editText.getText().toString());
        commentMap.put("userId", mUser.getUid());

        databaseReference.push().setValue(commentMap);
        addNotification();
        editText.setText("");

    }

    private void getProImage(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userData = snapshot.getValue(UserData.class);
                Glide.with(getApplicationContext()).load(userData.getProfilePic()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addNotification (){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

        HashMap<String, Object> notMap = new HashMap<>();
        notMap.put("userId", currentUserId);
        notMap.put("postId", postId);
        notMap.put("text", "commented: "+editText.getText().toString());
        notMap.put("isPost", "true");

        if (!currentUserId.equals(userId)){
            databaseReference.push().setValue(notMap);
        }
    }

    private void readComments(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("comments").child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentDataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CommentData commentData = dataSnapshot.getValue(CommentData.class);
                    commentDataList.add(commentData);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}