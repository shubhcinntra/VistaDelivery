package com.cinntra.vistadelivery.model;


import com.google.gson.annotations.SerializedName;


public class StagesValue
{
    public int id;
    @SerializedName("Opp_Id")
    public String opp_Id;
    @SerializedName("Stageno")
    public String stageno;
    @SerializedName("SequenceNo")
    public String sequenceNo;
    @SerializedName("Name")
    public String name;
    @SerializedName("DocId")
    public String docId;
    @SerializedName("Comment")
    public String comment;
    @SerializedName("File")
    public String file;
    @SerializedName("ClosingPercentage")
    public String closingPercentage;
    @SerializedName("Cancelled")
    public String cancelled;
    @SerializedName("IsSales")
    public String isSales;
    @SerializedName("IsPurchasing")
    public String isPurchasing;
    @SerializedName("Status")
    public int status;
    @SerializedName("StartDate")
    public String startDate;
    @SerializedName("EndDate")
    public String endDate;
    @SerializedName("CreateDate")
    public String createDate;
    @SerializedName("UpdateDate")
    public String updateDate;

    @SerializedName("Color")
    public String color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpp_Id() {
        return opp_Id;
    }

    public void setOpp_Id(String opp_Id) {
        this.opp_Id = opp_Id;
    }

    public String getStageno() {
        return stageno;
    }

    public void setStageno(String stageno) {
        this.stageno = stageno;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getClosingPercentage() {
        return closingPercentage;
    }

    public void setClosingPercentage(String closingPercentage) {
        this.closingPercentage = closingPercentage;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getIsSales() {
        return isSales;
    }

    public void setIsSales(String isSales) {
        this.isSales = isSales;
    }

    public String getIsPurchasing() {
        return isPurchasing;
    }

    public void setIsPurchasing(String isPurchasing) {
        this.isPurchasing = isPurchasing;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
