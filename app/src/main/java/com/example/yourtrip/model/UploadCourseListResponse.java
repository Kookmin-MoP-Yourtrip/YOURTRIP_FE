package com.example.yourtrip.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UploadCourseListResponse {

    @SerializedName("uploadCourses")
    public List<UploadCourseItem> uploadCourses;
}
