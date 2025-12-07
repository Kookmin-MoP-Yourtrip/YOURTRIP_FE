package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.auth.model.FindPasswordResetRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPasswordResetActivity extends AppCompatActivity {

    private static final String TAG = "FindPwReset";

    private EditText edtPassword, edtConfirm;
    private TextView tvError;
    private Button btnNext;

    private ApiService apiService;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_reset);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) { finish(); return; }

        apiService = RetrofitClient.getAuthService(this);

        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtPasswordConfirm);
        tvError = findViewById(R.id.tvPasswordError);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtPassword.addTextChangedListener(watcher);
        edtConfirm.addTextChangedListener(watcher);

        btnNext.setOnClickListener(v -> resetPassword());
    }

    private void validate() {
        String pw = edtPassword.getText().toString();
        String cf = edtConfirm.getText().toString();

        if (!pw.equals(cf)) {
            tvError.setText("비밀번호가 일치하지 않습니다.");
            tvError.setVisibility(TextView.VISIBLE);
            btnNext.setEnabled(false);
            return;
        }

        if (pw.length() < 8) {
            tvError.setText("비밀번호는 8자 이상이어야 합니다.");
            tvError.setVisibility(TextView.VISIBLE);
            btnNext.setEnabled(false);
            return;
        }

        tvError.setVisibility(TextView.GONE);
        btnNext.setEnabled(true);
    }

    private void resetPassword() {
        String newPassword = edtPassword.getText().toString();
        Log.d(TAG, "resetPassword() 실행됨, newPassword=" + newPassword);
        FindPasswordResetRequest request = new FindPasswordResetRequest(userEmail, newPassword);

        apiService.findPasswordReset(request).enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "API 응답 도착, code=" + response.code());

                if (response.isSuccessful()) {
                    Intent i = new Intent(FindPasswordResetActivity.this, FindPasswordCompleteActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(FindPasswordResetActivity.this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FindPasswordResetActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

