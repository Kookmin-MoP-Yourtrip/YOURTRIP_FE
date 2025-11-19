package com.example.yourtrip.mytrip;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;  // TextView import 추가
import com.example.yourtrip.R;
import java.util.ArrayList;
import java.util.List;

public class CreateCourseDayDetailFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 이 프래그먼트의 레이아웃을 인플레이트
        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerViewTripDays);

        // LinearLayoutManager로 가로 스크롤 설정
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Adapter 설정 (아이템 리스트를 어댑터에 전달)
        adapter = new TripAdapter(getItemList());  // getItemList()는 표시할 아이템 리스트를 반환
        recyclerView.setAdapter(adapter);

        return view;
    }

    // 예시로 사용할 아이템 리스트 (임시 데이터)
    private List<String> getItemList() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add("Item " + i); // 예시 아이템: Item 0, Item 1, ...
        }
        return items;
    }

    // RecyclerView에서 사용할 Adapter
    public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

        private List<String> tripList;

        // Adapter 생성자
        public TripAdapter(List<String> tripList) {
            this.tripList = tripList;
        }

        @Override
        public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);  // 간단한 텍스트 아이템 사용
            return new TripViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TripViewHolder holder, int position) {
            String item = tripList.get(position);
            holder.textView.setText(item);  // TextView에 텍스트 설정
        }

        @Override
        public int getItemCount() {
            return tripList.size();
        }

        // RecyclerView의 ViewHolder
        public class TripViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;  // TextView 선언

            public TripViewHolder(View view) {
                super(view);
                textView = view.findViewById(android.R.id.text1);  // 기본 텍스트뷰 사용
            }
        }
    }
}


//package com.example.yourtrip.mytrip;
//
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.util.Log;
//
//import com.example.yourtrip.R;
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
//        // 이 프래그먼트의 레이아웃을 인플레이트
//        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
//
//        // RecyclerView 초기화
//        recyclerView = view.findViewById(R.id.recyclerViewTripDays);
//
//        // LinearLayoutManager로 한 화면에 3개의 아이템을 표시
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//
//        // GridLayoutManager로 한 화면에 3개의 아이템을 표시 LinearLayoutManager->GridLayoutManager로 변경
////        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.HORIZONTAL, false);
////        recyclerView.setLayoutManager(layoutManager);
//
//        // Adapter 설정 (아이템 리스트를 어댑터에 전달)
//        adapter = new TripAdapter(getItemList());  // getItemList()는 표시할 아이템 리스트를 반환
//        recyclerView.setAdapter(adapter);
//
//        return view;
//    }
//
//    // 예시로 사용할 아이템 리스트 (임시 데이터)
//    private List<String> getItemList() {
//        List<String> items = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            items.add("Item " + i); // 예시 아이템: Item 0, Item 1, ...
//        }
//
//        // 아이템 리스트 로그 찍기
//        Log.d("CreateCourseDayDetailFragment", "아이템 리스트: " + items.toString());
//        return items;
//    }
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
//            // item_trip_day.xml을 사용하여 아이템 레이아웃을 생성
//            View itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_trip_day, parent, false);  // 여기에 item_trip_day.xml 사용
//
//            // 각 아이템 너비를 화면 너비의 1/3로 설정
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
//            params.width = parent.getWidth() / 3;  // 한 화면에 3개씩 표시되도록 너비 설정
//            itemView.setLayoutParams(params);
//
//            return new TripViewHolder(itemView);
//        }
//
//
//        @Override
//        public void onBindViewHolder(TripViewHolder holder, int position) {
//            String item = tripList.get(position);
//            holder.textView.setText(item);  // TextView에 텍스트 설정
//
//            // 아이템 바인딩 로그 찍기
//            Log.d("CreateCourseDayDetailFragment", "아이템 바인딩: " + item);
//        }
//
//        @Override
//        public int getItemCount() {
//            return tripList.size();
//        }
//
//        // RecyclerView의 ViewHolder
//        public class TripViewHolder extends RecyclerView.ViewHolder {
//            public TextView textView;  // TextView 선언
//
//            public TripViewHolder(View view) {
//                super(view);
//                // item_trip_day.xml에서 정의된 ID와 정확히 연결된 텍스트뷰를 연결해야함
//                textView = view.findViewById(R.id.textViewItem);
//            }
//        }
//    }
//}
