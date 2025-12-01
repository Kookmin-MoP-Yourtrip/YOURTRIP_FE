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
import com.example.yourtrip.model.FeedMediaDetailResponse;

import java.util.ArrayList;
import java.util.List;
public class UploadFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FIRST = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ADD_BUTTON = 2;

    // ⭐ 기존 이미지(id + url) 리스트
    private List<FeedMediaDetailResponse> originalImages = new ArrayList<>();

    // ⭐ 새로 추가한 이미지 URI
    private List<Uri> newImageUris = new ArrayList<>();

    private final OnUploadClickListener listener;

    public interface OnUploadClickListener {
        void onAddPhotoClick();
        void onDeletePhotoClick(int position);
    }

    // 업로드 화면용 (새 이미지만)
    public UploadFeedAdapter(List<Uri> newImageUris,
                             OnUploadClickListener listener) {
        this.newImageUris = newImageUris;
        this.listener = listener;
    }

    // 수정 화면용 호출
    public void setEditMode(List<FeedMediaDetailResponse> original, List<Uri> news) {
        this.originalImages = original;
        this.newImageUris = news;
    }

    @Override
    public int getItemCount() {
        int total = originalImages.size() + newImageUris.size();

        if (total == 0) return 1;         // 최초 추가 버튼
        if (total < 5) return total + 1;  // 전체 + 추가 버튼
        return total;                     // 최대 5장
    }

    @Override
    public int getItemViewType(int position) {
        int total = originalImages.size() + newImageUris.size();

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

        if (position < originalImages.size()) {
            // 기존 이미지 (서버 URL)
            Glide.with(vh.itemView.getContext())
                    .load(originalImages.get(position).getMediaUrl())
                    .centerCrop()
                    .into(vh.imageView);

        } else {
            // 새로 추가된 이미지
            Uri uri = newImageUris.get(position - originalImages.size());
            Glide.with(vh.itemView.getContext())
                    .load(uri)
                    .centerCrop()
                    .into(vh.imageView);
        }

        vh.btnDelete.setOnClickListener(
                v -> listener.onDeletePhotoClick(position)
        );
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

    static class FirstViewHolder extends RecyclerView.ViewHolder {
        public FirstViewHolder(View v) { super(v); }
    }

    static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        public AddButtonViewHolder(View v) { super(v); }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, btnDelete;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_selected);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

