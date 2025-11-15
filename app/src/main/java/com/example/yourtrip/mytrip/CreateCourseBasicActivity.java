package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.datepicker.MaterialDatePicker.Builder;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateCourseBasicActivity extends AppCompatActivity {

    private EditText etStartDate, etEndDate;
    private TextView tvTitle;
    private ImageView btnBack;    // include된 상단바의 뒤로가기 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_basic);

        initViews();
        setTopBar();
        setDatePicker();
    }

    // 뷰 초기화
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);

        etStartDate = findViewById(R.id.btnStartDate);
        etEndDate = findViewById(R.id.btnEndDate);
    }

    //  상단바 설정
    private void setTopBar() {
        tvTitle.setText("코스 만들기");

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
            // selection = Pair<Long, Long> 형식
            androidx.core.util.Pair<Long, Long> datePair = (androidx.core.util.Pair<Long, Long>) selection;

            if (datePair.first != null && datePair.second != null) {
                String start = dateFormat.format(new Date(datePair.first));
                String end = dateFormat.format(new Date(datePair.second));

                etStartDate.setText(start);
                etEndDate.setText(end);
            }
        });
    }
}
