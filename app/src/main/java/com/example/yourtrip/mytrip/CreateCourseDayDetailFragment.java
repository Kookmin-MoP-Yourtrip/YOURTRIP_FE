package com.example.yourtrip.mytrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.LocationItem;

import java.util.ArrayList;
import java.util.List;

public class CreateCourseDayDetailFragment extends Fragment {

    private ArrayList<LocationItem> locationList  = new ArrayList<>();
    private LocationAdapter locationAdapter;
    private RecyclerView rvLocations;

    // 🔵 AddLocationActivity → 결과 받는 Launcher
    private ActivityResultLauncher<Intent> addLocationLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {

                                // 반환된 값 받기
                                LocationItem item = new LocationItem();
                                item.setPlaceName(data.getStringExtra("placeName"));
                                item.setAddress(data.getStringExtra("address"));
                                item.setLat(data.getDoubleExtra("lat", 0));
                                item.setLng(data.getDoubleExtra("lng", 0));

                                // 리스트에 추가
                                locationList.add(item);
                                locationAdapter.notifyItemInserted(locationList.size());
                            }
                        }
                    }
            );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);

        // ===============================
        // 1) 상단 일차 스크롤 (dayList)
        // ===============================
        ArrayList<String> dayList = getArguments().getStringArrayList("dayList");
        Log.d("CreateCourseDayDetail", "받은 dayList = " + dayList);

        RecyclerView rvDays = view.findViewById(R.id.recyclerViewTripDays);
        rvDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDays.setAdapter(new TripAdapter(dayList));


        // ===============================
        // 2) 장소 리스트 RecyclerView
        // ===============================
        locationList = new ArrayList<>();

        RecyclerView rvLocations = view.findViewById(R.id.recyclerLocationList);
        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
        locationAdapter = new LocationAdapter(locationList);
        rvLocations.setAdapter(locationAdapter);


        // ===============================
        // 3) 장소 추가하기 버튼 → Activity 이동
        // ===============================
        locationAdapter.setOnAddClickListener(() -> {
            Intent intent = new Intent(getActivity(), AddLocationActivity.class);
            addLocationLauncher.launch(intent);
        });


        // ===============================
        // 4) 장소 삭제하기 버튼
        // ===============================
        locationAdapter.setOnDeleteClickListener(position -> {
            locationList.remove(position);
            locationAdapter.notifyItemRemoved(position);
            locationAdapter.notifyItemRangeChanged(position, locationList.size());
        });

        return view;
    }


    // ===============================
    // TripAdapter (일차 스크롤 영역)
    // ===============================
    public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

        private List<String> tripList;

        public TripAdapter(List<String> tripList) {
            this.tripList = tripList;
        }

        @Override
        public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new TripViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TripViewHolder holder, int position) {
            String item = tripList.get(position);
            holder.textView.setText(item);
        }

        @Override
        public int getItemCount() {
            return tripList.size();
        }

        // ViewHolder
        public class TripViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public TripViewHolder(View view) {
                super(view);
                textView = view.findViewById(android.R.id.text1);
            }
        }
    }
}


//장소 추가 액티비티 연결 전 코드
//package com.example.yourtrip.mytrip;
//
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log; // 🔵 추가됨
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.model.LocationItem;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CreateCourseDayDetailFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // 이 프래그먼트의 레이아웃을 인플레이트
//        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
//
//        // Activity에서 전달한 dayList 받기
//        ArrayList<String> dayList = getArguments().getStringArrayList("dayList");
//        Log.d("CreateCourseDayDetail", "받은 dayList = " + dayList);
//
//        // 일차 스크롤 RecyclerView
//        recyclerView = view.findViewById(R.id.recyclerViewTripDays);
//        // LinearLayoutManager로 가로 스크롤 설정
//        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new TripAdapter(dayList);
//        recyclerView.setAdapter(adapter);
//
//        // 장소 리스트 RecyclerView -------------------------
//        ArrayList<LocationItem> locationList = new ArrayList<>();
//
//        // 2) RecyclerView 초기화
//        RecyclerView rvLocations = view.findViewById(R.id.recyclerLocationList);
//        LocationAdapter locationAdapter = new LocationAdapter(locationList);
//        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvLocations.setAdapter(locationAdapter);
//
//        // 3) 장소 추가하기 (+)
//        locationAdapter.setOnAddClickListener(() -> {
//            locationList.add(new LocationItem());
//            locationAdapter.notifyItemInserted(locationList.size());
//        });
//
//        // 4) 장소 삭제하기 (X)
//        locationAdapter.setOnDeleteClickListener(position -> {
//            locationList.remove(position);
//            locationAdapter.notifyItemRemoved(position);
//            locationAdapter.notifyItemRangeChanged(position, locationList.size());
//        });
//
//        return view;
//    }
//
//
//    // RecyclerView에서 사용할 Adapter
//    public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
//
//        private List<String> tripList;
//
//        // Adapter 생성자
//        public TripAdapter(List<String> tripList) {
//            this.tripList = tripList;
//        }
//
//        @Override
//        public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(android.R.layout.simple_list_item_1, parent, false);
////                    .inflate(R.layout.item_trip_day, parent, false);
//            return new TripViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(TripViewHolder holder, int position) {
//            String item = tripList.get(position);
//            holder.textView.setText(item);
//        }
//
//        @Override
//        public int getItemCount() {
//            return tripList.size();
//        }
//
//        // RecyclerView의 ViewHolder
//        public class TripViewHolder extends RecyclerView.ViewHolder {
//            public TextView textView;
//
//            public TripViewHolder(View view) {
//                super(view);
////                textView = view.findViewById(R.id.tvDayItem);
//                textView = view.findViewById(android.R.id.text1);
//            }
//        }
//    }
//}
//
