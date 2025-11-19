package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.example.yourtrip.R;

public class CreateCourseDetailActivity extends AppCompatActivity {
    private static final String TAG = "CreateCourseDetail";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_detail);

        // Intent에서 전달된 데이터 받기
        String courseTitle = getIntent().getStringExtra("courseTitle");
        String location = getIntent().getStringExtra("location");
        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");

        // 로그로 Intent 데이터를 확인
        Log.d(TAG, "Intent data - courseTitle: " + courseTitle);
        Log.d(TAG, "Intent data - location: " + location);
        Log.d(TAG, "Intent data - startDate: " + startDate);
        Log.d(TAG, "Intent data - endDate: " + endDate);
    }

}
