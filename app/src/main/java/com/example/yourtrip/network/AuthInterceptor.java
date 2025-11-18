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
public class AuthInterceptor implements Interceptor {
    
    //Retrofit이 서버로 요청 보내기 직전에 intercept()가 자동으로 호출되어 요청 가로챔
    @Override
    public Response intercept(Chain chain) throws IOException {

        Context context = YourTripApplication.getAppContext(); //전역 Context를 가져옴

        SharedPreferences prefs =
                context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String token = prefs.getString("accessToken", null);
        Log.d("AUTH_INTERCEPTOR", "보내는 토큰 값: " + token);

        Request request = chain.request().newBuilder()
                .addHeader("Authorization", token != null ? "Bearer " + token : "")
                .build();

        return chain.proceed(request);
    }
}

