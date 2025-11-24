package com.example.yourtrip.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NaverGeocodeApi {

    @GET("map-geocode/v2/geocode")
    Call<NaverGeocodeResponse> geocode(
            @Header("X-NCP-APIGW-API-KEY-ID") String clientId,
            @Header("X-NCP-APIGW-API-KEY") String clientSecret,
            @Query("query") String query
    );
}
