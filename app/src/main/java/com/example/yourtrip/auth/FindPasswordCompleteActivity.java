package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class FindPasswordCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_complete);

        Button btnGoLogin = findViewById(R.id.btnGoLogin);

        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(FindPasswordCompleteActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            finish();
        });
    }
}
