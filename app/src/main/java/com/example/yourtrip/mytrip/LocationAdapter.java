package com.example.yourtrip.mytrip;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.LocationItem;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 일차별 장소 목록을 표시하는 RecyclerView 어댑터.
 * '장소 카드'와 '장소 추가 버튼' 두 가지 뷰 타입을 처리
 */
public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // [추가] Fragment와 통신하기 위한 인터페이스 정의
    public interface OnLocationInteractionListener {
        void onTimeUpdateRequested(long placeId, String time, int position);
        void onPhotoAddRequested(long placeId, int position);
        void onMemoUpdateRequested(long placeId, String memo, int position);
    }

    // 뷰 타입을 구분하기 위한 상수. 숫자는 어떤 값이든 상관없지만, 서로 달라야 합니다.
    private static final int VIEW_TYPE_LOCATION = 1;
    private static final int VIEW_TYPE_ADD_BUTTON = 2;

    // 장소(LocationItem)와 추가 버튼(특별한 값, 여기서는 String)을 모두 담을 수 있는 List
    private final List<Object> items;
    private long courseId;
    private long dayId; // final 제거
    private final CreateCourseDayDetailFragment fragment; //Fragment 참조 변수
    private final OnLocationInteractionListener listener;

    public long getCurrentDayId() {
        return this.dayId;
    }


    //  생성자를 하나로 통일: 모든 필요한 정보를 받도록 함
    public LocationAdapter(List<Object> items, long courseId, long dayId, CreateCourseDayDetailFragment fragment) {
        this.items = items;
        this.courseId = courseId;
        this.dayId = dayId;
        this.fragment = fragment;

        // [수정] 생성자에서 Fragment를 리스너로 캐스팅
        if (fragment instanceof OnLocationInteractionListener) {
            this.listener = (OnLocationInteractionListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString() + " must implement OnLocationInteractionListener");
        }
    }


    // 프래그먼트에서 새로운 dayId를 전달받아, 어댑터의 dayId 값을 업데이트
    public void updateDayId(long newDayId) {
        //  디버깅 로그 4: Adapter의 dayId가 업데이트될 때
        Log.d("DEBUG_DAY_ID", "[LocationAdapter update] dayId가 " + this.dayId + "에서 " + newDayId + " (으)로 업데이트됨.");
        this.dayId = newDayId;
    }



    // 새로운 장소 아이템을 리스트에 추가, RecyclerView 갱신
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


    //새로운 장소 목록으로 전체 데이터 교체 및 화면 갱신
    public void updateItems(List<LocationItem> newPlaces) {
        // 1. 기존 아이템 리스트를 완전히 비웁니다.
        items.clear();

        // 2. 서버에서 받아온 새로운 장소 목록이 null이 아닐 경우, 모두 추가합니다.
        if (newPlaces != null) {
            items.addAll(newPlaces);
        }

        // 3. 리스트의 맨 마지막에 '+ 장소 추가하기' 버튼을 위한 데이터를 추가합니다.
        items.add("ADD_BUTTON");

        // 4. 데이터셋 전체가 변경되었음을 알려 화면을 완전히 새로고침합니다.
        notifyDataSetChanged();
    }

    // [추가] Fragment로부터 호출받아 시간 데이터와 UI를 최종 업데이트하는 메서드
    public void updateTime(int position, String time) {
        if (position >= 0 && position < items.size()) {
            Object item = items.get(position);
            if (item instanceof LocationItem) {
                ((LocationItem) item).setStartTime(time); // 데이터 모델 값 변경
                notifyItemChanged(position); // 해당 아이템 뷰만 새로고침
            }
        }
    }

    public Object getItemAt(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    // 리스트 아이템 타입 판단 -> 뷰 타입 반환
    @Override
    public int getItemViewType(int position) {
        // 현재 위치의 아이템이 LocationItem 클래스의 인스턴스(객체)이면
        if (items.get(position) instanceof LocationItem) {
            return VIEW_TYPE_LOCATION; // '장소 카드' 타입을 반환
        } else {
            return VIEW_TYPE_ADD_BUTTON; // 그 외에는 '추가 버튼' 타입을 반환
        }
    }

    // 뷰 타입에 따라 xml 레이아웃 inflate해서 ViewHolder 생성
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
            return new AddButtonViewHolder(view, this);
        }
    }


    // ViewHolder에 데이터 바인딩
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


    //전체 아이템 개수 반환
    @Override
    public int getItemCount() {
        return items.size();
    }


    // --- ViewHolder 클래스들 ---

    // [수정] LocationViewHolder의 static 키워드 제거. Adapter의 멤버(items 등)에 접근하기 위함
    public class LocationViewHolder extends RecyclerView.ViewHolder {
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

            // 아이템의 startTime 값에 따라 초기 UI 설정
            if (item.getStartTime() != null && !item.getStartTime().isEmpty()) {
                // 서버에서 받은 시간(HH:mm 또는 HH:mm:ss)을 "오전/오후 hh:mm" 형식으로 변환
                tvTime.setText(formatTime(item.getStartTime())); // 헬퍼 메서드 사용
                tvTime.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
            } else {
                tvTime.setText("눌러서 시간 입력");
                tvTime.setTextColor(itemView.getContext().getResources().getColor(R.color.gray_500));
            }

            // --- 이미지 표시 로직 (Glide 사용) ---
            // 장소에 이미지가 하나 이상 있다면, 첫 번째 이미지를 ivMap에 표시
            if (item.getImageUrls() != null && !item.getImageUrls().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImageUrls().get(0)) // 첫 번째 이미지를 로드
                        .centerCrop()
                        .into(ivMap);
            } else {
                // 이미지가 없다면 기본 배경색 또는 플레이스홀더 표시
                ivMap.setImageResource(0); // 이미지 리소스 제거
                ivMap.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.gray_150));
            }

            // --- [추가] 메모 입력 리스너 ---
            etMemo.setText(item.getMemo()); // 초기 메모 설정


            // --- 클릭 이벤트 리스너 설정 ---
            // [수정] 클릭 시 Adapter에 구현된 showTimePickerDialog를 호출하도록 변경
            tvTime.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Adapter 클래스에 정의된 메서드를 호출
                    showTimePickerDialog((LocationItem) items.get(position), position);
                }
            });

            btnAddPhoto.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPhotoAddRequested(item.getPlaceId(), position);
                }
            });

            //우선은 포커스르 잃었을 때로 설정했음  -> 추후에 협업 기능이 들어오면 수정하기
            etMemo.setOnFocusChangeListener((v, hasFocus) -> {
                // 포커스를 잃었을 때 (입력이 끝났다고 간주) API 호출
                if (!hasFocus) {
                    int position = getBindingAdapterPosition();
                    String newMemo = etMemo.getText().toString();
                    // 기존 메모와 다를 경우에만 업데이트 요청
                    if (position != RecyclerView.NO_POSITION && listener != null && !newMemo.equals(item.getMemo())) {
                        listener.onMemoUpdateRequested(item.getPlaceId(), newMemo, position);
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                // TODO: 이 아이템을 삭제하는 로직 구현 (API 호출 등)
            });
        }
    }

    // [수정] TimePickerDialog 관련 메서드를 ViewHolder 밖, Adapter 클래스 내부로 이동
    private void showTimePickerDialog(LocationItem currentItem, int position) {
        if (listener == null) return;

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // MaterialTimePicker 빌더를 생성
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                // 커스텀 테마를 적용
                .setTheme(R.style.CustomMaterialTimePicker)
                // 시계 화면(CLOCK)을 기본 설정
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                // 12시간 형식(AM/PM)으로 설정
                .setTimeFormat(TimeFormat.CLOCK_12H)
                // 현재 시간을 다이얼로그의 초기 시간으로 설정
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("방문 시간 설정") // 다이얼로그의 제목을 설정
                .build();

        // 'OK' 버튼을 눌렀을 때의 동작을 정의
        timePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();

            // 서버에 저장할 24시간 형식 문자열(예: "17:20")
            String timeForServer = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute);

            // Fragment에 시간 업데이트를 요청
            listener.onTimeUpdateRequested(currentItem.getPlaceId(), timeForServer, position);
        });

        //  TimePicker를 화면에 보여줌
        timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "MaterialTimePicker");

    }

    // --- 시간 포맷을 변환하는 메서드 ---
    private String formatTime(String time) {
        if (time == null || time.isEmpty()) {
            return "";
        }
        try {
            if (time.length() > 5) {
                time = time.substring(0, 5);
            }
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(time);
            SimpleDateFormat outputFormat = new SimpleDateFormat("a hh:mm", Locale.getDefault());
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            Log.e("LocationAdapter", "시간 포맷 변경 중 오류 발생", e);
            return time;
        }
        return time;
    }



    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumber;
        private final Context context;

        /**
         * 🟡 수정: 생성자에서 courseId, dayId, fragment 대신 Adapter 전체를 받습니다.
         * 이렇게 하면 Adapter의 최신 상태를 언제든지 참조할 수 있습니다.
         */
        public AddButtonViewHolder(@NonNull View itemView, LocationAdapter adapter) {
            super(itemView);
            this.context = itemView.getContext();
            tvNumber = itemView.findViewById(R.id.tvNumber);

            // "장소 추가하기" 버튼 전체에 클릭 리스너 설정
            itemView.setOnClickListener(v -> {
                // 🟡 수정: 클릭되는 바로 그 순간에 Adapter로부터 최신 courseId와 dayId를 가져옵니다.
                // 이렇게 하면 항상 현재 선택된 탭의 올바른 ID를 사용할 수 있습니다.
                long currentCourseId = adapter.courseId;
                long currentDayId = adapter.getCurrentDayId(); // getCurrentDayId() 메서드 사용

                // 🟡 디버깅 로그 5: '+ 장소 추가' 버튼이 클릭되었을 때
                Log.d("DEBUG_DAY_ID", "[AddButton Click] '+ 장소 추가' 버튼 클릭. 현재 Adapter가 가진 dayId: " + currentDayId);

                // Adapter가 가지고 있는 fragment 참조를 사용하여 Activity 실행을 요청합니다.
                adapter.fragment.launchAddLocationActivity(currentCourseId, currentDayId);
            });
        }

        public void bind(String number) {
            tvNumber.setText(number);
        }
    }


}


