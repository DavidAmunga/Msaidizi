package com.buttercell.msaidizi.model;

import java.io.Serializable;

/**
 * Created by amush on 21-Mar-18.
 */

public class Client implements Serializable{

    public String userName, userEmail, userPhone, userBio, userImage;


    public Client(String userName, String userEmail, String userPhone, String userBio, String userImage) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userBio = userBio;
        this.userImage = userImage;
    }

    public Client() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
