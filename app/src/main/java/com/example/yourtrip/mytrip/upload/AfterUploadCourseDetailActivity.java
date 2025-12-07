package com.example.yourtrip.mytrip.upload;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.ForkCourseResponse;
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

    private long uploadCourseId;
    private ApiService apiService;

    // --- UI 뷰 멤버 변수 ---
    private TextView tvTitleCard, tvDateCard, tvLocationCard;
    private TextView tvIntroduction;
    private FlexboxLayout flexboxTags;

    //  기본/활성화 상태의 버튼과 카운트 뷰를 모두 변수로 선언
    private View forkButtonDefault;
    private View forkButtonActive;
    private TextView tvForkCountDefault;
    private TextView tvForkCountActive;

    private final List<String> moveTypeKeywords = Arrays.asList("뚜벅이", "자차");
    private final List<String> partnerKeywords = Arrays.asList("혼자", "연인", "친구", "가족");
    private final List<String> budgetKeywords = Arrays.asList("가성비", "평균예산", "프리미엄");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_upload_course_detail);

        apiService = RetrofitClient.getAuthService(this);

        uploadCourseId = getIntent().getLongExtra("uploadCourseId", -1L);
        if (uploadCourseId == -1L) {
            Toast.makeText(this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setTopBar();
        fetchUploadedCourseDetails();
        setForkButtonListener();
    }

    private void initViews() {
        View tripCard = findViewById(R.id.item_trip_card);
        tvTitleCard = tripCard.findViewById(R.id.tv_title);
        tvDateCard = tripCard.findViewById(R.id.tv_date);
        tvLocationCard = tripCard.findViewById(R.id.tv_location);

        // 두 개의 버튼 레이아웃과 내부의 카운트 TextView를 모두 찾음
        forkButtonDefault = tripCard.findViewById(R.id.fork_button_default);
        forkButtonActive = tripCard.findViewById(R.id.fork_button_active);
        tvForkCountDefault = forkButtonDefault.findViewById(R.id.tv_fork_count);
        tvForkCountActive = forkButtonActive.findViewById(R.id.tv_fork_count);

        tvIntroduction = findViewById(R.id.tv_uploaded_content);
        flexboxTags = findViewById(R.id.flexbox_upload_confirm_tags);
    }

    private void setTopBar() {
        View topBar = findViewById(R.id.top_bar);
        TextView tvTitle = topBar.findViewById(R.id.tv_title);
        ImageView btnClose = topBar.findViewById(R.id.btn_close);

        tvTitle.setText("코스 자세히 보기");

        btnClose.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUploadedCourseDetails() {
        apiService.getUploadedCourseDetail(uploadCourseId).enqueue(new Callback<UploadCourseResponse>() {
            @Override
            public void onResponse(@NonNull Call<UploadCourseResponse> call, @NonNull Response<UploadCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UploadCourseResponse data = response.body();
                    updateAllUI(data);
                    setupReadOnlyFragment(data);
                } else {
                    Toast.makeText(AfterUploadCourseDetailActivity.this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadCourseResponse> call, @NonNull Throwable t) {
                Toast.makeText(AfterUploadCourseDetailActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAllUI(UploadCourseResponse data) {
        tvTitleCard.setText(data.getTitle());
        String dateText = DateUtils.formatKoreanDate(data.getStartDate()) + " ~ " + DateUtils.formatKoreanDate(data.getEndDate()) + " (" + DateUtils.getNightDayText(data.getStartDate(), data.getEndDate()) + ")";
        tvDateCard.setText(dateText);
        tvLocationCard.setText(data.getLocation());

        //  초기 카운트를 두 버튼 모두에 설정
        String forkCountStr = String.valueOf(data.getForkCount());
        tvForkCountDefault.setText(forkCountStr);
        tvForkCountActive.setText(forkCountStr);

        // TODO: isForked 필드가 추가되면, 초기 visibility 상태를 여기서 설정
        // if (data.isForked()) {
        //     forkButtonDefault.setVisibility(View.GONE);
        //     forkButtonActive.setVisibility(View.VISIBLE);
        // } else {
        //     forkButtonDefault.setVisibility(View.VISIBLE);
        //     forkButtonActive.setVisibility(View.GONE);
        // }

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

    private int getStyleForTag(java.lang.String tagName) {
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

    private void setupReadOnlyFragment(UploadCourseResponse data) {
        List<UploadCourseResponse.DaySchedule> daySchedules = data.getDaySchedules();
        if (daySchedules == null || daySchedules.isEmpty()) return;

        ReadOnlyCourseDetailFragment fragment = ReadOnlyCourseDetailFragment.newInstance(daySchedules);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.trip_fragment_container, fragment)
                .commit();
    }

    /**
     * 포크 버튼에 클릭 리스너를 설정하는 메서드
     */
    private void setForkButtonListener() {
        //  '기본 상태' 버튼에만 클릭 리스너를 설정합니다.
        forkButtonDefault.setOnClickListener(v -> {
            forkCourseApiCall();
        });

        // '활성화 상태' 버튼은 눌러도 아무 동작도 하지 않도록 하거나, Toast 메시지를 띄움
        forkButtonActive.setOnClickListener(v -> {
            Toast.makeText(this, "이미 포크한 코스입니다.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Fork API를 호출하고 응답을 처리하는 메서드
     */
    private void forkCourseApiCall() {
        apiService.forkCourse(uploadCourseId).enqueue(new Callback<ForkCourseResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForkCourseResponse> call, @NonNull Response<ForkCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // --- 포크 성공 ---
                    // 카운트 숫자 1 증가
                    int currentForkCount = Integer.parseInt(tvForkCountDefault.getText().toString());
                    String newForkCountStr = String.valueOf(currentForkCount + 1);
                    
                    // 두 버튼의 카운트를 모두 업데이트
                    tvForkCountDefault.setText(newForkCountStr);
                    tvForkCountActive.setText(newForkCountStr);

                    // 버튼의 visibility를 교체하여 상태 변경
                    forkButtonDefault.setVisibility(View.GONE);
                    forkButtonActive.setVisibility(View.VISIBLE);

                    showForkSuccessDialog();

                } else {
                    // --- 포크 실패 (자신의 코스 등) ---
                    Toast.makeText(AfterUploadCourseDetailActivity.this, "자신이 업로드한 코스는 포크할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForkCourseResponse> call, @NonNull Throwable t) {
                // --- 네트워크 오류 ---
                Toast.makeText(AfterUploadCourseDetailActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 포크 성공 시 보여줄 커스텀 다이얼로그
     */
    private void showForkSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialogStyle);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_fork_success, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // '확인' 버튼
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> dialog.dismiss());

        // '나의 여행 리스트 보기' 버튼
        Button btnGoToList = dialogView.findViewById(R.id.btnGoToList);
        btnGoToList.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigateTo", "MyTripListFragment");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}
