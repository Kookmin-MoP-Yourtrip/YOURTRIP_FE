package com.example.yourtrip.mytrip.create_ai;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class CreateAICourseLocationActivity extends AppCompatActivity {

    private EditText etLocation;
    private TextView tvTitle;
    private ImageView btnBack; //include된 상단바의 뒤로가기 버튼
    private Button btnNext;

    private static final String TAG = "CreateAICourseLocation";

    private String startDate, endDate; //이전 화면에서 받은 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ai_course_location); //레이아웃을 현재 activity 화면으로 사용

        initViews(); //findViewById로 xml의 뷰들을 멤버 변수에 연결
        receiveFromDateActivity();
        setTopBar(); //상단바 타이틀과 뒤로가기 버튼 설정

        // 추가: 입력 필드 변경 감지
        setInputWatchers(); //모든 값이 채워졌는지 확인해서 btnNext 활성/비활성
        setNextButton();

    }

    private void initViews() {
        tvTitle   = findViewById(R.id.tv_title);
        btnBack   = findViewById(R.id.btnBack);
        etLocation = findViewById(R.id.etLocation);
        btnNext   = findViewById(R.id.btnNext);
    }

    /** 이전 (날짜) 화면에서 startDate / endDate 받기 */
    private void receiveFromDateActivity() {
        Intent intent = getIntent();
        startDate = intent.getStringExtra("startDate");
        endDate   = intent.getStringExtra("endDate");

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Log.e(TAG, "날짜 정보가 전달되지 않았습니다. start=" + startDate + ", end=" + endDate);
            Toast.makeText(this, "여행 기간을 입력하지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d(TAG, "받은 날짜: start=" + startDate + ", end=" + endDate);
        }
    }

    private void setTopBar() {
        tvTitle.setText("AI 코스 만들기");
        btnBack.setOnClickListener(v -> finish());
    }

    //  입력 필드 변경 감지하여 버튼 활성화
    private void setInputWatchers() {

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormValid();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etLocation.addTextChangedListener(watcher);
    }

    //  모든 입력값 채워졌는지 체크 → btnNext 활성화
    private void checkFormValid() {
        String location = etLocation.getText().toString().trim();
        boolean isValid = !location.isEmpty();

        btnNext.setEnabled(isValid);
    }

    /** 다음 버튼 클릭 처리 */
    private void setNextButton() {
        btnNext.setOnClickListener(v -> {
            String location = etLocation.getText().toString().trim();

            // 다음 화면으로 넘기는 로직
            Intent intent = new Intent(CreateAICourseLocationActivity.this, CreateAICourseTagsActivity.class);
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("location",location);

            startActivity(intent);
        });
    }


}
