package com.example.yourtrip.feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;
import com.example.yourtrip.model.FeedDetailResponse;
import com.example.yourtrip.model.FeedLikeResponse;
import com.example.yourtrip.model.FeedMediaDetailResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedDetailFragment extends Fragment {

    private ImageView btnBack, btnLike, btnChat, imgProfile;
    private TextView tvNickname, tvCaption, tvLocation;
    private View tagLocation;

    private ViewPager2 vpPhotos;

    private int feedId;
    private View layoutLoading;
    private View layoutContent;
    private boolean isLiked = false;

    private int loginUserId;   // 로그인한 사용자
    private int writerId;      // 피드 작성자 (서버 응답에서 받음)

    private ImageView btnMore;
    private View moreMenu;
    private View btnEdit, btnDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences prefs =
                requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        loginUserId = prefs.getInt("userId", -1);


        View view = inflater.inflate(R.layout.fragment_feed_detail, container, false);

        initViews(view);

        if (getArguments() != null) {
            feedId = getArguments().getInt("feedId", -1);
        }

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnChat.setOnClickListener(v -> {
            Fragment chat = new FeedChatFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("feedId", feedId);
            chat.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, chat)
                    .addToBackStack(null)
                    .commit();
        });

        btnMore.setOnClickListener(v -> {
            if (moreMenu.getVisibility() == View.VISIBLE) {
                moreMenu.setVisibility(View.GONE);
            } else {
                moreMenu.setVisibility(View.VISIBLE);
            }
        });

        btnEdit.setOnClickListener(v -> {

            Fragment editFragment = new FeedEditFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("feedId", feedId);
            editFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, editFragment)
                    .addToBackStack(null)
                    .commit();

            // 더보기 메뉴 닫기
            moreMenu.setVisibility(View.GONE);
        });

        btnDelete.setOnClickListener(v -> {
            moreMenu.setVisibility(View.GONE); // 더보기 메뉴 닫기
            deleteFeed();
        });



        btnLike = view.findViewById(R.id.btn_like);

        btnLike.setOnClickListener(v -> toggleLikeAPI());

        loadFeedDetail();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnLike = view.findViewById(R.id.btn_like);
        btnChat = view.findViewById(R.id.btn_chat);

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvCaption = view.findViewById(R.id.tv_caption);
        tvLocation = view.findViewById(R.id.tv_location);

        imgProfile = view.findViewById(R.id.img_profile);
        tagLocation = view.findViewById(R.id.tag_location);

        vpPhotos = view.findViewById(R.id.vp_photos);

        layoutLoading = view.findViewById(R.id.loadingLayout_feed_detail);
        layoutContent = view.findViewById(R.id.contentLayout_feed_detail);


        // ⭐ 더보기 관련
        btnMore = view.findViewById(R.id.feed_detail_btn_more);
        moreMenu = view.findViewById(R.id.feed_more_menu);
        btnEdit = view.findViewById(R.id.btn_feed_edit);
        btnDelete = view.findViewById(R.id.btn_feed_delete);
        moreMenu.setVisibility(View.GONE);
    }

    private void loadFeedDetail() {

        layoutLoading.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);

        ApiService api = RetrofitClient.getAuthService(getContext());

        api.getFeedDetail(feedId).enqueue(new Callback<FeedDetailResponse>() {
            @Override
            public void onResponse(Call<FeedDetailResponse> call, Response<FeedDetailResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    layoutLoading.setVisibility(View.GONE);
                    return;
                }

                FeedDetailResponse data = response.body();

                writerId = data.getUserId();   // 서버에서 주는 작성자 ID

                // ⭐ 작성자와 로그인 유저가 같을 때만 더보기 버튼 표시
                if (loginUserId == writerId) {
                    btnMore.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.GONE);
                }



                // 닉네임
                tvNickname.setText(data.getNickname());

                // 프로필 이미지
                Glide.with(requireContext())
                        .load(data.getProfileImageUrl())
                        .placeholder(R.drawable.ic_default_profile)
                        .into(imgProfile);

                // 캡션
                if (data.getContent() == null || data.getContent().isEmpty()) {
                    tvCaption.setVisibility(View.GONE);
                } else {
                    tvCaption.setVisibility(View.VISIBLE);
                    tvCaption.setText(data.getContent());
                }

                // 장소
                if (data.getLocation() == null || data.getLocation().isEmpty()) {
                    tagLocation.setVisibility(View.GONE);
                } else {
                    tagLocation.setVisibility(View.VISIBLE);
                    tvLocation.setText(data.getLocation());
                }

                // 사진 리스트
                List<String> photos = new ArrayList<>();
                if (data.getMediaList() != null) {
                    for (FeedMediaDetailResponse media : data.getMediaList()) {
                        if (media.getMediaUrl() != null) {
                            photos.add(media.getMediaUrl());
                        }
                    }
                }

                // 좋아요 초기 상태 세팅
                isLiked = data.isLiked();   // ← 서버 응답의 isLiked
                if (isLiked) {
                    btnLike.setImageResource(R.drawable.ic_heart_filled);
                } else {
                    btnLike.setImageResource(R.drawable.ic_heart_default);
                }



                // ViewPager2 적용
                FeedPhotoAdapter adapter = new FeedPhotoAdapter(photos);
                vpPhotos.setAdapter(adapter);

                layoutLoading.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<FeedDetailResponse> call, Throwable t) {
                t.printStackTrace();
                layoutLoading.setVisibility(View.GONE);
            }
        });
    }
    private void animateLike(ImageView view) {
        view.animate()
                .scaleX(1.1f)   // 1 → 1.3배 확대
                .scaleY(1.1f)
                .setDuration(120)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)     // 다시 원래 크기
                                .scaleY(1f)
                                .setDuration(120)
                )
                .start();
    }

    private void toggleLikeAPI() {

        ApiService api = RetrofitClient.getAuthService(getContext());
        Call<FeedLikeResponse> call = api.toggleFeedLike(feedId);

        call.enqueue(new Callback<FeedLikeResponse>() {
            @Override
            public void onResponse(Call<FeedLikeResponse> call, Response<FeedLikeResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                FeedLikeResponse result = response.body();

                // 서버 응답 기반으로 갱신
                isLiked = result.isLiked();

                if (isLiked) {
                    btnLike.setImageResource(R.drawable.ic_heart_filled);
                    animateLike(btnLike);     // 좋아요 애니메이션
                } else {
                    btnLike.setImageResource(R.drawable.ic_heart_default);
                }

                // heartCount UI도 필요하다면 추가
                // tvHeartCount.setText(String.valueOf(result.getHeartCount()));
            }

            @Override
            public void onFailure(Call<FeedLikeResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void deleteFeed() {

        ApiService api = RetrofitClient.getAuthService(getContext());

        api.deleteFeed(feedId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 204) {     // 삭제 성공
                    requireActivity().runOnUiThread(() -> {
                        // 이전 화면으로 이동
                        requireActivity().onBackPressed();
                    });
                    return;
                }

                if (response.code() == 404) {
                    // 피드 없음
                    // 필요하면 Toast 추가
                }

                if (response.code() == 403) {
                    // 권한 없음
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}


