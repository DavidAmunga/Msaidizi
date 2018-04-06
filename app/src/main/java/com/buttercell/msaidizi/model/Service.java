package com.buttercell.msaidizi.model;

import java.io.Serializable;

/**
 * Created by amush on 23-Mar-18.
 */

public class Service implements Serializable {
    public String servicer,servicerId,servicerImage,serviceStatus,servicerComments,servicerSkill;


    public Service(String servicer, String servicerId, String servicerImage, String serviceStatus, String servicerComments, String servicerSkill) {
        this.servicer = servicer;
        this.servicerId = servicerId;
        this.servicerImage = servicerImage;
        this.serviceStatus = serviceStatus;
        this.servicerComments = servicerComments;
        this.servicerSkill = servicerSkill;
    }

    public Service() {
    }

    public String getServicer() {
        return servicer;
    }

    public void setServicer(String servicer) {
        this.servicer = servicer;
    }

    public String getServicerId() {
        return servicerId;
    }

    public void setServicerId(String servicerId) {
        this.servicerId = servicerId;
    }

    public String getServicerImage() {
        return servicerImage;
    }

    public void setServicerImage(String servicerImage) {
        this.servicerImage = servicerImage;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServicerComments() {
        return servicerComments;
    }

    public void setServicerComments(String servicerComments) {
        this.servicerComments = servicerComments;
    }

    public String getServicerSkill() {
        return servicerSkill;
    }

    public void setServicerSkill(String servicerSkill) {
        this.servicerSkill = servicerSkill;
    }
}
