package com.example.yourtrip.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://yourtrip.site/";

    // 기존 getInstance() 메서드는 유지
    // getClient 메서드가 Context를 받도록 수정
    public static Retrofit getInstance(Context context) {
        if (retrofit == null) { //처음 한 번만 생성
            //OkHttpClient + Interceptor 추가 -> Retrofit 요청을 가로채서 조작하는 장치
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context)) // 인터셉터 생성 시 context 전달
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)   // 헤더 자동 첨부, 토큰 하나하나 안붙여줘도 됨
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit; // 이후엔 기존 객체 재사용
    }

    // 기존 API 호출 서비스 유지
    public static ApiService getAuthService(Context context) {
        return getInstance(context).create(ApiService.class);
    }

    // Naver Geocoding API 호출 서비스 추가
    public static NaverGeocodeAPI getNaverGeocodingService(Context context) {
        return getInstance(context).create(NaverGeocodeAPI.class);
    }
}
