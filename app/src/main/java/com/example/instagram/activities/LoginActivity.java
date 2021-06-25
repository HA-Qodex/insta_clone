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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView signUpText;
    private EditText emailEdit, passwordEdit;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpText = findViewById(R.id.signUp_Id);
        emailEdit = findViewById(R.id.login_email_Id);
        passwordEdit = findViewById(R.id.login_pass_Id);
        loginButton = findViewById(R.id.logIn_button_Id);
        lottieAnimationView = findViewById(R.id.login_loading_anim_Id);

        mAuth = FirebaseAuth.getInstance();

        signUpText.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.signUp_Id){
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        }

        if (v.getId()==R.id.logIn_button_Id){
            userLogin();
        }

    }

    private void userLogin() {

        String email = emailEdit.getText().toString().replace(" ", "");
        String password = passwordEdit.getText().toString().replace(" ", "");
        if (email.equals("")){
            emailEdit.setError("Enter email address");
            emailEdit.setFocusable(true);
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("Invalid email");
            emailEdit.setFocusable(true);
        } else if (password.equals("")){
            passwordEdit.setError("Enter password");
            passwordEdit.setFocusable(true);
        } else if (password.length()<6){
            passwordEdit.setError("Invalid password");
            passwordEdit.setFocusable(true);
        } else {
            lottieAnimationView.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                        lottieAnimationView.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
            }). addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    lottieAnimationView.setVisibility(View.GONE);
                }
            });
        }

    }
}