package com.buttercell.msaidizi.model;

/**
 * Created by amush on 21-Mar-18.
 */

public class Msaidizi {
    private String userName, userEmail, userPhone, userSkill, userBio, userImage;
    private int payrate;


    public Msaidizi() {
    }

    public Msaidizi(String userName, String userEmail, String userPhone, String userSkill, String userBio, String userImage,int payrate) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userSkill = userSkill;
        this.userBio = userBio;
        this.userImage = userImage;
        this.payrate=payrate;
    }

    public int getPayrate() {
        return payrate;
    }

    public void setPayrate(int payrate) {
        this.payrate = payrate;
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

    public String getUserSkill() {
        return userSkill;
    }

    public void setUserSkill(String userSkill) {
        this.userSkill = userSkill;
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
