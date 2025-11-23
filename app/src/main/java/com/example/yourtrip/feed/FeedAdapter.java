package com.example.yourtrip.feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<FeedItem> feedList;

    public FeedAdapter(List<FeedItem> feedList) {
        this.feedList = feedList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedItem item = feedList.get(position);

        // 🔹 URL 이미지 로딩 (Glide)
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .centerCrop()
                .into(holder.imgFeed);
    }


    @Override
    public int getItemCount() {
        return feedList.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFeed;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFeed = itemView.findViewById(R.id.img_feed);
        }
    }
}
