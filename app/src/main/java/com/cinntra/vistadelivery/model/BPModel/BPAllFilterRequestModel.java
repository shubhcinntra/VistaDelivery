package com.cinntra.vistadelivery.model.BPModel;

public class BPAllFilterRequestModel {



    public String SalesPersonCode;
    public String order_by_field;
    public String order_by_value;
    public int PageNo;
    public int maxItem;
    public String SearchText;
    public Field field = null;

    public String getSalesPersonCode() {
        return SalesPersonCode;
    }

    public void setSalesPersonCode(String salesPersonCode) {
        SalesPersonCode = salesPersonCode;
    }

    public int getPageNo() {
        return PageNo;
    }

    public void setPageNo(int pageNo) {
        PageNo = pageNo;
    }

    public int getMaxItem() {
        return maxItem;
    }

    public void setMaxItem(int maxItem) {
        this.maxItem = maxItem;
    }

    public String getSearchText() {
        return SearchText;
    }

    public void setSearchText(String searchText) {
        SearchText = searchText;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public static class Field{
        public String CardType;
        public String Industry;
        public String SalesPersonPerson;
        public String U_TYPE;
        public String SalesPersonCode;
        public String PayTermsGrpCode;


        public String getU_TYPE() {
            return U_TYPE;
        }

        public void setU_TYPE(String u_TYPE) {
            U_TYPE = u_TYPE;
        }

        public String getSalesPersonCode() {
            return SalesPersonCode;
        }

        public void setSalesPersonCode(String salesPersonCode) {
            SalesPersonCode = salesPersonCode;
        }

        public String getPayTermsGrpCode() {
            return PayTermsGrpCode;
        }

        public void setPayTermsGrpCode(String payTermsGrpCode) {
            PayTermsGrpCode = payTermsGrpCode;
        }

        public String getIndustry() {
            return Industry;
        }

        public void setIndustry(String industry) {
            Industry = industry;
        }

        public String getSalesPersonPerson() {
            return SalesPersonPerson;
        }

        public void setSalesPersonPerson(String salesPersonPerson) {
            SalesPersonPerson = salesPersonPerson;
        }

        public String getCardType() {
            return CardType;
        }

        public void setCardType(String cardType) {
            CardType = cardType;
        }
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
}
