package com.example.yourtrip.mytrip.create_direct;

import android.content.Context;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.LocationItem;

import android.app.AlertDialog;
import android.widget.NumberPicker;

import com.example.yourtrip.mytrip.model.PlaceImage;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * 일차별 장소 목록을 표시하는 RecyclerView 어댑터.
 * '장소 카드'와 '장소 추가 버튼' 두 가지 뷰 타입을 처리
 */
public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Fragment와 통신하기 위한 인터페이스 정의
    public interface OnLocationInteractionListener {
        void onTimeUpdateRequested(long placeId, String time, int position);
        void onPhotoAddRequested(long placeId, int position);
        void onMemoUpdateRequested(long placeId, String memo, int position);
        void onPlaceDeleteRequested(long placeId, int position);
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

        // 생성자에서 Fragment를 리스너로 캐스팅
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
        // 리스트의 맨 마지막에는 항상 '추가 버튼'이 있으므로
        // 그 바로 앞 위치에 새로운 장소 아이템을 추가
        int position = items.size() - 1;
        items.add(position, newItem);
        
        notifyItemInserted(position);
        // 번호가 모두 바뀌었으므로, 전체 아이템의 UI를 다시 그림
        notifyItemRangeChanged(position, items.size());
    }

    // 삭제된 아이템을 리스트에서 제거하고 화면을 갱신하는 메서드-
    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        }
    }


    //새로운 장소 목록으로 전체 데이터 교체 및 화면 갱신
    public void updateItems(List<LocationItem> newPlaces) {
        items.clear();
        
        if (newPlaces != null) {
            items.addAll(newPlaces);
        }
        // 리스트의 맨 마지막에 '+ 장소 추가하기' 버튼을 위한 데이터를 추가
        items.add("ADD_BUTTON");

        // 화면을 완전히 새로고침
        notifyDataSetChanged();
    }

    // Fragment로부터 호출받아 시간 데이터와 UI를 최종 업데이트하는 메서드
    public void updateTime(int position, String time) {
        if (position >= 0 && position < items.size()) {
            Object item = items.get(position);
            if (item instanceof LocationItem) {
                ((LocationItem) item).setStartTime(time); // 데이터 모델 값 변경
                notifyItemChanged(position); // 해당 아이템 뷰만 새로고침
            }
        }
    }

    //Fragment로 부터 호출받아 특정 아이템 이미지 갱신하는 메서드
    public void updateItemImage(int position, String newImageUrl) {
        if (position >= 0 && position < items.size()) {

            Object obj = items.get(position);
            if (obj instanceof LocationItem) {

                LocationItem item = (LocationItem) obj;

                // placeImages가 없으면 새로 생성
                if (item.getPlaceImages() == null) {
                    item.setPlaceImages(new ArrayList<>());
                }

                // PlaceImage 객체 생성해서 넣기
                PlaceImage image = new PlaceImage(newImageUrl);
                item.getPlaceImages().add(image);

                notifyItemChanged(position);
            }
        }
    }

//    public void updateItemImage(int position, String newImageUrl) {
//        if (position >= 0 && position < items.size()) {
//            Object item = items.get(position);
//            if (item instanceof LocationItem) {
//                // 1. 데이터 모델에 새로운 이미지 URL을 추가합니다.
//                ((LocationItem) item).addImageUrl(newImageUrl);
//                // 2. RecyclerView에게 해당 위치의 아이템만 뷰를 새로 그리라고 알려줍니다.
//                //    이렇게 하면 전체 목록을 다시 불러올 필요 없이 부드럽게 화면이 갱신됩니다.
//                notifyItemChanged(position);
//            }
//        }
//    }

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
    public class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumber;
        private final TextView tvTime;
        private final ImageView btnDelete;
        private final TextView tvPlaceName;
        private final TextView tvAddress;
//        private final ImageView ivMap;
        private final ImageView ivAddedPhoto;
        private final LinearLayout btnAddPhoto;
        private final EditText etMemo;

        // 메모 자동 저장을 위한 핸들러와 러너블
        private final Handler memoSaveHandler = new Handler(Looper.getMainLooper());
        private Runnable memoSaveRunnable;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
