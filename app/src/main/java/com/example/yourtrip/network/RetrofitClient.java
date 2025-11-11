//서버랑 통신할 수 있는 Retrofit 세팅

package com.example.yourtrip.network;

//Retrofit : 서버랑 HTTP 통신을 쉽게 해주는 핵심 라이브러리
// GsonConverterFactory : 서버에서 받은 JSON 데이터를 자동으로 Java 객체로 바꿔줌
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://3.89.62.194:8080"; //  서버 주소

    public static Retrofit getInstance() {
        if (retrofit == null) { //처음 한 번만 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit; //이후엔 기존 객체 재사용
    }

    public static ApiService getAuthService() {
        return getInstance().create(ApiService.class);
    }
}
