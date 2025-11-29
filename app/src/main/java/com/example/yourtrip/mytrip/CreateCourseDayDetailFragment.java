package com.example.yourtrip.mytrip;

// [삭제] 권한 관련 import 제거
// import android.Manifest;
// import android.content.pm.PackageManager;
// import androidx.core.content.ContextCompat;

// [삭제] 실제 파일 경로를 사용하지 않으므로, 아래 import들은 필요 없습니다.
// import android.database.Cursor;
// import android.provider.MediaStore;
// import java.io.File;

// [추가] InputStream과 파일 이름 처리를 위한 import
import com.example.yourtrip.commonUtil.FileUtils;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.DayPlacesResponse;
import com.example.yourtrip.mytrip.model.LocationItem;
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
import com.example.yourtrip.mytrip.model.PlaceAddResponse;
import com.example.yourtrip.mytrip.model.PlaceMemoRequest;
import com.example.yourtrip.mytrip.model.PlaceTimeRequest;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// LocationAdapter의 콜백을 받기 위해 OnLocationInteractionListener 인터페이스를 구현(implements)
public class CreateCourseDayDetailFragment extends Fragment implements LocationAdapter.OnLocationInteractionListener {

    private static final String TAG = "CourseDayDetailFragment";
    private static final String ARG_COURSE_ID = "courseId";
    private static final String ARG_DAY_SCHEDULES = "daySchedules";

    // 데이터
    private long courseId;
    private List<MyCourseDetailResponse.DaySchedule> daySchedules;
    private ApiService apiService;

    // UI 컴포넌트
    private RecyclerView recyclerViewTripDays;
    private DayAdapter dayAdapter;
    private RecyclerView recyclerLocationList;
    private LocationAdapter locationAdapter;

    // ActivityResultLaunchers
    private ActivityResultLauncher<Intent> addLocationLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    // [삭제] 권한 요청 Launcher는 이제 사용하지 않습니다.
    // private ActivityResultLauncher<String> requestPermissionLauncher;

    // 사진 추가 시, 어떤 아이템이 선택되었는지 위치를 저장하기 위한 변수
    private int selectedItemPosition = -1;

    //프래그먼트는 반드시 비어있는 기본 생성자를 가져야 함
    public CreateCourseDayDetailFragment() {
        // Required empty public constructor
    }

