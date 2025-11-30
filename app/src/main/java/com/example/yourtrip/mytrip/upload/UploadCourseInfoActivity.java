package com.example.yourtrip.mytrip.upload;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yourtrip.R;
import java.io.Serializable;
import java.util.List;

public class UploadCourseInfoActivity extends AppCompatActivity {

    private static final String TAG = "UploadCourseInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course_info);

        // 이전 화면(UploadCourseTagsActivity)에서 전달받은 데이터를 꺼냅니다.
        long courseId = getIntent().getLongExtra("courseId", -1L);
        List<String> selectedTags = (List<String>) getIntent().getSerializableExtra("selectedTags");

        // [중요] 데이터가 잘 넘어왔는지 Logcat과 Toast 메시지로 확인합니다.
        if (courseId != -1L && selectedTags != null) {
            String logMessage = "Course ID: " + courseId + ", Tags: " + selectedTags.toString();
            Log.d(TAG, "전달받은 데이터: " + logMessage);
            Toast.makeText(this, "태그 정보가 잘 넘어왔습니다!", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "데이터 전달 실패!");
            Toast.makeText(this, "데이터를 전달받지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
