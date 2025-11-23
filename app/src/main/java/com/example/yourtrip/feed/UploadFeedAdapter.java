package com.example.yourtrip.feed;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;

import java.util.List;

public class UploadFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FIRST = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ADD_BUTTON = 2;

    private final List<Uri> imageList;
    private final OnUploadClickListener listener;

    public interface OnUploadClickListener {
        void onAddPhotoClick();
        void onDeletePhotoClick(int position);
    }

    public UploadFeedAdapter(List<Uri> imageList, OnUploadClickListener listener) {
        this.imageList = imageList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (imageList.size() == 0)
            return TYPE_FIRST;

        // 마지막에 + 버튼 넣을지 결정
        if (position == imageList.size()) {
            return imageList.size() < 5 ? TYPE_ADD_BUTTON : TYPE_IMAGE;
        }

        return TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
        if (imageList.size() == 0)
            return 1; // 첫 추가 버튼만

        if (imageList.size() < 5)
            return imageList.size() + 1; // 이미지들 + 추가 버튼

        return imageList.size(); // 5장이면 + 없음
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_FIRST) {
            View view = inflater.inflate(R.layout.item_add_image_first, parent, false);
            return new FirstViewHolder(view);
        } else if (viewType == TYPE_ADD_BUTTON) {
            View view = inflater.inflate(R.layout.item_add_image_button, parent, false);
            return new AddButtonViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_feed_image, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FirstViewHolder) {
            holder.itemView.setOnClickListener(v -> listener.onAddPhotoClick());

        } else if (holder instanceof AddButtonViewHolder) {
            holder.itemView.setOnClickListener(v -> listener.onAddPhotoClick());

        } else if (holder instanceof ImageViewHolder) {

            Uri uri = imageList.get(position);
            ImageViewHolder vh = (ImageViewHolder) holder;

            Glide.with(vh.itemView.getContext())
                    .load(uri)
                    .centerCrop()
                    .into(vh.imageView);

            vh.btnDelete.setOnClickListener(v -> listener.onDeletePhotoClick(position));
        }
    }

    // Holder들
    static class FirstViewHolder extends RecyclerView.ViewHolder {
        public FirstViewHolder(@NonNull View itemView) { super(itemView); }
    }

    static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        public AddButtonViewHolder(@NonNull View itemView) { super(itemView); }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, btnDelete;
        FrameLayout container;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
            imageView = itemView.findViewById(R.id.img_selected);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
