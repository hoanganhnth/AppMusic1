package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmusictest.dialog.MyProgressDialog;
import com.example.appmusictest.R;
import com.example.appmusictest.SessionManager;
import com.example.appmusictest.model.api.RegisterResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        registerBt.setOnClickListener(v -> {
            hideKeyboard();
            validateData();
        });
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
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
            Toast.makeText(this, "Tên đăng nhập bỏ trống", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Mật khẩu bỏ trống", Toast.LENGTH_SHORT).show();
        }
        else if (passwordCf.isEmpty()) {
            Toast.makeText(this, "Hãy xác nhận lại mật khẩu", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(passwordCf)) {
            Toast.makeText(this, "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
        }
        else {
            createUserAccount();
        }
    }

    private void createUserAccount() {

        myProgressDialog.show();
        myProgressDialog.setMessage("Create account ...");
        performSignUp();

    }

    private void performSignUp() {
        DataService dataService = ApiService.getService();
        Call<RegisterResponse> callback = dataService.performUserSignUp(name, email, password);
        callback.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        Toast.makeText(RegisterActivity.this, "Đăng kí thành công. Bây giờ hãy đăng nhập vào ứng dụng.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Email đã tồn tại. Vui lòng nhập email khác. ", Toast.LENGTH_SHORT).show();
                    }
                }
                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                myProgressDialog.dismiss();
                Log.d(TAG, "Not get data from server due to " + t.getMessage());
            }
        });
    }
}