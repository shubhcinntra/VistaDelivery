package com.cinntra.vistadelivery.newapimodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseQuoteListDropDown {
    public String message;
    public String status;
    public ArrayList<Datum> data;


    public class Datum{
        public String id;
        @SerializedName("U_QUOTNM")
        public String u_QUOTNM;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getU_QUOTNM() {
            return u_QUOTNM;
        }

        public void setU_QUOTNM(String u_QUOTNM) {
            this.u_QUOTNM = u_QUOTNM;
        }
    }

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

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }
}
