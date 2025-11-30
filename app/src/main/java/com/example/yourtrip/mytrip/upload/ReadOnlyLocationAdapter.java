package com.example.yourtrip.mytrip.upload;

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
import com.example.yourtrip.mytrip.model.UploadCourseResponse;
import java.util.ArrayList;
import java.util.List;

public class ReadOnlyLocationAdapter extends RecyclerView.Adapter<ReadOnlyLocationAdapter.LocationViewHolder> {

    private List<UploadCourseResponse.Place> places = new ArrayList<>();

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_readonly_location_card, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        UploadCourseResponse.Place place = places.get(position);
        holder.bind(place, position + 1);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void updatePlaces(List<UploadCourseResponse.Place> newPlaces) {
        this.places = newPlaces != null ? newPlaces : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvTime, tvPlaceName, tvAddress, tvMemo;
        ImageView ivMap, ivAddedPhoto;
        Context context;

        LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvMemo = itemView.findViewById(R.id.tvMemo);
            ivMap = itemView.findViewById(R.id.ivMap);
            ivAddedPhoto = itemView.findViewById(R.id.ivAddedPhoto);
        }

        void bind(UploadCourseResponse.Place place, int number) {
            tvNumber.setText(String.valueOf(number));
            tvTime.setText(place.getStartTime()); // TODO: "PM 2:00" 형식으로 변환 필요
            tvPlaceName.setText(place.getPlaceName());
            tvAddress.setText(place.getPlaceLocation());

            if (place.getMemo() != null && !place.getMemo().isEmpty()) {
                tvMemo.setText(place.getMemo());
                tvMemo.setVisibility(View.VISIBLE);
            } else {
                tvMemo.setVisibility(View.GONE);
            }

            if (place.getPlaceImages() != null && !place.getPlaceImages().isEmpty()) {
                Glide.with(context).load(place.getPlaceImages().get(0).getImageUrl()).into(ivAddedPhoto);
                ivAddedPhoto.setVisibility(View.VISIBLE);
            } else {
                ivAddedPhoto.setVisibility(View.GONE);
            }
        }
    }
}
