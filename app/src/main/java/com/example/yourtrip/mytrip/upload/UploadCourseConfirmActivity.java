package com.example.yourtrip.mytrip.upload;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.create_direct.CreateCourseDayDetailFragment;
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.example.yourtrip.mytrip.model.UploadCourseRequest;
import com.example.yourtrip.mytrip.model.UploadCourseResponse;
import com.example.yourtrip.mytrip.util.TagMapper;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadCourseConfirmActivity extends AppCompatActivity {

    private static final String TAG = "UploadConfirmActivity";

    // 전달받을 모든 데이터
    private long courseId;
    private List<String> selectedTags;
    private String title;
    private String content;
    private Uri imageUri;
    private List<MyCourseDetailResponse.DaySchedule> daySchedules;

    // UI 뷰
    private TextView tvContent;
    private FlexboxLayout flexboxTags;
    private Button btnUpload;
    // 코스 상세 정보 UI 매핑을 위한 뷰 변수 선언
    private TextView tvCourseTitle;
    private TextView tvCourseDate;
    private TextView tvCourseLocation;

    private ApiService apiService;

    // styles.xml의 태그를 식별하기 위한 리스트
    private final List<String> moveTypeKeywords = Arrays.asList("뚜벅이", "자차");
    private final List<String> partnerKeywords = Arrays.asList("혼자", "연인", "친구", "가족");
    private final List<String> budgetKeywords = Arrays.asList("가성비", "평균예산", "프리미엄");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course_confirm);
        apiService = RetrofitClient.getAuthService(this);

        if (!receiveAllIntentData()) {
            return;
        }

        initViews();
        setTopBar();
        updateUI();
        setButtons();
        fetchCourseDetails();
    }

    private boolean receiveAllIntentData() {
        courseId = getIntent().getLongExtra("courseId", -1L);
        selectedTags = (List<String>) getIntent().getSerializableExtra("selectedTags");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        imageUri = getIntent().getParcelableExtra("imageUri");

        if (courseId == -1L || selectedTags == null || title == null) {
            String errorMessage = "업로드 정보를 받아오는데 실패했습니다. 다시 시도해주세요.";
            Log.e(TAG, errorMessage);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        return true;
    }

    private void initViews() {
        tvContent = findViewById(R.id.tv_upload_content);
        flexboxTags = findViewById(R.id.flexbox_upload_confirm_tags);
        btnUpload = findViewById(R.id.btn_upload);

        // 포함된 레이아웃(item_trip_card) 내부의 뷰들을 findViewById로 연결
        View courseCard = findViewById(R.id.item_trip_card);
        tvCourseTitle = courseCard.findViewById(R.id.tv_title);
        tvCourseDate = courseCard.findViewById(R.id.tv_date);
        tvCourseLocation = courseCard.findViewById(R.id.tv_location);
    }

    private void setTopBar() {
        View topBar = findViewById(R.id.top_bar);
        TextView tvTitle = topBar.findViewById(R.id.tv_title);
        ImageView btnBack = topBar.findViewById(R.id.btnBack);

        tvTitle.setText("코스 업로드");
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateUI() {
        if (content != null && !content.isEmpty()) {
            tvContent.setText(content);
            tvContent.setVisibility(View.VISIBLE);
        } else {
            tvContent.setVisibility(View.GONE);
        }

        flexboxTags.removeAllViews();

        for (String tagName : selectedTags) {
            int styleResId = getStyleForTag(tagName);
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, styleResId);

            TextView tagView = (TextView) LayoutInflater.from(wrapper)
                    .inflate(R.layout.view_upload_tag, flexboxTags, false);

            tagView.setText(tagName);
            flexboxTags.addView(tagView);
        }
    }

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

    private void fetchCourseDetails() {
        apiService.getMyCourseDetail(courseId).enqueue(new Callback<MyCourseDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyCourseDetailResponse> call, @NonNull Response<MyCourseDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API 응답을 courseDetails 변수에 저장
                    MyCourseDetailResponse courseDetails = response.body();
                    daySchedules = courseDetails.getDaySchedules();

                    // courseDetails 변수를 사용하여 UI 업데이트
                    tvCourseTitle.setText(courseDetails.getTitle());
                    tvCourseDate.setText(formatDateRange(courseDetails.getStartDate(), courseDetails.getEndDate()));
                    tvCourseLocation.setText(courseDetails.getLocation());

                    setupFragment();
                } else {
                    Toast.makeText(UploadCourseConfirmActivity.this, "코스 상세 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyCourseDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(UploadCourseConfirmActivity.this, "네트워크 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFragment() {
        CreateCourseDayDetailFragment fragment =
                CreateCourseDayDetailFragment.newInstance(courseId, daySchedules);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.trip_fragment_container, fragment)
                .commit();
    }

    private void setButtons() {
        btnUpload.setOnClickListener(v -> uploadCourseToServer());
    }

    private void uploadCourseToServer() {
        List<String> keywordCodes = TagMapper.convert(selectedTags);
        UploadCourseRequest requestDto =
                new UploadCourseRequest(courseId, title, content != null ? content : "", keywordCodes);

        String requestJson = new Gson().toJson(requestDto);
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJson);

        MultipartBody.Part imagePart = null;

        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                // API level 33 이상에서만 사용 가능한 readAllBytes()를 대체하는 로직
                byte[] fileBytes = readBytesFromInputStream(inputStream);
                inputStream.close();

                String fileName = getFileNameFromUri(imageUri);
                String mimeType = getContentResolver().getType(imageUri);
                if (mimeType == null) mimeType = "image/jpeg";

                RequestBody fileBody = RequestBody.create(MediaType.parse(mimeType), fileBytes);
                imagePart = MultipartBody.Part.createFormData("thumbnailImage", fileName, fileBody);

            } catch (Exception e) {
                Log.e(TAG, "이미지 처리 오류", e);
                Toast.makeText(this, "이미지 파일 처리 오류", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnUpload.setEnabled(false);

        Call<UploadCourseResponse> call =
                (imagePart != null)
                        ? apiService.uploadCourse(imagePart, requestBody)
                        : apiService.uploadCourseOnlyJson(requestBody);

        call.enqueue(new Callback<UploadCourseResponse>() {
            @Override
            public void onResponse(@NonNull Call<UploadCourseResponse> call, @NonNull Response<UploadCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UploadCourseConfirmActivity.this, "업로드 성공!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadCourseConfirmActivity.this, UploadCourseCompleteActivity.class);
                    intent.putExtra("uploadCourseId", response.body().getUploadCourseId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UploadCourseConfirmActivity.this, "업로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    btnUpload.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadCourseResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "[네트워크 오류]", t);
                Toast.makeText(UploadCourseConfirmActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnUpload.setEnabled(true);
            }
        });
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            // Cursor를 try-with-resources 구문으로 감싸서 자동으로 close되도록 함
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    //  getColumnIndex가 -1을 반환할 경우를 대비한 안전 코드
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    //InputStream을 byte[]로 변환하는 헬퍼 메서드 (readAllBytes() 대체)
    private byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
        // 수정: 잘못된 Firebase 경로 대신 표준 java.io.ByteArrayOutputStream 사용
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    //  날짜 형식을 보기 좋게 변환하는 헬퍼 메서드
    private String formatDateRange(String startDateStr, String endDateStr) {
        // API 응답이 null일 경우를 대비한 방어 코드
        if (startDateStr == null || endDateStr == null) {
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());

            Date startDate = inputFormat.parse(startDateStr);
            Date endDate = inputFormat.parse(endDateStr);

            if (startDate != null && endDate != null) {
                long diff = endDate.getTime() - startDate.getTime();
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                long nights = days;

                String formattedStartDate = outputFormat.format(startDate);
                String formattedEndDate = outputFormat.format(endDate);

                return String.format(Locale.getDefault(), "%s ~ %s (%d박 %d일)",
                        formattedStartDate, formattedEndDate, nights, days + 1);
            }
        } catch (ParseException e) {
            Log.e(TAG, "날짜 형식 변환 오류", e);
        }
        // 파싱 실패 시 원본 날짜 반환
        return startDateStr + " ~ " + endDateStr;
    }
}

//package com.example.yourtrip.mytrip.upload;
//
//import static androidx.core.content.ContentProviderCompat.requireContext;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.OpenableColumns;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.view.ContextThemeWrapper;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.create_direct.CreateCourseDayDetailFragment;
//import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
//import com.example.yourtrip.network.ApiService;
//import com.example.yourtrip.network.RetrofitClient;
//import com.example.yourtrip.mytrip.model.UploadCourseRequest;
//import com.example.yourtrip.mytrip.model.UploadCourseResponse;
//import com.example.yourtrip.mytrip.util.TagMapper;
//import com.google.android.flexbox.FlexboxLayout;
//import com.google.gson.Gson;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//// ⭐ 수정됨 — OkHttp 기반으로 변경
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class UploadCourseConfirmActivity extends AppCompatActivity {
//
//    private static final String TAG = "UploadConfirmActivity";
//
//    // 전달받을 모든 데이터
//    private long courseId;
//    private List<String> selectedTags;
//    private String title;
//    private String content;
//    private Uri imageUri;
//    private List<MyCourseDetailResponse.DaySchedule> daySchedules;
//
//    // UI 뷰
//    private TextView tvContent;
//    private FlexboxLayout flexboxTags;
//    private Button btnUpload;
//    private TextView tvCourseTitle;
//    private TextView tvCourseDate;
//    private TextView tvCourseLocation;
//
//    private ApiService apiService;
//
//    // styles.xml의 태그를 식별하기 위한 리스트
//    private final List<String> moveTypeKeywords = Arrays.asList("뚜벅이", "자차");
//    private final List<String> partnerKeywords = Arrays.asList("혼자", "연인", "친구", "가족");
//    private final List<String> budgetKeywords = Arrays.asList("가성비", "평균예산", "프리미엄");
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_upload_course_confirm);
//        apiService = RetrofitClient.getAuthService(this);
//
//        if (!receiveAllIntentData()) {
//            return;
//        }
//
//        initViews();
//        setTopBar();
//        updateUI();
//        setButtons();
//        fetchCourseDetails();
//    }
//
//    private boolean receiveAllIntentData() {
//        courseId = getIntent().getLongExtra("courseId", -1L);
//        selectedTags = (List<String>) getIntent().getSerializableExtra("selectedTags");
//        title = getIntent().getStringExtra("title");
//        content = getIntent().getStringExtra("content");
//        imageUri = getIntent().getParcelableExtra("imageUri");
//        // --- 데이터 확인 로직 ---
//        if (courseId == -1L || selectedTags == null || title == null) {
//            String errorMessage = "업로드 정보를 받아오는데 실패했습니다. 다시 시도해주세요.";
//            Log.e(TAG, errorMessage);
//            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
//            finish();
//            return false;
//        }
//
//        Log.d(TAG, "--- 데이터 수신 성공 ---");
//        Log.d(TAG, "Course ID: " + courseId);
//        Log.d(TAG, "Selected Tags: " + selectedTags.toString());
//        Log.d(TAG, "Title: " + title);
//        Log.d(TAG, "Content: " + (content != null ? content : "null"));
//        Log.d(TAG, "Image URI: " + (imageUri != null ? imageUri : "null"));
//        Log.d(TAG, "-------------------");
//
//        Toast.makeText(this, "최종 확인 단계입니다!", Toast.LENGTH_SHORT).show();
//        return true;
//    }
//
//    private void initViews() {
//        tvContent = findViewById(R.id.tv_upload_content);
//        flexboxTags = findViewById(R.id.flexbox_upload_confirm_tags);
//        btnUpload = findViewById(R.id.btn_upload);
//        View courseCard = findViewById(R.id.item_trip_card);
//        tvCourseTitle = courseCard.findViewById(R.id.tv_title);
//        tvCourseDate = courseCard.findViewById(R.id.tv_date);
//        tvCourseLocation = courseCard.findViewById(R.id.tv_location);
//    }
//
//    private void setTopBar() {
//        View topBar = findViewById(R.id.top_bar);
//        TextView tvTitle = topBar.findViewById(R.id.tv_title);
//        ImageView btnBack = topBar.findViewById(R.id.btnBack);
//
//        tvTitle.setText("코스 업로드");
//        btnBack.setOnClickListener(v -> finish());
//    }
//
//    private void updateUI() {
//        if (content != null && !content.isEmpty()) {
//            tvContent.setText(content);
//            tvContent.setVisibility(View.VISIBLE);
//        } else {
//            tvContent.setVisibility(View.GONE);
//        }
//
//        flexboxTags.removeAllViews();
//
//        for (String tagName : selectedTags) { // 태그 이름에 따라 적용할 스타일 ID를 결정
//            int styleResId = getStyleForTag(tagName);
//            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, styleResId);
//
//            TextView tagView = (TextView) LayoutInflater.from(wrapper)
//                    .inflate(R.layout.view_upload_tag, flexboxTags, false);
//
//            tagView.setText(tagName);
//            flexboxTags.addView(tagView);
//        }
//    }
//
//    private int getStyleForTag(String tagName) {
//        if (moveTypeKeywords.contains(tagName)) {
//            return R.style.Tag_Movetype;
//        }else if (partnerKeywords.contains(tagName)) {
//            return R.style.Tag_Partner;
//        }else if (budgetKeywords.contains(tagName)) {
//            return R.style.Tag_Budget;
//        } else {
//            // 그 외에는 모두 '여행 분위기' 태그로 간주
//            return R.style.Tag_Theme;
//        }
//    }
//
//    private void fetchCourseDetails() {
//        apiService.getMyCourseDetail(courseId).enqueue(new Callback<MyCourseDetailResponse>() {
//            @Override
//            public void onResponse(Call<MyCourseDetailResponse> call, Response<MyCourseDetailResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    daySchedules = response.body().getDaySchedules();
//
//                    tvCourseTitle.setText(courseDetails.getTitle());
//                    tvCourseDate.setText(formatDateRange(courseDetails.getStartDate(), courseDetails.getEndDate()));
//                    tvCourseLocation.setText(courseDetails.getRegion());
//
//                    setupFragment();
//                } else {
//                    Toast.makeText(UploadCourseConfirmActivity.this, "코스 상세 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MyCourseDetailResponse> call, Throwable t) {
//                Toast.makeText(UploadCourseConfirmActivity.this, "네트워크 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void setupFragment() {
//        CreateCourseDayDetailFragment fragment =
//                CreateCourseDayDetailFragment.newInstance(courseId, daySchedules);
//
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.trip_fragment_container, fragment)
//                .commit();
//    }
//
//    private void setButtons() {
//        btnUpload.setOnClickListener(v -> uploadCourseToServer());
//    }
//
//    // 업로드 API 구성
//    private void uploadCourseToServer() {
//        Log.d(TAG, "=== [UPLOAD 시작] uploadCourseToServer() ===");
//
//        // TagMapper 활용해서 키워드 매핑
//        List<String> keywordCodes = TagMapper.convert(selectedTags);
//        Log.d(TAG, "[KeywordCodes 변환 결과] = " + keywordCodes);
//
//
////        UploadCourseRequest requestDto =
////                new UploadCourseRequest(courseId, title, content, keywordCodes);
//        UploadCourseRequest requestDto =
//                new UploadCourseRequest(courseId, title, content != null ? content : "", keywordCodes);
//
//        Log.d(TAG, "=== [DTO 생성 완료] UploadCourseRequest ===");
//        Log.d(TAG, "myCourseId: " + requestDto.getMyCourseId());
//        Log.d(TAG, "title: " + requestDto.getTitle());
//        Log.d(TAG, "introduction: " + requestDto.getIntroduction());
//        Log.d(TAG, "keywords: " + requestDto.getKeywords());
//        Log.d(TAG, "=========================================");
//
//        String requestJson = new Gson().toJson(requestDto);
//        Log.d(TAG, "=== [Serialized JSON] ===\n" + requestJson);
//
//        // JSON RequestBody 생성
//        RequestBody requestBody =
//                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJson);
//
//        MultipartBody.Part imagePart = null; //이미지 기본 null
//
//        // 이미지 있을 때만 multipart part 생성
//        if (imageUri != null) {
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                byte[] fileBytes = readBytesFromInputStream(inputStream);
//                inputStream.close();
//
//                String fileName = getFileNameFromUri(imageUri);
//
//                String mimeType = getContentResolver().getType(imageUri);
//                if (mimeType == null) mimeType = "image/jpeg";
//
//                RequestBody fileBody = RequestBody.create(MediaType.parse(mimeType), fileBytes);
//
//                imagePart = MultipartBody.Part.createFormData(
//                        "thumbnailImage",
//                        fileName,
//                        fileBody
//                );
//
//                Log.d(TAG, "이미지 포함: " + fileName);
//
//            } catch (Exception e) {
//                Log.e(TAG, "이미지 처리 오류", e);
//                Toast.makeText(this, "이미지 파일 처리 오류", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        } else {
//            Log.d(TAG, "[이미지 없음] → thumbnailImage part 제거됨 (전송 안 함)");
//        }
//
//        btnUpload.setEnabled(false);
//
//
//        // API 호출 — imagePart가 null이면 JSON만 보내도록 오버로드한 API 호출 사용
//        Call<UploadCourseResponse> call =
//                (imagePart != null)
//                        ? apiService.uploadCourse(imagePart, requestBody)
//                        : apiService.uploadCourseOnlyJson(requestBody);
//
//        Log.d(TAG, "=== [API 호출 직전 최종 요약] ===");
//        Log.d(TAG, "이미지 포함 여부: " + (imagePart != null));
//        Log.d(TAG, "JSON body: " + requestJson);
//        Log.d(TAG, "================================");
//
//        call.enqueue(new Callback<UploadCourseResponse>() {
//
//            @Override
//            public void onResponse(Call<UploadCourseResponse> call,
//                                   Response<UploadCourseResponse> response) {
//
//                Log.d(TAG, "=== [서버 응답 수신] ===");
//                Log.d(TAG, "Status Code: " + response.code());
//
//                if (!response.isSuccessful()) {
//                    //  response.errorBody() 로그 추가
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Log.e(TAG, "[Error Body]\n" + errorBody);
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error body 파싱 실패", e);
//                    }
//                }
//
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.d(TAG, "[업로드 성공] 응답 body: " + response.body().toString());
//                    Toast.makeText(UploadCourseConfirmActivity.this,
//                            "업로드 성공!", Toast.LENGTH_SHORT).show();
//
//                    UploadCourseResponse resp = response.body();
//                    Log.d(TAG, "[업로드 성공] UploadCourseResponse resp: " + resp.getUploadCourseId());
//
//                    Intent intent = new Intent(UploadCourseConfirmActivity.this,
//                            UploadCourseCompleteActivity.class);
//
//                    intent.putExtra("uploadCourseId", resp.getUploadCourseId());
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    Toast.makeText(UploadCourseConfirmActivity.this,
//                            "업로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
//                    btnUpload.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UploadCourseResponse> call, Throwable t) {
//                Log.e(TAG, "[네트워크 오류]", t);
//                Toast.makeText(UploadCourseConfirmActivity.this,
//                        "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                btnUpload.setEnabled(true);
//            }
//        });
//    }
//
//    // URI에서 파일 이름 추출
//    pprivate String getFileNameFromUri(Uri uri) {
//        String result = null;
//        if ("content".equals(uri.getScheme())) {
//            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
//                if (cursor != null && cursor.moveToFirst()) {
//                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    if (nameIndex != -1) { // 인덱스가 유효한지 확인
//                        result = cursor.getString(nameIndex);
//                    }
//                }
//            }
//        }
//        if (result == null) {
//            result = uri.getLastPathSegment();
//        }
//        return result;
//    }
//    // InputStream을 byte[]로 변환하는 헬퍼 메서드 (readAllBytes() 대체)
//    private byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
//        ByteArrayOutputStream byteBuffer = new com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream();
//        int bufferSize = 1024;
//        byte[] buffer = new byte[bufferSize];
//
//        int len;
//        while ((len = inputStream.read(buffer)) != -1) {
//            byteBuffer.write(buffer, 0, len);
//        }
//        return byteBuffer.toByteArray();
//    }
//
//    //  날짜 형식을 보기 좋게 변환하는 헬퍼 메서드
//    private String formatDateRange(String startDateStr, String endDateStr) {
//        try {
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
//
//            Date startDate = inputFormat.parse(startDateStr);
//            Date endDate = inputFormat.parse(endDateStr);
//
//            if (startDate != null && endDate != null) {
//                long diff = endDate.getTime() - startDate.getTime();
//                long days = TimeUnit.MILLISECONDS.toDays(diff);
//                long nights = days; // 1박 2일 -> 1일 차이 -> 1박
//
//                String formattedStartDate = outputFormat.format(startDate);
//                String formattedEndDate = outputFormat.format(endDate);
//
//                return String.format(Locale.getDefault(), "%s ~ %s (%d박 %d일)",
//                        formattedStartDate, formattedEndDate, nights, days + 1);
//            }
//        } catch (ParseException e) {
//            Log.e(TAG, "날짜 형식 변환 오류", e);
//        }
//        // 파싱 실패 시 원본 날짜 반환
//        return startDateStr + " ~ " + endDateStr;
//    }
//
//
////    private List<String> convertTagsToKeywords(List<String> tags) {
////        List<String> keywordCodes = new ArrayList<>();
////        for (String tag : tags) {
////            switch (tag) {
////                case "뚜벅이": keywordCodes.add("WALK"); break;
////                case "맛집탐방": keywordCodes.add("FOOD"); break;
////                case "힐링": keywordCodes.add("HEALING"); break;
////                default:
////                    Log.w(TAG, "매핑되지 않은 태그: " + tag);
////            }
////        }
////        return keywordCodes;
////    }
//}
//
