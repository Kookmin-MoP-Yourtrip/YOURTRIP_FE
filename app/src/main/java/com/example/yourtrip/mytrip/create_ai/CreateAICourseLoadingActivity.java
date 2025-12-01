package com.example.yourtrip.mytrip.create_ai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.create_direct.CreateCourseDetailActivity;
import com.example.yourtrip.mytrip.model.AICourseCreateRequest;
import com.example.yourtrip.mytrip.model.AICourseCreateResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAICourseLoadingActivity extends AppCompatActivity {

    private static final String TAG = "CreateAICourseLoading";

    private String startDate, endDate, location; //이전 화면에서 받은 값
    private ArrayList<String> keywords;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ai_course_loading);   // XML 파일명

        ImageView ivLoadingCircle = findViewById(R.id.ivLoadingCircle);

        // GIF 로딩
        Glide.with(this)
                .asGif()
                .load(R.drawable.loading_circle2)   // drawable에 넣은 GIF 이름
                .override(600, 600)     // 강제 확대
                .into(ivLoadingCircle);

        apiService = RetrofitClient.getAuthService(this);
        receiveFromDateActivity();

        // API 요청 보내기
        submitAICourse(startDate, endDate, location, keywords);

    }

    private void receiveFromDateActivity() {
        Intent intent = getIntent();
        startDate = intent.getStringExtra("startDate");
        endDate   = intent.getStringExtra("endDate");
        location=intent.getStringExtra("location");
        keywords=intent.getStringArrayListExtra("keywords");

        if (startDate.isEmpty() || endDate.isEmpty()||location.isEmpty()) {
            Log.e(TAG, "날짜 및 여행지 정보가 전달되지 않았습니다. start=" + startDate + ", end=" + endDate+", location="+location);
            Toast.makeText(this, "여행 기간 및 장소를 입력하지 않았습니다. 압력 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void submitAICourse(String startDate, String endDate, String location, List<String> keywords){
        AICourseCreateRequest request = new AICourseCreateRequest(startDate, endDate, location, keywords);

        Call<AICourseCreateResponse> call = apiService.createAICourse(request);
        call.enqueue(new Callback<AICourseCreateResponse>() {
            @Override
            public void onResponse(Call<AICourseCreateResponse> call,
                                   Response<AICourseCreateResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    AICourseCreateResponse body = response.body();
                    Log.d(TAG, "AI 코스 생성 성공: 반환된 myCourseId = " + body.getMyCourseId());

                    Toast.makeText(CreateAICourseLoadingActivity.this,
                            "AI 코스 생성 완료!", Toast.LENGTH_SHORT).show();

                    // TODO: 다음 화면으로 이동 (예: 결과 화면 액티비티)
                     Intent intent = new Intent(CreateAICourseLoadingActivity.this,
                             CreateCourseDetailActivity.class);
                     intent.putExtra("courseId", body.getMyCourseId());
                     startActivity(intent);
                     finish();

                } else {
                    Log.e(TAG, "AI 코스 생성 실패 - code: " + response.code());
                    Toast.makeText(CreateAICourseLoadingActivity.this,
                            "AI 코스 생성에 실패했습니다. 잠시 후 다시 시도해 주세요.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AICourseCreateResponse> call, Throwable t) {
                Log.e(TAG, "AI 코스 생성 API 통신 오류", t);
                Toast.makeText(CreateAICourseLoadingActivity.this,
                        "서버 통신 중 오류가 발생했습니다.",
                        Toast.LENGTH_SHORT).show();
                // 필요하면 finish();
            }
        });
    }



}
