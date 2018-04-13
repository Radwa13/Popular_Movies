package com.example.alfa.popularmovies.model;

/**
 * Created by Alfa on 4/8/2018.
 */

public class ReviewAndVideos {

    private  ReviewList reviewList;
    private  VideoList videoList;

    public ReviewList getReviewList() {
        return reviewList;
    }

    public void setReviewList(ReviewList reviewList) {
        this.reviewList = reviewList;
    }

    public VideoList getVideoList() {
        return videoList;
    }

    public void setVideoList(VideoList videoList) {
        this.videoList = videoList;
    }
}
