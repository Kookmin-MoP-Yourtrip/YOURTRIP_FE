package com.example.yourtrip.feed;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        layoutLoading = view.findViewById(R.id.loadingLayout);
        layoutContent = view.findViewById(R.id.contentLayout);

    }

    private void loadFeedDetail() {

        layoutLoading.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);

        ApiService api = RetrofitClient.getAuthService();

        api.getFeedDetail(feedId).enqueue(new Callback<FeedDetailResponse>() {
            @Override
            public void onResponse(Call<FeedDetailResponse> call, Response<FeedDetailResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    layoutLoading.setVisibility(View.GONE);
                    return;
                }

                FeedDetailResponse data = response.body();


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

}


