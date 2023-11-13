package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmusictest.SessionManager;
import com.example.appmusictest.dialog.MyProgressDialog;
import com.example.appmusictest.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLoginEt,passwordLoginEt;
    private TextView loginBtn1;
    private TextView noAccountTv;
    private MyProgressDialog myProgressDialog;
    private static final String TAG = "Login_Activity";
    SessionManager sessionManager;

    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
        myProgressDialog = new MyProgressDialog(this);
       initView();
       noAccountTv.setOnClickListener(v -> {
           startActivity(new Intent(this, RegisterActivity.class));
       });
       loginBtn1.setOnClickListener(v -> {
            validateData();

       });

    }

    private void validateData() {
        email = emailLoginEt.getText().toString();
        password = passwordLoginEt.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else {
            loginUser();
        }
    }

    private void loginUser() {
        myProgressDialog.show();
        myProgressDialog.setMessage("Logging account");
        sessionManager.createSession("", email);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void initView() {
        emailLoginEt = findViewById(R.id.emailLoginEt);
        passwordLoginEt = findViewById(R.id.passwordLoginEt);
        loginBtn1 = findViewById(R.id.loginBtn1);
        noAccountTv = findViewById(R.id.noAccountTv);
    }
}