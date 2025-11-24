package com.example.yourtrip.mytrip;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMapSdk;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.yourtrip.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MapFragment extends Fragment {

    private MapView mapView;  // MapView 객체
    private NaverMap naverMap;  // NaverMap 객체

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 네이버 맵 SDK 초기화
        NaverMapSdk.getInstance(getContext()).setClient(
                new NaverMapSdk.NcpKeyClient("lm7f1yckad")
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fragment_map.xml 레이아웃을 인플레이트
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // MapView 초기화
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);  // 지도 초기화

        // 지도 객체를 비동기적으로 받아오기
        mapView.getMapAsync(naverMap -> {
            this.naverMap = naverMap;
            // 지도 기본 위치 설정 (예: 서울 시청)
            LatLng defaultLocation = new LatLng(37.5665, 126.9780);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(defaultLocation);
            naverMap.moveCamera(cameraUpdate);  // 지도 이동
        });

        return view;
    }

    // MapView 라이프사이클 연결
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
