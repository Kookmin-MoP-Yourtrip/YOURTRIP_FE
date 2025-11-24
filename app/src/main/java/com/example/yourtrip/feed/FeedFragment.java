package com.example.yourtrip.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
    private FeedAdapter adapter;
    private List<FeedItem> feedItems = new ArrayList<>();
    private EditText etSearch;
    private ImageView btnSearch;
    private View layoutLoading;
    private View layoutContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_main, container, false);

        layoutLoading = view.findViewById(R.id.loadingLayout_feed_main);
        layoutContent = view.findViewById(R.id.contentLayout_feed_main);

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

        loadFeedList();

        etSearch = view.findViewById(R.id.tvFeedSearch);
        btnSearch = view.findViewById(R.id.btnFeedSearch);

        // ⭐ 검색 버튼 클릭 시
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();

            if (keyword.isEmpty()) {
                // 검색어 없으면 전체 리스트
                loadFeedList();
            } else {
                // 검색 실행
                searchFeedList(keyword);
            }
        });

        ImageView btnAddFeed = view.findViewById(R.id.btn_add_feed);

        btnAddFeed.setOnClickListener(v -> {
            // TODO: 피드 업로드 화면으로 이동
            FeedUploadFragment fragment = new FeedUploadFragment();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
    }

    private void showContent() {
        layoutLoading.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);
    } private void searchFeedList(String keyword) {

        showLoading();

        ApiService api = RetrofitClient.getAuthService();
        api.searchFeeds(keyword, 0, 20)
                .enqueue(new Callback<FeedListResponse>() {
                    @Override
                    public void onResponse(Call<FeedListResponse> call, Response<FeedListResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("SEARCH_API", "errorBody=" + response.errorBody());
                            showContent();
                            return;
                        }

                        List<FeedDetailResponse> result = response.body().getFeeds();
                        if (result == null) {
                            showContent();
                            return;
                        }

                        feedItems.clear();

                        for (FeedDetailResponse feed : result) {
                            String thumbnail = null;
                            if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
                                thumbnail = feed.getMediaList().get(0).getMediaUrl();
                            }

                            feedItems.add(new FeedItem(feed.getFeedId(), thumbnail));
                        }

                        adapter.notifyDataSetChanged();
                        showContent();
                    }

                    @Override
                    public void onFailure(Call<FeedListResponse> call, Throwable t) {
                        Log.e("SEARCH_API", "fail : " + t.getMessage());
                        showContent();
                    }
                });
    }

    private void loadFeedList() {

        showLoading();

        ApiService api = RetrofitClient.getAuthService();
        api.getFeedList("NEW", 0, 20)
                .enqueue(new Callback<FeedListResponse>() {
                    @Override
                    public void onResponse(Call<FeedListResponse> call, Response<FeedListResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            showContent();
                            return;
                        }

                        List<FeedDetailResponse> serverList = response.body().getFeeds();
                        if (serverList == null) {
                            showContent();
                            return;
                        }

                        feedItems.clear();

                        for (FeedDetailResponse feed : serverList) {
                            String thumbnail = null;
                            if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
                                thumbnail = feed.getMediaList().get(0).getMediaUrl();
                            }

                            feedItems.add(new FeedItem(feed.getFeedId(), thumbnail));
                        }

                        adapter.notifyDataSetChanged();
                        showContent();
                    }

                    @Override
                    public void onFailure(Call<FeedListResponse> call, Throwable t) {
                        Log.e("FEED_API", "FAILURE: " + t.getMessage());
                        showContent();
                    }
                });
    }

}
