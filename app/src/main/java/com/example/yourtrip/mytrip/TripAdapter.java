package com.example.yourtrip.mytrip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.MyCourseListItemResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<MyCourseListItemResponse> courseList;

    public TripAdapter(List<MyCourseListItemResponse> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip_card, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        MyCourseListItemResponse item = courseList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvLocation.setText(item.getLocation());

        // ⭐ 날짜 표시
        String period = getNightDayText(item.getStartDate(), item.getEndDate());
        String dateText = item.getStartDate()
                + " ~ "
                + item.getEndDate()
                + " (" + period + ")";

        holder.tvDate.setText(dateText);

        holder.tvParty.setText(item.getMemberCount() + "명 참여");

        // 더보기 버튼 클릭 리스너 (기능 연결은 나중)
        holder.btnMore.setOnClickListener(v -> {
            // TODO: 여기에서 팝업 메뉴 띄우면 됨
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    // ⬇️⬇️⬇️⬇️  ⭐ 바로 여기에 추가하는 게 정답! ⭐  ⬇️⬇️⬇️⬇️
    private String getNightDayText(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            long diffMillis = endDate.getTime() - startDate.getTime();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);

            long nights = diffDays;
            long days = diffDays + 1;

            return nights + "박 " + days + "일";

        } catch (Exception e) {
            return "";
        }
    }
    // ⬆️⬆️⬆️⬆️  ⭐ Adapter 안 private 함수로 추가 ⭐  ⬆️⬆️⬆️⬆️

    static class TripViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvLocation, tvDate, tvParty;
        ImageView btnMore;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvParty = itemView.findViewById(R.id.tv_party);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }
}
