package com.example.yourtrip.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.TagConverter;
import com.example.yourtrip.model.UploadCourseItem;
import com.example.yourtrip.model.UploadCourseListResponse;
import com.example.yourtrip.mytrip.upload.AfterUploadCourseDetailActivity;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeSearchResultFragment extends Fragment {

    private LinearLayout tagListContainer;
    private ArrayList<String> selectedTags;
    private String keyword;
    private String mode;

    private RecyclerView rv;
    private UploadCourseAdapter adapter;

    private TextView btnSort, sortLatest, sortPopular;
    private View sortMenu;
    private String currentSort = "POPULAR";   // 기본값: 인기순

    private static final String LOG_TAG = "SEARCH_API";  // ⭐ LOG 태그
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_search_result, container, false);

        tagListContainer = view.findViewById(R.id.tagListContainer);
        rv = view.findViewById(R.id.rvSearchResult);
        // 🔽 정렬 버튼 / 메뉴 뷰 연결
        btnSort = view.findViewById(R.id.btnSort);
        sortMenu = view.findViewById(R.id.sortMenu);
        sortLatest = view.findViewById(R.id.sortLatest);
        sortPopular = view.findViewById(R.id.sortPopular);

        // 초기 상태: 메뉴 숨김
        sortMenu.setVisibility(View.GONE);

        // 🔽 정렬 버튼 누르면 메뉴 열고 닫기
        btnSort.setOnClickListener(v -> {
            if (sortMenu.getVisibility() == View.VISIBLE) {
                sortMenu.setVisibility(View.GONE);
            } else {
                sortMenu.setVisibility(View.VISIBLE);
            }
        });

        // 🔽 최신순 클릭
        sortLatest.setOnClickListener(v -> {
            sortMenu.setVisibility(View.GONE);
            currentSort = "NEW";    // ✨ 최신순 파라미터
            loadSearchResults();
        });

        // 🔽 인기순 클릭
        sortPopular.setOnClickListener(v -> {
            sortMenu.setVisibility(View.GONE);
            currentSort = "POPULAR";   // ✨ 인기순 파라미터
            loadSearchResults();
        });


        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UploadCourseAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(getContext(), AfterUploadCourseDetailActivity.class);
            intent.putExtra("uploadCourseId", item.uploadCourseId);  // ★★ 반드시 이 키 사용!
            startActivity(intent);
        });


        // ⭐ 전달받은 검색 데이터 처리
        Bundle args = getArguments();
        mode = "search";
        if (args != null) {
            mode = args.getString("mode", "search");
            keyword = args.getString("keyword", "");
            selectedTags = args.getStringArrayList("tags");
        }

        // 검색창 처리
        EditText etSearch = view.findViewById(R.id.tvSearch);
        etSearch.setText(keyword);
        etSearch.setFocusable(false);
        etSearch.setClickable(true);
        etSearch.setOnClickListener(v -> goToSearchPage());

        // 태그 표시
        if (selectedTags != null) addSelectedTagsToContainer();

        // ⭐ API 호출
        loadSearchResults();

        return view;
    }

    // ================================
    // 🔥 API 호출 + 로그 출력
    // ================================
    private void loadSearchResults() {

        ApiService api = RetrofitClient.getInstance(getContext()).create(ApiService.class);

        String sendKeyword = (keyword == null || keyword.isEmpty()) ? null : keyword;
        List<String> sendTags = TagConverter.toServerCodes(selectedTags);

        // ⭐ 요청 전 LOG 출력
        Log.d(LOG_TAG, "--------------------------------------------");
        Log.d(LOG_TAG, "📌 검색 API 요청");
        Log.d(LOG_TAG, "mode        = " + mode);
        Log.d(LOG_TAG, "keyword     = " + sendKeyword);
        Log.d(LOG_TAG, "uiTags      = " + selectedTags);
        Log.d(LOG_TAG, "serverTags  = " + sendTags);
        Log.d(LOG_TAG, "sort        = POPULAR");
        Log.d(LOG_TAG, "--------------------------------------------");

        api.getUploadCourses(sendKeyword, sendTags, currentSort)
                .enqueue(new Callback<UploadCourseListResponse>() {
                    @Override
                    public void onResponse(Call<UploadCourseListResponse> call,
                                           Response<UploadCourseListResponse> response) {

                        if (!response.isSuccessful()) {
                            Log.e(LOG_TAG, "❌ 응답 실패 - code=" + response.code());
                            return;
                        }

                        if (response.body() == null) {
                            Log.e(LOG_TAG, "❌ 응답 body = null");
                            return;
                        }

                        List<UploadCourseItem> list = response.body().uploadCourses;

                        // ⭐ 응답 LOG 출력
                        Log.d(LOG_TAG, "✅ 검색 결과 성공");
                        Log.d(LOG_TAG, "결과 개수 = " + list.size());

                        for (UploadCourseItem item : list) {
                            Log.d(LOG_TAG, "• " + item.title + " | " + item.location);
                        }

                        adapter.setItems(list);
                    }

                    @Override
                    public void onFailure(Call<UploadCourseListResponse> call, Throwable t) {
                        Log.e(LOG_TAG, "❌ onFailure: " + t.getMessage());
                    }
                });
    }


    // ================================
    // 🔥 기존 태그 UI 그대로 유지
    // ================================
    private void addSelectedTagsToContainer() {

        tagListContainer.removeAllViews();

        for (String tag : selectedTags) {
            TextView tv = new TextView(requireContext(), null, 0, getStyleForTag(tag));
            tv.setText(tag);
            tv.setPadding(20, 10, 20, 10);
            tagListContainer.addView(tv);
        }
    }

    // ⭐ 기존 코드 그대로 유지
    private int getStyleForTag(String tag) {
        if (tag.equals("뚜벅이") || tag.equals("자차")) return R.style.Tag_Movetype;
        if (tag.equals("혼자") || tag.equals("연인")|| tag.equals("친구")|| tag.equals("가족")) return R.style.Tag_Partner;
        if (tag.equals("하루") || tag.equals("1박 2일")|| tag.equals("주말")|| tag.equals("장기")) return R.style.Tag_Period;
        if (tag.equals("힐링") || tag.equals("액티비티")|| tag.equals("맛집탐방")|| tag.equals("감성")|| tag.equals("문화/전시")
                || tag.equals("자연")|| tag.equals("쇼핑")) return R.style.Tag_Theme;
        if (tag.equals("가성비") || tag.equals("프리미엄")|| tag.equals("보통")) return R.style.Tag_Budget;

        return R.style.Tag;
    }

    private void goToSearchPage() {
        Fragment fragment = new HomeSearchFragment();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
