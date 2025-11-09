package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class SignupPasswordActivity extends AppCompatActivity {

    private EditText edtPassword, edtPasswordConfirm;
    private Button btnNext;
    private TextView tvPasswordError;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_password);

        //  View 초기화
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
        btnNext = findViewById(R.id.btnNext);
        tvPasswordError = findViewById(R.id.tvPasswordError);

        //  include된 상단바 안의 ProgressBar 접근
        View header = findViewById(R.id.signupHeader);
        progressBar = header.findViewById(R.id.progressSignup);
        progressBar.setProgress(3); // 3단계 진행 표시

        //  비밀번호 입력 감지 리스너
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pw = edtPassword.getText().toString().trim();
                String pwConfirm = edtPasswordConfirm.getText().toString().trim();

                //  둘 다 입력된 경우에만 버튼 활성화
                boolean filled = !pw.isEmpty() && !pwConfirm.isEmpty();
                btnNext.setEnabled(filled);

                //  (임시 로직) 비밀번호 불일치 시 오류 표시
                if (filled && !pw.equals(pwConfirm)) {
                    tvPasswordError.setVisibility(View.VISIBLE);
                    tvPasswordError.setText("비밀번호가 일치하지 않습니다.");
                } else {
                    tvPasswordError.setVisibility(View.GONE);
                }

                //  [추가 예정]
                // 백엔드 제약조건(API 연동 후 적용):
                // - 비밀번호 최소/최대 길이 (예: 8~20자)
                // - 영문 + 숫자 조합 필수 여부
                // - 특수문자 포함 여부
                // 위 조건 미충족 시 다음과 같은 형태로 오류 표시 예정:
                // tvPasswordError.setVisibility(View.VISIBLE);
                // tvPasswordError.setText("비밀번호는 영문과 숫자를 포함해야 합니다.");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        //  두 EditText 모두 리스너 등록
        edtPassword.addTextChangedListener(watcher);
        edtPasswordConfirm.addTextChangedListener(watcher);

        //  다음 버튼 클릭 시
        btnNext.setOnClickListener(v -> {
            String pw = edtPassword.getText().toString().trim();
            String pwConfirm = edtPasswordConfirm.getText().toString().trim();

            // (임시 검증) 비밀번호 일치만 확인
            if (pw.equals(pwConfirm)) {
                tvPasswordError.setVisibility(View.GONE);

                //  [추가 예정]
                // - 서버 API로 비밀번호 유효성 검사 및 회원가입 데이터 전달

                Intent intent = new Intent(SignupPasswordActivity.this, SignupProfileActivity.class);
                startActivity(intent);
            } else {
                tvPasswordError.setVisibility(View.VISIBLE);
                tvPasswordError.setText("비밀번호가 일치하지 않습니다.");
            }
        });
    }
}
