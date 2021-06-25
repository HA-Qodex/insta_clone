package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.activities.ListActivity;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.PostData;
import com.example.instagram.R;
import com.example.instagram.models.UserData;
import com.example.instagram.activities.CommentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<PostData> postDataList;

    public PostAdapter(Context context, List<PostData> postDataList) {
        this.context = context;
        this.postDataList = postDataList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.post_sample_layout, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {



        PostData postData = postDataList.get(position);

        Glide.with(context).load(postData.getImageUrl()).into(holder.postImage);
        if (postData.getDescription().equals("")) {
            holder.postDec.setVisibility(View.GONE);
        } else {
            holder.postDec.setText(postData.getDescription());
        }

        publisherInfo(holder.proName, holder.proImage, postData.getUserId());
        isLiked(postData.getPostId(), holder.like);
        likeCounter(postData.getPostId(), holder.likeCount);
        getComments(postData.getPostId(), holder.commentCount);
        isSaved(postData.getPostId(), holder.save);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference("likes")
                            .child(postData.getPostId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    addNotification(postData.getUserId(), postData.getPostId());
                } else {
                    FirebaseDatabase.getInstance().getReference("likes")
                            .child(postData.getPostId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", postData.getPostId());
                intent.putExtra("userId", postData.getUserId());
                context.startActivity(intent);
            }
        });

        holder.commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", postData.getPostId());
                intent.putExtra("userId", postData.getUserId());
                context.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference("saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(postData.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference("saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(postData.getPostId()).removeValue();
                }
            }
        });

        holder.proName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("userId", postData.getUserId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new ProfileFragment()).commit();

            }
        });

        holder.proImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("userId", postData.getUserId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new ProfileFragment()).commit();

            }
        });

        holder.likeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("postId", postData.getPostId());
                intent.putExtra("title", "likes");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postDataList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView proImage, postImage, like, comment, dotMenu, save;
        private TextView proName, postDec, likeCount, commentCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            proImage = itemView.findViewById(R.id.postProImage_Id);
            proName = itemView.findViewById(R.id.postProName_Id);
            postImage = itemView.findViewById(R.id.postImageView_Id);
            postDec = itemView.findViewById(R.id.postDesc_Id);
            dotMenu = itemView.findViewById(R.id.dotMenu_Id);
            like = itemView.findViewById(R.id.postLike_Id);
            comment = itemView.findViewById(R.id.postComment_Id);
            save = itemView.findViewById(R.id.save_Id);

            likeCount = itemView.findViewById(R.id.postLikeCount_Id);
            commentCount = itemView.findViewById(R.id.postCommentText_Id);

        }
    }

    private void isLiked(String postId, ImageView like){

        final  FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("likes").child(postId);

        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(mUser.getUid()).exists()){
                    like.setImageResource(R.drawable.ic_heart_red);
                    like.setTag("liked");
                } else {
                    like.setImageResource(R.drawable.ic_heart);
                    like.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void likeCounter(String postId, TextView likeCount ){

        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("likes").child(postId);

        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount()>1){
                    likeCount.setText(snapshot.getChildrenCount()+" likes");
                } else {
                    likeCount.setText(snapshot.getChildrenCount()+" like");
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void publisherInfo(final TextView name, final ImageView profileImage, final String userId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userData = snapshot.getValue(UserData.class);

                name.setText(userData.getName());
                Glide.with(context).load(userData.getProfilePic()).into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getComments(String postId, TextView commentCounter){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("comments").child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                commentCounter.setText("View all " + snapshot.getChildrenCount()+" comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isSaved(String postId, ImageView saveImage){

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("saves").child(mUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postId).exists()){
                    saveImage.setImageResource(R.drawable.ic_bookmark);
                    saveImage.setTag("saved");
                } else {
                    saveImage.setImageResource(R.drawable.ic_save);
                    saveImage.setTag("save");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification (String userId, String postId){

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

        HashMap<String, Object> notMap = new HashMap<>();
        notMap.put("userId", currentUserId);
        notMap.put("postId", postId);
        notMap.put("text", "Liked your post");
        notMap.put("isPost", "true");

        if (!currentUserId.equals(userId)){
            databaseReference.push().setValue(notMap);
        }


    }
}
