package com.cinntra.vistadelivery.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.SelectedItems;
import com.cinntra.vistadelivery.adapters.ContactPersonAdapter;
import com.cinntra.vistadelivery.adapters.CountryAdapter;
import com.cinntra.vistadelivery.adapters.PaymentAdapter;
import com.cinntra.vistadelivery.adapters.SalesEmployeeAdapter;
import com.cinntra.vistadelivery.adapters.SalesEmployeeAutoAdapter;
import com.cinntra.vistadelivery.adapters.StateAdapter;
import com.cinntra.vistadelivery.adapters.bpAdapters.ContactPersonAutoAdapter;
import com.cinntra.vistadelivery.databinding.EditQuotationBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.AddressExtension;
import com.cinntra.vistadelivery.model.BPModel.BPAllFilterRequestModel;
import com.cinntra.vistadelivery.model.BPModel.BranchOneResponseModel;
import com.cinntra.vistadelivery.model.ContactPerson;
import com.cinntra.vistadelivery.model.ContactPersonData;
import com.cinntra.vistadelivery.model.CountryData;
import com.cinntra.vistadelivery.model.CountryResponse;
import com.cinntra.vistadelivery.model.DocumentLines;
import com.cinntra.vistadelivery.model.OpportunityModels.OppAddressResponseModel;
import com.cinntra.vistadelivery.model.PayMentTerm;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.QuotationOneAPiModel;
import com.cinntra.vistadelivery.model.QuotationDocumentLines;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.ResponseCompanyBranchAllFilter;
import com.cinntra.vistadelivery.model.SalesEmployeeItem;
import com.cinntra.vistadelivery.model.StateData;
import com.cinntra.vistadelivery.model.StateRespose;
import com.cinntra.vistadelivery.model.TokenExpireModel;
import com.cinntra.vistadelivery.model.UpdateQTDocumentLines;
import com.cinntra.vistadelivery.model.UpdateQuotationModel;
import com.cinntra.vistadelivery.model.orderModels.OrderDetailResponseModel;
import com.cinntra.vistadelivery.room.CountriesDatabase;
import com.cinntra.vistadelivery.spinneradapter.BranchBusinessPartnerTypeSearchableSpinnerAdapter;
import com.cinntra.vistadelivery.spinneradapter.BranchTypeAllSearchableSpinnerAdapter;
import com.cinntra.vistadelivery.viewModel.ItemViewModel;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class Order_Update_Fragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Order";
    public static int SelectItemCode = 102;
    private String QT_ID = "";
    public static String CardValue;
    public static String salePCode;
    UpdateQuotationModel addQuotationObj;

    CountriesDatabase db = CountriesDatabase.getDatabase(getActivity());

    FragmentActivity act;


    EditQuotationBinding binding;

    public static boolean ESCAPED = true;
    public static boolean DISABLED = true;
    String[] shippinngType;
    String billtoState, billtoCountrycode, billtoCountryName, shiptoState, shiptoCountrycode, shiptoCountryName, shiptoStateCode, billtoStateCode;
    CountryAdapter countryAdapter;
    StateAdapter stateAdapter, shipstateAdapter;
    ArrayList<StateData> stateList = new ArrayList<>();
    ArrayList<StateData> shipstateList = new ArrayList<>();
    ArrayList<CountryData> localData = new ArrayList<>();

    public Order_Update_Fragment() {
        //Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Order_Update_Fragment newInstance(String param1, String param2) {
        Order_Update_Fragment fragment = new Order_Update_Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    OrderDetailResponseModel.Data quotationItem;

    String orderId = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle b = getArguments();
            orderId = b.getString("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        act = getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        binding = EditQuotationBinding.inflate(getLayoutInflater());
        View v = inflater.inflate(R.layout.edit_quotation, container, false);
        //  ButterKnife.bind(this, v);

        //todo order
        Globals.IsQUOTEORDER = true;
        Globals.ISORDER = true;

        if (Globals.ISORDER) {
            binding.quotationGeneralContent.linearHidedataFromQuotation.setVisibility(View.VISIBLE);

        } else {

        }

        binding.quotationGeneralContent.quoNamevalue.setClickable(false);
        binding.quotationGeneralContent.quoNamevalue.setFocusable(false);



        if (Globals.checkInternet(getActivity())) {
            callOrderOneApi();
            //todo calling country api here---
            callCountryApi();
            callPaymentApi();
            setUpBusinessPartnerbranchTypeSpinner();

        }

        binding.quotationGeneralContent.purchaseDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.disablePastSelectDate(getContext(),binding.quotationGeneralContent.purchaseDateValue);
            }
        });



        shippinngType = getActivity().getResources().getStringArray(R.array.shippingType);
        binding.quotationGeneralContent.companyNameCard.setVisibility(View.VISIBLE);
        binding.quotationGeneralContent.oppView.setVisibility(View.GONE);
        binding.headerBottomroundEdit.headTitle.setText(getString(R.string.details_order));
        binding.quotationGeneralContent.quoteInformation.setText(getResources().getString(R.string.order_information));
        binding.quotationGeneralContent.bpView.setVisibility(View.GONE);
        binding.tab1.setOnClickListener(this);
        binding.tab2.setOnClickListener(this);
        binding.tab3.setOnClickListener(this);
        binding.general.setOnClickListener(this);
        binding.quotationTotalContent.itemFrame.setOnClickListener(this);
        binding.headerBottomroundEdit.backPress.setOnClickListener(this);
       // setDisable();

        //todo comment

        //     setData();


        //todo item select of contact person..
        binding.quotationGeneralContent.acContactPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactPersonData partnerData = ContactEmployeesList.get(position);
                if (partnerData != null) {
                    ContactPerson = partnerData.getFirstName();
                    ContactPersonCode = partnerData.getInternalCode();
                    binding.quotationGeneralContent.acContactPerson.setText(partnerData.getFirstName());
                }
            }
        });

        binding.quotationGeneralContent.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    binding.quotationGeneralContent.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                    salesEmployeeCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();
                    salesEmployeeName = salesEmployeeItemList.get(position).getSalesEmployeeName();
                } else {
                    binding.quotationGeneralContent.acSalesEmployee.setText("");
                    salesEmployeeCode = "";
                    salesEmployeeName = "";
                }
            }
        });

        binding.quotationAddressContent.addressSection.checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.quotationAddressContent.addressSection.shipBlock.setVisibility(View.VISIBLE);
                } else {
                    binding.quotationAddressContent.addressSection.shipBlock.setVisibility(View.GONE);
                }
            }
        });


        binding.quotationGeneralContent.paymentTermValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (getPaymenterm.size() > 0)
                    payment_term = getPaymenterm.get(position).getGroupNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (getPaymenterm.size() > 0)
                    payment_term = getPaymenterm.get(0).getGroupNumber();

            }
        });

        binding.quotationAddressContent.addressSection.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oppname = binding.quotationGeneralContent.opportunityNameValue.getText().toString().trim();
                String poDate = binding.quotationGeneralContent.edCreatedDate.getText().toString().trim();
                String vDate = binding.quotationGeneralContent.validTillValue.getText().toString().trim();
                String docDate = binding.quotationGeneralContent.documentDateValue.getText().toString().trim();
                String discount = binding.quotationTotalContent.discontValue.getText().toString().trim();
                String remark = binding.quotationGeneralContent.remarkValue.getText().toString().trim();

                if (valiadtion(ContactPersonCode, poDate, vDate, docDate, remark)) {
                    getQuotationDocLin();


                    addQuotationObj.setOpportunityName(oppname);
                    addQuotationObj.setQuotationID(quotationItem.getU_QUOTID());
                    addQuotationObj.setSalesPerson(ContactPersonCode);
                    addQuotationObj.setSalesPersonCode(salesPersonCode);
                    addQuotationObj.setPostingDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(poDate));
                    addQuotationObj.setValidDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(vDate));
                    addQuotationObj.setDiscountPercent(Float.valueOf(discount));
                    addQuotationObj.setDocumentDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(docDate));
                    addQuotationObj.setRemarks(remark);
                    addQuotationObj.setUpdateDate(Globals.getTodaysDate());
                    addQuotationObj.setUpdateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateDate(Globals.getTodaysDate());
                    addQuotationObj.setId(quotationItem.getId());


                    //todo new key
                    addQuotationObj.setU_QUOTID(quotationItem.getU_QUOTID());
                    addQuotationObj.setU_QUOTNM(quotationItem.getU_QUOTNM());
                    addQuotationObj.setDepartement("2");
                    addQuotationObj.setPRID("");

                    addQuotationObj.setBPLID(branchTypeSelected);
                    addQuotationObj.setPaymentGroupCode(payment_term);
                    addQuotationObj.setPONumber(binding.quotationGeneralContent.etPurchaseOrderNumber.getText().toString().trim());
                    addQuotationObj.setPODate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(binding.quotationGeneralContent.purchaseDateValue.getText().toString().trim()));


                    if (OppId.equalsIgnoreCase("")) {
                        addQuotationObj.setU_OPPID("");
                    } else {
                        addQuotationObj.setU_OPPID(OppId.trim());
                    }

                    /***** Total items calculations *****/
                    addQuotationObj.setDocTotal(Float.parseFloat(binding.quotationTotalContent.totalBeforeDiscontValue.getText().toString()));

                    if (!discount.isEmpty())
                        addQuotationObj.setDiscountPercent(Float.parseFloat(discount));
                    else addQuotationObj.setDiscountPercent(0);


                    //todo add items lines.
                    ArrayList<DocumentLines> documentLineArrayList = new ArrayList<>();


                    for (int j = 0; j < Globals.SelectedItems.size(); j++) {
                        DocumentLines documentLines = new DocumentLines();

                        documentLines.setQuantity(quotationItem.getDocumentLines().get(j).getQuantity());
                        documentLines.setDiscountPercent(quotationItem.getDocumentLines().get(j).getDiscountPercent());
                        documentLines.setUnitPrice(quotationItem.getDocumentLines().get(j).getUnitPrice());
                        documentLines.setItemCode(quotationItem.getDocumentLines().get(j).getItemCode());
                        documentLines.setItemDescription(quotationItem.getDocumentLines().get(j).getItemDescription());
                        documentLines.setTaxCode(quotationItem.getDocumentLines().get(j).getTaxCode());
                        documentLines.setTax(quotationItem.getDocumentLines().get(j).getTax());//
                        documentLines.setTaxRate(quotationItem.getDocumentLines().get(j).getTaxRate());//

                        documentLineArrayList.addAll(Collections.singleton(documentLines));

                    }


                    addQuotationObj.setDocumentLines(documentLineArrayList);

                    //todo new keys
                    addQuotationObj.setFreightCharge(binding.quotationTotalContent.etFrieghtCharge.getText().toString().trim());


                    AddressExtension addressExtension = new AddressExtension();
                    addressExtension.setShipToBuilding(binding.quotationAddressContent.addressSection.shippingNameValue.getText().toString());
                    addressExtension.setShipToStreet(binding.quotationAddressContent.addressSection.shippingAddressValue.getText().toString());
                    addressExtension.setShipToCity(binding.quotationAddressContent.addressSection.shipcityValue.getText().toString());
                    addressExtension.setShipToZipCode(binding.quotationAddressContent.addressSection.zipcodeValue2.getText().toString());
                    addressExtension.setShipToState(shiptoStateCode);
                    addressExtension.setShipToCountry(shiptoCountrycode);   //countryCode2
                    addressExtension.setU_SSTATE(shiptoState);
                    addressExtension.setU_SCOUNTRY(shiptoCountryName);
                    addressExtension.setU_SHPTYPS(ship_shiptype);
                    addressExtension.setU_OPPRNM("");
                    addressExtension.setBillToBuilding(binding.quotationAddressContent.addressSection.billingNameValue.getText().toString());
                    addressExtension.setBillToStreet(binding.quotationAddressContent.addressSection.billingAddressValue.getText().toString());
                    addressExtension.setBillToCity(binding.quotationAddressContent.addressSection.cityValue.getText().toString());
                    addressExtension.setBillToZipCode(binding.quotationAddressContent.addressSection.zipCodeValue.getText().toString());
                    addressExtension.setBillToState(billtoStateCode);
                    addressExtension.setBillToCountry(billtoCountrycode);       //countryCode2
                    addressExtension.setU_BSTATE(billtoState);
                    addressExtension.setU_BCOUNTRY(billtoCountryName);
                    addressExtension.setU_SHPTYPB(billshiptype);
                    addressExtension.setId(quotationItem.getAddressExtension().getId());
                    addQuotationObj.setAddressExtension(addressExtension);

                    //  addQuotationObj.setDocumentLines(postJson(quotationItem.getDocumentLines()));
