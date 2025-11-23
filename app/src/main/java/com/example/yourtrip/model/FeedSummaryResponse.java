package com.example.yourtrip.model;

import java.util.List;

public class FeedSummaryResponse {
    private Long feedId; // 피드 ID
    private List<FeedMediaItem> mediaList;

    public Long getFeedId() {
        return feedId;
    }

    public List<FeedMediaItem> getMediaList() { // 첫번째 이미지 URL
        return mediaList;
    }
}

