package com.cinntra.vistadelivery.model;

import com.google.gson.annotations.SerializedName;

public class DataBusinessType {

    public String id;
    @SerializedName("Type")
    public String type;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
