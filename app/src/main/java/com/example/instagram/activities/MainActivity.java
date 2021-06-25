package com.example.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.instagram.R;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.NotificationFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frameLayout_Id);
        bottomNavigationView = findViewById(R.id.bottom_nav_Id);

        Bundle intent = getIntent().getExtras();

        if (intent != null) {
            String userId = intent.getString("userId");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("userId", userId);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, new HomeFragment()).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                switch (item.getItemId()) {

                    case R.id.home_Id:
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.search_Id:
                        selectedFragment = new SearchFragment();
                        break;

                    case R.id.camera_Id:
                        selectedFragment = null;
                        startActivity(new Intent(MainActivity.this, CameraActivity.class));
                        break;

                    case R.id.not_Id:
                        selectedFragment = new NotificationFragment();
                        break;

                    case R.id.profile_Id:
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();

                        selectedFragment = new ProfileFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Id, selectedFragment).commit();
                }


                return true;
            }
        });

    }
}