package com.cinntra.vistadelivery.model;

import com.google.gson.annotations.SerializedName;

public class DataDropDownZone {
    public String id;
    @SerializedName("Name")
    public String name;
    @SerializedName("Status")
    public String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
