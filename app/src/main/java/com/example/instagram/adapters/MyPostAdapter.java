package com.example.instagram.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.fragments.PostFragment;
import com.example.instagram.models.PostData;
import com.example.instagram.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.PostViewHolder> {

    private Context context;
    private List<PostData> postDataList;

    public MyPostAdapter(Context context, List<PostData> postDataList) {
        this.context = context;
        this.postDataList = postDataList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.my_post_layout, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        PostData postData = postDataList.get(position);
        Glide.with(context).load(postData.getImageUrl()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postId", postData.getPostId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new PostFragment()).commit();


            }
        });

    }

    @Override
    public int getItemCount() {
        return postDataList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.post_Id);
        }
    }
}
