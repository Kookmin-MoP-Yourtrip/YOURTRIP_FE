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
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.example.yourtrip.feed.FeedFragment;
import com.example.yourtrip.mytrip.list.MyTripListFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MypageFragment extends Fragment {

    private ImageView imgProfile;
    private TextView tvNickname;

    private View loadingLayout;
    private View contentLayout;

    public static String latestProfileUrl = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        tvNickname = view.findViewById(R.id.tvNickname);

        loadingLayout = view.findViewById(R.id.loadingLayout_mypage);
        contentLayout = view.findViewById(R.id.contentLayout_mypage);

        LinearLayout btnMyCourse = view.findViewById(R.id.btnMyCourse);
        LinearLayout btnMyFeed = view.findViewById(R.id.btnMyFeed);
        TextView btnEdit = view.findViewById(R.id.btnEditProfile);

        // 초기 상태는 스켈레톤만
        loadingLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        btnMyCourse.setOnClickListener(v ->
                ((MainActivity) requireActivity()).switchFragment(new MyTripListFragment(), true)
        );

        btnMyFeed.setOnClickListener(v ->
                ((MainActivity) requireActivity()).switchFragment(new FeedFragment(), true)
        );

        btnEdit.setOnClickListener(v ->
                ((MainActivity) requireActivity()).switchFragment(new ProfileEditFragment(), true)
        );

        loadProfile();

        return view;
    }

    private void loadProfile() {

        ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);

        api.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> res) {

                if (!res.isSuccessful() || res.body() == null) {
                    loadingLayout.setVisibility(View.GONE);
                    return;
                }

                ProfileResponse p = res.body();

                tvNickname.setText(p.nickname);

                String url = latestProfileUrl != null ? latestProfileUrl : p.profileImageUrl;

                Glide.with(requireContext())
                        .load(url)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .circleCrop()
                        .into(imgProfile);

                // 최종 상태 전환
                loadingLayout.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }
}