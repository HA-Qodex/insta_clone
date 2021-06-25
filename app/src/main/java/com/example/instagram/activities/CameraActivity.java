package com.example.instagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView cancelButton, postImage, postButton;
    private EditText description;
    private static final int REQUEST_CODE = 100;
    private Uri uri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser mUser;
    private CardView AnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        postImage = findViewById(R.id.postImage_Id);
        postButton = findViewById(R.id.postButton_Id);
        cancelButton = findViewById(R.id.cancelButton_Id);
        description = findViewById(R.id.postDes_Id);
        AnimationView = findViewById(R.id.loadingAnim_Id);

        uploadImage();

        cancelButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelButton_Id) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        if (v.getId() == R.id.postButton_Id) {
            postImage();
        }
    }

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Glide.with(getApplicationContext()).load(uri).into(postImage);
        }
    }

    private String fileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void postImage() {

        String postDescription = description.getText().toString();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        AnimationView.setVisibility(View.VISIBLE);
        if (mUser != null) {
            AnimationView.setVisibility(View.VISIBLE);
            storageReference = FirebaseStorage.getInstance().getReference(mUser.getUid());
            StorageReference storage = storageReference.child("IMG" + System.currentTimeMillis() + "." + fileExtension(uri));
            storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri imageUri = uriTask.getResult();
                    String postImageUrl = imageUri.toString();

                    databaseReference = FirebaseDatabase.getInstance().getReference("posts");
                    String postId = databaseReference.push().getKey();

                    HashMap<String, String> postMap = new HashMap<>();
                    postMap.put("imageUrl", postImageUrl);
                    postMap.put("description", postDescription);
                    postMap.put("userId", mUser.getUid());
                    postMap.put("postId", postId);

                    databaseReference.child(postId).setValue(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(CameraActivity.this, "Data stored successfully", Toast.LENGTH_SHORT).show();
                                AnimationView.setVisibility(View.GONE);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AnimationView.setVisibility(View.GONE);
                            Toast.makeText(CameraActivity.this, "Data store failed\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AnimationView.setVisibility(View.GONE);
                    Toast.makeText(CameraActivity.this, "Image upload failed\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}