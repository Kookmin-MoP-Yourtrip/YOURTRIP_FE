/**
 * Retrofit Interceptor가 SharedPreferences에 저장된 JWT 토큰을 읽기 위해
 * 앱 전체에서 공용으로 사용할 수 있는 전역 Context가 필요
 * 이를 위해 Application 클래스를 상속한 YourTripApplication을 생성
 * Interceptor는 YourTripApplication.getAppContext() 를 통해 Context에 접근
 */

// Context = 안드로이드에서 모든 것의 환경 정보를 들고 있는 객체
// 앱의 현재 상태 + 시스템 기능을 사용할 수 있게 연결


package com.example.yourtrip;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.naver.maps.map.NaverMapSdk;

public class YourTripApplication extends Application {

    private static YourTripApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Naver0_YourTripApplication", "onCreate() called");
        instance = this;

        // 네이버맵 SDK 전역 초기화
        try {
            NaverMapSdk.getInstance(this).setClient(
                    new NaverMapSdk.NcpKeyClient("lm7f1yckad")
            );
            Log.d("Naver0_NAVER_SDK", "NaverMap SDK initialization success.");
        } catch (Exception e) {
            Log.e("Naver0_NAVER_SDK", "NaverMap SDK initialization failed: " + e.getMessage());
        }
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
