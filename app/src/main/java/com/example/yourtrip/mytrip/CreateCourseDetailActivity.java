package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import com.example.yourtrip.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;

public class CreateCourseDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_detail);

        // UI 초기화
        initializeUI();

        // Intent에서 전달된 데이터 받기
        String courseTitle = getIntent().getStringExtra("courseTitle");
        String location = getIntent().getStringExtra("location");
        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");

        // 1) 날짜 계산 → periodText & dayCount 생성
        StringBuilder periodText = new StringBuilder(); // 🔵 추가됨
        int dayCount = calculatePeriod(startDate, endDate, periodText); // 🔵 추가됨

        // 2) TripCard UI 업데이트
        updateTripCard(courseTitle, location, startDate, endDate, periodText.toString()); // 🔵 변경됨

        // 3) dayList 생성
        ArrayList<String> dayList = generateDayList(dayCount); // 🔵 추가됨
        Log.d("CreateCourseDetail", "dayList = " + dayList);

        // 4) Fragment로 dayList 전달
        if (savedInstanceState == null) {
            addFragment(dayList); // 🔵 변경됨
        }
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

    // TripCard UI 업데이트만 담당
    private void updateTripCard(String courseTitle, String location, String startDate, String endDate, String periodText) {
        View tripCard = findViewById(R.id.item_trip_card);
        TextView titleTextViewCard = tripCard.findViewById(R.id.tv_title);
        TextView locationTextViewCard = tripCard.findViewById(R.id.tv_location);
        TextView dateTextView = tripCard.findViewById(R.id.tv_date);
        TextView partyTextView = tripCard.findViewById(R.id.tv_party);

        titleTextViewCard.setText(courseTitle);
        locationTextViewCard.setText(location);
        dateTextView.setText(startDate + " ~ " + endDate + " (" + periodText + ")"); // 🔵 변경됨
        partyTextView.setText("1명 참여 중");
    }

    // 🔵 날짜 차이 계산 + "N박 M일" 문자열 생성 + 총 며칠인지 반환
    private int calculatePeriod(String startDate, String endDate, StringBuilder periodTextOut) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            long diffMillis = end.getTime() - start.getTime();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);

            long nights = diffDays;
            long days = diffDays + 1;

            periodTextOut.append(nights + "박 " + days + "일");

            return (int) days;   // 총 며칠인지(dayCount)

        } catch (Exception e) {
            periodTextOut.append("");
            return 1;
        }
    }

    // 🔵 dayList 생성 함수
    private ArrayList<String> generateDayList(int dayCount) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= dayCount; i++) {
            list.add(i + "일차");
        }
        return list;
    }

    // 프래그먼트 동적으로 추가
    private void addFragment(ArrayList<String> dayList) {  // 🔵 변경됨
        CreateCourseDayDetailFragment fragment = new CreateCourseDayDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("dayList", dayList); // 🔵 추가됨
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.trip_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}




//package com.example.yourtrip.mytrip;
//
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.LinearLayout;
//import androidx.appcompat.app.AppCompatActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.util.Log;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.example.yourtrip.R;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class CreateCourseDetailActivity extends AppCompatActivity {
//    private static final String TAG = "CreateCourseDetail";
//    private ImageView btnBack;    // 상단바의 뒤로가기 버튼
//    private TextView tvTitle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_course_detail);
//
//        // 상단바의 버튼과 텍스트 뷰 초기화
//        btnBack = findViewById(R.id.btnBack);
//        tvTitle = findViewById(R.id.tv_title);
//        // 상단바 설정
//        setTopBar();
//
//        // Intent에서 전달된 데이터 받기
//        String courseTitle = getIntent().getStringExtra("courseTitle");
//        String location = getIntent().getStringExtra("location");
//        String startDate = getIntent().getStringExtra("startDate");
//        String endDate = getIntent().getStringExtra("endDate");
//
//
//        // item_trip_card 레이아웃을 가져오고, 텍스트를 동적으로 수정
//        View tripCard = findViewById(R.id.item_trip_card);  // 포함된 item_trip_card
//        TextView titleTextViewCard = tripCard.findViewById(R.id.tv_title);
//        TextView locationTextViewCard = tripCard.findViewById(R.id.tv_location);
//        TextView dateTextView = tripCard.findViewById(R.id.tv_date);
//        TextView partyTextView = tripCard.findViewById(R.id.tv_party);
//
//        // 텍스트 업데이트
//        titleTextViewCard.setText(courseTitle);
//        locationTextViewCard.setText(location);
//
//        // 날짜 차이 계산 및 "박" "일" 표시
//        String periodText = calculateStayPeriod(startDate, endDate);
//        dateTextView.setText(startDate + " ~ " + endDate + " (" + periodText + ")");
//
//        // 인원 수는 1명 참여 중
//        partyTextView.setText("1명 참여 중");
//
//        // 프래그먼트 동적으로 추가
//        if (savedInstanceState == null) {
//            // 프래그먼트를 동적으로 추가
//            CreateCourseDayDetailFragment fragment = new CreateCourseDayDetailFragment();
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, fragment);  // 프래그먼트 컨테이너에 추가
//            transaction.addToBackStack(null);  // 뒤로가기 스택에 추가
//            transaction.commit();  // 트랜잭션 커밋
//        }
//    }
//
//    // 상단바 설정
//    private void setTopBar() {
//        tvTitle.setText("코스 만들기");
//
//        // 뒤로가기 버튼
//        btnBack.setOnClickListener(v -> finish());
//    }
//
//    // 날짜 차이 계산 (몇 박 며칠)
//    private String calculateStayPeriod(String startDate, String endDate) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//            Date start = sdf.parse(startDate);
//            Date end = sdf.parse(endDate);
//
//            long diffMillis = end.getTime() - start.getTime();
//            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
//
//            // "박" "일" 형식으로 리턴
//            long nights = diffDays;
//            long days = diffDays + 1;
//            return nights + "박 " + days + "일";
//
//        } catch (Exception e) {
//            return "";
//        }
//    }
//}
