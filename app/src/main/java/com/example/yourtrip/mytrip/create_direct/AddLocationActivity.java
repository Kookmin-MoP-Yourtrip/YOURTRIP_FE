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

    private ApiService apiService;
    private long courseId = -1L;
    private long dayId = -1L;

    // 수정: RecyclerView와 Adapter, 총 개수 TextView 변수 선언
    private RecyclerView recyclerView;
    private PlaceSearchAdapter placeSearchAdapter;
    private TextView tvTotalCount;

    // 수정: 사용자가 선택한 장소를 저장할 변수
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
        initRecyclerView(); // 수정: RecyclerView 초기화 메서드 호출

        btnSearch.setOnClickListener(v -> doSearch());
        btnNext.setOnClickListener(v -> nextButtonAction());
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        etPlaceName = findViewById(R.id.etPlaceName);
        btnNext = findViewById(R.id.btnNext);
        btnSearch = findViewById(R.id.btnSearch);
        // 수정: RecyclerView와 총 개수 TextView ID 연결
        tvTotalCount = findViewById(R.id.tvTotalCount);
        recyclerView = findViewById(R.id.rvPlaces);
    }

    // 수정: RecyclerView와 어댑터를 초기화하고 클릭 리스너를 설정하는 새로운 메서드
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeSearchAdapter = new PlaceSearchAdapter();
        recyclerView.setAdapter(placeSearchAdapter);

        // '다음' 버튼은 처음에는 비활성화 상태로 시작
        btnNext.setEnabled(false);

        // 어댑터에 아이템 클릭 리스너 설정
        placeSearchAdapter.setOnItemClickListener(item -> {
            // 선택한 장소 정보를 멤버 변수에 저장
            selectedPlace = item;
            // '다음' 버튼 활성화
            btnNext.setEnabled(true);
            // 사용자에게 어떤 아이템이 선택되었는지 토스트 메시지로 알려줌
            Toast.makeText(this, "'" + Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY) + "' 선택됨", Toast.LENGTH_SHORT).show();
        });
    }

    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }

    // 기존 setTextWatcherForPlaceName()는 새 로직과 맞지 않아 주석 처리 또는 삭제
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


    // 수정: 검색 버튼 클릭 시 동작을 새롭게 정의
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

        PlaceSearchManager manager = new PlaceSearchManager(this);
        // 중요: PlaceSearchManager의 searchPlaces 메서드가 전체 응답(total 포함)을 전달하도록 수정되었다고 가정
        // 만약 searchPlaces가 List<Item>만 반환한다면, total 개수를 표시할 수 없음
        manager.searchPlaces(query, new PlaceSearchManager.PlaceSearchListener() {
            @Override
            public void onSuccess(List<NaverSearchResponse.Item> items, int total) { // PlaceSearchListener 인터페이스에 total 파라미터 추가 필요
                // 1. 총 결과 개수 텍스트 업데이트 및 표시
                tvTotalCount.setText("총 " + total + "개의 검색 결과");
                tvTotalCount.setVisibility(View.VISIBLE);

                // 2. 어댑터에 검색 결과 리스트 전달
                placeSearchAdapter.setItems(items);

                // 3. RecyclerView 표시
                recyclerView.setVisibility(View.VISIBLE);

                // 4. 새 검색이 시작되었으므로 이전에 선택한 장소 정보와 '다음' 버튼 상태 초기화
                selectedPlace = null;
                btnNext.setEnabled(false);
            }

            @Override
            public void onEmpty() {
                Toast.makeText(AddLocationActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                // 검색 결과가 없으면 목록과 총 개수 텍스트를 숨김
                tvTotalCount.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                showAddLocationDialog(); // 기존 로직 유지
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddLocationActivity.this, "검색 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 수정: '다음' 버튼 클릭 시 동작을 새롭게 정의
    private void nextButtonAction() {
        // 사용자가 목록에서 장소를 선택했는지 확인
        if (selectedPlace != null) {
            // HTML 태그 제거하여 순수 텍스트 이름 가져오기
            String name = Html.fromHtml(selectedPlace.title, Html.FROM_HTML_MODE_LEGACY).toString();

            // API 요청 객체 생성
            PlaceAddRequest request = new PlaceAddRequest(
                    name,
                    convertMapY(selectedPlace.mapy), // 좌표 변환
                    convertMapX(selectedPlace.mapx), // 좌표 변환
                    selectedPlace.link,
                    selectedPlace.address
            );
            // 서버에 장소 추가 API 호출
            addPlaceApiCall(request);

        } else {
            // 이 경우는 거의 없지만, 사용자가 아무것도 선택하지 않고 다음을 누른 경우에 대한 예외 처리
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

//package com.example.yourtrip.mytrip.create_direct;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.widget.EditText;
//import android.text.Editable;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Button;
//import android.widget.Toast;
//import android.view.View;
//import android.app.AlertDialog;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.model.PlaceAddRequest;
//import com.example.yourtrip.mytrip.model.PlaceAddResponse;
//import com.example.yourtrip.network.ApiService;
//import com.example.yourtrip.network.RetrofitClient;
//import com.example.yourtrip.network.NaverSearchResponse;
//
////import com.naver.maps.map.MapFragment;
////import com.naver.maps.map.NaverMap;
////import com.naver.maps.map.OnMapReadyCallback;
////import com.naver.maps.geometry.LatLng;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class AddLocationActivity extends AppCompatActivity  {
//
//    private ImageView btnBack;
//    private TextView tvTitle;
//    private EditText etPlaceName;
//    private Button btnNext;
//    private ImageView btnSearch;
//
//    private ApiService apiService;
//    private long courseId = -1L;
//    private long dayId = -1L;
//
//    // 검색 성공 시 저장될 값
////    private LatLng selectedLocation = null;
//    private String selectedAddress = null;
//    private String selectedPlaceUrl = null;
//    private RecyclerView recyclerView;
//    private PlaceSearchAdapter placeSearchAdapter;
//    private TextView tvTotalCount;
//
//    // 사용자가 선택한 장소를 저장할 변수
//    private NaverSearchResponse.Item selectedPlace = null;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trip_add_location);
//
//        courseId = getIntent().getLongExtra("courseId", -1L);
//        dayId = getIntent().getLongExtra("dayId", -1L);
//
//        apiService = RetrofitClient.getAuthService(this);
//
//        initViews();
//        setTopBar();
//        setTextWatcherForPlaceName();
//
////        FragmentManager fm = getSupportFragmentManager();
////        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.mapFragment);
////        if (mapFragment == null) {
////            mapFragment = MapFragment.newInstance();
////            fm.beginTransaction().add(R.id.mapFragment, mapFragment).commit();
////        }
////        mapFragment.getMapAsync(this);
//
//        btnSearch.setOnClickListener(v -> doSearch());
//        btnNext.setOnClickListener(v -> nextButtonAction());
//    }
//
////    @Override
////    public void onMapReady(@NonNull NaverMap naverMap) {
////        Toast.makeText(this, "지도 준비 완료!", Toast.LENGTH_SHORT).show();
////    }
//
//    //뷰 초기화
//    private void initViews() {
//        tvTitle = findViewById(R.id.tv_title);
//        btnBack = findViewById(R.id.btnBack);
//        etPlaceName = findViewById(R.id.etPlaceName);
//        btnNext = findViewById(R.id.btnNext);
//        btnSearch = findViewById(R.id.btnSearch);
//        tvTotalCount = findViewById(R.id.tvTotalCount);
//        recyclerView = findViewById(R.id.rvPlaces);
//    }
//
//    private void setTopBar() {
//        tvTitle.setText("장소 추가하기");
//        btnBack.setOnClickListener(v -> finish());
//    }
//
//    private void setTextWatcherForPlaceName() {
//        etPlaceName.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void afterTextChanged(Editable s) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                btnNext.setEnabled(charSequence.length() > 0);
//            }
//        });
//    }
//
//
//
//    private void doSearch() {
//
//
//        String query = etPlaceName.getText().toString().trim();
//
//        if (query.isEmpty()) {
//            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        PlaceSearchManager manager = new PlaceSearchManager(this);
//
//        manager.searchPlaces(query, new PlaceSearchManager.PlaceSearchListener() {
//            @Override
//            public void onSuccess(List<NaverSearchResponse.Item> items) {
//
//                Toast.makeText(AddLocationActivity.this, "장소 검색 성공!", Toast.LENGTH_SHORT).show();
//
//                NaverSearchResponse.Item first = items.get(0);
//
//                // 주소 저장
//                selectedAddress = first.address;
//                selectedPlaceUrl = first.link;
//
//                // 좌표 저장
//                double lat = convertMapY(first.mapy);
//                double lng = convertMapX(first.mapx);
//
////                selectedLocation = new LatLng(lat, lng);
//
//            }
//
//            @Override
//            public void onEmpty() {
//                Toast.makeText(AddLocationActivity.this, "검색 결과 없음", Toast.LENGTH_SHORT).show();
//                showAddLocationDialog();
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Toast.makeText(AddLocationActivity.this, "검색 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private void nextButtonAction() {
//
//        String name = etPlaceName.getText().toString().trim();
//
//        if (name.isEmpty()) {
//            Toast.makeText(this, "장소명을 입력해주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // 검색 후 선택된 위치가 존재하는 경우 → 즉시 저장
////        if (selectedLocation != null) {
////
////            PlaceAddRequest request = new PlaceAddRequest(
////                    name,
////                    selectedLocation.latitude,
////                    selectedLocation.longitude,
////                    selectedPlaceUrl,
////                    selectedAddress   // 검색할 때 저장된 주소
////            );
////
////            addPlaceApiCall(request);
////            return;
////        }
//
//        // 검색 버튼을 누르지 않았을 경우 → 자동 검색
//        PlaceSearchManager manager = new PlaceSearchManager(this);
//
//        manager.searchPlaces(name, new PlaceSearchManager.PlaceSearchListener() {
//
//            @Override
//            public void onSuccess(List<NaverSearchResponse.Item> items) {
//
//                NaverSearchResponse.Item first = items.get(0);
//
//                selectedAddress = first.address;
//                selectedPlaceUrl = first.link;
//
//                double lat = convertMapY(first.mapy);
//                double lng = convertMapX(first.mapx);
//
////                selectedLocation = new LatLng(lat, lng);
//
//                Toast.makeText(AddLocationActivity.this,
//                        "장소 검색 후 자동 등록합니다.", Toast.LENGTH_SHORT).show();
//
//                PlaceAddRequest request = new PlaceAddRequest(
//                        name,
//                        lat,
//                        lng,
//                        selectedPlaceUrl,
//                        selectedAddress
//                );
//
//                addPlaceApiCall(request);
//            }
//
//            @Override
//            public void onEmpty() {
//                showAddLocationDialog();
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Toast.makeText(AddLocationActivity.this,
//                        errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//
//
//    private void showAddLocationDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialogStyle);
//        View dialogView = getLayoutInflater().inflate(R.layout.popup_location_dialog, null);
//        builder.setView(dialogView);
//
//        AlertDialog dialog = builder.create();
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//
//        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
//        dialogMessage.setText("등록되지 않은 주소예요.\n이대로 추가할까요?");
//
//        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
//        btnAdd.setOnClickListener(v -> {
//
//            String placeName = etPlaceName.getText().toString().trim();
//
//            PlaceAddRequest request = new PlaceAddRequest(
//                    placeName,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            addPlaceApiCall(request);
//            dialog.dismiss();
//        });
//
//        Button btnSearchAgain = dialogView.findViewById(R.id.btnSearchAgain);
//        btnSearchAgain.setOnClickListener(v -> {
//            etPlaceName.setText("");
//            dialog.dismiss();
//        });
//
//        dialog.show();
//    }
//
//
//    private void addPlaceApiCall(PlaceAddRequest request) {
//
//        if (courseId == -1 || dayId == -1) {
//            Toast.makeText(this, "코스 정보가 없습니다.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        apiService.addPlaceToDay(courseId, dayId, request)
//                .enqueue(new Callback<PlaceAddResponse>() {
//                    @Override
//                    public void onResponse(Call<PlaceAddResponse> call, Response<PlaceAddResponse> response) {
//
//                        if (response.isSuccessful() && response.body() != null) {
//                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra("newPlace", response.body());
//                            setResult(Activity.RESULT_OK, resultIntent);
//                            finish();
//                        } else {
//                            Toast.makeText(AddLocationActivity.this, "추가 실패", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<PlaceAddResponse> call, Throwable t) {
//                        Toast.makeText(AddLocationActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//
//    // 좌표 변환
//    private double convertMapX(String mapx) { return Double.parseDouble(mapx) / 10000000.0; }
//    private double convertMapY(String mapy) { return Double.parseDouble(mapy) / 10000000.0; }
//
//}
