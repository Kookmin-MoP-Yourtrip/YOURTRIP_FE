package com.example.yourtrip.mytrip.upload;

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

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.UploadCourseResponse;
import com.example.yourtrip.mytrip.util.DateUtils;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.google.android.flexbox.FlexboxLayout;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AfterUploadCourseDetailActivity extends AppCompatActivity {

    private static final String TAG = "AfterUploadActivity";

    private long uploadCourseId; // 이전 화면에서 전달받을 업로드된 코스 ID
    private ApiService apiService;

    // --- UI 뷰 멤버 변수 ---
    private TextView tvTitleCard, tvDateCard, tvLocationCard, tvForkCount;
    private TextView tvIntroduction;
    private FlexboxLayout flexboxTags;
    private View forkButtonLayout;

    // 태그 스타일 카테고리 키워드 리스트
    private final List<String> moveTypeKeywords = Arrays.asList("뚜벅이", "자차");
    private final List<String> partnerKeywords = Arrays.asList("혼자", "연인", "친구", "가족");
    private final List<String> budgetKeywords = Arrays.asList("가성비", "보통", "프리미엄"); // 이 외에는 여행 분위기로 간주


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_upload_course_detail);

        apiService = RetrofitClient.getAuthService(this);

        // 이전 화면(UploadCourseCompleteActivity)에서 'uploadCourseId'를 받아옴
        uploadCourseId = getIntent().getLongExtra("uploadCourseId", -1L);
        Log.d("UPLOAD_DETAIL_ID", "받은 uploadCourseId = " + uploadCourseId);   // ⭐ 추가

        if (uploadCourseId == -1L) {
            Toast.makeText(this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setTopBar();
        fetchUploadedCourseDetails(); // API 호출 시작

        // TODO: 이제 여기서 'uploadCourseId'를 사용하여
        // '업로드된 코스 상세 조회 API'를 호출하고,
        // 응답 데이터로 UI를 업데이트하고, 프래그먼트를 설정하는 로직을 추가
        Toast.makeText(this, "전달받은 코스 ID: " + uploadCourseId, Toast.LENGTH_SHORT).show();


    }
    
    //레이아웃 UI 초기화 메서드
    private void initViews() {
        // 카드 뷰 내부
        View tripCard = findViewById(R.id.item_trip_card);
        tvTitleCard = tripCard.findViewById(R.id.tv_title);
        tvDateCard = tripCard.findViewById(R.id.tv_date);
        tvLocationCard = tripCard.findViewById(R.id.tv_location);

        // 포크 버튼 내부
        forkButtonLayout = tripCard.findViewById(R.id.fork_button);
        tvForkCount = forkButtonLayout.findViewById(R.id.tv_fork_count);
        
        tvForkCount.setText("0");

        // 나머지 UI
        tvIntroduction = findViewById(R.id.tv_uploaded_content);
        flexboxTags = findViewById(R.id.flexbox_upload_confirm_tags);
    }

    //상단바 제목 설정, 닫기 버튼 클릭 이벤트 설정 메서드
    private void setTopBar() {
        // XML 레이아웃에 include된 상단바 뷰를 찾음
        View topBar = findViewById(R.id.top_bar);
        TextView tvTitle = topBar.findViewById(R.id.tv_title);
        ImageView btnClose = topBar.findViewById(R.id.btn_close);

        // 제목을 설정=
        tvTitle.setText("코스 자세히 보기");

        // '닫기(X)' 버튼을 눌렀을 때의 동작을 설정
        btnClose.setOnClickListener(v -> {
            // 홈 화면(MainActivity)으로 이동하는 인텐트를 생성
            Intent intent = new Intent(this, MainActivity.class);
            // 이전에 쌓여있던 모든 액티비티를 스택에서 제거하고, MainActivity를 새로 시작합니다.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // 현재 화면도 종료
        });
    }
    // 업로드된 코스 상세 조회 api 호출 메서드
    private void fetchUploadedCourseDetails() {
        Log.d("UPLOAD_DETAIL_API", "API 요청 보냄 → ID = " + uploadCourseId); // ⭐ 추가

        apiService.getUploadedCourseDetail(uploadCourseId).enqueue(new Callback<UploadCourseResponse>() {
            @Override
            public void onResponse(Call<UploadCourseResponse> call, Response<UploadCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.d("UPLOAD_DETAIL_API", "API 성공! title=" + response.body().getTitle());

                    UploadCourseResponse data = response.body();
                    // API 응답 성공 시, UI를 실제 데이터로 업데이트하고 프래그먼트를 설정
                    updateAllUI(data);
                    setupReadOnlyFragment(data);
                } else {
                    Toast.makeText(AfterUploadCourseDetailActivity.this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("UPLOAD_DETAIL_API",
                            "API 실패! code=" + response.code()
                                    + " | errorBody=" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<UploadCourseResponse> call, Throwable t) {
                Toast.makeText(AfterUploadCourseDetailActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // api 응답 데이터로 ui 업데이트 메서드
    private void updateAllUI(UploadCourseResponse data) {
        // 코스 정보 카드 업데이트
        tvTitleCard.setText(data.getTitle());
        String dateText = DateUtils.formatKoreanDate(data.getStartDate()) + " ~ " + DateUtils.formatKoreanDate(data.getEndDate()) + " (" + DateUtils.getNightDayText(data.getStartDate(), data.getEndDate()) + ")";
        tvDateCard.setText(dateText);
        tvLocationCard.setText(data.getLocation());
        tvForkCount.setText(String.valueOf(data.getForkCount()));

        // 소개글 업데이트
        tvIntroduction.setText(data.getIntroduction());

        // 태그 목록 업데이트
        flexboxTags.removeAllViews();
        if (data.getKeywords() != null) {
            for (String tagName : data.getKeywords()) {
                // 태그 이름에 따라 적용할 스타일 ID를 결정
                int styleResId = getStyleForTag(tagName);
                ContextThemeWrapper contextWrapper = new ContextThemeWrapper(this, styleResId);

                // view_upload_tag.xml을 기반으로, 스타일이 적용된 Context를 사용하여 TextView를 생성
                TextView tagView = (TextView) LayoutInflater.from(contextWrapper)
                        .inflate(R.layout.view_upload_tag, flexboxTags, false);

                tagView.setText(tagName);
                flexboxTags.addView(tagView);
            }
        }
    }

    // 태그 이름에 따라 적절한 스타일 리소스를 반환하는 헬퍼 메서드
    private int getStyleForTag(java.lang.String tagName) {
        if (moveTypeKeywords.contains(tagName)) {
            return R.style.Tag_Movetype;
        } else if (partnerKeywords.contains(tagName)) {
            return R.style.Tag_Partner;
        } else if (budgetKeywords.contains(tagName)) {
            return R.style.Tag_Budget;
        } else {
            // 그 외에는 모두 '여행 분위기' 태그로 간주
            return R.style.Tag_Theme;
        }
    }


    // 읽기 전용 상세 일정 프레그먼트 설정하는 메서드
    private void setupReadOnlyFragment(UploadCourseResponse data) {
        List<UploadCourseResponse.DaySchedule> daySchedules = data.getDaySchedules();

        if (daySchedules == null || daySchedules.isEmpty()) {
            // TODO: 일정이 없을 때의 UI 처리 (예: "일정이 없습니다" 텍스트 표시)
            return;
        }

        Log.d(TAG, "--- 프래그먼트 설정 시작 ---");
        Log.d(TAG, "전달할 DaySchedules: " + (daySchedules != null ? "총 " + daySchedules.size() + "개" : "null"));
        Log.d(TAG, "DaySchedules 내용: " + daySchedules.toString()); // toString()으로 실제 내용 확인
        Log.d(TAG, "----------------------");

        // '읽기 전용' 프래그먼트 ReadOnlyCourseDetailFragment를 생성
        //  API 응답에 포함된 daySchedules 데이터만 받음
        ReadOnlyCourseDetailFragment fragment = ReadOnlyCourseDetailFragment.newInstance(daySchedules);

        // FragmentManager를 사용하여 프래그먼트를 trip_fragment_container에 표시
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.trip_fragment_container, fragment)
                .commit();
    }

}
