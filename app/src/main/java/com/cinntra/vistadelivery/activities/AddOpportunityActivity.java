package com.cinntra.vistadelivery.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.adapters.BPTypeSpinnerAdapter;
import com.cinntra.vistadelivery.adapters.CategoryAdapter;
import com.cinntra.vistadelivery.adapters.LeadTypeAdapter;
import com.cinntra.vistadelivery.adapters.SalesEmployeeAutoAdapter;
import com.cinntra.vistadelivery.adapters.bpAdapters.ContactPersonAutoAdapter;
import com.cinntra.vistadelivery.databinding.AddOpportunityBinding;
import com.cinntra.vistadelivery.databinding.TaxesAlertBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.globals.MainBaseActivity;
import com.cinntra.vistadelivery.interfaces.DatabaseClick;
import com.cinntra.vistadelivery.interfaces.FragmentRefresher;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.cinntra.vistadelivery.model.ContactPerson;
import com.cinntra.vistadelivery.model.ContactPersonData;
import com.cinntra.vistadelivery.model.ItemCategoryData;
import com.cinntra.vistadelivery.model.ItemCategoryResponse;
import com.cinntra.vistadelivery.model.LeadTypeData;
import com.cinntra.vistadelivery.model.LeadTypeResponse;
import com.cinntra.vistadelivery.model.NewOppResponse;
import com.cinntra.vistadelivery.model.OwnerItem;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.SalesEmployeeItem;
import com.cinntra.vistadelivery.model.SalesOpportunitiesLines;
import com.cinntra.vistadelivery.model.UTypeData;
import com.cinntra.vistadelivery.newapimodel.AddOpportunityModel;
import com.cinntra.vistadelivery.newapimodel.LeadResponse;
import com.cinntra.vistadelivery.newapimodel.LeadValue;
import com.cinntra.vistadelivery.viewModel.ItemViewModel;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddOpportunityActivity extends MainBaseActivity implements View.OnClickListener, DatabaseClick , FragmentRefresher {

    public static int PARTNERCODE = 100;
    public static int OWNERCODE = 1001;
    public static int LeadCode = 101;
    public int ITEMSVIEWCODE = 10000;

    Activity act;
    int salesEmployeeCode = 0;
    String salesEmployeename = "";
    String ContactPersonName = "";
    String ContactPersonCode = "";

    String stagesCode = "No";
    String TYPE = "";
    String LEAD_SOURCE = "";

    String DataOwnershipfield = "";
    String LeadID = "0";
    String CardName = "";
    AddOpportunityBinding binding;
    String  BPCardCode = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddOpportunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Globals.IsQUOTEORDER=false;

        binding.loader.loader.setVisibility(View.GONE);

        act = AddOpportunityActivity.this;
        Globals.SelectedItems.clear();

        if (getIntent() != null) {
            BPCardCode  = getIntent().getStringExtra("BPCardCodeShortCut");
            // Use the passed data as needed
        }

        setDefaults();
        eventManager();





        if (Globals.checkInternet(this)) {
            callSourceApi();


            //callUTypeApi();

            try {
                if (BPCardCode != null){
                    callBPOneAPi(BPCardCode);
                }
                else {
                    callSalessApi();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            callDepartmentApi(); //todo comment by me
        }

      /*  if (Globals.checkInternet(this)) {
            callSalessApi();
            callSourceApi();

        }*/

        binding.itemFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Globals.SelectedItems.size() == 0) {
//                    Globals.ItemType = "Paid";
                    openCategorydailog();
                   /* Intent intent = new Intent(AddOpportunityActivity.this, ItemsList.class);
                    intent.putExtra("CategoryID", 0);
                    startActivityForResult(intent, ITEMSVIEWCODE);*/
                } else {
                    Intent intent = new Intent(AddOpportunityActivity.this, SelectedItems.class);
                    intent.putExtra("FromWhere", "addQt");
                    startActivityForResult(intent, ITEMSVIEWCODE);
                }
            }
        });


    }


    List<String> probability_list = Arrays.asList(Globals.probability_list);

    //todo event listeners---
    private void eventManager() {


        binding.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    salesEmployeeCode = Integer.parseInt(salesEmployeeItemList.get(position).getSalesEmployeeCode());
                    salesEmployeename = salesEmployeeItemList.get(position).getSalesEmployeeName();

                    binding.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                } else {
                    salesEmployeeCode = 0;
                    salesEmployeename = "";

                    binding.acSalesEmployee.setText("");
                }
            }
        });


        binding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (utypelist.size() > 0)
                    TYPE = utypelist.get(position).getId().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.leadSourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sourceData.size() > 0) {
                    LEAD_SOURCE = sourceData.get(position).getName().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LEAD_SOURCE = sourceData.get(0).getName().toString();
            }
        });


        //todo item click of contact person---

        binding.acContactPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContactEmployeesList.size() > 0) {
                    ContactPersonName = ContactEmployeesList.get(position).getFirstName();
                    ContactPersonCode = ContactEmployeesList.get(position).getInternalCode();

                    binding.acContactPerson.setText(ContactEmployeesList.get(position).getFirstName());
                }
            }
        });


        //todo bind probability adapter---
        ArrayAdapter<String> probailityAdapter = new ArrayAdapter<>(AddOpportunityActivity.this, R.layout.drop_down_textview, probability_list);
        binding.acProbability.setAdapter(probailityAdapter);


        binding.acProbability.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.acProbability.setText(probability_list.get(position));

                ArrayAdapter<String> probailityAdapter = new ArrayAdapter<>(AddOpportunityActivity.this, R.layout.drop_down_textview, probability_list);
                binding.acProbability.setAdapter(probailityAdapter);
            }
        });


    }


    Dialog TaxListdialog;

    private void openCategorydailog() {
        RelativeLayout backPress;
        TextView head_title;
        RecyclerView recyclerview;
//        ProgressBar loader;
        SpinKitView loader;

        TaxListdialog = new Dialog(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View custom_dialog = layoutInflater.inflate(R.layout.taxes_alert, null);
        recyclerview = custom_dialog.findViewById(R.id.recyclerview);
        backPress = custom_dialog.findViewById(R.id.back_press);
        head_title = custom_dialog.findViewById(R.id.head_title);
        loader = custom_dialog.findViewById(R.id.loader);
        head_title.setText(getResources().getString(R.string.select_tax));
        TaxListdialog.setContentView(custom_dialog);
        TaxListdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TaxListdialog.show();

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaxListdialog.dismiss();
            }
        });

        loader.setVisibility(View.GONE);

        Call<ItemCategoryResponse> call = NewApiClient.getInstance().getApiService(this).getAllCategory();
        call.enqueue(new Callback<ItemCategoryResponse>() {
            @Override
            public void onResponse(Call<ItemCategoryResponse> call, Response<ItemCategoryResponse> response) {
                loader.setVisibility(View.GONE);
                if (response.body().getStatus() == 200) {
                    List<ItemCategoryData> itemsList = response.body().getData();

                    CategoryAdapter adapter = new CategoryAdapter(AddOpportunityActivity.this, itemsList, TaxListdialog);
                    recyclerview.setLayoutManager(new LinearLayoutManager(AddOpportunityActivity.this, RecyclerView.VERTICAL, false));
                    recyclerview.setAdapter(adapter);
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(AddOpportunityActivity.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ItemCategoryResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(AddOpportunityActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setDefaults() {
        binding.headerBottomRounded.headTitle.setText(getResources().getString(R.string.add_opportunity));
        binding.headerBottomRounded.backPress.setOnClickListener(this);
        binding.bussinessPartner.setOnClickListener(this);
        binding.owener.setOnClickListener(this);
        binding.submitButton.setOnClickListener(this);
        binding.businessPartnerValue.setOnClickListener(this);
        binding.startDateValue.setOnClickListener(this);
        binding.opportunityOwnerValue.setOnClickListener(this);
        binding.startDate.setOnClickListener(this);
        binding.startcalender.setOnClickListener(this);
        binding.closeDate.setOnClickListener(this);
        binding.closeDateValue.setOnClickListener(this);
        binding.startcalender.setOnClickListener(this);
        binding.leadView.setOnClickListener(this);
        binding.leadValue.setOnClickListener(this);
        binding.startDateValue.setText(Globals.getTodaysDate());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PARTNERCODE && resultCode == RESULT_OK) {

            /*BusinessPartnerData customerItem = (BusinessPartnerData) data.getSerializableExtra(Globals.CustomerItemData);
            setData(customerItem);*/

            BusinessPartnerAllResponse.Datum customerItem = (BusinessPartnerAllResponse.Datum) data.getSerializableExtra(Globals.CustomerItemData);
            callBPOneAPi(customerItem.getCardCode());

        } else if (requestCode == OWNERCODE && resultCode == RESULT_OK) {
            OwnerItem ownerItem = (OwnerItem) data.getSerializableExtra(Globals.OwnerItemData);
            binding.opportunityOwnerValue.setText(ownerItem.getFirstName() + " " + ownerItem.getMiddleName() + " " + ownerItem.getLastName());
            DataOwnershipfield = ownerItem.getEmployeeID();
        } else if (requestCode == LeadCode && resultCode == RESULT_OK) {
            LeadValue leadValue = data.getParcelableExtra(Globals.Lead_Data);
            binding.leadValue.setText(leadValue.getCompanyName());
            binding.opportunityNameValue.setText(leadValue.getCompanyName());
            LeadID = leadValue.getId().toString();
        }
        else if (resultCode == RESULT_OK && requestCode == ITEMSVIEWCODE) {
            binding.itemCount.setText("Item (" + Globals.SelectedItems.size() + ")");
//            binding.totalAfterItemDiscount = Double.parseDouble(String.valueOf(Globals.calculateTotalOfItem(Globals.SelectedItemsData)));
            String sum = String.valueOf(Globals.calculateTotalOfItem(Globals.SelectedItems));
            binding.totalBeforeDiscontValue.setText(sum);
//            getQuotationDocLin();
        }


    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_press:
                finish();
                break;
            case R.id.startcalender:
            case R.id.start_date_value:
            case R.id.startDate:
                startDate();
                break;
            case R.id.closeCalender:
            case R.id.close_date_value:
            case R.id.closeDate:
                closetDate();
                break;

            case R.id.bussinessPartner:
            case R.id.business_partner_value:

                selectBPartner();
                break;
            case R.id.lead_value:
            case R.id.lead_view:
                Prefs.putString(Globals.BussinessPageType, "AddOpportunityLead");
                Intent i = new Intent(AddOpportunityActivity.this, LeadsActivity.class);
                startActivityForResult(i, LeadCode);
                break;
            case R.id.owener:
            case R.id.opportunity_owner_value:

                Intent ii = new Intent(AddOpportunityActivity.this, OwnerList.class);
                startActivityForResult(ii, OWNERCODE);
                break;
            case R.id.submit_button:

                String cardValue = binding.businessPartnerValue.getText().toString().trim();
                String remark = binding.descriptionValue.getText().toString().trim();

                String inputString = binding.acProbability.getText().toString();

                int valueOfProbability = 0;
                if (!inputString.equalsIgnoreCase("")){
                    String stringWithoutPercent = inputString.replace("%", "");

                    valueOfProbability = Integer.parseInt(stringWithoutPercent);
                    System.out.println("Value at 0 index: " + valueOfProbability);
                }else {
//                    String stringWithoutPercent = inputString.replace("%", "");

                    valueOfProbability = Integer.parseInt("0");
                    System.out.println("Value at 0 index: " + valueOfProbability);
                }



                if (validation(cardValue, salesEmployeeCode,  TYPE, LEAD_SOURCE, ContactPerson, binding.closeDateValue)) {
                    jsonlist.clear();
                    SalesOpportunitiesLines dc = new SalesOpportunitiesLines();
                    dc.setSalesPerson(salesEmployeeCode);
                    dc.setDocumentType("bodt_MinusOne");
                    dc.setMaxLocalTotal(0);
                   // dc.setMaxLocalTotal(Float.valueOf(binding.potentialAmountValue.getText().toString().trim()));
                    dc.setStageKey("2");
                    jsonlist.add(dc);

                    AddOpportunityModel obj = new AddOpportunityModel();
                    obj.setOpportunityName(binding.opportunityNameValue.getText().toString().trim());
                    obj.setClosingDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(binding.closeDateValue.getText().toString().trim()));
                    obj.setPredictedClosingDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(binding.closeDateValue.getText().toString().trim()));
                    obj.setUType(TYPE);
                    obj.setCustomerName(CardName);
                    obj.setUFav("N");
                    obj.setULsource(LEAD_SOURCE);
                    obj.setUProblty(String.valueOf(valueOfProbability));
                    // obj.setDataOwnershipfield(DataOwnershipfield);

                    //todo cardcode from cardvalue
                    obj.setCardCode(cardCode); //cardValue
                    obj.setSalesPerson(String.valueOf(salesEmployeeCode));
                    obj.setContactPerson(ContactPersonCode);
                    obj.setContactPersonName(ContactPersonName);
                    obj.setCurrentStageName("");
                    obj.setCurrentStageNumber("");
                    obj.setSequentialNo("");
                    obj.setMaxLocalTotal(binding.potentialAmountValue.getText().toString().trim());//Potential Ammount
                    obj.setRemarks(remark);
                    obj.setMaxSystemTotal("0.7576");
                    obj.setStatus("sos_Open");
                    obj.setReasonForClosing("None");
                    obj.setTotalAmountLocal("5.0");
                    obj.setTotalAmounSystem("0.075");
                    obj.setCurrentStageNo("2");
                    obj.setIndustry("None");
                    obj.setLinkedDocumentType("None");
                    obj.setStatusRemarks("None");
                    obj.setProjectCode("None");
                    obj.setClosingType("sos_Days");
                    obj.setOpportunityType("boOpSales");
                    obj.setUpdateDate(Globals.getTodaysDatervrsfrmt());
                    obj.setUpdateTime(Globals.getTCurrentTime());
                    obj.setSalesOpportunitiesLines(jsonlist);
                    obj.setSource("None");
                    //todo change from salesemployee to static 0
                    obj.setDataOwnershipfield(String.valueOf("0"));//String.valueOf(salesEmployeeCode)
                    obj.setSalesPersonName(salesEmployeename);
                    obj.setDataOwnershipName(salesEmployeename);
                    obj.setStartDate(Globals.getTodaysDatervrsfrmt());
                    obj.setU_LEADID(LeadID);
                    obj.setU_LEADNM(binding.leadValue.getText().toString());

                    obj.setOppItem(Globals.SelectedItems);

                    obj.setDocumentLines(Globals.SelectedItems);

                    //todo new key doctotal
                    if (binding.totalBeforeDiscontValue.getText().toString().trim().isEmpty()){
                        obj.setDocTotal(0);
                    }else {
                        obj.setDocTotal(Float.valueOf(binding.totalBeforeDiscontValue.getText().toString()));
                    }




                    if (Globals.checkInternet(getApplicationContext()))
                        addQuotation(obj);
                }


                break;

        }
    }

    private void startDate() {
        Globals.selectDate(AddOpportunityActivity.this, binding.startDateValue);
    }

    private void closetDate() {
        Globals.enableAllCalenderDateSelect(AddOpportunityActivity.this, binding.closeDateValue);
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.itemCount.setText("Item (" + Globals.SelectedItems.size() + ")");

    }

    private void selectBPartner() {
        Prefs.putString(Globals.BussinessPageType, "AddOpportunity");
       // Prefs.putString(Globals.BussinessPageType, "DashBoard");

        Intent i = new Intent(AddOpportunityActivity.this, BussinessPartners.class);
        startActivityForResult(i, PARTNERCODE);
    }

    ArrayList<SalesOpportunitiesLines> jsonlist = new ArrayList<>();
    //    private ContactPersonAdapter contactPersonAdapter;
    private ContactPersonAutoAdapter contactPersonAdapter;
    String CardCode = "";

   /* private void setData(BusinessPartnerAllResponse.Datum customerItem) {

        ContactEmployeesList = new ArrayList<>();
        CardName = customerItem.getCardName();
        callContactEmployeeApi(customerItem.getCardCode());


        binding.businessPartnerValue.setText(customerItem.getCardCode());

        if (ContactEmployeesList.size() > 0)
            ContactPersonCode = ContactEmployeesList.get(0).getInternalCode();

    }*/

    BusinessPartnerAllResponse.Datum customerItem = null;
    String cardCode="";

    //todo calling bp one api here...
    private void callBPOneAPi(String BPCardCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CardCode", BPCardCode);
        Call<BusinessPartnerAllResponse> call = NewApiClient.getInstance().getApiService(this).callBPOneAPi(jsonObject);
        call.enqueue(new Callback<BusinessPartnerAllResponse>() {
            @Override
            public void onResponse(Call<BusinessPartnerAllResponse> call, Response<BusinessPartnerAllResponse> response) {
                if (response.body().getStatus() == 200) {
                    customerItem = response.body().getData().get(0);

                    binding.businessPartnerValue.setText(customerItem.getCardName());
                    cardCode=customerItem.getCardCode();

                    if (customerItem != null) {
                        setData(customerItem);
                    }

                    callSalessApi();

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BusinessPartnerAllResponse> call, Throwable t) {
                Log.e("ApiFailure==>", "onFailure: " + t.getMessage());
            }
        });

    }


    String ContactPerson = "";

    //todo set bp data..
    private void setData(BusinessPartnerAllResponse.Datum customerItem) {
        Prefs.putString(Globals.BusinessPartnerCardCode, "");

        CardName = customerItem.getCardName();
        CardCode = customerItem.getCardCode();

        Prefs.putString(Globals.BusinessPartnerCardCode, customerItem.getCardCode());

//        callBPBillToAddressApi(customerItem.getCardCode());
//        callBPShipToAddressApi(customerItem.getCardCode());

        binding.businessPartnerValue.setText(customerItem.getCardName());
        binding.leadValue.setText(customerItem.getU_LEADNM());

        callLeadOneApi(customerItem.U_LEADID);

        //todo bind adapter list----

        if (customerItem.getContactEmployees().size() > 0) {
            ContactPersonCode = customerItem.getContactEmployees().get(0).getInternalCode();
            ContactPersonName = customerItem.getContactEmployees().get(0).getFirstName();
            binding.acContactPerson.setText(customerItem.getContactEmployees().get(0).getFirstName());

            callContactEmployeeApi(CardCode);

        } else {
            binding.acContactPerson.setText("");
        }


       /* if (customerItem.getSalesPersonCode().size() > 0) {
            binding.salesEmployeeSpinner.setSelection(Globals.getSalesEmployeePos(salesEmployeeItemList, customerItem.getSalesPersonCode().get(0).getSalesEmployeeName()));
            salesEmployeeCode = Integer.valueOf(Globals.getSelectedSalesP(salesEmployeeItemList, customerItem.getSalesPersonCode().get(0).getSalesEmployeeCode()));
        }
*/
        if (customerItem.getSalesPersonCode().size() > 0) {
            binding.acSalesEmployee.setText(customerItem.getSalesPersonCode().get(0).getSalesEmployeeName());
            salesEmployeeCode = Integer.parseInt(customerItem.getSalesPersonCode().get(0).getSalesEmployeeCode());
            salesEmployeename = customerItem.getSalesPersonCode().get(0).getSalesEmployeeName();
        }

        binding.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    salesEmployeeCode = Integer.parseInt(salesEmployeeItemList.get(position).getSalesEmployeeCode());
                    salesEmployeename = salesEmployeeItemList.get(position).getSalesEmployeeName();

                    binding.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                } else {
                    salesEmployeeCode = 0;
                    salesEmployeename = "";

                    binding.acSalesEmployee.setText("");
                }
            }
        });


        /*binding.salesEmployeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    salesEmployeename = salesEmployeeItemList.get(position).getSalesEmployeeName();
                    salesEmployeeCode = Integer.valueOf(salesEmployeeItemList.get(position).getSalesEmployeeCode());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                salesEmployeename = "";
                salesEmployeeCode = 0;
            }
        });
*/

    }


    //todo call lead one api here---
    private void callLeadOneApi(String id) {
        LeadValue lv = new LeadValue();
        if (id.isEmpty()){
            lv.setId(0);
        }else {
            lv.setId(Integer.valueOf(id));
        }

        JsonObject jsonObject =new JsonObject();
        jsonObject.addProperty("id",id);
        Call<LeadResponse> call = NewApiClient.getInstance().getApiService(this).particularlead(jsonObject);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {
                if (response != null) {
                    if (response.body().getStatus() == 200) {
                        LeadValue leadValue = response.body().getData().get(0);

                        if (!leadValue.getSource().isEmpty()){
                            LEAD_SOURCE = leadValue.getSource();
                            binding.leadSourceSpinner.setSelection(Globals.getLeadSourcePos(sourceData, leadValue.getSource()));
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<LeadResponse> call, Throwable t) {

            }
        });
    }


    ArrayList<ContactPersonData> ContactEmployeesList = new ArrayList<>();


    //todo calling contact person api here---
    private void callContactEmployeeApi(String id) {
        ContactPersonData contactPersonData = new ContactPersonData();
        contactPersonData.setCardCode(id);
        binding.loader.loader.setVisibility(View.VISIBLE);
        Call<ContactPerson> call = NewApiClient.getInstance().getApiService(this).contactemplist(contactPersonData);
        call.enqueue(new Callback<ContactPerson>() {
            @Override
            public void onResponse(Call<ContactPerson> call, Response<ContactPerson> response) {
                binding.loader.loader.setVisibility(View.GONE);
                if (response.body().getStatus() == 200) {
                    if (response.body().getData().size() > 0) {
                        ContactEmployeesList.clear();
                        ContactEmployeesList.addAll(response.body().getData());

                        contactPersonAdapter = new ContactPersonAutoAdapter(AddOpportunityActivity.this, R.layout.drop_down_textview, ContactEmployeesList);
                        binding.acContactPerson.setAdapter(contactPersonAdapter);
                    }

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(AddOpportunityActivity.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactPerson> call, Throwable t) {
                binding.loader.loader.setVisibility(View.GONE);
                Toast.makeText(AddOpportunityActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    public List<SalesEmployeeItem> salesEmployeeItemList = new ArrayList<>();


    //todo calling sales employee api..
    private void callSalessApi() {
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getSalesEmployeeList().observe(this, new Observer<List<SalesEmployeeItem>>() {
            @Override
            public void onChanged(@Nullable List<SalesEmployeeItem> itemsList) {
                if (itemsList == null || itemsList.size() == 0) {
                    Globals.setmessage(getApplicationContext());
                } else {
                    salesEmployeeItemList = itemsList;
                    SalesEmployeeAutoAdapter adapter = new SalesEmployeeAutoAdapter(AddOpportunityActivity.this, R.layout.drop_down_textview, itemsList);
                    binding.acSalesEmployee.setAdapter(adapter);

                    callUTypeApi();

                }
            }
        });
    }


    List<UTypeData> utypelist = new ArrayList<>();

    private void callUTypeApi() {
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getOPpTypeList().observe(this, new Observer<List<UTypeData>>() {
            @Override
            public void onChanged(@Nullable List<UTypeData> itemsList) {
                if (itemsList == null || itemsList.size() == 0) {
                    Globals.setmessage(act);
                } else {
                    utypelist = itemsList;
                    binding.typeSpinner.setAdapter(new BPTypeSpinnerAdapter(act, itemsList));
                    TYPE = utypelist.get(0).getId().toString();

                }
            }
        });
    }

    ArrayList<LeadTypeData> sourceData = new ArrayList<>();

    //tod calling source type api ---
    private void callSourceApi() {
        Call<LeadTypeResponse> call = NewApiClient.getInstance().getApiService(this).getsourceType();
        call.enqueue(new Callback<LeadTypeResponse>() {
            @Override
            public void onResponse(Call<LeadTypeResponse> call, Response<LeadTypeResponse> response) {

                if (response.body().getStatus() == 200) {

                    sourceData.clear();
//                    sourceData.addAll(MainActivity.leadSourceListFromLocal);
                    sourceData.addAll(response.body().getData());
                    binding.leadSourceSpinner.setAdapter(new LeadTypeAdapter(AddOpportunityActivity.this, sourceData));
                    LEAD_SOURCE = sourceData.get(0).getName();
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                        Toast.makeText(AddOpportunityActivity.this, mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                }

            }

            @Override
            public void onFailure(Call<LeadTypeResponse> call, Throwable t) {
//                binding.loader.setVisibility(View.GONE);
                Toast.makeText(AddOpportunityActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addQuotation(AddOpportunityModel in) {
        binding.submitButton.setEnabled(false);
        binding.loader.loader.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        String jsonTut = gson.toJson(in);
        Log.e("data", jsonTut);
        Call<NewOppResponse> call = NewApiClient.getInstance().getApiService(this).createopportunity(in);
        call.enqueue(new Callback<NewOppResponse>() {
            @Override
            public void onResponse(Call<NewOppResponse> call, Response<NewOppResponse> response) {
                binding.submitButton.setEnabled(true);
                binding.loader.loader.setVisibility(View.GONE);
                if (response.code() == 200) {

                    if (response.body().getStatus() == 200) {
                        Toasty.success(AddOpportunityActivity.this, "Add Successfully", Toast.LENGTH_LONG).show();
                        Globals.SelectedItems.clear();
                        onBackPressed();
                    } else {
                        Toasty.warning(AddOpportunityActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    }
                } else {
                    binding.submitButton.setEnabled(true);
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(AddOpportunityActivity.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewOppResponse> call, Throwable t) {
                binding.submitButton.setEnabled(true);
                binding.loader.loader.setVisibility(View.GONE);
                Toasty.error(AddOpportunityActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validation(String cardCode, int salesEmployeeCode, String potentialAmount, String remark, String contactPerson, EditText closeDateValue) {
        if (Globals.SelectedItems.size() == 0){
            Globals.showMessage(act, "Select Atleast One Item");
            return false;
        }
        else if (cardCode.isEmpty()) {
            Globals.showMessage(act, getString(R.string.select_bp));
            return false;
        } else if (ContactPersonCode.equalsIgnoreCase("-1")) {
            Globals.showMessage(act, getString(R.string.enter_cp));
            return false;
        }
       /* else if (potentialAmount.isEmpty()) {
            Globals.showMessage(act, getString(R.string.potential_amnt_error));
            return false;
        }*/ else if (binding.opportunityNameValue.getText().toString().trim().length() == 0) {
            binding.opportunityNameValue.requestFocus();
            binding.opportunityNameValue.setError(getString(R.string.enter_opp));
            Globals.showMessage(act, getString(R.string.enter_opp));
            return false;
        } else if (binding.closeDateValue.getText().toString().trim().length() == 0) {
            Globals.showMessage(act, "Enter closing date");
            return false;
        } else if (TYPE.equalsIgnoreCase("-None-")) {
            Globals.showMessage(act, getString(R.string.enter_tye));
            return false;
        } else if (LEAD_SOURCE.equalsIgnoreCase("-None-")) {
            Globals.showMessage(act, getString(R.string.enter_lead_source));
            return false;
        } else if (salesEmployeeCode == 0) {
            Globals.showMessage(act, getString(R.string.enter_sp));
            return false;
        } /*else if (remark.isEmpty()) {
            binding.descriptionValue.requestFocus();
            binding.descriptionValue.setError(getString(R.string.remark_error));
            Globals.showMessage(act, getString(R.string.remark_error));
            return false;
        }*/

        return true;
    }


    //todo adapter interface value getting..
    @Override
    public void onClick(int po) {
        Intent intent = new Intent(this, ItemsList.class);
        intent.putExtra("CategoryID", po);
        startActivityForResult(intent, ITEMSVIEWCODE);
    }


    @Override
    public void onRefresh() {
        Intent intent = new Intent(AddOpportunityActivity.this, ItemsList.class);
        intent.putExtra("CardCode", CardCode);
        startActivity(intent);
    }


}