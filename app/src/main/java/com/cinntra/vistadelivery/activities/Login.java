package com.cinntra.vistadelivery.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.databinding.ActivityLoginBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.globals.MyApp;
import com.cinntra.vistadelivery.interfaces.DatabaseClick;

import com.cinntra.vistadelivery.model.LogInDetail;
import com.cinntra.vistadelivery.model.LogInRequest;
import com.cinntra.vistadelivery.model.LogInResponse;
import com.cinntra.vistadelivery.model.NewLogINResponse;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.TokenResponseModel;
import com.cinntra.vistadelivery.superadmin.ApiClientSuperAdmin;
import com.cinntra.vistadelivery.superadmin.response.ResponseSuperAdmin;
import com.cinntra.vistadelivery.webservices.APIsClient;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.HashMap;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener, DatabaseClick {
//    private Button signin;
//    @BindView(R.id.progressBar1)
//    ProgressBar progressBar;
//    @BindView(R.id.goto_reg)
//    LinearLayout goto_reg;
//    @BindView(R.id.sql_setting)
//    RelativeLayout sql_setting;
//    @BindView(R.id.login_username)
//    EditText login_username;
//    @BindView(R.id.login_password)
//    EditText login_password;
//    @BindView(R.id.register_here)
//    TextView register_here;


    private AppCompatActivity activity;
    private String token = "";


    ActivityLoginBinding binding;

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MyApp.timer != null) {
            MyApp.timer.cancel();
            MyApp.timer = null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        activity = Login.this;
        setContentView(binding.getRoot());
        // ButterKnife.bind(this);

        overridePendingTransition(0, 0);
        View relativeLayout = findViewById(R.id.login_container);
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        relativeLayout.startAnimation(animation);

     /*   binding.registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });*/


        //todo commented tokenapi
     //   getTokenAPi();

        FirebaseMessaging messaging = FirebaseMessaging.getInstance();
        messaging.getToken().addOnSuccessListener(s -> {

            if (Prefs.getString(Globals.TOKENFireBase, "").isEmpty()) {
                token = s;
                Prefs.putString(Globals.TOKENFireBase, s);
            }
        });


        binding.progressBar1.setVisibility(View.GONE);
        // signin =findViewById(R.id.login_button);
        binding.loginButton.setOnClickListener(this);
        binding.gotoReg.setOnClickListener(this);
        binding.sqlSetting.setOnClickListener(this);
        binding.registerHere.setOnClickListener(this);

        if (Prefs.getString(Globals.REMEMBER_ME, "").equalsIgnoreCase("rem")) {
            binding.loginUsername.setText(Prefs.getString(Globals.USERNAME, ""));
            binding.loginPassword.setText(Prefs.getString(Globals.USER_PASSWORD, ""));
            binding.rememberme.setChecked(true);
        } else {
            binding.loginUsername.setText("");
            binding.loginPassword.setText("");
            binding.rememberme.setChecked(false);
        }


        //todo remove from live
        binding.ivlogomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   startActivity(new Intent(Login.this, TestBlankActivity.class));
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:


                if (Globals.checkInternet(this)) {

                    if (validation(binding.loginUsername.getText().toString().trim(), binding.loginPassword.getText().toString().trim())) {

                        Globals.APILog = "APILog";

                        Prefs.putString(Globals.SelectedBranch, "");
                        Prefs.putString(Globals.SelectedBranchID, "");
                        Prefs.putString(Globals.SelectedWareHose, "");
                        Prefs.putString(Globals.SessionID, "");
                        Prefs.putString(Globals.USERNAME, binding.loginUsername.getText().toString().trim());
                        Prefs.putString(Globals.USER_PASSWORD, binding.loginPassword.getText().toString().trim());
                        getTokenAPi();

                        //loginUser(Globals.SelectedDB,login_username.getText().toString().trim(),login_password.getText().toString().trim());
                        //    sessionloginApi();



//             callLogInApi(login_username.getText().toString().trim(),login_password.getText().toString().trim());


                    }


                }

                break;
            case R.id.register_here:
              //  startActivity(new Intent(this, SignUp.class));
                Intent intentnew=new Intent(Login.this,RegisterNowActivity.class);
                startActivity(intentnew);
                break;

       /* case R.id.goto_reg:
            startActivity(new Intent(this,SignUp.class));
            break;*/
            case R.id.sql_setting:
           /* Intent intent=new Intent(this,SqlSetting.class);
            startActivityForResult(intent, 2);*/

                Intent intent = new Intent(this, DemoActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void sessionloginApi() {
        binding.progressBar1.setVisibility(View.VISIBLE);
        HashMap<String, String> session = new HashMap<>();
        session.put("username", "root");
        session.put("password", "Sunil@123");


        Call<NewLogINResponse> call = NewApiClient.getInstance().getApiService(this).sessionlogin(session);
        call.enqueue(new Callback<NewLogINResponse>() {
            @Override
            public void onResponse(Call<NewLogINResponse> call, Response<NewLogINResponse> response) {

                Globals.APILog = "Not";
                Prefs.putBoolean(Globals.AutoLogIn, true);
                Prefs.putString(Globals.SessionID, response.body().getToken());
                Log.e("DETAILS>>>", "onResponse: " + binding.loginUsername.getText().toString().trim() + binding.loginPassword.getText().toString().trim());
                callLogInApi(binding.loginUsername.getText().toString().trim(), binding.loginPassword.getText().toString().trim());


            }

            @Override
            public void onFailure(Call<NewLogINResponse> call, Throwable t) {
                binding.progressBar1.setVisibility(View.GONE);

            }
        });
    }

    private void callLogInApi(String username, String password) {

        LogInDetail logInDetail = new LogInDetail();
        logInDetail.setUserName(username);
        logInDetail.setPassword(password);
        logInDetail.setFcm(token);
        Prefs.putString(Globals.USER_PASSWORD, password);

        Call<NewLogINResponse> call = NewApiClient.getInstance().getApiService(this).loginEmployee(logInDetail);
        call.enqueue(new Callback<NewLogINResponse>() {
            @Override
            public void onResponse(Call<NewLogINResponse> call, Response<NewLogINResponse> response) {

                if (response.body().getStatus() == 200) {
                    binding.progressBar1.setVisibility(View.GONE);

                    if (binding.rememberme.isChecked()) {
                        Prefs.putString(Globals.REMEMBER_ME, "rem");
                    }

                    if (response.body().getLogInDetail()!=null){
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getLogInDetail());
                        Prefs.putString(Globals.AppUserDetails, json);
                        Prefs.putBoolean(Globals.AutoLogIn, true);
                        //   Globals.APILog = "APILog";
                        Globals.TeamSalesEmployeCode = response.body().getLogInDetail().getSalesEmployeeCode();
                        Globals.TeamRole = response.body().getLogInDetail().getRole();
                        Globals.TeamEmployeeID = String.valueOf(response.body().getLogInDetail().getId());
                        Globals.MYEmployeeID = String.valueOf(response.body().getLogInDetail().getId());
                        //Globals.SelectedDB = String.valueOf(response.body().getSap().getCompanyDB());
                        Prefs.putString(Globals.Employee_Name, response.body().getLogInDetail().getUserName());
                        Prefs.putString(Globals.USER_NAME, response.body().getLogInDetail().getEmail());

                        Prefs.putString(Globals.Employee_Code, response.body().getLogInDetail().getSalesEmployeeCode());
                        Prefs.putString(Globals.EmployeeID, String.valueOf(response.body().getLogInDetail().getId()));
                        Prefs.putString(Globals.SalesEmployeeCode, String.valueOf(response.body().getLogInDetail().getSalesEmployeeCode()));
                        Prefs.putString(Globals.SalesEmployeeName, String.valueOf(response.body().getLogInDetail().getSalesEmployeeName()));
//                        Prefs.putString(Globals.SelectedDB, String.valueOf(response.body().getSap().getCompanyDB()));
                        Prefs.putString(Globals.Role, String.valueOf(response.body().getLogInDetail().getRole()));
                        Prefs.putString(Globals.Position, String.valueOf(response.body().getLogInDetail().getPosition()));


                        Prefs.putBoolean(Globals.Location_FirstTime, true);

                        long session = Long.parseLong("30");
                        session = session * 60 * 1000;

                        Prefs.putLong(Globals.SESSION_TIMEOUT, session);
                        Prefs.putLong(Globals.SESSION_REMAIN_TIME, 0);
                            /* LogInRequest in = new LogInRequest();
                            in.setCompanyDB(response.body().getSap().getCompanyDB());  //HANA
                            in.setPassword(response.body().getSap().getPassword());//"manager"//8097
                            in.setUserName(response.body().getSap().getUserName());//"manager"
                             userLogin(in);*/
                        gotoHome();
                    }else {
                        Toast.makeText(Login.this, "Empty Object", Toast.LENGTH_SHORT).show();
                    }




                } else if (response.body().getStatus()==201) {
                    binding.progressBar1.setVisibility(View.GONE);
                    Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    binding.progressBar1.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Check Login Credentials.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<NewLogINResponse> call, Throwable t) {
                binding.progressBar1.setVisibility(View.GONE);
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    //todo token api
    private void getTokenAPi() {
        binding.progressBar1.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
    /*    jsonObject.addProperty("email", binding.loginUsername.getText().toString().trim());
        jsonObject.addProperty("password", binding.loginPassword.getText().toString().trim());
        jsonObject.addProperty("FCM","");
        jsonObject.addProperty("app_id","2");*/

        jsonObject.addProperty("username", "root");
        jsonObject.addProperty("password", "Sunil@123");

      /*  {
            "email": "shubham.bairwa@cinntra.com",
                "password": "cinn@12345",
                "FCM": "",
                "app_id": "2"
        }*/




        Call<TokenResponseModel> call = NewApiClient.getInstance().getApiService(this).loginToken(jsonObject);
        call.enqueue(new Callback<TokenResponseModel>() {
            @Override
            public void onResponse(Call<TokenResponseModel> call, Response<TokenResponseModel> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()){
                        if (!response.body().getToken().isEmpty()) {
                            Log.e("TAG_Token", "onResponse: " + response.body().getToken());
                            Prefs.putString(Globals.Token, "Token " + response.body().getToken());
                            Prefs.putString(Globals.token, response.body().getToken());
                            callLogInApi(binding.loginUsername.getText().toString().trim(), binding.loginPassword.getText().toString().trim());
                        }
                    }else {
                        binding.progressBar1.setVisibility(View.GONE);

                        Toast.makeText(Login.this, response.message(), Toast.LENGTH_SHORT).show();

                    }


                }
            }

            @Override
            public void onFailure(Call<TokenResponseModel> call, Throwable t) {
                binding.progressBar1.setVisibility(View.GONE);

                Log.e("FailureAPi ===> ", "onFailure: " + t.getMessage());
            }
        });

    }


    private void userLogin(LogInRequest in) {

        Call<LogInResponse> call = APIsClient.getInstance().getApiService().LogIn(in);
        call.enqueue(new Callback<LogInResponse>() {
            @Override
            public void onResponse(Call<LogInResponse> call, Response<LogInResponse> response) {
                binding.progressBar1.setVisibility(View.GONE);
                if (response.code() == 200) {
                    Prefs.putString(Globals.USER_TYPE, "manager");
                    Prefs.putString(Globals.SessionID, response.body().getSessionId());
                    long session = Long.parseLong(response.body().getSessionTimeout());
                    session = session * 60 * 1000;

                    Prefs.putLong(Globals.SESSION_TIMEOUT, session);
                    Prefs.putLong(Globals.SESSION_REMAIN_TIME, 0);

                    // LoginHierarchy2ndLevel("manager");
                    gotoHome();
                } else {
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(Login.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                    }
                }

            }

            @Override
            public void onFailure(Call<LogInResponse> call, Throwable t) {
                binding.progressBar1.setVisibility(View.GONE);
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void gotoHome() {
        Intent i = new Intent(Login.this, MainActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        finish();
    }


    private boolean validation(String user, String pass) {
        if (user.isEmpty()) {
            Toast.makeText(activity, getResources().getString(R.string.enter_user), Toast.LENGTH_SHORT).show();
            return false;
        } else if (pass.isEmpty()) {
            Toast.makeText(activity, getResources().getString(R.string.enter_sql_pass), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    /******************** Rest Client ***********************/


    /************ DataBase Alert *****************/


    Dialog dialog;


    @Override
    public void onClick(int po) {


        Prefs.putString(Globals.SessionID, "");
        Globals.APILog = "APILog";
        callLogInApi(binding.loginUsername.getText().toString().trim(), binding.loginPassword.getText().toString().trim());

        if (dialog != null)
            dialog.dismiss();
    }


}