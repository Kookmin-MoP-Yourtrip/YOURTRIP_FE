package com.example.yourtrip.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.TagColorManager;
import com.example.yourtrip.model.UploadCourseItem;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class UploadCourseAdapter extends RecyclerView.Adapter<UploadCourseAdapter.ViewHolder> {

    private List<UploadCourseItem> itemList = new ArrayList<>();
    private Context context;

    // 클릭 이벤트
    public interface OnItemClickListener {
        void onClick(UploadCourseItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public UploadCourseAdapter(List<UploadCourseItem> list) {
        this.itemList = list;
    }

    public void setItems(List<UploadCourseItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UploadCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_upload_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadCourseAdapter.ViewHolder holder, int position) {
        UploadCourseItem item = itemList.get(position);

        // ✔ 썸네일 이미지 URL 로드
        Glide.with(context)
                .load(item.thumbnailImageUrl)
                .placeholder(R.drawable.ic_loading)  // 기본 이미지
                .into(holder.imgThumbnail);

        holder.tvLocation.setText(item.location);
        holder.tvTitle.setText(item.title);
        holder.tvLikeCount.setText(String.valueOf(item.forkCount));

        // 태그 목록
        holder.tagContainer.removeAllViews();
        if (item.keywords != null) {
            for (String tag : item.keywords) {
                View tagView = LayoutInflater.from(context)
                        .inflate(R.layout.item_tag_for_list, holder.tagContainer, false);

                TextView tv = tagView.findViewById(R.id.tv_theme_tag);
                tv.setText(tag);

                TagColorManager.TagStyle style = TagColorManager.get(tag);
                tv.setTextColor(context.getColor(style.textColor));
                tv.setBackgroundTintList(context.getColorStateList(style.tintColor));

                holder.tagContainer.addView(tagView);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvLocation;
        TextView tvTitle;
        TextView tvLikeCount;
        FlexboxLayout tagContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.imgUploadCourseThumbnail);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvTitle = itemView.findViewById(R.id.tv_date);
            tvLikeCount = itemView.findViewById(R.id.tv_forkCount);
            tagContainer = itemView.findViewById(R.id.tagContainer);
        }
    }
}
