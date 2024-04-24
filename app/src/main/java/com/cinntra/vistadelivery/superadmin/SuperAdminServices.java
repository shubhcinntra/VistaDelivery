package com.cinntra.vistadelivery.superadmin;

import com.cinntra.vistadelivery.model.BodyForRegisterSaas;
import com.cinntra.vistadelivery.model.ResponseIndustrySaas;
import com.cinntra.vistadelivery.superadmin.response.ResponseSuperAdmin;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SuperAdminServices {

    @POST("api/user/login")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseSuperAdmin> loginToken(@Body JsonObject jsonObject);


    @GET("api/industries/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseIndustrySaas> getIndustrySaas();

    @POST("api/customer/register")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseIndustrySaas> registerCustomer(@Body BodyForRegisterSaas bodyForRegisterSaas);

    @POST("api/customer/verify_otp")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseIndustrySaas> verifyOtp(@Body JsonObject bodyForRegisterSaas);

}
