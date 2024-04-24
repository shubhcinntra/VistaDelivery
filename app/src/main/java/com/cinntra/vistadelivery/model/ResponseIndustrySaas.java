package com.cinntra.vistadelivery.model;

import java.util.ArrayList;

public class ResponseIndustrySaas {

    public String message;
    public String status;
    public ArrayList<Datum> data;
    public String errors;

    public  class  Datum{
        public String id;
        public String industry_name;
        public String industry_code;
        public String industry_desc;
        public String create_at;
        public String update_at;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIndustry_name() {
            return industry_name;
        }

        public void setIndustry_name(String industry_name) {
            this.industry_name = industry_name;
        }

        public String getIndustry_code() {
            return industry_code;
        }

        public void setIndustry_code(String industry_code) {
            this.industry_code = industry_code;
        }

        public String getIndustry_desc() {
            return industry_desc;
        }

        public void setIndustry_desc(String industry_desc) {
            this.industry_desc = industry_desc;
        }

        public String getCreate_at() {
            return create_at;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }

        public String getUpdate_at() {
            return update_at;
        }

        public void setUpdate_at(String update_at) {
            this.update_at = update_at;
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

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }


}
