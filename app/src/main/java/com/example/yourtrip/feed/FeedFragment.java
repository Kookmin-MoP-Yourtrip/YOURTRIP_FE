package com.example.yourtrip.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.FeedDetailResponse;
import com.example.yourtrip.model.FeedListResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FeedFragment extends Fragment {

    private RecyclerView rvFeed;

    // 🔹 전역 변수로 변경
    private FeedAdapter adapter;
    private List<FeedItem> feedItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_main, container, false);

        rvFeed = view.findViewById(R.id.rv_feed);

        // 2열 그리드
        rvFeed.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 🔹 Adapter 생성 (초기에는 빈 리스트)
        adapter = new FeedAdapter(feedItems);
        rvFeed.setAdapter(adapter);

        // 🔹 서버에서 피드 불러오기
        loadFeeds();

        return view;
    }

    private void loadFeeds() {
        ApiService api = RetrofitClient.getAuthService();

        api.getFeedList(0, 20).enqueue(new Callback<FeedListResponse>() {
            @Override
            public void onResponse(Call<FeedListResponse> call, Response<FeedListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    for (FeedDetailResponse feed : response.body().getFeeds()) {
                        if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
                            String url = feed.getMediaList().get(0).getUrl();

                            // 🔹 URL 기반 FeedItem 추가
                            feedItems.add(new FeedItem(url));
                        }
                    }

                    // RecyclerView 갱신
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<FeedListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}