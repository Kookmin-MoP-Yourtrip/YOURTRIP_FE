package com.example.yourtrip.mytrip;

import android.content.Context; // Context 추가
import android.graphics.Color; // Color 추가
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // ContextCompat 추가
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private final List<MyCourseDetailResponse.DaySchedule> daySchedules;
    private int selectedPosition = 0; // 🔵 현재 선택된 아이템의 위치를 저장 (기본값: 0)

    // 🟡 1. 클릭 이벤트를 전달할 인터페이스 정의
    public interface OnDayTabClickListener {
        void onDayTabClick(int position, long dayId);
    }
    private final OnDayTabClickListener listener; // 🟡 2. 리스너 멤버 변수 추가

    // 🟡 3. 생성자에서 리스너를 전달받도록 수정
    public DayAdapter(List<MyCourseDetailResponse.DaySchedule> daySchedules, OnDayTabClickListener listener) {
        this.daySchedules = daySchedules;
        this.listener = listener;
    }

    // 생성자에서 데이터 리스트를 받음
//    public DayAdapter(List<MyCourseDetailResponse.DaySchedule> daySchedules) {
//        this.daySchedules = daySchedules;
//    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_trip_day.xml 레이아웃을 인플레이트하여 ViewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        // 현재 위치(position)에 해당하는 DaySchedule 객체를 가져옴
        MyCourseDetailResponse.DaySchedule daySchedule = daySchedules.get(position);

        // 🔵 isSelected 파라미터 추가하여 바인딩
        holder.bind(daySchedule, position == selectedPosition);

        // 🔵 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != position) {
                int previousPosition = selectedPosition;
                selectedPosition = position;
                // 이전 선택된 아이템과 새로 선택된 아이템을 갱신
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                // 🟡 4. 인터페이스를 통해 Fragment에 클릭 이벤트 전달
                if (listener != null) {
                    listener.onDayTabClick(position, daySchedule.getDayId());
                }

                Log.d("DayAdapter", (position + 1) + "일차 탭 클릭됨! dayId: " + daySchedules.get(position).getDayId());
            }
        });

        // ViewHolder에 데이터를 바인딩
//        holder.bind(daySchedule); // 이 부분은 중복 호출되므로 주석 처리된 상태로 두는 것이 맞습니다.
    }

    @Override
    public int getItemCount() {
        // 전체 아이템 개수 반환
        return daySchedules != null ? daySchedules.size() : 0;
    }

    // 각 아이템 뷰를 보관하는 ViewHolder 클래스
    static class DayViewHolder extends RecyclerView.ViewHolder {
        // item_trip_day.xml에 있는 TextView
        private final TextView dayTextView;
        // 🟡 수정: Context를 사용하기 위해 멤버 변수로 추가
        private final Context context;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            // 🟡 수정: itemView로부터 Context를 얻어와서 변수에 저장
            this.context = itemView.getContext();
            // ID를 기반으로 TextView를 찾음
            dayTextView = itemView.findViewById(R.id.tv_day);
        }

        // 데이터를 UI에 바인딩하는 메서드
        // 🔵 bind 메서드 수정
        public void bind(MyCourseDetailResponse.DaySchedule daySchedule, boolean isSelected) {
            String dayText = daySchedule.getDay() + "일차";
            dayTextView.setText(dayText);

            if (isSelected) {
                // 선택된 상태의 스타일 적용
                // 🟡 수정: ViewHolder에 저장된 context를 사용하여 오류 해결
                itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_day_tab_selected));
                dayTextView.setTextColor(ContextCompat.getColor(context, R.color.blue_main)); // 예시 색상
            } else {
                // 선택되지 않은 상태의 스타일 적용
                // 🟡 수정: ViewHolder에 저장된 context를 사용하여 오류 해결
                itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_day_tab_normal));
                dayTextView.setTextColor(Color.parseColor("#646B72"));
            }
        }
    }
}



