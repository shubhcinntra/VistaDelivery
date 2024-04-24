package com.cinntra.vistadelivery.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseCompanyBranchAllFilter implements Serializable {
    public String message;
    public int status;
    public ArrayList<Datum> data;



    public class Datum implements Serializable {
        public int id;
        @SerializedName("BPLId")
        public String bPLId;
        @SerializedName("BPLName")
        public String bPLName;
        @SerializedName("Address")
        public String address;
        @SerializedName("StreetNo")
        public String streetNo;
        @SerializedName("Building")
        public String building;
        @SerializedName("ZipCode")
        public String zipCode;
        @SerializedName("City")
        public String city;
        @SerializedName("State")
        public String state;
        @SerializedName("Country")
        public String country;
        @SerializedName("FederalTaxID")
        public String federalTaxID;
        @SerializedName("MainBPL")
        public String mainBPL;
        @SerializedName("Disabled")
        public String disabled;
        @SerializedName("Created_at")
        public String created_at;
        @SerializedName("Updated_at")
        public String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getbPLId() {
            return bPLId;
        }

        public void setbPLId(String bPLId) {
            this.bPLId = bPLId;
        }

        public String getbPLName() {
            return bPLName;
        }

        public void setbPLName(String bPLName) {
            this.bPLName = bPLName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStreetNo() {
            return streetNo;
        }

        public void setStreetNo(String streetNo) {
            this.streetNo = streetNo;
        }

        public String getBuilding() {
            return building;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getFederalTaxID() {
            return federalTaxID;
        }

        public void setFederalTaxID(String federalTaxID) {
            this.federalTaxID = federalTaxID;
        }

        public String getMainBPL() {
            return mainBPL;
        }

        public void setMainBPL(String mainBPL) {
            this.mainBPL = mainBPL;
        }

        public String getDisabled() {
            return disabled;
        }

        public void setDisabled(String disabled) {
            this.disabled = disabled;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }
}
