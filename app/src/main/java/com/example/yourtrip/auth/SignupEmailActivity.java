//백에서 회원가입 api 단계별로 분리해서 줘야함 
// 지금 상태로 하나로 통합된 회원가입 api로는 단계별 회원가입 불가함
// 추후 백이 수정해주면 api 연동 다시 하고 수정 들어가겠음

package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class SignupEmailActivity extends AppCompatActivity {

    private EditText edtEmail;
    private TextView tvEmailError;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_email);

        // View 초기화
        edtEmail = findViewById(R.id.edtEmail);
        tvEmailError = findViewById(R.id.tvEmailError);
        btnNext = findViewById(R.id.btnNext);

        // 버튼 클릭 시 동작
        btnNext.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            // 이메일 비어있는지 검사
            if (email.isEmpty()) {
                tvEmailError.setText("이메일을 입력해주세요.");
                tvEmailError.setVisibility(View.VISIBLE);
                return;
            }

            // 예시: 이미 존재하는 이메일(샘플 로직) -> api 연동 후 수정 필요
            if (email.equalsIgnoreCase("test@example.com")) {
                tvEmailError.setText("이미 사용 중인 이메일입니다.");
                tvEmailError.setVisibility(View.VISIBLE);
            } else {
                // 오류 문구 숨기고 다음 단계로 이동
                tvEmailError.setVisibility(View.GONE);

                // 다음 Activity로 이동 (회원가입 2단계: 비밀번호 설정)
                Intent intent = new Intent(SignupEmailActivity.this, SignupVerificationActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}