//package com.example.yourtrip.mytrip;
//
//import android.app.TimePickerDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import java.util.Calendar;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.model.LocationItem;
//import com.example.yourtrip.mytrip.model.PlaceTimeRequest;
//
//
//import java.util.List;
//
///**
// * 일차별 장소 목록을 표시하는 RecyclerView 어댑터.
// * '장소 카드'와 '장소 추가 버튼' 두 가지 뷰 타입을 처리
// */
//public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    // [추가] Fragment와 통신하기 위한 인터페이스 정의
//    public interface OnLocationInteractionListener {
//        void onTimeUpdateRequested(long placeId, String time, int position);
//    }
//
//    // 뷰 타입을 구분하기 위한 상수. 숫자는 어떤 값이든 상관없지만, 서로 달라야 합니다.
//    private static final int VIEW_TYPE_LOCATION = 1;
//    private static final int VIEW_TYPE_ADD_BUTTON = 2;
//
//    // 장소(LocationItem)와 추가 버튼(특별한 값, 여기서는 String)을 모두 담을 수 있는 List
//    private final List<Object> items;
//    private long courseId;
//    private long dayId; // final 제거
//    private final CreateCourseDayDetailFragment fragment; //Fragment 참조 변수
//    private final OnLocationInteractionListener listener;
//
//    public long getCurrentDayId() {
//        return this.dayId;
//    }
//
//
//    //  생성자를 하나로 통일: 모든 필요한 정보를 받도록 함
//    public LocationAdapter(List<Object> items, long courseId, long dayId, CreateCourseDayDetailFragment fragment) {
//        this.items = items;
//        this.courseId = courseId;
//        this.dayId = dayId;
//        this.fragment = fragment;
//
//        // [수정] 생성자에서 Fragment를 리스너로 캐스팅
//        if (fragment instanceof OnLocationInteractionListener) {
//            this.listener = (OnLocationInteractionListener) fragment;
//        } else {
//            throw new RuntimeException(fragment.toString() + " must implement OnLocationInteractionListener");
//        }
//        // [수정] ApiService 초기화 코드 제거
//    }
//
//
//    // 프래그먼트에서 새로운 dayId를 전달받아, 어댑터의 dayId 값을 업데이트
//    public void updateDayId(long newDayId) {
//        //  디버깅 로그 4: Adapter의 dayId가 업데이트될 때
//        Log.d("DEBUG_DAY_ID", "[LocationAdapter update] dayId가 " + this.dayId + "에서 " + newDayId + " (으)로 업데이트됨.");
//        this.dayId = newDayId;
//    }
//
//
//
//    // 새로운 장소 아이템을 리스트에 추가, RecyclerView 갱신
//    public void addItem(LocationItem newItem) {
//        // 리스트의 맨 마지막에는 항상 '추가 버튼'이 있으므로,
//        // 그 바로 앞 위치에 새로운 장소 아이템을 추가합니다.
//        int position = items.size() - 1;
//        items.add(position, newItem);
//
//        // 아이템이 추가된 위치를 어댑터에 알려줘서 화면을 효율적으로 갱신합니다.
//        notifyItemInserted(position);
//        // 번호가 모두 바뀌었으므로, 전체 아이템의 UI를 다시 그리도록 알려줍니다.
//        // (n번째 '추가' 버튼이 n+1번째가 되므로)
//        notifyItemRangeChanged(position, items.size());
//    }
//
//
//    //새로운 장소 목록으로 전체 데이터 교체 및 화면 갱신
//    public void updateItems(List<LocationItem> newPlaces) {
//        // 1. 기존 아이템 리스트를 완전히 비웁니다.
//        items.clear();
//
//        // 2. 서버에서 받아온 새로운 장소 목록이 null이 아닐 경우, 모두 추가합니다.
//        if (newPlaces != null) {
//            items.addAll(newPlaces);
//        }
//
//        // 3. 리스트의 맨 마지막에 '+ 장소 추가하기' 버튼을 위한 데이터를 추가합니다.
//        items.add("ADD_BUTTON");
//
//        // 4. 데이터셋 전체가 변경되었음을 알려 화면을 완전히 새로고침합니다.
//        notifyDataSetChanged();
//    }
//
//    // [추가] Fragment로부터 호출받아 시간 데이터와 UI를 최종 업데이트하는 메서드
//    public void updateTime(int position, String time) {
//        if (position >= 0 && position < items.size()) {
//            Object item = items.get(position);
//            if (item instanceof LocationItem) {
//                // 이 코드가 정상 동작하려면 LocationItem.java에 setStartTime 메서드가 있어야 합니다.
//                ((LocationItem) item).setStartTime(time); // 데이터 모델 값 변경
//                notifyItemChanged(position); // 해당 아이템 뷰만 새로고침
//            }
//        }
//    }
//
//
//
//    // 리스트 아이템 타입 판단 -> 뷰 타입 반환
//    @Override
//    public int getItemViewType(int position) {
//        // 현재 위치의 아이템이 LocationItem 클래스의 인스턴스(객체)이면
//        if (items.get(position) instanceof LocationItem) {
//            return VIEW_TYPE_LOCATION; // '장소 카드' 타입을 반환
//        } else {
//            return VIEW_TYPE_ADD_BUTTON; // 그 외에는 '추가 버튼' 타입을 반환
//        }
//    }
//
//    // 뷰 타입에 따라 xml 레이아웃 inflate해서 ViewHolder 생성
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        // '장소 카드' 타입일 경우
//        if (viewType == VIEW_TYPE_LOCATION) {
//            View view = inflater.inflate(R.layout.item_trip_location_card, parent, false);
//            return new LocationViewHolder(view);
//        }
//        // '추가 버튼' 타입일 경우
//        else { // viewType == VIEW_TYPE_ADD_BUTTON
//            View view = inflater.inflate(R.layout.view_add_location_button, parent, false);
//            // ViewHolder 생성 시 courseId와 dayId를 직접 전달
//
//            // 🟡 수정: ViewHolder 생성 시 모든 필요한 정보 전달
////            return new AddButtonViewHolder(view, courseId, dayId, fragment);
//            return new AddButtonViewHolder(view, this);
//        }
//    }
//
//
//    // ViewHolder에 데이터 바인딩
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        // 순번은 0부터 시작하는 position에 1을 더해서 만듭니다.
//        String number = String.valueOf(position + 1);
//
//        // ViewHolder의 타입에 따라 다른 작업을 수행합니다.
//        if (holder.getItemViewType() == VIEW_TYPE_LOCATION) {
//            // LocationViewHolder로 형변환
//            LocationViewHolder locationHolder = (LocationViewHolder) holder;
//            // 리스트에서 LocationItem 객체를 가져옴
//            LocationItem locationItem = (LocationItem) items.get(position);
//            // ViewHolder의 bind 메서드를 호출하여 데이터를 채움
//            locationHolder.bind(locationItem, number);
//        }
//        else { // VIEW_TYPE_ADD_BUTTON
//            // AddButtonViewHolder로 형변환
//            AddButtonViewHolder addButtonHolder = (AddButtonViewHolder) holder;
//            // ViewHolder의 bind 메서드를 호출하여 데이터를 채움
//            addButtonHolder.bind(number);
//        }
//    }
//
//
//    //전체 아이템 개수 반환
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//
//    // --- ViewHolder 클래스들 ---
//
//    // 장소 카드 (item_trip_location.xml)의 뷰들을 관리하는 ViewHolder
//    public static class LocationViewHolder extends RecyclerView.ViewHolder {
//        private final TextView tvNumber;
//        private final TextView tvTime;
//        private final ImageView btnDelete;
//        private final TextView tvPlaceName;
//        private final TextView tvAddress;
//        private final ImageView ivMap;
//        private final LinearLayout btnAddPhoto;
//        private final EditText etMemo;
//
//        public LocationViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvNumber = itemView.findViewById(R.id.tvNumber);
//            tvTime = itemView.findViewById(R.id.tvTime);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
//            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
//            tvAddress = itemView.findViewById(R.id.tvAddress);
//            ivMap = itemView.findViewById(R.id.ivMap);
//            btnAddPhoto = itemView.findViewById(R.id.btnAddPhoto); // 사진 추가 버튼
//            etMemo = itemView.findViewById(R.id.etMemo);
//        }
//
//        public void bind(LocationItem item, String number) {
//            tvNumber.setText(number);
//            tvPlaceName.setText(item.getPlaceName());
//            tvAddress.setText(item.getPlaceLocation());
//
//            // 아이템의 startTime 값에 따라 초기 UI 설정
//            if (item.getStartTime() != null && !item.getStartTime().isEmpty()) {
//                tvTime.setText(item.getStartTime());
//                tvTime.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black)); // 예시: 검은색으로
//            } else {
//                tvTime.setText("눌러서 시간 입력");
//                tvTime.setTextColor(itemView.getContext().getResources().getColor(R.color.gray_500)); // 예시: 회색으로
//            }
//
//
//            // --- 클릭 이벤트 리스너 설정 ---
//            // [수정] 클릭 시 Adapter에 구현된 showTimePickerDialog를 호출하도록 변경
//            tvTime.setOnClickListener(v -> {
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION) {
//                    showTimePickerDialog((LocationItem) items.get(position), position);
//                }
//            });
//
//            btnDelete.setOnClickListener(v -> {
//                // TODO: 이 아이템을 삭제하는 로직 구현 (API 호출 등)
//            });
//
//            btnAddPhoto.setOnClickListener(v -> {
//                // TODO: 갤러리를 열어 사진을 선택하는 로직 구현
//            });
//        }
//    }
//
//    // [수정] TimePickerDialog 관련 메서드를 ViewHolder 밖으로 이동
//    private void showTimePickerDialog(LocationItem currentItem, int position) {
//        if (listener == null) return;
//
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//
//        // ViewHolder의 itemView에서 context를 가져올 수 없으므로, fragment의 context를 사용
//        TimePickerDialog timePickerDialog = new TimePickerDialog(
//                fragment.requireContext(),
//                (view, selectedHour, selectedMinute) -> {
//                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
//                    // 인터페이스를 통해 Fragment에 이벤트 전달
//                    listener.onTimeUpdateRequested(currentItem.getPlaceId(), formattedTime, position);
//                },
//                hour, minute, true
//        );
//        timePickerDialog.show();
//    }
//
//
//
//    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
//        private final TextView tvNumber;
//        private final Context context;
//
//        /**
//         * 🟡 수정: 생성자에서 courseId, dayId, fragment 대신 Adapter 전체를 받습니다.
//         * 이렇게 하면 Adapter의 최신 상태를 언제든지 참조할 수 있습니다.
//         */
//        public AddButtonViewHolder(@NonNull View itemView, LocationAdapter adapter) {
//            super(itemView);
//            this.context = itemView.getContext();
//            tvNumber = itemView.findViewById(R.id.tvNumber);
//
//            // "장소 추가하기" 버튼 전체에 클릭 리스너 설정
//            itemView.setOnClickListener(v -> {
//                // 🟡 수정: 클릭되는 바로 그 순간에 Adapter로부터 최신 courseId와 dayId를 가져옵니다.
//                // 이렇게 하면 항상 현재 선택된 탭의 올바른 ID를 사용할 수 있습니다.
//                long currentCourseId = adapter.courseId;
//                long currentDayId = adapter.getCurrentDayId(); // getCurrentDayId() 메서드 사용
//
//                // 🟡 디버깅 로그 5: '+ 장소 추가' 버튼이 클릭되었을 때
//                Log.d("DEBUG_DAY_ID", "[AddButton Click] '+ 장소 추가' 버튼 클릭. 현재 Adapter가 가진 dayId: " + currentDayId);
//
//                // Adapter가 가지고 있는 fragment 참조를 사용하여 Activity 실행을 요청합니다.
//                adapter.fragment.launchAddLocationActivity(currentCourseId, currentDayId);
//            });
//        }
//
//        public void bind(String number) {
//            tvNumber.setText(number);
//        }
//    }
//
//
//}
