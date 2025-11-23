package com.example.yourtrip.commonUtil;

import java.util.ArrayList;
import java.util.List;

public class TagConverter {

    // UI 태그 한 개를 서버 코드로 변환
    public static String toServerCode(String tag) {

        switch (tag) {

            // -------- 이동수단 --------
            case "뚜벅이": return "WALK";
            case "자차": return "CAR";

            // -------- 동행 대상 --------
            case "혼자": return "SOLO";
            case "연인": return "COUPLE";
            case "친구": return "FRIENDS";
            case "가족": return "FAMILY";

            // -------- 기간 --------
            case "하루": return "ONE_DAY";
            case "1박 2일": return "TWO_DAYS";
            case "주말": return "WEEKEND";
            case "장기": return "LONG";

            // -------- 테마 --------
            case "힐링": return "HEALING";
            case "액티비티": return "ACTIVITY";
            case "맛집탐방": return "FOOD";
            case "감성": return "SENSIBILITY";
            case "문화/전시": return "CULTURE";
            case "자연": return "NATURE";
            case "쇼핑": return "SHOPPING";

            // -------- 예산 --------
            case "가성비": return "COST_EFFECTIVE";
            case "보통": return "NORMAL";
            case "프리미엄": return "PREMIUM";
        }

        return null; // 매칭 안되면 null
    }

    // UI 태그 리스트를 서버 코드 리스트로 변환
    public static List<String> toServerCodes(List<String> uiTagList) {
        if (uiTagList == null) return null;

        List<String> result = new ArrayList<>();

        for (String tag : uiTagList) {
            String code = toServerCode(tag);
            if (code != null) result.add(code);
        }

        return result;
    }
}