//package com.example.yourtrip.mytrip;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.yourtrip.R;
//
//import java.util.List;
//
///**
// * CreateCourseDayDetailFragment에서 '일차별 장소 목록'을 보여주는 역할만 전담하는 새로운 어댑터입니다.
// * '장소 카드'와 '+ 장소 추가' 버튼 두 종류의 뷰를 처리합니다.
// */
//public class DayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private final List<DayDetailItem> itemList;
//    private final CreateCourseDayDetailFragment fragment; // AddLocationActivity를 실행하기 위해 Fragment 참조
//
//    public DayAdapter(List<DayDetailItem> itemList, CreateCourseDayDetailFragment fragment) {
//        this.itemList = itemList;
//        this.fragment = fragment;
//    }
//
//    /**
//     * 리스트의 특정 위치(position)에 있는 아이템의 종류(ViewType)를 반환합니다.
//     * 이 값에 따라 onCreateViewHolder에서 다른 레이아웃을 선택하게 됩니다.
//     */
//    @Override
//    public int getItemViewType(int position) {
//        return itemList.get(position).getViewType();
//    }
//
//    /**
//     * getItemViewType에서 반환된 viewType에 따라,
//     * '장소 카드' 또는 '추가 버튼'에 맞는 ViewHolder를 생성합니다.
//     */
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        if (viewType == DayDetailItem.TYPE_LOCATION) {
//            View view = inflater.inflate(R.layout.item_trip_location_card, parent, false);
//            return new LocationViewHolder(view);
//        } else { // TYPE_ADD_BUTTON
//            View view = inflater.inflate(R.layout.view_add_location_button, parent, false);
//            return new AddButtonViewHolder(view);
//        }
//    }
//
//    /**
//     * 생성된 ViewHolder에 실제 데이터를 채워 넣는(바인딩하는) 역할을 합니다.
//     */
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (holder.getItemViewType() == DayDetailItem.TYPE_LOCATION) {
//            LocationViewHolder locationHolder = (LocationViewHolder) holder;
//            DayDetailItem item = itemList.get(position);
//
//            // 동적 번호 매기기: position은 0부터 시작하므로 +1을 해줍니다.
//            locationHolder.tvNumber.setText(String.valueOf(position + 1));
//            locationHolder.tvPlaceName.setText(item.getPlaceName());
//            locationHolder.tvAddress.setText(item.getPlaceAddress());
//
//            // TODO: 시간, 메모, 사진 등 나머지 데이터를 설정하는 로직 추가
//
//        } else { // TYPE_ADD_BUTTON
//            AddButtonViewHolder addButtonHolder = (AddButtonViewHolder) holder;
//            // '+ 장소 추가하기' 버튼에 클릭 이벤트를 설정합니다.
//            addButtonHolder.addButtonLayout.setOnClickListener(v -> {
//                // Fragment에 만들어 둔 launchAddLocation() 메서드를 호출합니다.
//                fragment.launchAddLocation();
//            });
//        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return itemList.size();
//    }
//
//    /**
//     * '장소 카드' (item_trip_location_card.xml)의 뷰들을 관리하는 ViewHolder
//     */
//    public static class LocationViewHolder extends RecyclerView.ViewHolder {
//        TextView tvNumber, tvTime, tvPlaceName, tvAddress;
//        ImageView btnDelete, ivMap;
//        View btnAddPhoto;
//        EditText etMemo;
//
//        public LocationViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvNumber = itemView.findViewById(R.id.tvNumber);
//            tvTime = itemView.findViewById(R.id.tvTime);
//            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
//            tvAddress = itemView.findViewById(R.id.tvAddress);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
//            ivMap = itemView.findViewById(R.id.ivMap);
//            btnAddPhoto = itemView.findViewById(R.id.btnAddPhoto);
//            etMemo = itemView.findViewById(R.id.etMemo);
//        }
//    }
//
//    /**
//     * '+ 장소 추가하기' 버튼 (view_add_location_button.xml)의 뷰를 관리하는 ViewHolder
//     */
//    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
//        View addButtonLayout; // 클릭 이벤트를 줄 최상위 레이아웃
//
//        public AddButtonViewHolder(@NonNull View itemView) {
//            super(itemView);
//            // view_add_location_button.xml의 최상위 뷰 ID로 가정
//            addButtonLayout = itemView.findViewById(R.id.add_location_button_layout);
//        }
//    }
//}
