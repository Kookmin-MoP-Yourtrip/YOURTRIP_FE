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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.model.PasswordRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupPasswordActivity extends AppCompatActivity {

    private EditText edtPassword, edtPasswordConfirm;
    private Button btnNext;
    private TextView tvPasswordError;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_password);

        // View 초기화
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
        btnNext = findViewById(R.id.btnNext);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        btnBack = findViewById(R.id.btnBack);

        // include된 상단바 안의 ProgressBar 접근
        View header = findViewById(R.id.signupHeader);
        progressBar = header.findViewById(R.id.progressSignup);
        progressBar.setProgress(3); // 3단계 진행 표시

        // 상단바 뒤로가기 버튼 동작
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 이전 상태 복원 (뒤로 갔다 돌아왔을 때)
        if (savedInstanceState != null) {
            String savedPw = savedInstanceState.getString("password_text");
            String savedPwConfirm = savedInstanceState.getString("password_confirm_text");

            edtPassword.setText(savedPw);
            edtPasswordConfirm.setText(savedPwConfirm);

            boolean filled = (savedPw != null && !savedPw.isEmpty()) &&
                    (savedPwConfirm != null && !savedPwConfirm.isEmpty());
            btnNext.setEnabled(filled);
        } else {
            btnNext.setEnabled(false); // 초기엔 비활성화
        }

        // 비밀번호 입력 감지 리스너
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pw = edtPassword.getText().toString().trim();
                String pwConfirm = edtPasswordConfirm.getText().toString().trim();

                // 둘 다 입력된 경우에만 버튼 활성화
                boolean filled = !pw.isEmpty() && !pwConfirm.isEmpty();
                btnNext.setEnabled(filled);

                // 비밀번호 불일치 시 오류 표시
                if (filled && !pw.equals(pwConfirm)) {
                    tvPasswordError.setVisibility(View.VISIBLE);
                    tvPasswordError.setText("비밀번호가 일치하지 않습니다.");
                } else {
                    tvPasswordError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 비밀번호 확인 감지 리스너
        edtPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pw = edtPassword.getText().toString().trim();
                String pwConfirm = edtPasswordConfirm.getText().toString().trim();

                // 둘 다 입력된 경우에만 버튼 활성화
                boolean filled = !pw.isEmpty() && !pwConfirm.isEmpty();
                btnNext.setEnabled(filled);

                // 비밀번호 불일치 시 오류 표시
                if (filled && !pw.equals(pwConfirm)) {
                    tvPasswordError.setVisibility(View.VISIBLE);
                    tvPasswordError.setText("비밀번호가 일치하지 않습니다.");
                } else {
                    tvPasswordError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 다음 버튼 클릭 시
        btnNext.setOnClickListener(v -> {
            String pw = edtPassword.getText().toString().trim();
            String pwConfirm = edtPasswordConfirm.getText().toString().trim();

            // 비밀번호 일치 확인
            if (pw.equals(pwConfirm)) {
                tvPasswordError.setVisibility(View.GONE);

                // 비밀번호를 서버에 보내기 위한 요청
                submitPassword(pw);
            } else {
                tvPasswordError.setVisibility(View.VISIBLE);
                tvPasswordError.setText("비밀번호가 일치하지 않습니다.");
            }
        });
    }

    // 비밀번호를 서버에 제출 (API 호출)
    private void submitPassword(String password) {
        // 이메일은 Intent로 전달받은 이메일을 사용
        String email = getIntent().getStringExtra("email");

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        PasswordRequest request = new PasswordRequest(email, password);  // 이메일과 비밀번호
        apiService.setPassword(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 비밀번호 설정 성공 시 다음 페이지로 이동
                    Intent intent = new Intent(SignupPasswordActivity.this, SignupProfileActivity.class);
                    startActivity(intent);
                } else {
                    tvPasswordError.setVisibility(View.VISIBLE);
                    tvPasswordError.setText("비밀번호 설정 실패");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                tvPasswordError.setVisibility(View.VISIBLE);
                tvPasswordError.setText("서버와의 연결을 실패했습니다.");
            }
        });
    }

    // Activity가 사라질 때 입력값 저장 (뒤로 가기 후 복원용)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("password_text", edtPassword.getText().toString());
        outState.putString("password_confirm_text", edtPasswordConfirm.getText().toString());
    }
}



