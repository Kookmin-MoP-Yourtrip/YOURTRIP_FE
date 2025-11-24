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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.FileUtils;
import com.example.yourtrip.model.FeedUploadRequest;
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

public class FeedUploadFragment extends Fragment {

    private RecyclerView rvPhotos;
    private Button btnUpload;
    private EditText editText;

    private UploadFeedAdapter adapter;
    private final List<Uri> selectedImages = new ArrayList<>();
    private static final int MAX_IMAGES = 5;

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().findViewById(R.id.bottomNav).setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
    }

    // 갤러리 오픈 런처
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {

                            Uri uri = result.getData().getData();
                            if (uri != null && selectedImages.size() < MAX_IMAGES) {
                                selectedImages.add(uri);
                                adapter.notifyDataSetChanged();
                                updateUploadButtonState();
                            }
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_upload, container, false);

        editText = view.findViewById(R.id.editDynamic);
        rvPhotos = view.findViewById(R.id.rv_upload_photos);
        btnUpload = view.findViewById(R.id.btn_feed_upload);

        btnUpload.setOnClickListener(v -> uploadFeedToServer());
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> requireActivity().onBackPressed());

        setupRecyclerView();
        updateUploadButtonState();

        return view;
    }

    private void setupRecyclerView() {
        rvPhotos.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        adapter = new UploadFeedAdapter(selectedImages, new UploadFeedAdapter.OnUploadClickListener() {
            @Override
            public void onAddPhotoClick() {
                openGallery();
            }

            @Override
            public void onDeletePhotoClick(int position) {
                selectedImages.remove(position);
                adapter.notifyDataSetChanged();
                updateUploadButtonState();
            }
        });

        rvPhotos.setAdapter(adapter);
    }

    private void uploadFeedToServer() {

        if (selectedImages.isEmpty()) {
            Toast.makeText(getContext(), "사진을 1장 이상 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getAuthService();
        List<MultipartBody.Part> fileParts = new ArrayList<>();

        // ▣ 이미지 → byte[] → MultipartBody
        for (Uri uri : selectedImages) {
            try {
                String fileName = FileUtils.getFileName(requireContext(), uri);

                // 🔥 압축된 이미지로 교체
                byte[] compressedBytes = compressImage(requireContext(), uri);

                RequestBody fileBody =
                        RequestBody.create(MediaType.parse("image/jpeg"), compressedBytes);

                MultipartBody.Part part = MultipartBody.Part.createFormData(
                        "mediaFiles",
                        fileName,
                        fileBody
                );

                fileParts.add(part);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "이미지 처리 오류", Toast.LENGTH_SHORT).show();
                return;
            }
        }

//        for (Uri uri : selectedImages) {
//            try {
//                // MIME 타입 읽기
//                String mimeType = requireContext().getContentResolver().getType(uri);
//                if (mimeType == null) mimeType = "image/jpeg"; // fallback
//
//                // 파일명
//                String fileName = FileUtils.getFileName(requireContext(), uri);
//
//                // InputStream -> byte[] 변환
//                InputStream is = requireContext().getContentResolver().openInputStream(uri);
//                byte[] bytes = FileUtils.readBytes(is);
//
//                // 정확한 MIME 타입으로 RequestBody 생성
//                RequestBody fileBody = RequestBody.create(
//                        MediaType.parse(mimeType),
//                        bytes
//                );
//
//                MultipartBody.Part part = MultipartBody.Part.createFormData(
//                        "mediaFiles",
//                        fileName,
//                        fileBody
//                );
//
//                fileParts.add(part);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(getContext(), "이미지 처리 오류", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }


        // ▣ JSON 부분 (title, location, content 모두 선택)
        String content = editText.getText().toString().trim();
        if (content.isEmpty()) content = null;

        FeedUploadRequest data = new FeedUploadRequest(
                null,   // title
                null,   // location
                content // caption
        );
//        String content = editText.getText().toString().trim();
//        if (content.isEmpty()) content = "";
//
//        FeedUploadRequest data = new FeedUploadRequest(
//                "",        // title 선택 → 빈 String으로
//                "",        // location 선택 → 빈 String으로
//                content
//        );

        RequestBody jsonBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                new Gson().toJson(data)
        );


        Log.e("UPLOAD_DEBUG", "📤 JSON 데이터 = " + new Gson().toJson(data));
        for (Uri u : selectedImages) {
            Log.e("UPLOAD_DEBUG", "📸 선택된 이미지 = " + FileUtils.getFileName(requireContext(), u));
        }


        // ▣ Retrofit 업로드 요청
        api.uploadFeed(fileParts, jsonBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "피드 업로드 완료!", Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new FeedFragment())
                            .commit();

                } else {
                    Log.e("UPLOAD", "Error = " + response.code());
                    Toast.makeText(getContext(),
                            "업로드 실패 (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void updateUploadButtonState() {
        btnUpload.setEnabled(!selectedImages.isEmpty());
        btnUpload.setAlpha(selectedImages.isEmpty() ? 0.4f : 1f);
    }

    // 사진 앞축
    private byte[] compressImage(Context context, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream); // 🔥 품질 70%로 압축
            return stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
