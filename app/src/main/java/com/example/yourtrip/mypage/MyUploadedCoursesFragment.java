package com.example.yourtrip.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.UploadCourseItem;
import com.example.yourtrip.model.UploadCourseListResponse;
import com.example.yourtrip.mytrip.upload.AfterUploadCourseDetailActivity;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyUploadedCoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyUploadCourseAdapter adapter;
    private List<UploadCourseItem> courseList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_uploaded_courses, container, false);

        recyclerView = view.findViewById(R.id.rv_my_uploaded_courses);

        // 2열 그리드 레이아웃
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyUploadCourseAdapter(courseList);
        adapter.setOnItemClickListener(item -> {
            // 클릭 시 AfterUploadCourseDetailActivity로 이동
            Intent intent = new Intent(requireActivity(), AfterUploadCourseDetailActivity.class);
            intent.putExtra("uploadCourseId", (long) item.uploadCourseId);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadMyUploadedCourses();

        return view;
    }

    private void loadMyUploadedCourses() {
        ApiService api = RetrofitClient.getAuthService(getContext());

        api.getMyUploadedCourses().enqueue(new Callback<UploadCourseListResponse>() {
            @Override
            public void onResponse(Call<UploadCourseListResponse> call, Response<UploadCourseListResponse> response) {
                Log.d("MY_UPLOADED", "Response code: " + response.code());

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("MY_UPLOADED", "ERROR: " + response.code());
                    return;
                }

                List<UploadCourseItem> serverList = response.body().uploadCourses;

                if (serverList == null) {
                    Log.e("MY_UPLOADED", "List is NULL");
                    return;
                }

                courseList.clear();
                courseList.addAll(serverList);
                adapter.notifyDataSetChanged();

                Log.d("MY_UPLOADED", "Loaded " + courseList.size() + " uploaded courses");
            }

            @Override
            public void onFailure(Call<UploadCourseListResponse> call, Throwable t) {
                Log.e("MY_UPLOADED", "FAILURE: " + t.getMessage());
            }
        });
    }
}
