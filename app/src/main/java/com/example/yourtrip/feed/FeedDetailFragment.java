package com.example.yourtrip.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;

public class FeedDetailFragment extends Fragment {

    private ImageView btnBack, btnLike, btnChat, imgProfile;
    private TextView tvNickname, tvCaption, tvLocation;
    private RecyclerView rvPhotos;
    private View tagLocation;

    private int feedId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_detail, container, false);

        initViews(view);

        // feedId 받기
        if (getArguments() != null) {
            feedId = getArguments().getInt("feedId", -1);
        }

        // 뒤로가기
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // 댓글 페이지 이동
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
        rvPhotos = view.findViewById(R.id.rv_photos);
        tagLocation = view.findViewById(R.id.tag_location);
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
    }
}
