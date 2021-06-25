package com.example.instagram.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.adapters.PostAdapter;
import com.example.instagram.adapters.StoryAdapter;
import com.example.instagram.models.PostData;
import com.example.instagram.R;
import com.example.instagram.models.StoryData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView postRecyclerView, storyRecyclerView;
    private List<PostData> postDataList;
    private List<StoryData> storyDataList;
    private PostAdapter adapter;
    private StoryAdapter storyAdapter;
    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postRecyclerView = view.findViewById(R.id.postRecycle_Id);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postRecyclerView.setLayoutManager(linearLayoutManager);

        postDataList = new ArrayList<>();

        adapter = new PostAdapter(getContext(), postDataList);
        postRecyclerView.setAdapter(adapter);

        storyDataList = new ArrayList<>();
        storyRecyclerView = view.findViewById(R.id.storyRecycle_Id);
        storyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRecyclerView.setLayoutManager(linearLayoutManager1);
        storyAdapter = new StoryAdapter(getContext(), storyDataList);
        storyRecyclerView.setAdapter(storyAdapter);

        checkFollowing();

        return view;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference("follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        followingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                followingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    followingList.add(dataSnapshot.getKey());
                }
                readPost();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPost() {

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("posts");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postDataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostData postData = dataSnapshot.getValue(PostData.class);

                    if (postData.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        postDataList.add(postData);
                    } else {
                        for (String id : followingList) {
                            if (postData.getUserId().equals(id) ) {
                                postDataList.add(postData);
                            }
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

    private void readStory(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("stories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentTime = System.currentTimeMillis();
                storyDataList.clear();
                storyDataList.add(new StoryData(FirebaseAuth.getInstance().getCurrentUser().getUid(), "", "", 0, 0));
                for (String id : followingList){
                    int storyCount = 0;
                    StoryData story = null;

                    for (DataSnapshot dataSnapshot : snapshot.child(id).getChildren()){
                        story = dataSnapshot.getValue(StoryData.class);
                        if (currentTime > story.getStartTime() && currentTime < story.getEndTime()){
                            storyCount ++;
                        }
                    }
                    if (storyCount > 0){
                        storyDataList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}