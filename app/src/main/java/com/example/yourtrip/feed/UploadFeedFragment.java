package com.example.yourtrip.feed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;

import java.util.ArrayList;
import java.util.List;

public class UploadFeedFragment extends Fragment {

    private RecyclerView rvPhotos;
    private Button btnNext;

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

        rvPhotos = view.findViewById(R.id.rv_upload_photos);
        btnNext = view.findViewById(R.id.btnNext);

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> requireActivity().onBackPressed());

        setupRecyclerView();

        updateNextButtonState();

        return view;
    }

    private void setupRecyclerView() {
        // 🔹 가로 스크롤로 변경!
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
