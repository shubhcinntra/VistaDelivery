package com.cinntra.vistadelivery.model;

import java.util.ArrayList;

public class ResponseBusinessPartnerDropDown {

    public String message;
    public String status;
    public ArrayList<DataBusinessPartnerDropDown> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<DataBusinessPartnerDropDown> getData() {
        return data;
    }

    public void setData(ArrayList<DataBusinessPartnerDropDown> data) {
        this.data = data;
    }
}
