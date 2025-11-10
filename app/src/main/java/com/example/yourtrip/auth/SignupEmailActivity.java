// 백에서 회원가입 API 단계별로 분리해서 줘야 함
// 지금 상태로 하나로 통합된 회원가입 API로는 단계별 회원가입 불가함
// 추후 백이 수정해주면 API 연동 다시 하고 수정 들어가겠음

package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

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

        // View 초기화
        edtEmail = findViewById(R.id.edtEmail);
        tvEmailError = findViewById(R.id.tvEmailError);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        //  상단바 뒤로가기 버튼 동작
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        //  onCreate 시점에서 저장된 상태 복원
        if (savedInstanceState != null) {
            String savedEmail = savedInstanceState.getString("email_text");
            edtEmail.setText(savedEmail);
            btnNext.setEnabled(savedEmail != null && !savedEmail.isEmpty());
        } else {
            btnNext.setEnabled(false); // 초기엔 비활성화
        }

        //  이메일 입력 시 버튼 활성화/비활성화 처리
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

            // 예시: 이미 존재하는 이메일(샘플 로직) -> api 연동 후 수정 필요
            if (email.equalsIgnoreCase("test@example.com")) {
                tvEmailError.setText("이미 사용 중인 이메일입니다.");
                tvEmailError.setVisibility(TextView.VISIBLE);
            } else {
                // 오류 문구 숨기고 다음 단계로 이동
                tvEmailError.setVisibility(TextView.GONE);

                // 다음 Activity로 이동 (회원가입 2단계: 이메일 인증)
                Intent intent = new Intent(SignupEmailActivity.this, SignupVerificationActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    //  Activity가 사라질 때 입력값 저장 (뒤로 가기 후 복원용)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email_text", edtEmail.getText().toString());
    }
}


//onSaveInstanceState(), savedInstanceState 추가 -> 입력값 유지
// 뒤로 갔다가 다시 와도 입력값 & 버튼 상태 그대로 복원