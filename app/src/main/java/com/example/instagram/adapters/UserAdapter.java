package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activities.MainActivity;
import com.example.instagram.models.UserData;
import com.example.instagram.fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {

    private Context context;
    private List<UserData> userDataList;
    private List<UserData> userData;
    private FirebaseUser mUser;
    private boolean isFragment;

    public UserAdapter(Context context, List<UserData> userDataList, boolean isFragment) {
        this.context = context;
        this.userDataList = userDataList;
        this.isFragment = isFragment;
        this.userData = new ArrayList<>(userDataList);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.search_layout, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        UserData userData = userDataList.get(position);

        Glide.with(context).load(userData.getProfilePic()).into(holder.imageView);
        holder.name.setText(userData.getName());
        holder.email.setText(userData.getEmail());
        holder.follow.setVisibility(View.VISIBLE);

        if (userData.getUserId().equals(mUser.getUid())) {
            holder.follow.setVisibility(View.GONE);
        }

        onFollowing(userData.getUserId(), holder.follow);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFragment) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("userId", userData.getUserId());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new ProfileFragment()).commit();
                } else {

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("userId", userData.getUserId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }

            }
        });

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.follow.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference("follow").child(mUser.getUid()).child("following").child(userData.getUserId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference("follow").child(userData.getUserId()).child("followers").child(mUser.getUid()).setValue(true);
                    addNotification(userData.getUserId());
                } else {
                    FirebaseDatabase.getInstance().getReference("follow").child(mUser.getUid()).child("following").child(userData.getUserId()).removeValue();

                    FirebaseDatabase.getInstance().getReference("follow").child(userData.getUserId()).child("followers").child(mUser.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private TextView name, email, follow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.searchProfile_Id);
            name = itemView.findViewById(R.id.searchName_Id);
            email = itemView.findViewById(R.id.searchEmail_Id);
            follow = itemView.findViewById(R.id.followButton_Id);
        }
    }

    private void onFollowing(final String userId, final TextView button) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("follow")
                .child(mUser.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.child(userId).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<UserData> userList = new ArrayList<>();

            if (!constraint.toString().isEmpty()) {

                for (UserData data : userData) {
                    if (data.getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            data.getEmail().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        userList.add(data);
                    }
                }

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = userList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            userDataList.clear();
            userDataList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    private void addNotification(String userId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

        HashMap<String, Object> notMap = new HashMap<>();
        notMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        notMap.put("postId", "");
        notMap.put("text", "started following you");
        notMap.put("isPost", "false");

        databaseReference.push().setValue(notMap);

    }
}
