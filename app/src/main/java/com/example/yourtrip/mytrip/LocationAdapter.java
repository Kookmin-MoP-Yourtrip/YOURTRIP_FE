package com.example.yourtrip.mytrip;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.LocationItem;

import java.util.List;

/**
 * 일차별 장소 목록을 표시하는 RecyclerView 어댑터.
 * '장소 카드'와 '장소 추가 버튼' 두 가지 뷰 타입을 처리
 */
public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 뷰 타입을 구분하기 위한 상수. 숫자는 어떤 값이든 상관없지만, 서로 달라야 합니다.
    private static final int VIEW_TYPE_LOCATION = 1;
    private static final int VIEW_TYPE_ADD_BUTTON = 2;

    // 장소(LocationItem)와 추가 버튼(특별한 값, 여기서는 String)을 모두 담을 수 있는 List
    private final List<Object> items;
    private long courseId;
    private long dayId; // final 제거

    // 🟡 1. Fragment 참조를 위한 변수 추가
    private final CreateCourseDayDetailFragment fragment;

    // 🟡 2. 생성자를 하나로 통일: 모든 필요한 정보를 받도록 함
    public LocationAdapter(List<Object> items, long courseId, long dayId, CreateCourseDayDetailFragment fragment) {
        this.items = items;
        this.courseId = courseId;
        this.dayId = dayId;
        this.fragment = fragment;
    }


    // 프래그먼트에서 새로운 dayId를 전달받아, 어댑터의 dayId 값을 업데이트
    public void updateDayId(long newDayId) {
        this.dayId = newDayId;
    }


    /**
     * 🟡 이 메서드를 추가해주세요.
     * 새로운 장소 아이템을 리스트에 추가하고, RecyclerView를 갱신합니다.
     * @param newItem AddLocationActivity에서 받아온 새로운 장소 정보
     */
    public void addItem(LocationItem newItem) {
        // 리스트의 맨 마지막에는 항상 '추가 버튼'이 있으므로,
        // 그 바로 앞 위치에 새로운 장소 아이템을 추가합니다.
        int position = items.size() - 1;
        items.add(position, newItem);

        // 아이템이 추가된 위치를 어댑터에 알려줘서 화면을 효율적으로 갱신합니다.
        notifyItemInserted(position);
        // 번호가 모두 바뀌었으므로, 전체 아이템의 UI를 다시 그리도록 알려줍니다.
        // (n번째 '추가' 버튼이 n+1번째가 되므로)
        notifyItemRangeChanged(position, items.size());
    }


    /**
     * 1. getItemViewType: 리스트의 각 아이템이 어떤 종류인지 판단하여 뷰 타입을 반환합니다.
     * 이 메서드의 반환값이 onCreateViewHolder의 두 번째 파라미터(viewType)로 전달됩니다.
     */
    @Override
    public int getItemViewType(int position) {
        // 현재 위치의 아이템이 LocationItem 클래스의 인스턴스(객체)이면
        if (items.get(position) instanceof LocationItem) {
            return VIEW_TYPE_LOCATION; // '장소 카드' 타입을 반환
        } else {
            return VIEW_TYPE_ADD_BUTTON; // 그 외에는 '추가 버튼' 타입을 반환
        }
    }

    /**
     * 2. onCreateViewHolder: getItemViewType이 반환한 뷰 타입에 따라 각각 다른 XML 레이아웃을 inflate하여
     * 그에 맞는 ViewHolder를 생성합니다.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // '장소 카드' 타입일 경우
        if (viewType == VIEW_TYPE_LOCATION) {
            View view = inflater.inflate(R.layout.item_trip_location_card, parent, false);
            return new LocationViewHolder(view);
        }
        // '추가 버튼' 타입일 경우
        else { // viewType == VIEW_TYPE_ADD_BUTTON
            View view = inflater.inflate(R.layout.view_add_location_button, parent, false);
            // ViewHolder 생성 시 courseId와 dayId를 직접 전달
//            return new AddButtonViewHolder(view, courseId, dayId);
            // 🟡 수정: ViewHolder 생성 시 모든 필요한 정보 전달
            return new AddButtonViewHolder(view, courseId, dayId, fragment);
        }
    }

    /**
     * 3. onBindViewHolder: 생성된 ViewHolder에 실제 데이터를 바인딩(연결)합니다.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 순번은 0부터 시작하는 position에 1을 더해서 만듭니다.
        String number = String.valueOf(position + 1);

        // ViewHolder의 타입에 따라 다른 작업을 수행합니다.
        if (holder.getItemViewType() == VIEW_TYPE_LOCATION) {
            // LocationViewHolder로 형변환
            LocationViewHolder locationHolder = (LocationViewHolder) holder;
            // 리스트에서 LocationItem 객체를 가져옴
            LocationItem locationItem = (LocationItem) items.get(position);
            // ViewHolder의 bind 메서드를 호출하여 데이터를 채움
            locationHolder.bind(locationItem, number);
        }
        else { // VIEW_TYPE_ADD_BUTTON
            // AddButtonViewHolder로 형변환
            AddButtonViewHolder addButtonHolder = (AddButtonViewHolder) holder;
            // ViewHolder의 bind 메서드를 호출하여 데이터를 채움
            addButtonHolder.bind(number);
        }
    }

    /**
     * 전체 아이템 개수를 반환합니다.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }


    // --- ViewHolder 클래스들 ---

    /**
     * '장소 카드' (item_trip_location.xml)의 뷰들을 관리하는 ViewHolder
     */
    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumber;
        private final TextView tvTime;
        private final ImageView btnDelete;
        private final TextView tvPlaceName;
        private final TextView tvAddress;
        private final ImageView ivMap;
        private final LinearLayout btnAddPhoto;
        private final EditText etMemo;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivMap = itemView.findViewById(R.id.ivMap);
            btnAddPhoto = itemView.findViewById(R.id.btnAddPhoto); // 사진 추가 버튼
            etMemo = itemView.findViewById(R.id.etMemo);
        }

        public void bind(LocationItem item, String number) {
            tvNumber.setText(number);
            tvPlaceName.setText(item.getPlaceName());
            tvAddress.setText(item.getPlaceLocation());

            // --- 클릭 이벤트 리스너 설정 ---
            tvTime.setOnClickListener(v -> {
                // TODO: 시간 선택 다이얼로그(Time Picker)를 띄우는 로직 구현
            });

            btnDelete.setOnClickListener(v -> {
                // TODO: 이 아이템을 삭제하는 로직 구현 (API 호출 등)
            });

            btnAddPhoto.setOnClickListener(v -> {
                // TODO: 갤러리를 열어 사진을 선택하는 로직 구현
            });

            // TODO: 지도 이미지(ivMap) 로딩 로직 (Glide 라이브러리 등 사용)
        }
    }

    /**
     * '+ 장소 추가하기' 버튼 (view_add_location_button.xml)의 뷰들을 관리하는 ViewHolder
     */
    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumber;
        // 🟡 추가: Context 변수 추가
        private final Context context;