    /**
     * 프래그먼트 인스턴스를 생성하고 초기 데이터를 전달하는 정적 팩토리 메서드(Factory Method)입니다.
     * 이 방법을 사용하면 데이터 전달 과정을 캡슐화하고, Activity와의 결합도를 낮춰 오류를 줄일 수 있습니다.
     * @param courseId 코스 ID
     * @param daySchedules 일차 정보 리스트
     * @return 데이터가 포함된 새로운 CreateCourseDayDetailFragment 인스턴스
     */
    public static CreateCourseDayDetailFragment newInstance(long courseId, List<MyCourseDetailResponse.DaySchedule> daySchedules) {
        CreateCourseDayDetailFragment fragment = new CreateCourseDayDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_COURSE_ID, courseId);
        args.putSerializable(ARG_DAY_SCHEDULES, (Serializable) daySchedules);
        fragment.setArguments(args);
        return fragment;
    }


    // --- 1. 생명주기 메서드 ---

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 이전 액티비티/프래그먼트로부터 전달받은 데이터 처리
        if (getArguments() != null) {
            courseId = getArguments().getLong(ARG_COURSE_ID, -1L);
            try {
                daySchedules = (List<MyCourseDetailResponse.DaySchedule>) getArguments().getSerializable(ARG_DAY_SCHEDULES);
            } catch (ClassCastException e) {
                Log.e(TAG, "DaySchedules 리스트를 변환하는 데 실패했습니다.", e);
                daySchedules = null;
            }
        }

        // 네트워크 서비스 초기화
        apiService = RetrofitClient.getAuthService(requireContext());

        // [수정] onCreate가 복잡해지지 않도록 Launcher 초기화 로직을 별도 메서드로 분리
        initializeLaunchers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_trip_create_detail.xml 레이아웃을 인플레이트
        return inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI 컴포넌트 findViewById
        recyclerViewTripDays = view.findViewById(R.id.recyclerViewTripDays);
        recyclerLocationList = view.findViewById(R.id.recyclerLocationList);

        // RecyclerView 설정
        setupDayRecyclerView();
        setupLocationRecyclerView();
    }


    // --- 2. 초기 설정 및 UI 관련 메서드 ---

    /**
     * 모든 ActivityResultLauncher들을 초기화하는 메서드.
     */
    private void initializeLaunchers() {
        // 2-1. 장소 추가 결과 처리 Launcher
        addLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        PlaceAddResponse newPlaceResponse = (PlaceAddResponse) result.getData().getSerializableExtra("newPlace");
                        if (newPlaceResponse != null && locationAdapter != null) {
                            long currentDayId = locationAdapter.getCurrentDayId();
                            if (currentDayId != -1L) {
                                // 장소 추가 성공 시, 현재 일차 목록을 새로고침
                                fetchPlacesForDay(currentDayId);
                            }
                        }
                    }
                });

        // [삭제] 권한 요청 Launcher 초기화 코드는 이제 필요 없습니다.

        // [수정] 갤러리 선택 결과 처리 Launcher (안전한 방식으로 변경)
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null && selectedItemPosition != -1 && locationAdapter != null) {
                            Object item = locationAdapter.getItemAt(selectedItemPosition);
                            if (item instanceof LocationItem) {
                                long placeId = ((LocationItem) item).getPlaceId();
                                // [중요] 실제 파일 경로 대신, 선택된 이미지의 Uri를 업로드 메서드로 바로 전달합니다. (성능 및 호환성 최적화)
                                uploadImageToServer(placeId, imageUri);
                            }
                        }
                    }
                }
        );
    }

    /**
     * 상단 일차 탭 RecyclerView를 설정하는 메서드.
     */
    private void setupDayRecyclerView() {
        if (daySchedules == null || daySchedules.isEmpty()) return;
        dayAdapter = new DayAdapter(daySchedules, (position, dayId) -> {
            // 탭 클릭 시 LocationAdapter의 dayId를 갱신하고, 해당 일차의 장소 목록을 불러옴
            if (locationAdapter != null) {
                locationAdapter.updateDayId(dayId);
            }
            fetchPlacesForDay(dayId);
        });
        recyclerViewTripDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTripDays.setAdapter(dayAdapter);
    }

    /**
     * 하단 장소 목록 RecyclerView를 설정하는 메서드.
     */
    private void setupLocationRecyclerView() {
        List<Object> initialList = new ArrayList<>();
        initialList.add("ADD_BUTTON"); // '장소 추가' 버튼 아이템 추가
        long initialDayId = (daySchedules != null && !daySchedules.isEmpty()) ? daySchedules.get(0).getDayId() : -1L;
        locationAdapter = new LocationAdapter(initialList, courseId, initialDayId, this);
        recyclerLocationList.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLocationList.setAdapter(locationAdapter);
        // 화면이 처음 보일 때, 첫 번째 일차의 장소 목록을 가져옴
        if (initialDayId != -1L) {
            fetchPlacesForDay(initialDayId);
        }
    }


    // ========================================================================
    // A. Adapter-Fragment Interaction (어댑터로부터의 요청 처리)
    // ========================================================================

    /**
     * 시간 수정 요청을 받았을 때 호출되는 콜백 메서드 (from Adapter)
     */
    @Override
    public void onTimeUpdateRequested(long placeId, String time, int position) {
        updatePlaceTime(placeId, time, position); // 실제 API 호출 로직으로 전달
    }

    /**
     * 사진 추가 요청을 받았을 때 호출되는 콜백 메서드 (from Adapter)
     */
    @Override
    public void onPhotoAddRequested(long placeId, int position) {
        this.selectedItemPosition = position; // 어떤 아이템에서 요청이 왔는지 위치 저장

        // [임시] 갤러리 대신 임의의 이미지로 서버 통신을 테스트하는 코드
        // ========================================================================
        Log.d(TAG, "--- [테스트 모드] 임의의 이미지로 업로드 테스트 시작 ---");

        // 1. 앱의 drawable 폴더에 있는 임의의 이미지 리소스 ID를 가져옵니다.
        //    (예: R.drawable.ic_launcher_background)
        int resourceId = R.drawable.ic_feed_camera; // 테스트용 이미지, 원하는 것으로 변경 가능

        // 2. 리소스 ID를 사용하여 안드로이드 리소스를 가리키는 Uri를 생성합니다.
        Uri sampleImageUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getResources().getResourcePackageName(resourceId))
                .appendPath(getResources().getResourceTypeName(resourceId))
                .appendPath(getResources().getResourceEntryName(resourceId))
                .build();

        Log.d(TAG, "[테스트 모드] 생성된 샘플 URI: " + sampleImageUri.toString());

        // 3. 생성된 임의의 Uri로 uploadImageToServer 메서드를 직접 호출합니다.
        uploadImageToServer(placeId, sampleImageUri);
        // ========================================================================
        
        // 갤러리 여는 메서드
