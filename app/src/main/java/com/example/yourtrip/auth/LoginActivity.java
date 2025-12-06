package com.example.yourtrip.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.auth.model.LoginRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignUp, btnSkipLogin, btnLogin;
    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // View 초기화
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSkipLogin = findViewById(R.id.tvSkipLogin);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        // 처음에는 로그인 버튼 비활성화
        btnLogin.setEnabled(false);

        //  입력값 감지 → 버튼 활성화 로직
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // 이메일과 비밀번호 둘 다 입력되었을 때만 로그인 버튼 활성화
                btnLogin.setEnabled(!email.isEmpty() && !password.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtEmail.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);

        // 회원가입 버튼 → SignupEmailActivity로 이동
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupEmailActivity.class);
            startActivity(intent);
        });

        // 로그인 없이 둘러보기 → MainActivity로 이동
        btnSkipLogin.setOnClickListener(v -> {

            clearLoginState();   // 🔥 비로그인 모드 → 토큰 삭제

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // 로그인 화면 종료
        });

        // 로그인 버튼 클릭 시 → API 호출 실행
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            doLogin(email, password);
        });
    }
    //이전 로그인 토크 없앰 -> 비로그인 버튼용
    private void clearLoginState() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("accessToken");  // 토큰 삭제
        editor.remove("userId");
        editor.remove("nickname");
        editor.remove("profileImageUrl");
        editor.apply();
    }

    // 로그인 API 요청 함수
    private void doLogin(String email, String password) {
        // Retrofit 인터페이스 생성
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        // 요청 객체 - JSON Body에 들어갈 데이터
        LoginRequest request = new LoginRequest(email, password);

        // Retrofit 비동기 호출
        apiService.login(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //  로그인 성공 - 200
                if (response.isSuccessful()) {
                    try {
                        // 서버 응답 본문(JSON) 읽기
                        String body = response.body().string();

                        //  응답 내용 Logcat에 출력
                        Log.d("LoginResponse", "서버 응답: " + body);

                        JSONObject json = new JSONObject(body);
                        // accessToken 추출
                        String token = json.optString("accessToken", "");
                        // userId
                        int userId = json.optInt("userId", -1);

                        // ⭐ 닉네임 + 프로필 URL 가져오기
                        String nickname = json.optString("nickname", "사용자이름");
                        String profileImageUrl = json.optString("profileImageUrl", "");


                        // SharedPreferences에 JWT 토큰 저장
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString("accessToken", token)
                                .putInt("userId", userId)
                                .putString("nickname", nickname)
                                .putString("profileImageUrl", profileImageUrl)
                                .apply();

                        // 저장된 토큰 Logcat 확인
                        String savedToken = prefs.getString("accessToken", "NULL");
                        Log.d("LOGIN", "Saved Token: " + savedToken);
                        Log.d("LOGIN", "Saved userId: " + userId);

                        Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                        // 메인 페이지로 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.putExtra("accessToken", token); //SharedPerferences에 토큰 저장 시 intent로 보낼 필요 없음
                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "응답 파싱 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }

                }
                // 로그인 실패 (400 등)
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject json = new JSONObject(errorBody);
                        String code = json.optString("code", "");
                        String message;

                        // 서버에서 정의한 에러 코드별 처리
                        switch (code) {
                            case "EMAIL_NOT_FOUND":
                                message = "존재하지 않는 이메일입니다.";
                                break;
                            case "NOT_MATCH_PASSWORD":
                                message = "비밀번호가 일치하지 않습니다.";
                                break;
                            default:
                                message = json.optString("message", "로그인 실패: 서버 오류");
                                break;
                        }

                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "오류 응답 처리 중 예외 발생", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            //서버 연결 자체가 실패했을 때
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "서버 연결 실패: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

