package com.example.yourtrip.network;

import java.util.List;

public class NaverSearchResponse {

    public String lastBuildDate;   // 검색 결과 생성 시간
    public int total;              // 총 검색 결과 개수
    public int start;              // 검색 시작 위치
    public int display;            // 한 번에 표시할 결과 개수

    // 네이버 지역 검색의 개별 결과 목록
    public List<Item> items;

    // 지역 검색 item 구조
    public static class Item {
        public String title;        // 업체/기관 이름
        public String link;         // 상세 정보 URL
        public String category;     // 분류 정보
        public String description;  // 설명
        public String telephone;    // 전화번호 (현재는 빈 문자열일 수 있음)
        public String address;      // 지번 주소
        public String roadAddress;  // 도로명 주소
        public String mapx;         // x 좌표(WGS84 기준, 문자열로 옴)
        public String mapy;         // y 좌표(WGS84 기준, 문자열로 옴)
    }
}
