package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;// Serializable은 이제 Broadcast를 사용하지 않으므로 필수는 아니지만,
// 나중에 다른 화면으로 객체를 전달할 때 필요할 수 있으니 유지하는 것이 좋습니다.
public class MyCourseListItemResponse implements Serializable {

    // 서버 응답의 courseId를 받기 위해 필드 추가 및 @SerializedName 사용
    @SerializedName("courseId")
    private Long courseId;

    @SerializedName("title")
    private String title;

    @SerializedName("location")
    private String location;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("memberCount")
    private int memberCount;



    // 생성자: 필드를 초기화하는 올바른 코드로 수정합니다.
    public MyCourseListItemResponse(Long courseId, String title, String location, String startDate, String endDate, int memberCount) {
        this.courseId = courseId;
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberCount = memberCount;
    }

    // 각 필드의 값을 가져오는 Getter 메서드들
    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getMemberCount() {
        return memberCount;
    }
}
