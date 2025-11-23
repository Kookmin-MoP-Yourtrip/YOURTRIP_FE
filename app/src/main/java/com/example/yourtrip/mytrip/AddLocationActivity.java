package com.example.yourtrip.mytrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class AddLocationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_location);

        initViews();
        setTopBar();
        
    }
    
    // 뷰 초기화
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        
    }

    //  상단바 설정
    private void setTopBar() {
        tvTitle.setText("장소 추가하기");

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());
    }
}
