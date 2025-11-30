package com.example.yourtrip.mypage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.feed.FeedAdapter;
import com.example.yourtrip.feed.FeedDetailFragment;
import com.example.yourtrip.feed.FeedItem;
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

public class MyPersonalFeedFragment extends Fragment {

    private RecyclerView rvFeed;
    private FeedAdapter adapter;
    private List<FeedItem> feedItems = new ArrayList<>();
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_personal_feed, container, false);

        // 뒤로가기 버튼 설정
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).switchFragment(new MypageFragment(), false);
        });

        // SharedPreferences에서 userId 가져오기
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        rvFeed = view.findViewById(R.id.rv_my_feed);

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

        // 내 피드 로드
        if (userId != -1) {
            loadMyFeeds();
        } else {
            Log.e("MY_FEED", "User not logged in");
        }

        return view;
    }

    private void loadMyFeeds() {
        ApiService api = RetrofitClient.getAuthService(getContext());

        api.getUserFeeds(userId, 0, 20)
                .enqueue(new Callback<FeedListResponse>() {
                    @Override
                    public void onResponse(Call<FeedListResponse> call, Response<FeedListResponse> response) {

                        Log.d("MY_FEED_API", "Response code: " + response.code());

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("MY_FEED_API", "ERROR: " + response.code());
                            return;
                        }

                        Log.d("MY_FEED_API", "body: " + new Gson().toJson(response.body()));

                        List<FeedDetailResponse> serverList = response.body().getFeeds();

                        if (serverList == null) {
                            Log.e("MY_FEED_API", "serverList is NULL");
                            return;
                        }

                        feedItems.clear();


                        for (FeedDetailResponse feed : serverList) {

                            String thumbnail = null;

                            if (feed.getMediaList() != null && !feed.getMediaList().isEmpty()) {
                                thumbnail = feed.getMediaList().get(0).getMediaUrl();
                            }

                            feedItems.add(new FeedItem( // 수정: 변수 없이 바로 추가
                                    feed.getFeedId(),
                                    thumbnail
                            ));
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<FeedListResponse> call, Throwable t) {
                        Log.e("MY_FEED_API", "FAILURE: " + t.getMessage());
                    }
                });
    }
}
