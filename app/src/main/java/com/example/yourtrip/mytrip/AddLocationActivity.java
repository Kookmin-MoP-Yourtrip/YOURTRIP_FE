package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.EditText;
import android.text.Editable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMapSdk;

public class AddLocationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private EditText etPlaceName;
    private MapView mapView;
    private NaverMap naverMap;
    private Button btnNext;

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
        setTextWatcherForPlaceName();

        // MapView 초기화는 setContentView() 이후에만 가능
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        // 지도 객체를 비동기로 받아오고, 기본 카메라 위치 설정
        mapView.getMapAsync(naverMap -> {
            this.naverMap = naverMap;

            // 지도 기본 위치 설정 (예: 서울 시청)
            LatLng defaultLocation = new LatLng(37.5665, 126.9780);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(defaultLocation);
            naverMap.moveCamera(cameraUpdate);
        });
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        etPlaceName = findViewById(R.id.etPlaceName);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }

    private void setTextWatcherForPlaceName() {
        etPlaceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 아무 것도 하지 않음
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
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy(); // super 전에 호출
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
