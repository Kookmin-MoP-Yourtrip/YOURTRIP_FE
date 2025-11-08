package com.example.yourtrip.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//R 인식 안돼서 수동으로 넣어줌
import com.example.yourtrip.R;


public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}