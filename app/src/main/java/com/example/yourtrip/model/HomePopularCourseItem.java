package com.example.yourtrip.model;
import java.util.List;


public class HomePopularCourseItem {
    public String title;
    public String location;
    public int imageRes;
    public int likeCount;
    public List<String> tags;


    public HomePopularCourseItem(String title, String location, int imageRes, List<String> tags) {
        this.title = title;
        this.location = location;
        this.imageRes = imageRes;
        this.likeCount = 0;
        this.tags = tags; // 기본 테스트용
    }
}