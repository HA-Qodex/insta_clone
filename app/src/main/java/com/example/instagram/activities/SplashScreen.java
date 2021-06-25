package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.instagram.R;
import com.example.instagram.activities.LoginActivity;
import com.example.instagram.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);



        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startAnimation();
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                if(mUser != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else{
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });
        thread.start();
    }

    private void startAnimation() {

        for (int i=0; i<2; i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}