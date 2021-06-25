package com.example.instagram.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.adapters.PostAdapter;
import com.example.instagram.adapters.UserAdapter;
import com.example.instagram.models.PostData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<PostData> postDataList;
    private PostAdapter postAdapter;
    private String postId;
    private ImageView goBackButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE );
        postId = sharedPreferences.getString("postId", "none");

        postDataList = new ArrayList<>();

        goBackButton = view.findViewById(R.id.backToProfile_Id);
        recyclerView = view.findViewById(R.id.postViewRecycler_Id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter  = new PostAdapter(getContext(), postDataList);
        recyclerView.setAdapter(postAdapter);


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new ProfileFragment()).commit();
            }
        });

        readPost();
        
        return view;
    }

    private void readPost() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postDataList.clear();
                PostData postData = snapshot.getValue(PostData.class);
                postDataList.add(postData);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}