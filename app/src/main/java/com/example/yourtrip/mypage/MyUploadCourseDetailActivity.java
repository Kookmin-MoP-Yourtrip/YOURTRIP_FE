package com.example.yourtrip.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.UploadCourseResponse;
import com.example.yourtrip.mytrip.upload.ReadOnlyCourseDetailFragment;
import com.example.yourtrip.mytrip.util.DateUtils;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.google.android.flexbox.FlexboxLayout;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyUploadCourseDetailActivity extends AppCompatActivity {

    private static final String TAG = "MyUploadCourseDetail";

    private long uploadCourseId;
    private ApiService apiService;

    // UI 뷰 멤버 변수
    private TextView tvTitleCard, tvDateCard, tvLocationCard, tvForkCount;
    private TextView tvIntroduction;
    private FlexboxLayout flexboxTags;

    // 태그 스타일 카테고리 키워드 리스트
    private final List<String> moveTypeKeywords = Arrays.asList("뚜벅이", "자차");
    private final List<String> partnerKeywords = Arrays.asList("혼자", "연인", "친구", "가족");
    private final List<String> budgetKeywords = Arrays.asList("가성비", "평균예산", "프리미엄");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload_course_detail);

        apiService = RetrofitClient.getAuthService(this);

        // uploadCourseId 받아오기
        uploadCourseId = getIntent().getLongExtra("uploadCourseId", -1L);
        Log.d(TAG, "받은 uploadCourseId = " + uploadCourseId);

        if (uploadCourseId == -1L) {
            Toast.makeText(this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setTopBar();
        fetchUploadedCourseDetails();
    }

    // 레이아웃 UI 초기화
    private void initViews() {
        View tripCard = findViewById(R.id.item_trip_card);
        tvTitleCard = tripCard.findViewById(R.id.tv_title);
        tvDateCard = tripCard.findViewById(R.id.tv_date);
        tvLocationCard = tripCard.findViewById(R.id.tv_location);

        // 수정: 새로운 레이아웃 구조에 맞게 ID를 찾고 상태를 설정
        View forkButtonDefault = tripCard.findViewById(R.id.fork_button_default);
        View forkButtonActive = tripCard.findViewById(R.id.fork_button_active);

        // '내가 올린 코스'에서는 포크할 수 없으므로, 기본 상태의 버튼만 보여줌
        forkButtonDefault.setVisibility(View.VISIBLE);
        forkButtonActive.setVisibility(View.GONE);

        tvForkCount = forkButtonDefault.findViewById(R.id.tv_fork_count);

        // '내가 올린 코스'에서는 포크 버튼이 눌리지 않도록 하거나, 안내 메시지를 표시
        forkButtonDefault.setOnClickListener(v ->
            Toast.makeText(this, "자신이 업로드한 코스는 포크할 수 없습니다.", Toast.LENGTH_SHORT).show()
        );

        tvIntroduction = findViewById(R.id.tv_uploaded_content);
        flexboxTags = findViewById(R.id.flexbox_upload_confirm_tags);
    }

    // 커스텀 상단바 설정 (뒤로 가기 버튼)
    private void setTopBar() {
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView tvTitle = findViewById(R.id.tv_top_title);

        tvTitle.setText("코스 자세히 보기");

        // 뒤로 가기 버튼 클릭 시 현재 Activity 종료
        btnBack.setOnClickListener(v -> finish());
    }

    // 업로드된 코스 상세 조회 API 호출
    private void fetchUploadedCourseDetails() {
        Log.d(TAG, "API 요청 보냄 → ID = " + uploadCourseId);

        apiService.getUploadedCourseDetail(uploadCourseId).enqueue(new Callback<UploadCourseResponse>() {
            @Override
            public void onResponse(Call<UploadCourseResponse> call, Response<UploadCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API 성공! title=" + response.body().getTitle());

                    UploadCourseResponse data = response.body();
                    updateAllUI(data);
                    setupReadOnlyFragment(data);
                } else {
                    Toast.makeText(MyUploadCourseDetailActivity.this,
                            "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API 실패! code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<UploadCourseResponse> call, Throwable t) {
                Toast.makeText(MyUploadCourseDetailActivity.this,
                        "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // API 응답 데이터로 UI 업데이트
    private void updateAllUI(UploadCourseResponse data) {
        tvTitleCard.setText(data.getTitle());
        String dateText = DateUtils.formatKoreanDate(data.getStartDate()) + " ~ " +
                DateUtils.formatKoreanDate(data.getEndDate()) + " (" +
                DateUtils.getNightDayText(data.getStartDate(), data.getEndDate()) + ")";
        tvDateCard.setText(dateText);
        tvLocationCard.setText(data.getLocation());
        tvForkCount.setText(String.valueOf(data.getForkCount()));

        tvIntroduction.setText(data.getIntroduction());

        flexboxTags.removeAllViews();
        if (data.getKeywords() != null) {
            for (String tagName : data.getKeywords()) {
                int styleResId = getStyleForTag(tagName);
                ContextThemeWrapper contextWrapper = new ContextThemeWrapper(this, styleResId);

                TextView tagView = (TextView) LayoutInflater.from(contextWrapper)
                        .inflate(R.layout.view_upload_tag, flexboxTags, false);

                tagView.setText(tagName);
                flexboxTags.addView(tagView);
            }
        }
    }

    // 태그 이름에 따라 적절한 스타일 반환
    private int getStyleForTag(String tagName) {
        if (moveTypeKeywords.contains(tagName)) {
            return R.style.Tag_Movetype;
        } else if (partnerKeywords.contains(tagName)) {
            return R.style.Tag_Partner;
        } else if (budgetKeywords.contains(tagName)) {
            return R.style.Tag_Budget;
        } else {
            return R.style.Tag_Theme;
        }
    }

    // 읽기 전용 상세 일정 프래그먼트 설정
    private void setupReadOnlyFragment(UploadCourseResponse data) {
        List<UploadCourseResponse.DaySchedule> daySchedules = data.getDaySchedules();

        if (daySchedules == null || daySchedules.isEmpty()) {
            return;
        }

        Log.d(TAG, "프래그먼트 설정 - DaySchedules: 총 " + daySchedules.size() + "개");

        ReadOnlyCourseDetailFragment fragment = ReadOnlyCourseDetailFragment.newInstance(daySchedules);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.trip_fragment_container, fragment)
                .commit();
    }
}
