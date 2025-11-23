package com.example.yourtrip.feed;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

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

            // 새 댓글 추가
            chatList.add(new FeedChat(
                    null,             // 추후 서버에서 프로필 URL 넣기
                    "나",             // 추후 로그인 사용자 닉네임
                    newComment
            ));

            adapter.notifyItemInserted(chatList.size() - 1);
            rvChatList.scrollToPosition(chatList.size() - 1);

            editFeedChat.setText("");
        });
    }
}

