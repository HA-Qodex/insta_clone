package com.example.instagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.models.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backButton, doneButton;
    private CircleImageView proImage;
    private TextView uploadButton;
    private EditText nameEdit, userNameEdit, phoneEdit, bioEdit;
    private final int REQUEST_CODE = 101;
    private Uri uri;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        backButton = findViewById(R.id.editGoBackButton_Id);
        doneButton = findViewById(R.id.editPostButton_Id);
        proImage = findViewById(R.id.editProPic_Id);
        uploadButton = findViewById(R.id.editButton_Id);
        nameEdit = findViewById(R.id.editName_Id);
        userNameEdit = findViewById(R.id.editUserName_Id);
        phoneEdit = findViewById(R.id.editPhone_Id);
        bioEdit = findViewById(R.id.editBio_Id);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        backButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);

        getServerData();
    }

    private void getServerData() {
        String userId = mUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userData = snapshot.getValue(UserData.class);
                nameEdit.setText(userData.getName());
                userNameEdit.setText(userData.getUserName());
                phoneEdit.setText(userData.getPhone());
                bioEdit.setText(userData.getBio());
                Glide.with(getApplicationContext()).load(userData.getProfilePic()).into(proImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editGoBackButton_Id) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        if (v.getId() == R.id.editPostButton_Id) {
            updateData();
        }

        if (v.getId() == R.id.editButton_Id) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_CODE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Glide.with(getApplicationContext()).load(uri).into(proImage);
        }
    }

    private String fileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updateData() {
        String userId = mUser.getUid();

        String name = nameEdit.getText().toString();
        String userName = userNameEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String bio = bioEdit.getText().toString();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(userId);
        StorageReference storage = storageReference.child("IMG"+System.currentTimeMillis()+"."+fileExtension(uri));
        storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String imageUrl = uriTask.getResult().toString();

                HashMap<String, Object> dataMap = new HashMap<>();
                dataMap.put("name", name);
                dataMap.put("userName", userName);
                dataMap.put("phone", phone);
                dataMap.put("bio", bio);
                dataMap.put("profilePic", imageUrl);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                databaseReference.updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(EditProfileActivity.this, "Data update successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Data upload failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}