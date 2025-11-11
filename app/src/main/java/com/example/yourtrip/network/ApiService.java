package com.example.yourtrip.network;

import com.example.yourtrip.model.EmailRequest;
import com.example.yourtrip.model.LoginRequest;
import com.example.yourtrip.model.LoginResponse;
import com.example.yourtrip.model.SignupRequest;
import com.example.yourtrip.model.SignupResponse;
import com.example.yourtrip.model.VerificationRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    //  회원가입 API
    @POST("/api/users/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);

    //  로그인 API
    @POST("/api/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    //  이메일 API
    @POST("/api/users/email/send")
    Call<ResponseBody> checkEmail(@Body EmailRequest emailRequest);

    //  이메일 인증번호 API
    @POST("/api/users/email/verify")
    Call<ResponseBody> verifyCode(@Body VerificationRequest request);
}

