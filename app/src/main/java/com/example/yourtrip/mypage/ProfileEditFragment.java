package com.example.yourtrip.mypage;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
    private TextView btnDeleteUser, tvNicknameError;
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

        ImageView btnAddPhoto = v.findViewById(R.id.btnAddPhoto);

        edtNickname = v.findViewById(R.id.edtNickname);
        tvNicknameError = v.findViewById(R.id.tvNicknameError);
        editCurrentPw = v.findViewById(R.id.editCurrentPw);
        editNewPw = v.findViewById(R.id.editNewPw);
        editConfirmPw = v.findViewById(R.id.editConfirmPw);
        btnDeleteUser = v.findViewById(R.id.btnDeleteUser);
        btnSave = v.findViewById(R.id.btnSave);

        ImageView btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(vv ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        loadProfile();

        imgProfile.setOnClickListener(vv -> pickImage());
        btnAddPhoto.setOnClickListener(vv -> pickImage());

        btnSave.setOnClickListener(vv -> saveProfile());
        btnDeleteUser.setOnClickListener(vv -> showDeleteConfirmDialog());

        edtNickname.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNicknameDuplicate(s.toString());
            }
        });
    }

    // 1. 프로필 조회
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

    // 닉네임 중복 체크
    private void checkNicknameDuplicate(String nickname) {

        if (nickname.trim().isEmpty()) {
            tvNicknameError.setVisibility(View.GONE);
            return;
        }

        ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);

        api.checkNickname(nickname).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> res) {
                if (res.isSuccessful()) {
                    tvNicknameError.setVisibility(View.GONE);
                } else {
                    tvNicknameError.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }


    // 비밀번호 검증
    private boolean isValidPassword(String pw) {
        return pw.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    // 저장
    private void saveProfile() {

        ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);

        if (tvNicknameError.getVisibility() == View.VISIBLE) {
            Toast.makeText(requireContext(), "닉네임 중복을 해결해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        NicknameChangeRequest nickReq =
                new NicknameChangeRequest(edtNickname.getText().toString());

        api.updateNickname(nickReq).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) { }
            @Override public void onFailure(Call<Void> call, Throwable t) { }
        });

        String oldPw = editCurrentPw.getText().toString();
        String newPw = editNewPw.getText().toString();
        String confirmPw = editConfirmPw.getText().toString();

        if (!newPw.isEmpty()) {

            if (!isValidPassword(newPw)) {
                Toast.makeText(requireContext(), "비밀번호는 영문+숫자 포함 최소 8자입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPw.equals(confirmPw)) {
                Toast.makeText(requireContext(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            PasswordChangeRequest pwReq =
                    new PasswordChangeRequest(oldPw, newPw);

            api.updatePassword(pwReq).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) { }
                @Override public void onFailure(Call<Void> call, Throwable t) { }
            });
        }

        Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    // 5. 탈퇴 확인 팝업
    private void showDeleteConfirmDialog() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(requireContext(), R.style.TransparentDialogStyle);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnConfirm = dialogView.findViewById(R.id.btnConfirmDelete);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelDelete);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAccountApi();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    // 탈퇴 API
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

    // 7. 탈퇴 완료 팝업 (수정됨)
    private void showDeleteCompleteDialog() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(requireContext(), R.style.TransparentDialogStyle);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_complete, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnOk = dialogView.findViewById(R.id.btnDeleteCompleteOk);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finishAffinity();
        });

        dialog.show();
    }
}