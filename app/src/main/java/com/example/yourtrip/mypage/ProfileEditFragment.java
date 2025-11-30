package com.example.yourtrip.mypage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.auth.LoginActivity;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditFragment extends Fragment {

    private ImageView imgProfile;
    private EditText edtNickname, editCurrentPw, editNewPw, editConfirmPw;
    private TextView tvNicknameError, btnDeleteUser;
    private Button btnSave;

    private Uri selectedImageUri = null;

    // ActivityResultLauncher 선언
    private ActivityResultLauncher<Intent> imagePickerLauncher;

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
                ((MainActivity) requireActivity()).switchFragment(new MypageFragment(), false)
        );

        loadProfile();

        // 이미지 선택 ActivityResultLauncher 등록
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgProfile.setImageURI(selectedImageUri);
                        uploadProfileImage();
                    }
                }
        );

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

    // 이미지 선택
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // 프로필 불러오기
    private void loadProfile() {

        ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);

        api.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                ProfileResponse p = res.body();
                edtNickname.setText(p.nickname);

                Glide.with(requireContext())
                        .load(p.profileImageUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .circleCrop()
                        .placeholder(R.drawable.ic_default_profile)
                        .into(imgProfile);
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {}
        });
    }

    // 이미지 업로드
    private void uploadProfileImage() {
        if (selectedImageUri == null) return;

        try {
            InputStream in = requireContext().getContentResolver().openInputStream(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] bytes = baos.toByteArray();

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpeg"), bytes);

            MultipartBody.Part filePart =
                    MultipartBody.Part.createFormData("file", "profile.jpg", requestFile);

            ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);

            api.uploadProfileImage(filePart).enqueue(new Callback<ProfileImageResponse>() {
                @Override
                public void onResponse(Call<ProfileImageResponse> call, Response<ProfileImageResponse> res) {
                    if (res.isSuccessful() && res.body() != null) {

                        String newUrl = res.body().profileImageUrl;

                        // MyPageFragment 반영
                        MypageFragment.latestProfileUrl = newUrl;

                        Glide.with(requireContext())
                                .load(newUrl)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .circleCrop()
                                .into(imgProfile);

                    } else {
                        Toast.makeText(requireContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProfileImageResponse> call, Throwable t) {
                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "이미지 처리 중 오류", Toast.LENGTH_SHORT).show();
        }
    }

    // 닉네임 체크
    private void checkNicknameDuplicate(String nickname) {
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

    // 저장
    private void saveProfile() {
        ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);

        if (tvNicknameError.getVisibility() == View.VISIBLE) {
            Toast.makeText(requireContext(), "닉네임 중복을 해결해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 닉네임 변경
        NicknameChangeRequest req = new NicknameChangeRequest(edtNickname.getText().toString());
        api.updateNickname(req).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> res) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });

        // 비밀번호 변경
        String oldPw = editCurrentPw.getText().toString();
        String newPw = editNewPw.getText().toString();
        String confirmPw = editConfirmPw.getText().toString();

        if (!newPw.isEmpty()) {

            if (!newPw.equals(confirmPw)) {
                Toast.makeText(requireContext(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            PasswordChangeRequest pwReq = new PasswordChangeRequest(oldPw, newPw);

            api.updatePassword(pwReq).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> res) {}
                @Override public void onFailure(Call<Void> call, Throwable t) {}
            });
        }

        Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();

        ((MainActivity) requireActivity()).switchFragment(new MypageFragment(), false);
    }

    // 탈퇴 confirm dialog
    private void showDeleteConfirmDialog() {
        AlertDialog dialog;

        AlertDialog.Builder builder =
                new AlertDialog.Builder(requireContext(), R.style.TransparentDialogStyle);
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button ok = view.findViewById(R.id.btnConfirmDelete);
        Button cancel = view.findViewById(R.id.btnCancelDelete);

        ok.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAccountApi();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deleteAccountApi() {
        ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);

        api.deleteUser().enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> res) {
                showDeleteCompleteDialog();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void showDeleteCompleteDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(requireContext(), R.style.TransparentDialogStyle);
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_complete, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button ok = view.findViewById(R.id.btnDeleteCompleteOk);
        ok.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finishAffinity();
        });

        dialog.show();
    }
}