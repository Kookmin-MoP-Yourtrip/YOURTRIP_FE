package com.example.yourtrip.feed;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.create_direct.PlaceSearchAdapter;
import com.example.yourtrip.mytrip.create_direct.PlaceSearchManager;
import com.example.yourtrip.network.NaverSearchResponse;

import java.util.List;

public class FeedAddLocationFragment extends Fragment {

    private EditText etPlaceName;
    private ImageView btnSearch;
    private Button btnAddLocation;
    private TextView tvEmptyResult;   // 🔹 추가


    private RecyclerView rvPlaces;
    private PlaceSearchAdapter adapter;
    private TextView tvTotalCount;

    private NaverSearchResponse.Item selectedPlace = null;

    // ⭐ 버튼 상태 변화 추적
    private boolean lastEnabledState = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_feed_add_location, container, false);

        initViews(view);
        initRecyclerView();

        // 검색 버튼 클릭
        btnSearch.setOnClickListener(v -> doSearch());
        btnAddLocation.setOnClickListener(v -> sendSelectedLocation());

        // 취소하기 버튼
        view.findViewById(R.id.btn_feed_cancel_location)
                .setOnClickListener(v -> requireActivity().onBackPressed());


        // ⭐ 검색창 입력 감지 → 버튼 활성화 처리
        etPlaceName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearchButtonState();
            }
        });

        etPlaceName.setOnEditorActionListener((v, actionId, event) -> {
            // 엔터 또는 Search 액션 눌렀을 때
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {

                if (btnSearch.isEnabled()) {
                    btnSearch.performClick();   // 🔹 검색 버튼 자동 클릭
                }
                return true;   // 기본 엔터 동작 막기
            }
            return false;
        });


        // ⭐ 최초 비활성화
        btnSearch.setEnabled(false);
        btnSearch.setColorFilter(
                getResources().getColor(R.color.gray_300),
                android.graphics.PorterDuff.Mode.SRC_IN
        );

        return view;
    }

    private void initViews(View view) {
        etPlaceName = view.findViewById(R.id.etPlaceName);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnAddLocation = view.findViewById(R.id.btn_feed_add_location);

        rvPlaces = view.findViewById(R.id.rvPlaces);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvEmptyResult = view.findViewById(R.id.tvEmptyResult);   // 🔹 추가

        btnAddLocation.setEnabled(false);
    }


    private void initRecyclerView() {
        rvPlaces.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PlaceSearchAdapter();
        rvPlaces.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            selectedPlace = item;
            btnAddLocation.setEnabled(true);
        });
    }


    // ⭐ 검색창 입력 → 버튼 활성/비활성 + 애니메이션
    private void updateSearchButtonState() {
        boolean hasKeyword = !etPlaceName.getText().toString().trim().isEmpty();
        boolean enable = hasKeyword;

        // 변화 없으면 처리 안 함
        if (enable == lastEnabledState) return;

        btnSearch.setEnabled(enable);
        lastEnabledState = enable;

        if (!enable) {
            // 비활성 모드
            btnSearch.setColorFilter(
                    getResources().getColor(R.color.gray_300),
                    android.graphics.PorterDuff.Mode.SRC_IN
            );
            // 크기 초기화
            btnSearch.setScaleX(1f);
            btnSearch.setScaleY(1f);
            return;
        }

        // ⭐ 활성화 모드: 파란색 + 확대 애니메이션
        btnSearch.setColorFilter(
                getResources().getColor(R.color.blue_main),
                android.graphics.PorterDuff.Mode.SRC_IN
        );

        btnSearch.animate()
                .scaleX(1.18f)
                .scaleY(1.18f)
                .setDuration(130)
                .withEndAction(() ->
                        btnSearch.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(130)
                );
    }


    private void doSearch() {
        String query = etPlaceName.getText().toString().trim();
        if (query.isEmpty()) return;

        PlaceSearchManager manager = new PlaceSearchManager(requireContext());

        manager.searchPlaces(query, new PlaceSearchManager.PlaceSearchListener() {

            @Override
            public void onSuccess(List<NaverSearchResponse.Item> items, int total) {
                tvEmptyResult.setVisibility(View.GONE);   // 🔹 추가

                tvTotalCount.setText("총 " + total + "개의 검색 결과");
                tvTotalCount.setVisibility(View.VISIBLE);

                adapter.setItems(items);
                rvPlaces.setVisibility(View.VISIBLE);

                selectedPlace = null;
                btnAddLocation.setEnabled(false);
            }


            @Override
            public void onEmpty() {
                tvTotalCount.setVisibility(View.GONE);
                rvPlaces.setVisibility(View.GONE);

                // 🔹 "검색 결과가 없습니다" 문구 표시
                tvEmptyResult.setVisibility(View.VISIBLE);

                selectedPlace = null;
                btnAddLocation.setEnabled(false);
            }


            @Override
            public void onFailure(String errorMessage) {}
        });
    }


    private void sendSelectedLocation() {
        if (selectedPlace == null) return;

        String title = Html.fromHtml(
                selectedPlace.title,
                Html.FROM_HTML_MODE_LEGACY
        ).toString();

        Bundle result = new Bundle();
        result.putString("selected_location", title);
        result.putString("selected_address", selectedPlace.address);
        result.putString("selected_link", selectedPlace.link);
        result.putString("selected_lat", selectedPlace.mapy);
        result.putString("selected_lng", selectedPlace.mapx);

        getParentFragmentManager().setFragmentResult("location_request", result);
        requireActivity().onBackPressed();
    }


    @Override
    public void onResume() {
        super.onResume();
        View bottomNav = requireActivity().findViewById(R.id.bottomNav);
        if (bottomNav != null) bottomNav.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        View bottomNav = requireActivity().findViewById(R.id.bottomNav);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
    }
}
