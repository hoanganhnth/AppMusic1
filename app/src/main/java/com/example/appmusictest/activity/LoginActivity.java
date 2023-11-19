package com.example.appmusictest.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmusictest.SessionManager;
import com.example.appmusictest.dialog.MyProgressDialog;
import com.example.appmusictest.R;
import com.example.appmusictest.model.api.LoginResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
           hideKeyboard();
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
        myProgressDialog.setMessage("Logging account ...");
        performUserLogin();

    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void performUserLogin() {
        DataService dataService = ApiService.getService();
        Call<LoginResponse> callback = dataService.performUserLoginIn(email, password);

        callback.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                        sessionManager.createSession(response.body().getUsername(), email, response.body().getIdUser());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else if (response.body().getMessage().equals("Mật khẩu không đúng")){
                        Toast.makeText(LoginActivity.this, "Mật khẩu không đúng. Vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại.", Toast.LENGTH_SHORT).show();

                    }
                }
                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                myProgressDialog.dismiss();
                Log.d(TAG, "Not get data from server due to " + t.getMessage());
            }
        });
    }

    private void initView() {
        emailLoginEt = findViewById(R.id.emailLoginEt);
        passwordLoginEt = findViewById(R.id.passwordLoginEt);
        loginBtn1 = findViewById(R.id.loginBtn1);
        noAccountTv = findViewById(R.id.noAccountTv);
    }
}