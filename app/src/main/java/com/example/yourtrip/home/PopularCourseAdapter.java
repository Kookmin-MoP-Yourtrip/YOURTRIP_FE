package com.example.yourtrip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.TagColorManager;
import com.example.yourtrip.model.HomePopularCourseItem;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class PopularCourseAdapter extends RecyclerView.Adapter<PopularCourseAdapter.ViewHolder> {

    private List<HomePopularCourseItem> itemList = new ArrayList<>();
    private Context context;

    // 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onClick(HomePopularCourseItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // 생성자
    public PopularCourseAdapter(List<HomePopularCourseItem> list) {
        this.itemList = list;
    }

    // 리스트 갱신
    public void updateList(List<HomePopularCourseItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularCourseAdapter.ViewHolder holder, int position) {
        HomePopularCourseItem item = itemList.get(position);

        // 이미지 (리소스)
        holder.imgThumbnail.setImageResource(item.imageRes);

        // 지역
        holder.tvLocation.setText(item.location);

        // 제목
        holder.tvTitle.setText(item.title);

        // 좋아요 수
        holder.tvLikeCount.setText(String.valueOf(item.likeCount));

        // 태그 표시 (FlexboxLayout)
        holder.tagContainer.removeAllViews();
        for (String tag : item.tags) {

            View tagView = LayoutInflater.from(context)
                    .inflate(R.layout.item_tag, holder.tagContainer, false);

            TextView tv = tagView.findViewById(R.id.tv_theme_tag);
            tv.setText(tag);

            // ⭐ 여기서 TagColorManager로 스타일 가져오기
            TagColorManager.TagStyle style = TagColorManager.get(tag);

            // 텍스트 색상 적용
            tv.setTextColor(context.getColor(style.textColor));

            // 배경 tint 적용
            tv.setBackgroundTintList(
                    context.getColorStateList(style.tintColor)
            );

            holder.tagContainer.addView(tagView);
        }

        // 클릭 이벤트
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

            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvTitle = itemView.findViewById(R.id.tv_date); // XML id 유지
            tvLikeCount = itemView.findViewById(R.id.tv_forkCount);
            tagContainer = itemView.findViewById(R.id.tagContainer);
        }
    }
}
