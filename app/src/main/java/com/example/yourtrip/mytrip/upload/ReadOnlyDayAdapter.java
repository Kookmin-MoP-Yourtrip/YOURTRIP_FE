package com.example.yourtrip.mytrip.upload;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.UploadCourseResponse;
import java.util.List;

public class ReadOnlyDayAdapter extends RecyclerView.Adapter<ReadOnlyDayAdapter.DayViewHolder> {

    private final List<UploadCourseResponse.DaySchedule> daySchedules; // [수정]
    private final OnDayClickListener listener;
    private int selectedPosition = 0;

    public interface OnDayClickListener {
        void onDayClick(int position);
    }

    public ReadOnlyDayAdapter(List<UploadCourseResponse.DaySchedule> daySchedules, OnDayClickListener listener) { // [수정]
        this.daySchedules = daySchedules;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        UploadCourseResponse.DaySchedule daySchedule = daySchedules.get(position);

        holder.bind(daySchedule, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != holder.getAdapterPosition()) {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onDayClick(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return daySchedules != null ? daySchedules.size() : 0;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDay;
        private final Context context;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            tvDay = itemView.findViewById(R.id.tv_day);
        }

        void bind(UploadCourseResponse.DaySchedule daySchedule, boolean isSelected) {
            String dayText = daySchedule.getDay() + "일차";
            tvDay.setText(dayText);

            if (isSelected) {
                // 선택된 상태의 UI
                itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_day_tab_selected));
                tvDay.setTextColor(ContextCompat.getColor(context, R.color.blue_main));
            } else {
                // 선택되지 않은 상태의 UI
                itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_day_tab_normal));
                tvDay.setTextColor(Color.parseColor("#646B72"));
            }
        }
    }
}
