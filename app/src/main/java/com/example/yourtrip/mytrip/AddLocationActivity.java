package com.example.yourtrip.mytrip;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
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
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.overlay.Marker;

public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView btnBack;
    private TextView tvTitle;
    private EditText etPlaceName;
    private MapView mapView;
    private NaverMap naverMap;
    private Button btnNext;
    private ImageView btnSearch;
    private boolean isMapReady = false; // 지도가 준비되었는지 확인하는 플래그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_location);

        initViews();
        setTopBar();
        setTextWatcherForPlaceName();

        // MapView 초기화
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState); //mapView의 생명주기를 액티비티에 연결

        // 로그로 SDK 초기화가 정상적으로 되었는지 확인
        Log.d("Naver1_NAVER_SDK_TEST", String.valueOf(NaverMapSdk.getInstance(this).getClient()));
        // getMapAsync는 여기서 한 번만 호출
//        mapView.getMapAsync(this);
//        Log.d("Naver2_getMapAsync", "onCreate: getMapAsync() 호출 시작.");

        // ★★★ 최종 병기: 모든 뷰가 다 그려진 후에 getMapAsync를 호출 ★★★
        // 액티비티의 최상위 뷰(decorView)에 리스너를 붙여서,
        // 레이아웃 그리기가 완전히 끝나는 시점을 포착합니다.
        View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 리스너가 여러 번 호출되는 것을 막기 위해, 한 번 실행된 후에는 바로 제거합니다.
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // 이제 모든 뷰의 크기와 위치 계산이 끝났음이 보장됩니다.
                // 이 시점에서 getMapAsync를 호출합니다.
                if (mapView != null) {
                    Log.d("Naver2_getMapAsync", "GlobalLayoutListener: 모든 뷰가 그려진 후 getMapAsync 호출!");
                    mapView.getMapAsync(AddLocationActivity.this);
                }
            }
        });

        Log.d("NaverMap", "onCreate: GlobalLayoutListener 등록 완료.");

        // 검색 버튼 클릭 이벤트 처리
        btnSearch.setOnClickListener(v -> searchPlace());
        // btnNext 클릭 이벤트 처리
        btnNext.setOnClickListener(v -> nextButtonAction());
    }

