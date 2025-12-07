package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.auth.model.EmailRequest;
import com.example.yourtrip.auth.model.VerificationRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupVerificationActivity extends AppCompatActivity {

    private TextView tvEmail, tvTitle, tvCodeError;
    private EditText edtCode;
    private Button btnNext;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private TextView btnResend;  // 인증번호 다시 받기 버튼

    // 이메일을 Intent로 전달받은 후 사용할 email 변수 선언
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_verification);

        // View 초기화
        tvEmail = findViewById(R.id.tvEmail);
        tvTitle = findViewById(R.id.tvTitle);
        btnNext = findViewById(R.id.btnNext);
        edtCode = findViewById(R.id.edtCode);
        tvCodeError = findViewById(R.id.tvCodeError);
        btnBack = findViewById(R.id.btnBack);
        btnResend = findViewById(R.id.btnResend);  // 인증번호 다시 받기 버튼

        // include 안의 뷰 접근 (공통 헤더)
        View header = findViewById(R.id.signupHeader);
        progressBar = header.findViewById(R.id.progressSignup);
        TextView headerTitle = header.findViewById(R.id.tv_title);

        if (headerTitle != null) {
            headerTitle.setText(" ");
        }
        if (progressBar != null) {
            progressBar.setProgress(2);
        }

        // 상단바 뒤로가기 버튼 동작
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 이메일을 Intent로 받기
        email = getIntent().getStringExtra("email"); // Intent에서 이메일 받기
        if (email != null) tvEmail.setText(email); // 이메일을 TextView에 표시

        // 이전 상태 복원 (뒤로 갔다가 돌아왔을 때)
        if (savedInstanceState != null) {
            String savedCode = savedInstanceState.getString("code_text");
            edtCode.setText(savedCode);
            btnNext.setEnabled(savedCode != null && !savedCode.isEmpty());
        } else {
            btnNext.setEnabled(false); // 초기엔 비활성화
        }

        // 입력 감지 리스너 - 인증번호 입력 시에만 다음 버튼 활성화
        edtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNext.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 다음 버튼 클릭 시 동작
        btnNext.setOnClickListener(v -> {
            String code = edtCode.getText().toString().trim();
            if (email != null) {
                // 인증번호 검증 API 호출
                verifyCode(email, code);  // 인증 번호 검증
            }
        });

        // 인증번호 다시 받기 버튼 클릭 시 동작
        btnResend.setOnClickListener(v -> {
            if (email != null) {
                // 인증번호 발송 API 호출 (다시 받기)
                sendVerificationCode(email);  // 인증번호 재전송
            }
        });
    }

    // 인증번호 재발송 (API 호출)
    private void sendVerificationCode(String email) {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        EmailRequest request = new EmailRequest(email);  // 이메일만 보내는 EmailRequest 사용
        apiService.checkEmail(request).enqueue(new Callback<ResponseBody>() {  // checkEmail API 사용
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupVerificationActivity.this, "인증번호가 재전송되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    tvCodeError.setVisibility(View.VISIBLE);
                    tvCodeError.setText("인증번호 재전송 실패");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                tvCodeError.setVisibility(View.VISIBLE);
                tvCodeError.setText("서버와의 연결을 실패했습니다.");
            }
        });
    }

    // 인증번호 검증 (API 호출)
    private void verifyCode(String email, String code) {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        VerificationRequest request = new VerificationRequest(email, code);  // 이메일과 코드 모두 필요
        apiService.verifyCode(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    tvCodeError.setVisibility(View.GONE);
                    // 인증 성공 후 다음 단계로 이동
                    Intent intent = new Intent(SignupVerificationActivity.this, SignupPasswordActivity.class);
                    intent.putExtra("email", tvEmail.getText().toString());
                    startActivity(intent);
                } else {
                    // 에러 코드에 따른 처리
                    if (response.code() == 400) {
                        try {
                            String errorMessage = response.errorBody().string();
                            if (errorMessage.contains("INVALID_VERIFICATION_CODE")) {
                                tvCodeError.setText("잘못된 인증 코드입니다.");
                            } else if (errorMessage.contains("VERIFICATION_CODE_EXPIRED")) {
                                tvCodeError.setText("인증 코드가 만료되었습니다.");
                            } else if (errorMessage.contains("INVALID_REQUEST_FIELD")) {
                                tvCodeError.setText("필드가 누락되었거나 형식이 잘못되었습니다.");
                            }
                            tvCodeError.setVisibility(View.VISIBLE);
                        } catch (Exception e) { // 서버 응답을 처리하는 도중 예외 발생 상황
                            tvCodeError.setText("인증에 실패했습니다.");  // 주석을 날리지 않고 메시지 수정
                            tvCodeError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvCodeError.setText("인증에 실패했습니다.");
                        tvCodeError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                tvCodeError.setVisibility(View.VISIBLE);
                tvCodeError.setText("서버와의 연결을 실패했습니다.");
            }
        });
    }

    // Activity가 사라질 때 인증번호 입력값 저장
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("code_text", edtCode.getText().toString());
    }
}
