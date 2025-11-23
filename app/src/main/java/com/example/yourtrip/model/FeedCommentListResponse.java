package com.example.yourtrip.model;

import java.util.List;

public class FeedCommentListResponse {
    private List<FeedCommentDetailResponse> comments;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;

    public List<FeedCommentDetailResponse> getComments() {
        return comments;
    }
}
