package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignUp, btnSkipLogin, btnLogin;
    private EditText edtEmail, edtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 로그인 화면 XML

        //버튼, 입력창 초기화
        btnSignUp = findViewById(R.id.btnSignUp);  // 회원가입 버튼
        btnSkipLogin = findViewById(R.id.tvSkipLogin);  // 로그인 없이 둘러보기 버튼
        btnLogin = findViewById(R.id.btnLogin);  // 로그인 버튼

        edtEmail = findViewById(R.id.edtEmail);  // 이메일 입력창
        edtPassword = findViewById(R.id.edtPassword);  // 비밀번호 입력창

        //처음에는 비활성화
        btnLogin.setEnabled(false);

        // 입력값 변화 감지 리스너
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // 둘 다 비어 있지 않으면 활성화
                btnLogin.setEnabled(!email.isEmpty() && !password.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // 두 입력창에 리스너 연결
        edtEmail.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);

        
        //intent 연결
        // 회원가입 버튼 클릭 시
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUpActivity로 이동
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 없이 둘러보기 버튼 클릭 시
        btnSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
