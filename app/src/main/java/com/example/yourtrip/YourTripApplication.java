package com.example.yourtrip;

import android.app.Application;
import android.util.Log;

import com.naver.maps.map.NaverMapSdk;

public class YourTripApplication extends Application {

    // 앱 전체에서 SDK 초기화가 단 한 번만 실행되도록 보장하는 변수
    private static boolean isNaverMapSdkInitialized = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Naver0_YourTripApplication", "onCreate()가 호출되었습니다.");

        // SDK가 아직 초기화되지 않았을 때만 초기화 코드를 실행
        if (!isNaverMapSdkInitialized) {
            Log.d("Naver0_YourTripApplication", "네이버 지도 SDK 초기화를 시작합니다.");
            try {
                NaverMapSdk.getInstance(this).setClient(
                        new NaverMapSdk.NcpKeyClient("lm7f1yckad")
                );
                isNaverMapSdkInitialized = true; // 초기화 성공 상태를 기록
                Log.d("Naver0_YourTripApplication", "네이버 지도 SDK 초기화에 성공했습니다.");

            } catch (Exception e) {
                // 초기화 중 오류가 발생하면 로그를 남겨서 쉽게 확인할 수 있도록 함
                Log.e("Naver0_YourTripApplication", "네이버 지도 SDK 초기화 중 오류 발생", e);
            }
        } else {
            // 이미 초기화된 경우, 불필요한 재실행을 방지했음을 로그로 확인
            Log.d("Naver0_YourTripApplication", "네이버 지도 SDK는 이미 초기화되었습니다.");
        }
    }
}



//기존에 전역 context로 관리하던 코드
/**
 * Retrofit Interceptor가 SharedPreferences에 저장된 JWT 토큰을 읽기 위해
 * 앱 전체에서 공용으로 사용할 수 있는 전역 Context
 * 이를 위해 Application 클래스를 상속한 YourTripApplication을 생성
 * Interceptor는 YourTripApplication.getAppContext() 를 통해 Context에 접근
 */

// Context = 안드로이드에서 모든 것의 환경 정보를 들고 있는 객체

//package com.example.yourtrip;
//import android.app.Application;
//import android.content.Context;
//import android.util.Log;
//
//import com.naver.maps.map.NaverMapSdk;
//
//public class YourTripApplication extends Application {
//
//    private static YourTripApplication instance;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d("Naver0_YourTripApplication", "onCreate() called");
//        instance = this;
//
//        // 네이버맵 SDK 전역 초기화
//        try {
//            NaverMapSdk.getInstance(this).setClient(
//                    new NaverMapSdk.NcpKeyClient("lm7f1yckad")
//            );
//            Log.d("Naver0_NAVER_SDK", "NaverMap SDK initialization success.");
//        } catch (Exception e) {
//            Log.e("Naver0_NAVER_SDK", "NaverMap SDK initialization failed: " + e.getMessage());
//        }
//    }
//
//    public static Context getAppContext() {
//        return instance.getApplicationContext();
//    }
//}