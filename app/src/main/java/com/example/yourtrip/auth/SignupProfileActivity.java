package com.example.yourtrip.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class SignupProfileActivity extends AppCompatActivity {

    private ImageView imgProfileField;
    private EditText edtNickname;
    private Button btnComplete;
    private TextView tvNicknameError;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri selectedImageUri = null; // 선택한 프로필 이미지 저장용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_profile);

        //  View 초기화
        imgProfileField = findViewById(R.id.imgProfileField);
        edtNickname = findViewById(R.id.edtNickname);
        btnComplete = findViewById(R.id.btnComplete);
        tvNicknameError = findViewById(R.id.tvNicknameError);
        btnBack = findViewById(R.id.btnBack);

        //  상단 진행바 4단계 표시
        View header = findViewById(R.id.signupHeader);
        progressBar = header.findViewById(R.id.progressSignup);
        progressBar.setProgress(4);

        //  상단바 뒤로가기 버튼 동작
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        //  초기 상태 버튼 비활성화
        btnComplete.setEnabled(false);
        btnComplete.setAlpha(0.5f); // 시각적 구분 (비활성 시 투명도 ↓)

        //  닉네임 입력 감지
        edtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nickname = s.toString().trim();

                // 입력이 있으면 버튼 활성화
                if (!nickname.isEmpty()) {
                    btnComplete.setEnabled(true);
                    btnComplete.setAlpha(1.0f);
                    tvNicknameError.setVisibility(View.GONE);
                } else {
                    btnComplete.setEnabled(false);
                    btnComplete.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //  완료 버튼 클릭 시
        btnComplete.setOnClickListener(v -> {
            // 버튼이 활성화되지 않은 상태면 아무 동작도 하지 않음
            if (!btnComplete.isEnabled()) return;

            String nickname = edtNickname.getText().toString().trim();

            // (임시 로직) 닉네임 중복 체크
            if (nickname.equalsIgnoreCase("test")) {
                tvNicknameError.setVisibility(View.VISIBLE);
                tvNicknameError.setText("이미 사용 중인 닉네임입니다.");
                return;
            }

            tvNicknameError.setVisibility(View.GONE);

            //  [추후 API 연동 예정]
            // - 선택한 이미지(selectedImageUri)를 Multipart로 백엔드에 업로드
            // - 닉네임 중복 검증 API 요청
            // - 응답 성공 시 다음 화면으로 이동

            //  축하 화면으로 이동
            Intent intent = new Intent(SignupProfileActivity.this, SignupCompleteActivity.class);
            startActivity(intent);
            finish();
        });

        //  프로필 이미지 클릭 → 갤러리에서 선택
        imgProfileField.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });
    }

    //  갤러리에서 이미지 선택 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfileField.setImageURI(selectedImageUri);
        }
    }
}
