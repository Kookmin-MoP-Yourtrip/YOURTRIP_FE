package com.example.yourtrip.mytrip.upload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yourtrip.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 코스 업로드 과정 중, 코스와 관련된 태그를 선택하는 화면.
 * XML에 정의된 Selector를 사용하여 UI를 변경하고, 카테고리별 선택 로직을 관리합니다.
 */
public class UploadCourseTagsActivity extends AppCompatActivity {

    private long courseId; // 이전 화면에서 전달받을 코스 ID

    // [수정] 선택된 태그 '뷰'를 카테고리별로 나누어 관리합니다. (필수 선택 검사 및 단일 선택 처리를 위해)
    private final List<TextView> selectedMoveTypeTags = new ArrayList<>();
    private final List<TextView> selectedCompanionTags = new ArrayList<>();
    private final List<TextView> selectedMoodTags = new ArrayList<>();
    private final List<TextView> selectedBudgetTags = new ArrayList<>();

    // 각 카테고리별 모든 태그 뷰들을 나누어 관리합니다.
    private final List<TextView> moveTypeTags = new ArrayList<>();
    private final List<TextView> companionTags = new ArrayList<>();
    private final List<TextView> moodTags = new ArrayList<>();
    private final List<TextView> budgetTags = new ArrayList<>();

