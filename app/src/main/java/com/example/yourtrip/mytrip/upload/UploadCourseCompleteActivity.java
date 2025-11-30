package com.example.yourtrip.mytrip.upload;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
public class UploadCourseCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course_complete);



        long uploadCourseId = getIntent().getLongExtra("uploadCourseId", -1);



        // 업로드한 코스 보러가기 버튼
        findViewById(R.id.btnUploadedCourse).setOnClickListener(v -> {
//            Intent intent = new Intent(this, UploadedCourseDetailActivity.class);
//            intent.putExtra("uploadCourseId", uploadCourseId);
//            startActivity(intent);
        });

        // 홈으로 돌아가기 버튼
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            finishAffinity();  // 앱 홈 화면으로 이동
        });
    }
}

