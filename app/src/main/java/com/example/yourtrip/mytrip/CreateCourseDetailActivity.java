package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;

import com.example.yourtrip.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateCourseDetailActivity extends AppCompatActivity {
    private static final String TAG = "CreateCourseDetail";
    private ImageView btnBack;    // 상단바의 뒤로가기 버튼
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_detail);

        // 상단바의 버튼과 텍스트 뷰 초기화
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tv_title);
        // 상단바 설정
        setTopBar();

        // Intent에서 전달된 데이터 받기
        String courseTitle = getIntent().getStringExtra("courseTitle");
        String location = getIntent().getStringExtra("location");
        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");


        // item_trip_card 레이아웃을 가져오고, 텍스트를 동적으로 수정
        View tripCard = findViewById(R.id.item_trip_card);  // 포함된 item_trip_card
        TextView titleTextViewCard = tripCard.findViewById(R.id.tv_title);
        TextView locationTextViewCard = tripCard.findViewById(R.id.tv_location);
        TextView dateTextView = tripCard.findViewById(R.id.tv_date);
        TextView partyTextView = tripCard.findViewById(R.id.tv_party);

        // 텍스트 업데이트
        titleTextViewCard.setText(courseTitle);
        locationTextViewCard.setText(location);

        // 날짜 차이 계산 및 "박" "일" 표시
        String periodText = calculateStayPeriod(startDate, endDate);
        dateTextView.setText(startDate + " ~ " + endDate + " (" + periodText + ")");

        // 인원 수는 1명 참여 중
        partyTextView.setText("1명 참여 중");
    }

    // 상단바 설정
    private void setTopBar() {
        tvTitle.setText("코스 만들기");

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());
    }

    // 날짜 차이 계산 (몇 박 며칠)
    private String calculateStayPeriod(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            long diffMillis = end.getTime() - start.getTime();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);

            // "박" "일" 형식으로 리턴
            long nights = diffDays;
            long days = diffDays + 1;
            return nights + "박 " + days + "일";

        } catch (Exception e) {
            return "";
        }
    }
}
