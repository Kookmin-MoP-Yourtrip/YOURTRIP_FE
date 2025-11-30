package com.example.yourtrip.mytrip.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagMapper {

    private static final String TAG = "TagMapper";

    // 태그 매핑 테이블
    private static final Map<String, String> TAG_CODE_MAP = new HashMap<String, String>() {{
        // 이동수단
        put("뚜벅이", "WALK");
        put("자차", "CAR");

        // 동행유형
        put("혼자", "SOLO");
        put("연인", "COUPLE");
        put("친구", "FRIEND");
        put("가족", "FAMILY");

        // 분위기
        put("맛집탐방", "FOOD");
        put("힐링", "HEALING");
        put("액티비티", "ACTIVITY");
        put("감성", "SENTIMENTAL");
        put("문화·전시", "CULTURE");
        put("자연", "NATURE");
        put("쇼핑", "SHOPPING");

        // 여행 기간
        put("하루", "ONE_DAY");
        put("1박2일", "TWO_DAYS");
        put("주말", "WEEKEND");
        put("장기", "LONG_TERM");

        // 예산
        put("가성비", "COST_EFFECTIVE");
        put("보통", "NORMAL");
        put("프리미엄", "PREMIUM");
    }};

    // 리스트 변환 함수
    public static List<String> convert(List<String> tags) {
        List<String> keywordCodes = new ArrayList<>();

        for (String tag : tags) {
            String code = TAG_CODE_MAP.get(tag);

            if (code != null) {
                keywordCodes.add(code);
            } else {
                Log.w(TAG, "⚠ 매핑되지 않은 태그: " + tag);
            }
        }

        return keywordCodes;
    }
}

