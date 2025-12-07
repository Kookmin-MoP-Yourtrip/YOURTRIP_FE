package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.auth.model.FindPasswordEmailRequest;
import com.example.yourtrip.auth.model.VerificationRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPasswordVerifyActivity extends AppCompatActivity {

    private static final String TAG = "FindPwVerify";

    private EditText edtCode, edtEmail;
    private Button btnNext, btnResend;
    private TextView tvError;
    private ApiService apiService;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_verify);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) { finish(); return; }

        apiService = RetrofitClient.getAuthService(this);

        edtEmail = findViewById(R.id.tvEmail);
        edtCode = findViewById(R.id.edtCode);
        btnNext = findViewById(R.id.btnNext);
        btnResend = findViewById(R.id.btnResend);
        tvError = findViewById(R.id.tvCodeError);

        edtEmail.setText(userEmail);
        edtEmail.setEnabled(false);

        btnNext.setEnabled(false);

        edtCode.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNext.setEnabled(s.length() == 6);
                tvError.setVisibility(TextView.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnNext.setOnClickListener(v -> verifyCode());
        btnResend.setOnClickListener(v -> resendCode());
    }

    private void verifyCode() {
        String code = edtCode.getText().toString().trim();
        Log.d(TAG, "verifyCode() 호출됨, 입력 코드 = " + code);

        VerificationRequest request = new VerificationRequest(userEmail, code);

        apiService.findPasswordVerify(request).enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "API 응답 도착, code = " + response.code());

                if (response.isSuccessful()) {
                    Intent intent = new Intent(FindPasswordVerifyActivity.this, FindPasswordResetActivity.class);
                    intent.putExtra("email", userEmail);
                    startActivity(intent);
                } else {
                    tvError.setText("인증번호가 올바르지 않습니다.");
                    tvError.setVisibility(TextView.VISIBLE);
                }
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FindPasswordVerifyActivity.this, "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendCode() {
        // 재전송은 'email만' 보내는 발송 API 호출
        FindPasswordEmailRequest request = new FindPasswordEmailRequest(userEmail);

        apiService.findPasswordEmail(request).enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    Toast.makeText(FindPasswordVerifyActivity.this, "인증번호를 재전송했습니다.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(FindPasswordVerifyActivity.this, "재전송 실패", Toast.LENGTH_SHORT).show();
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FindPasswordVerifyActivity.this, "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
