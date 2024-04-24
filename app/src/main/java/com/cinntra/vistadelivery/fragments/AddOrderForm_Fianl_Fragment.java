package com.cinntra.vistadelivery.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.AddOrderAct;

import com.cinntra.vistadelivery.adapters.CountryAdapter;
import com.cinntra.vistadelivery.adapters.StateAdapter;
import com.cinntra.vistadelivery.databinding.FragmentAddQtFinalBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.interfaces.SubmitQuotation;
import com.cinntra.vistadelivery.model.AddressExtensions;
import com.cinntra.vistadelivery.model.BPAddress;
import com.cinntra.vistadelivery.model.BPModel.BPAllFilterRequestModel;
import com.cinntra.vistadelivery.model.BPModel.BranchOneResponseModel;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerData;
import com.cinntra.vistadelivery.model.CountryData;
import com.cinntra.vistadelivery.model.CountryResponse;
import com.cinntra.vistadelivery.model.CustomerBusinessRes;
import com.cinntra.vistadelivery.model.OpportunityModels.OppAddressResponseModel;
import com.cinntra.vistadelivery.model.QuotationItem;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.StateData;
import com.cinntra.vistadelivery.model.StateRespose;
import com.cinntra.vistadelivery.newapimodel.NewOpportunityRespose;
import com.cinntra.vistadelivery.spinneradapter.BranchTypeAllSearchableSpinnerAdapter;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddOrderForm_Fianl_Fragment extends Fragment {

    SubmitQuotation submitQuotation;
    String[] shippinngType;
//    String[] billingType;
    String billshipType;
    String ship_shiptype;
    String billtoState, billtoStateCode, billtoCountrycode, billtoCountryName, shiptoState, shiptoCountrycode, shiptoCountryName, shiptoStateCode;
    CountryAdapter countryAdapter;
    StateAdapter stateAdapter, shipStateAdapter;
    ArrayList<StateData> stateList = new ArrayList<>();
    ArrayList<StateData> shipstateList = new ArrayList<>();
    String mCardCode = "";

    NewOpportunityRespose mOpportunityItemValue = new NewOpportunityRespose();
    QuotationItem quotationItem = new QuotationItem();

    public AddOrderForm_Fianl_Fragment(QuotationItem quotationItem1, NewOpportunityRespose mOpportunityItemValue, String mCardCode) {
        quotationItem = quotationItem1;
        this.mOpportunityItemValue = mOpportunityItemValue;
        this.mCardCode = mCardCode;
    }


    // TODO: Rename and change types and number of parameters
    public static AddOrderForm_Fianl_Fragment newInstance(String param1, String param2, QuotationItem quotationItem1, NewOpportunityRespose mOpportunityItemValue, String mCardCode) {
        AddOrderForm_Fianl_Fragment fragment = new AddOrderForm_Fianl_Fragment(quotationItem1, mOpportunityItemValue, mCardCode);
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    FragmentAddQtFinalBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddQtFinalBinding.inflate(inflater, container, false);
        View v = inflater.inflate(R.layout.fragment_add_qt_final, container, false);
        //  ButterKnife.bind(this,v);
        binding.quotationPreparedForContent.addressSection.checkboxManager.setVisibility(View.VISIBLE);
        setDefaults();
        eventManageer();

        shippinngType = getResources().getStringArray(R.array.bpShippingType);
        ship_shiptype = shippinngType[0];
//        billingType = getResources().getStringArray(R.array.bpBillingType);
        billshipType = shippinngType[0];

        submitQuotation = (SubmitQuotation) getActivity();


        binding.headerBottomRounded.headTitle.setText(getResources().getString(R.string.add_order));
        binding.headerBottomRounded.backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if (!mCardCode.equalsIgnoreCase("") || mCardCode != null){
            callBpAddressAPI();
        }


        //todo calling country api here---
        callCountryApi();

        binding.quotationPreparedForContent.addressSection.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validation(binding.quotationPreparedForContent.addressSection.billingNameValue.getText().toString().trim(),
                        binding.quotationPreparedForContent.addressSection.billingAddressValue.getText().toString().trim())) {
                    AddressExtensions addressExtension = new AddressExtensions();
                    if (binding.quotationPreparedForContent.addressSection.checkbox1.isChecked()) {
                        addressExtension.setShipToZipCode(binding.quotationPreparedForContent.addressSection.zipcodeValue2.getText().toString().trim());
                        addressExtension.setShipToBuilding(binding.quotationPreparedForContent.addressSection.shippingNameValue.getText().toString().trim());
                        addressExtension.setShipToStreet(binding.quotationPreparedForContent.addressSection.shippingAddressValue.getText().toString().trim());
                        addressExtension.setU_SSTATE(shiptoState);
                        addressExtension.setU_SCOUNTRY(shiptoCountryName);
                        addressExtension.setU_SHPTYPS(ship_shiptype);
                        addressExtension.setShipToState(shiptoStateCode);
                        addressExtension.setShipToCountry(shiptoCountrycode);
                        addressExtension.setShipToCity(binding.quotationPreparedForContent.addressSection.shipcityValue.getText().toString());
                    } else {
                        addressExtension.setShipToZipCode(binding.quotationPreparedForContent.addressSection.zipCodeValue.getText().toString().trim());
                        addressExtension.setShipToBuilding(binding.quotationPreparedForContent.addressSection.billingNameValue.getText().toString().trim());
                        addressExtension.setShipToStreet(binding.quotationPreparedForContent.addressSection.billingAddressValue.getText().toString().trim());
                        addressExtension.setU_SSTATE(billtoState);
                        addressExtension.setU_SCOUNTRY(billtoCountryName);
                        addressExtension.setU_SHPTYPS(billshipType);
                        addressExtension.setShipToState(billtoStateCode);
                        addressExtension.setShipToCountry(billtoCountrycode);
                        addressExtension.setShipToCity(binding.quotationPreparedForContent.addressSection.cityValue.getText().toString());
                    }


                    addressExtension.setShipToCity("");
                    // addressExtension.setShipToZipCode(zipcode_value2.getText().toString());
                    //countryCode2
                    addressExtension.setU_BSTATE(billtoState);
                    addressExtension.setU_BCOUNTRY(billtoCountryName);
                    addressExtension.setBillToBuilding(binding.quotationPreparedForContent.addressSection.billingNameValue.getText().toString());
                    addressExtension.setBillToStreet(binding.quotationPreparedForContent.addressSection.billingAddressValue.getText().toString());
                    addressExtension.setBillToCity(binding.quotationPreparedForContent.addressSection.cityValue.getText().toString());
                    addressExtension.setBillToZipCode(binding.quotationPreparedForContent.addressSection.zipCodeValue.getText().toString());
                    addressExtension.setBillToState(billtoStateCode);
                    addressExtension.setBillToCountry(billtoCountrycode);   //countryCode
                    addressExtension.setU_SHPTYPB(billshipType);
                    AddOrderAct.addQuotationObj.setAddressExtension(addressExtension);
                    //todo new prID key Added
                    AddOrderAct.addQuotationObj.setPRID("");
                    AddOrderAct.addQuotationObj.setDepartement("2");
                    if (Globals.checkInternet(getActivity()))
                        submitQuotation.submitQuotaion(binding.loader.loader);
                }
            }
        });

        setUpBranchAllSpinner();
        return binding.getRoot();
    }


    String branchTypeBillingSelected = "";
    String branchTypeShippingSelected = "";
    ArrayList<OppAddressResponseModel.Data> branchTypeDataList = new ArrayList<>();

    private void setUpBranchAllSpinner() {
        binding.quotationPreparedForContent.addressSection.saerchableSpinnerBillingAddress.setTitle("Branch");
        binding.quotationPreparedForContent.addressSection.saerchableSpinnerShippingAddress.setTitle("Branch");
        binding.quotationPreparedForContent.addressSection.saerchableSpinnerBillingAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SPINNER SEARCH", "onItemSelected: " + branchTypeDataList.get(i).getAddressName());
                branchTypeBillingSelected = branchTypeDataList.get(i).getAddressName();
                callBranchOneAPi(branchTypeDataList.get(i).id,"bill");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.quotationPreparedForContent.addressSection.saerchableSpinnerShippingAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SPINNER SEARCH", "onItemSelected: " + branchTypeDataList.get(i).getAddressName());
                branchTypeShippingSelected = branchTypeDataList.get(i).getAddressName();
                callBranchOneAPi(branchTypeDataList.get(i).id,"ship");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        BPAllFilterRequestModel requestModel = new BPAllFilterRequestModel();
        //   requestModel.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        requestModel.setPageNo(1);
        requestModel.setMaxItem(50);
        requestModel.setSearchText("");
        requestModel.setOrder_by_field(Globals.orderbyField_id);
        requestModel.setOrder_by_value(Globals.orderbyvalueDesc);
        BPAllFilterRequestModel.Field field = new BPAllFilterRequestModel.Field();
         /*   field.setCardType("");
            field.setIndustry(industryCode);
            field.setSalesPersonPerson(salesEmployeeCode);//solIdName*/
        requestModel.setField(field);
        JsonObject jsonObject =new JsonObject();

    /*    {
            "BPCode": "C6",
                "PageNo": 1,
                "maxItem": "All",
                "order_by_field": "id",
                "order_by_value": "asc",
                "SearchText": "",
                "field": {}
        }*/



        jsonObject.addProperty("BPCode",mCardCode);
        jsonObject.addProperty("PageNo",1);
        jsonObject.addProperty("maxItem","All");
        jsonObject.addProperty("SearchText","");
        jsonObject.addProperty("order_by_field","id");
        jsonObject.addProperty(Globals.orderbyvalue,"asc");
        // Create an empty JSON object for the "field" property
        JsonObject emptyFieldObject = new JsonObject();

        // Add the empty "field" object to the main JSON object
        jsonObject.add("field", emptyFieldObject);


        Call<OppAddressResponseModel> call = NewApiClient.getInstance().getApiService(requireContext()).getShipToAddress(jsonObject);
        call.enqueue(new Callback<OppAddressResponseModel>() {
            @Override
            public void onResponse(Call<OppAddressResponseModel> call, Response<OppAddressResponseModel> response) {

                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {


                        branchTypeDataList.clear();
                        branchTypeDataList.addAll(response.body().getData());
                        branchTypeBillingSelected = branchTypeDataList.get(0).getAddressName();
                        branchTypeShippingSelected = branchTypeDataList.get(0).getAddressName();
                        BranchTypeAllSearchableSpinnerAdapter sourceSearchableSpinnerAdapter = new BranchTypeAllSearchableSpinnerAdapter(requireContext(), branchTypeDataList);

                        binding.quotationPreparedForContent.addressSection.saerchableSpinnerBillingAddress.setAdapter(sourceSearchableSpinnerAdapter);
                        binding.quotationPreparedForContent.addressSection.saerchableSpinnerShippingAddress.setAdapter(sourceSearchableSpinnerAdapter);


                        //todo item click on assign to---
                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                    }


                } else {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<OppAddressResponseModel> call, Throwable t) {

                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }




    //todo call branch one api here...
    private void callBranchOneAPi(String id,String flag) {
        // binding.loaderLayout.loader.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);

        Call<BranchOneResponseModel> call = NewApiClient.getInstance().getApiService(requireContext()).getBranchOneApi(jsonObject);
        call.enqueue(new Callback<BranchOneResponseModel>() {
            @Override
            public void onResponse(Call<BranchOneResponseModel> call, Response<BranchOneResponseModel> response) {
                try {
                    if (response.body().getStatus() == 200) {

                        //    branchOneResponseModel_gl = response.body().getData().get(0);

                        if (flag.equalsIgnoreCase("bill")){
                            setDefaultDataByBranchOne(response.body().getData().get(0));
                        }else {
                            setDefaultDataByBranchOneShip(response.body().getData().get(0));
                        }



                    } else {

                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BranchOneResponseModel> call, Throwable t) {

                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    //todo set default data...
    private void setDefaultDataByBranchOne(BranchOneResponseModel.Data data) {


        callBillToStateApi(data.getCountry());
        binding.quotationPreparedForContent.addressSection.billingNameValue.setText(data.AddressName);

        binding.quotationPreparedForContent.addressSection.billingAddressValue.setText(data.getStreet());
        binding.quotationPreparedForContent.addressSection.zipCodeValue.setText(data.getZipCode());
        binding.quotationPreparedForContent.addressSection.acCountry.setText(data.getU_COUNTRY());
        binding.quotationPreparedForContent.addressSection.acBillToState.setText(data.getU_STATE());
        binding.quotationPreparedForContent.addressSection.cityValue.setText(data.getCity());

        billtoCountrycode = data.getCountry();
        billtoStateCode = data.getState();
        //   BranchType = branchOneResponseModel_gl.getBranchType();

        billtoCountryName = data.getU_COUNTRY();
        billtoState = data.getU_STATE();
        billshipType=data.getU_SHPTYP();
        binding.quotationPreparedForContent.addressSection.shippingSpinner.setSelection(Globals.getShipTypePo(shippinngType,billshipType));


    }



    private void setDefaultDataByBranchOneShip(BranchOneResponseModel.Data data) {
        callShipToStateApi(data.getCountry());
        binding.quotationPreparedForContent.addressSection.shippingNameValue.setText(data.AddressName);

        binding.quotationPreparedForContent.addressSection.shippingAddressValue.setText(data.getStreet());
        binding.quotationPreparedForContent.addressSection.zipcodeValue2.setText(data.getZipCode());
        binding.quotationPreparedForContent.addressSection.acShipCountry.setText(data.getU_COUNTRY());
        binding.quotationPreparedForContent.addressSection.acShipToState.setText(data.getU_STATE());
        binding.quotationPreparedForContent.addressSection.shipcityValue.setText(data.getCity());

        shiptoCountrycode = data.getCountry();
        shiptoStateCode = data.getState();
        //   BranchType = branchOneResponseModel_gl.getBranchType();

        shiptoCountryName = data.getU_COUNTRY();
        shiptoState = data.getU_STATE();
        ship_shiptype=data.getU_SHPTYP();
        binding.quotationPreparedForContent.addressSection.shippingSpinner2.setSelection(Globals.getShipTypePo(shippinngType,ship_shiptype));



    }










    //todo set fields data acc to parent proforma invoice...
    private void setDefaultData(List<BPAddress> bpAddresses) {
      /*  binding.quotationPreparedForContent.addressSection.zipCode.setVisibility(View.GONE);
        binding.quotationPreparedForContent.addressSection.zipCode1.setVisibility(View.VISIBLE);
        binding.quotationPreparedForContent.addressSection.zipcode2.setVisibility(View.GONE);
        binding.quotationPreparedForContent.addressSection.zipcode3.setVisibility(View.VISIBLE);*/


        int boBillToPos = getAddres_BillRow_Pos(bpAddresses, "0");
        int boShipToPos = getAddres_ShipRow_Pos(bpAddresses, "1");

        if (bpAddresses.size() > 0){

            if (boBillToPos == 0){
                billtoCountrycode = bpAddresses.get(boBillToPos).getCountry();
                billtoCountryName = bpAddresses.get(boBillToPos).getUCountry();
                billtoStateCode = bpAddresses.get(0).getState();
                billtoState = bpAddresses.get(0).getUState();
                callBillToStateApi(billtoCountrycode);
                binding.quotationPreparedForContent.addressSection.billingNameValue.setText(bpAddresses.get(boBillToPos).getAddressName());
                binding.quotationPreparedForContent.addressSection.zipCodeValue.setText(bpAddresses.get(boBillToPos).getZipCode());
//                binding.quotationPreparedForContent.addressSection.acCountry.setSelection(Globals.getCountrypos(countyList, bpAddresses.get(boBillToPos).getUCountry()));
                binding.quotationPreparedForContent.addressSection.acCountry.setText(bpAddresses.get(boBillToPos).getUCountry());
                binding.quotationPreparedForContent.addressSection.acBillToState.setText(bpAddresses.get(0).getUState());
                binding.quotationPreparedForContent.addressSection.cityValue.setText(bpAddresses.get(boBillToPos).getCity());
                binding.quotationPreparedForContent.addressSection.shippingSpinner.setSelection(Globals.getShipTypePo(shippinngType, bpAddresses.get(boBillToPos).getUShptyp()));
                binding.quotationPreparedForContent.addressSection.billingAddressValue.setText(bpAddresses.get(boBillToPos).getStreet());

            }

            if (boShipToPos == 1){
                shiptoCountrycode = bpAddresses.get(boShipToPos).getCountry();
                shiptoCountryName = bpAddresses.get(boShipToPos).getUCountry();
                shiptoStateCode = bpAddresses.get(1).getState();
                shiptoState = bpAddresses.get(1).getUState();
                callShipToStateApi(shiptoCountrycode);
                binding.quotationPreparedForContent.addressSection.shippingNameValue.setText(bpAddresses.get(boShipToPos).getAddressName());
                binding.quotationPreparedForContent.addressSection.zipcodeValue2.setText(bpAddresses.get(boShipToPos).getZipCode());
//                binding.quotationPreparedForContent.addressSection.acShipCountry.setSelection(Globals.getCountrypos(countyList, bpAddresses.get(boShipToPos).getUCountry()));
                binding.quotationPreparedForContent.addressSection.acShipCountry.setText(bpAddresses.get(boShipToPos).getUCountry());
//                binding.quotationPreparedForContent.addressSection.acShipToState.setSelection(Globals.getStatePo(stateList, bpAddresses.get(boShipToPos).getUState()));
                binding.quotationPreparedForContent.addressSection.acShipToState.setText(bpAddresses.get(1).getUState());
                binding.quotationPreparedForContent.addressSection.shipcityValue.setText(bpAddresses.get(boShipToPos).getCity());
                binding.quotationPreparedForContent.addressSection.shippingSpinner2.setSelection(Globals.getShipTypePo(shippinngType, bpAddresses.get(boShipToPos).getUShptyp()));
                binding.quotationPreparedForContent.addressSection.shippingAddressValue.setText(bpAddresses.get(boShipToPos).getStreet()); //quotationItem.getAddressExtension().getShipToStreet()

            }

        }


    }

    private void eventManageer() {


   /*     billtoCountrycode = "";
        billtoCountryName = "India";
        shiptoCountrycode = "IN";
        shiptoCountryName = "India";
*/
//        callStateApi(billtoCountrycode);


        //todo bill to state item click..
        binding.quotationPreparedForContent.addressSection.acBillToState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                billtoState = stateList.get(position).getName();
                billtoStateCode = stateList.get(position).getCode();

                binding.quotationPreparedForContent.addressSection.acBillToState.setText(stateList.get(position).getName());
            }
        });


        //todo ship to state item click..
        binding.quotationPreparedForContent.addressSection.acShipToState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shiptoState = stateList.get(position).getName();
                shiptoStateCode = stateList.get(position).getCode();

                binding.quotationPreparedForContent.addressSection.acShipToState.setText(stateList.get(position).getName());
            }
        });


        binding.quotationPreparedForContent.addressSection.checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.quotationPreparedForContent.addressSection.shipBlock.setVisibility(View.VISIBLE);
                } else {
                    binding.quotationPreparedForContent.addressSection.shipBlock.setVisibility(View.GONE);
                }
            }
        });

        binding.quotationPreparedForContent.addressSection.shippingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                billshipType = shippinngType[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                billshipType = shippinngType[0];
            }
        });


        binding.quotationPreparedForContent.addressSection.shippingSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ship_shiptype = shippinngType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ship_shiptype = shippinngType[0];
            }
        });
    }


    //todo business partner one api calling here...
    private void callBpAddressAPI() {
        BusinessPartnerData contactPersonData = new BusinessPartnerData();

        if (Globals.opportunityData.size() > 0) {
            contactPersonData.setCardCode(mCardCode); //Globals.opportunityData.get(0).getCardCode()
        } else {
        }

        contactPersonData.setCardCode(mCardCode);
        binding.loader.loader.setVisibility(View.VISIBLE);
        Call<CustomerBusinessRes> call = NewApiClient.getInstance().getApiService(requireContext()).particularcustomerdetails(contactPersonData);
        call.enqueue(new Callback<CustomerBusinessRes>() {
            @Override
            public void onResponse(Call<CustomerBusinessRes> call, Response<CustomerBusinessRes> response) {
                binding.loader.loader.setVisibility(View.GONE);
                if (response.body().getStatus() == 200) {

                    if (response.body().getData().size() > 0) {
                        setDefaultData(response.body().getData().get(0).getBPAddresses());
                    }


                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomerBusinessRes> call, Throwable t) {
                binding.loader.loader.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    //todo calling country api here...

    ArrayList<CountryData> countyList = new ArrayList<>();

    private void callCountryApi() {
        Call<CountryResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getCountryList();
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.body().getStatus() == 200) {
                    if (response.body().getData().size() > 0) {

                        List<CountryData> itemsList = response.body().getData();
                        itemsList = filterList(response.body().getData());
                        countyList.addAll(itemsList);

                        List<String> itemNames = new ArrayList<>();
                        List<String> cardCodeName = new ArrayList<>();
                        for (CountryData item : countyList) {
                            itemNames.add(item.getName());
                            cardCodeName.add(item.getCode());
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_textview, itemNames);
                        binding.quotationPreparedForContent.addressSection.acCountry.setAdapter(adapter);


                        //todo bill to and ship to address drop down item select..
                        binding.quotationPreparedForContent.addressSection.acCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    String countryName = (String) parent.getItemAtPosition(position);
                                    billtoCountryName = countryName;

                                    int pos = Globals.getCountryCodePos(countyList, countryName);
                                    billtoCountrycode = countyList.get(pos).getCode();

                                    if (countryName.isEmpty()) {
                                        binding.quotationPreparedForContent.addressSection.rlRecyclerViewLayout.setVisibility(View.GONE);
                                        binding.quotationPreparedForContent.addressSection.rvCountryList.setVisibility(View.GONE);
                                    } else {
                                        binding.quotationPreparedForContent.addressSection.rlRecyclerViewLayout.setVisibility(View.VISIBLE);
                                        binding.quotationPreparedForContent.addressSection.rvCountryList.setVisibility(View.VISIBLE);
                                    }

                                    if (!countryName.isEmpty()) {
                                        adapter.notifyDataSetChanged();
                                        binding.quotationPreparedForContent.addressSection.acCountry.setText(countryName);
                                        binding.quotationPreparedForContent.addressSection.acCountry.setSelection(binding.quotationPreparedForContent.addressSection.acCountry.length());

                                        callBillToStateApi(billtoCountrycode);
                                    } else {
                                        billtoCountryName = "";
                                        billtoCountrycode = "";
                                        binding.quotationPreparedForContent.addressSection.acCountry.setText("");
                                    }
                                } catch (Exception e) {
                                    Log.e("catch", "onItemClick: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });

                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.drop_down_textview, itemNames);
                        binding.quotationPreparedForContent.addressSection.acShipCountry.setAdapter(adapter);



                        //todo set on ship country Item Click
                        binding.quotationPreparedForContent.addressSection.acShipCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    String countryName = (String) parent.getItemAtPosition(position);
                                    shiptoCountryName = countryName;

                                    int pos = Globals.getCountryCodePos(countyList, countryName);
                                    shiptoCountrycode = countyList.get(pos).getCode();

                                    if (countryName.isEmpty()) {
                                        binding.quotationPreparedForContent.addressSection.rlShipREcyclerLayout.setVisibility(View.GONE);
                                        binding.quotationPreparedForContent.addressSection.rvShipCountryList.setVisibility(View.GONE);
                                    } else {
                                        binding.quotationPreparedForContent.addressSection.rlShipREcyclerLayout.setVisibility(View.VISIBLE);
                                        binding.quotationPreparedForContent.addressSection.rvShipCountryList.setVisibility(View.VISIBLE);
                                    }

                                    if (!countryName.isEmpty()) {
                                        adapter1.notifyDataSetChanged();
                                        binding.quotationPreparedForContent.addressSection.acShipCountry.setText(countryName);
                                        binding.quotationPreparedForContent.addressSection.acShipCountry.setSelection(binding.quotationPreparedForContent.addressSection.acShipCountry.length());

                                        callShipToStateApi(shiptoCountrycode);
                                    } else {
                                        shiptoCountryName = "";
                                        shiptoCountrycode = "";
                                        binding.quotationPreparedForContent.addressSection.acShipCountry.setText("");
                                    }
                                } catch (Exception e) {
                                    Log.e("catch", "onItemClick: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });


                    }

                } else {
                    Toasty.error(getActivity(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<CountryData> filterList(List<CountryData> value) {
        List<CountryData> tempList = new ArrayList<>();
        for (CountryData customer : value) {
            if (!customer.getName().equals("admin")) {
                tempList.add(customer);
            }
        }
        return tempList;
    }


    private void callBillToStateApi(String billtoCountrycode) {
        StateData stateData = new StateData();
        stateData.setCountry(billtoCountrycode);
        Call<StateRespose> call = NewApiClient.getInstance().getApiService(requireContext()).getStateList(stateData);
        call.enqueue(new Callback<StateRespose>() {
            @Override
            public void onResponse(Call<StateRespose> call, Response<StateRespose> response) {

                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {

                        stateList.clear();
                        if (response.body().getData().size() > 0) {
                            stateList.addAll(response.body().getData());
                        } else {
                            StateData sta = new StateData();
                            sta.setName("Select State");
                            stateList.add(sta);
                        }
                        stateAdapter = new StateAdapter(getContext(), R.layout.drop_down_textview, stateList);
                        binding.quotationPreparedForContent.addressSection.acBillToState.setAdapter(stateAdapter);
                       /* billtoState = stateList.get(0).getName();
                        billtoStateCode = stateList.get(0).getCode();*/
                        stateAdapter.notifyDataSetChanged();
                    }

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(getContext(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StateRespose> call, Throwable t) {

                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callShipToStateApi(String shiptoCountrycode) {
        StateData stateData = new StateData();
        stateData.setCountry(shiptoCountrycode);
        Call<StateRespose> call = NewApiClient.getInstance().getApiService(requireContext()).getStateList(stateData);
        call.enqueue(new Callback<StateRespose>() {
            @Override
            public void onResponse(Call<StateRespose> call, Response<StateRespose> response) {

                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {

                        stateList.clear();
                        if (response.body().getData().size() > 0) {
                            stateList.addAll(response.body().getData());
                        } else {
                            StateData sta = new StateData();
                            sta.setName("Select State");
                            stateList.add(sta);
                        }
                        stateAdapter = new StateAdapter(getContext(), R.layout.drop_down_textview, stateList);
                        binding.quotationPreparedForContent.addressSection.acShipToState.setAdapter(stateAdapter);
                       /* shiptoState = stateList.get(0).getName();
                        shiptoStateCode = stateList.get(0).getCode();*/
                        stateAdapter.notifyDataSetChanged();

                    }

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(getContext(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StateRespose> call, Throwable t) {

                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setDefaults() {

        binding.headerBottomRounded.headTitle.setText(getResources().getString(R.string.add_order));
        binding.headerBottomRounded.backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }



    private static int getAddres_BillRow_Pos(List<BPAddress> bpAddresses, String bo_billTo) {
        int po = -1;
        for (BPAddress obj : bpAddresses) {
            if (obj.getRowNum().trim().equalsIgnoreCase(bo_billTo.trim())) {
                po = bpAddresses.indexOf(obj);
                break;
            }
        }
        return po;
    }

    private static int getAddres_ShipRow_Pos(List<BPAddress> bpAddresses, String bo_billTo) {
        int po = -1;
        for (BPAddress obj : bpAddresses) {
            if (obj.getRowNum().trim().equalsIgnoreCase(bo_billTo.trim())) {
                po = bpAddresses.indexOf(obj);
                break;
            }
        }
        return po;
    }


    private boolean validation(String billingName, String bAddress) {
        if (billingName.isEmpty()) {
            Globals.showMessage(getActivity(), getResources().getString(R.string.can_not_empty));
            return false;
        } else if (bAddress.isEmpty()) {
            Globals.showMessage(getActivity(), getResources().getString(R.string.can_not_empty));
            return false;
        }
        return true;

    }

}