
package com.example.yourtrip.mytrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

//  네이버 지도 SDK import
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.MapFragment;

//  Retrofit & 네이버 API import
import com.example.yourtrip.network.NaverGeocodeApi;
import com.example.yourtrip.network.NaverGeocodeResponse;
import com.example.yourtrip.network.NaverRetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private MapView mapView;
    private NaverMap naverMap;
    private EditText etPlaceName;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_location);

        // 네이버 맵 SDK 초기화 (여기서 클라이언트 ID 설정)
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NcpKeyClient("lm7f1yckad")
        );

        initViews();
        setTopBar();

        //  MapView 초기화는 setContentView() 이후에만 가능
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        //  지도 객체를 비동기로 받아오고, 기본 카메라 위치 설정
        mapView.getMapAsync(naverMap -> {
            this.naverMap = naverMap;

            // 지도 기본 위치 설정 (예: 서울 시청)
            LatLng defaultLocation = new LatLng(37.5665, 126.9780);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(defaultLocation);
            naverMap.moveCamera(cameraUpdate);
        });

        //  검색창 엔터 입력 시 검색 실행
        etPlaceName.setOnEditorActionListener((v, actionId, event) -> {
            searchLocation();
            return true;
        });
    }

    // 뷰 초기화
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        etPlaceName = findViewById(R.id.etPlaceName);
    }

    // 상단바 설정
    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }

    // 위치 검색 → 지도 이동 + 마커 표시
    private void searchLocation() {
        String keyword = etPlaceName.getText().toString().trim();
        if (keyword.isEmpty()) return;

        NaverGeocodeApi api =
                NaverRetrofitClient.getClient().create(NaverGeocodeApi.class);

        api.geocode(
                "lm7f1yckad",   // Client ID
                "RQPlwhUG7QlhaomAvlXcUzuYGfe5c0ebOvH8Ahen", // Secret
                keyword
        ).enqueue(new Callback<NaverGeocodeResponse>() {
            @Override
            public void onResponse(Call<NaverGeocodeResponse> call, Response<NaverGeocodeResponse> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                if (response.body().addresses.size() == 0) return;

                NaverGeocodeResponse.Address addr = response.body().addresses.get(0);

                double lng = Double.parseDouble(addr.x);
                double lat = Double.parseDouble(addr.y);

                LatLng position = new LatLng(lat, lng);

                // 지도 이동
                naverMap.moveCamera(CameraUpdate.scrollTo(position));

                // 이전 마커 삭제
                if (marker != null) marker.setMap(null);

                // 새 마커 생성
                marker = new Marker();
                marker.setPosition(position);
                marker.setMap(naverMap);
            }

            @Override
            public void onFailure(Call<NaverGeocodeResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //  MapView 라이프사이클 연결
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
//        mapView.onPause(); // super 전에 호출해야 함
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);  // 상태 저장
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy(); // super 전에 호출
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}


//package com.example.yourtrip.mytrip;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yourtrip.R;
//
////  네이버 지도 SDK import
//import com.naver.maps.map.NaverMap;
//import com.naver.maps.geometry.LatLng;
//import com.naver.maps.map.CameraUpdate;
//import com.naver.maps.map.overlay.Marker;
//import com.naver.maps.map.NaverMapSdk;
//import com.naver.maps.map.MapFragment;
//
////  Retrofit & 네이버 API import
//import com.example.yourtrip.network.NaverGeocodeApi;
//import com.example.yourtrip.network.NaverGeocodeResponse;
//import com.example.yourtrip.network.NaverRetrofitClient;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class AddLocationActivity extends AppCompatActivity {
//
//    private ImageView btnBack;
//    private TextView tvTitle;
//    private EditText etPlaceName;
//    private Marker marker;
//    private NaverMap naverMap;
//    private MapFragment mapFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trip_add_location);
//
//        // 네이버 맵 SDK 초기화 (여기서 클라이언트 ID 설정)
//        NaverMapSdk.getInstance(this).setClient(
//                new NaverMapSdk.NcpKeyClient("lm7f1yckad")
//        );
//
//        initViews();
//        setTopBar();
//
//        // MapFragment 초기화
//        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
//        if (mapFragment == null) {
//            mapFragment = MapFragment.newInstance();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.map_fragment, mapFragment)
//                    .commit();
//        }
//
//        // 지도 객체를 비동기로 받아오고, 기본 카메라 위치 설정
//        mapFragment.getMapAsync(naverMap -> {
//            this.naverMap = naverMap;
//
//            // 지도 기본 위치 설정 (예: 서울 시청)
//            LatLng defaultLocation = new LatLng(37.5665, 126.9780);
//            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(defaultLocation);
//            naverMap.moveCamera(cameraUpdate);
//        });
//
//        // 검색창 엔터 입력 시 검색 실행
//        etPlaceName.setOnEditorActionListener((v, actionId, event) -> {
//            searchLocation();
//            return true;
//        });
//    }
//
//    // 뷰 초기화
//    private void initViews() {
//        tvTitle = findViewById(R.id.tv_title);
//        btnBack = findViewById(R.id.btnBack);
//        etPlaceName = findViewById(R.id.etPlaceName);
//    }
//
//    // 상단바 설정
//    private void setTopBar() {
//        tvTitle.setText("장소 추가하기");
//        btnBack.setOnClickListener(v -> finish());
//    }
//
//    // 위치 검색 → 지도 이동 + 마커 표시
//    private void searchLocation() {
//        String keyword = etPlaceName.getText().toString().trim();
//        if (keyword.isEmpty()) return;
//
//        NaverGeocodeApi api =
//                NaverRetrofitClient.getClient().create(NaverGeocodeApi.class);
//
//        api.geocode(
//                "lm7f1yckad",   // Client ID
//                "RQPlwhUG7QlhaomAvlXcUzuYGfe5c0ebOvH8Ahen", // Secret
//                keyword
//        ).enqueue(new Callback<NaverGeocodeResponse>() {
//            @Override
//            public void onResponse(Call<NaverGeocodeResponse> call, Response<NaverGeocodeResponse> response) {
//                if (!response.isSuccessful() || response.body() == null) return;
//                if (response.body().addresses.size() == 0) return;
//
//                NaverGeocodeResponse.Address addr = response.body().addresses.get(0);
//
//                double lng = Double.parseDouble(addr.x);
//                double lat = Double.parseDouble(addr.y);
//
//                LatLng position = new LatLng(lat, lng);
//
//                // 지도 이동
//                naverMap.moveCamera(CameraUpdate.scrollTo(position));
//
//                // 이전 마커 삭제
//                if (marker != null) marker.setMap(null);
//
//                // 새 마커 생성
//                marker = new Marker();
//                marker.setPosition(position);
//                marker.setMap(naverMap);
//            }
//
//            @Override
//            public void onFailure(Call<NaverGeocodeResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }
//
//    //  MapView 라이프사이클 연결
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mapFragment.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapFragment.onResume();
//    }
//
////    @Override
////    protected void onPause() {
////        mapFragment.onPause(); // super 전에 호출해야 함
////        super.onPause();
////    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapFragment.onPause(); // super 전에 호출해야 함
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapFragment.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onStop() {
//        mapFragment.onStop();
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        mapFragment.onDestroy(); // super 전에 호출
//        super.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapFragment.onLowMemory();
//    }
//}


