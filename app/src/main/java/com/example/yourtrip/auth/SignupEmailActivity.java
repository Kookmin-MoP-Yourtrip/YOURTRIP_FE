package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.model.EmailRequest;
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

        // View 초기화
        edtEmail = findViewById(R.id.edtEmail);
        tvEmailError = findViewById(R.id.tvEmailError);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        // 상단바 뒤로가기 버튼 동작
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // onCreate 시점에서 저장된 상태 복원
        if (savedInstanceState != null) {
            String savedEmail = savedInstanceState.getString("email_text");
            edtEmail.setText(savedEmail);
            btnNext.setEnabled(savedEmail != null && !savedEmail.isEmpty());
        } else {
            btnNext.setEnabled(false); // 초기엔 비활성화
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
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
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
                                    tvEmailError.setText("이메일 형식이 올바르지 않거나 비어있습니다."); //백에서 에러 메시지까지 담아서 보내준지는 모르겠음
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





//api 연동 전 임시 확인 코드

//package com.example.yourtrip.auth;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yourtrip.R;
//
//public class SignupEmailActivity extends AppCompatActivity {
//
//    private EditText edtEmail;
//    private TextView tvEmailError;
//    private Button btnNext;
//    private ImageButton btnBack;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_signup_email);
//
//        // View 초기화
//        edtEmail = findViewById(R.id.edtEmail);
//        tvEmailError = findViewById(R.id.tvEmailError);
//        btnNext = findViewById(R.id.btnNext);
//        btnBack = findViewById(R.id.btnBack);
//
//        //  상단바 뒤로가기 버튼 동작
//        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
//
//        //  onCreate 시점에서 저장된 상태 복원
//        if (savedInstanceState != null) {
//            String savedEmail = savedInstanceState.getString("email_text");
//            edtEmail.setText(savedEmail);
//            btnNext.setEnabled(savedEmail != null && !savedEmail.isEmpty());
//        } else {
//            btnNext.setEnabled(false); // 초기엔 비활성화
//        }
//
//        //  이메일 입력 시 버튼 활성화/비활성화 처리
//        edtEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                btnNext.setEnabled(!s.toString().trim().isEmpty());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
//        // 버튼 클릭 시 동작
//        btnNext.setOnClickListener(v -> {
//            String email = edtEmail.getText().toString().trim();
//
//            // 이메일 비어있는지 검사
//            if (email.isEmpty()) {
//                tvEmailError.setText("이메일을 입력해주세요.");
//                tvEmailError.setVisibility(TextView.VISIBLE);
//                return;
//            }
//
//            // 예시: 이미 존재하는 이메일(샘플 로직) -> api 연동 후 수정 필요
//            if (email.equalsIgnoreCase("test@example.com")) {
//                tvEmailError.setText("이미 사용 중인 이메일입니다.");
//                tvEmailError.setVisibility(TextView.VISIBLE);
//            } else {
//                // 오류 문구 숨기고 다음 단계로 이동
//                tvEmailError.setVisibility(TextView.GONE);
//
//                // 다음 Activity로 이동 (회원가입 2단계: 이메일 인증)
//                Intent intent = new Intent(SignupEmailActivity.this, SignupVerificationActivity.class);
//                intent.putExtra("email", email);
//                startActivity(intent);
//            }
//        });
//    }
//
//    //  Activity가 사라질 때 입력값 저장 (뒤로 가기 후 복원용)
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("email_text", edtEmail.getText().toString());
//    }
//}

