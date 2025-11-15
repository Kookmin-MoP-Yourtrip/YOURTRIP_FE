package com.example.yourtrip.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.model.ProfileRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupProfileActivity extends AppCompatActivity {

    private ImageView imgProfileField;
    private EditText edtNickname;
    private Button btnComplete;
    private TextView tvNicknameError;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri selectedImageUri = null; // 선택한 프로필 이미지 저장용

    private String email; // 이전 단계에서 전달받은 이메일

    //  최신 권장 방식: 이미지 선택 런처
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_profile);

        // View 초기화
        imgProfileField = findViewById(R.id.imgProfileField);
        edtNickname = findViewById(R.id.edtNickname);
        btnComplete = findViewById(R.id.btnComplete);
        tvNicknameError = findViewById(R.id.tvNicknameError);
        btnBack = findViewById(R.id.btnBack);

        // 상단 진행바 4단계 표시
        View header = findViewById(R.id.signupHeader);
        progressBar = header.findViewById(R.id.progressSignup);
        progressBar.setProgress(4);

        // 상단바 뒤로가기 버튼 동작
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 이메일 전달받기
        email = getIntent().getStringExtra("email");

        // 초기 상태 버튼 비활성화
        btnComplete.setEnabled(false);
        btnComplete.setAlpha(0.5f);

        // 닉네임 입력 감지
        edtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nickname = s.toString().trim();
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

        //  이미지 선택 결과 처리 (registerForActivityResult)
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgProfileField.setImageURI(selectedImageUri);
                    }
                }
        );

        // 프로필 이미지 클릭 → 갤러리에서 선택
        imgProfileField.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
//            startActivityForResult(intent, REQUEST_IMAGE_PICK);
            imagePickerLauncher.launch(intent);
        });

        // 갤러리에서 이미지 선택 결과 처리
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
//            selectedImageUri = data.getData();
//            imgProfileField.setImageURI(selectedImageUri);
//        }
//    }




        // 완료 버튼 클릭 시
        btnComplete.setOnClickListener(v -> {
            if (!btnComplete.isEnabled()) return;

            String nickname = edtNickname.getText().toString().trim();
            String profileUrl = (selectedImageUri != null) ? selectedImageUri.toString() : null;

            //  디버깅 로그 추가
            Log.d("SignupProfileActivity", "email=" + email + ", nickname=" + nickname);

            submitProfile(email, nickname, profileUrl);
        });


    }

    // 프로필 등록 API 호출
    private void submitProfile(String email, String nickname, String profileUrl) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        ProfileRequest request = new ProfileRequest(email, nickname, profileUrl);

        apiService.setProfile(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 가입 완료 → 축하 화면 이동
                    Intent intent = new Intent(SignupProfileActivity.this, SignupCompleteActivity.class);
                    startActivity(intent);
                    finish();
                } else { // 201 아닌 상태코드를 반환하는 경우
                    try {
                        // 서버에서 내려준 오류 메시지 파싱
                        String errorBody = response.errorBody().string();
                        JSONObject json = new JSONObject(errorBody);

                        // 서버 응답에서 code, message 추출
                        String code = json.optString("code", "");
                        String message;

                        // 에러 코드별 처리
                        switch (code) {
                            case "EMAIL_NOT_VERIFIED":
                                message = "이메일 인증이 완료되지 않았습니다. 다시 인증을 진행해주세요.";
                                break;
                            case "INVALID_REQUEST_FIELD":
                                message = "닉네임 형식이 올바르지 않거나 입력이 누락되었습니다.";
                                break;
                            case "USER_NOT_FOUND":
                                message = "가입 정보를 찾을 수 없습니다. 처음부터 다시 시도해주세요.";
                                break;
                            case "EMAIL_ALREADY_EXIST":
                                message = "이미 가입이 완료된 이메일입니다. 로그인 화면으로 이동해주세요.";
                                break;
                            default:
                                message = json.optString("message", "회원가입 중 오류가 발생했습니다.");
                                break;
                        }

                        tvNicknameError.setText(message);
                        tvNicknameError.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        // JSON 파싱 실패 또는 서버 응답 이상
                        tvNicknameError.setText("회원가입 요청 처리 중 오류가 발생했습니다.");
                        tvNicknameError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 서버랑 연동 아예 실패
                tvNicknameError.setText("서버와의 연결을 실패했습니다. 네트워크 상태를 확인해주세요.");
                tvNicknameError.setVisibility(View.VISIBLE);
            }
        });
    }
}




