package com.example.yourtrip.mytrip.upload;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.MainActivity;
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
            Intent intent = new Intent(this, MainActivity.class);
            // 이전에 쌓여있던 모든 액티비티를 스택에서 제거하고, MainActivity를 새로 시작
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

