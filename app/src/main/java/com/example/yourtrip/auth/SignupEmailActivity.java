package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.auth.model.EmailRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupEmailActivity extends AppCompatActivity {

    private EditText edtEmail;
    private TextView tvEmailError;
    private Button btnNext;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_email);

        // 공통 헤더의 뷰에 접근
        View headerView = findViewById(R.id.signupHeader); // 다른 Signup 화면과 동일한 ID로 가정

        // View 초기화
        edtEmail = findViewById(R.id.edtEmail);
        tvEmailError = findViewById(R.id.tvEmailError);
        btnNext = findViewById(R.id.btnNext);
        
        // 헤더 안에서 버튼을 다시 찾아야 함
        if (headerView != null) {
            btnBack = headerView.findViewById(R.id.btnBack); 
        }

        // 헤더 UI 설정: 제목 제거, 프로그레스바 설정
        if (headerView != null) {
            TextView headerTitle = headerView.findViewById(R.id.tv_title);
            ProgressBar progressBar = headerView.findViewById(R.id.progressSignup);
            if (headerTitle != null) {
                headerTitle.setText("");
            }
            if (progressBar != null) {
                progressBar.setProgress(1);
            }
        }

        // btnBack이 null이 아닐 때만 리스너를 설정
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        } else {
            Log.e("SignupEmailActivity", "뒤로가기 버튼(btnBack)을 헤더에서 찾을 수 없습니다.");
        }

        // onCreate 시점에서 저장된 상태 복원
        if (savedInstanceState != null) {
            String savedEmail = savedInstanceState.getString("email_text");
            edtEmail.setText(savedEmail);
            btnNext.setEnabled(savedEmail != null && !savedEmail.isEmpty());
        } else {
            btnNext.setEnabled(false);
        }

        // 이메일 입력 시 버튼 활성화/비활성화 처리
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNext.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 버튼 클릭 시 동작
        btnNext.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            // 이메일 비어있는지 검사
            if (email.isEmpty()) {
                tvEmailError.setText("이메일을 입력해주세요.");
                tvEmailError.setVisibility(TextView.VISIBLE);
                return;
            }

            // 이메일 중복 체크 API 호출
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            apiService.checkEmail(new EmailRequest(email)).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // 이메일 중복이 아니고 올바른 형식이면 다음 단계로 이동
                        tvEmailError.setVisibility(TextView.GONE);

                        // 다음 Activity로 이동 (회원가입 2단계: 이메일 인증)
                        Intent intent = new Intent(SignupEmailActivity.this, SignupVerificationActivity.class);
                        intent.putExtra("email", email); //이메일 전달
                        startActivity(intent);
                    } else {
                        // 이메일 중복 또는 형식 오류
                        if (response.code() == 400 && response.errorBody() != null) {
                            try {
                                String errorMessage = response.errorBody().string();
                                if (errorMessage.contains("EMAIL_ALREADY_EXIST")) {
                                    tvEmailError.setText("이미 사용 중인 이메일입니다.");
                                    tvEmailError.setVisibility(TextView.VISIBLE);
                                } else if (errorMessage.contains("INVALID_REQUEST_FIELD")) {
                                    tvEmailError.setText("이메일 형식이 올바르지 않거나 비어있습니다.");
                                    tvEmailError.setVisibility(TextView.VISIBLE);
                                }
                            } catch (Exception e) { //서버 응답을 처리하는 도중 예외 발생 상황
                                tvEmailError.setText("이메일 전송에 실패했습니다.");
                                tvEmailError.setVisibility(TextView.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // 네트워크 연결 아예 실패 시 에러 처리
                    Log.e("API Error", t.getMessage(), t);  // 오류 메시지와 스택 트레이스를 로그로 출력
                    tvEmailError.setText("서버와의 연결을 실패했습니다.");
                    tvEmailError.setVisibility(TextView.VISIBLE);
                }
            });
        });
    }

    // Activity가 사라질 때 입력값 저장 (뒤로 가기 후 복원용)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email_text", edtEmail.getText().toString());
    }
}