    private Button btnNext;
    private ProgressBar progressUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course_tags);

        // 1. 이전 화면(MyTripListFragment)에서 'courseId'를 받습니다.
        courseId = getIntent().getLongExtra("courseId", -1L);
        if (courseId == -1L) {
            Toast.makeText(this, "코스 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish(); // courseId가 없으면 이 화면은 의미가 없으므로 종료
            return;
        }

        // 2. 화면을 구성하는 헬퍼 메서드들을 순서대로 호출합니다.
        initViews();
        setTopBar();
        setTagClickListeners();
        setNextButton();
        updateNextButtonState(); // 초기 버튼 상태는 비활성화
    }

    /**
     * XML 레이아웃의 뷰들을 찾아서 카테고리별 리스트에 담습니다.
     */
    private void initViews() {
        btnNext = findViewById(R.id.btnNext);
        progressUpload = findViewById(R.id.progressSignup);

        // 이동 수단
        moveTypeTags.add(findViewById(R.id.tag_search_walking));
        moveTypeTags.add(findViewById(R.id.tag_search_car));
        // 동행 유형
        companionTags.add(findViewById(R.id.tag_search_solo));
        companionTags.add(findViewById(R.id.tag_search_couple));
        companionTags.add(findViewById(R.id.tag_search_friends));
        companionTags.add(findViewById(R.id.tag_search_family));
        // 여행 분위기
        moodTags.add(findViewById(R.id.tag_search_healing));
        moodTags.add(findViewById(R.id.tag_search_activity));
        moodTags.add(findViewById(R.id.tag_search_food_tour));
        moodTags.add(findViewById(R.id.tag_search_emotional));
        moodTags.add(findViewById(R.id.tag_search_culture_exhibition));
        moodTags.add(findViewById(R.id.tag_search_nature));
        moodTags.add(findViewById(R.id.tag_search_shopping));
        // 예산
        budgetTags.add(findViewById(R.id.tag_search_budget));
        budgetTags.add(findViewById(R.id.tag_search_normal));
        budgetTags.add(findViewById(R.id.tag_search_premium));
    }

    /**
     * 상단바의 제목과 뒤로가기 버튼을 설정합니다.
     */
    private void setTopBar() {
        View topBar = findViewById(R.id.topBar);
        TextView tvTitle = topBar.findViewById(R.id.tv_title);
        ImageView btnBack = topBar.findViewById(R.id.btnBack);

        tvTitle.setText("코스 업로드");
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * 모든 태그 TextView에 클릭 리스너를 설정합니다.
     * 카테고리별로 단일 선택/다중 선택 여부를 지정하여 공통 메서드를 호출합니다.
     */
    private void setTagClickListeners() {
        // 이동 수단 (단일 선택)
        for (TextView tagView : moveTypeTags) {
            tagView.setOnClickListener(v -> handleTagClick(tagView, selectedMoveTypeTags, true));
        }
        // 동행 유형 (단일 선택)
        for (TextView tagView : companionTags) {
            tagView.setOnClickListener(v -> handleTagClick(tagView, selectedCompanionTags, true));
        }
        // 여행 분위기 (다중 선택)
        for (TextView tagView : moodTags) {
            tagView.setOnClickListener(v -> handleTagClick(tagView, selectedMoodTags, false));
        }
        // 예산 (단일 선택)
        for (TextView tagView : budgetTags) {
            tagView.setOnClickListener(v -> handleTagClick(tagView, selectedBudgetTags, true));
        }
    }

    /**
     * 태그 클릭 이벤트를 처리하는 공통 메서드.
     * UI 변경은 XML Selector가 담당하므로, Java에서는 상태(selected)와 데이터만 관리합니다.
     * @param clickedTag 클릭된 TextView
     * @param selectedTagsInCategory 해당 카테고리에서 선택된 TextView 리스트
     * @param isSingleSelection 단일 선택 여부
     */
    private void handleTagClick(TextView clickedTag, List<TextView> selectedTagsInCategory, boolean isSingleSelection) {
        boolean isCurrentlySelected = clickedTag.isSelected();

        if (isCurrentlySelected) {
            // 이미 선택된 태그를 다시 클릭 -> 선택 해제
            clickedTag.setSelected(false);
            selectedTagsInCategory.remove(clickedTag);
        } else {
            // 새로운 태그를 클릭
            if (isSingleSelection) {
                // 단일 선택 모드일 경우, 기존에 선택된 태그가 있다면 모두 해제
                if (!selectedTagsInCategory.isEmpty()) {
                    TextView oldTag = selectedTagsInCategory.get(0);
                    oldTag.setSelected(false); // 상태만 변경하면 XML Selector가 UI를 자동으로 바꿈
                    selectedTagsInCategory.clear();
                }
            }
            // 새로운 태그 선택
            clickedTag.setSelected(true); // 상태만 변경하면 XML Selector가 UI를 자동으로 바꿈
            selectedTagsInCategory.add(clickedTag);
        }
        // '다음' 버튼 활성화 여부 업데이트
        updateNextButtonState();
    }

    /**
     * '다음' 버튼의 클릭 이벤트를 설정합니다.
     */
    private void setNextButton() {
        btnNext.setOnClickListener(v -> {
            // 모든 카테고리에서 선택된 태그들의 텍스트를 하나의 리스트로 합칩니다.
            List<String> allSelectedTags = new ArrayList<>();
            for (TextView tag : selectedMoveTypeTags) allSelectedTags.add(tag.getText().toString());
            for (TextView tag : selectedCompanionTags) allSelectedTags.add(tag.getText().toString());
            for (TextView tag : selectedMoodTags) allSelectedTags.add(tag.getText().toString());
            for (TextView tag : selectedBudgetTags) allSelectedTags.add(tag.getText().toString());

            // [중요] 각 카테고리별로 최소 1개씩 선택했는지 검사
            if (selectedMoveTypeTags.isEmpty() || selectedCompanionTags.isEmpty() || selectedMoodTags.isEmpty() || selectedBudgetTags.isEmpty()) {
                Toast.makeText(this, "각 카테고리별로 태그를 1개 이상 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 다음 화면(UploadCourseInfoActivity)으로 모든 정보를 전달합니다.
            Intent intent = new Intent(this, UploadCourseInfoActivity.class);
            intent.putExtra("courseId", courseId); // [중요] courseId를 계속 전달
            intent.putExtra("selectedTags", (Serializable) allSelectedTags); // 선택된 모든 태그 전달
            startActivity(intent);
        });
    }

    /**
     * '다음' 버튼의 활성화 상태를 업데이트하는 메서드
     */
    private void updateNextButtonState() {
        // 모든 카테고리에서 하나 이상씩 선택되었을 때만 버튼 활성화
        boolean isEnabled = !selectedMoveTypeTags.isEmpty()
                && !selectedCompanionTags.isEmpty()
                && !selectedMoodTags.isEmpty()
                && !selectedBudgetTags.isEmpty();
        btnNext.setEnabled(isEnabled);
    }
}
