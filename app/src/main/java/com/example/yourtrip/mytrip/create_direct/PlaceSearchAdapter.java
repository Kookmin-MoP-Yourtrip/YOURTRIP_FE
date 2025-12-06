package com.example.yourtrip.mytrip.create_direct;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.network.NaverSearchResponse;
import com.example.yourtrip.R;

import java.util.ArrayList;
import java.util.List;

public class PlaceSearchAdapter extends RecyclerView.Adapter<PlaceSearchAdapter.PlaceViewHolder> {

    private List<NaverSearchResponse.Item> items = new ArrayList<>();
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(NaverSearchResponse.Item item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaceName, tvPlaceDescription, tvPlaceAddress, tvRoadAddress, tvPlaceLink;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvPlaceDescription = itemView.findViewById(R.id.tvPlaceDescription);
            tvPlaceAddress = itemView.findViewById(R.id.tvPlaceAddress);
            tvRoadAddress = itemView.findViewById(R.id.tvRoadAddress);
            tvPlaceLink = itemView.findViewById(R.id.tvPlaceLink);
        }

        // 수정: listener 파라미터를 제거하고, 내부 로직을 깔끔하게 정리합니다.
        public void bind(final NaverSearchResponse.Item item) {

            // description이 비어있으면 숨김
            if (item.description != null && !TextUtils.isEmpty(item.description.trim())) {
                tvPlaceDescription.setText(Html.fromHtml(item.description, Html.FROM_HTML_MODE_LEGACY));
                tvPlaceDescription.setVisibility(View.VISIBLE);
            } else {
                tvPlaceDescription.setVisibility(View.GONE);
            }

            // link가 비어있으면 숨김
            if (item.link != null && !TextUtils.isEmpty(item.link.trim())) {
                tvPlaceLink.setText(item.link);
                tvPlaceLink.setVisibility(View.VISIBLE);
            } else {
                tvPlaceLink.setVisibility(View.GONE);
            }

            tvPlaceName.setText(Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY));
            tvPlaceAddress.setText("지번: " + item.address);
            tvRoadAddress.setText("도로명: " + item.roadAddress);
        }
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_search, parent, false);
        final PlaceViewHolder holder = new PlaceViewHolder(view);

        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (listener != null) {
                    listener.onItemClick(items.get(position));
                }

                int previousPosition = selectedPosition;
                selectedPosition = position;

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.itemView.setActivated(position == selectedPosition);
        holder.bind(items.get(position));
    }

    public void setItems(List<NaverSearchResponse.Item> items) {
        this.items = items;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void clearItems() {
        this.items.clear();
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
