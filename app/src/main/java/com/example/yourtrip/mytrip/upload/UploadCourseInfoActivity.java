package com.example.yourtrip.mytrip.upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;

import java.io.Serializable;
import java.util.List;

public class UploadCourseInfoActivity extends AppCompatActivity {

    private static final String TAG = "UploadCourseInfo";

    // 이전 화면들에서 전달받을 데이터
    private long courseId;
    private List<String> selectedTags;

    // 현재 화면의 UI 뷰들
    private FrameLayout layoutImageContainer;
    private ImageView ivRepresentativeImage;
    private ImageView btnAddImage;
    private ImageView ivAddedPhoto;
    private EditText etTitle;
    private EditText etContent;
    private Button btnNext;

    // 사용자가 갤러리에서 선택한 이미지의 Uri
    private Uri selectedImageUri;

    // 갤러리 실행 및 결과 처리를 위한 Launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // 선택된 이미지를 ivAddedPhoto에 표시
                    Glide.with(this).load(selectedImageUri).centerCrop().into(ivAddedPhoto);
                    // 뷰의 보이기/숨기기 상태를 전환
                    ivAddedPhoto.setVisibility(View.VISIBLE);
                    btnAddImage.setVisibility(View.GONE);
                    checkFormValid();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course_info);

        // 이전 화면(UploadCourseTagsActivity)에서 데이터 받기
        receiveIntentData();

        // 화면 구성 헬퍼 메서드 호출
        initViews();
        setTopBar();
        setImagePicker();
        setInputWatchers();
        setNextButton();
        checkFormValid(); // 초기 버튼 상태 설정
    }

    /**
     * 이전 Activity로부터 courseId와 selectedTags를 전달받음
     */
    private void receiveIntentData() {
        courseId = getIntent().getLongExtra("courseId", -1L);
        selectedTags = (List<String>) getIntent().getSerializableExtra("selectedTags");

        // =============================================================
        // [추가] 전달받은 데이터 Logcat으로 확인
        // =============================================================
        Log.d(TAG, "--- 데이터 수신 ---");
        Log.d(TAG, "Course ID: " + courseId);
        Log.d(TAG, "Selected Tags: " + (selectedTags != null ? selectedTags.toString() : "null"));
        Log.d(TAG, "-----------------");
        // =============================================================

        // 데이터가 하나라도 없으면, 이전 화면으로 돌아갑니다.
        if (courseId == -1L || selectedTags == null) {
            Toast.makeText(this, "이전 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * XML 레이아웃의 뷰들을 멤버 변수에 연결
     */
    private void initViews() {
        layoutImageContainer = findViewById(R.id.layout_image_frame);
        ivAddedPhoto = findViewById(R.id.ivAddedPhoto);
        btnAddImage = findViewById(R.id.btn_add_image);
        etTitle = findViewById(R.id.et_upload_title);
        etContent = findViewById(R.id.et_upload_content);
        btnNext = findViewById(R.id.btnNext);
    }

    /**
     * 상단바의 제목과 뒤로가기 버튼을 설정
     */
    private void setTopBar() {
        View topBar = findViewById(R.id.topBar);
        TextView tvTitle = topBar.findViewById(R.id.tv_title);
        ImageView btnBack = topBar.findViewById(R.id.btnBack);
        tvTitle.setText("코스 업로드");
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * 대표 이미지 영역에 클릭 리스너를 설정하여 갤러리를 열기
     */
    private void setImagePicker() {
        // 이미지 영역 전체(FrameLayout)를 클릭했을 때 갤러리가 열리도록 설정
        layoutImageContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    /**
     * 제목과 내용 EditText에 TextWatcher를 설정하여 실시간으로 입력을 감지
     */
    private void setInputWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormValid(); // 텍스트가 변경될 때마다 '다음' 버튼의 활성화 상태를 체크
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        etTitle.addTextChangedListener(watcher);
        etContent.addTextChangedListener(watcher);
    }

    /**
     * '다음' 버튼의 활성화 여부를 결정하는 메서드
     */
    private void checkFormValid() {
        // 제목 채워져야 '다음' 버튼 활성화
        boolean isTitleValid = !etTitle.getText().toString().trim().isEmpty();
        btnNext.setEnabled(isTitleValid);
    }

    /**
     * '다음' 버튼 클릭 시, 지금까지 수집된 모든 데이터를 다음 화면으로 전달합니다.
     */
    private void setNextButton() {
        btnNext.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String content = etContent.getText().toString().trim();
            String finalContent = content.isEmpty() ? null : content;

            Log.d(TAG, "--- 데이터 전달 시작 ---");
            Log.d(TAG, "Course ID: " + courseId);
            Log.d(TAG, "Selected Tags: " + (selectedTags != null ? selectedTags.toString() : "null"));
            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Content: " + finalContent);
            Log.d(TAG, "Image URI: " + (selectedImageUri != null ? selectedImageUri.toString() : "null"));
            Log.d(TAG, "--------------------");

            // 다음 화면(UploadCourseConfirmActivity)으로 모든 정보를 전달
            Intent intent = new Intent(this, UploadCourseConfirmActivity.class);
            intent.putExtra("courseId", courseId);
            intent.putExtra("selectedTags", (Serializable) selectedTags);
            intent.putExtra("title", title);
            intent.putExtra("content", finalContent);
//            String content = etContent.getText().toString().trim();
//            if (content.isEmpty()) {
//                intent.putExtra("content", (String) null); // 명시적으로 null 전달
//            } else {
//                intent.putExtra("content", content);
//            }
            intent.putExtra("imageUri", selectedImageUri);

            Log.d(TAG, "다음 화면으로 전달할 데이터: courseId=" + courseId + ", title=" + etTitle.getText());
            startActivity(intent);
        });
    }
}
