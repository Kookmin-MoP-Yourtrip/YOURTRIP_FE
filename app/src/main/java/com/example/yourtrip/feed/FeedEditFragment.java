package com.example.yourtrip.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

    private int feedId;

    // 기존 이미지 URL (서버에서 받은 순서 그대로)
    private final List<String> originalImages = new ArrayList<>();

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

                            if (uri != null && (originalImages.size() + newImages.size()) < MAX_IMAGES) {
                                newImages.add(uri);
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
                            originalImages.remove(position);
                        } else {
                            newImages.remove(position - originalImages.size());
                        }

                        adapter.setEditMode(originalImages, newImages);
                        adapter.notifyDataSetChanged();
                    }
                }
        );


        rvPhotos.setAdapter(adapter);
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

                // 기존 사진 URL 저장
                originalImages.clear();
                if (data.getMediaList() != null) {
                    for (FeedMediaDetailResponse m : data.getMediaList()) {
                        originalImages.add(m.getMediaUrl());
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

        // 새 이미지 있을 때만 새 이미지 전송
        if (!newImages.isEmpty()) {

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

        } else {
            // 🔥 새 이미지 없다 → 기존 이미지는 자동 유지 (fileParts = empty)
            Log.e("EDIT", "기존 이미지 유지 → mediaFiles 전송 안함");
        }

        // JSON 데이터 생성
        String content = editText.getText().toString().trim();
        FeedUpdateRequest updateRequest = new FeedUpdateRequest(
                null, null, content, new ArrayList<>(), null
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

}
