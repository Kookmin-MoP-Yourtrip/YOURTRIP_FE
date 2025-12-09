package com.example.yourtrip.mytrip.create_direct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCourseDetailActivity extends AppCompatActivity {
    private static final String TAG = "CourseDetailActivity";
    private ApiService apiService; // 변수명 apiService로 변경
    private ImageView btnBack;
    private TextView tvTitle;
    private long courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_detail);

        // UI 및 서비스 초기화
        initializeUI();
        setupRetrofit();

        Intent intent = getIntent();
        long receivedCourseId = -1L; // courseId를 담을 임시 변수

        // 두 가지 다른 경로로 들어오는 courseId 처리
        // 시나리오 1: MyTripListFragment에서 '기존 코스 조회'로 들어온 경우 ("courseId" 키 사용)
        if (intent.hasExtra("courseId")) {
            receivedCourseId = intent.getLongExtra("courseId", -1L);
            Log.d(TAG, "[기존 코스 조회] Intent로부터 'courseId' 키로 전달받음: " + receivedCourseId);
        }
        // 시나리오 2: CreateCourseBasicActivity에서 '새 코스 생성' 후 들어온 경우 ("course_basic" 키 사용)
        else if (intent.hasExtra("course_basic")) {
            receivedCourseId = intent.getLongExtra("course_basic", -1L);
            Log.d(TAG, "[새 코스 생성] Intent로부터 'course_basic' 키로 전달받음: " + receivedCourseId);
        }

        // --- 공통 처리 로직 ---
        // 두개의 경로 둘 다 courseId로 처리해서 api 호출
        if (receivedCourseId != -1L) {
            this.courseId = receivedCourseId; // 멤버 변수에 저장
            fetchCourseDetails(this.courseId);
        } else {
            Toast.makeText(this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Intent에서 유효한 courseId를 받지 못했습니다.");
            finish(); // 유효한 ID가 없으면 Activity 종료
        }

//
//        // Intent에서 myCourseId 받기 (long 타입이라고 가정)
//        courseId = getIntent().getLongExtra("course_basic", -1L);
//
//        // courseId 유효성 검사 및 로그 추가
//        if (courseId == -1L) {
//            Toast.makeText(this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//            Log.e("CourseDetailActivity_intent 전달", "Intent에서 유효한 myCourseId를 받지 못했습니다.");
//            finish(); // 유효한 ID가 없으면 Activity 종료
//            return;
//        }
//
//        // 🔵 요청하신 로그: 받아온 courseId 확인
//        Log.d("CourseDetailActivity_intent 전달", "Intent로부터 전달받은 courseId: " + courseId);
//
//        // courseId로 코스 상세 정보 조회 API 호출
//        fetchCourseDetails(courseId);
    }

    // UI 초기화 (상단바, 버튼 설정 등)
    private void initializeUI() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tv_title);
        setTopBar();
    }

    // 상단바 설정
    private void setTopBar() {
        tvTitle.setText("코스 만들기");
        btnBack.setOnClickListener(v -> finish());
    }

    // Retrofit 서비스 설정
    private void setupRetrofit() {
        apiService = RetrofitClient.getAuthService(this);
    }

    // API를 호출하여 코스 상세 정보를 가져오는 메소드
    private void fetchCourseDetails(long courseId) {
        apiService.getMyCourseDetail(courseId).enqueue(new Callback<MyCourseDetailResponse>() {
            @Override
            public void onResponse(Call<MyCourseDetailResponse> call, Response<MyCourseDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyCourseDetailResponse courseDetail = response.body();
                    Log.d("CourseDetailActivity_api 응답", "API 응답 성공: " + courseDetail.toString());

                    if (courseDetail.getDaySchedules() != null) {
                        Log.d("CourseDetailActivity", "--- API 응답: daySchedules 목록 ---");
                        for (MyCourseDetailResponse.DaySchedule schedule : courseDetail.getDaySchedules()) {
                            Log.d("CourseDetailActivity", "Day: " + schedule.getDay() + ", Day ID: " + schedule.getDayId());
                        }
                        Log.d("CourseDetailActivity", "------------------------------------");
                    }

                    // 1. 상단 카드 UI 업데이트
                    updateTripCard(courseDetail);

                    // 2. Fragment로 daySchedules 데이터 전달
                    if (courseDetail.getDaySchedules() != null && !courseDetail.getDaySchedules().isEmpty()) {
                        // 프래그먼트가 아직 추가되지 않았을 때만 추가
                        if (getSupportFragmentManager().findFragmentById(R.id.trip_fragment_container) == null) {
                            addDayDetailFragment(courseDetail.getDaySchedules());
                        }
                    } else {
                        Log.e(TAG, "DaySchedules 리스트가 비어있거나 null입니다.");
                        Toast.makeText(CreateCourseDetailActivity.this, "일차 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e(TAG, "API 응답 실패: " + response.code() + " " + response.message());
                    Toast.makeText(CreateCourseDetailActivity.this, "코스 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyCourseDetailResponse> call, Throwable t) {
                Log.e(TAG, "API 호출 실패", t);
                Toast.makeText(CreateCourseDetailActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // API 응답 데이터로 TripCard UI 업데이트
    private void updateTripCard(MyCourseDetailResponse data) {
        View tripCard = findViewById(R.id.item_trip_card);
        TextView titleTextViewCard = tripCard.findViewById(R.id.tv_title);
        TextView locationTextViewCard = tripCard.findViewById(R.id.tv_location);
        TextView dateTextView = tripCard.findViewById(R.id.tv_date);
        TextView partyTextView = tripCard.findViewById(R.id.tv_party);
        ImageView btnMore = tripCard.findViewById(R.id.btn_more);

        if (btnMore != null) {
            btnMore.setVisibility(View.GONE);
        }


        titleTextViewCard.setText(data.getTitle());
        locationTextViewCard.setText(data.getLocation());
        partyTextView.setText(data.getMemberCount() + "명 참여 중");

        // 날짜 형식 및 "N박 M일" 계산하여 표시
        String periodText = calculatePeriod(data.getStartDate(), data.getEndDate());
        dateTextView.setText(data.getStartDate() + " ~ " + data.getEndDate() + " (" + periodText + ")");
    }

    // "N박 M일" 문자열 생성 (Java 8 time API 사용)
    private String calculatePeriod(String startDate, String endDate) {
        if (startDate == null || endDate == null) return "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = formatter.parse(startDate);
            Date end = formatter.parse(endDate);

            long diffInMillis = end.getTime() - start.getTime();
            long diffDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            long nights = diffDays;
            long days = diffDays + 1;

            return nights + "박 " + days + "일";
        } catch (Exception e) {
            Log.e(TAG, "날짜 계산 중 오류 발생", e);
            return "";
        }
    }

    // 프래그먼트 추가 및 데이터 전달
    private void addDayDetailFragment(List<MyCourseDetailResponse.DaySchedule> daySchedules) {
//        CreateCourseDayDetailFragment fragment = new CreateCourseDayDetailFragment();
//
//        Bundle bundle = new Bundle();
//        // courseID와 daySchedules 리스트를 Bundle에 넣기 위해 Serializable로 캐스팅
//        bundle.putLong("courseId", courseId);
//        bundle.putSerializable("daySchedules", (Serializable) daySchedules);
//        fragment.setArguments(bundle);

        // 🔵 수정: newInstance() 메서드를 호출하여 프래그먼트 생성 및 데이터 전달을 한번에 처리
        CreateCourseDayDetailFragment fragment = CreateCourseDayDetailFragment.newInstance(courseId, daySchedules);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.trip_fragment_container, fragment);
        transaction.commit();
    }
}
