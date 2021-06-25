package com.example.instagram.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.adapters.MyPostAdapter;
import com.example.instagram.models.PostData;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;
    private String userId;
    private List<PostData> postDataList;
    private MyPostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("userIDPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "none");

        postDataList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.albumRecycler_Id);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        postAdapter = new MyPostAdapter(getContext(), postDataList);
        recyclerView.setAdapter(postAdapter);
        getPost();
        return view;
    }

    private void getPost(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PostData postData = dataSnapshot.getValue(PostData.class);
                    assert postData != null;
                    if (postData.getUserId().equals(userId)){
                        postDataList.add(postData);
                    }
                }

                Collections.reverse(postDataList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}