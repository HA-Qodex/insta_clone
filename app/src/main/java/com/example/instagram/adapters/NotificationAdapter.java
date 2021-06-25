package com.example.instagram.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.fragments.PostFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.NotificationData;
import com.example.instagram.models.PostData;
import com.example.instagram.models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotViewHolder> {

    private Context context;
    private List<NotificationData> notificationDataList;

    public NotificationAdapter(Context context, List<NotificationData> notificationDataList) {
        this.context = context;
        this.notificationDataList = notificationDataList;
    }

    @NonNull
    @Override
    public NotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.notification_sample, parent, false);

        return new NotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotViewHolder holder, int position) {

        NotificationData notificationData = notificationDataList.get(position);

        getUserInfo(holder.proImage, holder.proName, notificationData.getUserId());

        if (notificationData.getIsPost().equals("true")){
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notificationData.getPostId());
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.notText.setText(notificationData.getText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationData.getIsPost().equals("true")){
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postId", notificationData.getPostId());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new PostFragment()).commit();

                } else {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("userId", notificationData.getUserId());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new ProfileFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationDataList.size();
    }

    public class NotViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView proImage;
        private ImageView postImage;
        private TextView proName, notText;

        public NotViewHolder(@NonNull View itemView) {
            super(itemView);

            proImage = itemView.findViewById(R.id.notProImage_Id);
            proName = itemView.findViewById(R.id.notProName_Id);
            postImage = itemView.findViewById(R.id.notImage_Id);
            notText = itemView.findViewById(R.id.notText_Id);
        }
    }

    private void getUserInfo(ImageView proImage, TextView proName, String userId){

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

    private void getPostImage(ImageView postImage, String postId){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PostData postData = snapshot.getValue(PostData.class);
                Glide.with(context).load(postData.getImageUrl()).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