//        openGallery();
    }

    /**
     * 메모 수정 요청을 받았을 때 호출되는 콜백 메서드 (from Adapter)
     */
    @Override
    public void onMemoUpdateRequested(long placeId, String memo, int position) {
        updatePlaceMemo(placeId, memo, position); // 실제 API 호출 로직으로 전달
    }


    // ========================================================================
    // B. API Calls (서버 통신)
    // ========================================================================

    /**
     * 특정 일차의 장소 목록을 서버에서 불러오는 메서드.
     */
    private void fetchPlacesForDay(long dayId) {
        if (courseId == -1L) return;
        apiService.getPlacesForDay(courseId, dayId).enqueue(new Callback<DayPlacesResponse>() {
            @Override
            public void onResponse(Call<DayPlacesResponse> call, Response<DayPlacesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (locationAdapter != null) {
                        locationAdapter.updateItems(response.body().getPlaces());
                    }
                } else {
                    if (locationAdapter != null) locationAdapter.updateItems(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<DayPlacesResponse> call, Throwable t) {
                if (locationAdapter != null) locationAdapter.updateItems(new ArrayList<>());
            }
        });
    }

    /**
     * 특정 장소의 시간을 서버에 업데이트하는 메서드.
     */
    private void updatePlaceTime(long placeId, String time, int position) {
        long currentDayId = locationAdapter.getCurrentDayId();
        PlaceTimeRequest requestBody = new PlaceTimeRequest(time);
        apiService.updatePlaceTime(courseId, currentDayId, placeId, requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (getContext() != null) Toast.makeText(getContext(), "시간이 " + time + "으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    if (locationAdapter != null) locationAdapter.updateTime(position, time);
                } else {
                    if (getContext() != null) Toast.makeText(getContext(), "시간 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (getContext() != null) Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * [최적화] 특정 장소에 사진을 서버로 업로드하는 메서드. (File 대신 Uri를 받아 안전하게 처리)
     */
    private void uploadImageToServer(long placeId, Uri imageUri) {
        try {
            Log.d(TAG, "--- 이미지 업로드 시작 ---");
            Log.d(TAG, "선택된 이미지 URI: " + imageUri.toString());

            // 1. 파일 이름 가져오기 (FileUtils 유틸리티 사용)
            String fileName = FileUtils.getFileName(requireContext(), imageUri);

            // 2. ContentResolver를 사용해 파일의 내용(InputStream)을 열기
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);

            // 3. InputStream을 byte 배열로 변환 (FileUtils 유틸리티 사용)
            byte[] fileBytes = FileUtils.readBytes(inputStream);

            // 4. MIME 타입을 확인하고, byte 배열로 RequestBody 생성
            String mimeType = requireContext().getContentResolver().getType(imageUri);
            if (mimeType == null) {
                // 테스트용 리소스 Uri는 MIME 타입을 반환하지 않으므로, 기본값(예: "image/jpeg")을 지정해줍니다.
                // 실제 갤러리에서 가져온 Uri도 간혹 null을 반환할 수 있으므로 이 코드는 안전장치 역할을 합니다.
                mimeType = "image/jpeg";
                Log.d(TAG, "MIME 타입을 찾을 수 없어 기본값으로 설정: " + mimeType);
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("placeImage", fileName, requestFile);

            long currentDayId = locationAdapter.getCurrentDayId();
            Log.d(TAG, "API 호출 파라미터: courseId=" + courseId + ", dayId=" + currentDayId + ", placeId=" + placeId);
            Log.d(TAG, "--- API 호출 직전 ---");

            apiService.uploadPlaceImage(courseId, currentDayId, placeId, body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "사진 업로드 성공: " + response.body().toString());
                        if (getContext() != null) Toast.makeText(getContext(), "사진이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        // [성능 최적화] 사진 추가 성공 시, 전체 목록을 다시 불러오는 대신,
                        // API 응답에 새로운 이미지 URL이 있다면 해당 아이템만 갱신하는 것이 훨씬 효율적입니다.
                        // (현재는 전체 목록을 다시 불러오는 방식으로 유지)
                        fetchPlacesForDay(currentDayId);
                    } else {
                        Log.e(TAG, "사진 업로드 실패: " + response.code());
                        if (getContext() != null) Toast.makeText(getContext(), "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "사진 업로드 네트워크 오류", t);
                    if (getContext() != null) Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "이미지 처리 중 오류 발생", e);
            Toast.makeText(getContext(), "이미지를 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 특정 장소의 메모를 서버에 업데이트하는 메서드.
     */
    private void updatePlaceMemo(long placeId, String memo, int position) {
        long currentDayId = locationAdapter.getCurrentDayId();
        PlaceMemoRequest requestBody = new PlaceMemoRequest(memo);
        apiService.updatePlaceMemo(courseId, currentDayId, placeId, requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (getContext() != null) Toast.makeText(getContext(), "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // API 성공 시, 로컬 데이터 모델의 메모도 업데이트하여 UI 일관성 유지
                    if (locationAdapter != null) {
                        Object item = locationAdapter.getItemAt(position);
                        if (item instanceof LocationItem) {
                            ((LocationItem) item).setMemo(memo);
                        }
                    }
                } else {
                    if (getContext() != null) Toast.makeText(getContext(), "메모 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (getContext() != null) Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========================================================================
    // C. Helpers & Utilities (보조 및 유틸리티)
    // ========================================================================

    /**
     * 장소 추가 화면(AddLocationActivity)을 실행하는 메서드.
     */
    public void launchAddLocationActivity(long courseId, long dayId) {
        Intent intent = new Intent(requireActivity(), AddLocationActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("dayId", dayId);
        addLocationLauncher.launch(intent);
    }

    /**
     * 갤러리를 여는 인텐트를 실행하는 메서드.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    // [삭제] Uri로부터 실제 파일 경로를 얻는 헬퍼 메서드는 이제 필요 없습니다.
}


//package com.example.yourtrip.mytrip;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.MediaStore;
//import java.io.File;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.ArrayList;
//import com.example.yourtrip.mytrip.model.LocationItem;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
//import com.example.yourtrip.mytrip.model.PlaceAddResponse;
//import com.example.yourtrip.mytrip.model.DayPlacesResponse;
//import com.example.yourtrip.mytrip.model.PlaceMemoRequest;
//import com.example.yourtrip.mytrip.model.PlaceTimeRequest;
//import com.example.yourtrip.network.ApiService;
//import com.example.yourtrip.network.RetrofitClient;
//import com.google.gson.JsonObject;
//
//import java.io.Serializable;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//
//// LocationAdapter.OnLocationInteractionListener 인터페이스를 구현(implements)한다고 선언
//public class CreateCourseDayDetailFragment extends Fragment implements LocationAdapter.OnLocationInteractionListener {
//
//    private static final String TAG = "CourseDayDetailFragment";
//    private static final String ARG_COURSE_ID = "courseId";
//    private static final String ARG_DAY_SCHEDULES = "daySchedules";
//
//
//    // 데이터
//    private long courseId;
//    private List<MyCourseDetailResponse.DaySchedule> daySchedules;
//    private ApiService apiService;
//
//    // ActivityResultLauncher
//    private ActivityResultLauncher<Intent> addLocationLauncher;
//    private ActivityResultLauncher<Intent> pickImageLauncher;
//    private ActivityResultLauncher<String> requestPermissionLauncher;
//
//    private int selectedItemPosition = -1;
//
//    // UI 컴포넌트
//    private RecyclerView recyclerViewTripDays;
//    private DayAdapter dayAdapter;
//    private RecyclerView recyclerLocationList;
//    private LocationAdapter locationAdapter;
//
//    //프래그먼트는 반드시 비어있는 기본 생성자를 가져야 함
//    public CreateCourseDayDetailFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * 프래그먼트 인스턴스를 생성하고 초기 데이터를 전달하는 정적 팩토리 메서드(Factory Method)입니다.
//     * 이 방법을 사용하면 데이터 전달 과정을 캡슐화하고, Activity와의 결합도를 낮춰 오류를 줄일 수 있습니다.
//     * @param courseId 코스 ID
//     * @param daySchedules 일차 정보 리스트
//     * @return 데이터가 포함된 새로운 CreateCourseDayDetailFragment 인스턴스
//     */
//    public static CreateCourseDayDetailFragment newInstance(long courseId, List<MyCourseDetailResponse.DaySchedule> daySchedules) {
//        CreateCourseDayDetailFragment fragment = new CreateCourseDayDetailFragment();
//        Bundle args = new Bundle();
//        args.putLong(ARG_COURSE_ID, courseId);
//        args.putSerializable(ARG_DAY_SCHEDULES, (Serializable) daySchedules);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            courseId = getArguments().getLong(ARG_COURSE_ID, -1L);
//            // 형변환 시 ClassCastException을 방지하기 위해 try-catch 사용 가능
//            try {
//                daySchedules = (List<MyCourseDetailResponse.DaySchedule>) getArguments().getSerializable(ARG_DAY_SCHEDULES);
//                // 🟡 디버깅 로그 1: Fragment가 처음 데이터를 받았을 때
//                if (daySchedules != null) {
//                    Log.d("DEBUG_DAY_ID", "[Fragment onCreate] 프래그먼트가 처음 받은 daySchedules:");
//                    for (MyCourseDetailResponse.DaySchedule schedule : daySchedules) {
//                        Log.d("DEBUG_DAY_ID", "  >> Day: " + schedule.getDay() + ", Day ID: " + schedule.getDayId());
//                    }
//                }
//            } catch (ClassCastException e) {
//                Log.e(TAG, "DaySchedules 리스트를 변환하는 데 실패했습니다.", e);
//                daySchedules = null;
//            }
//        }
//
//        // 데이터 유효성 검사 및 로그 출력
//        if (courseId != -1L && daySchedules != null && !daySchedules.isEmpty()) {
//            Log.d(TAG, "프래그먼트 데이터 로드 성공. Course ID: " + courseId + ", 총 일차: " + daySchedules.size());
//        } else {
//            Log.e(TAG, "프래그먼트 데이터가 유효하지 않습니다.");
//            if (getActivity() != null) {
//                Toast.makeText(getActivity(), "일정 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        // ApiService 초기화
//        apiService = RetrofitClient.getAuthService(requireContext());
//
//        // Launcher 초기화: 결과를 어떻게 처리할지 미리 정의
//        addLocationLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    // AddLocationActivity에서 돌아왔을 때 이 코드가 실행됩니다.
//
//                    // 결과가 성공(OK)이고, 데이터가 비어있지 않은지 확인
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        // "newPlace" 라는 키로 전달된 응답 객체(PlaceAddResponse)를 꺼냅니다.
//                        PlaceAddResponse newPlaceResponse = (PlaceAddResponse) result.getData().getSerializableExtra("newPlace");
//
//                        if (newPlaceResponse != null) {
//                            Log.d(TAG, "새로운 장소 받음: " + newPlaceResponse.getPlaceName());
//
//                            if (locationAdapter != null) {
//                                // 1. 장소를 추가했던 바로 그 dayId를 어댑터로부터 가져옵니다.
//                                long currentDayId = locationAdapter.getCurrentDayId();
//                                if (currentDayId != -1L) {
//                                    Log.d(TAG, "장소 추가 성공 후, dayId " + currentDayId + "의 목록을 새로고침합니다.");
//                                    // 2. 해당 dayId의 최신 목록을 서버에서 다시 불러옵니다.
//                                    fetchPlacesForDay(currentDayId);
//                                }
//                            }
//                        }
//                    }
//                });
//
//        // 권한 요청 결과 처리를 위한 Launcher 초기화
//        requestPermissionLauncher = registerForActivityResult(
//                new ActivityResultContracts.RequestPermission(),
//                isGranted -> {
//                    if (isGranted) {
//                        // 사용자가 권한을 허용했을 경우, 바로 갤러리를 엽니다.
//                        Toast.makeText(getContext(), "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
//                        openGallery();
//                    } else {
//                        // 사용자가 권한을 거부했을 경우
//                        Toast.makeText(getContext(), "사진을 추가하려면 저장소 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//
//
//        // 갤러리 결과 처리를 위한 Launcher 초기화
//        pickImageLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri imageUri = result.getData().getData();
//                        if (imageUri != null && selectedItemPosition != -1) {
//                            if (locationAdapter != null) {
//                                Object item = locationAdapter.getItemAt(selectedItemPosition);
//                                if (item instanceof LocationItem) {
//                                    long placeId = ((LocationItem) item).getPlaceId();
//                                    File imageFile = new File(getRealPathFromURI(imageUri));
//                                    uploadImageToServer(placeId, imageFile);
//                                }
//                            }
//                        }
//                    }
//                }
//        );
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // fragment_trip_create_detail.xml 레이아웃을 인플레이트
//        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
//        return view;
//
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        //--- 상단 일차 탭 RecyclerView 설정 ---
//        // RecyclerView 초기화
//        recyclerViewTripDays = view.findViewById(R.id.recyclerViewTripDays);
//
//        // 데이터 유효성 검사 후 어댑터 설정
//        if (daySchedules != null && !daySchedules.isEmpty()) {
//            setupDayRecyclerView();
//        } else {
//            Log.w(TAG, "daySchedules가 비어있어 RecyclerView를 설정할 수 없습니다.");
//        }
//
//        //--- 하단 장소 목록 RecyclerView 설정 ---
//        recyclerLocationList = view.findViewById(R.id.recyclerLocationList);
//        setupLocationRecyclerView(); // 새로운 메서드 호출
//    }
//
//    //상단 일차 탭 RecyclerView 설정 메서드
//    private void setupDayRecyclerView() {
//        // 메서드 시작 시 한 번 더 확인하여 안정성 강화
//        if (daySchedules == null || daySchedules.isEmpty()) {
//            Log.w(TAG, "daySchedules가 없어 DayAdapter를 설정할 수 없습니다.");
//            return;
//        }
//
//        // DayAdapter를 생성할 때, 클릭 리스너를 함께 전달
//        dayAdapter = new DayAdapter(daySchedules, (position, dayId) -> {
//            // 이 람다(lambda) 표현식은 OnDayTabClickListener의 onDayTabClick 메서드를 구현한 것
//            // 탭이 클릭될 때마다 이 안의 코드가 실행
//            // 🟡 디버깅 로그 2: 상단 탭이 클릭되었을 때
//            Log.d("DEBUG_DAY_ID", "[DayAdapter Click] " + (position + 1) + "일차 탭 클릭됨. 전달된 dayId: " + dayId);
//
//            // 1. LocationAdapter에 새로운 dayId를 알려줌
//            if (locationAdapter != null) {
//                locationAdapter.updateDayId(dayId);
//            }
//
//            // 탭 클릭 시 API 호출 활성화
//            fetchPlacesForDay(dayId);
//        });
//        //레이아웃 매니저 설정 (가로 스크롤) , 어뎁터 연결
//        recyclerViewTripDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerViewTripDays.setAdapter(dayAdapter);
//
//    }
//
//    private void setupLocationRecyclerView() {
//        // --- 1. 화면에 보여줄 초기 데이터 생성 ---
//        List<Object> initialList = new ArrayList<>();
//        // 초기 상태에서도 '추가 버튼'이 보이도록 데이터를 추가
//        initialList.add("ADD_BUTTON");
//
//        // --- 2. 어댑터 생성 및 RecyclerView에 연결 ---
//        long initialDayId = (daySchedules != null && !daySchedules.isEmpty()) ? daySchedules.get(0).getDayId() : -1L;
//        locationAdapter = new LocationAdapter(initialList, courseId, initialDayId, this);
//
//        recyclerLocationList.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerLocationList.setAdapter(locationAdapter);
//
//        // -화면이 처음 보일 때, 첫 번째 일차의 장소 목록을 가져옴
//        if (initialDayId != -1L) {
//            fetchPlacesForDay(initialDayId);
//        }
//    }
//
//
//    // 일차별 장소 목록 호출 메서드
//    private void fetchPlacesForDay(long dayId) {
//        // courseId 유효성 검사
//        if (courseId == -1L) {
//            Log.e(TAG, "courseId가 유효하지 않아 API를 호출할 수 없습니다.");
//            return;
//        }
//
//        Log.d(TAG, "장소 목록 조회 API 호출 시작. dayId: " + dayId);
//        apiService.getPlacesForDay(courseId, dayId).enqueue(new Callback<DayPlacesResponse>() {
//            @Override
//            public void onResponse(Call<DayPlacesResponse> call, Response<DayPlacesResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // API 응답에서 장소 리스트(places)를 추출
//                    List<LocationItem> places = response.body().getPlaces();
//                    Log.d(TAG, "장소 " + places.size() + "개 조회 성공.");
//
//                    // 어댑터에 데이터를 업데이트하는 새로운 메서드를 호출
//                    if (locationAdapter != null) {
//                        locationAdapter.updateItems(places);
//                    }
//
//                } else {
//                    Log.e(TAG, "장소 목록 조회 API 응답 실패: " + response.code());
//                    // 오류 발생 시, 리스트를 비우고 '추가 버튼'만 보여주도록 처리
//                    if (locationAdapter != null) {
//                        locationAdapter.updateItems(new ArrayList<>());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DayPlacesResponse> call, Throwable t) {
//                Log.e(TAG, "장소 목록 조회 네트워크 오류", t);
//                if (locationAdapter != null) {
//                    locationAdapter.updateItems(new ArrayList<>());
//                }
//            }
//        });
//    }
//
//    // Adapter로부터 Activity 실행 요청 받아서 화면 전환 처리
//    public void launchAddLocationActivity(long courseId, long dayId) {
//        // 🟡 디버깅 로그 3: AddLocationActivity를 실행하기 직전
//        Log.d("DEBUG_DAY_ID", "[Fragment launch] AddLocationActivity 실행 요청. 전달할 dayId: " + dayId);
//
//        Intent intent = new Intent(requireActivity(), AddLocationActivity.class);
//        intent.putExtra("courseId", courseId);
//        intent.putExtra("dayId", dayId);
//
//        // ActivityResultLauncher를 사용하여 결과를 받음
//        // 🟡 5. 'startActivity' 대신 'launch'를 사용하여 Activity를 실행하고 결과를 기다립니다.
//        addLocationLauncher.launch(intent);
//    }
//
//    // ========================================================================
//    // 인터페이스 구현 메서드 (API 호출 로직)
//    // ========================================================================
//    @Override
//    public void onTimeUpdateRequested(long placeId, String time, int position) {
//        // 현재 프래그먼트가 보여주고 있는 일차(day)의 ID를 가져옵니다.
//        long currentDayId = locationAdapter.getCurrentDayId();
//
//        Log.d(TAG, "시간 수정 API 호출 시작: courseId=" + courseId + ", dayId=" + currentDayId + ", placeId=" + placeId + ", time=" + time);
//
//        // 1. 서버에 보낼 요청 본문(Body) 객체를 생성합니다.
//        PlaceTimeRequest requestBody = new PlaceTimeRequest(time);
//
//        // 2. Fragment가 직접 ApiService를 사용하여 네트워크 통신을 실행합니다.
//        apiService.updatePlaceTime(courseId, currentDayId, placeId, requestBody).enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful()) {
//                    // 3. API 호출이 성공했을 때
//                    Log.d(TAG, "시간 수정 성공: " + response.body());
//                    if(getContext() != null) {
//                        Toast.makeText(getContext(), "시간이 " + time + "으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
//                    }
//
//                    // 4. Adapter에게 특정 아이템의 화면을 갱신하라고 최종 지시를 내립니다.
//                    if (locationAdapter != null) {
//                        locationAdapter.updateTime(position, time);
//                    }
//                } else {
//                    // API 호출이 실패했을 때
//                    Log.e(TAG, "시간 수정 실패: " + response.code());
//                    if(getContext() != null) {
//                        Toast.makeText(getContext(), "시간 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                // 네트워크 자체에 오류가 발생했을 때
//                Log.e(TAG, "시간 수정 네트워크 오류", t);
//                if(getContext() != null) {
//                    Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    // [수정] Adapter가 호출할 인터페이스 메서드 구현
//    @Override
//    public void onPhotoAddRequested(long placeId, int position) {
//        this.selectedItemPosition = position; // 사용자가 어떤 아이템을 클릭했는지 위치 저장
//
//        // --- [수정] 권한 확인 및 요청 로직 추가 ---
//        // 1. 저장소 읽기 권한이 이미 허용되어 있는지 확인
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            // 1-1. 권한이 있다면, 바로 갤러리를 엽니다.
//            openGallery();
//        } else {
//            // 1-2. 권한이 없다면, 사용자에게 권한을 요청합니다.
//            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//    }
//
//    // [추가] 갤러리를 여는 로직을 별도 메서드로 분리
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        pickImageLauncher.launch(intent);
//    }
//
//
//    // [추가] 사진 업로드 API 호출 메서드
//    private void uploadImageToServer(long placeId, File file) { // [수정] position 파라미터 제거
//        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("placeImage", file.getName(), requestFile);
//
//        long currentDayId = locationAdapter.getCurrentDayId();
//        apiService.uploadPlaceImage(courseId, currentDayId, placeId, body).enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    if (getContext() != null) Toast.makeText(getContext(), "사진이 추가되었습니다.", Toast.LENGTH_SHORT).show();
//                    // [개선] 특정 일차 목록만 다시 불러와서 화면 전체를 갱신
//                    fetchPlacesForDay(currentDayId);
//                } else {
//                    if (getContext() != null) Toast.makeText(getContext(), "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                if (getContext() != null) Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // [추가] 메모 수정 API 호출 메서드
//    @Override
//    public void onMemoUpdateRequested(long placeId, String memo, int position) {
//        long currentDayId = locationAdapter.getCurrentDayId();
//        PlaceMemoRequest requestBody = new PlaceMemoRequest(memo);
//        apiService.updatePlaceMemo(courseId, currentDayId, placeId, requestBody).enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful()) {
//                    if (getContext() != null) Toast.makeText(getContext(), "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
//                    // 메모리 상의 데이터도 업데이트
//                    if (locationAdapter != null) {
//                        Object item = locationAdapter.getItemAt(position);
//                        if (item instanceof LocationItem) {
//                            ((LocationItem) item).setMemo(memo);
//                        }
//                    }
//                } else {
//                    if (getContext() != null) Toast.makeText(getContext(), "메모 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                if (getContext() != null) Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // [추가] Uri로부터 실제 파일 경로를 얻는 헬퍼 메서드
//    private String getRealPathFromURI(Uri contentUri) {
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = requireActivity().getContentResolver().query(contentUri, proj, null, null, null);
//        if (cursor == null) return null;
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String path = cursor.getString(column_index);
//        cursor.close();
//        return path;
//    }
//}
//
