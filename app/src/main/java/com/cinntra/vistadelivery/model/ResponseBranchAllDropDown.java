package com.cinntra.vistadelivery.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseBranchAllDropDown {

    public String message;
    public int status;
    public ArrayList<Datum> data;



    public class  Datum {
        public String id;
        @SerializedName("BPID")
        public String bPID;
        @SerializedName("BPCode")
        public String bPCode;
        @SerializedName("RowNum")
        public String rowNum;
        @SerializedName("AddressType")
        public String addressType;
        @SerializedName("AddressName")
        public String addressName;
        @SerializedName("AddressName2")
        public String addressName2;
        @SerializedName("AddressName3")
        public String addressName3;
        @SerializedName("BuildingFloorRoom")
        public String buildingFloorRoom;
        @SerializedName("Street")
        public String street;
        @SerializedName("Block")
        public String block;
        @SerializedName("ZipCode")
        public String zipCode;
        @SerializedName("City")
        public String city;
        @SerializedName("County")
        public String county;
        @SerializedName("Country")
        public String country;
        @SerializedName("State")
        public String state;
        @SerializedName("TaxCode")
        public String taxCode;
        @SerializedName("StreetNo")
        public String streetNo;
        @SerializedName("GSTIN")
        public String gSTIN;
        @SerializedName("Active")
        public String active;
        @SerializedName("CreateDate")
        public String createDate;
        @SerializedName("CreateTime")
        public String createTime;
        @SerializedName("UpdateDate")
        public String updateDate;
        @SerializedName("UpdateTime")
        public String updateTime;
        @SerializedName("U_LAT")
        public String u_LAT;
        @SerializedName("U_LONG")
        public String u_LONG;
        @SerializedName("U_SHPTYP")
        public String u_SHPTYP;
        @SerializedName("U_COUNTRY")
        public String u_COUNTRY;
        @SerializedName("U_STATE")
        public String u_STATE;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getbPID() {
            return bPID;
        }

        public void setbPID(String bPID) {
            this.bPID = bPID;
        }

        public String getbPCode() {
            return bPCode;
        }

        public void setbPCode(String bPCode) {
            this.bPCode = bPCode;
        }

        public String getRowNum() {
            return rowNum;
        }

        public void setRowNum(String rowNum) {
            this.rowNum = rowNum;
        }

        public String getAddressType() {
            return addressType;
        }

        public void setAddressType(String addressType) {
            this.addressType = addressType;
        }

        public String getAddressName() {
            return addressName;
        }

        public void setAddressName(String addressName) {
            this.addressName = addressName;
        }

        public String getAddressName2() {
            return addressName2;
        }

        public void setAddressName2(String addressName2) {
            this.addressName2 = addressName2;
        }

        public String getAddressName3() {
            return addressName3;
        }

        public void setAddressName3(String addressName3) {
            this.addressName3 = addressName3;
        }

        public String getBuildingFloorRoom() {
            return buildingFloorRoom;
        }

        public void setBuildingFloorRoom(String buildingFloorRoom) {
            this.buildingFloorRoom = buildingFloorRoom;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getBlock() {
            return block;
        }

        public void setBlock(String block) {
            this.block = block;
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

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTaxCode() {
            return taxCode;
        }

        public void setTaxCode(String taxCode) {
            this.taxCode = taxCode;
        }

        public String getStreetNo() {
            return streetNo;
        }

        public void setStreetNo(String streetNo) {
            this.streetNo = streetNo;
        }

        public String getgSTIN() {
            return gSTIN;
        }

        public void setgSTIN(String gSTIN) {
            this.gSTIN = gSTIN;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getU_LAT() {
            return u_LAT;
        }

        public void setU_LAT(String u_LAT) {
            this.u_LAT = u_LAT;
        }

        public String getU_LONG() {
            return u_LONG;
        }

        public void setU_LONG(String u_LONG) {
            this.u_LONG = u_LONG;
        }

        public String getU_SHPTYP() {
            return u_SHPTYP;
        }

        public void setU_SHPTYP(String u_SHPTYP) {
            this.u_SHPTYP = u_SHPTYP;
        }

        public String getU_COUNTRY() {
            return u_COUNTRY;
        }

        public void setU_COUNTRY(String u_COUNTRY) {
            this.u_COUNTRY = u_COUNTRY;
        }

        public String getU_STATE() {
            return u_STATE;
        }

        public void setU_STATE(String u_STATE) {
            this.u_STATE = u_STATE;
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
