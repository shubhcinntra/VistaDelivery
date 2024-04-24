package com.cinntra.vistadelivery.model.OpportunityModels;

public class OpportunityAllListRequest {

    public OpportunityAllListRequest.Field Field;
    private String SalesPersonCode;
    private int PageNo;
    private int maxItem;
    private String SearchText;

    public String order_by_field;
    public String order_by_value;
    private Field field = new Field();


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


   /* {
        "StartDate": "2024-03-06",
            "CardCode": "C93",
            "U_LSOURCE": "YouTube Interview",
            "U_TYPE": "6"
    }*/

    public static class Field {
        private String FromDate;
        //todo new key


        private String StartDate;
        private String U_LSOURCE;
        private String U_TYPE;


        private String ToDate;
        private String CardCode;
        private String CardName;
        private String Status;
        private String oppotype;
        private String source;


        public String getStartDate() {
            return StartDate;
        }

        public void setStartDate(String startDate) {
            StartDate = startDate;
        }

        public String getU_LSOURCE() {
            return U_LSOURCE;
        }

        public void setU_LSOURCE(String u_LSOURCE) {
            U_LSOURCE = u_LSOURCE;
        }

        public String getU_TYPE() {
            return U_TYPE;
        }

        public void setU_TYPE(String u_TYPE) {
            U_TYPE = u_TYPE;
        }

        public String getOppotype() {
            return oppotype;
        }

        public void setOppotype(String oppotype) {
            this.oppotype = oppotype;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getFromDate() {
            return FromDate;
        }

        public void setFromDate(String fromDate) {
            FromDate = fromDate;
        }

        public String getToDate() {
            return ToDate;
        }

        public void setToDate(String toDate) {
            ToDate = toDate;
        }

        public String getCardCode() {
            return CardCode;
        }

        public void setCardCode(String cardCode) {
            CardCode = cardCode;
        }

        public String getCardName() {
            return CardName;
        }

        public void setCardName(String cardName) {
            CardName = cardName;
        }


        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }
    }


}