//        public AddButtonViewHolder(@NonNull View itemView, long courseId, long dayId) {
//            super(itemView);
//            // 🟡 추가: itemView로부터 Context를 얻어와서 변수에 저장
//            this.context = itemView.getContext();
//            tvNumber = itemView.findViewById(R.id.tvNumber);
//
//            // "장소 추가하기" 버튼 전체에 클릭 리스너 설정
//            itemView.setOnClickListener(v -> {
//                // 🟡 수정: AddLocationActivity로 이동하는 Intent 로직 구현
//                Intent intent = new Intent(context, AddLocationActivity.class);
//                // TODO: AddLocationActivity에 courseId와 dayId를 전달해야 합니다.
//                 intent.putExtra("courseId", courseId);
//                 intent.putExtra("dayId", dayId);
//                context.startActivity(intent);
//            });
//        }
        // 🟡 4. 생성자에서 Fragment 참조를 받도록 수정
        public AddButtonViewHolder(@NonNull View itemView, long courseId, long dayId, CreateCourseDayDetailFragment fragment) {
            super(itemView);
            this.context = itemView.getContext();
            tvNumber = itemView.findViewById(R.id.tvNumber);

            itemView.setOnClickListener(v -> {
                // 🟡 5. Fragment의 메서드를 호출하여 Activity 실행 요청
                fragment.launchAddLocationActivity(courseId, dayId);
            });
        }

        public void bind(String number) {
            tvNumber.setText(number);
        }
    }
}


