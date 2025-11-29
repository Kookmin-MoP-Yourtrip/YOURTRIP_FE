package com.example.yourtrip.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.feed.FeedFragment;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.example.yourtrip.mytrip.MyTripListFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MypageFragment extends Fragment {

    private ImageView imgProfile;
    private TextView tvNickname;

    public static String latestProfileUrl = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // UI 요소 참조
        imgProfile = view.findViewById(R.id.imgProfile);
        tvNickname = view.findViewById(R.id.tvNickname);

        LinearLayout btnMyCourse = view.findViewById(R.id.btnMyCourse);
        LinearLayout btnMyFeed = view.findViewById(R.id.btnMyFeed);
        TextView btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // 코스 보기
        btnMyCourse.setOnClickListener(v ->
                ((MainActivity) requireActivity()).switchFragment(new MyTripListFragment(), true)
        );

        // 피드 보기
        btnMyFeed.setOnClickListener(v ->
                ((MainActivity) requireActivity()).switchFragment(new FeedFragment(), true)
        );

        // 프로필 수정
        btnEditProfile.setOnClickListener(v ->
                ((MainActivity) requireActivity()).switchFragment(new ProfileEditFragment(), true)
        );

        // 마이페이지 정보 로드
        loadProfile();

        return view;
    }

    private void loadProfile() {
        ApiService api = RetrofitClient
                .getInstance(requireContext())
                .create(ApiService.class);

        api.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                ProfileResponse p = res.body();

                // 닉네임 표시
                tvNickname.setText(p.nickname);

                String urlToLoad = latestProfileUrl != null ? latestProfileUrl : p.profileImageUrl;

                // 이미지 표시
                Glide.with(requireContext())
                        .load(urlToLoad)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .circleCrop()
                        .placeholder(R.drawable.ic_default_profile)
                        .error(R.drawable.ic_default_profile)
                        .into(imgProfile);
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) { }
        });
    }
}