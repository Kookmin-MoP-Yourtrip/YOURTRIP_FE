package com.example.yourtrip.mytrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateAICourseDateActivity extends AppCompatActivity {

    private EditText etStartDate, etEndDate;
    private TextView tvTitle;
    private ImageView btnBack; //include된 상단바의 뒤로가기 버튼
    private Button btnNext;

    private static final String TAG="CreateAICourseBasic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ai_course_date); //레이아웃을 현재 activity 화면으로 사용

        initViews(); //findViewById로 xml의 뷰들을 멤버 변수에 연결
        setTopBar(); //상단바 타이틀과 뒤로가기 버튼 설정
        setDatePicker(); //시작/종료 날짜 EditText 클릭 시 MaterialDatePicker 띄우게 설정

        setNextButton();

        // 추가: 입력 필드 변경 감지
        setInputWatchers(); //모든 값이 채워졌는지 확인해서 btnNext 활성/비활성
    }

    // 뷰 초기화
    //xml에서 정의한 각 뷰를 코드와 연결
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);

        etStartDate = findViewById(R.id.btnStartDate);
        etEndDate = findViewById(R.id.btnEndDate);

        btnNext = findViewById(R.id.btnNext);
    }

    //  상단바 설정
    private void setTopBar() {
        tvTitle.setText("AI 코스 만들기");

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());
    }

    // 날짜 선택 기능 (MaterialDateRangePicker 활용)
    private void setDatePicker() {

        // 클릭 시 키보드 안 뜨게 설정 (EditText지만 클릭 전용)
        etStartDate.setFocusable(false);
        etStartDate.setClickable(true);

        etEndDate.setFocusable(false);
        etEndDate.setClickable(true);

        // 날짜 포맷 (yyyy-MM-dd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // MaterialDatePicker 범위 선택 빌더
        MaterialDatePicker.Builder pickerBuilder = MaterialDatePicker.Builder.dateRangePicker();
        pickerBuilder.setTitleText("여행 날짜 선택");

        // DatePicker 생성
        MaterialDatePicker dateRangePicker = pickerBuilder.build();

        // 출발/종료 날짜 EditText 어느 쪽을 눌러도 같은 picker 호출
        etStartDate.setOnClickListener(v -> dateRangePicker.show(getSupportFragmentManager(), "DATE_RANGE"));
        etEndDate.setOnClickListener(v -> dateRangePicker.show(getSupportFragmentManager(), "DATE_RANGE"));

        // 날짜 선택 완료 시 콜백
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            androidx.core.util.Pair<Long, Long> datePair =
                    (androidx.core.util.Pair<Long, Long>) selection;

            if (datePair.first != null && datePair.second != null) {
                String start = dateFormat.format(new Date(datePair.first));
                String end = dateFormat.format(new Date(datePair.second));

                etStartDate.setText(start);
                etEndDate.setText(end);

                //  날짜 선택 후 버튼 활성화 체크
                checkFormValid();
            }
        });
    }

    // EditText 변화 감지해서 버튼 활성/비활성

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

        etStartDate.addTextChangedListener(watcher);
        etEndDate.addTextChangedListener(watcher);
    }

    //날짜가 다 채워졌는지 확인 -> btnNext 활성화
    private void checkFormValid() {
        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();

        boolean isValid = !start.isEmpty() && !end.isEmpty();

        btnNext.setEnabled(isValid);
    }

    /** 다음 버튼 클릭 처리 */
    private void setNextButton() {
        btnNext.setOnClickListener(v -> {
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "여행 기간을 모두 선택해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidDateRange(startDate, endDate)) {
                Toast.makeText(this, "시작일은 종료일 이후일 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 다음 화면으로 넘기는 로직
             Intent intent = new Intent(CreateAICourseDateActivity.this, CreateAICourseLocationActivity.class);
             intent.putExtra("startDate", startDate);
             intent.putExtra("endDate", endDate);
             startActivity(intent);
        });
    }

    // 날짜 범위 비교
    private boolean isValidDateRange(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date s = sdf.parse(start);
            Date e = sdf.parse(end);
            return !s.after(e);
        } catch (Exception ex) {
            return false;
        }
    }







}
