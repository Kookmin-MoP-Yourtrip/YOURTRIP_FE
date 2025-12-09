package com.example.yourtrip.mytrip.create_direct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.PlaceAddRequest;
import com.example.yourtrip.mytrip.model.PlaceAddResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.NaverSearchResponse;
import com.example.yourtrip.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private EditText etPlaceName;
    private Button btnNext;
    private ImageView btnSearch;
    private View layoutSearchResult;

    private ApiService apiService;
    private long courseId = -1L;
    private long dayId = -1L;
    
    private RecyclerView recyclerView;
    private PlaceSearchAdapter placeSearchAdapter;
    private TextView tvTotalCount;
    
    private NaverSearchResponse.Item selectedPlace = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_location);

        courseId = getIntent().getLongExtra("courseId", -1L);
        dayId = getIntent().getLongExtra("dayId", -1L);

        apiService = RetrofitClient.getAuthService(this);

        initViews();
        setTopBar();
        // 수정: 기존의 TextWatcher는 검색창이 비어있을 때만 '다음' 버튼을 비활성화하므로, 새로운 로직에서는 제거
        // setTextWatcherForPlaceName();
        initRecyclerView(); 

        btnSearch.setOnClickListener(v -> doSearch());
        btnNext.setOnClickListener(v -> nextButtonAction());
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        etPlaceName = findViewById(R.id.etPlaceName);
        btnNext = findViewById(R.id.btnNext);
        btnSearch = findViewById(R.id.btnSearch);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        recyclerView = findViewById(R.id.rvPlaces);
        layoutSearchResult = findViewById(R.id.layout_search_result);
    }

    //  RecyclerView와 어댑터를 초기화하고 클릭 리스너를 설정하는 새로운 메서드
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeSearchAdapter = new PlaceSearchAdapter();
        recyclerView.setAdapter(placeSearchAdapter);
        
        btnNext.setEnabled(false);

        // 어댑터에 아이템 클릭 리스너 설정
        placeSearchAdapter.setOnItemClickListener(item -> {
            selectedPlace = item;
            btnNext.setEnabled(true);
            Toast.makeText(this, "'" + Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY) + "' 선택됨", Toast.LENGTH_SHORT).show();
        });
    }

    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }

    // 기존 setTextWatcherForPlaceName()는 새 로직과 맞지 않아 주석 처리 
    /*
    private void setTextWatcherForPlaceName() {
        etPlaceName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 새 로직에서는 검색 결과 선택 시에만 버튼이 활성화되므로 이 코드는 더 이상 유효하지 않음
                btnNext.setEnabled(charSequence.length() > 0);
            }
        });
    }
    */


    // 수정: 검색 버튼 클릭 시 동작
    private void doSearch() {
        //검색 시작 시 키보드 숨기기
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        
        String query = etPlaceName.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        //검색 결과 layout 보여줌
        if (layoutSearchResult.getVisibility() == View.GONE) {
            layoutSearchResult.setVisibility(View.VISIBLE);
        }

        PlaceSearchManager manager = new PlaceSearchManager(this);
        manager.searchPlaces(query, new PlaceSearchManager.PlaceSearchListener() {
            @Override
            public void onSuccess(List<NaverSearchResponse.Item> items, int total) {
                tvTotalCount.setText("총 " + total + "개의 검색 결과");
                tvTotalCount.setVisibility(View.VISIBLE);

                placeSearchAdapter.setItems(items);

                recyclerView.setVisibility(View.VISIBLE);

                selectedPlace = null;
                btnNext.setEnabled(false);
            }

            @Override
            public void onEmpty() {
                Toast.makeText(AddLocationActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                // 검색 결과가 없으면 목록과 총 개수 텍스트를 숨김
                tvTotalCount.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                layoutSearchResult.setVisibility(View.GONE);
                showAddLocationDialog(); // 기존 로직 유지
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddLocationActivity.this, "검색 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 다음 버튼 클릭 이벤트
    private void nextButtonAction() {
        // 사용자가 목록에서 장소를 선택했는지 확인
        if (selectedPlace != null) {
            String name = Html.fromHtml(selectedPlace.title, Html.FROM_HTML_MODE_LEGACY).toString();

            // API 요청 객체 생성
            PlaceAddRequest request = new PlaceAddRequest(
                    name,
                    convertMapY(selectedPlace.mapy),
                    convertMapX(selectedPlace.mapx), // 좌표 변환
                    selectedPlace.link,
                    selectedPlace.address
            );
            // 서버에 장소 추가 API 호출
            addPlaceApiCall(request);

        } else {
            // 사용자가 아무것도 선택하지 않고 다음을 누른 경우에 대한 예외 처리
            Toast.makeText(this, "먼저 목록에서 장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialogStyle);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_location_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        dialogMessage.setText("등록되지 않은 주소예요.\n이대로 추가할까요?");

        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {

            String placeName = etPlaceName.getText().toString().trim();

            PlaceAddRequest request = new PlaceAddRequest(
                    placeName, null, null, null, null
            );

            addPlaceApiCall(request);
            dialog.dismiss();
        });

        Button btnSearchAgain = dialogView.findViewById(R.id.btnSearchAgain);
        btnSearchAgain.setOnClickListener(v -> {
            etPlaceName.setText("");
            dialog.dismiss();
        });

        dialog.show();
    }


    private void addPlaceApiCall(PlaceAddRequest request) {

        if (courseId == -1 || dayId == -1) {
            Toast.makeText(this, "코스 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.addPlaceToDay(courseId, dayId, request)
                .enqueue(new Callback<PlaceAddResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PlaceAddResponse> call, @NonNull Response<PlaceAddResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("newPlace", response.body());
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(AddLocationActivity.this, "추가 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PlaceAddResponse> call, @NonNull Throwable t) {
                        Toast.makeText(AddLocationActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 좌표 변환
    private double convertMapX(String mapx) {
        return Double.parseDouble(mapx) / 10000000.0;
    }

    private double convertMapY(String mapy) {
        return Double.parseDouble(mapy) / 10000000.0;
    }
}
