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
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
import com.example.yourtrip.mytrip.model.MyCourseListItemResponse;
import com.example.yourtrip.mytrip.model.MyCourseListResponse;
import com.example.yourtrip.mytrip.model.PlaceAddRequest;
import com.example.yourtrip.mytrip.model.PlaceAddResponse;
import com.example.yourtrip.mytrip.model.DayPlacesResponse;
import com.example.yourtrip.mytrip.model.LocationItem;
import com.example.yourtrip.mytrip.model.PlaceMemoRequest;
import com.example.yourtrip.mytrip.model.PlaceTimeRequest;
import com.example.yourtrip.mytrip.model.ImageUploadResponse;

import java.util.List;
import com.google.gson.JsonObject;

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
import retrofit2.http.PATCH;
import retrofit2.http.Query;


public interface ApiService {
    //=============회원가입 & 로그인 api================//
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

    //=============나의 코스 api================//
    // 나의 코스 기본 생성 api
    @POST("/api/my-courses")
    Call<MyCourseCreateBasicResponse> createMyCourse(@Body MyCourseCreateRequest request);

    // 나의 코스 리스트 조회 api
    @GET("/api/my-courses")
    Call<MyCourseListResponse> getMyCourses();


    //나의 코스 단건 조회 api
    @GET("/api/my-courses/{courseId}")
    Call<MyCourseDetailResponse> getMyCourseDetail(@Path("courseId") long courseId);


    //특정 일차에 새로운 장소 추가 API
    @POST("/api/my-courses/{courseId}/days/{dayId}/places")
    Call<PlaceAddResponse> addPlaceToDay(
            @Path("courseId") long courseId,
            @Path("dayId") long dayId,
            @Body PlaceAddRequest request
    );

    //특정 일차에 모든 장소 목록 조회 API
    @GET("/api/my-courses/{courseId}/days/{dayId}/places")
    Call<DayPlacesResponse> getPlacesForDay(
            @Path("courseId") long courseId,
            @Path("dayId") long dayId
    );

    //특정 장소의 시간을 수정하는 api
    @PATCH("/api/my-courses/{courseId}/days/{dayId}/places/{placeId}/start-time")
    Call<JsonObject> updatePlaceTime(
            @Path("courseId") long courseId,
            @Path("dayId") long dayId,
            @Path("placeId") long placeId,
            @Body PlaceTimeRequest requestBody
    );

    //특정 장소에 사진 추가
    @Multipart
    @POST("/api/my-courses/{courseId}/days/{dayId}/places/{placeId}/images")
    Call<ImageUploadResponse> uploadPlaceImage(
            @Path("courseId") long courseId,
            @Path("dayId") long dayId,
            @Path("placeId") long placeId,
            @Part MultipartBody.Part placeImage
    );

    //특정 장소에 메모 추가
    @PATCH("/api/my-courses/{courseId}/days/{dayId}/places/{placeId}/memo")
    Call<JsonObject> updatePlaceMemo(
            @Path("courseId") long courseId,
            @Path("dayId") long dayId,
            @Path("placeId") long placeId,
            @Body PlaceMemoRequest requestBody
    );


    //=============홈 api================//
    // 홈 다중 필터링(태그 & 텍스트) 검색
    @GET("/api/upload-courses")
    Call<UploadCourseListResponse> getUploadCourses(
            @Query("keyword") String keyword,        // 검색어 (없으면 null)
            @Query("tag") List<String> tags,         // 태그 여러 개
            @Query("sort") String sort               // "POPULAR" 또는 "NEW"
    );

    //=============피드 api================//
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

