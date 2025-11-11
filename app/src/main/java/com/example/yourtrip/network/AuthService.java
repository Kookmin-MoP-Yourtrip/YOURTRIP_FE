package com.example.yourtrip.network;

import com.example.yourtrip.model.LoginRequest;
import com.example.yourtrip.model.LoginResponse;
import com.example.yourtrip.model.SignupRequest;
import com.example.yourtrip.model.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    // 🟢 회원가입 API
    @POST("/api/users/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);

    // 🟢 로그인 API
    @POST("/api/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