//    @Override
//    public void onMapReady(@NonNull NaverMap naverMap) {
//        this.naverMap = naverMap;
//        this.isMapReady = true; // 맵이 준비되었음을 표시
//        Log.d("Naver3_onMapReady", "onMapReady 호출 완료. 지도가 준비되었습니다.");
//
//        // 줌 컨트롤 설정
//        naverMap.getUiSettings().setZoomControlEnabled(true);
//
//        // 지도 기본 위치 설정 (예: 서울 시청)
//        LatLng defaultLocation = new LatLng(37.5665, 126.9780);
//        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(defaultLocation);
//        naverMap.moveCamera(cameraUpdate);
//
//        // 뷰를 강제로 다시 그리도록 요청
//        mapView.invalidate();
//    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // onMapReady가 호출되었다는 것은 지도 객체가 성공적으로 생성되었다는 의미
        this.naverMap = naverMap;
        this.isMapReady = true; 
        Log.d("NaverMap3_onMapReady", "onMapReady: 지도가 준비되었습니다.");

        // 지도 UI 초기 설정
        naverMap.getUiSettings().setZoomControlEnabled(true);
        // 기본 위치로 카메라 이동
        LatLng defaultLocation = new LatLng(37.5665, 126.9780); //기본 위치 : 서울 시청으로 설정
        naverMap.moveCamera(CameraUpdate.scrollTo(defaultLocation));

        // onResume에서도 mapView.invalidate()를 호출할 것이므로 여기서도 호출해 렌더링을 보장
        if (mapView != null) {
            mapView.invalidate();
        }
    }


    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        etPlaceName = findViewById(R.id.etPlaceName);
        btnNext = findViewById(R.id.btnNext);
        btnSearch = findViewById(R.id.btnSearch); // 검색 버튼 초기화
    }

    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }

    private void setTextWatcherForPlaceName() {
        etPlaceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 아무 기능 없음
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 텍스트 필드가 비어 있으면 버튼 비활성화, 아니면 버튼 활성화
                btnNext.setEnabled(charSequence.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 아무 작업도 하지 않지만 TextWatcher 구현하려면 반드시 필요함
            }
        });
    }

    // 공통 주소 검색 함수
    private void searchPlace(String placeName, PlaceSearchListener listener) {
        // PlaceSearchManager를 사용하여 주소 검색
        PlaceSearchManager placeSearchManager = new PlaceSearchManager(AddLocationActivity.this);
        placeSearchManager.searchPlace(placeName, new PlaceSearchManager.PlaceSearchListener() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                listener.onSuccess(latitude, longitude);  // 성공 시 listener 호출
            }

            @Override
            public void onFailure(String errorMessage) {
                listener.onFailure(errorMessage);  // 실패 시 listener 호출
            }
        });
    }

    // PlaceSearchListener 인터페이스 정의 (성공과 실패 시 처리 로직을 다르게 할 수 있게 함)
    interface PlaceSearchListener {
        void onSuccess(double latitude, double longitude);  // 성공 시
        void onFailure(String errorMessage);  // 실패 시
    }

    // 검색 버튼 클릭 시 장소 검색
    private void searchPlace() {
        if (!isMapReady) {
            Toast.makeText(this, "지도를 로딩 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String placeName = etPlaceName.getText().toString();

        if (!placeName.isEmpty()) {
            // 주소 검색
            searchPlace(placeName, new PlaceSearchListener() {
                @Override
                public void onSuccess(double latitude, double longitude) {
                    // 검색 성공 시: 마커 추가 및 카메라 이동
                    LatLng location = new LatLng(latitude, longitude);
                    addMarkerToMap(location);

                    // 카메라 이동
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(location).animate(CameraAnimation.Linear);
                    naverMap.moveCamera(cameraUpdate);
                }

                @Override
                public void onFailure(String errorMessage) {
                    // 검색 실패 시: 실패 메시지 띄우기
                    Toast.makeText(AddLocationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AddLocationActivity.this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    // 지도에 마커 표시
    private void addMarkerToMap(LatLng location) {
        if (!isMapReady) return;
        Marker marker = new Marker();
        marker.setPosition(location);  // 검색된 위치에 마커 추가
        marker.setMap(naverMap);
    }

    // Next 버튼 클릭 시
    private void nextButtonAction() {
        String placeName = etPlaceName.getText().toString();

        if (!placeName.isEmpty()) {
            // 주소 검색
            searchPlace(placeName, new PlaceSearchListener() {
                @Override
                public void onSuccess(double latitude, double longitude) {
                    // 검색 성공 시: 다음 화면으로 전환
//                    Intent intent = new Intent(AddLocationActivity.this);
//                    startActivity(intent);
//                    finish();  // 현재 액티비티 종료
                    Toast.makeText(AddLocationActivity.this, "장소가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    // 검색 실패 시: 팝업 띄우기
                    showAddLocationDialog();
                }
            });
        } else {
            Toast.makeText(AddLocationActivity.this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    // 장소를 추가하는 팝업 띄우기
    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialogStyle);

        // 커스텀 레이아웃 적용
        View dialogView = getLayoutInflater().inflate(R.layout.popup_location_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        // 둥근 모서리 보이게
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        dialogMessage.setText("등록되지 않은 주소예요. \n 이대로 추가할까요?");


        Button btnAdd = dialogView.findViewById(R.id.btnAdd); //이대로 추가하기
        Button btnSearchAgain = dialogView.findViewById(R.id.btnSearchAgain); //다시 검색

        btnAdd.setOnClickListener(v -> {
            // 장소를 추가하는 로직 (예: 데이터베이스에 저장)
            Toast.makeText(this, "장소가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnSearchAgain.setOnClickListener(v -> {
            // 검색 입력 필드를 비웁니다
            clearSearchField();
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    // 검색 필드 초기화
    public void clearSearchField() {
        etPlaceName.setText(""); // 텍스트 필드를 비웁니다
    }

    //  MapView 라이프사이클 연결
    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mapView != null) {
//            mapView.onResume();
//            // 맵이 아직 준비되지 않았다면 getMapAsync 호출
//            if (!isMapReady) {
//                mapView.post(() -> {
//                    mapView.getMapAsync(this);
//                    Log.d("Naver2_onResume_post", "MapView.post() 내부에서 getMapAsync() 호출");
//                });
//            }
//        }
//    }
        @Override
        protected void onResume() {
            super.onResume();
            if (mapView != null) {
                mapView.onResume(); // MapView의 생명주기 메서드는 항상 호출

//                if (!isMapReady) {
//                    mapView.getMapAsync(this);
//                    Log.d("Naver2_onResume_post", "onResume: getMapAsync() 호출");
//                }
                // ★ ㅁ 화면에 다시 나타날 때마다 MapView를 강제로 다시 그리게
                mapView.invalidate();
                Log.d("Naver2_onResume_post", "onResume: mapView.invalidate() 호출됨");
            }
        }


    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);  // 상태 저장
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}

//package com.example.yourtrip.mytrip;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.Window;
//import android.widget.EditText;
//import android.text.Editable;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Button;
//import android.widget.Toast;
//import android.view.View;
//import android.app.AlertDialog;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yourtrip.R;
//import com.naver.maps.map.CameraAnimation;
//import com.naver.maps.map.MapView;
//import com.naver.maps.map.NaverMap;
//import com.naver.maps.map.CameraUpdate;
//import com.naver.maps.geometry.LatLng;
//import com.naver.maps.map.NaverMapOptions;
//import com.naver.maps.map.NaverMapSdk;
//import com.naver.maps.map.overlay.Marker;
//
//public class AddLocationActivity extends AppCompatActivity {
//
//    private ImageView btnBack;
//    private TextView tvTitle;
//    private EditText etPlaceName;
//    private MapView mapView;
//    private NaverMap naverMap;
//    private Button btnNext;
//    private ImageView btnSearch;
//    private boolean isMapReady = false; // 지도가 준비되었는지 확인하는 플래그
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trip_add_location);
//
//        initViews();
//        setTopBar();
//        setTextWatcherForPlaceName();
//
//        // 네이버 맵 SDK 초기화 코드 -> YourTripApplication으로 옮겼음
//        // MapView 초기화
//        mapView = findViewById(R.id.map_view);
//        mapView.onCreate(savedInstanceState); //mapView 초기화
//
//        // 로그로 SDK 초기화가 정상적으로 되었는지 확인
//        Log.d("Naver1_NAVER_SDK_TEST", String.valueOf(NaverMapSdk.getInstance(this).getClient()));
//
//        // mapView가 정상적으로 초기화되었는지 확인하고 getMapAsync 호출
//        if (mapView != null) {
//            mapView.getMapAsync(naverMap -> {
//                this.naverMap = naverMap;
//
//                Log.d("Naver2_getMapAsync 호출", "getMapAsync 호출 완료");
//
//                // 줌 컨트롤 설정
//                NaverMapOptions options = new NaverMapOptions();
//                options.zoomControlEnabled(true);  // 줌 컨트롤 활성화
//
//                // 지도 기본 위치 설정 (예: 서울 시청)
//                LatLng defaultLocation = new LatLng(37.5665, 126.9780);
//                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(defaultLocation);
//                naverMap.moveCamera(cameraUpdate);
//
//                // 뷰를 강제로 다시 그리도록 요청
//                mapView.invalidate();
//            });
//        } else {
//            Log.e("Naver2_getMapAsync Error", "NaverMap 객체가 null");
//        }
//
//
//        // 검색 버튼 클릭 이벤트 처리
//        btnSearch.setOnClickListener(v -> {
//            searchPlace(); // 주소 검색
//        });
//
//        // btnNext 클릭 이벤트 처리
//        btnNext.setOnClickListener(v -> {
//            // 버튼 클릭 시 장소 검색 및 팝업 처리
//            nextButtonAction(); // 주소 검색 후 장소 추가 또는 팝업 띄우기
//        });
//    }
//
//
//    private void initViews() {
//        tvTitle = findViewById(R.id.tv_title);
//        btnBack = findViewById(R.id.btnBack);
//        etPlaceName = findViewById(R.id.etPlaceName);
//        btnNext = findViewById(R.id.btnNext);
//        btnSearch = findViewById(R.id.btnSearch); // 검색 버튼 초기화
//    }
//
//    private void setTopBar() {
//        tvTitle.setText("장소 추가하기");
//        btnBack.setOnClickListener(v -> finish());
//    }
//
//    private void setTextWatcherForPlaceName() {
//        etPlaceName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
//                // 아무 기능 없음
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                // 텍스트 필드가 비어 있으면 버튼 비활성화, 아니면 버튼 활성화
//                btnNext.setEnabled(charSequence.length() > 0);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                // 아무 작업도 하지 않지만 TextWatcher 구현하려면 반드시 필요함
//            }
//        });
//    }
//
//    // 공통 주소 검색 함수
//    private void searchPlace(String placeName, PlaceSearchListener listener) {
//        // PlaceSearchManager를 사용하여 주소 검색
//        PlaceSearchManager placeSearchManager = new PlaceSearchManager(this);
//        placeSearchManager.searchPlace(placeName, new PlaceSearchManager.PlaceSearchListener() {
//            @Override
//            public void onSuccess(double latitude, double longitude) {
//                listener.onSuccess(latitude, longitude);  // 성공 시 listener 호출
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                listener.onFailure(errorMessage);  // 실패 시 listener 호출
//            }
//        });
//    }
//
//    // PlaceSearchListener 인터페이스 정의 (성공과 실패 시 처리 로직을 다르게 할 수 있게 함)
//    interface PlaceSearchListener {
//        void onSuccess(double latitude, double longitude);  // 성공 시
//        void onFailure(String errorMessage);  // 실패 시
//    }
//
//    // 검색 버튼 클릭 시 장소 검색
//    private void searchPlace() {
//        String placeName = etPlaceName.getText().toString();
//
//        if (!placeName.isEmpty()) {
//            // 주소 검색
//            searchPlace(placeName, new PlaceSearchListener() {
//                @Override
//                public void onSuccess(double latitude, double longitude) {
//                    // 검색 성공 시: 마커 추가 및 카메라 이동
//                    LatLng location = new LatLng(latitude, longitude);
//                    addMarkerToMap(location);
//
//                    // 카메라 이동
//                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(location).animate(CameraAnimation.Linear);
//                    naverMap.moveCamera(cameraUpdate);
//                }
//
//                @Override
//                public void onFailure(String errorMessage) {
//                    // 검색 실패 시: 실패 메시지 띄우기
//                    Toast.makeText(AddLocationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(AddLocationActivity.this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    // 지도에 마커 표시
//    private void addMarkerToMap(LatLng location) {
//        Marker marker = new Marker();
//        marker.setPosition(location);  // 검색된 위치에 마커 추가
//        marker.setMap(naverMap);
//    }
//
//    // Next 버튼 클릭 시
//    private void nextButtonAction() {
//        String placeName = etPlaceName.getText().toString();
//
//        if (!placeName.isEmpty()) {
//            // 주소 검색
//            searchPlace(placeName, new PlaceSearchListener() {
//                @Override
//                public void onSuccess(double latitude, double longitude) {
//                    // 검색 성공 시: 다음 화면으로 전환
////                    Intent intent = new Intent(AddLocationActivity.this);
////                    startActivity(intent);
////                    finish();  // 현재 액티비티 종료
//                    Toast.makeText(AddLocationActivity.this, "장소가 추가되었습니다.", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(String errorMessage) {
//                    // 검색 실패 시: 팝업 띄우기
//                    showAddLocationDialog();
//                }
//            });
//        } else {
//            Toast.makeText(AddLocationActivity.this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//
//
//    // 장소를 추가하는 팝업 띄우기
//    private void showAddLocationDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialogStyle);
//
//        // 커스텀 레이아웃 적용
//        View dialogView = getLayoutInflater().inflate(R.layout.popup_location_dialog, null);
//        builder.setView(dialogView);
//
//        AlertDialog dialog = builder.create();
//        // 둥근 모서리 보이게
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//
//        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
//        dialogMessage.setText("등록되지 않은 주소예요. \n 이대로 추가할까요?");
//
//
//        Button btnAdd = dialogView.findViewById(R.id.btnAdd); //이대로 추가하기
//        Button btnSearchAgain = dialogView.findViewById(R.id.btnSearchAgain); //다시 검색
//
//        btnAdd.setOnClickListener(v -> {
//            // 장소를 추가하는 로직 (예: 데이터베이스에 저장)
//            Toast.makeText(this, "장소가 추가되었습니다.", Toast.LENGTH_SHORT).show();
//        });
//
//        btnSearchAgain.setOnClickListener(v -> {
//            // 검색 입력 필드를 비웁니다
//            clearSearchField();
//            dialog.dismiss();
//        });
//
//        builder.setCancelable(true); // 다이얼로그 외부 클릭 시 닫히지 않도록 설정
//        dialog.setCanceledOnTouchOutside(true);
//
//        dialog.show();
//    }
//
//
//    // 검색 필드 초기화
//    public void clearSearchField() {
//        etPlaceName.setText(""); // 텍스트 필드를 비웁니다
//    }
//
//    //  MapView 라이프사이클 연결
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);  // 상태 저장
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//}
