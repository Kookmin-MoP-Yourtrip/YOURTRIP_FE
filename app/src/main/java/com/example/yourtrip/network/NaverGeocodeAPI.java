package com.example.yourtrip.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NaverGeocodeAPI {

    // 네이버 Geocoding API를 사용하여 주소를 위도와 경도로 변환
    @GET("map-geocode/v2/geocode")
    Call<NaverGeocodeResponse> getCoordinates(
            @Header("X-NCP-APIGW-API-KEY-ID") String clientId,
            @Header("X-NCP-APIGW-API-KEY") String clientSecret,
            @Query("query") String query
    );
}
