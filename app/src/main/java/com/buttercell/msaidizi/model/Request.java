package com.buttercell.msaidizi.model;

/**
 * Created by amush on 21-Mar-18.
 */

public class Request {
    public String userName,userImage,userComments,requestDate,status;

    public Request(String userName, String userImage, String userComments, String requestDate,String status) {
        this.userName = userName;
        this.userImage = userImage;
        this.userComments = userComments;
        this.requestDate = requestDate;
        this.status=status;
    }

    public Request() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserComments() {
        return userComments;
    }

    public void setUserComments(String userComments) {
        this.userComments = userComments;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
}
