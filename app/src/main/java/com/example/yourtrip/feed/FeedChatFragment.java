package com.example.yourtrip.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.FeedChat;
import com.example.yourtrip.model.FeedCommentDetailResponse;
import com.example.yourtrip.model.FeedCommentListResponse;
import com.example.yourtrip.model.FeedCommentWriteRequest;
import com.example.yourtrip.model.FeedCommentWriteResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedChatFragment extends Fragment {

    private ImageView btnBack;
    private RecyclerView rvChatList;
    private EditText editFeedChat;
    private TextView btnChatSend;
    private View loadingLayout;      // ⭐ 추가
    private View contentLayout;      // ⭐ 추가
    private List<FeedChat> chatList = new ArrayList<>();
    private FeedChatAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_chat, container, false);

        btnBack = view.findViewById(R.id.btn_back);
        rvChatList = view.findViewById(R.id.rvChatList);
        editFeedChat = view.findViewById(R.id.editFeedChat);
        btnChatSend = view.findViewById(R.id.btnChatSend);

        // ⭐ 스켈레톤/콘텐츠 레이아웃 찾기
        loadingLayout = view.findViewById(R.id.loadingLayout_feed_chat);
        contentLayout = view.findViewById(R.id.contentLayout_feed_chat);

        initRecyclerView();
        setListeners();


        int feedId = getArguments().getInt("feedId", -1);
        if (feedId != -1) {
            loadComments(feedId);
        }


        return view;
    }

    private void initRecyclerView() {
        adapter = new FeedChatAdapter(chatList);
        rvChatList.setAdapter(adapter);
        rvChatList.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter.notifyDataSetChanged();
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnChatSend.setOnClickListener(v -> {
            String newComment = editFeedChat.getText().toString().trim();
            if (newComment.isEmpty()) return;

            int feedId = getArguments().getInt("feedId", -1);
            if (feedId == -1) return;

//            ApiService api = RetrofitClient.getAuthService();
            ApiService api = RetrofitClient.getAuthService(getContext()); // 인증있어야함

            FeedCommentWriteRequest request = new FeedCommentWriteRequest(newComment);

            api.writeComment(feedId, request)
                    .enqueue(new Callback<FeedCommentWriteResponse>() {
                        @Override
                        public void onResponse(Call<FeedCommentWriteResponse> call,
                                               Response<FeedCommentWriteResponse> response) {

                            if (response.isSuccessful()) {

                                // UI도 업데이트
                                chatList.add(new FeedChat(
                                        null,
                                        "나",
                                        newComment
                                ));

                                adapter.notifyItemInserted(chatList.size() - 1);
                                rvChatList.scrollToPosition(chatList.size() - 1);

                                editFeedChat.setText("");

                                // 다시 조회해도 사라지지 않음
                            }
                        }

                        @Override
                        public void onFailure(Call<FeedCommentWriteResponse> call, Throwable t) {
                            Log.e("FEED_CHAT", "댓글 등록 실패: " + t.getMessage());
                        }
                    });
        });

    }

    private void loadComments(int feedId) {
        // ⭐ 스켈레톤 ON
        loadingLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

//        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        ApiService api = RetrofitClient.getInstance((getContext())).create(ApiService.class);

        api.getFeedComments(feedId, 0, 20)
                .enqueue(new Callback<FeedCommentListResponse>() {
                    @Override
                    public void onResponse(Call<FeedCommentListResponse> call,
                                           Response<FeedCommentListResponse> response) {

                        // ⭐ 스켈레톤 OFF → 실제 UI ON
                        loadingLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<FeedCommentDetailResponse> serverComments = response.body().getComments();

                            chatList.clear();

                            for (FeedCommentDetailResponse c : serverComments) {
                                chatList.add(new FeedChat(
                                        c.getProfileImageUrl(),
                                        c.getNickname(),
                                        c.getSentence()
                                ));
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<FeedCommentListResponse> call, Throwable t) {
                        Log.e("FEED_CHAT", "댓글 조회 실패: " + t.getMessage());
                        // 실패해도 스켈레톤 OFF
                        loadingLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

}

