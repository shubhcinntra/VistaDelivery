package com.cinntra.vistadelivery.modelfilter;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

public class FilterOverAll {

    @Nullable
    public FieldFilter field;
    public String leadType;
    public String maxItem;
    public String order_by_field;
    public String order_by_value;
    @SerializedName("PageNo")
    public int pageNo;
    @SerializedName("SalesPersonCode")
    public String salesPersonCode;
    @SerializedName("SearchText")
    public String searchText;

    public FieldFilter getField() {
        return field;
    }

    public void setField(FieldFilter field) {
        this.field = field;
    }

    public String getLeadType() {
        return leadType;
    }

    public void setLeadType(String leadType) {
        this.leadType = leadType;
    }

    public String getMaxItem() {
        return maxItem;
    }

    public void setMaxItem(String maxItem) {
        this.maxItem = maxItem;
    }

    public String getOrder_by_field() {
        return order_by_field;
    }

    public void setOrder_by_field(String order_by_field) {
        this.order_by_field = order_by_field;
    }

    public String getOrder_by_value() {
        return order_by_value;
    }

    public void setOrder_by_value(String order_by_value) {
        this.order_by_value = order_by_value;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getSalesPersonCode() {
        return salesPersonCode;
    }

    public void setSalesPersonCode(String salesPersonCode) {
        this.salesPersonCode = salesPersonCode;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