//                    addQuotationObj.setDocumentLines(postJsonCopy(Globals.SelectedItems,quotationItem.getDocumentLines()));
                    //   addQuotationObj.setDocumentLines(Globals.SelectedItems);


                    if (Globals.checkInternet(getActivity())) {

                        updateQuotation(QT_ID, addQuotationObj);
                    }
                }
            }
        });



        return binding.getRoot();
    }

    private void callOrderOneApi() {
        binding.loader.loader.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", orderId);

        Call<OrderDetailResponseModel> call = NewApiClient.getInstance().getApiService(requireContext()).getOrderOneDetail(jsonObject);
        call.enqueue(new Callback<OrderDetailResponseModel>() {
            @Override
            public void onResponse(Call<OrderDetailResponseModel> call, Response<OrderDetailResponseModel> response) {
                try {
                    if (response.isSuccessful()) {
                        binding.loader.loader.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().getStatus() == 200) {

                                if (response.body().getData().size()>0){
                                    quotationItem = response.body().getData().get(0);
                                }


                                // setDefaultData(response.body().getData().get(0));
                                setData();
                            }

                        } else {
                            binding.loader.loader.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.code() == 401) {
                        binding.loader.loader.setVisibility(View.GONE);
                        if (response.body().getStatus() == 301) {
                            Gson gson = new GsonBuilder().create();
                            TokenExpireModel mError = new TokenExpireModel();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, TokenExpireModel.class);
                                Toast.makeText(getActivity(), mError.getDetail(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen(getActivity());
                            } catch (IOException e) {
                                // handle failure to read error
                            }

                        }
                    } else {
                        binding.loader.loader.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Gson gson = new GsonBuilder().create();
                        QuotationOneAPiModel mError = new QuotationOneAPiModel();
                        try {
                            String s = response.errorBody().string();
                            mError = gson.fromJson(s, QuotationOneAPiModel.class);
                            Toast.makeText(getActivity(), mError.getError().getMessage(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    binding.loader.loader.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponseModel> call, Throwable t) {
                Log.e("TAG_APi_failure", "onFailure: " + t.getMessage());
                binding.loader.loader.setVisibility(View.GONE);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    String payment_term = "";

    List<PayMentTerm> getPaymenterm = new ArrayList<>();

    private void callPaymentApi() {
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getPaymentList().observe(act, new Observer<List<PayMentTerm>>() {
            @Override
            public void onChanged(List<PayMentTerm> payMentTermList) {
                if (payMentTermList == null || payMentTermList.size() == 0) {
                    Globals.setmessage(act);
                } else {
                    getPaymenterm.clear();
                    getPaymenterm = payMentTermList;
                    binding.quotationGeneralContent.paymentTermValue.setAdapter(new PaymentAdapter(act, getPaymenterm));
                    if (getPaymenterm.size()>0){
                        payment_term = getPaymenterm.get(0).getGroupNumber();
                    }


                }

            }
        });

      /*  if (MainActivity.paymentTermListFromLocal == null || MainActivity.paymentTermListFromLocal.size() == 0) {
            Globals.setmessage(act);
        } else {
            getPaymenterm.clear();
            getPaymenterm = MainActivity.paymentTermListFromLocal;
            binding.fragmentAddpartnergeneral.paymentTermValue.setAdapter(new PaymentAdapter(act, getPaymenterm));
            payment_term = getPaymenterm.get(0).getGroupNumber();

        }*/

        //  callUTypeApi();
    }


    String branchTypeSelected = "";
    String cardCode = "";
    ArrayList<ResponseCompanyBranchAllFilter.Datum> branchTypeDataList = new ArrayList<>();

    private void setUpBusinessPartnerbranchTypeSpinner() {
        binding.quotationGeneralContent.saerchableSpinnerBranch.setTitle("Branch");
        binding.quotationGeneralContent.saerchableSpinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SPINNER SEARCH", "onItemSelected: " + branchTypeDataList.get(i).getbPLName());
                branchTypeSelected = branchTypeDataList.get(i).getbPLId();
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

        Call<ResponseCompanyBranchAllFilter> call = NewApiClient.getInstance().getApiService(requireContext()).getBranchAllFilter(requestModel);
        call.enqueue(new Callback<ResponseCompanyBranchAllFilter>() {
            @Override
            public void onResponse(Call<ResponseCompanyBranchAllFilter> call, Response<ResponseCompanyBranchAllFilter> response) {

                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {


                        branchTypeDataList.clear();
                        branchTypeDataList.addAll(response.body().getData());
                        if (branchTypeDataList.size()>0){
                            branchTypeSelected = branchTypeDataList.get(0).getbPLId();
                        }

                        BranchBusinessPartnerTypeSearchableSpinnerAdapter sourceSearchableSpinnerAdapter = null;
                        try {
                            sourceSearchableSpinnerAdapter = new BranchBusinessPartnerTypeSearchableSpinnerAdapter(requireContext(), branchTypeDataList);
                            binding.quotationGeneralContent.saerchableSpinnerBranch.setAdapter(sourceSearchableSpinnerAdapter);

                        } catch (Exception e) {

                        }


                        //todo item click on assign to---
                    } else {
                        try {
                            Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }

                    }


                } else {
                    try {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseCompanyBranchAllFilter> call, Throwable t) {

                try {
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }
            }
        });


    }


    String branchTypeBillingSelected = "";
    String branchTypeShippingSelected = "";
    ArrayList<OppAddressResponseModel.Data> branchTypeAddressDataList = new ArrayList<>();

    private void setUpBranchAllSpinner() {
        binding.quotationAddressContent.addressSection.saerchableSpinnerBillingAddress.setTitle("Branch");
        binding.quotationAddressContent.addressSection.saerchableSpinnerShippingAddress.setTitle("Branch");
        binding.quotationAddressContent.addressSection.saerchableSpinnerBillingAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SPINNER SEARCH", "onItemSelected: " + branchTypeAddressDataList.get(i).getAddressName());
                branchTypeBillingSelected = branchTypeAddressDataList.get(i).getAddressName();
                callBranchOneAPi(branchTypeAddressDataList.get(i).id, "bill");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.quotationAddressContent.addressSection.saerchableSpinnerShippingAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SPINNER SEARCH", "onItemSelected: " + branchTypeAddressDataList.get(i).getAddressName());
                branchTypeShippingSelected = branchTypeAddressDataList.get(i).getAddressName();
                callBranchOneAPi(branchTypeAddressDataList.get(i).id, "ship");
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
        JsonObject jsonObject = new JsonObject();

    /*    {
            "BPCode": "C6",
                "PageNo": 1,
                "maxItem": "All",
                "order_by_field": "id",
                "order_by_value": "asc",
                "SearchText": "",
                "field": {}
        }*/


        jsonObject.addProperty("BPCode", quotationItem.getCardCode());
        jsonObject.addProperty("PageNo", 1);
        jsonObject.addProperty("maxItem", "All");
        jsonObject.addProperty("SearchText", "");
        jsonObject.addProperty("order_by_field", "id");
        jsonObject.addProperty(Globals.orderbyvalue, "asc");
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


                        branchTypeAddressDataList.clear();
                        branchTypeAddressDataList.addAll(response.body().getData());

                        if (branchTypeAddressDataList.size()>0){
                            branchTypeBillingSelected = branchTypeAddressDataList.get(0).getAddressName();
                            branchTypeShippingSelected = branchTypeAddressDataList.get(0).getAddressName();
                            BranchTypeAllSearchableSpinnerAdapter sourceSearchableSpinnerAdapter = new BranchTypeAllSearchableSpinnerAdapter(requireContext(), branchTypeAddressDataList);

                            binding.quotationAddressContent.addressSection.saerchableSpinnerBillingAddress.setAdapter(sourceSearchableSpinnerAdapter);
                            binding.quotationAddressContent.addressSection.saerchableSpinnerShippingAddress.setAdapter(sourceSearchableSpinnerAdapter);

                        }


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
    private void callBranchOneAPi(String id, String flag) {
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

                        if (response.body().getData().size()>0){
                            if (flag.equalsIgnoreCase("bill")) {
                                setDefaultDataByBranchOne(response.body().getData().get(0));
                            } else {
                                setDefaultDataByBranchOneShip(response.body().getData().get(0));
                            }
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
        binding.quotationAddressContent.addressSection.billingNameValue.setText(data.AddressName);

        binding.quotationAddressContent.addressSection.billingAddressValue.setText(data.getStreet());
        binding.quotationAddressContent.addressSection.zipCodeValue.setText(data.getZipCode());
        binding.quotationAddressContent.addressSection.acCountry.setText(data.getU_COUNTRY());
        binding.quotationAddressContent.addressSection.acBillToState.setText(data.getU_STATE());
        binding.quotationAddressContent.addressSection.cityValue.setText(data.getCity());

        billtoCountrycode = data.getCountry();
        billtoStateCode = data.getState();
        //   BranchType = branchOneResponseModel_gl.getBranchType();

        billtoCountryName = data.getU_COUNTRY();
        billtoState = data.getU_STATE();
        billshiptype = data.getU_SHPTYP();
        binding.quotationAddressContent.addressSection.shippingSpinner.setSelection(Globals.getShipTypePo(shippinngType, billshiptype));


    }


    private void setDefaultDataByBranchOneShip(BranchOneResponseModel.Data data) {
        callShipToStateApi(data.getCountry());
        binding.quotationAddressContent.addressSection.shippingNameValue.setText(data.AddressName);

        binding.quotationAddressContent.addressSection.shippingAddressValue.setText(data.getStreet());
        binding.quotationAddressContent.addressSection.zipcodeValue2.setText(data.getZipCode());
        binding.quotationAddressContent.addressSection.acShipCountry.setText(data.getU_COUNTRY());
        binding.quotationAddressContent.addressSection.acShipToState.setText(data.getU_STATE());
        binding.quotationAddressContent.addressSection.shipcityValue.setText(data.getCity());

        shiptoCountrycode = data.getCountry();
        shiptoStateCode = data.getState();
        //   BranchType = branchOneResponseModel_gl.getBranchType();

        shiptoCountryName = data.getU_COUNTRY();
        shiptoState = data.getU_STATE();
        ship_shiptype = data.getU_SHPTYP();
        binding.quotationAddressContent.addressSection.shippingSpinner2.setSelection(Globals.getShipTypePo(shippinngType, ship_shiptype));


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
                        binding.quotationAddressContent.addressSection.acBillToState.setAdapter(stateAdapter);
                     /*   billtoState = stateList.get(0).getName();
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

                        shipstateList.clear();
                        if (response.body().getData().size() > 0) {
                            shipstateList.addAll(response.body().getData());
                        } else {
                            StateData sta = new StateData();
                            sta.setName("Select State");
                            shipstateList.add(sta);
                        }
                        stateAdapter = new StateAdapter(getContext(), R.layout.drop_down_textview, shipstateList);
                        binding.quotationAddressContent.addressSection.acShipToState.setAdapter(stateAdapter);
                       /* shiptoState = shipstateList.get(0).getName();
                        shiptoStateCode = shipstateList.get(0).getCode();*/
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


    private void setDisable() {

        binding.headerBottomroundEdit.add.setOnClickListener(this);
        binding.headerBottomroundEdit.add.setVisibility(View.INVISIBLE);
        binding.quotationGeneralContent.bpView.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.quotationGeneralContent.opportunityNameValue.getApplicationWindowToken(), 0);


        binding.quotationGeneralContent.submit.setClickable(false);
        binding.quotationGeneralContent.submit.setFocusable(false);


        binding.quotationGeneralContent.opportunityNameValue.setFocusableInTouchMode(false);
        binding.quotationGeneralContent.opportunityNameValue.setFocusable(false);
        binding.quotationGeneralContent.opportunityNameValue.setClickable(false);

     /*   binding.quotationGeneralContent.contactPersonSpinner.setEnabled(false);
        binding.quotationGeneralContent.salesemployeeSpinner.setEnabled(false);

        binding.quotationGeneralContent.postingValue.setClickable(false);
        binding.quotationGeneralContent.postingValue.setFocusable(false);
        binding.quotationGeneralContent.postingValue.setFocusableInTouchMode(false);
*/
        binding.quotationGeneralContent.postingDate.setFocusable(false);
        binding.quotationGeneralContent.postingDate.setClickable(false);
        binding.quotationGeneralContent.postingDate.setFocusableInTouchMode(false);
        binding.quotationGeneralContent.postingDate.setEnabled(false);

        binding.quotationGeneralContent.validTillValue.setFocusableInTouchMode(false);
        binding.quotationGeneralContent.validTillValue.setClickable(false);
        binding.quotationGeneralContent.validTillValue.setFocusable(false);


        binding.quotationGeneralContent.validDate.setClickable(false);
        binding.quotationGeneralContent.validDate.setFocusable(false);
        binding.quotationGeneralContent.validDate.setEnabled(false);
        binding.quotationGeneralContent.validDate.setFocusableInTouchMode(false);

        binding.quotationGeneralContent.documentDateValue.setFocusable(false);
        binding.quotationGeneralContent.documentDateValue.setClickable(false);
        binding.quotationGeneralContent.documentDateValue.setFocusableInTouchMode(false);

        binding.quotationGeneralContent.documentDate.setFocusable(false);
        binding.quotationGeneralContent.documentDate.setClickable(false);
        binding.quotationGeneralContent.documentDate.setEnabled(false);
        binding.quotationGeneralContent.documentDate.setFocusableInTouchMode(false);

        binding.quotationGeneralContent.remarkValue.setFocusableInTouchMode(false);
        binding.quotationGeneralContent.remarkValue.setFocusable(false);
        binding.quotationGeneralContent.remarkValue.setClickable(false);

        binding.quotationTotalContent.totalBeforeDiscontValue.setClickable(false);
        binding.quotationTotalContent.totalBeforeDiscontValue.setFocusable(false);
        binding.quotationTotalContent.totalBeforeDiscontValue.setFocusableInTouchMode(false);

        binding.quotationTotalContent.discontValue.setFocusableInTouchMode(false);
        binding.quotationTotalContent.discontValue.setFocusable(false);
        binding.quotationTotalContent.discontValue.setClickable(false);


        binding.quotationTotalContent.nextButton.setFocusable(false);
        binding.quotationTotalContent.nextButton.setClickable(false);


        binding.quotationAddressContent.addressSection.shippingNameValue.setFocusableInTouchMode(false);
        binding.quotationAddressContent.addressSection.shippingNameValue.setFocusable(false);
        binding.quotationAddressContent.addressSection.shippingNameValue.setClickable(false);

        binding.quotationAddressContent.addressSection.zipcodeValue2.setClickable(false);
        binding.quotationAddressContent.addressSection.zipcodeValue2.setFocusable(false);
        binding.quotationAddressContent.addressSection.zipcodeValue2.setFocusableInTouchMode(false);

        binding.quotationAddressContent.addressSection.shippingSpinner2.setEnabled(false);

        binding.quotationAddressContent.addressSection.doneButton.setClickable(true);
        binding.quotationAddressContent.addressSection.doneButton.setFocusable(true);


        binding.quotationAddressContent.addressSection.shippingAddressValue.setFocusableInTouchMode(false);
        binding.quotationAddressContent.addressSection.shippingAddressValue.setFocusable(false);
        binding.quotationAddressContent.addressSection.shippingAddressValue.setClickable(false);

        binding.quotationAddressContent.addressSection.billingAddressValue.setClickable(false);
        binding.quotationAddressContent.addressSection.billingAddressValue.setFocusable(false);
        binding.quotationAddressContent.addressSection.billingAddressValue.setFocusableInTouchMode(false);

        binding.quotationAddressContent.addressSection.billingNameValue.setFocusableInTouchMode(false);
        binding.quotationAddressContent.addressSection.billingNameValue.setFocusable(false);
        binding.quotationAddressContent.addressSection.billingNameValue.setClickable(false);

        binding.quotationAddressContent.addressSection.zipCodeValue.setClickable(false);
        binding.quotationAddressContent.addressSection.zipCodeValue.setFocusable(false);
        binding.quotationAddressContent.addressSection.zipCodeValue.setFocusableInTouchMode(false);
       /* binding.quotationAddressContent.addressSection.countryValue.setClickable(false);
        binding.quotationAddressContent.addressSection.shipStateValue.setClickable(false);
        binding.quotationAddressContent.addressSection.shipCountryValue.setClickable(false);
        binding.quotationAddressContent.addressSection.stateValue.setClickable(false);*/
        binding.quotationAddressContent.addressSection.shippingSpinner.setEnabled(false);

        //    binding.headerBottomroundEdit.add.setVisibility(View.VISIBLE);
        binding.headerBottomroundEdit.ok.setVisibility(View.GONE);

        ESCAPED = true;
        DISABLED = false;

    }

    String ContactPersonCode = "";
    String salesPersonCode = "";
    String billshiptype = "";
    String ship_shiptype = "";
    String cardName = "";
    String ContactPerson = "";
    public String salesEmployeeCode = "";
    public String salesEmployeeName = "";
    String OppId = "";

    String FLAG = "";

    private void setData() {
        OppId = quotationItem.getU_OPPID();
        binding.quotationTotalContent.etFrieghtCharge.setText(quotationItem.getFreightCharge());


        cardCode = quotationItem.getCardCode();
        cardName = quotationItem.getCardName();
        setUpBranchAllSpinner();
        billtoCountrycode = quotationItem.getAddressExtension().getBillToCountry();
        billtoStateCode = quotationItem.getAddressExtension().getBillToState();
        billtoCountryName = quotationItem.getAddressExtension().getU_BCOUNTRY();
        billtoState = quotationItem.getAddressExtension().getU_BSTATE();
        shiptoCountrycode = quotationItem.getAddressExtension().getShipToCountry();
        shiptoStateCode = quotationItem.getAddressExtension().getShipToState();
        shiptoCountryName = quotationItem.getAddressExtension().getU_SCOUNTRY();
        shiptoState = quotationItem.getAddressExtension().getU_SSTATE();
        binding.quotationAddressContent.addressSection.cityValue.setText(quotationItem.getAddressExtension().getBillToCity());
        binding.quotationGeneralContent.quoNamevalue.setText(quotationItem.getU_QUOTNM());
        binding.quotationAddressContent.addressSection.shipcityValue.setText(quotationItem.getAddressExtension().getShipToCity());
        binding.quotationGeneralContent.etPurchaseOrderNumber.setText(quotationItem.getPONumber());
        binding.quotationGeneralContent.purchaseDateValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getPoDate()));


        // callStateApi(billtoCountrycode, "billto");

        //  callStateApi(shiptoCountrycode, "shipto");


        localData = (ArrayList) db.myDataDao().getAll();
        Log.d(TAG, "setData: ");

        if (localData.isEmpty()) {
            Log.d(TAG, "emptyLocal Data: ");
            callCountryApi();
        }
        Log.d(TAG, "gettingdata: ");
        countryAdapter = new CountryAdapter(getContext(), localData);


       /* binding.quotationAddressContent.addressSection.countryValue.setAdapter(countryAdapter);
        binding.quotationAddressContent.addressSection.shipCountryValue.setAdapter(countryAdapter);

        binding.quotationAddressContent.addressSection.countryValue.setSelection(Globals.getCountrypos(localData, billtoCountryName));
        binding.quotationAddressContent.addressSection.shipCountryValue.setSelection(Globals.getCountrypos(localData, shiptoCountryName));*/
        QT_ID = quotationItem.getDocEntry();
        if (quotationItem.getContactPersonCodeDetails().size()>0){
            ContactPersonCode = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getId());
        }

        if (quotationItem.getSalesPersonCodeDetails().size()>0){
            salesPersonCode = quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeCode();
        }




        if (!quotationItem.getContactPersonCodeDetails().isEmpty()) {
            binding.quotationGeneralContent.acContactPerson.setText(quotationItem.getContactPersonCodeDetails().get(0).getFirstName());
            ContactPersonCode = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getId());
            ContactPerson = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getFirstName());
        } else {
            binding.quotationGeneralContent.acContactPerson.setText("NA");
        }

        if (!quotationItem.getSalesPersonCode().isEmpty()) {
            binding.quotationGeneralContent.acSalesEmployee.setText(quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeName());
            salesEmployeeCode = quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeCode();
            salesEmployeeName = quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeName();
        } else {
            binding.quotationGeneralContent.acSalesEmployee.setHint("NA");
            salesEmployeeCode = "";
            salesEmployeeName = "";
        }

        if (Globals.checkInternet(getActivity())) {
            callContactEmployeeApi(quotationItem.getCardCode());
        }
        //callContactApi(quotationItem.getCardCode());

        Globals.SelectedItems.clear();
       /* Globals.contentList.clear();
        Globals.contentList.addAll(quotationItem.getDocumentLines());*/


        /*********** Set Data In Header Section**************/

//          qt_status.setText(Globals.getStatus(quotationItem.getDocumentStatus()));

       /* if (Globals.viewStatus(quotationItem.getDocumentStatus()) == "Close"){
            qt_status.setText("Closed");
            qt_status.setBackground(getResources().getDrawable(R.drawable.closeroundsaffron));

        }else{
            qt_status.setText("Open");
            qt_status.setBackground(getResources().getDrawable(R.drawable.openroundedgreen));
        }*/

        Total_Before_Disscount = Total_Before_Disscount(quotationItem.getDocumentLines());
        Globals.SelectedItems.addAll(quotationItem.getDocumentLines());

        String totalBefroe = String.valueOf(Globals.foo(Globals.calculateTotalOfItemAfterHeaderDiswithFreightCharge(Globals.SelectedItems, Double.parseDouble(quotationItem.getDiscountPercent()), quotationItem.getFreightCharge())));

        totalAfterItemDiscount = Double.parseDouble(String.valueOf(Globals.foo(Globals.calculateTotalOfItem(Globals.SelectedItems))));

        binding.quotationTotalContent.totalBeforeDiscontValue.setText(String.valueOf(Globals.foo(Double.parseDouble(totalBefroe))));

        String discount = "";
        if (quotationItem.getDiscountPercent().isEmpty()) {
            binding.quotationTotalContent.discontValue.setText("0.0");
        } else {
            try {
                discount = quotationItem.getDiscountPercent();
                double doubleValue = Double.parseDouble(discount);
                Log.e(TAG, "setDefaultData: " + "discountSet");

                FLAG = "SET_DATA";
                binding.quotationTotalContent.discontValue.setText(String.valueOf(doubleValue));
            } catch (NumberFormatException e) {
                // Handle the exception, e.g., log an error or inform the user
                e.printStackTrace();
            }
        }

        enableTextWatcher();

        // Set the default value without triggering the TextWatcher
        setDefaultValue(discount);


        binding.quotationGeneralContent.companyName.setText(quotationItem.getCardName());
        binding.quotationGeneralContent.validUntillDate.setText(getResources().getString(R.string.valid_untill) + ": " + quotationItem.getDocDueDate());

        //  binding.quotationTotalContent.discontValue.setText(quotationItem.getDiscountPercent());//
        //  binding.quotationTotalContent.totalBeforeDiscontValue.setText(Globals.getAmmount(quotationItem.getDocCurrency(), String.valueOf(Total_Before_Disscount)));
        //total_value.setText(Globals.getAmmount(quotationItem.getDocCurrency(),quotationItem.getDocTotal())+" ( "+Globals.getAmmount(quotationItem.getDocCurrency(),quotationItem.getDocTotalSys())+" )");
        binding.quotationTotalContent.totalBeforeDiscontValue.setText(quotationItem.getDocCurrency() + " " + Globals.calculatetotalofitem(Globals.SelectedItems));
        binding.quotationGeneralContent.documentDateValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getTaxDate()));
        binding.quotationGeneralContent.validTillValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getDocDueDate()));
        binding.quotationGeneralContent.edCreatedDate.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getDocDate()));
        binding.quotationGeneralContent.remarkValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getComments()));

        if (quotationItem.getAddressExtension().getBillToBuilding() != null)
            binding.quotationAddressContent.addressSection.billingNameValue.setText(quotationItem.getAddressExtension().getBillToBuilding());
        if (quotationItem.getAddressExtension().getBillToStreet() != null)
            binding.quotationAddressContent.addressSection.billingAddressValue.setText(quotationItem.getAddressExtension().getBillToStreet());
        if (quotationItem.getAddressExtension().getBillToZipCode() != null)
            binding.quotationAddressContent.addressSection.zipCodeValue.setText(quotationItem.getAddressExtension().getBillToZipCode());


        if (quotationItem.getAddressExtension().getShipToBuilding() != null)
            binding.quotationAddressContent.addressSection.shippingNameValue.setText(quotationItem.getAddressExtension().getShipToBuilding());
        if (quotationItem.getAddressExtension().getShipToStreet() != null)
            binding.quotationAddressContent.addressSection.shippingAddressValue.setText(quotationItem.getAddressExtension().getShipToStreet());
        if (quotationItem.getAddressExtension().getShipToZipCode() != null)
            binding.quotationAddressContent.addressSection.zipcodeValue2.setText(quotationItem.getAddressExtension().getShipToZipCode());

        billshiptype = quotationItem.getAddressExtension().getU_SHPTYPB();
        if (billshiptype != null)
            binding.quotationAddressContent.addressSection.shippingSpinner.setSelection(Globals.getShipTypePo(shippinngType, billshiptype));

        ship_shiptype = quotationItem.getAddressExtension().getU_SHPTYPS();
        if (ship_shiptype != null)
            binding.quotationAddressContent.addressSection.shippingSpinner2.setSelection(Globals.getShipTypePo(shippinngType, ship_shiptype));

        binding.quotationTotalContent.itemsCount.setText("Items (" + quotationItem.getDocumentLines().size() + ")");


        /*********** Set Data In Content Section**************/
        frameManager(binding.generalFrame, binding.totalFrame, binding.preparedFrame, binding.general, binding.total, binding.address);


        /******************* Data for Api use ************************/
        addQuotationObj = new UpdateQuotationModel();
        CardValue = quotationItem.getCardCode();
        if (quotationItem.getContactPersonCodeDetails().size()>0){
            salePCode = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getId());
        }

        addQuotationObj.setCardCode(CardValue);
        addQuotationObj.setSalesPerson(salePCode);


        /*binding.quotationGeneralContent.contactPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ContactPersonCode = ContactEmployeesList.get(position).getInternalCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        binding.quotationAddressContent.addressSection.shippingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                billshiptype = shippinngType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (billshiptype != null)
                    binding.quotationAddressContent.addressSection.shippingSpinner.setSelection(Globals.getShipTypePo(shippinngType, billshiptype));
            }
        });

        binding.quotationAddressContent.addressSection.shippingSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ship_shiptype = shippinngType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (ship_shiptype != null)
                    binding.quotationAddressContent.addressSection.shippingSpinner2.setSelection(Globals.getShipTypePo(shippinngType, ship_shiptype));
            }
        });


     /*   binding.quotationGeneralContent.salesemployeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0 && position > 0) {
                    salesPersonCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });*/

        salesEmployeeItemList = Globals.getSaleEmployeeArrayList(Globals.SalesEmployeeList);
        if (salesEmployeeItemList == null)
            callSalessApi();
        else {
            salesadapter = new SalesEmployeeAdapter(getActivity(), salesEmployeeItemList);
//            binding.quotationGeneralContent.salesemployeeSpinner.setAdapter(salesadapter);
//            if (!salesEmployeeItemList.isEmpty() && !salesPersonCode.isEmpty())
//                binding.quotationGeneralContent.salesemployeeSpinner.setSelection(Globals.getSelectedSalesP(salesEmployeeItemList, salesPersonCode));
        }


      /*  binding.quotationAddressContent.addressSection.countryValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                billtoCountrycode = MainActivity.countrylistFromLocal.get(position).getCode();
                billtoCountryName = MainActivity.countrylistFromLocal.get(position).getName();

                callStateApi(billtoCountrycode, "billto");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                billtoCountrycode = MainActivity.countrylistFromLocal.get(0).getCode();
                billtoCountryName = MainActivity.countrylistFromLocal.get(0).getName();
            }
        });

        binding.quotationAddressContent.addressSection.stateValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                billtoState = stateList.get(position).getName();
                billtoStateCode = stateList.get(position).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                billtoState = stateList.get(0).getName();
                billtoStateCode = stateList.get(0).getCode();
            }
        });


        binding.quotationAddressContent.addressSection.shipCountryValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shiptoCountrycode = MainActivity.countrylistFromLocal.get(position).getCode();
                shiptoCountryName = MainActivity.countrylistFromLocal.get(position).getName();

                callStateApi(shiptoCountrycode, "shipto");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                shiptoCountrycode = MainActivity.countrylistFromLocal.get(0).getCode();
                shiptoCountryName = MainActivity.countrylistFromLocal.get(0).getName();

            }
        });

        binding.quotationAddressContent.addressSection.shipStateValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shiptoState = shipstateList.get(position).getName();
                shiptoStateCode = shipstateList.get(position).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                shiptoState = shipstateList.get(0).getName();
                shiptoStateCode = shipstateList.get(0).getCode();
            }
        });*/


    }

    private void callCountryApi() {

        Call<CountryResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getCountryList();
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {
                        localData.clear();
                        localData.addAll(response.body().getData());
                    }


                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setDefaults() {

        binding.general.setOnClickListener(this);
        binding.total.setOnClickListener(this);
        binding.quotationGeneralContent.submit.setOnClickListener(this);
        binding.address.setOnClickListener(this);
        binding.headerBottomroundEdit.add.setOnClickListener(this);
        binding.quotationTotalContent.nextButton.setOnClickListener(this);
    //    binding.quotationAddressContent.addressSection.doneButton.setOnClickListener(this);
        binding.quotationGeneralContent.edCreatedDate.setOnClickListener(this);
        binding.quotationGeneralContent.postingDate.setOnClickListener(this);
        binding.quotationGeneralContent.postCal.setOnClickListener(this);
        binding.quotationGeneralContent.validTillValue.setOnClickListener(this);
        binding.quotationGeneralContent.validDate.setOnClickListener(this);
        binding.headerBottomroundEdit.ok.setOnClickListener(this);
        binding.quotationGeneralContent.validCal.setOnClickListener(this);
        binding.quotationGeneralContent.documentDateValue.setOnClickListener(this);
        binding.quotationGeneralContent.docCal.setOnClickListener(this);
        binding.quotationGeneralContent.documentDate.setOnClickListener(this);




    }

    private void callStateApi(String countryCode, String code) {

        StateData stateData = new StateData();
        stateData.setCountry(countryCode);
        Call<StateRespose> call = NewApiClient.getInstance().getApiService(requireContext()).getStateList(stateData);
        call.enqueue(new Callback<StateRespose>() {
            @Override
            public void onResponse(Call<StateRespose> call, Response<StateRespose> response) {

                if (response.code() == 200) {
                    if (code.equalsIgnoreCase("billto")) {


                        stateList.clear();
                        if (response.body().getData().size() > 0) {

                            stateList.addAll(response.body().getData());

                        } else {
                            StateData sta = new StateData();
                            sta.setName("Select State");
                            stateList.add(sta);
                        }
//                        stateAdapter = new StateAdapter(getContext(), stateList);
                       /* binding.quotationAddressContent.addressSection.stateValue.setAdapter(stateAdapter);

//                        stateAdapter.notifyDataSetChanged();
                        if (quotationItem.getAddressExtension().getU_BSTATE() != null)
                            binding.quotationAddressContent.addressSection.stateValue.setSelection(Globals.getStatePo(stateList, quotationItem.getAddressExtension().getU_BSTATE()));
*/
                    } else {
                        shipstateList.clear();
                        if (response.body().getData().size() > 0) {

                            shipstateList.addAll(response.body().getData());

                        } else {
                            StateData sta = new StateData();
                            sta.setName("Select State");
                            shipstateList.add(sta);
                        }
//                        shipstateAdapter = new StateAdapter(getContext(), shipstateList);
                       /* binding.quotationAddressContent.addressSection.shipStateValue.setAdapter(shipstateAdapter);
//                        shipstateAdapter.notifyDataSetChanged();
                        if (quotationItem.getAddressExtension().getU_SSTATE() != null)
                            binding.quotationAddressContent.addressSection.shipStateValue.setSelection(Globals.getStatePo(shipstateList, quotationItem.getAddressExtension().getU_SSTATE()));
                  */
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

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.add:
                if (ESCAPED) {
                    Globals.openKeyboard(getContext());
                    setEnable();
                    setDefaults();
                } else {
                    setDisable();
                }
                break;
            case R.id.ok:
                setDisable();
                String oppname = binding.quotationGeneralContent.opportunityNameValue.getText().toString().trim();
                String poDate = binding.quotationGeneralContent.edCreatedDate.getText().toString().trim();
                String vDate = binding.quotationGeneralContent.validTillValue.getText().toString().trim();
                String docDate = binding.quotationGeneralContent.documentDateValue.getText().toString().trim();
                String discount = binding.quotationTotalContent.discontValue.getText().toString().trim();
                String remark = binding.quotationGeneralContent.remarkValue.getText().toString().trim();

                if (valiadtion(ContactPersonCode, poDate, vDate, docDate, remark)) {
                    getQuotationDocLin();


                    addQuotationObj.setOpportunityName(oppname);
                    addQuotationObj.setSalesPerson(ContactPersonCode);
                    addQuotationObj.setSalesPersonCode(salesPersonCode);
                    addQuotationObj.setPostingDate(poDate);
                    addQuotationObj.setValidDate(vDate);
                    addQuotationObj.setDiscountPercent(Float.valueOf(discount));
                    addQuotationObj.setDocumentDate(docDate);
                    addQuotationObj.setRemarks(remark);
                    addQuotationObj.setUpdateDate(Globals.getTodaysDate());
                    addQuotationObj.setUpdateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateDate(Globals.getTodaysDate());
                    addQuotationObj.setId(quotationItem.getId());


                    //todo new key
                    addQuotationObj.setU_QUOTID(quotationItem.getU_QUOTID());
                    addQuotationObj.setU_QUOTNM(quotationItem.getU_QUOTNM());
                    addQuotationObj.setDepartement("2");
                    addQuotationObj.setPRID("");

                    addQuotationObj.setBPLID(branchTypeSelected);
                    addQuotationObj.setPaymentGroupCode(payment_term);
                    addQuotationObj.setPONumber(binding.quotationGeneralContent.etPurchaseOrderNumber.getText().toString().trim());
                    addQuotationObj.setPODate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(binding.quotationGeneralContent.purchaseDateValue.getText().toString().trim()));


                    if (OppId.equalsIgnoreCase("")) {
                        addQuotationObj.setU_OPPID("");
                    } else {
                        addQuotationObj.setU_OPPID(OppId.trim());
                    }

                    /***** Total items calculations *****/
                    addQuotationObj.setDocTotal(Float.parseFloat(binding.quotationTotalContent.totalBeforeDiscontValue.getText().toString()));

                    if (!discount.isEmpty())
                        addQuotationObj.setDiscountPercent(Float.parseFloat(discount));
                    else addQuotationObj.setDiscountPercent(0);


                    //todo add items lines.
                    ArrayList<DocumentLines> documentLineArrayList = new ArrayList<>();


                    for (int j = 0; j < Globals.SelectedItems.size(); j++) {
                        DocumentLines documentLines = new DocumentLines();

                        documentLines.setQuantity(quotationItem.getDocumentLines().get(j).getQuantity());
                        documentLines.setDiscountPercent(quotationItem.getDocumentLines().get(j).getDiscountPercent());
                        documentLines.setUnitPrice(quotationItem.getDocumentLines().get(j).getUnitPrice());
                        documentLines.setItemCode(quotationItem.getDocumentLines().get(j).getItemCode());
                        documentLines.setItemDescription(quotationItem.getDocumentLines().get(j).getItemDescription());
                        documentLines.setTaxCode(quotationItem.getDocumentLines().get(j).getTaxCode());
                        documentLines.setTax(quotationItem.getDocumentLines().get(j).getTax());//
                        documentLines.setTaxRate(quotationItem.getDocumentLines().get(j).getTaxRate());//

                        documentLineArrayList.addAll(Collections.singleton(documentLines));

                    }


                    addQuotationObj.setDocumentLines(documentLineArrayList);

                    //todo new keys
                    addQuotationObj.setFreightCharge(binding.quotationTotalContent.etFrieghtCharge.getText().toString().trim());


                    AddressExtension addressExtension = new AddressExtension();
                    addressExtension.setShipToBuilding(binding.quotationAddressContent.addressSection.shippingNameValue.getText().toString());
                    addressExtension.setShipToStreet(binding.quotationAddressContent.addressSection.shippingAddressValue.getText().toString());
                    addressExtension.setShipToCity(binding.quotationAddressContent.addressSection.shipcityValue.getText().toString());
                    addressExtension.setShipToZipCode(binding.quotationAddressContent.addressSection.zipcodeValue2.getText().toString());
                    addressExtension.setShipToState(shiptoStateCode);
                    addressExtension.setShipToCountry(shiptoCountrycode);   //countryCode2
                    addressExtension.setU_SSTATE(shiptoState);
                    addressExtension.setU_SCOUNTRY(shiptoCountryName);
                    addressExtension.setU_SHPTYPS(ship_shiptype);
                    addressExtension.setU_OPPRNM("");
                    addressExtension.setBillToBuilding(binding.quotationAddressContent.addressSection.billingNameValue.getText().toString());
                    addressExtension.setBillToStreet(binding.quotationAddressContent.addressSection.billingAddressValue.getText().toString());
                    addressExtension.setBillToCity(binding.quotationAddressContent.addressSection.cityValue.getText().toString());
                    addressExtension.setBillToZipCode(binding.quotationAddressContent.addressSection.zipCodeValue.getText().toString());
                    addressExtension.setBillToState(billtoStateCode);
                    addressExtension.setBillToCountry(billtoCountrycode);       //countryCode2
                    addressExtension.setU_BSTATE(billtoState);
                    addressExtension.setU_BCOUNTRY(billtoCountryName);
                    addressExtension.setU_SHPTYPB(billshiptype);
                    addressExtension.setId(quotationItem.getAddressExtension().getId());
                    addQuotationObj.setAddressExtension(addressExtension);

                    //  addQuotationObj.setDocumentLines(postJson(quotationItem.getDocumentLines()));
//                    addQuotationObj.setDocumentLines(postJsonCopy(Globals.SelectedItems,quotationItem.getDocumentLines()));
                    //   addQuotationObj.setDocumentLines(Globals.SelectedItems);


                    if (Globals.checkInternet(getActivity())) {

                        updateQuotation(QT_ID, addQuotationObj);
                    }
                }

                break;
            case R.id.customer_block:
                Bussiness_Partner_Fragment frg = new Bussiness_Partner_Fragment();
                FragmentTransaction tr = getFragmentManager().beginTransaction();
                tr.replace(R.id.quatoes_main_container, frg);
                tr.addToBackStack(null);
                tr.commit();
                break;
            case R.id.back_press:
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                Globals.hideKeybaord(v, getContext());
                getActivity().onBackPressed();
                break;
            case R.id.tab_1:
            case R.id.general:
                frameManager(binding.generalFrame, binding.totalFrame, binding.preparedFrame, binding.general, binding.total, binding.address);
                break;
            case R.id.tab_2:
            case R.id.total:
                frameManager(binding.totalFrame, binding.generalFrame, binding.preparedFrame, binding.total, binding.general, binding.address);
                break;
            case R.id.tab_3:

            case R.id.address:
                frameManager(binding.preparedFrame, binding.generalFrame, binding.totalFrame, binding.address, binding.general, binding.total);
                break;
            case R.id.posting_value:
            case R.id.postCal:
            case R.id.postingDate:

                    Globals.disablePastSelectDate(getContext(), binding.quotationGeneralContent.edCreatedDate);
                break;
            case R.id.valid_till_value:
            case R.id.validDate:
            case R.id.validCal:

                    Globals.disablePastSelectDate(getContext(), binding.quotationGeneralContent.validTillValue);
                break;
            case R.id.document_date_value:
            case R.id.docCal:
            case R.id.documentDate:

                    Globals.disablePastSelectDate(getContext(), binding.quotationGeneralContent.documentDateValue);
                break;
            case R.id.submit:
                if (validation(binding.quotationGeneralContent.remarkValue.getText().toString().trim(), ContactPersonCode)) {
                    frameManager(binding.totalFrame, binding.generalFrame, binding.preparedFrame, binding.total, binding.general, binding.address);
                }
                break;
            case R.id.next_button:
                frameManager(binding.preparedFrame, binding.generalFrame, binding.totalFrame, binding.address, binding.general, binding.total);
                break;
           /* case R.id.done_button:
                // frameManager(prepared_frame,general_frame,total_frame,address,general,total);
                binding.headerBottomroundEdit.add.performClick();
                break;*/

            case R.id.item_frame:
                Globals.hideKeybaord(v, getContext());
                if (DISABLED) {
                    Globals.inventory_item_close = false;
                    Intent intent = new Intent(act, SelectedItems.class);
                    intent.putExtra("FromWhere", "QT_UP");
                    startActivityForResult(intent, SelectItemCode);
                } else {
                    Globals.inventory_item_close = true;
                    Intent intent = new Intent(act, SelectedItems.class);
                    intent.putExtra("FromWhere", "invoices");
                    startActivityForResult(intent, SelectItemCode);
                }
                break;


        }


    }


    double totalAfterItemDiscount = 0;

    //todo calculate sum of all edit text ..
    private TextWatcher NumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
         /*   String discount = "";
            double doubleValue = 0.0;
            if (quotationItem.getDiscountPercent().isEmpty()){
                binding.quotationTotalContent.discontValue.setText("0.0");
            }else {
                try {
                    discount = quotationItem.getDiscountPercent();
                    doubleValue = Double.parseDouble(discount);
                    binding.quotationTotalContent.discontValue.setText(String.valueOf(doubleValue));
                } catch (NumberFormatException e) {
                    // Handle the exception, e.g., log an error or inform the user
                    e.printStackTrace();
                }
            }
            charSequence = discount;*/

            if (charSequence.equals("")) {
                Log.e("TEXTWATCHER123>>>", "onTextChanged: 0");
                calculateAndDisplaySum(0.0);
            } else {
                Log.e("TEXTWATCHER>>>", "onTextChanged: " + charSequence);
                Log.e("TEXTWATCHER>>>", "total: " + totalAfterItemDiscount);
                calculateAndDisplaySum(totalAfterItemDiscount);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void calculateAndDisplaySum(double total) {
        double num1 = parseEditTextValue(binding.quotationTotalContent.discontValue);


        double temp = Globals.calculateTotalOfItem(Globals.SelectedItems, String.valueOf(num1), binding.quotationTotalContent.etFrieghtCharge.getText().toString());

        binding.quotationTotalContent.totalBeforeDiscontValue.setText(String.valueOf(temp));
    }


    //todo calculate sum of all edit text ..
    private TextWatcher freightTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.equals("")) {
                Log.e("TEXTWATCHER>>>", "onTextChanged: 0");
                calculateAndDisplayFeightSum();
            } else {
                Log.e("TEXTWATCHER>>>", "onTextChanged: " + charSequence);
                Log.e("TEXTWATCHER>>>", "total: " + totalAfterItemDiscount);
                calculateAndDisplayFeightSum();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void calculateAndDisplayFeightSum() {
        double num1 = parseEditTextValue(binding.quotationTotalContent.etFrieghtCharge);

        double temp = Globals.calculateTotalOfItem(Globals.SelectedItems, binding.quotationTotalContent.discontValue.getText().toString(), String.valueOf(num1));

        binding.quotationTotalContent.totalBeforeDiscontValue.setText(String.valueOf(temp));
    }

    private Float parseEditTextValue(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            return 0.0f;
        } else {
            return Float.parseFloat(text);
        }
    }


    private void enableTextWatcher() {
        // Add the TextWatcher to the EditText
        binding.quotationTotalContent.discontValue.addTextChangedListener(NumberTextWatcher);
        binding.quotationTotalContent.etFrieghtCharge.addTextChangedListener(freightTextWatcher);
    }

    private void disableTextWatcher() {
        // Remove the TextWatcher from the EditText
        binding.quotationTotalContent.discontValue.removeTextChangedListener(NumberTextWatcher);
        binding.quotationTotalContent.etFrieghtCharge.removeTextChangedListener(freightTextWatcher);
    }

    private void setDefaultValue(String defaultValue) {
        // Temporarily disable the TextWatcher
        disableTextWatcher();

        // Set the default value
        binding.quotationTotalContent.discontValue.setText(defaultValue);

        // Re-enable the TextWatcher
        enableTextWatcher();


        // Set the default value without triggering the TextWatcher

    }

    private void setEnable() {

        binding.quotationGeneralContent.edCreatedDate.setClickable(true);

        binding.quotationGeneralContent.validTillValue.setClickable(true);

        binding.quotationGeneralContent.documentDateValue.setClickable(true);

        binding.quotationGeneralContent.submit.setClickable(true);
        binding.quotationGeneralContent.submit.setFocusable(true);
        binding.quotationGeneralContent.submit.setEnabled(true);

        binding.quotationGeneralContent.opportunityNameValue.setFocusableInTouchMode(true);
        binding.quotationGeneralContent.opportunityNameValue.setFocusable(true);
        binding.quotationGeneralContent.opportunityNameValue.setClickable(true);

        binding.quotationGeneralContent.acContactPerson.setEnabled(true);
        binding.quotationGeneralContent.acSalesEmployee.setEnabled(true);

        binding.quotationGeneralContent.edCreatedDate.setClickable(true);


        binding.quotationGeneralContent.postCal.setEnabled(true);
        binding.quotationGeneralContent.postCal.setClickable(true);
        binding.quotationGeneralContent.postCal.setFocusable(true);
        binding.quotationGeneralContent.postCal.setFocusableInTouchMode(true);


        binding.quotationGeneralContent.postingDate.setClickable(true);

        binding.quotationGeneralContent.validTillValue.setClickable(true);


        binding.quotationGeneralContent.validCal.setEnabled(true);
        binding.quotationGeneralContent.validCal.setClickable(true);
        binding.quotationGeneralContent.validDate.setClickable(true);

        binding.quotationGeneralContent.documentDateValue.setClickable(true);

        binding.quotationGeneralContent.docCal.setEnabled(true);
        binding.quotationGeneralContent.docCal.setClickable(true);

        binding.quotationGeneralContent.documentDate.setClickable(true);

        binding.quotationGeneralContent.remarkValue.setFocusableInTouchMode(true);
        binding.quotationGeneralContent.remarkValue.setFocusable(true);
        binding.quotationGeneralContent.remarkValue.setClickable(true);

        binding.quotationTotalContent.totalBeforeDiscontValue.setClickable(true);
        binding.quotationTotalContent.totalBeforeDiscontValue.setFocusable(true);
        binding.quotationTotalContent.totalBeforeDiscontValue.setFocusableInTouchMode(true);

        binding.quotationTotalContent.discontValue.setFocusableInTouchMode(true);
        binding.quotationTotalContent.discontValue.setFocusable(true);
        binding.quotationTotalContent.discontValue.setClickable(true);


        binding.quotationTotalContent.nextButton.setFocusable(true);
        binding.quotationTotalContent.nextButton.setEnabled(true);
        binding.quotationTotalContent.nextButton.setClickable(true);

        binding.quotationAddressContent.addressSection.shippingNameValue.setFocusableInTouchMode(true);
        binding.quotationAddressContent.addressSection.shippingNameValue.setFocusable(true);
        binding.quotationAddressContent.addressSection.shippingNameValue.setClickable(true);

        binding.quotationAddressContent.addressSection.zipcodeValue2.setClickable(true);
        binding.quotationAddressContent.addressSection.zipcodeValue2.setFocusable(true);
        binding.quotationAddressContent.addressSection.zipcodeValue2.setFocusableInTouchMode(true);
        binding.quotationAddressContent.addressSection.shippingSpinner2.setEnabled(true);

        binding.quotationAddressContent.addressSection.doneButton.setClickable(true);
        binding.quotationAddressContent.addressSection.doneButton.setFocusable(true);
        binding.quotationAddressContent.addressSection.doneButton.setEnabled(true);

        binding.quotationAddressContent.addressSection.shippingAddressValue.setFocusableInTouchMode(true);
        binding.quotationAddressContent.addressSection.shippingAddressValue.setFocusable(true);
        binding.quotationAddressContent.addressSection.shippingAddressValue.setClickable(true);

        binding.quotationAddressContent.addressSection.billingAddressValue.setClickable(true);
        binding.quotationAddressContent.addressSection.billingAddressValue.setFocusable(true);
        binding.quotationAddressContent.addressSection.billingAddressValue.setFocusableInTouchMode(true);

        binding.quotationAddressContent.addressSection.billingNameValue.setFocusableInTouchMode(true);
        binding.quotationAddressContent.addressSection.billingNameValue.setFocusable(true);
        binding.quotationAddressContent.addressSection.billingNameValue.setClickable(true);

        binding.quotationAddressContent.addressSection.zipCodeValue.setClickable(true);
        binding.quotationAddressContent.addressSection.zipCodeValue.setFocusable(true);
        binding.quotationAddressContent.addressSection.zipCodeValue.setFocusableInTouchMode(true);
        binding.quotationAddressContent.addressSection.shippingSpinner.setEnabled(true);
       /* binding.quotationAddressContent.addressSection.countryValue.setClickable(true);
        binding.quotationAddressContent.addressSection.stateValue.setClickable(true);
        binding.quotationAddressContent.addressSection.shipStateValue.setClickable(true);
        binding.quotationAddressContent.addressSection.shipCountryValue.setClickable(true);*/
        binding.headerBottomroundEdit.add.setVisibility(View.GONE);
        binding.headerBottomroundEdit.ok.setVisibility(View.VISIBLE);

        ESCAPED = false;
        DISABLED = true;
    }


    //todo getting opp items list from items list screen..
    ArrayList<DocumentLines> oppItemTempList_gl = new ArrayList<>();

    public ArrayList<DocumentLines> getQuotationDocLin() {
        oppItemTempList_gl.clear();
        for (DocumentLines dc : Globals.SelectedItems) {
            DocumentLines dcc = new DocumentLines();
            dcc.setId(dc.getId());
            dcc.setQuantity(dc.getQuantity());

            dcc.setUnitPrice(dc.getUnitPrice());
            dcc.setDiscountPercent(dc.getDiscountPercent());
            dcc.setItemCode(dc.getItemCode());
            dcc.setItemName(dc.getItemName());
            dcc.setItemDescription(dc.getItemDescription());
            dcc.setTaxCode(dc.getTaxCode());
            dcc.setTax(dc.getTax());


            oppItemTempList_gl.add(dcc);
        }
        return oppItemTempList_gl;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SelectItemCode) {
            binding.quotationTotalContent.itemsCount.setText("Item (" + Globals.SelectedItems.size() + ")");

            totalAfterItemDiscount = Double.parseDouble(String.valueOf(Globals.foo(Globals.calculateTotalOfItem(Globals.SelectedItems))));
            getQuotationDocLin();
            binding.quotationTotalContent.totalBeforeDiscontValue.setText(String.valueOf(Globals.foo(Globals.calculateTotalOfItemAfterHeaderDiswithFreightCharge(Globals.SelectedItems, Double.parseDouble(quotationItem.getDiscountPercent()), quotationItem.getFreightCharge()))));

        }

    }


    private void frameManager(FrameLayout visiblle_frame, FrameLayout f1, FrameLayout f2,
                              TextView selected, TextView t1, TextView t2) {
        selected.setTextColor(getResources().getColor(R.color.colorPrimary));
        t1.setTextColor(getResources().getColor(R.color.black));
        t2.setTextColor(getResources().getColor(R.color.black));

        visiblle_frame.setVisibility(View.VISIBLE);
        f1.setVisibility(View.GONE);
        f2.setVisibility(View.GONE);

    }


    private boolean validation(
            String stagesCode, String remark) {
        if (stagesCode.isEmpty()) {
            Globals.showMessage(getContext(), getString(R.string.can_not_empty));
            return false;
        } else if (remark.isEmpty()) {
            Globals.showMessage(getContext(), getString(R.string.can_not_empty));
            return false;
        }

        return true;
    }


    /******************* Update API *********************/
    ArrayList<UpdateQTDocumentLines> postlist;

    private ArrayList<UpdateQTDocumentLines> postJson(ArrayList<QuotationDocumentLines> list) {
        postlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++
        ) {
            UpdateQTDocumentLines dc = new UpdateQTDocumentLines();
            dc.setLineNum(list.get(i).getLineNum());
            dc.setItemCode(list.get(i).getItemCode());
            dc.setQuantity(list.get(i).getQuantity());
            dc.setTaxCode(list.get(i).getTaxCode());//BED+VAT
            dc.setUnitPrice(list.get(i).getUnitPrice());
            dc.setDiscountPercent(Float.valueOf(list.get(i).getDiscountPercent()));
            postlist.add(dc);
        }

        return postlist;
    }

    private ArrayList<UpdateQTDocumentLines> postJsonCopy(ArrayList<DocumentLines> list, ArrayList<QuotationDocumentLines> existingList) {
        int docNum = existingList.size();
        postlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            UpdateQTDocumentLines dc = new UpdateQTDocumentLines();
            if (i < docNum) {
                dc.setLineNum(existingList.get(i).getLineNum());
            } else {
                docNum++;
                dc.setLineNum("" + docNum);
            }

            dc.setItemCode(list.get(i).getItemCode());
            dc.setQuantity(list.get(i).getQuantity());
            dc.setTaxCode(list.get(i).getTaxCode());//BED+VAT
            dc.setUnitPrice(String.valueOf(list.get(i).getUnitPrice()));
            dc.setItemDescription(list.get(i).getItemName());
            dc.setDiscountPercent(Float.valueOf("0.0"));
            postlist.add(dc);
        }

        return postlist;
    }


    float Total_Before_Disscount = 0;

    private float Total_Before_Disscount(ArrayList<DocumentLines> list) {
        float result = 0;
        for (int i = 0; i < list.size(); i++) {
            result = result + Float.parseFloat(list.get(i).getQuantity()) * Float.parseFloat(String.valueOf(list.get(i).getUnitPrice()));
        }
        return result;
    }

    private boolean valiadtion(String contactPerson, String postDate, String validDate,
                               String DocDate, String remarks) {
        if (contactPerson.isEmpty()) {
            Globals.showMessage(act, getResources().getString(R.string.can_not_empty));
            return false;
        } else if (validDate.isEmpty()) {
            Globals.showMessage(act, getResources().getString(R.string.can_not_empty));
            return false;
        } else if (DocDate.isEmpty()) {
            Globals.showMessage(act, getResources().getString(R.string.can_not_empty));
            return false;
        } else if (postDate.isEmpty()) {
            Globals.showMessage(act, getResources().getString(R.string.can_not_empty));
            return false;
        }else if (binding.quotationGeneralContent.etPurchaseOrderNumber.getText().toString().trim().isEmpty()) {
            Globals.showMessage(act, getResources().getString(R.string.can_not_empty));
            return false;
        }
        return true;
    }

    private void updateQuotation(String QT_ID, UpdateQuotationModel in) {
        binding.loader.loader.setVisibility(View.VISIBLE);
        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService(requireContext()).updateOrder(in);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                binding.loader.loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    Globals.SelectedItems.clear();
                    Toast.makeText(act, "Posted Successfully.", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(act, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                binding.loader.loader.setVisibility(View.GONE);
                Toast.makeText(act, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<ContactPersonData> ContactEmployeesList;
    ContactPersonAdapter contactPersonAdapter;


    private void callContactEmployeeApi(String cardCode) {
        ContactPersonData contactPersonData = new ContactPersonData();
        contactPersonData.setCardCode(cardCode);

        Call<com.cinntra.vistadelivery.model.ContactPerson> call = NewApiClient.getInstance().getApiService(requireContext()).contactemplist(contactPersonData);
        call.enqueue(new Callback<ContactPerson>() {
            @Override
            public void onResponse(Call<ContactPerson> call, Response<ContactPerson> response) {
                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {
                        ContactEmployeesList = new ArrayList<>();
                        ContactEmployeesList.clear();
                        ContactEmployeesList.addAll(response.body().getData());
                        ContactPersonAutoAdapter leadTypeAdapter = new ContactPersonAutoAdapter(getActivity(), R.layout.drop_down_textview, ContactEmployeesList);
                        binding.quotationGeneralContent.acContactPerson.setAdapter(leadTypeAdapter);

                    } else {
                        Toasty.error(getActivity(), response.body().getMessage());
                    }
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(getActivity(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactPerson> call, Throwable t) {

                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

 /*   private void callContactApi(String cardCode) {
        ContactEmployeesList = new ArrayList<>();
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getContactEmployeeList(cardCode).observe(getActivity(), new Observer<List<ContactPersonData>>() {
            @Override
            public void onChanged(@Nullable List<ContactPersonData> itemsList) {
                if (itemsList == null || itemsList.size() == 0) {
                    Globals.setmessage(getActivity());
                } else {
                    ContactEmployeesList = itemsList;
                    contactPersonAdapter = new ContactPersonAdapter(getActivity(), ContactEmployeesList);
                  *//*  binding.quotationGeneralContent.contactPersonSpinner.setAdapter(contactPersonAdapter);
                    //int index = ContactEmployeesList.get


                    if (itemsList != null && ContactPersonCode != null)
                        binding.quotationGeneralContent.contactPersonSpinner.setSelection(Globals.getSelectedContact(itemsList, ContactPersonCode));*//*

                }
            }
        });
    }*/

    SalesEmployeeAdapter salesadapter;
    public List<SalesEmployeeItem> salesEmployeeItemList = new ArrayList<>();

    private void callSalessApi() {
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getSalesEmployeeList().observe(getActivity(), new Observer<List<SalesEmployeeItem>>() {
            @Override
            public void onChanged(@Nullable List<SalesEmployeeItem> itemsList) {
                if (itemsList == null || itemsList.size() == 0) {
                    Globals.setmessage(getActivity());
                } else {
                    salesEmployeeItemList = itemsList;
                    salesadapter = new SalesEmployeeAdapter(getActivity(), itemsList);
                    SalesEmployeeAutoAdapter adapter = new SalesEmployeeAutoAdapter(getActivity(), R.layout.drop_down_textview, itemsList);
                    binding.quotationGeneralContent.acSalesEmployee.setAdapter(adapter);
                }
            }
        });
    }

}