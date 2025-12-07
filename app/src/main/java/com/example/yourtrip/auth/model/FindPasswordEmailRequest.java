package com.example.yourtrip.auth.model;

public class FindPasswordEmailRequest {
    private String email;

    public FindPasswordEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
}
