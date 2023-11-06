package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmusictest.MyProgressDialog;
import com.example.appmusictest.R;
import com.example.appmusictest.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEt, emailEt, passwordEt,passwordCfEt;
    private TextView registerBt;
    private ImageButton backBtn;
    SessionManager sessionManager;
    private MyProgressDialog myProgressDialog;
    private static final String TAG = "Register_Activity";
    private String name,email, password, passwordCf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myProgressDialog = new MyProgressDialog(this);
        sessionManager = new SessionManager(getApplicationContext());
        initView();
        registerBt.setOnClickListener(v -> validateData());
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        passwordCfEt = findViewById(R.id.passwordCfEt);
        registerBt = findViewById(R.id.registerBtn);
        backBtn = findViewById(R.id.backBtn);
    }
    public void validateData() {
        name = nameEt.getText().toString();
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();
        passwordCf = passwordCfEt.getText().toString();


        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else if (passwordCf.isEmpty()) {
            Toast.makeText(this, "Confirm password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(passwordCf)) {
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show();
        }
        else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        myProgressDialog.show();
        myProgressDialog.setMessage("Create account ...");
        sessionManager.createSession(name, email);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}