//            ivMap = itemView.findViewById(R.id.ivMap);
            ivAddedPhoto = itemView.findViewById(R.id.ivAddedPhoto);
            btnAddPhoto = itemView.findViewById(R.id.btnAddPhoto);
            etMemo = itemView.findViewById(R.id.etMemo);
        }

        public void bind(LocationItem item, String number) {
            tvNumber.setText(number);
            tvPlaceName.setText(item.getPlaceName());
            etMemo.setText(item.getMemo());

            // 주소 정보(placeLocation)가 있는지 확인
            String address = item.getPlaceLocation();
            if (address != null && !address.trim().isEmpty()) {
                // 주소가 있으면 보여주고, 텍스트 설정
                tvAddress.setText(address);
                tvAddress.setVisibility(View.VISIBLE);
            } else {
                tvAddress.setVisibility(View.GONE);
            }


            // 아이템의 startTime 값에 따라 초기 UI 설정
            if (item.getStartTime() != null && !item.getStartTime().isEmpty()) {
                // 서버에서 받은 시간(HH:mm 또는 HH:mm:ss)을 "오전/오후 hh:mm" 형식으로 변환
                tvTime.setText(formatTime(item.getStartTime()));
                tvTime.setTextColor(itemView.getContext().getResources().getColor(R.color.blue_main));
            } else {
                tvTime.setText("눌러서 시간 입력");
                tvTime.setTextColor(itemView.getContext().getResources().getColor(R.color.gray_500));
            }


            // --- 이미지 표시 로직 (Glide 사용) ---
            List<PlaceImage> images = item.getPlaceImages();

            if (item.getPlaceImages() != null && !item.getPlaceImages().isEmpty()) {
                ivAddedPhoto.setVisibility(View.VISIBLE);
                btnAddPhoto.setVisibility(View.GONE);

                String url = item.getPlaceImages().get(0).getImageUrl();

                Glide.with(itemView.getContext())
                        .load(url)
                        .centerCrop()
                        .into(ivAddedPhoto);

            } else {
                ivAddedPhoto.setVisibility(View.GONE);
                btnAddPhoto.setVisibility(View.VISIBLE);
            }
//            if (item.getImageUrls() != null && !item.getImageUrls().isEmpty()) {
//                // 이미지가 있는 경우
//                ivAddedPhoto.setVisibility(View.VISIBLE);
//                btnAddPhoto.setVisibility(View.GONE);
//
//                // Glide로 첫 번째 이미지를 로드
//                Glide.with(itemView.getContext())
//                        .load(item.getImageUrls().get(0))
//                        .centerCrop()
//                        .into(ivAddedPhoto);
//            } else {
//                // 이미지가 없는 경우
//                ivAddedPhoto.setVisibility(View.GONE);
//                btnAddPhoto.setVisibility(View.VISIBLE);
//            }


            // --- 클릭 이벤트 리스너 설정 ---
            tvTime.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Adapter 클래스에 정의된 메서드를 호출
                    showTimePickerDialog((LocationItem) items.get(position), position);
                }
            });

            // 사진 추가 클릭 리스너 설정
            btnAddPhoto.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPhotoAddRequested(item.getPlaceId(), position);
                }
            });


            // --- 메모 입력 리스너 ---
            // 수정 : 텍스트 변경 감지 리스너 (자동 저장 로직)
            // 이전 리스너를 제거하여 중복 실행 방지
            if (etMemo.getTag() instanceof TextWatcher) {
                etMemo.removeTextChangedListener((TextWatcher) etMemo.getTag());
            }

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 텍스트 변경 전에 예약된 저장 작업을 취소
                    memoSaveHandler.removeCallbacks(memoSaveRunnable);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 이 메서드에서는 아무것도 하지 않음
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // 텍스트 변경이 끝난 후 실행될 저장 작업을 정의
                    memoSaveRunnable = () -> {
                        int position = getAdapterPosition();
                        String newMemo = s.toString();

                        // 기존 메모와 다를 경우에만 업데이트 요청 (불필요한 API 호출 방지)
                        if (position != RecyclerView.NO_POSITION && listener != null && !newMemo.equals(item.getMemo())) {
                            listener.onMemoUpdateRequested(item.getPlaceId(), newMemo, position);
                        }
                    };
                    // 1초 후에 위에서 정의한 저장 작업을 실행하도록 예약
                    memoSaveHandler.postDelayed(memoSaveRunnable, 1000);
                }
            };

            etMemo.addTextChangedListener(textWatcher);
            etMemo.setTag(textWatcher);


            //우선은 포커스르 잃었을 때로 설정했음  -> 추후에 협업 기능이 들어오면 수정하기
