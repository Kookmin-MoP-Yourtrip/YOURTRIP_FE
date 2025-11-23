package com.example.yourtrip.model;

import java.util.List;

public class FeedSummaryResponse {
    private int feedId;
    private String nickname;
    private String title;
    private String location;
    private String content;
    private List<FeedMediaItem> mediaList;

    public int getFeedId() { return feedId; }
    public List<FeedMediaItem> getMediaList() { return mediaList; }
}


