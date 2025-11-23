package com.example.yourtrip.feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;
import com.example.yourtrip.model.FeedChat;

import java.util.List;

public class FeedChatAdapter extends RecyclerView.Adapter<FeedChatAdapter.ChatViewHolder> {

    private List<FeedChat> chatList;

    public FeedChatAdapter(List<FeedChat> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        FeedChat item = chatList.get(position);

        holder.tvNickname.setText(item.getNickname());
        holder.tvChat.setText(item.getContent());

        Glide.with(holder.itemView.getContext())
                .load(item.getProfileUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_default_profile)
                .into(holder.imgProfile);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfile;
        TextView tvNickname, tvChat;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgChatProfile);
            tvNickname = itemView.findViewById(R.id.tvChatNickname);
            tvChat = itemView.findViewById(R.id.tv_feed_chat);
        }
    }
}
