package com.example.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.adapters.UserAdapter;
import com.example.instagram.models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView toolTitle;
    private Toolbar toolbar;
    private String userId, title, postId;
    private List<String> userList;
    private List<UserData> userDataList;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = findViewById(R.id.listToolbar_Id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        postId = intent.getStringExtra("postId");
        title = intent.getStringExtra("title");

        userDataList = new ArrayList<>();
        userList = new ArrayList<>();

        toolTitle = toolbar.findViewById(R.id.listTitle_Id);
        toolTitle.setText(title);
        recyclerView = findViewById(R.id.listRecycler_Id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UserAdapter(getApplicationContext(), userDataList, false);
        recyclerView.setAdapter(adapter);

        switch (title){
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;

            case "likes":
                getLikes();
                break;
        }

    }

    private void getFollowing() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("follow").child(userId).child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userList.add(dataSnapshot.getKey());
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowers() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("follow").child(userId).child("followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userList.add(dataSnapshot.getKey());
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getLikes() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("likes").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userList.add(dataSnapshot.getKey());
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUsers(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    for (String id : userList){
                        if (id.equals(userData.getUserId())){
                            userDataList.add(userData);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}