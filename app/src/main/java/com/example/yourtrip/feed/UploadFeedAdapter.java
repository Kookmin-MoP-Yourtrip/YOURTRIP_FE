package com.example.yourtrip.feed;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;

import java.util.ArrayList;
import java.util.List;
public class UploadFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FIRST = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ADD_BUTTON = 2;

    private List<String> originalImageUrls = new ArrayList<>();  // 수정 모드 전용(서버 이미지)
    private List<Uri> newImageUris = new ArrayList<>();          // 업로드 + 수정 공통

    private final OnUploadClickListener listener;

    public interface OnUploadClickListener {
        void onAddPhotoClick();
        void onDeletePhotoClick(int position);
    }

    // 🔹 업로드 화면용 생성자 (새 이미지만)
    public UploadFeedAdapter(List<Uri> newImageUris,
                             OnUploadClickListener listener) {
        this.newImageUris = newImageUris;
        this.listener = listener;
    }

    // 🔹 수정 화면용 모드 설정
    public void setEditMode(List<String> original, List<Uri> news) {
        this.originalImageUrls = original;
        this.newImageUris = news;
    }

    @Override
    public int getItemCount() {
        int total = originalImageUrls.size() + newImageUris.size();

        if (total == 0) return 1;
        if (total < 5) return total + 1;
        return total;
    }

    @Override
    public int getItemViewType(int position) {
        int total = originalImageUrls.size() + newImageUris.size();

        if (total == 0) return TYPE_FIRST;
        if (position == total && total < 5) return TYPE_ADD_BUTTON;

        return TYPE_IMAGE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FirstViewHolder || holder instanceof AddButtonViewHolder) {
            holder.itemView.setOnClickListener(v -> listener.onAddPhotoClick());
            return;
        }

        ImageViewHolder vh = (ImageViewHolder) holder;

        if (position < originalImageUrls.size()) {
            Glide.with(vh.itemView.getContext())
                    .load(originalImageUrls.get(position))
                    .centerCrop()
                    .into(vh.imageView);
        } else {
            Uri uri = newImageUris.get(position - originalImageUrls.size());
            Glide.with(vh.itemView.getContext())
                    .load(uri)
                    .centerCrop()
                    .into(vh.imageView);
        }

        vh.btnDelete.setOnClickListener(v -> listener.onDeletePhotoClick(position));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_FIRST)
            return new FirstViewHolder(inflater.inflate(R.layout.item_add_image_first, parent, false));
        else if (viewType == TYPE_ADD_BUTTON)
            return new AddButtonViewHolder(inflater.inflate(R.layout.item_add_image_button, parent, false));
        else
            return new ImageViewHolder(inflater.inflate(R.layout.item_feed_upload_image, parent, false));
    }

    static class FirstViewHolder extends RecyclerView.ViewHolder { public FirstViewHolder(View v) { super(v); } }
    static class AddButtonViewHolder extends RecyclerView.ViewHolder { public AddButtonViewHolder(View v) { super(v); } }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, btnDelete;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_selected);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

