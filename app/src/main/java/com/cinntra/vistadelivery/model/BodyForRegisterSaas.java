package com.cinntra.vistadelivery.model;

import java.util.ArrayList;

public class BodyForRegisterSaas {
    public String industry;
    public String customer_name;
    public String phone_number;
    public String email;
    public String address;
    public String status;
    public String payment_status;
    public String username;
    public String password;
    public ArrayList<ApplicationDetail> application_details;
    public static class ApplicationDetail{
        public String application;
        public String license_cost;
        public int active_users;
        public String url;
        public String backend_url;
        public String start_date;
        public String end_date;
        public String payment_frequency;
        public String subscription;

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }

        public String getLicense_cost() {
            return license_cost;
        }

        public void setLicense_cost(String license_cost) {
            this.license_cost = license_cost;
        }

        public int getActive_users() {
            return active_users;
        }

        public void setActive_users(int active_users) {
            this.active_users = active_users;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBackend_url() {
            return backend_url;
        }

        public void setBackend_url(String backend_url) {
            this.backend_url = backend_url;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getPayment_frequency() {
            return payment_frequency;
        }

        public void setPayment_frequency(String payment_frequency) {
            this.payment_frequency = payment_frequency;
        }

        public String getSubscription() {
            return subscription;
        }

        public void setSubscription(String subscription) {
            this.subscription = subscription;
        }
    }


    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<ApplicationDetail> getApplication_details() {
        return application_details;
    }

    public void setApplication_details(ArrayList<ApplicationDetail> application_details) {
        this.application_details = application_details;
    }
}
