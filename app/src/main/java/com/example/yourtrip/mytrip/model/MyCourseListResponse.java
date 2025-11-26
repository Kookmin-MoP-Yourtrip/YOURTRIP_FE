package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

//"myCourses" 라는 이름의 '리스트'를 담는 역할
public class MyCourseListResponse {

    @SerializedName("myCourses")
    private List<MyCourseListItemResponse> myCourses;

    public List<MyCourseListItemResponse> getMyCourses() {
        return myCourses;
    }
}
