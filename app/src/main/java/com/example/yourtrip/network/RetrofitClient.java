//서버랑 통신할 수 있는 Retrofit 세팅

package com.example.yourtrip.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://yourtrip.site/"; //  서버 주소

    public static Retrofit getInstance() {
        if (retrofit == null) { //처음 한 번만 생성
            //OkHttpClient + Interceptor 추가 -> Retrofit 요청을 가로채서 조작하는 장치
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor()) //Retrofit이 보내는 모든 요청을 Interceptor가 가로챔
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)   // 헤더 자동 첨부, 토큰 하나하나 안붙여줘도 됨
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit; //이후엔 기존 객체 재사용
    }

    public static ApiService getAuthService() {
        return getInstance().create(ApiService.class);
    }
}
