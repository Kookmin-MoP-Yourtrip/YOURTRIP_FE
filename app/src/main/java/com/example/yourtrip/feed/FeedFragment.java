package com.example.yourtrip.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.FeedListResponse;
import com.example.yourtrip.model.FeedSummaryResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedFragment extends Fragment {

    private RecyclerView rvFeed;
    private FeedAdapter adapter;
    private List<FeedItem> feedItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_main, container, false);

        rvFeed = view.findViewById(R.id.rv_feed);

        // 2열 그리드
        rvFeed.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // adapter 생성 (클릭 시 상세로 이동)
        adapter = new FeedAdapter(feedItems, item -> {

            FeedDetailFragment fragment = new FeedDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("feedId", item.getId());
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        rvFeed.setAdapter(adapter);

//        // ---------- 더미 1개만 넣기 ----------
//        feedItems.clear();
//        feedItems.add(new FeedItem(1, "https://pichsum.photos/300"));
//        adapter.notifyDataSetChanged();
//        // -----------------------------------

        loadFeedList();

        return view;
    }
//
//    private void loadFeedList() {
//
//        ApiService api = RetrofitClient.getAuthService();
//
//        api.getFeedList("NEW", 0, 20)
//                .enqueue(new Callback<FeedListResponse>() {
//                    @Override
//                    public void onResponse(Call<FeedListResponse> call,
//                                           Response<FeedListResponse> response) {
//
//                        if (!response.isSuccessful() || response.body() == null) {
//                            return;
//                        }
//
//                        List<FeedSummaryResponse> serverList = response.body().getFeeds();
//                        if (serverList == null) return;
//
//                        feedItems.clear();
//
//                        for (FeedSummaryResponse feed : serverList) {
//
//                            // 썸네일(첫 이미지) 추출
//                            String thumbnail = null;
//                            if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
//                                thumbnail = feed.getMediaList().get(0).getUrl();
//                            }
//
//                            feedItems.add(new FeedItem(
//                                    feed.getFeedId(),
//                                    thumbnail
//                            ));
//                        }
//
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onFailure(Call<FeedListResponse> call, Throwable t) {
//                        t.printStackTrace();
//                    }
//                });
//    }
private void loadFeedList() {

    ApiService api = RetrofitClient.getAuthService();

    api.getFeedList("NEW", 0, 20)
            .enqueue(new Callback<FeedListResponse>() {
                @Override
                public void onResponse(Call<FeedListResponse> call, Response<FeedListResponse> response) {

                    // ★ 응답 코드 확인
                    Log.d("FEED_API", "Response code: " + response.code());

                    if (response.isSuccessful()) {
                        Log.d("FEED_API", "SUCCESS");

                        // ★ body null 체크
                        if (response.body() == null) {
                            Log.e("FEED_API", "body is NULL");
                            return;
                        }

                        // ★ body 전체 로그 출력
                        Log.d("FEED_API", "body: " + new Gson().toJson(response.body()));

                        List<FeedSummaryResponse> serverList = response.body().getFeeds();

                        if (serverList == null) {
                            Log.e("FEED_API", "serverList is NULL");
                            return;
                        }

                        Log.d("FEED_API", "feed count = " + serverList.size());

                        feedItems.clear();

                        for (FeedSummaryResponse feed : serverList) {

                            String thumbnail = null;
                            if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
                                thumbnail = feed.getMediaList().get(0).getUrl();
                            }

                            feedItems.add(new FeedItem(
                                    feed.getFeedId(),
                                    thumbnail
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        // ★ errorBody 확인
                        try {
                            String err = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "errorBody is NULL";
                            Log.e("FEED_API", "ERROR response = " + err);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<FeedListResponse> call, Throwable t) {
                    Log.e("FEED_API", "FAILURE: " + t.getMessage());
                }
            });
}

}
