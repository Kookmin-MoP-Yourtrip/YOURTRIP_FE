package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.yourtrip.R;

public class AddLocationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private EditText etPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_location);

        initViews();
        setTopBar();

        // 📌 지도 프래그먼트 붙이기
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentByTag("map_fragment");

        if (mapFragment == null) {
            mapFragment = new MapFragment();
            fm.beginTransaction()
                    .replace(R.id.map_fragment_container, mapFragment, "map_fragment")
                    .commit();
        }

        // 검색창 엔터 입력 시
        etPlaceName.setOnEditorActionListener((v, actionId, event) -> {
            // TODO: 나중에 mapFragment에 이벤트 전달하도록 수정
            return true;
        });
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btnBack);
        etPlaceName = findViewById(R.id.etPlaceName);
    }

    private void setTopBar() {
        tvTitle.setText("장소 추가하기");
        btnBack.setOnClickListener(v -> finish());
    }
}
