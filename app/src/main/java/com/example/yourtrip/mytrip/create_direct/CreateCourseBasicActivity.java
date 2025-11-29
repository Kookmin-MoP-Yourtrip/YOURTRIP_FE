//detail 코스 작성 페이지로 넘어가는 코드
package com.example.yourtrip.mytrip.create_direct;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.MyCourseCreateBasicResponse;
import com.example.yourtrip.mytrip.model.MyCourseCreateRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCourseBasicActivity extends AppCompatActivity {

    private EditText etStartDate, etEndDate, etCourseTitle, etLocation;
    private TextView tvTitle;
    private ImageView btnBack;    // include된 상단바의 뒤로가기 버튼
    private Button btnNext;

    private ApiService apiService;

    private static final String TAG = "CreateCourseBasic"; //로그찍을 때 사용할 태그 문자열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_basic); //이 레이아웃을 이 activity 화면으로 사용

        initViews(); //findViewById로 xml의 뷰들을 멤버 변수에 연결
        setTopBar(); //상단바 타이틀과 뒤로가기 버튼 설정
        setDatePicker(); //시작/종료 날짜 EditText 클릭 시 MaterialDatePicker 띄우게 설정

        apiService = RetrofitClient.getAuthService(this);
        setNextButton();

        // 추가: 입력 필드 변경 감지
        setInputWatchers(); //모든 값이 채워졌는지 확인해서 btnNext 활성/비활성
    }

    // 뷰 초기화
    //xml에서 정의한 각 뷰를 코드와 연결
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);

        etCourseTitle = findViewById(R.id.etCourseTitle);
        etLocation = findViewById(R.id.etLocation);

        etStartDate = findViewById(R.id.btnStartDate);
        etEndDate = findViewById(R.id.btnEndDate);

        btnNext = findViewById(R.id.btnNext);
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

        etCourseTitle.addTextChangedListener(watcher);
        etLocation.addTextChangedListener(watcher);
        etStartDate.addTextChangedListener(watcher);
        etEndDate.addTextChangedListener(watcher);
    }

    //  모든 입력값 채워졌는지 체크 → btnNext 활성화
    private void checkFormValid() {
        String title = etCourseTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();

        boolean isValid = !title.isEmpty() && !location.isEmpty()
                && !start.isEmpty() && !end.isEmpty();

        btnNext.setEnabled(isValid);
    }


    // 다음 버튼 클릭 이벤트
    private void setNextButton() {
        btnNext.setOnClickListener(v -> {
            // 입력값을 받아오기
            String title = etCourseTitle.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();

            // 간단 유효성 검사
            if (title.isEmpty() || location.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidDateRange(startDate, endDate)) {
                Toast.makeText(this, "시작일은 종료일 이후일 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 요청 객체 생성
            MyCourseCreateRequest request = new MyCourseCreateRequest(title, location, startDate, endDate);

            // API 요청 보내기
            submitCourse(request);

//            // Intent로 데이터를 넘기기 (CreateCourseDetailActivity로)
//            Intent intent = new Intent(CreateCourseBasicActivity.this, CreateCourseDetailActivity.class);
//            intent.putExtra("courseTitle", title);
//            intent.putExtra("location", location);
//            intent.putExtra("startDate", startDate);
//            intent.putExtra("endDate", endDate);
//
//            // 로그로 Intent 데이터를 확인
//            Log.d(TAG, "Intent data - courseTitle: " + title);
//            Log.d(TAG, "Intent data - location: " + location);
//            Log.d(TAG, "Intent data - startDate: " + startDate);
//            Log.d(TAG, "Intent data - endDate: " + endDate);
//
//
//            // 다음 화면으로 이동
//            startActivity(intent);
        });
    }


    // API 요청
    private void submitCourse(MyCourseCreateRequest request) {
        btnNext.setEnabled(false);
        // Call 제네릭 타입을 MyCourseCreateBasicResponse로 수정함
        apiService.createMyCourse(request).enqueue(new Callback<MyCourseCreateBasicResponse>() {
            @Override
            public void onResponse(Call<MyCourseCreateBasicResponse> call, Response<MyCourseCreateBasicResponse> response) {
                btnNext.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // [수정] API 성공 시, 여기서 다음 화면으로 이동
                    Toast.makeText(CreateCourseBasicActivity.this, "코스 생성 완료!", Toast.LENGTH_SHORT).show(); //"코스생성완료" 토스트 띄우기

                    // 서버로부터 받은 응답 데이터
                    MyCourseCreateBasicResponse courseResponse = response.body();
                    Log.d(TAG, "MyCourseCreateBasic_API 응답 데이터: " + courseResponse.toString());


                    //createCourseBasicActivity로 이동하기 위한 intent 생성
                    Intent intent = new Intent(CreateCourseBasicActivity.this, CreateCourseDetailActivity.class);
                    // "course_detail" 이라는 키로, 서버에서 받은 응답 객체 전체를 담아서 전달
//                    intent.putExtra("course_detail", courseResponse);
                    intent.putExtra("course_basic", courseResponse.getMyCourseId());
                    startActivity(intent);

                    // DetailActivity에서 뒤로가기 시 MyTripListFragment로 돌아가게 함
                    finish();

                } else {
                    // ... (기존의 400 에러 및 기타 에러 처리 로직은 그대로)
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("MyCourseCreate", "에러 코드: " + response.code() + ", errorBody = " + errorBody);
                        Toast.makeText(CreateCourseBasicActivity.this, "코스 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyCourseCreateBasicResponse> call, Throwable t) {
                btnNext.setEnabled(true);
                Log.e("MyCourseCreate", "API 실패: " + t.getMessage());
                Toast.makeText(CreateCourseBasicActivity.this,
                        "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
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





