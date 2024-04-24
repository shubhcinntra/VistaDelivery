package com.cinntra.vistadelivery.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.adapters.PaymentAdapter;
import com.cinntra.vistadelivery.adapters.SalesEmployeeAutoAdapter;
import com.cinntra.vistadelivery.adapters.bpAdapters.ContactPersonAutoAdapter;
import com.cinntra.vistadelivery.databinding.AddQuotationBinding;
import com.cinntra.vistadelivery.fragments.AddOrderForm_One_Fragment;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.globals.MainBaseActivity;
import com.cinntra.vistadelivery.interfaces.SubmitQuotation;
import com.cinntra.vistadelivery.model.AddQuotation;
import com.cinntra.vistadelivery.model.BPModel.BPAllFilterRequestModel;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.cinntra.vistadelivery.model.ContactPerson;
import com.cinntra.vistadelivery.model.ContactPersonData;
import com.cinntra.vistadelivery.model.ContactPersonResponseModel;
import com.cinntra.vistadelivery.model.EmployeeValue;
import com.cinntra.vistadelivery.model.NewOppResponse;
import com.cinntra.vistadelivery.model.PayMentTerm;
import com.cinntra.vistadelivery.model.QuotationItem;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.ResponseCompanyBranchAllFilter;
import com.cinntra.vistadelivery.model.SaleEmployeeResponse;
import com.cinntra.vistadelivery.model.SalesEmployeeItem;
import com.cinntra.vistadelivery.model.orderModels.CreateOrderRequestModel;
import com.cinntra.vistadelivery.newapimodel.LeadValue;
import com.cinntra.vistadelivery.newapimodel.NewOpportunityRespose;
import com.cinntra.vistadelivery.newapimodel.OpportunityValue;
import com.cinntra.vistadelivery.spinneradapter.BranchBusinessPartnerTypeSearchableSpinnerAdapter;
import com.cinntra.vistadelivery.viewModel.ItemViewModel;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddOrderAct extends MainBaseActivity implements View.OnClickListener, SubmitQuotation {
    private static final int QUOTCODE = 100001;
    public static int PARTNERCODE = 10000;
    public static int ITEMSCODE = 1000;

    public static int LeadCode = 101;
    public static int PARENT_PROFORMA_INVOICE = 1002;
    public static int OPPCODE = 1001;

    AppCompatActivity act;
    public static String salesEmployeeCode = "";
    public String QuotName = "";
    public String QuotID = "";
    //    @BindView(R.id.submit)
//    Button submit;
    public static QuotationItem fromquotation;
    String LeadID = "";
    String LeadName = "";
    String ContactPersonCode = "";
    String ContactPerson = "";

    String OPPID = "";
    public String CardCode;
    public String CardName;
    public String contactPersonCode;
    String OppID = null;

    public String salePCode;

    public static AddQuotation addQuotationObj;
    List<SalesEmployeeItem> salesEmployeeItemList = new ArrayList<>();
    private ArrayList<ContactPersonData> ContactEmployeesList = new ArrayList<>();
    QuotationItem quotationItem1 = new QuotationItem();
    NewOpportunityRespose opportunityItemValue = new NewOpportunityRespose();

    QuotationItem quotationForOrder = new QuotationItem();

    AddQuotationBinding binding;
    String BPCardCode = "";

    public static CreateOrderRequestModel createOrderRequestModel;
    ArrayList<ContactPersonResponseModel.Datum> contactPersonListgl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        act = AddOrderAct.this;

        binding = AddQuotationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Globals.IsQUOTEORDER = true;
        Globals.ISORDER = true;

        if (Globals.ISORDER) {
            binding.quotationGeneralContent.linearHidedataFromQuotation.setVisibility(View.VISIBLE);

        } else {

        }

        setUpBusinessPartnerbranchTypeSpinner();
        callPaymentApi();

        if (getIntent() != null) {
            BPCardCode = getIntent().getStringExtra("BPCardCodeShortCut");
            // Use the passed data as needed
        }

        //  ButterKnife.bind(this);
        addQuotationObj = new AddQuotation();

        salesEmployeeCode = Prefs.getString(Globals.SalesEmployeeCode, "");

        binding.quotationGeneralContent.quotationView.setVisibility(View.GONE);
        binding.quotationGeneralContent.ppiView.setVisibility(View.VISIBLE);
        binding.quotationGeneralContent.glassAndCoatingDate.setVisibility(View.GONE);
        binding.quotationGeneralContent.validAndDocDate.setVisibility(View.VISIBLE);

        binding.quotationGeneralContent.oppView.setVisibility(View.VISIBLE);
        binding.quotationGeneralContent.QtView.setVisibility(View.GONE);
        binding.quotationGeneralContent.bpView.setVisibility(View.VISIBLE);
        binding.quotationGeneralContent.quotationNumLayout.setVisibility(View.GONE);
        binding.quotationGeneralContent.quotationAttachmentLayout.setVisibility(View.GONE);


        //todo create quotation of particular opportunity if quotation not created of that opportunity.
        OppID = getIntent().getStringExtra("FROM_OPP_ID");


        if (OppID != null && !OppID.isEmpty()) {
            callOppOneApi();
        } else {

        }


        if (Globals.checkInternet(this)) {

            try {
                if (!BPCardCode.isEmpty()) {
                    callBPOneAPi(BPCardCode, "");
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (Prefs.getString(Globals.FromQuotation, "").equalsIgnoreCase("Quotation")) {

        }
        if (getIntent() != null) {
            quotationForOrder = (QuotationItem) getIntent().getSerializableExtra("QuotationObject");
        }
//            quotationItem = Globals.quotationOrder.get(0);//todo comment by me--

        if (quotationForOrder != null) {
            setQuotationData(quotationForOrder);
        }

        binding.quotationGeneralContent.purchaseDateValue.setText(Globals.getTodaysDate());
        binding.quotationGeneralContent.validTillValue.setText(Globals.getTodaysDate());
        binding.quotationGeneralContent.documentDateValue.setText(Globals.getTodaysDate());
        binding.quotationGeneralContent.edCreatedDate.setText(Globals.getTodaysDate());

        callSalessApi("");
        clickListeners();


        binding.quotationGeneralContent.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    binding.quotationGeneralContent.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                    salesEmployeeCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();
                } else {
                    salesEmployeeCode = "";
                    binding.quotationGeneralContent.acSalesEmployee.setText("");
                }
            }
        });

        //todo-----------------
