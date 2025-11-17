package com.example.yourtrip.commonUtil;

import com.example.yourtrip.R;

import java.util.HashMap;
import java.util.Map;

public class TagColorManager {

    public static class TagStyle {
        public int tintColor;
        public int textColor;

        public TagStyle(int tintColor, int textColor) {
            this.tintColor = tintColor;
            this.textColor = textColor;
        }
    }

    private static final Map<String, TagStyle> map = new HashMap<>();

    static {
        // 이동수단
        map.put("뚜벅이", new TagStyle(R.color.tag_yellow, R.color.gray_600));
        map.put("자차", new TagStyle(R.color.tag_yellow, R.color.gray_600));

        // 누구랑
        map.put("혼자", new TagStyle(R.color.tag_pink, R.color.gray_600));
        map.put("연인", new TagStyle(R.color.tag_pink, R.color.gray_600));
        map.put("친구", new TagStyle(R.color.tag_pink, R.color.gray_600));
        map.put("가족", new TagStyle(R.color.tag_pink, R.color.gray_600));

        // 테마
        map.put("힐링", new TagStyle(R.color.tag_skyblue, R.color.gray_600));
        map.put("액티비티", new TagStyle(R.color.tag_skyblue, R.color.gray_600));
        map.put("맛집탐방", new TagStyle(R.color.tag_skyblue, R.color.gray_600));
        map.put("감성", new TagStyle(R.color.tag_skyblue, R.color.gray_600));
        map.put("문화/전시", new TagStyle(R.color.tag_skyblue, R.color.gray_600));
        map.put("자연", new TagStyle(R.color.tag_skyblue, R.color.gray_600));
        map.put("쇼핑", new TagStyle(R.color.tag_skyblue, R.color.gray_600));

        // 기간
        map.put("하루", new TagStyle(R.color.tag_green, R.color.gray_600));
        map.put("1박 2일", new TagStyle(R.color.tag_green, R.color.gray_600));
        map.put("주말", new TagStyle(R.color.tag_green, R.color.gray_600));
        map.put("장기", new TagStyle(R.color.tag_green, R.color.gray_600));

        // 예산
        map.put("가성비", new TagStyle(R.color.tag_purple, R.color.gray_600));
        map.put("보통", new TagStyle(R.color.tag_purple, R.color.gray_600));
        map.put("프리미엄", new TagStyle(R.color.tag_purple, R.color.gray_600));
    }

    public static TagStyle get(String tagName) {
        return map.getOrDefault(
                tagName,
                new TagStyle(R.color.tag_blue, R.color.white)
        );
    }
}
