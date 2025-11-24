package com.example.yourtrip.network;

import java.util.List;

public class NaverGeocodeResponse {

    public List<Address> addresses;

    public static class Address {
        public String roadAddress;
        public String jibunAddress;
        public String x;   // 경도 (String으로 옴)
        public String y;   // 위도
    }
}

