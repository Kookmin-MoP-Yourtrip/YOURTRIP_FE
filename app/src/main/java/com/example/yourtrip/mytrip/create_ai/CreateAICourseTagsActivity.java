package com.example.yourtrip.mytrip.create_ai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.TagConverter;
import com.example.yourtrip.mytrip.model.AICourseCreateRequest;
import com.example.yourtrip.network.ApiService;

import java.util.ArrayList;
import java.util.List;

public class CreateAICourseTagsActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView btnBack; //include된 상단바의 뒤로가기 버튼
    private Button btnNext;

    private static final String TAG = "CreateAICourseTags";

    private String startDate, endDate, location; //이전 화면에서 받은 값

    private final List<TextView> tagViews = new ArrayList<>();
    private final ArrayList<String> selectedTags = new ArrayList<>();

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ai_course_tags); //레이아웃을 현재 activity 화면으로 사용

        receiveFromDateActivity();
        initViews(); //findViewById로 xml의 뷰들을 멤버 변수에 연결

        setTopBar(); //상단바 타이틀과 뒤로가기 버튼 설정

        setupTagClickListeners();

        setNextButton();
    }

    /** 이전 (날짜) 화면에서 startDate, endDate, location 받기 */
    private void receiveFromDateActivity() {
        Intent intent = getIntent();
        startDate = intent.getStringExtra("startDate");
        endDate   = intent.getStringExtra("endDate");
        location=intent.getStringExtra("location");

        if (startDate.isEmpty() || endDate.isEmpty()||location.isEmpty()) {
            Log.e(TAG, "날짜 및 여행지 정보가 전달되지 않았습니다. start=" + startDate + ", end=" + endDate+", location="+location);
            Toast.makeText(this, "여행 기간 및 장소를 입력하지 않았습니다. 압력 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);

        // 🔹 태그 뷰 수집 (XML에 있는 태그 id 등록)
        tagViews.add(findViewById(R.id.tag_search_walking));
        tagViews.add(findViewById(R.id.tag_search_car));

        tagViews.add(findViewById(R.id.tag_search_solo));
        tagViews.add(findViewById(R.id.tag_search_couple));
        tagViews.add(findViewById(R.id.tag_search_friends));
        tagViews.add(findViewById(R.id.tag_search_family));

        tagViews.add(findViewById(R.id.tag_search_one_day));
        tagViews.add(findViewById(R.id.tag_search_one_night_two_days));
        tagViews.add(findViewById(R.id.tag_search_weekend));
        tagViews.add(findViewById(R.id.tag_search_long_term));

        tagViews.add(findViewById(R.id.tag_search_healing));
        tagViews.add(findViewById(R.id.tag_search_activity));
        tagViews.add(findViewById(R.id.tag_search_food_tour));
        tagViews.add(findViewById(R.id.tag_search_emotional));
        tagViews.add(findViewById(R.id.tag_search_culture_exhibition));
        tagViews.add(findViewById(R.id.tag_search_nature));
        tagViews.add(findViewById(R.id.tag_search_shopping));

        tagViews.add(findViewById(R.id.tag_search_budget));
        tagViews.add(findViewById(R.id.tag_search_normal));
        tagViews.add(findViewById(R.id.tag_search_premium));
    }

    private void setTopBar() {
        tvTitle.setText("AI 코스 만들기");
        btnBack.setOnClickListener(v -> finish());
    }

    // 🔹 태그 다중선택 로직
    private void setupTagClickListeners() {
        for (TextView tag : tagViews) {
            tag.setOnClickListener(v -> {

                boolean newState = !tag.isSelected();
                tag.setSelected(newState);

                // 데이터: 선택 리스트에 추가/제거
                String tagText = tag.getText().toString();

                if (newState) {
                    if (!selectedTags.contains(tagText))
                        selectedTags.add(tagText);
                } else {
                    selectedTags.remove(tagText);
                }

                boolean isValid = !selectedTags.isEmpty();
                btnNext.setEnabled(isValid); // ⭐ 태그 클릭 후 btnNext 활성화
            });
        }
    }

    private void setNextButton() {
        btnNext.setOnClickListener(v -> {
            List<String> sendTags = TagConverter.toServerCodes(selectedTags);
            Log.d(TAG, "AICourseCreate_API 요청 데이터: " + "startDate = "+ startDate+", endDate = "+endDate+", location = "+location +", sendTags = "+sendTags);

            // 다음 화면으로 넘기는 로직
            Intent intent = new Intent(CreateAICourseTagsActivity.this, CreateAICourseLoadingActivity.class);
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("location", location);
            intent.putExtra("keywords", new ArrayList<>(sendTags));

            startActivity(intent);
//            AICourseCreateRequest request = new AICourseCreateRequest(startDate, endDate, location, sendTags);
//            submitAICourse(request); //TODO: API 연동
        });
    }


}
