package com.example.yourtrip.mytrip;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.LocationItem;

import java.util.List;

/**
 * 여행 코스 한 날(day)에 들어가는 장소 리스트 어댑터
 * - 위쪽: 장소 카드들 (item_trip_location_card)
 * - 맨 아래: "+ 장소 추가하기" 버튼 (view_add_location_button)
 */
public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOCATION = 0;   // 장소 카드
    private static final int VIEW_TYPE_ADD_BUTTON = 1; // + 장소 추가하기

    private final List<LocationItem> locationList;
    private OnAddClickListener addClickListener;
    private OnDeleteClickListener deleteClickListener;

    public LocationAdapter(List<LocationItem> locationList) {
        this.locationList = locationList;
    }

    // ----------- 콜백 인터페이스 -----------
    public interface OnAddClickListener {
        void onAddClick();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnAddClickListener(OnAddClickListener listener) {
        this.addClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // ----------- RecyclerView 필수 구현 -----------

    @Override
    public int getItemViewType(int position) {
        // 마지막 칸 = "+ 장소 추가하기"
        if (position == locationList.size()) {
            return VIEW_TYPE_ADD_BUTTON;
        }
        return VIEW_TYPE_LOCATION;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_LOCATION) {
            View view = inflater.inflate(R.layout.item_trip_location_card, parent, false);
            return new LocationViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.view_add_location_button, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_LOCATION) {

            LocationViewHolder vh = (LocationViewHolder) holder;
            LocationItem item = locationList.get(position);

            // 번호 (1부터 시작)
            vh.tvNumber.setText(String.valueOf(position + 1));

            // 시간 입력
            vh.tvTime.setText(
                    TextUtils.isEmpty(item.getTime())
                            ? "눌러서 시간 입력"
                            : item.getTime()
            );

            // 장소명 / 주소 / 메모
            vh.tvPlaceName.setText(item.getPlaceName());
            vh.tvAddress.setText(item.getAddress());
            vh.etMemo.setText(item.getMemo());

            // TODO : 지도 이미지 로딩 (카카오 Static Map 추가 예정)

            // 삭제 버튼 클릭
            vh.btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(holder.getAdapterPosition());
                }
            });

        } else {
            // "+ 장소 추가하기"
            AddButtonViewHolder vh = (AddButtonViewHolder) holder;
            vh.tvNumber.setText(String.valueOf(locationList.size() + 1));
            //  첫 번째 위치일 때만 테두리 배경 적용
            if (locationList.size() == 0) {
                vh.itemView.setBackgroundResource(R.drawable.bg_location_box);
            } else {
                vh.itemView.setBackground(null);
            }

            vh.itemView.setOnClickListener(v -> {
                if (addClickListener != null) addClickListener.onAddClick();
            });
        }
    }

    @Override
    public int getItemCount() {
        return locationList.size() + 1; // 장소 카드 + 마지막 추가 버튼
    }

    // ----------- ViewHolder 구현 -----------

    static class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView tvNumber, tvTime, tvPlaceName, tvAddress;
        ImageView ivMap, btnDelete;
        View btnAddPhoto;
        EditText etMemo;

        LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivMap = itemView.findViewById(R.id.ivMap);
            btnAddPhoto = itemView.findViewById(R.id.btnAddPhoto);
            etMemo = itemView.findViewById(R.id.etMemo);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    static class AddButtonViewHolder extends RecyclerView.ViewHolder {

        TextView tvNumber, tvAddLocation;

        AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvAddLocation = itemView.findViewById(R.id.tvAddLocation);
        }
    }
}
