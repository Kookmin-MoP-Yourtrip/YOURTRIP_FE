package com.example.yourtrip.mytrip.model;

import java.util.ArrayList;

public class AICourseCreateRequest {
    private String startDate;
    private String endDate;
    private String location;
    private ArrayList<String> keywords;

    public AICourseCreateRequest(String startDate, String endDate, String location, ArrayList<String> keywords){
        this.startDate=startDate;
        this.endDate=endDate;
        this.location=location;
        this.keywords=keywords;
    }


}
