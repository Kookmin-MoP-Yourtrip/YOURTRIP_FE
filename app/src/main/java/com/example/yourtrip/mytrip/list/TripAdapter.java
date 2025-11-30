package com.example.yourtrip.mytrip.list;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
import com.example.yourtrip.mytrip.model.MyCourseListItemResponse;
import com.example.yourtrip.mytrip.upload.UploadCourseTagsActivity;
import com.example.yourtrip.mytrip.util.DateUtils;

import java.io.Serializable;
import java.util.List;


/**
 * MyTripListFragment에서 '나의 코스 목록'을 보여주는 역할만 전담하는 어댑터
 */
public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private final List<MyCourseListItemResponse> courseList;

    // 🟡아이템 클릭 이벤트를 Fragment에 전달할 인터페이스를 정의
    public interface OnItemClickListener {
        void onItemClick(MyCourseListItemResponse myTrip);
    }
    private OnItemClickListener listener;
    private OnMoreButtonClickListener moreButtonClickListener;
    public interface OnMoreButtonClickListener {
        void onMoreButtonClick(MyCourseListItemResponse courseItem);
    }

    // 🟡Fragment에서 리스너를 설정할 수 있는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TripAdapter(List<MyCourseListItemResponse> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_trip_card.xml 레이아웃을 사용하여 뷰를 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip_card, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        // 현재 위치(position)에 해당하는 데이터를 가져옴
        MyCourseListItemResponse item = courseList.get(position);

        // ViewHolder의 뷰에 데이터를 설정
        holder.tvTitle.setText(item.getTitle());
        holder.tvLocation.setText(item.getLocation());

        // DateUtils를 사용하여 날짜 관련 텍스트를 간단하게 처리
        String startK = DateUtils.formatKoreanDate(item.getStartDate());
        String endK = DateUtils.formatKoreanDate(item.getEndDate());
        String period = DateUtils.getNightDayText(item.getStartDate(), item.getEndDate());
        String dateText = startK + " ~ " + endK + " (" + period + ")";
        holder.tvDate.setText(dateText);

        // 인원 표기 로직
        int memberCount = item.getMemberCount();
        if (memberCount <= 1) {
            holder.tagParty.setVisibility(View.GONE); // 1명 이하면 태그 숨김
        } else {
            holder.tagParty.setVisibility(View.VISIBLE);
            holder.tvParty.setText(memberCount + "명 참여");
        }

        // 더보기 버튼 클릭 시 메뉴를 보여줌
        holder.btnMore.setOnClickListener(v -> showMoreMenu(v, holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        // 리스트에 있는 전체 아이템 개수를 반환
        return courseList.size();
    }

    /**
     * item_trip_card.xml 레이아웃의 뷰들을 관리하는 ViewHolder 클래스
     */
    class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate, tvParty;
        ImageView btnMore;
        LinearLayout tagParty;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            // XML의 뷰 ID와 클래스의 멤버 변수를 연결
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvParty = itemView.findViewById(R.id.tv_party);
            btnMore = itemView.findViewById(R.id.btn_more);
            tagParty = itemView.findViewById(R.id.tag_party);

            // 아이템 뷰 전체에 클릭 리스너를 설정
            itemView.setOnClickListener(v -> {
                // 어댑터의 getAdapterPosition() 메서드를 통해 현재 클릭된 아이템의 위치 가져옴
                int position = getBindingAdapterPosition();
                // 유효한 위치이고, 어댑터에 리스너가 설정되어 있다면
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    MyCourseListItemResponse clickedItem = courseList.get(position);

                    // 로그 추가: 클릭된 아이템의 정보를 Logcat에 출력
                    Log.d("TripAdapter", "아이템 클릭됨 - Position: " + position + ", Title: " + clickedItem.getTitle() + ", CourseID: " + clickedItem.getCourseId());

                    // 리스너를 통해 Fragment로 클릭된 아이템 정보를 전달
                    listener.onItemClick(clickedItem);
                }
            });
        }
    }

    /**
     * 더보기(...) 버튼을 눌렀을 때 나타나는 메뉴 다이얼로그를 보여줌
     * @param anchor 메뉴가 나타날 기준점이 되는 뷰 (더보기 버튼)
     */
    private void showMoreMenu(View anchor, int position) {
        //유효하지 않은 position에 대한 방어 코드
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        Context context = anchor.getContext();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.menu_trip_card_more);

        // 다이얼로그의 배경을 투명하게 만들어 둥근 모서리가 보이도록
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // 뒷배경 어둡게 하지 않음
        }

        // 다이얼로그의 위치를 앵커 뷰 기준으로 계산하여 조정
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP | Gravity.END); // 오른쪽 상단 정렬
            WindowManager.LayoutParams params = window.getAttributes();

            // 앵커 뷰의 화면상 절대 좌표
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);

            // 앵커 뷰의 오른쪽 끝에 맞춰 팝업 위치를 계산
            params.x = anchor.getContext().getResources().getDisplayMetrics().widthPixels - location[0] - anchor.getWidth() - 3; // x 위치 (오른쪽 정렬)
            params.y = location[1] + anchor.getHeight() + 10; // y 위치 (앵커 아래)
            window.setAttributes(params);
        }

        // 메뉴 안의 버튼들과 클릭 이벤트를 연결
        LinearLayout btnUpload = dialog.findViewById(R.id.btn_upload);
        LinearLayout btnEdit = dialog.findViewById(R.id.btn_edit);
        LinearLayout btnDelete = dialog.findViewById(R.id.btn_delete);

        btnUpload.setOnClickListener(v -> {
            // 1. 클릭된 코스의 정보를 가져옵니다.
            MyCourseListItemResponse courseItem = courseList.get(position);
            Long courseId = courseItem.getCourseId();
            if (courseId == null) {
                Toast.makeText(context, "코스 ID가 없어 업로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            // 2. UploadCourseTagsActivity로 향하는 Intent를 생성합니다.
            Intent intent = new Intent(context, UploadCourseTagsActivity.class);

            // 3. [중요] 업로드할 코스의 ID를 Intent에 담아 전달합니다.
            intent.putExtra("courseId", courseId);

            // 4. 새로운 액티비티를 시작합니다.
            context.startActivity(intent);

            // 5. 클릭 후 다이얼로그를 닫습니다.
            dialog.dismiss();
        });
        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();
            // TODO: 편집 기능 구현
        });
        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            // TODO: 삭제 기능 구현
        });

        dialog.show();
    }
}


