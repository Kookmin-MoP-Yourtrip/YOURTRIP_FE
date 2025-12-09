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
import android.view.inputmethod.EditorInfo;
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
    private TextView tvEmptyResult;
    
    private NaverSearchResponse.Item selectedPlace = null;
    private boolean isSearchResultEmpty = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_location);

        courseId = getIntent().getLongExtra("courseId", -1L);
        dayId = getIntent().getLongExtra("dayId", -1L);

        apiService = RetrofitClient.getAuthService(this);

        initViews();
        setTopBar();
        setTextWatcherForSearch();
        initRecyclerView(); 

        btnSearch.setOnClickListener(v -> doSearch());
        btnNext.setOnClickListener(v -> nextButtonAction());

        // 엔터키 리스너 설정
        etPlaceName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (btnSearch.isEnabled()) { // 검색 버튼이 활성화 상태일 때만 실행
                    doSearch();
                }
                return true; // 이벤트 소비
            }
            return false;
        });
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
        tvEmptyResult = findViewById(R.id.tvEmptyResult); // tvEmptyResult 초기화
        
        btnSearch.setEnabled(false);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeSearchAdapter = new PlaceSearchAdapter();
        recyclerView.setAdapter(placeSearchAdapter);
        
        btnNext.setEnabled(false);

        placeSearchAdapter.setOnItemClickListener(item -> {
            selectedPlace = item;
            isSearchResultEmpty = false; 
            btnNext.setEnabled(true);
        });
    }
    
    private void setTextWatcherForSearch() {
        etPlaceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSearch.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }

    private void doSearch() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        
        String query = etPlaceName.getText().toString().trim();
        if (query.isEmpty()) {
            return;
        }
        
        isSearchResultEmpty = false;
        selectedPlace = null;
        
        layoutSearchResult.setVisibility(View.VISIBLE);

        PlaceSearchManager manager = new PlaceSearchManager(this);
        manager.searchPlaces(query, new PlaceSearchManager.PlaceSearchListener() {
            @Override
            public void onSuccess(List<NaverSearchResponse.Item> items, int total) {
                isSearchResultEmpty = false;
                tvTotalCount.setText("총 " + total + "개의 검색 결과");
                tvTotalCount.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyResult.setVisibility(View.GONE);
                
                placeSearchAdapter.setItems(items);
                btnNext.setEnabled(false); 
            }

            @Override
            public void onEmpty() {
                isSearchResultEmpty = true;
                tvTotalCount.setText("총 0개의 검색 결과");
                tvTotalCount.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                tvEmptyResult.setVisibility(View.VISIBLE);
                
                btnNext.setEnabled(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                isSearchResultEmpty = false;
                layoutSearchResult.setVisibility(View.GONE);
                Toast.makeText(AddLocationActivity.this, "검색 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nextButtonAction() {
        if (selectedPlace != null) {
            String name = Html.fromHtml(selectedPlace.title, Html.FROM_HTML_MODE_LEGACY).toString();
            PlaceAddRequest request = new PlaceAddRequest(
                    name,
                    convertMapY(selectedPlace.mapy),
                    convertMapX(selectedPlace.mapx),
                    selectedPlace.link,
                    selectedPlace.address
            );
            addPlaceApiCall(request);
        } else if (isSearchResultEmpty) {
            showAddLocationDialog();
        } else {
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

    private double convertMapX(String mapx) {
        try {
            return Double.parseDouble(mapx) / 10000000.0;
        } catch (NumberFormatException e) {
            return 0.0; 
        }
    }

    private double convertMapY(String mapy) {
        try {
            return Double.parseDouble(mapy) / 10000000.0;
        } catch (NumberFormatException e) {
            return 0.0; 
        }
    }
}
