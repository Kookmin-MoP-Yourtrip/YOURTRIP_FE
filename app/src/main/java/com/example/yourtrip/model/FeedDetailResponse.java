package com.example.yourtrip.model;

import java.util.List;

public class FeedDetailResponse {
    private Long feedId;
    private List<FeedMedialItem> mediaList;

    public Long getFeedId() {
        return feedId;
    }

    public List<FeedMedialItem> getMediaList() {
        return mediaList;
    }
}

