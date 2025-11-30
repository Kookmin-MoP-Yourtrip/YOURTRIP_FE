package com.example.yourtrip.mytrip.create_direct;

import android.net.Uri; // 사진 Uri를 다루기 위해 import

// RecyclerView의 각 아이템에 대한 데이터를 담는 파일
public class DayDetailItem {

    // --- 아이템의 종류를 구분하기 위한 '타입' 상수 정의 ---
    public static final int TYPE_LOCATION = 0;   // '장소 카드' 타입
    public static final int TYPE_ADD_BUTTON = 1; // '+ 장소 추가하기' 버튼 타입

    // --- 공통 필드 ---
    private final int viewType; // 이 아이템의 타입을 저장할 변수

    // --- 장소 카드(TYPE_LOCATION) 전용 필드 ---
    private String placeName;
    private String placeAddress;
    private String startTime; // 시간 (선택값) - 예: "오전 9:00"
    private String memo;      // 메모 (선택값)
    private Uri photoUri;     // 갤러리에서 선택한 사진의 Uri (선택값)

    // --- 생성자 1: '+ 장소 추가하기' 버튼을 위한 생성자 ---
    public DayDetailItem(int viewType) {
        this.viewType = viewType;
    }

    // --- 생성자 2: '장소 카드'를 위한 생성자 (필수값만 받음) ---
    public DayDetailItem(int viewType, String placeName, String placeAddress) {
        this.viewType = viewType;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
    }

    // --- Getter 메서드들: 모든 데이터에 접근 가능 ---
    public int getViewType() {
        return viewType;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getMemo() {
        return memo;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    // --- Setter 메서드들: 선택값들을 나중에 설정할 수 있도록 함 ---
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