// 기존 임시 화면 확인용 코드

//package com.example.yourtrip.auth;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yourtrip.R;
//
//public class SignupPasswordActivity extends AppCompatActivity {
//
//    private EditText edtPassword, edtPasswordConfirm;
//    private Button btnNext;
//    private TextView tvPasswordError;
//    private ProgressBar progressBar;
//    private ImageButton btnBack;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_signup_password);
//
//        //  View 초기화
//        edtPassword = findViewById(R.id.edtPassword);
//        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
//        btnNext = findViewById(R.id.btnNext);
//        tvPasswordError = findViewById(R.id.tvPasswordError);
//        btnBack = findViewById(R.id.btnBack);
//
//        //  include된 상단바 안의 ProgressBar 접근
//        View header = findViewById(R.id.signupHeader);
//        progressBar = header.findViewById(R.id.progressSignup);
//        progressBar.setProgress(3); // 3단계 진행 표시
//
//        //  상단바 뒤로가기 버튼 동작
//        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
//
//        //  이전 상태 복원 (뒤로 갔다 돌아왔을 때)
//        if (savedInstanceState != null) {
//            String savedPw = savedInstanceState.getString("password_text");
//            String savedPwConfirm = savedInstanceState.getString("password_confirm_text");
//
//            edtPassword.setText(savedPw);
//            edtPasswordConfirm.setText(savedPwConfirm);
//
//            boolean filled = (savedPw != null && !savedPw.isEmpty()) &&
//                    (savedPwConfirm != null && !savedPwConfirm.isEmpty());
//            btnNext.setEnabled(filled);
//        } else {
//            btnNext.setEnabled(false); // 초기엔 비활성화
//        }
//
//        //  비밀번호 입력 감지 리스너
//        TextWatcher watcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String pw = edtPassword.getText().toString().trim();
//                String pwConfirm = edtPasswordConfirm.getText().toString().trim();
//
//                //  둘 다 입력된 경우에만 버튼 활성화
//                boolean filled = !pw.isEmpty() && !pwConfirm.isEmpty();
//                btnNext.setEnabled(filled);
//
//                //  (임시 로직) 비밀번호 불일치 시 오류 표시
//                if (filled && !pw.equals(pwConfirm)) {
//                    tvPasswordError.setVisibility(View.VISIBLE);
//                    tvPasswordError.setText("비밀번호가 일치하지 않습니다.");
//                } else {
//                    tvPasswordError.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        };
//
//        //  두 EditText 모두 리스너 등록
//        edtPassword.addTextChangedListener(watcher);
//        edtPasswordConfirm.addTextChangedListener(watcher);
//
//        //  다음 버튼 클릭 시
//        btnNext.setOnClickListener(v -> {
//            String pw = edtPassword.getText().toString().trim();
//            String pwConfirm = edtPasswordConfirm.getText().toString().trim();
//
//            // (임시 검증) 비밀번호 일치만 확인
//            if (pw.equals(pwConfirm)) {
//                tvPasswordError.setVisibility(View.GONE);
//
//                //  [추후 추가 예정]
//                // - 서버 API로 비밀번호 유효성 검사 및 회원가입 데이터 전달
//
//                Intent intent = new Intent(SignupPasswordActivity.this, SignupProfileActivity.class);
//                startActivity(intent);
//            } else {
//                tvPasswordError.setVisibility(View.VISIBLE);
//                tvPasswordError.setText("비밀번호가 일치하지 않습니다.");
//            }
//        });
//    }
//
//    //  Activity가 사라질 때 입력값 저장 (뒤로 가기 후 복원용)
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("password_text", edtPassword.getText().toString());
//        outState.putString("password_confirm_text", edtPasswordConfirm.getText().toString());
//    }
//}
