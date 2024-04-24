package com.cinntra.vistadelivery.model;

import com.google.gson.annotations.SerializedName;

public class DataBusinessPartnerDropDown {

    public String id;
    @SerializedName("CardCode")
    public String cardCode;
    @SerializedName("CardName")
    public String cardName;
    @SerializedName("Industry")
    public String industry;
    @SerializedName("CardType")
    public String cardType;
    @SerializedName("SalesPersonCode")
    public String salesPersonCode;
    @SerializedName("U_CONTOWNR")
    public String u_CONTOWNR;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getSalesPersonCode() {
        return salesPersonCode;
    }

    public void setSalesPersonCode(String salesPersonCode) {
        this.salesPersonCode = salesPersonCode;
    }

    public String getU_CONTOWNR() {
        return u_CONTOWNR;
    }

    public void setU_CONTOWNR(String u_CONTOWNR) {
        this.u_CONTOWNR = u_CONTOWNR;
    }
}
