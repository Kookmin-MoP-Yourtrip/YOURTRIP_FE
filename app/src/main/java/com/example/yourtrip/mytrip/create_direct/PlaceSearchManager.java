package com.example.yourtrip.mytrip.create_direct;

import android.content.Context;
import android.util.Log;

import com.example.yourtrip.R;
import com.example.yourtrip.network.NaverSearchApiService;
import com.example.yourtrip.network.NaverSearchResponse;
import com.example.yourtrip.network.NaverSearchRetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceSearchManager {

    private final Context context;
    private final NaverSearchApiService api;

    private static final String TAG = "NAVER_LOCAL_SEARCH";

    public PlaceSearchManager(Context context) {
        this.context = context;
        this.api = NaverSearchRetrofitClient.getClient().create(NaverSearchApiService.class);
    }

    public void searchPlaces(String query, PlaceSearchListener listener) {

        //  네이버 API KEY
        String clientId = context.getString(R.string.naver_client_id);
        String clientSecret = context.getString(R.string.naver_client_secret);

        Log.d(TAG, " 검색 요청 시작 — query = " + query);
        Log.d(TAG, " ClientId=" + clientId + " / ClientSecret=" + clientSecret);

        Call<NaverSearchResponse> call = api.searchLocal(
                clientId,
                clientSecret,
                query,
                5,        // 최대 5개 (지역 검색 API 제한)
                1,
                "random"
        );

        //  요청 URL 확인
        Log.d(TAG, " 요청 URL: " + call.request().url());

        call.enqueue(new Callback<NaverSearchResponse>() {
            @Override
            public void onResponse(Call<NaverSearchResponse> call, Response<NaverSearchResponse> response) {

                Log.d(TAG, " 응답 도착! HTTP CODE = " + response.code());

                // body null 체크
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, " 응답 실패 — code=" + response.code());
                    listener.onFailure("API 실패: " + response.code());
                    return;
                }

                // 응답 전체 JSON 로그
                Log.d(TAG, " 응답 Body: " + response.body().toString());

                List<NaverSearchResponse.Item> items = response.body().items;

                Log.d(TAG, " 검색 결과 개수 = " + (items != null ? items.size() : 0));

                if (items == null || items.isEmpty()) {
                    Log.w(TAG, "⚠ 검색 결과 없음");
                    listener.onEmpty();
                    return;
                }

                // 각 item 로그
                for (int i = 0; i < items.size(); i++) {
                    NaverSearchResponse.Item item = items.get(i);
                    Log.d(TAG, " Item " + i + ": " + item.title +
                            " | " + item.roadAddress +
                            " | x=" + item.mapx + " / y=" + item.mapy);
                }

                listener.onSuccess(items);
            }

            @Override
            public void onFailure(Call<NaverSearchResponse> call, Throwable t) {
                Log.e(TAG, " 네트워크 오류: " + t.getMessage());
                listener.onFailure("네트워크 오류: " + t.getMessage());
            }
        });
    }

    // 결과 상태 3가지로 분리하는 Listener
    public interface PlaceSearchListener {
        void onSuccess(List<NaverSearchResponse.Item> items);
        void onEmpty();
        void onFailure(String errorMessage);
    }
}
