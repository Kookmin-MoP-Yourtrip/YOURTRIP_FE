package com.example.yourtrip.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NaverGeocodeAPI {

    // 네이버 Geocoding API를 사용하여 주소를 위도와 경도로 변환
    @GET("map/geocode")
    Call<NaverGeocodeResponse> getCoordinates(
            @Query("clientId") String clientId,
            @Query("clientSecret") String clientSecret,
            @Query("query") String query
    );
}
