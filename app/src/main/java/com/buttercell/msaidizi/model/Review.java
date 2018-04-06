package com.buttercell.msaidizi.model;

/**
 * Created by amush on 21-Mar-18.
 */

public class Review {
    private String reviewer_name,reviewer_image, service_id,review_comments;
    private int review_rating;

    public Review(String reviewer_name, String reviewer_image, int review_rating, String service_id, String review_comments) {
        this.reviewer_name = reviewer_name;
        this.reviewer_image = reviewer_image;
        this.service_id = service_id;
        this.review_comments = review_comments;
        this.review_rating = review_rating;
    }

    public Review() {
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public void setReviewer_name(String reviewer_name) {
        this.reviewer_name = reviewer_name;
    }

    public String getReviewer_image() {
        return reviewer_image;
    }

    public void setReviewer_image(String reviewer_image) {
        this.reviewer_image = reviewer_image;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getReview_comments() {
        return review_comments;
    }

    public void setReview_comments(String review_comments) {
        this.review_comments = review_comments;
    }

    public int getReview_rating() {
        return review_rating;
    }

    public void setReview_rating(int review_rating) {
        this.review_rating = review_rating;
    }
}
