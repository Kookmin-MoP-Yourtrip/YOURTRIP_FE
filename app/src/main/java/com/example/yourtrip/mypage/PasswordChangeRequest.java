package com.example.yourtrip.mypage;

public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;

    public PasswordChangeRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
