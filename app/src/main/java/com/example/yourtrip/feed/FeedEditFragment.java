package com.example.yourtrip.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.FileUtils;
import com.example.yourtrip.model.FeedDetailResponse;
import com.example.yourtrip.model.FeedMediaDetailResponse;
import com.example.yourtrip.model.FeedUpdateRequest;
import com.example.yourtrip.model.FeedUpdateResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedEditFragment extends Fragment {

    private EditText editText;
    private RecyclerView rvPhotos;
    private Button btnSave;
    private TextView btnCancel;
    // ⬇⬇⬇ 이 두 줄 추가
    private View btnAddLocation;
    private ViewGroup locationGroup;

    private int feedId;

    // ⭐ 기존 이미지 목록 (id + url 둘 다 보관)
    private final List<FeedMediaDetailResponse> originalImages = new ArrayList<>();

    // ⭐ 유지할 기존 이미지 id 목록
    private final List<Long> keepMediaIds = new ArrayList<>();


    // 새로 추가한 이미지 URI
    private final List<Uri> newImages = new ArrayList<>();

    private UploadFeedAdapter adapter;

    private static final int MAX_IMAGES = 5;

    // 갤러리 오픈 런처
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {

                            Uri uri = result.getData().getData();

                            if (uri != null &&
                                    (originalImages.size() + newImages.size()) < MAX_IMAGES) {
                                newImages.add(uri);
                                adapter.setEditMode(originalImages, newImages);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_edit, container, false);

        feedId = getArguments().getInt("feedId", -1);

        editText = view.findViewById(R.id.editDynamic);
        rvPhotos = view.findViewById(R.id.rv_upload_photos);
        btnSave = view.findViewById(R.id.btn_feed_edit);   // XML과 맞춤
        btnCancel = view.findViewById(R.id.tv_feed_cancel);

        // ⬇⬇⬇ 여기 추가
        locationGroup = view.findViewById(R.id.location_group);
        btnAddLocation = view.findViewById(R.id.btn_add_location);

        btnAddLocation.setOnClickListener(v -> openAddLocationFragment());

        // FeedAddLocationFragment 에서 되돌아올 때 장소 결과 받기
        getParentFragmentManager().setFragmentResultListener(
                "location_request",
                this,
                (requestKey, bundle) -> {
                    String location = bundle.getString("selected_location");
                    if (location != null) {
                        addLocationChip(location);
                    }
                }
        );

        setupRecyclerView();

        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());
        btnSave.setOnClickListener(v -> updateFeed());

        loadOriginalDetail();

        return view;
    }
    private void setupRecyclerView() {
        rvPhotos.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        adapter = new UploadFeedAdapter(
                newImages,   // ← 새 이미지 목록 (수정 화면에서도 반드시 필요)
                new UploadFeedAdapter.OnUploadClickListener() {

                    @Override
                    public void onAddPhotoClick() {
                        openGallery();
                    }

                    @Override
                    public void onDeletePhotoClick(int position) {

                        if (position < originalImages.size()) {
                            // ⭐ 기존 이미지 삭제 → keep 목록에서 제거
                            FeedMediaDetailResponse removed = originalImages.remove(position);
                            keepMediaIds.remove(removed.getMediaId());


                        } else {
                            // 새 이미지 삭제
                            newImages.remove(position - originalImages.size());
                        }

                        adapter.setEditMode(originalImages, newImages);
                        adapter.notifyDataSetChanged();
                    }
                }
        );


        rvPhotos.setAdapter(adapter);
    }

    private void addLocationChip(String location) {
        if (locationGroup == null) return;

        // 기존 장소 태그는 하나만 유지하도록 모두 삭제
        locationGroup.removeAllViews();

        // item_location_tag.xml 재사용 (FeedUploadFragment와 동일)
        View tagView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_location_tag, locationGroup, false);

        TextView tvLocation = tagView.findViewById(R.id.tv_location);
        tvLocation.setText(location);

        locationGroup.addView(tagView);
    }

    // 🔻 기존 피드 내용 + 기존 이미지 로드
    private void loadOriginalDetail() {
        ApiService api = RetrofitClient.getAuthService(getContext());

        api.getFeedDetail(feedId).enqueue(new Callback<FeedDetailResponse>() {
            @Override
            public void onResponse(Call<FeedDetailResponse> call, Response<FeedDetailResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                FeedDetailResponse data = response.body();

                editText.setText(data.getContent());

                // ⬇⬇⬇ 추가: 기존 장소 태그 세팅
                if (data.getLocation() != null && !data.getLocation().isEmpty()) {
                    addLocationChip(data.getLocation());
                }

                // ⭐ 기존 이미지 id + url 저장
                originalImages.clear();
                keepMediaIds.clear();

                if (data.getMediaList() != null) {
                    for (FeedMediaDetailResponse m : data.getMediaList()) {
                        originalImages.add(m);
                        keepMediaIds.add(m.getMediaId());   // 유지 대상에 기본 등록
                    }
                }

                adapter.setEditMode(originalImages, newImages);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<FeedDetailResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    // 🔻 수정 요청 보내기
    private void updateFeed() {

        ApiService api = RetrofitClient.getAuthService(getContext());

        List<MultipartBody.Part> fileParts = new ArrayList<>();

        // 새 이미지 있을 때만 전송
        for (Uri uri : newImages) {
            try {
                String fileName = FileUtils.getFileName(requireContext(), uri);
                byte[] bytes = compressImage(requireContext(), uri);

                RequestBody body = RequestBody.create(
                        MediaType.parse("image/jpeg"), bytes);

                MultipartBody.Part part = MultipartBody.Part.createFormData(
                        "mediaFiles",
                        fileName,
                        body
                );

                fileParts.add(part);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "이미지 처리 오류", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // ⭐ JSON 데이터 생성 전, 선택된 location 읽어오기
        String selectedLocation = null;
        if (locationGroup != null && locationGroup.getChildCount() > 0) {
            View tagView = locationGroup.getChildAt(0);
            TextView tvLocation = tagView.findViewById(R.id.tv_location);
            if (tvLocation != null) {
                selectedLocation = tvLocation.getText().toString();
            }
        }

        // ⭐ JSON 데이터 생성
        FeedUpdateRequest updateRequest = new FeedUpdateRequest(
                null,                // title
                selectedLocation,    // ✅ location
                editText.getText().toString().trim(), // content
                new ArrayList<>(),   // hashtags
                null,                // uploadCourseId
                keepMediaIds         // 유지할 기존 이미지 id
        );

        RequestBody jsonBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                new Gson().toJson(updateRequest)
        );

        api.updateFeed(feedId, fileParts, jsonBody).enqueue(new Callback<FeedUpdateResponse>() {
            @Override
            public void onResponse(Call<FeedUpdateResponse> call, Response<FeedUpdateResponse> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "피드 수정 완료!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();

                } else {
                    Toast.makeText(getContext(),
                            "수정 실패 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FeedUpdateResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // 사진 압축
    private byte[] compressImage(Context context, Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            return stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        View bottomNav = requireActivity().findViewById(R.id.bottomNav);
        if (bottomNav != null) bottomNav.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        View bottomNav = requireActivity().findViewById(R.id.bottomNav);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
    }

    // 장소 추가 화면으로 이동
    private void openAddLocationFragment() {
        Fragment addLocation = new FeedAddLocationFragment();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, addLocation)
                .addToBackStack(null)
                .commit();
    }


}
