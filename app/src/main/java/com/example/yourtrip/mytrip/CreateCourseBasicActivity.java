package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class CreateCourseBasicActivity extends AppCompatActivity {

    private ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_basic);


        // include 된 상단바에서 제목 텍스트 설정
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("코스 만들기");
    }
}
