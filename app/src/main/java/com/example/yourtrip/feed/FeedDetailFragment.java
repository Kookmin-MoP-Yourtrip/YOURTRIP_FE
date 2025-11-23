package com.example.yourtrip.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.yourtrip.R;

import java.util.ArrayList;
import java.util.List;

public class FeedDetailFragment extends Fragment {

    private ImageView btnBack, btnLike, btnChat, imgProfile;
    private TextView tvNickname, tvCaption, tvLocation;
    private View tagLocation;

    private ViewPager2 vpPhotos;

    private int feedId;

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
    }

    private void loadFeedDetail() {
        tvNickname.setText("혜원");
        tvCaption.setText("여기는 피드 상세 캡션입니다!");

        String place = "성수동";
        if (place == null || place.isEmpty()) {
            tagLocation.setVisibility(View.GONE);
        } else {
            tagLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(place);
        }

        // 사진 더미
        List<String> photos = new ArrayList<>();
        photos.add("https://picsum.photos/300/500");
        photos.add("https://picsum.photos/800/300");
        photos.add("https://picsum.photos/600/600");
        photos.add("https://picsum.photos/500/800");

        FeedPhotoAdapter adapter = new FeedPhotoAdapter(photos);
        vpPhotos.setAdapter(adapter);
    }
}