//임시 화면 확인용 코드

//package com.example.yourtrip.auth;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yourtrip.R;
//
//public class SignupProfileActivity extends AppCompatActivity {
//
//    private ImageView imgProfileField;
//    private EditText edtNickname;
//    private Button btnComplete;
//    private TextView tvNicknameError;
//    private ProgressBar progressBar;
//    private ImageButton btnBack;
//
//    private static final int REQUEST_IMAGE_PICK = 1;
//    private Uri selectedImageUri = null; // 선택한 프로필 이미지 저장용
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_signup_profile);
//
//        //  View 초기화
//        imgProfileField = findViewById(R.id.imgProfileField);
//        edtNickname = findViewById(R.id.edtNickname);
//        btnComplete = findViewById(R.id.btnComplete);
//        tvNicknameError = findViewById(R.id.tvNicknameError);
//        btnBack = findViewById(R.id.btnBack);
//
//        //  상단 진행바 4단계 표시
//        View header = findViewById(R.id.signupHeader);
//        progressBar = header.findViewById(R.id.progressSignup);
//        progressBar.setProgress(4);
//
//        //  상단바 뒤로가기 버튼 동작
//        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
//
//        //  초기 상태 버튼 비활성화
//        btnComplete.setEnabled(false);
//        btnComplete.setAlpha(0.5f); // 시각적 구분 (비활성 시 투명도 ↓)
//
//        //  닉네임 입력 감지
//        edtNickname.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String nickname = s.toString().trim();
//
//                // 입력이 있으면 버튼 활성화
//                if (!nickname.isEmpty()) {
//                    btnComplete.setEnabled(true);
//                    btnComplete.setAlpha(1.0f);
//                    tvNicknameError.setVisibility(View.GONE);
//                } else {
//                    btnComplete.setEnabled(false);
//                    btnComplete.setAlpha(0.5f);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
//        //  완료 버튼 클릭 시
//        btnComplete.setOnClickListener(v -> {
//            // 버튼이 활성화되지 않은 상태면 아무 동작도 하지 않음
//            if (!btnComplete.isEnabled()) return;
//
//            String nickname = edtNickname.getText().toString().trim();
//
//            // (임시 로직) 닉네임 중복 체크
//            if (nickname.equalsIgnoreCase("test")) {
//                tvNicknameError.setVisibility(View.VISIBLE);
//                tvNicknameError.setText("이미 사용 중인 닉네임입니다.");
//                return;
//            }
//
//            tvNicknameError.setVisibility(View.GONE);
//
//            //  [추후 API 연동 예정]
//            // - 선택한 이미지(selectedImageUri)를 Multipart로 백엔드에 업로드
//            // - 닉네임 중복 검증 API 요청
//            // - 응답 성공 시 다음 화면으로 이동
//
//            //  축하 화면으로 이동
//            Intent intent = new Intent(SignupProfileActivity.this, SignupCompleteActivity.class);
//            startActivity(intent);
//            finish();
//        });
//
//        //  프로필 이미지 클릭 → 갤러리에서 선택
//        imgProfileField.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_PICK);
//            intent.setType("image/*");
//            startActivityForResult(intent, REQUEST_IMAGE_PICK);
//        });
//    }
//
//    //  갤러리에서 이미지 선택 결과 처리
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
//            selectedImageUri = data.getData();
//            imgProfileField.setImageURI(selectedImageUri);
//        }
//    }
//}
