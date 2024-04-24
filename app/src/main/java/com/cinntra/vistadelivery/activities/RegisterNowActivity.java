package com.cinntra.vistadelivery.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cinntra.vistadelivery.adapters.IndustrySpinnerSaasAdapter;
import com.cinntra.vistadelivery.databinding.ActivityRegisterNowBinding;
import com.cinntra.vistadelivery.databinding.OtpVerifyDialogBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.BodyForRegisterSaas;
import com.cinntra.vistadelivery.model.ResponseIndustrySaas;
import com.cinntra.vistadelivery.superadmin.ApiClientSuperAdmin;

import androidx.appcompat.app.AppCompatActivity;


import com.cinntra.vistadelivery.R;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.aabhasjindal.otptextview.OTPListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterNowActivity extends AppCompatActivity {


    private ActivityRegisterNowBinding binding;
    AppCompatActivity act;
    String industryCode = "";
    List<ResponseIndustrySaas.Datum> IndustryItemItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterNowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        act = RegisterNowActivity.this;
        getIndustryListForSaas();
        eventManager();
        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validation()) {
                    callRegisterApi();
                }
            }
        });


    }



    private void showOTPDialog(String email) {
        OtpVerifyDialogBinding otpVerifyDialogBinding;
        Dialog dialog = new Dialog(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View custom_dialog = layoutInflater.inflate(R.layout.otp_verify_dialog, null);
        otpVerifyDialogBinding=OtpVerifyDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(otpVerifyDialogBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        otpVerifyDialogBinding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("email",email);
                jsonObject.addProperty("otp",otp);
                Call<ResponseIndustrySaas> call =  ApiClientSuperAdmin.getInstance().getApiService(RegisterNowActivity.this).verifyOtp(jsonObject);
                call.enqueue(new Callback<ResponseIndustrySaas>() {
                    @Override
                    public void onResponse(Call<ResponseIndustrySaas> call, Response<ResponseIndustrySaas> response) {
                        binding.loader.loader.setVisibility(View.GONE);
                        if (response.body().getStatus().equals("200")) {
                            binding.loginButton.setEnabled(true);
                            finish();
                            Globals.showMessage(act, "SuccessFully Created");
                            dialog.dismiss();

                        } else if (response.body().getStatus().equals("201")) {
                            Globals.showMessage(act, response.body().getMessage());

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseIndustrySaas> call, Throwable t) {
                        binding.loginButton.setEnabled(true);
                        binding.loader.loader.setVisibility(View.GONE);
                        Toasty.error(RegisterNowActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        dialog.show();
    }

    private void callRegisterApi() {
        binding.loader.loader.setVisibility(View.VISIBLE);
  /*      {
            "industry":"4",
                "customer_name":"shubh test",
                "phone_number":"9871933243",
                "email":"adam@gmail.com",
                "address":"address",
                "status":"Active",
                "payment_status":"Due",
                "username":"adam@gmail.com",
                "password":"cinn@12345",
                "application_details": [
            {
                "application":"2",
                    "license_cost":"0",
                    "active_users":2,
                    "url":"",
                    "backend_url":"",
                    "start_date":"2024-04-15",
                    "end_date":"2024-05-15",
                    "payment_frequency":"Once",
                    "subscription":"1"
            }
    ]
        }*/

        BodyForRegisterSaas bodyForRegisterSaas = new BodyForRegisterSaas();
        bodyForRegisterSaas.setIndustry(industryCode);
        bodyForRegisterSaas.setAddress("address");
        bodyForRegisterSaas.setStatus("Active");
        bodyForRegisterSaas.setPayment_status("Due");
        bodyForRegisterSaas.setUsername(binding.companyEmailValue.getText().toString().trim());
        bodyForRegisterSaas.setPassword(binding.edtPassword.getText().toString().trim());
        bodyForRegisterSaas.setCustomer_name(binding.nameValue.getText().toString().trim());
        bodyForRegisterSaas.setPhone_number(binding.companyNoValue.getText().toString().trim());
        bodyForRegisterSaas.setEmail(binding.companyEmailValue.getText().toString().trim());

        ArrayList<BodyForRegisterSaas.ApplicationDetail> applicationDetailList = new ArrayList<>();
        BodyForRegisterSaas.ApplicationDetail applicationDetail = new BodyForRegisterSaas.ApplicationDetail();
        applicationDetail.setApplication("2");
        applicationDetail.setLicense_cost("0");
        applicationDetail.setActive_users(2);
        applicationDetail.setUrl("");
        applicationDetail.setBackend_url("");
        applicationDetail.setStart_date(Globals.getTodaysDate());
        applicationDetail.setEnd_date(Globals.getTodaysDate());
        applicationDetail.setPayment_frequency("Once");
        applicationDetail.setSubscription("1");

        applicationDetailList.add(applicationDetail);
        bodyForRegisterSaas.setApplication_details(applicationDetailList);

        Call<ResponseIndustrySaas> call =  ApiClientSuperAdmin.getInstance().getApiService(this).registerCustomer(bodyForRegisterSaas);
        call.enqueue(new Callback<ResponseIndustrySaas>() {
            @Override
            public void onResponse(Call<ResponseIndustrySaas> call, Response<ResponseIndustrySaas> response) {
                binding.loader.loader.setVisibility(View.GONE);
                if (response.body().getStatus().equals("200")) {
                    binding.loginButton.setEnabled(true);

                    Globals.showMessage(act, "SuccessFully Sent Otp");
                    showOTPDialog(binding.companyEmailValue.getText().toString().trim());

                } else if (response.body().getStatus().equals("201")) {
                    Globals.showMessage(act, response.body().getMessage());

                }
            }

            @Override
            public void onFailure(Call<ResponseIndustrySaas> call, Throwable t) {
                binding.loginButton.setEnabled(true);
                binding.loader.loader.setVisibility(View.GONE);
                Toasty.error(RegisterNowActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private Boolean validation() {
        if (!binding.companyEmailValue.getText().toString().trim().isEmpty() && Globals.isvalidateemail(binding.companyEmailValue)) {
            binding.companyEmailValue.requestFocus();
            Globals.showMessage(act, "Enter Valid Email");
            return false;
        } else if (binding.companyNoValue.getText().toString().trim().isEmpty()) {
            Globals.showMessage(act, "Enter Company Contact No.");
            return false;
        } else if (industryCode.isEmpty()) {
            Globals.showMessage(act, "Select Industry Code");
            return false;
        } else if (binding.edtPassword.getText().toString().trim().isEmpty()) {
            Globals.showMessage(act, "Enter Password");
            return false;
        } else if (binding.nameValue.getText().toString().trim().isEmpty()) {
            Globals.showMessage(act, "Enter Company Name");

            return false;
        } else {
            return true;
        }
    }

    private void eventManager() {
        binding.industrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (IndustryItemItemList.size() > 0)
                    industryCode = IndustryItemItemList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (IndustryItemItemList.size() > 0)
                    industryCode = IndustryItemItemList.get(0).getId();
            }
        });
    }


    private void getIndustryListForSaas() {
        Call<ResponseIndustrySaas> call =  ApiClientSuperAdmin.getInstance().getApiService(this).getIndustrySaas();
        call.enqueue(new Callback<ResponseIndustrySaas>() {
            @Override
            public void onResponse(Call<ResponseIndustrySaas> call, Response<ResponseIndustrySaas> response) {
                binding.loader.loader.setVisibility(View.GONE);
                if (response.body().getStatus().equals("200")) {
                    IndustryItemItemList = response.body().getData();
                    binding.industrySpinner.setAdapter(new IndustrySpinnerSaasAdapter(RegisterNowActivity.this, IndustryItemItemList));
                    industryCode = IndustryItemItemList.get(0).getId();

                } else {


                }
            }

            @Override
            public void onFailure(Call<ResponseIndustrySaas> call, Throwable t) {
                binding.loader.loader.setVisibility(View.GONE);
                Toasty.error(RegisterNowActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}