/*
        if (Prefs.getString(Globals.FromQuotation, "").equalsIgnoreCase("Quotation")) {
            fromquotation = Globals.quotationOrder.get(0);
            setQuotationData(fromquotation);
        }

        callSalessApi();*/

    }


    NewOpportunityRespose oppItemLine = new NewOpportunityRespose();

    //todo calling opportunity on eapi here..
    private void callOppOneApi() {
        OpportunityValue opportunityValue = new OpportunityValue();
        opportunityValue.setId(Integer.valueOf(OppID));
        Call<NewOppResponse> call = NewApiClient.getInstance().getApiService(this).getparticularopportunity(opportunityValue);
        call.enqueue(new Callback<NewOppResponse>() {
            @Override
            public void onResponse(Call<NewOppResponse> call, Response<NewOppResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {
                        setOppData(response.body().getData().get(0));
                        oppItemLine = response.body().getData().get(0);

                        opportunityItemValue = oppItemLine;

                        callBPOneAPi(oppItemLine.getCardCode(), "");
                    } else {
                        Toast.makeText(AddOrderAct.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 500) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 201) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<NewOppResponse> call, Throwable t) {
                Toast.makeText(AddOrderAct.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//        callContactApi(opportunityItem.getCardCode());

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

                    binding.quotationGeneralContent.paymentTermValue.setSelection(Globals.getPaymentTermPo(getPaymenterm,payment_term));
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


    @SuppressLint("ResourceType")
    private void setQuotationData(QuotationItem quotationItem) {
        if (quotationItem.getPaymentGroupCodeDetails().size() > 0) {
            payment_term = quotationItem.getPaymentGroupCodeDetails().get(0).getGroupNumber();
        }
        branchTypeSelected = quotationItem.getBPLID();


        binding.quotationGeneralContent.saerchableSpinnerBranch.setSelection(Globals.getCompanyBranchPo(branchTypeDataList, branchTypeSelected));

        binding.quotationGeneralContent.edParentProformaValue.setText(quotationItem.getU_QUOTNM());
        QuotName = quotationItem.getU_QUOTNM();
        QuotID = quotationItem.getId();
        binding.quotationGeneralContent.quoNamevalue.setText(quotationItem.getU_QUOTNM());
       /* document_date_value.setText(fromquotation.getTaxDate());
        valid_till_value.setText(fromquotation.getDocDueDate());
        posting_value.setText(fromquotation.getDocDate());*/
        binding.quotationGeneralContent.remarkValue.setText(quotationItem.getComments());
        binding.quotationGeneralContent.businessPartnerValue.setTextColor(Color.parseColor(getString(R.color.black)));
        binding.quotationGeneralContent.businessPartnerValue.setText(quotationItem.getCardCode());
        binding.quotationGeneralContent.opportunityNameValue.setText(quotationItem.getOpportunityName());


       binding.quotationGeneralContent.edCreatedDate.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getDocDate()));
        binding.quotationGeneralContent.validTillValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getDocDueDate()));

        binding.quotationGeneralContent.documentDateValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getTaxDate()));



        OppID = quotationItem.getU_OPPID();

        CardCode = quotationItem.getCardCode();
        CardName = quotationItem.getCardName();
