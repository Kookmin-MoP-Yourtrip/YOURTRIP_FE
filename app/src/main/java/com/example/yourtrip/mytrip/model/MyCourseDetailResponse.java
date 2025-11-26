// CourseDetailResponse.java
package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class MyCourseDetailResponse implements Serializable { // Serializable 구현
    private long courseId;
    private String title;
    private String location;
    private String startDate;
    private String endDate;
    private int memberCount;
    private String role;
    private String updatedAt;

    // API의 daySchedules 필드와 매핑
    @SerializedName("daySchedules")
    private List<DaySchedule> daySchedules;

    // Getter들...
    public long getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getMemberCount() { return memberCount; }
    public String getRole() { return role; }
    public String getUpdatedAt() { return updatedAt; }
    public List<DaySchedule> getDaySchedules() { return daySchedules; }

    // DaySchedule 내부 클래스도 Serializable 구현
    public static class DaySchedule implements Serializable {
        private long dayId;
        private int day;

        // Getter들...
        public long getDayId() { return dayId; }
        public int getDay() { return day; }
    }
}
