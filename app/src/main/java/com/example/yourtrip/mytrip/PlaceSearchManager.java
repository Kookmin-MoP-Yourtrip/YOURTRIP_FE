package com.example.yourtrip.mytrip;

import android.content.Context;

import com.example.yourtrip.network.NaverGeocodeAPI;
import com.example.yourtrip.network.NaverGeocodeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceSearchManager {

    private static final String BASE_URL = "https://naveropenapi.apis.naver.com/";
    private static final String CLIENT_ID = "YOUR_CLIENT_ID"; // 네이버 클라우드에서 발급받은 클라이언트 ID
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET"; // 네이버 클라우드에서 발급받은 클라이언트 비밀
    private Context context;

    public PlaceSearchManager(Context context) {
        this.context = context;
    }

    // 장소 검색
    public void searchPlace(String placeName, PlaceSearchListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NaverGeocodeAPI api = retrofit.create(NaverGeocodeAPI.class);

        // Geocoding API 호출
        Call<NaverGeocodeResponse> call = api.getCoordinates(CLIENT_ID, CLIENT_SECRET, placeName);

        call.enqueue(new Callback<NaverGeocodeResponse>() {
            @Override
            public void onResponse(Call<NaverGeocodeResponse> call, Response<NaverGeocodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NaverGeocodeResponse geocodingResponse = response.body();
                    if (geocodingResponse.getStatus().equals("OK")) {
                        double latitude = geocodingResponse.getItems().get(0).getLatitude();
                        double longitude = geocodingResponse.getItems().get(0).getLongitude();

                        // 성공적으로 좌표를 받아온 경우, 리스너에 결과 전달
                        listener.onSuccess(latitude, longitude);
                    } else {
                        // 주소를 찾을 수 없으면 실패 처리
                        listener.onFailure("주소를 찾을 수 없습니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<NaverGeocodeResponse> call, Throwable t) {
                // 네트워크 실패 시 처리
                listener.onFailure("주소 검색에 실패했습니다.");
            }
        });
    }

    // 장소 검색 성공/실패 리스너 인터페이스
    public interface PlaceSearchListener {
        void onSuccess(double latitude, double longitude);  // 성공 시 호출
        void onFailure(String errorMessage);  // 실패 시 호출
    }
}
