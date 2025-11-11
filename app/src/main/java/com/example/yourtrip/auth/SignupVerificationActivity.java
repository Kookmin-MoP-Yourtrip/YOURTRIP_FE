// 백에서 이메일 인증 관련 API 분리 필요
// 현재는 임시 코드("1234")로 인증 처리 중
// 추후 백엔드에서 이메일 전송 및 인증 검증 API 제공 시 연동 예정

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

public class SignupVerificationActivity extends AppCompatActivity {

    private TextView tvEmail, tvTitle, btnResend, tvCodeError;
    private EditText edtCode;
    private ImageButton btnSendCode;
    private Button btnNext;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_verification);

        // View 초기화
        tvEmail = findViewById(R.id.tvEmail);
        tvTitle = findViewById(R.id.tvTitle);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnNext = findViewById(R.id.btnNext);
        btnResend = findViewById(R.id.btnResend);
        edtCode = findViewById(R.id.edtCode);
        tvCodeError = findViewById(R.id.tvCodeError);
        btnBack = findViewById(R.id.btnBack);

        // include 안의 ProgressBar 접근 (공통 헤더)
        View header = findViewById(R.id.signupHeader);
        progressBar = header.findViewById(R.id.progressSignup);
        progressBar.setProgress(2); // 두 번째 단계로 표시

        // 상단바 뒤로가기 버튼 동작
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 이메일 데이터 표시
        String email = getIntent().getStringExtra("email");
        if (email != null) tvEmail.setText(email);

        //  이전 상태 복원 (뒤로 갔다가 돌아왔을 때)
        if (savedInstanceState != null) {
            String savedCode = savedInstanceState.getString("code_text");
            edtCode.setText(savedCode);
            btnNext.setEnabled(savedCode != null && !savedCode.isEmpty());
        } else {
            btnNext.setEnabled(false); // 초기엔 비활성화
        }

        // 인증번호 전송 버튼
        btnSendCode.setOnClickListener(v ->
                Toast.makeText(this, "인증번호를 전송했습니다. (임시 메시지)", Toast.LENGTH_SHORT).show()
        );

        // 인증번호 다시 받기
        btnResend.setOnClickListener(v ->
                Toast.makeText(this, "인증번호를 다시 전송했습니다. (임시 메시지)", Toast.LENGTH_SHORT).show()
        );

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

            // ⚠ 임시 코드 (나중에 서버 인증 로직으로 교체 예정)
            if (code.equals("1234")) { // 임시 인증번호
                tvCodeError.setVisibility(View.GONE);
                Toast.makeText(this, "인증 성공! (임시 데이터)", Toast.LENGTH_SHORT).show();

                // 다음 Activity로 이동 (회원가입 비밀번호 설정 페이지)
                Intent intent = new Intent(SignupVerificationActivity.this, SignupPasswordActivity.class);
                intent.putExtra("email", tvEmail.getText().toString());
                startActivity(intent);

            } else {
                // 인증 실패 시
                tvCodeError.setVisibility(View.VISIBLE);
            }
        });
    }

    //  Activity가 사라질 때 인증번호 입력값 저장
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("code_text", edtCode.getText().toString());
    }
}


//onSaveInstanceState()로 인증번호 유지