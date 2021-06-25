package com.example.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView loginText;
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loginText = findViewById(R.id.reg_to_login_Id);
        nameEditText = findViewById(R.id.reg_name_Id);
        emailEditText = findViewById(R.id.reg_email_Id);
        phoneEditText = findViewById(R.id.reg_phone_Id);
        passwordEditText = findViewById(R.id.reg_pass_Id);
        submitButton = findViewById(R.id.reg_button_Id);
        lottieAnimationView = findViewById(R.id.reg_loading_anim_Id);

        mAuth = FirebaseAuth.getInstance();

        submitButton.setOnClickListener(this);
        loginText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.reg_to_login_Id) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        if (v.getId() == R.id.reg_button_Id) {
            storeUserData();
        }

    }

    private void storeUserData() {

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString().replace(" ", "");
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString().replace(" ", "");

        if (name.equals("")) {
            nameEditText.setError("Enter your name");
            nameEditText.setFocusable(true);
        } else if(email.equals("")){
            emailEditText.setError("Enter email");
            emailEditText.setFocusable(true);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            emailEditText.setFocusable(true);
        } else if(phone.equals("")){
            phoneEditText.setError("Enter phone no");
            phoneEditText.setFocusable(true);
        } else if(password.equals("")){
            passwordEditText.setError("Enter email");
            passwordEditText.setFocusable(true);
        } else if(password.length()<6){
            passwordEditText.setError("Password length must be 6");
            passwordEditText.setFocusable(true);
        } else {
            lottieAnimationView.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser() != null) {
                            userId = mAuth.getCurrentUser().getUid();

                        HashMap<String, Object> dataMap = new HashMap<>();
                        dataMap.put("name", name);
                        dataMap.put("email", email);
                        dataMap.put("phone", phone);
                        dataMap.put("password", password);
                        dataMap.put("profilePic", "");
                        dataMap.put("userId", userId);

                        databaseReference = FirebaseDatabase.getInstance().getReference("users");

                        databaseReference.child(userId).setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    lottieAnimationView.setVisibility(View.GONE);
                                    Toast.makeText(RegistrationActivity.this, "Data upload successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegistrationActivity.this, "Data upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistrationActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}