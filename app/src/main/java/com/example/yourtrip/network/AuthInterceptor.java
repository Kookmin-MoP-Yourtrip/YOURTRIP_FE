package com.example.yourtrip.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.yourtrip.YourTripApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

// OkHttp의 Interceptor 기능을 구현하는 클래스
// API 요청을 가로채서 수정하는 클래스

//Retrofit 클라이언트를 만들 때, new AuthInterceptor(this)와 같이 Context를 건내줄거임

public class AuthInterceptor implements Interceptor {

    // Context를 저장할 전용 변수
    private final Context context;

    public AuthInterceptor(Context context) {
        // 외부에서 건네받은 context를 내 보관함에 안전하게 저장
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 만능 Context가 아닌 생성자에서 이미 받아둔 안전한 전용 context를 사용
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        Log.d("AUTH_INTERCEPTOR", "보내는 토큰 값: " + token);
        Log.e("TOKEN_CHECK", "token = " + token);

        Request request = chain.request().newBuilder()
                .addHeader("Authorization", token != null ? "Bearer " + token : "")
                .build();

        return chain.proceed(request);
    }
}

// 생성자: Context를 매개변수로 받도록 수정
//    public AuthInterceptor(Context context) {
//        this.context = context.getApplicationContext(); // Application Context를 저장해 메모리 누수 방지
//    }
//
//    //Retrofit이 서버로 요청 보내기 직전에 intercept()가 자동으로 호출되어 요청 가로챔
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//
//        Context context = YourTripApplication.getAppContext(); //전역 Context를 가져옴
//
//        SharedPreferences prefs =
//                context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//
//        String token = prefs.getString("accessToken", null);
//        Log.d("AUTH_INTERCEPTOR", "보내는 토큰 값: " + token);
//        Log.e("TOKEN_CHECK", "token = " + token);
//
//        Request request = chain.request().newBuilder()
//                .addHeader("Authorization", token != null ? "Bearer " + token : "")
//                .build();
//
//        return chain.proceed(request);
//    }

