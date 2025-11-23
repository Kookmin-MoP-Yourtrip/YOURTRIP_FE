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

        // 🔥 테스트 더미 데이터 (API 붙이기 전)
        chatList.add(new FeedChat(null, "이원희", "와!! 카페가 너무 예뻐요. 정보 알려주시면 안 될까요?? ㅠ"));
        chatList.add(new FeedChat(null, "민주예요", "샌드위치 맛있겠네요ㅎㅎ"));
        chatList.add(new FeedChat(null, "머리고쿨", "여러분 여기 연남동 샌드샌드입니다"));

        adapter.notifyDataSetChanged();
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnChatSend.setOnClickListener(v -> {
            String newComment = editFeedChat.getText().toString().trim();
            if (newComment.isEmpty()) return;

            int feedId = getArguments().getInt("feedId", -1);
            if (feedId == -1) return;

            ApiService api = RetrofitClient.getAuthService(); // 인증있어야함

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
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.getFeedComments(feedId, 0, 20)
                .enqueue(new Callback<FeedCommentListResponse>() {
                    @Override
                    public void onResponse(Call<FeedCommentListResponse> call,
                                           Response<FeedCommentListResponse> response) {

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
                    }
                });
    }

}

