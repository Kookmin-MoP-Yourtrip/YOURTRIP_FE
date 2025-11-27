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


