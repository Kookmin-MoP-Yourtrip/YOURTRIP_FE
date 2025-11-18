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

public class YourTripApplication extends Application {

    private static YourTripApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
