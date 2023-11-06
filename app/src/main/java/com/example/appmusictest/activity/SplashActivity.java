package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.appmusictest.R;
import com.example.appmusictest.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(getApplicationContext());

            if (sessionManager.checkSession()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            else {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        },2000);
    }
}