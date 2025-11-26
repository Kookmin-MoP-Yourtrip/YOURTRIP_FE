package com.example.yourtrip.feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.yourtrip.R;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    public interface OnFeedClickListener {
        void onFeedClick(FeedItem item);
    }

    private List<FeedItem> feedList;
    private OnFeedClickListener listener;

    public FeedAdapter(List<FeedItem> feedList, OnFeedClickListener listener) {
        this.feedList = feedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed_card, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedItem item = feedList.get(position);

        // 이미지 로딩
//        Glide.with(holder.itemView.getContext())
//                .load(item.getImageUrl())
//                .into(holder.imgFeed);

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .thumbnail(0.01f)  // 저화질 프리뷰 먼저 띄움
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // 캐시 강하게
                .transition(DrawableTransitionOptions.withCrossFade(150))
                .skipMemoryCache(false)
                .into(holder.imgFeed);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onFeedClick(item);
        });
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
