
package com.example.yourtrip.mytrip.upload;

import android.content.Context;
import android.util.Log;
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

// 수정: 시간 포맷 변경을 위한 import 추가
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        ImageView ivAddedPhoto;
        Context context;

        LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvMemo = itemView.findViewById(R.id.tvMemo);
//            ivMap = itemView.findViewById(R.id.ivMap);
            ivAddedPhoto = itemView.findViewById(R.id.ivAddedPhoto);
        }

        void bind(UploadCourseResponse.Place place, int number) {
            tvNumber.setText(String.valueOf(number));
            tvPlaceName.setText(place.getPlaceName());

            // --- 시간(tvTime) 처리 로직 ---
            String startTime = place.getStartTime();
            if (startTime != null && !startTime.trim().isEmpty()) {
                tvTime.setText(formatTime(startTime));
                tvTime.setVisibility(View.VISIBLE);
            } else {
                // 시간 값이 없으면, 뷰를 완전히 숨김
                tvTime.setVisibility(View.GONE);
            }

            // ---  주소(tvAddress) 처리 로직 ---
            String address = place.getPlaceLocation();
            if (address != null && !address.trim().isEmpty()) {
                tvAddress.setText(address);
                tvAddress.setVisibility(View.VISIBLE);
            } else {
                // 주소 값이 없으면, 뷰를 완전히 숨김
                tvAddress.setVisibility(View.GONE);
            }

            // --- 메모(tvMemo) 처리 로직 (공백 체크 추가) ---
            if (place.getMemo() != null && !place.getMemo().trim().isEmpty()) {
                tvMemo.setText(place.getMemo());
                tvMemo.setVisibility(View.VISIBLE);
            } else {
                tvMemo.setVisibility(View.GONE);
            }


            // --- 사진 개수에 따라 이미지 뷰를 제어하는 새로운 로직 ---
            List<UploadCourseResponse.PlaceImage> images = place.getPlaceImages();

            if (images != null && !images.isEmpty()) {
                ivAddedPhoto.setVisibility(View.VISIBLE);
                Glide.with(context).load(images.get(0).getImageUrl()).into(ivAddedPhoto);
            } else {
                // 이미지 없으면 업로드 화면에서 숨김
                ivAddedPhoto.setVisibility(View.GONE);
            }
        }

        /**
         * 추가: "HH:mm:ss" 또는 "HH:mm" 형식의 시간을 "a hh:mm" (예: 오후 02:00) 형식으로 변환하는 헬퍼 메서드
         */
        private String formatTime(String time) {
            if (time == null || time.isEmpty()) {
                return "";
            }
            try {
                // "HH:mm:ss" 또는 "HH:mm" 형식 모두 처리 가능하도록
                if (time.length() > 5) {
                    time = time.substring(0, 5); // "HH:mm" 형식으로 자름
                }
                SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
                Date date = inputFormat.parse(time);

                // "a hh:mm" 형식 (오전/오후 hh:mm)으로 변환
                SimpleDateFormat outputFormat = new SimpleDateFormat("a hh:mm", Locale.KOREA);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (ParseException e) {
                Log.e("ReadOnlyLocationAdapter", "시간 포맷 변경 중 오류 발생", e);
                // 파싱 실패 시 원본 시간이라도 반환
                return time;
            }
            return time;
        }
    }
}

//package com.example.yourtrip.mytrip.upload;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.bumptech.glide.Glide;
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.model.UploadCourseResponse;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ReadOnlyLocationAdapter extends RecyclerView.Adapter<ReadOnlyLocationAdapter.LocationViewHolder> {
//
//    private List<UploadCourseResponse.Place> places = new ArrayList<>();
//
//    @NonNull
//    @Override
//    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_readonly_location_card, parent, false);
//        return new LocationViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
//        UploadCourseResponse.Place place = places.get(position);
//        holder.bind(place, position + 1);
//    }
//
//    @Override
//    public int getItemCount() {
//        return places.size();
//    }
//
//    public void updatePlaces(List<UploadCourseResponse.Place> newPlaces) {
//        this.places = newPlaces != null ? newPlaces : new ArrayList<>();
//        notifyDataSetChanged();
//    }
//
//    static class LocationViewHolder extends RecyclerView.ViewHolder {
//        TextView tvNumber, tvTime, tvPlaceName, tvAddress, tvMemo;
//        ImageView ivMap, ivAddedPhoto;
//        Context context;
//
//        LocationViewHolder(@NonNull View itemView) {
//            super(itemView);
//            context = itemView.getContext();
//            tvNumber = itemView.findViewById(R.id.tvNumber);
//            tvTime = itemView.findViewById(R.id.tvTime);
//            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
//            tvAddress = itemView.findViewById(R.id.tvAddress);
//            tvMemo = itemView.findViewById(R.id.tvMemo);
//            ivMap = itemView.findViewById(R.id.ivMap);
//            ivAddedPhoto = itemView.findViewById(R.id.ivAddedPhoto);
//        }
//
//        void bind(UploadCourseResponse.Place place, int number) {
//            tvNumber.setText(String.valueOf(number));
//            tvTime.setText(place.getStartTime()); // TODO: "PM 2:00" 형식으로 변환 필요
//            tvPlaceName.setText(place.getPlaceName());
//            tvAddress.setText(place.getPlaceLocation());
//
//            if (place.getMemo() != null && !place.getMemo().isEmpty()) {
//                tvMemo.setText(place.getMemo());
//                tvMemo.setVisibility(View.VISIBLE);
//            } else {
//                tvMemo.setVisibility(View.GONE);
//            }
//
//            if (place.getPlaceImages() != null && !place.getPlaceImages().isEmpty()) {
//                Glide.with(context).load(place.getPlaceImages().get(0).getImageUrl()).into(ivAddedPhoto);
//                ivAddedPhoto.setVisibility(View.VISIBLE);
//            } else {
//                ivAddedPhoto.setVisibility(View.GONE);
//            }
//        }
//    }
//}
