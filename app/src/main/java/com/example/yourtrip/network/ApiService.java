package com.example.yourtrip.network;


import com.example.yourtrip.auth.model.EmailRequest;
import com.example.yourtrip.auth.model.LoginRequest;
import com.example.yourtrip.auth.model.PasswordRequest;
import com.example.yourtrip.auth.model.ProfileRequest;
import com.example.yourtrip.auth.model.VerificationRequest;
import com.example.yourtrip.model.FeedCommentListResponse;
import com.example.yourtrip.model.FeedCommentWriteRequest;
import com.example.yourtrip.model.FeedCommentWriteResponse;
import com.example.yourtrip.model.FeedDetailResponse;
import com.example.yourtrip.model.FeedListResponse;
import com.example.yourtrip.model.UploadCourseListResponse;
import com.example.yourtrip.mytrip.model.MyCourseCreateBasicResponse;
import com.example.yourtrip.mytrip.model.MyCourseCreateRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
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
    Call<MyCourseCreateBasicResponse> createMyCourse(@Body MyCourseCreateRequest request);

    // 홈 다중 필터링(태그 & 텍스트) 검색
    @GET("/api/upload-courses")
    Call<UploadCourseListResponse> getUploadCourses(
            @Query("keyword") String keyword,        // 검색어 (없으면 null)
            @Query("tag") List<String> tags,         // 태그 여러 개
            @Query("sort") String sort               // "POPULAR" 또는 "NEW"
    );

    // 피드 리스트 조회 API
    @GET("/api/feeds")
    Call<FeedListResponse> getFeedList(
            @Query("sortType") String sortType,   // NEW or POPULAR
            @Query("page") int page,
            @Query("size") int size
    );

    @Multipart
    @POST("api/feeds")
    Call<Void> uploadFeed(
            @Part List<MultipartBody.Part> mediaFiles,
            @Part("request") RequestBody request
    );


    // 피드 상세 조회 API
    @GET("/api/feeds/{feedId}")
    Call<FeedDetailResponse> getFeedDetail(
            @Path("feedId") int feedId
    );

    // 피드별 댓글 조회 API
    @GET("/api/feeds/{feedId}/comments")
    Call<FeedCommentListResponse> getFeedComments(
            @Path("feedId") int feedId,
            @Query("page") int page,
            @Query("size") int size
    );

    // 피드에 댓글 작성 API
    @POST("/api/feeds/{feedId}/comments")
    Call<FeedCommentWriteResponse> writeComment(
            @Path("feedId") int feedId,
            @Body FeedCommentWriteRequest request
    );

    // 키워드별 피드 조회 API
    @GET("/api/feeds/search")
    Call<FeedListResponse> searchFeeds(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

}

