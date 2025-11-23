package com.example.yourtrip.network;

import com.example.yourtrip.model.EmailRequest;
import com.example.yourtrip.model.FeedListResponse;
import com.example.yourtrip.model.LoginRequest;
import com.example.yourtrip.model.MyCourseCreateRequest;
import com.example.yourtrip.model.PasswordRequest;
import com.example.yourtrip.model.ProfileRequest;
import com.example.yourtrip.model.VerificationRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    //  이메일 API
    @POST("/api/users/email/send")
    Call<ResponseBody> checkEmail(@Body EmailRequest emailRequest);

    //  이메일 인증번호 API
    @POST("/api/users/email/verify")
    Call<ResponseBody> verifyCode(@Body VerificationRequest request);

    //  비밀번호 유효성 검사 API
    @POST("/api/users/password")
    Call<ResponseBody> setPassword(@Body PasswordRequest request);

    //닉네임,프로필 및 최종 프로필 등록 API
    @POST("/api/users/profile")
    Call<ResponseBody> setProfile(@Body ProfileRequest request);

    // 로그인 API
    @POST("/api/users/login")
    Call<ResponseBody> login(@Body LoginRequest request);

    @POST("/api/my-courses")
    Call<ResponseBody> createMyCourse(@Body MyCourseCreateRequest request);

    // 피드 리스트 API
    @GET("/api/feeds")
    Call<FeedListResponse> getFeedList(
            @Query("page") int page,
            @Query("size") int size
    );


}