//package com.example.yourtrip.mytrip;
//
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.model.LocationItem;
//
//import java.util.List;
//
///**
// * 여행 코스 한 날(day)에 들어가는 장소 리스트 어댑터
// * - 위쪽: 장소 카드들 (item_trip_location_card)
// * - 맨 아래: "+ 장소 추가하기" 버튼 (view_add_location_button)
// */
//public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static final int VIEW_TYPE_LOCATION = 0;   // 장소 카드
//    private static final int VIEW_TYPE_ADD_BUTTON = 1; // + 장소 추가하기
//
//    private final List<LocationItem> locationList;
//    private OnAddClickListener addClickListener;
//    private OnDeleteClickListener deleteClickListener;
//
//    public LocationAdapter(List<LocationItem> locationList) {
//        this.locationList = locationList;
//    }
//
//    // ----------- 콜백 인터페이스 -----------
//    public interface OnAddClickListener {
//        void onAddClick();
//    }
//
//    public interface OnDeleteClickListener {
//        void onDeleteClick(int position);
//    }
//
//    public void setOnAddClickListener(OnAddClickListener listener) {
//        this.addClickListener = listener;
//    }
//
//    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
//        this.deleteClickListener = listener;
//    }
//
//    // ----------- RecyclerView 필수 구현 -----------
//
//    @Override
//    public int getItemViewType(int position) {
//        // 마지막 칸 = "+ 장소 추가하기"
//        if (position == locationList.size()) {
//            return VIEW_TYPE_ADD_BUTTON;
//        }
//        return VIEW_TYPE_LOCATION;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//
//        if (viewType == VIEW_TYPE_LOCATION) {
//            View view = inflater.inflate(R.layout.item_trip_location_card, parent, false);
//            return new LocationViewHolder(view);
//        } else {
//            View view = inflater.inflate(R.layout.view_add_location_button, parent, false);
//            return new AddButtonViewHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        if (getItemViewType(position) == VIEW_TYPE_LOCATION) {
//
//            LocationViewHolder vh = (LocationViewHolder) holder;
//            LocationItem item = locationList.get(position);
//
//            // 번호 (1부터 시작)
//            vh.tvNumber.setText(String.valueOf(position + 1));
//
//            // 시간 입력
//            vh.tvTime.setText(
//                    TextUtils.isEmpty(item.getTime())
//                            ? "눌러서 시간 입력"
//                            : item.getTime()
//            );
//
//            // 장소명 / 주소 / 메모
//            vh.tvPlaceName.setText(item.getPlaceName());
//            vh.tvAddress.setText(item.getAddress());
//            vh.etMemo.setText(item.getMemo());
//
//            // TODO : 지도 이미지 로딩 (카카오 Static Map 추가 예정)
//
//            // 삭제 버튼 클릭
//            vh.btnDelete.setOnClickListener(v -> {
//                if (deleteClickListener != null) {
//                    deleteClickListener.onDeleteClick(holder.getAdapterPosition());
//                }
//            });
//
//        } else {
//            // "+ 장소 추가하기"
//            AddButtonViewHolder vh = (AddButtonViewHolder) holder;
//            vh.tvNumber.setText(String.valueOf(locationList.size() + 1));
//            //  첫 번째 위치일 때만 테두리 배경 적용
//            if (locationList.size() == 0) {
//                vh.itemView.setBackgroundResource(R.drawable.bg_location_box);
//            } else {
//                vh.itemView.setBackground(null);
//            }
//
//            vh.itemView.setOnClickListener(v -> {
//                if (addClickListener != null) addClickListener.onAddClick();
//            });
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return locationList.size() + 1; // 장소 카드 + 마지막 추가 버튼
//    }
//
//    // ----------- ViewHolder 구현 -----------
//
//    static class LocationViewHolder extends RecyclerView.ViewHolder {
//
//        TextView tvNumber, tvTime, tvPlaceName, tvAddress;
//        ImageView ivMap, btnDelete;
//        View btnAddPhoto;
//        EditText etMemo;
//
//        LocationViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvNumber = itemView.findViewById(R.id.tvNumber);
//            tvTime = itemView.findViewById(R.id.tvTime);
//            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
//            tvAddress = itemView.findViewById(R.id.tvAddress);
//            ivMap = itemView.findViewById(R.id.ivMap);
//            btnAddPhoto = itemView.findViewById(R.id.btnAddPhoto);
//            etMemo = itemView.findViewById(R.id.etMemo);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
//        }
//    }
//
//    static class AddButtonViewHolder extends RecyclerView.ViewHolder {
//
//        TextView tvNumber, tvAddLocation;
//
//        AddButtonViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvNumber = itemView.findViewById(R.id.tvNumber);
//            tvAddLocation = itemView.findViewById(R.id.tvAddLocation);
//        }
//    }
//}
