package com.example.yourtrip.mytrip.model;

import java.util.ArrayList;
import java.util.List;

public class AICourseCreateRequest {
    private String startDate;
    private String endDate;
    private String location;
    private List<String> keywords;

    public AICourseCreateRequest(String startDate, String endDate, String location, List<String> keywords){
        this.startDate=startDate;
        this.endDate=endDate;
        this.location=location;
        this.keywords=keywords;
    }


}
