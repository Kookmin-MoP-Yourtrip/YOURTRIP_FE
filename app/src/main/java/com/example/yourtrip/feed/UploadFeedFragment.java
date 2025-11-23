package com.example.yourtrip.feed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFeedFragment extends Fragment {

    private RecyclerView rvPhotos;
    private Button btnNext;
    private EditText editText;

    private UploadFeedAdapter adapter;
    private final List<Uri> selectedImages = new ArrayList<>();
    private static final int MAX_IMAGES = 5;

    @Override
    public void onResume() {
        super.onResume();
        // 하단바 숨기기
        requireActivity().findViewById(R.id.bottomNav).setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 하단바 다시 보이기
        requireActivity().findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
    }

    // 갤러리 선택 런처
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            if (uri != null && selectedImages.size() < MAX_IMAGES) {
                                selectedImages.add(uri);
                                adapter.notifyDataSetChanged();
                                updateNextButtonState();
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
        btnNext = view.findViewById(R.id.btn_feed_upload);
        btnNext.setOnClickListener(v -> uploadFeedToServer());

        view.findViewById(R.id.btn_cancel)
                .setOnClickListener(v -> requireActivity().onBackPressed());

        setupRecyclerView();
        updateNextButtonState();

        return view;
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvPhotos.setLayoutManager(layoutManager);

        adapter = new UploadFeedAdapter(selectedImages, new UploadFeedAdapter.OnUploadClickListener() {
            @Override
            public void onAddPhotoClick() {
                openGallery();
            }

            @Override
            public void onDeletePhotoClick(int position) {
                selectedImages.remove(position);
                adapter.notifyDataSetChanged();
                updateNextButtonState();
            }
        });

        rvPhotos.setAdapter(adapter);
    }

    private void uploadFeedToServer() {

        ApiService api = RetrofitClient.getAuthService();

        List<MultipartBody.Part> fileParts = new ArrayList<>();

        for (Uri uri : selectedImages) {
            try {

                // 파일 이름 추출
                String fileName = FileUtils.getFileName(requireContext(), uri);

                // InputStream → byte[]
                InputStream is = requireContext().getContentResolver().openInputStream(uri);
                byte[] bytes = FileUtils.readBytes(is);

                // byte[] 을 RequestBody로 변환
                RequestBody fileBody = RequestBody.create(
                        MediaType.parse("image/*"), bytes
                );

                MultipartBody.Part part = MultipartBody.Part.createFormData(
                        "mediaFiles",
                        fileName,
                        fileBody
                );

                fileParts.add(part);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "이미지 변환 실패", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // JSON 데이터 생성
        FeedUploadRequest data = new FeedUploadRequest(
                "제목",
                "장소",
                editText.getText().toString()

        );

        RequestBody jsonBody = RequestBody.create(
                MediaType.parse("application/json"),
                new Gson().toJson(data)
        );

        // 업로드 요청
        api.uploadFeed(fileParts, jsonBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();

                    // 스택 전체 클리어
                    requireActivity().getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    // 피드 리스트로 이동
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new FeedFragment())
                            .commit();

                } else {
                    Toast.makeText(getContext(),
                            "업로드 실패 (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    Log.e("UPLOAD", "실패 CODE=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void updateNextButtonState() {
        if (selectedImages.isEmpty()) {
            btnNext.setEnabled(false);
            btnNext.setAlpha(0.4f);
        } else {
            btnNext.setEnabled(true);
            btnNext.setAlpha(1f);
        }
    }
}


//package com.example.yourtrip.feed;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.commonUtil.FileUtils;
//import com.example.yourtrip.model.FeedUploadRequest;
//import com.example.yourtrip.network.ApiService;
//import com.example.yourtrip.network.RetrofitClient;
//import com.google.gson.Gson;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class UploadFeedFragment extends Fragment {
//
//    private RecyclerView rvPhotos;
//    private Button btnNext;
//    private EditText editText;
//
//    private UploadFeedAdapter adapter;
//    private final List<Uri> selectedImages = new ArrayList<>();
//    private static final int MAX_IMAGES = 5;
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // 하단바 숨기기
//        requireActivity().findViewById(R.id.bottomNav).setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        // 하단바 다시 보이기
//        requireActivity().findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
//    }
//
//
//    // 갤러리 선택 런처
//    private final ActivityResultLauncher<Intent> imagePickerLauncher =
//            registerForActivityResult(
//                    new ActivityResultContracts.StartActivityForResult(),
//                    result -> {
//                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                            Uri uri = result.getData().getData();
//                            if (uri != null && selectedImages.size() < MAX_IMAGES) {
//                                selectedImages.add(uri);
//                                adapter.notifyDataSetChanged();
//                                updateNextButtonState();
//                            }
//                        }
//                    });
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_feed_upload, container, false);
//
//        editText = view.findViewById(R.id.editDynamic);
//        rvPhotos = view.findViewById(R.id.rv_upload_photos);
//        btnNext = view.findViewById(R.id.btn_feed_upload);
//        btnNext.setOnClickListener(v -> uploadFeedToServer());
//
//        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> requireActivity().onBackPressed());
//
//
//
//        setupRecyclerView();
//
//        updateNextButtonState();
//
//        return view;
//    }
//
//    private void setupRecyclerView() {
//        // 🔹 가로 스크롤로 변경!
//        LinearLayoutManager layoutManager =
//                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//        rvPhotos.setLayoutManager(layoutManager);
//
//        adapter = new UploadFeedAdapter(selectedImages, new UploadFeedAdapter.OnUploadClickListener() {
//            @Override
//            public void onAddPhotoClick() {
//                openGallery();
//            }
//
//            @Override
//            public void onDeletePhotoClick(int position) {
//                selectedImages.remove(position);
//                adapter.notifyDataSetChanged();
//                updateNextButtonState();
//            }
//        });
//
//        rvPhotos.setAdapter(adapter);
//    }
//
//    private void uploadFeedToServer() {
//        ApiService api = RetrofitClient.getAuthService();
//
//        // ① 이미지 파일 변환
//        List<MultipartBody.Part> fileParts = new ArrayList<>();
//
//        for (int i = 0; i < selectedImages.size(); i++) {
//            Uri uri = selectedImages.get(i);
//
//            // 🔥 여기 추가!
//            String realPath = FileUtils.getPath(requireContext(), uri);
//            Log.e("UPLOAD_PATH", "real = " + realPath);
//
//            File file = new File(FileUtils.getPath(requireContext(), uri));
//            RequestBody fileBody =
//                    RequestBody.create(MediaType.parse("image/*"), file);
//
//            MultipartBody.Part part = MultipartBody.Part.createFormData(
//                    "mediaFiles",
//                    file.getName(),
//                    fileBody
//            );
//            fileParts.add(part);
//        }
//
//        // ② JSON 데이터 생성
//        FeedUploadRequest data = new FeedUploadRequest(
//                "제목",            // title
//                "장소",            // location
//                editText.getText().toString(),  // content
//                Arrays.asList("여행", "제주"),  // hashtags
//                0                  // uploadCourseId
//        );
//
//        Gson gson = new Gson();
//        RequestBody jsonBody = RequestBody.create(
//                MediaType.parse("application/json"),
//                gson.toJson(data)
//        );
//
//        // ③ 업로드 요청
//        api.uploadFeed(fileParts, jsonBody).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//
//                    // ⭐ 성공 메시지
//                    Toast.makeText(getContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
//
//                    // ⭐ (1) 기존 Fragment 스택 모두 제거
//                    requireActivity().getSupportFragmentManager()
//                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//                    // ⭐ (2) 피드 리스트 화면으로 이동 (FeedFragment)
//                    requireActivity().getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.fragmentContainer, new FeedFragment())
//                            .commit();
//
//                } else {
//                    Toast.makeText(getContext(),
//                            "업로드 실패 (" + response.code() + ")",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(getContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
//                t.printStackTrace();
//            }
//        });
//    }
//
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        imagePickerLauncher.launch(intent);
//    }
//
//    private void updateNextButtonState() {
//        if (selectedImages.isEmpty()) {
//            btnNext.setEnabled(false);
//            btnNext.setAlpha(0.4f);
//        } else {
//            btnNext.setEnabled(true);
//            btnNext.setAlpha(1f);
//        }
//    }
//}
