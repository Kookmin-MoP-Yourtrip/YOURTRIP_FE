//package com.example.yourtrip.auth;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yourtrip.MainActivity;
//import com.example.yourtrip.R;
//
//
//public class LoginActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//    }
//}

package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignUp, btnSkipLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 로그인 화면 XML

        btnSignUp = findViewById(R.id.btnSignUp);  // 회원가입 버튼
        btnSkipLogin = findViewById(R.id.tvSkipLogin);  // 로그인 없이 둘러보기 버튼

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
