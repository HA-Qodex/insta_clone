package com.example.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.activities.ListActivity;
import com.example.instagram.activities.EditProfileActivity;
import com.example.instagram.activities.LoginActivity;
import com.example.instagram.models.PostData;
import com.example.instagram.R;
import com.example.instagram.models.UserData;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView profileImage, proMenu;
    private TextView name, post, followers, following, bio, userName;
    private Button editButton;
    private String userId;
    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tabLayout = view.findViewById(R.id.profileTab_Id);
        viewPager = view.findViewById(R.id.proPager_Id);

        profileImage = view.findViewById(R.id.profilePic_Id);
        name = view.findViewById(R.id.profileName_Id);
        post = view.findViewById(R.id.profilePost_Id);
        followers = view.findViewById(R.id.profileFollowers_Id);
        following = view.findViewById(R.id.profileFollowing_Id);
        bio = view.findViewById(R.id.profileBio_Id);
        userName = view.findViewById(R.id.proUserName_Id);
        editButton = view.findViewById(R.id.editPro_Id);
        proMenu = view.findViewById(R.id.profileMenu_Id);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        proMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser != null) {
                    FirebaseAuth.getInstance().signOut();
                    getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        });

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "none");

        SharedPreferences.Editor editor = getContext().getSharedPreferences("userIDPrefs", Context.MODE_PRIVATE).edit();
        editor.putString("userId", userId);
        editor.apply();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.proPager_Id, new AlbumFragment()).commit();


        viewPager.setAdapter(new TabAdapter(getActivity().getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_pixels_us);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_save_us);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        userInfo();
        getFollowers();
        getFollowing();
        getPost();

        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);

        if (userId.equals(mUser.getUid())){
            editButton.setText("Edit Profile");
        } else {
            checkFollow();
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText = editButton.getText().toString();

                if (getText.equals("Edit Profile")) {

                    startActivity(new Intent(getActivity(), EditProfileActivity.class));

                } else if (getText.equals("Follow")) {

                    FirebaseDatabase.getInstance().getReference("follow").child(mUser.getUid()).child("following").child(userId).setValue(true);
                    FirebaseDatabase.getInstance().getReference("follow").child(userId).child("followers").child(mUser.getUid()).setValue(true);
                    addNotification();
                } else if (getText.equals("Following")) {
                    FirebaseDatabase.getInstance().getReference("follow").child(mUser.getUid()).child("following").child(userId).removeValue();
                    FirebaseDatabase.getInstance().getReference("follow").child(userId).child("followers").child(mUser.getUid()).removeValue();
                }
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("title", "followers");
                startActivity(intent);

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("title", "following");
                startActivity(intent);

            }
        });

        return view;
    }

    private void addNotification (){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

        HashMap<String, Object> notMap = new HashMap<>();
        notMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        notMap.put("postId", "");
        notMap.put("text", "started following you");
        notMap.put("isPost", "false");

        databaseReference.push().setValue(notMap);
    }

    static class TabAdapter extends FragmentPagerAdapter {

        String[] items = {"Album", "Save"};

        public TabAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new AlbumFragment();
                case 1:
                    return new SaveFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return items.length;
        }
    }

    private void userInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userData = snapshot.getValue(UserData.class);
                userName.setText(userData.getUserName());
                Glide.with(getContext()).load(userData.getProfilePic()).into(profileImage);
                name.setText(userData.getName());
                bio.setText(userData.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkFollow(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("follow").child(mUser.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(userId).exists()){
                    editButton.setText("Following");
                } else {
                    editButton.setText("Follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getFollowers(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("follow").child(userId).child("followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                followers.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getFollowing(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("follow").child(userId).child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                following.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPost(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PostData postData = dataSnapshot.getValue(PostData.class);
                    if (postData.getUserId().equals(userId)){
                        i++;
                    }
                }

                post.setText(""+i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}