package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.models.CommentData;
import com.example.instagram.activities.MainActivity;
import com.example.instagram.R;
import com.example.instagram.models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<CommentData> commentDataList;
    private FirebaseUser mUser;

    public CommentAdapter(Context context, List<CommentData> commentDataList) {
        this.context = context;
        this.commentDataList = commentDataList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.comment_layout, parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        CommentData commentData = commentDataList.get(position);

        holder.comment.setText(commentData.getComment());
        getUserInfo(commentData.getUserId(), holder.imageView, holder.name);

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("userId", commentData.getUserId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("userId", commentData.getUserId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentDataList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private TextView name, comment;
        private ImageView like;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.commentProImage_Id);
            name = itemView.findViewById(R.id.commentProName_Id);
            comment = itemView.findViewById(R.id.commentTextView_Id);
            like = itemView.findViewById(R.id.commentLike_Id);
        }
    }

    private void getUserInfo(String userId, ImageView proImage, TextView proName){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userData = snapshot.getValue(UserData.class);
                Glide.with(context).load(userData.getProfilePic()).into(proImage);
                proName.setText(userData.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