//        acContactPerson.setText(quotationItem.getContactPersonCode().get(0).getFirstName());
        contactPersonCode = String.valueOf(quotationItem.getContactPersonCode());

        callBPOneAPi(quotationItem.getCardCode(), "");

        if (quotationItem.getContactPersonCodeDetails().size() > 0) {
            ContactPersonCode = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getId());
            ContactPerson = quotationItem.getContactPersonCodeDetails().get(0).getFirstName();
            binding.quotationGeneralContent.acContactPerson.setText(quotationItem.getContactPersonCodeDetails().get(0).getFirstName());

            callContactEmployeeApi(CardCode);

        } else {
            binding.quotationGeneralContent.acContactPerson.setText("");
        }

        //todo item select of contact person..
        binding.quotationGeneralContent.acContactPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContactEmployeesList.size() > 0) {
                    ContactPersonCode = ContactEmployeesList.get(position).getInternalCode();
                    ContactPerson = ContactEmployeesList.get(position).getFirstName();
                    binding.quotationGeneralContent.acContactPerson.setText(ContactEmployeesList.get(position).getFirstName());
                } else {
                    ContactPersonCode = "";
                    ContactPerson = "";
                }

            }
        });


        if (quotationItem.getSalesPersonCodeDetails().size() > 0) {

            binding.quotationGeneralContent.acSalesEmployee.setText(quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeName());
            salesEmployeeCode = quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeCode();
        }



        binding.quotationGeneralContent.paymentTermValue.setSelection(Globals.getPaymentTermPo(getPaymenterm, payment_term));

        binding.quotationGeneralContent.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    binding.quotationGeneralContent.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                    salesEmployeeCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();
                } else {
                    salesEmployeeCode = "";
                    binding.quotationGeneralContent.acSalesEmployee.setText("");
                }
            }
        });


    }


    String branchTypeSelected = "";
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

        Call<ResponseCompanyBranchAllFilter> call = NewApiClient.getInstance().getApiService(this).getBranchAllFilter(requestModel);
        call.enqueue(new Callback<ResponseCompanyBranchAllFilter>() {
            @Override
            public void onResponse(Call<ResponseCompanyBranchAllFilter> call, Response<ResponseCompanyBranchAllFilter> response) {

                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {


                        branchTypeDataList.clear();
                        branchTypeDataList.addAll(response.body().getData());
                        branchTypeSelected = branchTypeDataList.get(0).getbPLId();
                        BranchBusinessPartnerTypeSearchableSpinnerAdapter sourceSearchableSpinnerAdapter = new BranchBusinessPartnerTypeSearchableSpinnerAdapter(AddOrderAct.this, branchTypeDataList);

                        binding.quotationGeneralContent.saerchableSpinnerBranch.setAdapter(sourceSearchableSpinnerAdapter);


                        //todo item click on assign to---
                    } else {
                        Toast.makeText(AddOrderAct.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    }


                } else {
                    Toast.makeText(AddOrderAct.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<ResponseCompanyBranchAllFilter> call, Throwable t) {

                Toast.makeText(AddOrderAct.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void clickListeners() {

        binding.headerBottomRounded.headTitle.setText(getResources().getString(R.string.new_order));
        binding.headerBottomRounded.backPress.setOnClickListener(this);
        binding.quotationGeneralContent.postingDate.setOnClickListener(this);
        binding.quotationGeneralContent.edCreatedDate.setOnClickListener(this);
        binding.quotationGeneralContent.edGlassOrderDate.setOnClickListener(this);
        binding.quotationGeneralContent.glassOrderDateLayout.setOnClickListener(this);
        binding.quotationGeneralContent.glassCal.setOnClickListener(this);
        binding.quotationGeneralContent.edCoatingDate.setOnClickListener(this);
        binding.quotationGeneralContent.coatingCall.setOnClickListener(this);
        binding.quotationGeneralContent.coatingDateLayout.setOnClickListener(this);
        binding.quotationGeneralContent.edDeliveryDate.setOnClickListener(this);
        binding.quotationGeneralContent.deliveryCall.setOnClickListener(this);
        binding.quotationGeneralContent.deliveryDateLayout.setOnClickListener(this);
        binding.quotationGeneralContent.postCal.setOnClickListener(this);
        binding.quotationGeneralContent.validDate.setOnClickListener(this);
        binding.quotationGeneralContent.validTillValue.setOnClickListener(this);
        binding.quotationGeneralContent.validCal.setOnClickListener(this);
        binding.quotationGeneralContent.documentDate.setOnClickListener(this);
        binding.quotationGeneralContent.documentDateValue.setOnClickListener(this);
        binding.quotationGeneralContent.docCal.setOnClickListener(this);
        binding.quotationGeneralContent.bussinessPartner.setOnClickListener(this);
        binding.quotationGeneralContent.businessPartnerValue.setOnClickListener(this);
        binding.quotationGeneralContent.quoNamevalue.setOnClickListener(this);
        binding.quotationGeneralContent.quoView.setOnClickListener(this);
        binding.quotationGeneralContent.oppView.setOnClickListener(this);
        binding.quotationGeneralContent.opportunityNameValue.setOnClickListener(this);
        binding.quotationGeneralContent.edParentProformaValue.setOnClickListener(this);

        binding.quotationGeneralContent.submit.setOnClickListener(this);

        binding.quotationGeneralContent.paymentTermValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (getPaymenterm.size() > 0)
                    payment_term = getPaymenterm.get(position).getGroupNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                payment_term = getPaymenterm.get(0).getGroupNumber();

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_press:
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else
                    onBackPressed();
                break;
            case R.id.postingDate:
            case R.id.postCal:
            case R.id.edCreatedDate:
                Globals.disablePastSelectDate(act, binding.quotationGeneralContent.edCreatedDate);
                break;

            case R.id.glassOrderDateLayout:
            case R.id.glassCal:
            case R.id.edGlassOrderDate:
                Globals.enableAllCalenderDateSelect(act, binding.quotationGeneralContent.edGlassOrderDate);
                break;

            case R.id.coatingDateLayout:
            case R.id.coatingCall:
            case R.id.edCoatingDate:
                Globals.enableAllCalenderDateSelect(act, binding.quotationGeneralContent.edCoatingDate);
                break;
            case R.id.deliveryDateLayout:
            case R.id.deliveryCall:
            case R.id.edDeliveryDate:
                Globals.enableAllCalenderDateSelect(act, binding.quotationGeneralContent.edDeliveryDate);
                break;

            case R.id.validDate:
            case R.id.valid_till_value:
            case R.id.validCal:
                Globals.disablePastSelectDate(act, binding.quotationGeneralContent.validTillValue);
                break;
            case R.id.documentDate:
            case R.id.document_date_value:
            case R.id.docCal:
                Globals.disablePastSelectDate(act, binding.quotationGeneralContent.documentDateValue);
                break;
            case R.id.bussinessPartner:
            case R.id.business_partner_value:
                selectBPartner();
                break;
            case R.id.ed_parent_proforma_value:
                selectParentProformaInvoice();
                break;
           /* case R.id.quo_namevalue:
            case R.id.quo_view:
                selectQuotation();
                break;*/
            case R.id.opp_view:
            case R.id.opportunity_name_value:
                selectOpportunity();
                break;

            case R.id.lead_view:
            case R.id.lead_value:
                Prefs.putString(Globals.BussinessPageType, "AddOrderLead");
                Intent i = new Intent(AddOrderAct.this, LeadsActivity.class);
                startActivityForResult(i, LeadCode);
                break;
            case R.id.itemsView:
                if (Globals.SelectedItems.size() == 0) {
                    Intent intent = new Intent(AddOrderAct.this, ItemsList.class);
                    startActivityForResult(intent, ITEMSCODE);
                } else {
                    Intent intent = new Intent(AddOrderAct.this, SelectedItems.class);
                    intent.putExtra("FromWhere", "order");
                    startActivityForResult(intent, ITEMSCODE);
                }
                break;
            case R.id.submit:

                String oppname = binding.quotationGeneralContent.opportunityNameValue.getText().toString().trim();
                String poDate = binding.quotationGeneralContent.edCreatedDate.getText().toString().trim();
                String vDate = binding.quotationGeneralContent.validTillValue.getText().toString().trim();
                String docDate = binding.quotationGeneralContent.documentDateValue.getText().toString().trim();
                String glassDate = binding.quotationGeneralContent.edGlassOrderDate.getText().toString().trim();
                String coatingDate = binding.quotationGeneralContent.edCoatingDate.getText().toString().trim();
                String deliveryDate = binding.quotationGeneralContent.edDeliveryDate.getText().toString().trim();
                String remark = binding.quotationGeneralContent.remarkValue.getText().toString().trim();
                if (valiadtion(binding.quotationGeneralContent.businessPartnerValue.getText().toString(), salesEmployeeCode, poDate, vDate, docDate, ContactPersonCode, binding.quotationGeneralContent.etPurchaseOrderNumber)) {

                    addQuotationObj.setU_QUOTNM(QuotName);
                    addQuotationObj.setU_QUOTID(QuotID);
                    addQuotationObj.setCardCode(CardCode.trim());
                    addQuotationObj.setCardName(CardName.trim());
                    addQuotationObj.setSalesPersonCode(salesEmployeeCode.trim());
                    addQuotationObj.setValidDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(vDate));
                    addQuotationObj.setPostingDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(docDate));
                    addQuotationObj.setDocumentDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(poDate));//docDate
                    addQuotationObj.setGlassDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(glassDate));//docDate
                    addQuotationObj.setCoatingDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(coatingDate));//docDate
                    addQuotationObj.setDeliveryDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(deliveryDate));//docDate
                    addQuotationObj.setSalesPerson(ContactPersonCode);
                    //todo new key
                    addQuotationObj.setQuotationID(QuotID);
                    addQuotationObj.setBPLID(branchTypeSelected);
                    addQuotationObj.setPaymentGroupCode(payment_term);
                    addQuotationObj.setPONumber(binding.quotationGeneralContent.etPurchaseOrderNumber.getText().toString().trim());
                    addQuotationObj.setPODate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(binding.quotationGeneralContent.purchaseDateValue.getText().toString().trim()));
//
//                    createOrderRequestModel.setContactPersonCode(salePCode);

                    addQuotationObj.setRemarks(remark);
                    addQuotationObj.setOpportunityName(oppname.trim());


                    if (OPPID.equalsIgnoreCase("")) {
                        addQuotationObj.setU_OPPID("");
                    } else {
                        addQuotationObj.setU_OPPID(OPPID.trim());
                    }

                    addQuotationObj.setUpdateDate(Globals.getTodaysDatervrsfrmt());
                    addQuotationObj.setUpdateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateDate(Globals.getTodaysDatervrsfrmt());

                    AddOrderForm_One_Fragment addQuotationForm_one_fragment = new AddOrderForm_One_Fragment(quotationForOrder, CardCode, opportunityItemValue);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.main_edit_qt_frame, addQuotationForm_one_fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
//
                break;
        }
    }


    private void selectBPartner() {
        Prefs.putString(Globals.BussinessPageType, "AddOrder");
        Intent i = new Intent(act, BussinessPartners.class);
        startActivityForResult(i, PARTNERCODE);
    }

    private void selectOpportunity() {
        Prefs.putString(Globals.SelectOpportnity, "Add_Order");
        Intent in = new Intent(act, Opportunities_Pipeline_Activity.class);
        startActivityForResult(in, OPPCODE);
    }

    private void selectParentProformaInvoice() {
        Prefs.putString(Globals.SelectQuotation, "Add_Order");
        Intent in = new Intent(act, QuotationActivity.class);
        startActivityForResult(in, PARENT_PROFORMA_INVOICE);
    }

    private void selectQuotation() {
        Prefs.putString(Globals.QuotationListing, "null");
        Prefs.putBoolean(Globals.SelectQuotation, false);
        Intent i = new Intent(act, QuotationActivity.class);
        startActivityForResult(i, QUOTCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PARTNERCODE) {
            BusinessPartnerAllResponse.Datum customerItem = (BusinessPartnerAllResponse.Datum) data.getSerializableExtra(Globals.CustomerItemData);
            callBPOneAPi(customerItem.getCardCode(), "");
        } else if (resultCode == RESULT_OK && requestCode == QUOTCODE) {
            QuotationItem qi = (QuotationItem) data.getSerializableExtra(Globals.QuotationData);
            setQuotationData(qi);
        } else if (resultCode == RESULT_OK && requestCode == PARENT_PROFORMA_INVOICE) {
            QuotationItem quotationItem = Globals.partent_proforma_invoice;
            if (quotationItem != null) {
                quotationItem1 = quotationItem;
                quotationForOrder=quotationItem;
                setProformaInvoiceData(quotationItem);
            }
        } else if (resultCode == RESULT_OK && requestCode == OPPCODE) {
            NewOpportunityRespose oppItem = Globals.opp;
            if (oppItem != null) {
                opportunityItemValue = oppItem;
                setOppData(oppItem);
            }
        } else if (requestCode == LeadCode && resultCode == RESULT_OK) {
            LeadValue leadValue = data.getParcelableExtra(Globals.Lead_Data);
//            lead_value.setText(leadValue.getCompanyName());
            LeadID = leadValue.getId().toString();
            LeadName = leadValue.getCompanyName();
        }

    }


    //todo set opportunity data..
    @SuppressLint("ResourceType")
    private void setOppData(NewOpportunityRespose oppdata) {
        binding.quotationGeneralContent.businessPartnerValue.setTextColor(Color.parseColor(getString(R.color.black)));
//        opportunity_name_value.setClickable(false);
//        opportunity_name_value.setEnabled(false);
        binding.quotationGeneralContent.opportunityNameValue.setTextColor(Color.parseColor(getString(R.color.black)));
//        binding.quotationGeneralContent.oppView.setClickable(false);
//        binding.quotationGeneralContent.oppView.setEnabled(false);
        OPPID = oppdata.getId();
        binding.quotationGeneralContent.opportunityNameValue.setText(oppdata.getOpportunityName());
        binding.quotationGeneralContent.businessPartnerValue.setText(oppdata.getCustomerName());
        binding.quotationGeneralContent.acContactPerson.setText(oppdata.getContactPersonName());
        ContactPersonCode = String.valueOf(oppdata.getContactPerson());

        CardCode = oppdata.getCardCode();
        CardName = oppdata.getCustomerName();
        salePCode = oppdata.getContactPerson();
        salesEmployeeCode = oppdata.getSalesPerson();
        callBPOneAPi(oppdata.getCardCode(), "OppSelect");
        callContactEmployeeApi(CardCode);

//        callContactPersonOneApi(oppdata.getCardCode());
//        callContactEmployeeApi(oppdata.getCardCode()); //todo comment by chach

       /* createOrderRequestModel.setCardCode(CardCode);
        createOrderRequestModel.setCardName(CardName);
        createOrderRequestModel.setContactPersonCode(salePCode);
        createOrderRequestModel.setSalesPersonCode(salesEmployeeCode);*/
    }

    //todo set data acc to proforma invoice..
    private void setProformaInvoiceData(QuotationItem quotationItem) {
        binding.quotationGeneralContent.edParentProformaValue.setText(quotationItem.getCardName());
        QuotID = quotationItem.getCardCode();
        QuotName = quotationItem.getCardName();
        binding.quotationGeneralContent.businessPartnerValue.setText(quotationItem.getCardName());
        CardCode = quotationItem.getCardCode();
        CardName = quotationItem.getCardName();
//        acContactPerson.setText(quotationItem.getContactPersonCode().get(0).getFirstName());
        contactPersonCode = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getId());

        callBPOneAPi(quotationItem.getCardCode(), "");

        if (quotationItem.getContactPersonCodeDetails().size() > 0) {
            ContactPersonCode = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getId());
            ContactPerson = quotationItem.getContactPersonCodeDetails().get(0).getFirstName();
            binding.quotationGeneralContent.acContactPerson.setText(quotationItem.getContactPersonCodeDetails().get(0).getFirstName());

            callContactEmployeeApi(CardCode);

        } else {
            binding.quotationGeneralContent.acContactPerson.setText("");
        }

        //todo item select of contact person..
        binding.quotationGeneralContent.acContactPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContactEmployeesList.size() > 0) {
                    ContactPersonCode = ContactEmployeesList.get(position).getInternalCode();
                    ContactPerson = ContactEmployeesList.get(position).getFirstName();
                    binding.quotationGeneralContent.acContactPerson.setText(ContactEmployeesList.get(position).getFirstName());
                } else {
                    ContactPersonCode = "";
                    ContactPerson = "";
                }

            }
        });


        if (quotationItem.getSalesPersonCodeDetails().size() > 0) {

            binding.quotationGeneralContent.acSalesEmployee.setText(quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeName());
            salesEmployeeCode = quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeCode();
        }


        binding.quotationGeneralContent.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    binding.quotationGeneralContent.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                    salesEmployeeCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();
                } else {
                    salesEmployeeCode = "";
                    binding.quotationGeneralContent.acSalesEmployee.setText("");
                }
            }
        });


    }

    //todo set data according to bp select--
    private void setData(BusinessPartnerAllResponse.Datum customerItem) {
        CardCode = customerItem.getCardCode();
        CardName = customerItem.getCardName();

        binding.quotationGeneralContent.businessPartnerValue.setText(customerItem.getCardName());

        if (customerItem.getContactEmployees().size() > 0) {
            ContactPersonCode = customerItem.getContactEmployees().get(0).getInternalCode();
            ContactPerson = customerItem.getContactEmployees().get(0).getFirstName();
            binding.quotationGeneralContent.acContactPerson.setText(customerItem.getContactEmployees().get(0).getFirstName());

            callContactEmployeeApi(CardCode);

        } else {
            binding.quotationGeneralContent.acContactPerson.setText("");
        }


        if (customerItem.getSalesPersonCode().size() > 0) {

            binding.quotationGeneralContent.acSalesEmployee.setText(customerItem.getSalesPersonCode().get(0).getSalesEmployeeName());
            salesEmployeeCode = customerItem.getSalesPersonCode().get(0).getSalesEmployeeCode();
        }


        binding.quotationGeneralContent.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (salesEmployeeItemList.size() > 0) {
                    binding.quotationGeneralContent.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                    salesEmployeeCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();
                } else {
                    salesEmployeeCode = "";
                    binding.quotationGeneralContent.acSalesEmployee.setText("");
                }
            }
        });


    }


    BusinessPartnerAllResponse.Datum customerItem = null;

    //todo calling bp one api here...
    private void callBPOneAPi(String BPCardCode, String oppSelect) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CardCode", BPCardCode);
        Call<BusinessPartnerAllResponse> call = NewApiClient.getInstance().getApiService(this).callBPOneAPi(jsonObject);
        call.enqueue(new Callback<BusinessPartnerAllResponse>() {
            @Override
            public void onResponse(Call<BusinessPartnerAllResponse> call, Response<BusinessPartnerAllResponse> response) {
                if (response.body().getStatus() == 200) {

                    if (response.body().getData().size() > 0) {
                        customerItem = response.body().getData().get(0);

                        binding.quotationGeneralContent.businessPartnerValue.setText(customerItem.getCardName());

                    }

                    callSalessApi(oppSelect);


                } else if (response.code() == 500) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 201) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BusinessPartnerAllResponse> call, Throwable t) {
                Log.e("ApiFailure==>", "onFailure: " + t.getMessage());
            }
        });

    }


    //todo calling contact employee llist api ---
    private void callContactEmployeeApi(String cardCode) {
        ContactPersonData contactPersonData = new ContactPersonData();
        contactPersonData.setCardCode(cardCode);

        Call<ContactPerson> call = NewApiClient.getInstance().getApiService(this).contactemplist(contactPersonData);
        call.enqueue(new Callback<ContactPerson>() {
            @Override
            public void onResponse(Call<ContactPerson> call, Response<ContactPerson> response) {
                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {
                        ContactEmployeesList = new ArrayList<>();
                        ContactEmployeesList.clear();
                        ContactEmployeesList.addAll(response.body().getData());
                        ContactPersonAutoAdapter leadTypeAdapter = new ContactPersonAutoAdapter(AddOrderAct.this, R.layout.drop_down_textview, ContactEmployeesList);
                        binding.quotationGeneralContent.acContactPerson.setAdapter(leadTypeAdapter);

                        //todo item select of contact person..
                        binding.quotationGeneralContent.acContactPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (ContactEmployeesList.size() > 0) {
                                    ContactPersonCode = ContactEmployeesList.get(position).getInternalCode();
                                    ContactPerson = ContactEmployeesList.get(position).getFirstName();
                                    binding.quotationGeneralContent.acContactPerson.setText(ContactEmployeesList.get(position).getFirstName());
                                } else {
                                    ContactPersonCode = "";
                                    ContactPerson = "";
                                }

                            }
                        });

                    } else {
                        Toasty.error(AddOrderAct.this, response.body().getMessage());
                    }
                } else if (response.code() == 500) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 201) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactPerson> call, Throwable t) {

                Toast.makeText(AddOrderAct.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void callSalessApi(String oppSelect) {

        EmployeeValue employeeValue = new EmployeeValue();
        employeeValue.setSalesEmployeeCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<SaleEmployeeResponse> call = NewApiClient.getInstance().getApiService(this).getSalesEmplyeeList(employeeValue);
        call.enqueue(new Callback<SaleEmployeeResponse>() {
            @Override
            public void onResponse(Call<SaleEmployeeResponse> call, Response<SaleEmployeeResponse> response) {

                if (response.code() == 200) {
                    if (response.body().getValue().size() > 0) {
                        salesEmployeeItemList.clear();
                        salesEmployeeItemList = response.body().getValue();

                        binding.quotationGeneralContent.acSalesEmployee.setAdapter(new SalesEmployeeAutoAdapter(AddOrderAct.this, R.layout.drop_down_textview, salesEmployeeItemList));
                        Globals.getSelectedSalesP(salesEmployeeItemList, salesEmployeeCode);

                        if (oppSelect.equalsIgnoreCase("OppSelect")) {
                            try {
                                if (customerItem != null) {
                                    if (customerItem.getSalesPersonCode().size() > 0 && customerItem.getSalesPersonCode() != null) {
                                        binding.quotationGeneralContent.acSalesEmployee.setText(customerItem.getSalesPersonCode().get(0).getSalesEmployeeName());
                                        salesEmployeeCode = customerItem.getSalesPersonCode().get(0).getSalesEmployeeCode();
                                    }
                                }


                                binding.quotationGeneralContent.acSalesEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if (salesEmployeeItemList.size() > 0 && position > 0) {
                                            salesEmployeeCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();
                                            binding.quotationGeneralContent.acSalesEmployee.setText(salesEmployeeItemList.get(position).getSalesEmployeeName());
                                        } else {
                                            salesEmployeeCode = "";
                                            binding.quotationGeneralContent.acSalesEmployee.setText("");
                                        }
                                    }
                                });
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else {
                            if (customerItem != null) {
                                setData(customerItem);
                            }
                        }

                    } else {
                        Globals.setmessage(getApplicationContext());
                    }
                } else if (response.code() == 500) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 201) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaleEmployeeResponse> call, Throwable t) {
                Toast.makeText(AddOrderAct.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    //todo set add order validation..
    @Override
    public void submitQuotaion(ProgressBar loader) {
        loader.setVisibility(View.VISIBLE);
//        createOrderRequestModel.setDocumentLines(Globals.SelectedItems);
        createOrder(addQuotationObj, loader);

    }

    //todo hitting create order api here...
    private void createOrder(AddQuotation in, ProgressBar loader) {
        Gson gson = new Gson();
        String jsonTut = gson.toJson(in);
        Log.e("data", jsonTut);
        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService(this).addOrder(in);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {
                        Globals.SelectedItems.clear();
                        Toasty.success(AddOrderAct.this, "Add Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toasty.warning(AddOrderAct.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    }
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(AddOrderAct.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toasty.error(AddOrderAct.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean valiadtion(String businessPartnerValue, String salesEmployeeCode, String poDate, String vDate, String docDate, String contactPersonCode, EditText purchaseOrderNum) {
        if (businessPartnerValue.isEmpty()) {
//            binding.quotationGeneralContent.businessPartnerValue.requestFocus();
//            binding.quotationGeneralContent.businessPartnerValue.setError("Select Business Partner");
            Globals.showMessage(act, "Select Business Partner");
            return false;
        } else if (salesEmployeeCode.isEmpty()) {
            Globals.showMessage(act, "Select Sales Representative is Required ! ");
            return false;
        } else if (vDate.isEmpty()) {
            Globals.showMessage(act, "Enter Valid date");
            return false;
        } else if (docDate.isEmpty()) {
            Globals.showMessage(act, "Enter Document date");
            return false;
        } else if (poDate.isEmpty()) {
            Globals.showMessage(act, "Enter Posting date");
            return false;
        } else if (contactPersonCode.isEmpty()) {
            Globals.showMessage(act, "Select Contact Person");
            return false;
        } else if (purchaseOrderNum.getText().toString().trim().isEmpty()) {
            Globals.showMessage(act, "Required Purchase Order Num");
            return false;
        }

        return true;
    }


    private boolean valiadtion(String contactPerson, String postDate, String validDate,
                               String DocDate, String remarks) {
        if (contactPerson.isEmpty()) {
            Globals.showMessage(act, "Select Contact Person");
            return false;
        } else if (validDate.isEmpty()) {
            Globals.showMessage(act, "Enter Valid date");
            return false;
        } else if (DocDate.isEmpty()) {
            Globals.showMessage(act, "Enter Document date");
            return false;
        } else if (postDate.isEmpty()) {
            Globals.showMessage(act, "Enter Posting date");
            return false;
        } else if (remarks.isEmpty()) {
            Globals.showMessage(act, "Enter Remarks");
            return false;
        }
        return true;
    }
}