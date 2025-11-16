package com.example.yourtrip.mytrip;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
        String startK = formatKoreanDate(item.getStartDate());
        String endK = formatKoreanDate(item.getEndDate());
        String period = getNightDayText(item.getStartDate(), item.getEndDate());
        String dateText = startK + " ~ " + endK + " (" + period + ")";
        holder.tvDate.setText(dateText);


        // ⭐ 인원 표시 처리
        int member = item.getMemberCount();

        // ⭐ 여기 추가!! (1명 참여는 숨기기)
        if (member <= 1) {
            // ⭐ 인원 태그 전체 숨김 (아이콘+텍스트+배경)
            holder.tagParty.setVisibility(View.GONE);
        } else {
            holder.tagParty.setVisibility(View.VISIBLE);
            holder.tvParty.setText(member + "명 참여");
        }

        // ⭐ 더보기 버튼 클릭 리스너 (기능 연결은 나중)
        holder.btnMore.setOnClickListener(v -> {
            showMoreMenu(v);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate, tvParty;
        ImageView btnMore;
        LinearLayout tagParty;   // ← 인원 태그 전체 레이아웃

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvParty = itemView.findViewById(R.id.tv_party);
            btnMore = itemView.findViewById(R.id.btn_more);
            tagParty = itemView.findViewById(R.id.tag_party);  //
        }
    }

    private String formatKoreanDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date parsed = sdf.parse(date);

            SimpleDateFormat korean = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA);
            return korean.format(parsed);

        } catch (Exception e) {
            return date; // 파싱 실패하면 원본 그대로
        }
    }

    private String getNightDayText(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            long diffMillis = endDate.getTime() - startDate.getTime();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);

            // ⭐ 당일치기 처리
            if (diffDays == 0) {
                return "당일치기";
            }

            long nights = diffDays;
            long days = diffDays + 1;
            return nights + "박 " + days + "일";

        } catch (Exception e) {
            return "";
        }
    }
//
//    private void showMoreMenu(View anchor) {
//        Context context = anchor.getContext();
//        Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_trip_more_menu);
//
//        // 배경 투명하게 (둥근 모서리 shape 보이게)
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//
//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            window.getDecorView().setBackground(null);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // ← dim 제거!
//
//            // 1) 윈도우 기준 초기화 (Gravity.TOP | Gravity.END : 오른쪽 상단 기준)
//            window.setGravity(Gravity.TOP | Gravity.END);
//            WindowManager.LayoutParams params = window.getAttributes();
//
//            // 2) 앵커의 절대좌표
//            int[] location = new int[2];
//            anchor.getLocationOnScreen(location);
//
//            // 3) 화면(윈도우)의 가로 길이 구하기
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            Display display = wm.getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//            int windowWidth = size.x;   // ← 화면 가로폭
//
//            // 4) anchor의 좌우 x의 절대좌표 계산
//            int anchorLeft   = location[0];
//            int anchorRight  = anchorLeft + anchor.getWidth();
//
//            // 5) 다이얼로그를 오른쪽 상단 윈도우 기준 상대위치로 초기화
//            int anchor_x = windowWidth - (location[0] + anchor.getWidth());
//            params.x = anchor_x-3; //margin 길이만큼 제외
//            params.y = location[1] + 10;
//
//            window.setAttributes(params);
//        }
//
//        // 메뉴 버튼 클릭 연결
//        LinearLayout btnUpload = dialog.findViewById(R.id.btn_upload);
//        LinearLayout btnEdit = dialog.findViewById(R.id.btn_edit);
//        LinearLayout btnDelete = dialog.findViewById(R.id.btn_delete);
//
//        btnUpload.setOnClickListener(v -> {
//            dialog.dismiss();
//            // TODO 업로드 기능 연결
//        });
//
//        btnEdit.setOnClickListener(v -> {
//            dialog.dismiss();
//            // TODO 편집 기능 연결
//        });
//
//        btnDelete.setOnClickListener(v -> {
//            dialog.dismiss();
//            // TODO 삭제 기능 연결
//        });
//
//        dialog.show();
//    }
    private void showMoreMenu(View anchor) { // 앵커는 더보기 버튼
        Context context = anchor.getContext();

        // 1) 커스텀 레이아웃 inflate
        View popupView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_trip_more_menu, null);

        // 2) PopupWindow 생성
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // 밖 터치시 닫기
        );

        // 3) 배경 투명하게 (shape의 둥근 모서리 보이게)
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(20f); // 그림자(Optional)

        // (1) 앵커의 절대 좌표 구하기
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int anchorX = location[0];
        int anchorY = location[1];
        int anchorWidth = anchor.getWidth();
        int anchorHeight = anchor.getHeight();

        // anchorX + anchorWidth가 팝업의 오른쪽 모서리가 되어야함
        //

        // (2) 화면 전체 폭 구하기
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int windowWidth = size.x;

        // (3) PopupView 측정 (너비 구해야 오른쪽 정렬 가능)
        popupView.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
        );
        int popupWidth = popupView.getMeasuredWidth();

        // (4) 너가 계산하던 anchor_x 공식 그대로 적용
        //    windowWidth - (anchorLeft + anchorWidth)
        int finalX = anchorX + anchorWidth - popupWidth  - 3;      // margin 보정
        int finalY = anchorY +anchorHeight + 10;      // 아래 offset

        // (5) showAtLocation으로 절대좌표 배치
        popupWindow.showAtLocation(anchor, Gravity.TOP | Gravity.START, finalX, finalY);

        // 5) 메뉴 클릭 연결
        LinearLayout btnUpload = popupView.findViewById(R.id.btn_upload);
        LinearLayout btnEdit = popupView.findViewById(R.id.btn_edit);
        LinearLayout btnDelete = popupView.findViewById(R.id.btn_delete);

        btnUpload.setOnClickListener(v -> {
            popupWindow.dismiss();
            // TODO 업로드 기능
        });

        btnEdit.setOnClickListener(v -> {
            popupWindow.dismiss();
            // TODO 편집 기능
        });

        btnDelete.setOnClickListener(v -> {
            popupWindow.dismiss();
            // TODO 삭제 기능
        });
    }

}
