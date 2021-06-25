package com.example.instagram.adapters;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activities.StoryActivity;
import com.example.instagram.models.StoryData;
import com.example.instagram.models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<StoryData> storyDataList;

    public StoryAdapter(Context context, List<StoryData> storyDataList) {
        this.context = context;
        this.storyDataList = storyDataList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if (viewType==0){
            view = layoutInflater.inflate(R.layout.add_story_item, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.story_item, parent,  false);
        }

        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {

        StoryData storyData = storyDataList.get(position);
        userInfo(holder, storyData.getUserId(), position);

        if (holder.getAdapterPosition()!=0){
            seenStory(holder, storyData.getUserId());
        }

        if (holder.getAdapterPosition()==0){
            myStory(holder.addStory, holder.addStoryText, false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition()==0){
                    myStory(holder.addStory, holder.addStoryText, true);
                } else {

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return storyDataList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView unseenStory, seenStory, addStory;
        private TextView userName, addStoryText;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            unseenStory = itemView.findViewById(R.id.unseenStory_Id);
            seenStory = itemView.findViewById(R.id.seenStory_Id);
            addStory = itemView.findViewById(R.id.addStory_Id);
            userName = itemView.findViewById(R.id.storyProName_Id);
            addStoryText = itemView.findViewById(R.id.addStoryText_Id);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        return 1;
    }

    private void userInfo(StoryViewHolder viewHolder, String userId, int position){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = snapshot.getValue(UserData.class);
                if (userData.getProfilePic()!=null) {
                    Glide.with(context).load(userData.getProfilePic()).into(viewHolder.unseenStory);
                    if (position != 0){
                        Glide.with(context).load(userData.getProfilePic()).into(viewHolder.seenStory);
                        viewHolder.userName.setText(userData.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myStory (ImageView imageView, TextView textView, boolean isClicked){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("stories").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = 0;
                long currentTime = System.currentTimeMillis();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    StoryData storyData = dataSnapshot.getValue(StoryData.class);
                    if (currentTime>storyData.getStartTime() && currentTime<storyData.getEndTime()){
                        count++;
                    }
                }

                if (isClicked){

                    if (count>0){
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show story"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Add story"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        context.startActivity(new Intent(context, StoryActivity.class));
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.show();
                    }
                   else {
                        context.startActivity(new Intent(context, StoryActivity.class));
                    }

                } else {
                    if (count>0){
                        textView.setText("MyStory");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seenStory(StoryViewHolder viewHolder, String userId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("stories").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (!dataSnapshot.child("views")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis() < dataSnapshot.getValue(StoryData.class).getEndTime()){
                        i++;
                    }
                }

                if (i>0){
                    viewHolder.unseenStory.setVisibility(View.VISIBLE);
                    viewHolder.seenStory.setVisibility(View.GONE);
                } else {
                    viewHolder.unseenStory.setVisibility(View.GONE);
                    viewHolder.seenStory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
