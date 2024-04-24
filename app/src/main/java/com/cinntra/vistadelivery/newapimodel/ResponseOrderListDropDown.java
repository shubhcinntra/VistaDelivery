package com.cinntra.vistadelivery.newapimodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseOrderListDropDown {
    public String message;
    public String status;
    public ArrayList<Datum> data;


    public class Datum{
        public String id;
        @SerializedName("CardName")
        public String cardName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
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
