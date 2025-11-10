package com.example.yourtrip.model;

//백에서 profileImage 빼고 개발했음 수정해주면 다시 반영하기
// api 분리될 예정임 이거 자체를 수정해야할듯

public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
    private String profileImage; // nullable

    public SignupRequest(String email, String password, String nickname, String profileImage) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
//        this.profileImage = profileImage;
    }
}
