package com.example.yourtrip.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NaverSearchApiService {

    // 지역 검색(local)
    @GET("v1/search/local.json")
    Call<NaverSearchResponse> searchLocal(
            @Header("X-Naver-Client-Id") String clientId,
            @Header("X-Naver-Client-Secret") String clientSecret,
            @Query("query") String query,
            @Query("display") Integer display,
            @Query("start") Integer start,
            @Query("sort") String sort
    );
}

