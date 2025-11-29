package com.example.yourtrip.mypage;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.yourtrip.auth.LoginActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.commonUtil.FileUtils;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.io.File;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditFragment extends Fragment {

    private ImageView imgProfile;
    private EditText edtNickname, editCurrentPw, editNewPw, editConfirmPw;
    private TextView btnDeleteUser;
    private Button btnSave;

    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {

        imgProfile = v.findViewById(R.id.imgProfile);

        // 플러스(+) 버튼 참조
        ImageView btnAddPhoto = v.findViewById(R.id.btnAddPhoto);

        edtNickname = v.findViewById(R.id.edtNickname);
        editCurrentPw = v.findViewById(R.id.editCurrentPw);
        editNewPw = v.findViewById(R.id.editNewPw);
        editConfirmPw = v.findViewById(R.id.editConfirmPw);
        btnDeleteUser = v.findViewById(R.id.btnDeleteUser);
        btnSave = v.findViewById(R.id.btnSave);

        // 상단바 뒤로가기 버튼
        ImageView btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(vv ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        loadProfile();

        // 기존 클릭 (프로필 이미지 클릭)
        imgProfile.setOnClickListener(vv -> pickImage());

        // 플러스(+) 버튼 클릭 시 이미지 선택
        btnAddPhoto.setOnClickListener(vv -> pickImage());

        btnSave.setOnClickListener(vv -> saveProfile());
        btnDeleteUser.setOnClickListener(vv -> showDeleteConfirmDialog());
    }

    // 1. 프로필 조회 API
    private void loadProfile() {
        ApiService api = RetrofitClient
                .getInstance(requireContext())
                .create(ApiService.class);

        api.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    ProfileResponse p = res.body();

                    edtNickname.setText(p.nickname);

                    Glide.with(requireContext())
                            .load(p.profileImageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_default_profile)
                            .into(imgProfile);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {}
        });
    }

    // 2. 이미지 선택
    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1000);
    }

    @Override
    public void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == 1000 && res == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
            uploadProfileImage();
        }
    }

    // 3. 이미지 업로드
    private void uploadProfileImage() {
        if (selectedImageUri == null) return;

        File file = new File(FileUtils.getPath(requireContext(), selectedImageUri));
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        ApiService api = RetrofitClient
                .getInstance(requireContext())
                .create(ApiService.class);
        api.uploadProfileImage(body).enqueue(new Callback<ProfileImageResponse>() {
            @Override
            public void onResponse(Call<ProfileImageResponse> call, Response<ProfileImageResponse> response) { }
            @Override
            public void onFailure(Call<ProfileImageResponse> call, Throwable t) { }
        });
    }

    // 4. 닉네임 + 비밀번호 변경
    private void saveProfile() {

        ApiService api = RetrofitClient
                .getInstance(requireContext())
                .create(ApiService.class);

        // 닉네임 변경
        NicknameChangeRequest nickReq =
                new NicknameChangeRequest(edtNickname.getText().toString());

        api.updateNickname(nickReq).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) { }
            @Override public void onFailure(Call<Void> call, Throwable t) { }
        });

        // 비밀번호 변경
        if (!editNewPw.getText().toString().isEmpty()) {

            PasswordChangeRequest pwReq =
                    new PasswordChangeRequest(
                            editCurrentPw.getText().toString(),
                            editNewPw.getText().toString()
                    );

            api.updatePassword(pwReq).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) { }
                @Override public void onFailure(Call<Void> call, Throwable t) { }
            });
        }

        Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    // 5. 커스텀 다이얼로그 - 탈퇴 확인
    private void showDeleteConfirmDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_account);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnConfirm = dialog.findViewById(R.id.btnConfirmDelete);
        Button btnCancel = dialog.findViewById(R.id.btnCancelDelete);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAccountApi();   // 실제 탈퇴 API 호출
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // 6. 탈퇴 API -> 탈퇴 완료 팝업
    private void deleteAccountApi() {

        ApiService api = RetrofitClient
                .getInstance(requireContext())
                .create(ApiService.class);

        api.deleteUser().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showDeleteCompleteDialog();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }

    // 7. 탈퇴 완료 팝업
    private void showDeleteCompleteDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_complete);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnOk = dialog.findViewById(R.id.btnDeleteCompleteOk);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finishAffinity();
        });

        dialog.show();
    }
}