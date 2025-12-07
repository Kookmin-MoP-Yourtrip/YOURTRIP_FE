package com.example.yourtrip.auth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.auth.model.FindPasswordEmailRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPasswordEmailActivity extends AppCompatActivity {

    private static final String TAG = "FindPwEmail";

    private EditText edtEmail;
    private Button btnNext;
    private TextView tvEmailError;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_email);

        // Retrofit 서비스 초기화
        apiService = RetrofitClient.getAuthService(this);

        initViews();
        setupHeader();
        setupListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        btnNext = findViewById(R.id.btnNext);
        tvEmailError = findViewById(R.id.tvEmailError);

        // 처음에는 버튼 비활성화
        btnNext.setEnabled(false);
    }

    private void setupHeader() {
        View headerView = findViewById(R.id.password_header_view);

        TextView headerTitle = headerView.findViewById(R.id.tv_title);
        ProgressBar progressBar = headerView.findViewById(R.id.progressSignup);

        if (headerTitle != null) {
            headerTitle.setText("비밀번호 찾기");
        }
        if (progressBar != null) {
            progressBar.setMax(3);
            progressBar.setProgress(1);
        }

        ImageView btnBack = headerView.findViewById(R.id.btnBack);
        if(btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupListeners() {
        // 이메일 입력창에 텍스트 변경 감지 리스너 추가
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 이메일 형식 유효성 검사
                String email = s.toString().trim();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    btnNext.setEnabled(true); // 유효하면 버튼 활성화
                    tvEmailError.setVisibility(View.GONE); // 에러 메시지 숨김
                } else {
                    btnNext.setEnabled(false); // 유효하지 않으면 버튼 비활성화
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // '다음' 버튼 클릭 리스너
        btnNext.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            requestEmailVerification(email);
        });
    }

    // 인증번호 발송 API를 호출하는 메서드
    private void requestEmailVerification(String email) {
        FindPasswordEmailRequest emailRequest = new FindPasswordEmailRequest(email);
        apiService.findPasswordEmail(emailRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "API 응답 도착, code = " + response.code());
                if (response.isSuccessful()) {
                    // --- 성공 시 ---
                    Log.d(TAG, "API 성공 → 다음 화면으로 이동");
                    Toast.makeText(FindPasswordEmailActivity.this, "인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FindPasswordEmailActivity.this, FindPasswordVerifyActivity.class);
                    intent.putExtra("email", email); // 다음 화면으로 이메일 전달
                    startActivity(intent);

                } else {
                    // --- 실패 시 (가입되지 않은 이메일 등)
                    Log.e(TAG, "API 실패 → 가입되지 않은 이메일");
                    tvEmailError.setText("가입되지 않은 이메일입니다.");
                    tvEmailError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // --- 네트워크 오류 시 ---
                Log.e(TAG, "API 통신 실패", t);
                Toast.makeText(FindPasswordEmailActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