//            etMemo.setOnFocusChangeListener((v, hasFocus) -> {
//                // 포커스를 잃었을 때 (입력이 끝났다고 간주) API 호출
//                if (!hasFocus) {
//                    int position = getAdapterPosition();
//                    String newMemo = etMemo.getText().toString();
//                    // 기존 메모와 다를 경우에만 업데이트 요청
//                    if (position != RecyclerView.NO_POSITION && listener != null && !newMemo.equals(item.getMemo())) {
//                        listener.onMemoUpdateRequested(item.getPlaceId(), newMemo, position);
//                    }
//                }
//            });


            // --- 장소 삭제 리스너 ---
            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPlaceDeleteRequested(item.getPlaceId(), position);
                }
            });
        }
    }

    // TimePickerDialog 관련 메서드를 ViewHolder 밖, Adapter 클래스 내부로 이동
    private void showTimePickerDialog(LocationItem currentItem, int position) {
        if (listener == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.requireContext());

//        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);

        LayoutInflater inflater = fragment.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_show_time_picker, null);
        builder.setView(dialogView);

        // 다이얼로그 내부의 뷰
        final NumberPicker hourPicker = dialogView.findViewById(R.id.picker_hour);
        final NumberPicker minutePicker = dialogView.findViewById(R.id.picker_minute);
        final Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        final Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // NumberPicker의 범위
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        hourPicker.setFormatter(i -> String.format(Locale.US, "%02d", i));
        minutePicker.setFormatter(i -> String.format(Locale.US, "%02d", i));

        // 기존에 저장된 시간이 있으면 초기값으로 설정
        if (currentItem.getStartTime() != null && !currentItem.getStartTime().isEmpty()) {
            try {
                // "HH:mm" 형식의 문자열에서 파싱
                int initialHour = Integer.parseInt(currentItem.getStartTime().substring(0, 2));
                int initialMinute = Integer.parseInt(currentItem.getStartTime().substring(3, 5));
                hourPicker.setValue(initialHour);
                minutePicker.setValue(initialMinute);
            } catch (Exception e) {
                // 시간 형식이 잘못된 경우, 기본값(예: 0시 0분)으로 설정
                hourPicker.setValue(0);
                minutePicker.setValue(0);
            }
        } else {
            // 기존 시간이 없으면 기본값으로 설정
            hourPicker.setValue(0);
            minutePicker.setValue(0);
        }

        //  다이얼로그 생성
        final AlertDialog dialog = builder.create();

        // 확인 버튼 클릭 리스너를 설정
        btnConfirm.setOnClickListener(v -> {
            int selectedHour = hourPicker.getValue();
            int selectedMinute = minutePicker.getValue();
            String timeForServer = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute);

            listener.onTimeUpdateRequested(currentItem.getPlaceId(), timeForServer, position);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
            return time; // 파싱 실패 시 원본 시간 반환
        }
        return time; // 성공적으로 파싱되었으나 date가 null인 경우
    }



    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumber;
        private final Context context;

        /**
         * 생성자에서 courseId, dayId, fragment 대신 Adapter 전체를 받음
         * 이렇게 하면 Adapter의 최신 상태를 언제든지 참조할 수 있음
         */
        public AddButtonViewHolder(@NonNull View itemView, LocationAdapter adapter) {
            super(itemView);
            this.context = itemView.getContext();
            tvNumber = itemView.findViewById(R.id.tvNumber);

            // "장소 추가하기" 버튼 전체에 클릭 리스너 설정
            itemView.setOnClickListener(v -> {
                // 클릭되는 바로 그 순간에 Adapter로부터 최신 courseId와 dayId를 가져옴
                long currentCourseId = adapter.courseId;
                long currentDayId = adapter.getCurrentDayId(); // getCurrentDayId() 메서드 사용

                // 🟡 디버깅 로그 5: '+ 장소 추가' 버튼이 클릭되었을 때
                Log.d("DEBUG_DAY_ID", "[AddButton Click] '+ 장소 추가' 버튼 클릭. 현재 Adapter가 가진 dayId: " + currentDayId);

                // Adapter가 가지고 있는 fragment 참조를 사용하여 Activity 실행을 요청
                adapter.fragment.launchAddLocationActivity(currentCourseId, currentDayId);
            });
        }

        public void bind(String number) {
            tvNumber.setText(number);
        }
    }


}

