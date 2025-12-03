package com.example.yourtrip.feed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.NotLoggedInActivity;
import com.example.yourtrip.model.FeedDetailResponse;
import com.example.yourtrip.model.FeedListResponse;
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
    private EditText etSearch;
    private ImageView btnSearch;

    private TextView btnSort, sortLatest, sortPopular;
    private View sortMenu;
    private String currentSort = "POPULAR";   // 기본값: 최신순


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_main, container, false);

        rvFeed = view.findViewById(R.id.rv_feed);

        btnSort = view.findViewById(R.id.btnSort);
        sortMenu = view.findViewById(R.id.sortMenu);
        sortLatest = view.findViewById(R.id.sortLatest);
        sortPopular = view.findViewById(R.id.sortPopular);

        // 정렬 버튼: 메뉴 토글
        btnSort.setOnClickListener(v -> {
            if (sortMenu.getVisibility() == View.VISIBLE) {
                sortMenu.setVisibility(View.GONE);
            } else {
                sortMenu.setVisibility(View.VISIBLE);
            }
        });

        // 최신순
        sortLatest.setOnClickListener(v -> {
            sortMenu.setVisibility(View.GONE);
            currentSort = "NEW";     // 🔥 최신순 정렬 파라미터
            loadFeedList();
        });

        // 인기순
        sortPopular.setOnClickListener(v -> {
            sortMenu.setVisibility(View.GONE);
            currentSort = "POPULAR"; // 🔥 인기순 정렬 파라미터
            loadFeedList();
        });


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

            // 로그인 여부 확인
            if (!isLoggedIn()) {
                Intent intent = new Intent(getContext(), NotLoggedInActivity.class);
                startActivity(intent);
                return;
            }

            // 로그인 상태 → 기존처럼 피드 업로드 화면으로 이동
            FeedUploadFragment fragment = new FeedUploadFragment();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }
    private boolean isLoggedIn() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        return token != null;
    }



    private void searchFeedList(String keyword) {

        ApiService api = RetrofitClient.getAuthService(getContext());

        api.searchFeeds(keyword, 0, 20)
                .enqueue(new Callback<FeedListResponse>() {
                    @Override
                    public void onResponse(Call<FeedListResponse> call, Response<FeedListResponse> response) {

                        Log.d("SEARCH_API", "code = " + response.code());

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("SEARCH_API", "errorBody=" + response.errorBody());
                            return;
                        }

                        List<FeedDetailResponse> result = response.body().getFeeds();

                        if (result == null) {
                            Log.e("SEARCH_API", "result list is NULL");
                            return;
                        }

                        feedItems.clear();

                        for (FeedDetailResponse feed : result) {

                            // 이미지 URL 추출
                            String thumbnail = null;
                            if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
                                String url = feed.getMediaList().get(0).getMediaUrl();
                                if (url != null) thumbnail = url;
                            }

                            feedItems.add(new FeedItem(
                                    feed.getFeedId(),   // int OK
                                    thumbnail
                            ));
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<FeedListResponse> call, Throwable t) {
                        Log.e("SEARCH_API", "fail : " + t.getMessage());
                    }
                });
    }


    private void loadFeedList() {

        ApiService api = RetrofitClient.getAuthService(getContext());

        api.getFeedList(currentSort, 0, 20)
                .enqueue(new Callback<FeedListResponse>() {
                    @Override
                    public void onResponse(Call<FeedListResponse> call, Response<FeedListResponse> response) {

                        Log.d("FEED_API", "Response code: " + response.code());

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("FEED_API", "ERROR: " + response.code());
                            return;
                        }

                        Log.d("FEED_API", "body: " + new Gson().toJson(response.body()));

                        // 🔥 타입 수정 (가장 중요한 부분)
                        List<FeedDetailResponse> serverList = response.body().getFeeds();

                        if (serverList == null) {
                            Log.e("FEED_API", "serverList is NULL");
                            return;
                        }

                        feedItems.clear();

                        for (FeedDetailResponse feed : serverList) {

                            String thumbnail = null;

                            if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
                                thumbnail = feed.getMediaList().get(0).getMediaUrl();
                            }

                            feedItems.add(new FeedItem(
                                    feed.getFeedId(),   // int OK
                                    thumbnail
                            ));
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<FeedListResponse> call, Throwable t) {
                        Log.e("FEED_API", "FAILURE: " + t.getMessage());
                    }
                });
    }

}
