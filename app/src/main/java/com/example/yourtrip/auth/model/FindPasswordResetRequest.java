package com.example.yourtrip.auth.model;

public class FindPasswordResetRequest {
    private String email;
    private String newPassword;

    public FindPasswordResetRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public String getEmail() { return email; }
    public String getNewPassword() { return newPassword; }
}
