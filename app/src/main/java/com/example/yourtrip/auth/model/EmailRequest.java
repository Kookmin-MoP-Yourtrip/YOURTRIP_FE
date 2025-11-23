package com.example.yourtrip.auth.model;

public class EmailRequest {
    private String email;

    // 생성자
    public EmailRequest(String email) {
        this.email = email;
    }

    // getter, setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
