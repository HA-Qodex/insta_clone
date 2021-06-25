package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.activities.LoginActivity;
import com.example.instagram.activities.RegistrationActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView login, registration;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login = findViewById(R.id.login_Id);
        registration = findViewById(R.id.registration_Id);

        login.setOnClickListener(this);
        registration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.login_Id){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        if (v.getId()==R.id.registration_Id){
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
        }
    }
}