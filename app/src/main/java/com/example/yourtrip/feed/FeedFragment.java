package com.example.yourtrip.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView rvFeed;
    private FeedAdapter adapter;
    private List<FeedItem> feedItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_main, container, false);

        rvFeed = view.findViewById(R.id.rv_feed);

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

        // ---------- 더미 1개만 넣기 ----------
        feedItems.clear();
        feedItems.add(new FeedItem(1, "https://picsum.photos/300"));
        adapter.notifyDataSetChanged();
        // -----------------------------------

        return view;
    }
}
