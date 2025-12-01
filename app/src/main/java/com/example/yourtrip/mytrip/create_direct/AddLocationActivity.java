package com.example.yourtrip.mytrip.create_direct;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.text.Editable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.PlaceAddRequest;
import com.example.yourtrip.mytrip.model.PlaceAddResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.example.yourtrip.network.NaverSearchResponse;

import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView btnBack;
    private TextView tvTitle;
    private EditText etPlaceName;
    private MapView mapView;
    private NaverMap naverMap;
    private Button btnNext;
    private ImageView btnSearch;
    private boolean isMapReady = false;

    private ApiService apiService;
    private long courseId = -1L;
    private long dayId = -1L;

    // 검색 성공 시 저장될 값
    private LatLng selectedLocation = null;
    private String selectedAddress = null;
    private String selectedPlaceUrl = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_location);

        courseId = getIntent().getLongExtra("courseId", -1L);
        dayId = getIntent().getLongExtra("dayId", -1L);

        apiService = RetrofitClient.getAuthService(this);

        initViews();
        setTopBar();
        setTextWatcherForPlaceName();

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        // 모든 뷰가 그려진 후 map async
        View decorView = getWindow().getDecorView();
        ViewTreeObserver.OnGlobalLayoutListener layoutListener =
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        decorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Log.d("Naver2_getMapAsync", "뷰 그려짐 → map async 호출");
                        if (mapView != null) {
                            mapView.getMapAsync(AddLocationActivity.this);
                        }
                    }
                };

        decorView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        btnSearch.setOnClickListener(v -> doSearch());
        btnNext.setOnClickListener(v -> nextButtonAction());
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        this.isMapReady = true;

        LatLng defaultLocation = new LatLng(37.5665, 126.9780);
        naverMap.moveCamera(CameraUpdate.scrollTo(defaultLocation));

        mapView.invalidate();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        etPlaceName = findViewById(R.id.etPlaceName);
        btnNext = findViewById(R.id.btnNext);
        btnSearch = findViewById(R.id.btnSearch);
    }

    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }

    private void setTextWatcherForPlaceName() {
        etPlaceName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                btnNext.setEnabled(charSequence.length() > 0);
            }
        });
    }



    private void doSearch() {

        if (!isMapReady) {
            Toast.makeText(this, "지도를 불러오는 중입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = etPlaceName.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        PlaceSearchManager manager = new PlaceSearchManager(this);

        manager.searchPlaces(query, new PlaceSearchManager.PlaceSearchListener() {
            @Override
            public void onSuccess(List<NaverSearchResponse.Item> items) {

                Toast.makeText(AddLocationActivity.this, "장소 검색 성공!", Toast.LENGTH_SHORT).show();

                NaverSearchResponse.Item first = items.get(0);

                // 주소 저장
                selectedAddress = first.address;
                selectedPlaceUrl = first.link;

                // 좌표 저장
                double lat = convertMapY(first.mapy);
                double lng = convertMapX(first.mapx);

                selectedLocation = new LatLng(lat, lng);

                // 마커 표시
                addMarkerToMap(selectedLocation);

                naverMap.moveCamera(
                        CameraUpdate.scrollTo(selectedLocation)
                                .animate(CameraAnimation.Linear)
                );
            }

            @Override
            public void onEmpty() {
                Toast.makeText(AddLocationActivity.this, "검색 결과 없음", Toast.LENGTH_SHORT).show();
                showAddLocationDialog();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddLocationActivity.this, "검색 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addMarkerToMap(LatLng location) {
        if (!isMapReady) return;

        Marker marker = new Marker();
        marker.setPosition(location);
        marker.setMap(naverMap);
    }


    private void nextButtonAction() {

        String name = etPlaceName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "장소명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 검색 후 선택된 위치가 존재하는 경우 → 즉시 저장
        if (selectedLocation != null) {

            PlaceAddRequest request = new PlaceAddRequest(
                    name,
                    selectedLocation.latitude,
                    selectedLocation.longitude,
                    selectedPlaceUrl,
                    selectedAddress   // 검색할 때 저장된 주소
            );

            addPlaceApiCall(request);
            return;
        }

        // 검색 버튼을 누르지 않았을 경우 → 자동 검색
        PlaceSearchManager manager = new PlaceSearchManager(this);

        manager.searchPlaces(name, new PlaceSearchManager.PlaceSearchListener() {

            @Override
            public void onSuccess(List<NaverSearchResponse.Item> items) {

                NaverSearchResponse.Item first = items.get(0);

                selectedAddress = first.address;
                selectedPlaceUrl = first.link;

                double lat = convertMapY(first.mapy);
                double lng = convertMapX(first.mapx);

                selectedLocation = new LatLng(lat, lng);

                Toast.makeText(AddLocationActivity.this,
                        "장소 검색 후 자동 등록합니다.", Toast.LENGTH_SHORT).show();

                PlaceAddRequest request = new PlaceAddRequest(
                        name,
                        lat,
                        lng,
                        selectedPlaceUrl,
                        selectedAddress
                );

                addPlaceApiCall(request);
            }

            @Override
            public void onEmpty() {
                showAddLocationDialog();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddLocationActivity.this,
                        errorMessage, Toast.LENGTH_LONG).show();
            }
        });
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
                    placeName,
                    null,
                    null,
                    null,
                    null
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
                    public void onResponse(Call<PlaceAddResponse> call, Response<PlaceAddResponse> response) {

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
                    public void onFailure(Call<PlaceAddResponse> call, Throwable t) {
                        Toast.makeText(AddLocationActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // 좌표 변환
    private double convertMapX(String mapx) { return Double.parseDouble(mapx) / 10000000.0; }
    private double convertMapY(String mapy) { return Double.parseDouble(mapy) / 10000000.0; }

    // MapView lifecycle
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onResume() { super.onResume(); mapView.onResume(); mapView.invalidate(); }
    @Override protected void onPause() { mapView.onPause(); super.onPause(); }
    @Override protected void onStop() { mapView.onStop(); super.onStop(); }
    @Override protected void onDestroy() { mapView.onDestroy(); super.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
