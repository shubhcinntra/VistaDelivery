package com.cinntra.vistadelivery.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.BussinessPartners;
import com.cinntra.vistadelivery.activities.Opportunities_Pipeline_Activity;
import com.cinntra.vistadelivery.activities.SelectedItems;
import com.cinntra.vistadelivery.adapters.CountryAdapter;
import com.cinntra.vistadelivery.adapters.PaymentAdapter;
import com.cinntra.vistadelivery.adapters.SalesEmployeeAdapter;
import com.cinntra.vistadelivery.adapters.SalesEmployeeAutoAdapter;
import com.cinntra.vistadelivery.adapters.StateAdapter;
import com.cinntra.vistadelivery.adapters.bpAdapters.ContactPersonAutoAdapter;
import com.cinntra.vistadelivery.databinding.EditQuotationBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.AddressExtensions;
import com.cinntra.vistadelivery.model.AttachmentResponseModel;
import com.cinntra.vistadelivery.model.BPModel.BPAllFilterRequestModel;
import com.cinntra.vistadelivery.model.BPModel.BranchOneResponseModel;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.cinntra.vistadelivery.model.ContactPerson;
import com.cinntra.vistadelivery.model.ContactPersonData;
import com.cinntra.vistadelivery.model.CountryData;
import com.cinntra.vistadelivery.model.CountryResponse;
import com.cinntra.vistadelivery.model.DocumentLines;
import com.cinntra.vistadelivery.model.OpportunityModels.OppAddressResponseModel;
import com.cinntra.vistadelivery.model.PayMentTerm;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.QuotationOneAPiModel;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.QuotationUpdateModel;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.UpdateProformaInvoiceRequestModel;
import com.cinntra.vistadelivery.model.QuotationDocumentLines;
import com.cinntra.vistadelivery.model.QuotationItem;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.ResponseCompanyBranchAllFilter;
import com.cinntra.vistadelivery.model.SalesEmployeeItem;
import com.cinntra.vistadelivery.model.StateData;
import com.cinntra.vistadelivery.model.StateRespose;
import com.cinntra.vistadelivery.model.TokenExpireModel;
import com.cinntra.vistadelivery.model.UpdateQTDocumentLines;
import com.cinntra.vistadelivery.model.UpdateQuotationModel;
import com.cinntra.vistadelivery.newapimodel.LeadResponse;
import com.cinntra.vistadelivery.newapimodel.NewOpportunityRespose;
import com.cinntra.vistadelivery.room.CountriesDatabase;
import com.cinntra.vistadelivery.spinneradapter.BranchBusinessPartnerTypeSearchableSpinnerAdapter;
import com.cinntra.vistadelivery.spinneradapter.BranchTypeAllSearchableSpinnerAdapter;
import com.cinntra.vistadelivery.viewModel.ItemViewModel;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class Quotation_Update_Fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Quotation_Update_Fragme";
    public static int SelectItemCode = 101;
    private String QT_ID = "";
    public static String CardValue;
    public static String salePCode;

    UpdateQuotationModel addQuotationObj;
    FragmentActivity act;
    CountriesDatabase db = CountriesDatabase.getDatabase(getActivity());
    ArrayList localData = new ArrayList<>();

    public static boolean ESCAPED = true;
    public static boolean DISABLED = true;
    String[] shippinngType;
    //    String[] billingType;
    String billtoState, billtoCountrycode, billtoCountryName, shiptoState, shiptoCountrycode, shiptoCountryName, shiptoStateCode, billtoStateCode;
    CountryAdapter countryAdapter;
    StateAdapter stateAdapter, shipstateAdapter;
    ArrayList<StateData> stateList = new ArrayList<>();
    ArrayList<StateData> shipstateList = new ArrayList<>();

    EditQuotationBinding binding;

    UpdateProformaInvoiceRequestModel updateProformaInvoiceRequestModel;

    String ContactPersonCode = "";
    String ContactPerson = "";
    String salesPersonCode = "";
    String billshiptype = "";
    String ship_shiptype = "";
    String payment_term = "";

    String Flag = "";
    NewOpportunityRespose oppItemLine = new NewOpportunityRespose();
    String OPPID = "";
    String OPP_Name = "";
    public static String CardName;
    public String salesEmployeeCode = "";
    public String salesEmployeeName = "";

    double totalAfterItemDiscount = 0;
    private static final int RESULT_LOAD_IMAGE = 102;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    File file;
    String picturePath = "";


    public Quotation_Update_Fragment() {
        //Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Quotation_Update_Fragment newInstance(String param1, String param2) {
        Quotation_Update_Fragment fragment = new Quotation_Update_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    QuotationItem quotationItem1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            quotationItem1 = (QuotationItem) b.getSerializable(Globals.QuotationItem);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        act = getActivity();
        binding = EditQuotationBinding.inflate(inflater, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


        /*binding.quotationGeneralContent.companyNameCard.setVisibility(View.VISIBLE);
        binding.headerBottomroundEdit.headTitle.setText(getString(R.string.quotation_update));
        binding.tab1.setOnClickListener(this);
        binding.tab3.setOnClickListener(this);
        binding.tab2.setOnClickListener(this);
        binding.general.setOnClickListener(this);
        binding.quotationTotalContent.itemFrame.setOnClickListener(this);
        binding.headerBottomroundEdit.backPress.setOnClickListener(this);

        setDisable();
        setData();*/


        binding.loader.loader.setVisibility(View.GONE);
        binding.headerBottomroundEdit.add.setVisibility(View.GONE);
        binding.headerBottomroundEdit.ok.setVisibility(View.GONE);
        binding.headerBottomroundEdit.headTitle.setText(getString(R.string.quotation_update));

        binding.quotationGeneralContent.quotationAttachmentLayout.setVisibility(View.GONE);

//        shippinngType = getActivity().getResources().getStringArray(R.array.bpShippingType);

        shippinngType = getResources().getStringArray(R.array.bpShippingType);
        ship_shiptype = shippinngType[0];
//        billingType = getResources().getStringArray(R.array.bpBillingType);
        billshiptype = shippinngType[0];

        if (Globals.checkInternet(getActivity())) {
            callQuotationOneApi();
            //todo calling country api here---
            callCountryApi();
            callPaymentApi();
            setUpBusinessPartnerbranchTypeSpinner();

        }


        updateProformaInvoiceRequestModel = new UpdateProformaInvoiceRequestModel();

        binding.tab2.setOnClickListener(this);
        binding.tab3.setOnClickListener(this);
        binding.tab1.setOnClickListener(this);
        binding.general.setOnClickListener(this);
        binding.quotationTotalContent.itemFrame.setOnClickListener(this);
        binding.headerBottomroundEdit.backPress.setOnClickListener(this);

//        binding.quotationTotalContent.discontValue.addTextChangedListener(NumberTextWatcher);


        setClickListeners();
        //todo bill to and ship to address drop down item select..


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


        //todo bill to state item click..
        binding.quotationAddressContent.addressSection.acBillToState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                billtoState = stateList.get(position).getName();
                billtoStateCode = stateList.get(position).getCode();

                binding.quotationAddressContent.addressSection.acBillToState.setText(stateList.get(position).getName());
            }
        });


        //todo ship to state item click..
        binding.quotationAddressContent.addressSection.acShipToState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shiptoState = shipstateList.get(position).getName();
                shiptoStateCode = shipstateList.get(position).getCode();

                binding.quotationAddressContent.addressSection.acShipToState.setText(shipstateList.get(position).getName());
            }
        });


        //todo click on atacment --
        binding.quotationGeneralContent.quotAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentDispatcher();
            }
        });


        return binding.getRoot();
    }


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
    }


    private void setClickListeners() {

        binding.general.setOnClickListener(this);
        binding.total.setOnClickListener(this);
        binding.quotationGeneralContent.submit.setOnClickListener(this);
        binding.address.setOnClickListener(this);
        binding.headerBottomroundEdit.add.setOnClickListener(this);
        binding.quotationTotalContent.nextButton.setOnClickListener(this);
        binding.quotationAddressContent.addressSection.doneButton.setOnClickListener(this);
        binding.headerBottomroundEdit.ok.setOnClickListener(this);
        binding.quotationGeneralContent.postCal.setOnClickListener(this);
        binding.quotationGeneralContent.validCal.setOnClickListener(this);
        binding.quotationGeneralContent.docCal.setOnClickListener(this);
        binding.quotationGeneralContent.postingDate.setOnClickListener(this);
        binding.quotationGeneralContent.validDate.setOnClickListener(this);
        binding.quotationGeneralContent.documentDate.setOnClickListener(this);
        binding.quotationGeneralContent.edCreatedDate.setOnClickListener(this);
        binding.quotationGeneralContent.validTillValue.setOnClickListener(this);
        binding.quotationGeneralContent.documentDateValue.setOnClickListener(this);
//        binding.quotationGeneralContent.businessPartnerValue.setOnClickListener(this);
        binding.quotationGeneralContent.bussinessPartner.setOnClickListener(this);
        binding.quotationGeneralContent.opportunityNameValue.setOnClickListener(this);
        binding.quotationGeneralContent.oppView.setOnClickListener(this);
//        bpView.setVisibility(View.GONE);

    }


    private ArrayList<ContactPersonData> ContactEmployeesList;

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {

            case R.id.ok:
                String oppname = binding.quotationGeneralContent.opportunityNameValue.getText().toString().trim();
                String poDate = binding.quotationGeneralContent.edCreatedDate.getText().toString().trim();
                String vDate = binding.quotationGeneralContent.validTillValue.getText().toString().trim();
                String discount = binding.quotationTotalContent.discontValue.getText().toString().trim();
                String docDate = binding.quotationGeneralContent.documentDateValue.getText().toString().trim();
                String remark = binding.quotationGeneralContent.remarkValue.getText().toString().trim();

                String businessPartnerValue = binding.quotationGeneralContent.businessPartnerValue.getText().toString().trim();

                if (valiadtion(businessPartnerValue, salesEmployeeCode, oppItemTempList_gl, Globals.SelectedItems.size(), binding.quotationGeneralContent.quoNamevalue.getText().toString(), ContactPersonCode, poDate)) {
                    getQuotationDocLin();

                    try {
                        updateProformaInvoiceRequestModel.setId(quotationItem.getId());
                        updateProformaInvoiceRequestModel.setU_QUOTNM(binding.quotationGeneralContent.quoNamevalue.getText().toString().trim());
                       updateProformaInvoiceRequestModel.setU_QUOTID(quotationItem.getId());
                        updateProformaInvoiceRequestModel.setQuoteNo(binding.quotationGeneralContent.edQuoteNo.getText().toString().trim());
                        updateProformaInvoiceRequestModel.setU_OPPRNM(OPP_Name);
                        updateProformaInvoiceRequestModel.setU_OPPID(OPPID);
                        updateProformaInvoiceRequestModel.setCardCode(mCardCode);
                        updateProformaInvoiceRequestModel.setCardName(CardName);
                        updateProformaInvoiceRequestModel.setContactPersonCode(ContactPersonCode);
                        updateProformaInvoiceRequestModel.setSalesPersonCode(salesEmployeeCode);
                        updateProformaInvoiceRequestModel.setComments(remark);

                        updateProformaInvoiceRequestModel.setTaxDate(quotationItem.getTaxDate());
                        updateProformaInvoiceRequestModel.setDocDueDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(vDate));
                        updateProformaInvoiceRequestModel.setDocDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(docDate));

                        updateProformaInvoiceRequestModel.setCreateDate(quotationItem.getCreateDate());
                        updateProformaInvoiceRequestModel.setCreateTime(quotationItem.getCreateTime());
                        updateProformaInvoiceRequestModel.setUpdateDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(poDate));
                        updateProformaInvoiceRequestModel.setUpdateTime(Globals.getTCurrentTime());


                        updateProformaInvoiceRequestModel.setDepartement("2");
                        updateProformaInvoiceRequestModel.setPRID("");

                        //todo new key
                        updateProformaInvoiceRequestModel.setBPLID(branchTypeSelected);
                        updateProformaInvoiceRequestModel.setPaymentGroupCode(payment_term);


                        /***** Total items calculations *****/
                        updateProformaInvoiceRequestModel.setDocTotal(Float.parseFloat(binding.quotationTotalContent.totalBeforeDiscontValue.getText().toString()));

                        if (!discount.isEmpty())
                            updateProformaInvoiceRequestModel.setDiscountPercent(Float.parseFloat(discount));
                        else updateProformaInvoiceRequestModel.setDiscountPercent(0);


                        //todo add items lines.
                        ArrayList<DocumentLines> documentLineArrayList = new ArrayList<>();

                        if (oppItemTempList_gl.size() > 0) {
                            for (int j = 0; j < oppItemTempList_gl.size(); j++) {
                                DocumentLines documentLines = new DocumentLines();
                                documentLines.setQuantity(oppItemTempList_gl.get(j).getQuantity());
                                documentLines.setDiscountPercent(oppItemTempList_gl.get(j).getDiscountPercent());
                                documentLines.setUnitPrice(oppItemTempList_gl.get(j).getUnitPrice());
                                documentLines.setItemCode(oppItemTempList_gl.get(j).getItemCode());
                                documentLines.setQuotationID(oppItemTempList_gl.get(j).getQuotationID());
                             /*   if (oppItemTempList_gl.get(j).getItemName() != "") {
                                    documentLines.setItemDescription(oppItemTempList_gl.get(j).getItemName());

                                    documentLines.setTaxCode(oppItemTempList_gl.get(j).getTaxCode());
                                    documentLines.setTax(oppItemTempList_gl.get(j).getTax());
                                } else {
                                    documentLines.setQuotationID(oppItemTempList_gl.get(j).getQuotationID());
                                    documentLines.setItemDescription(quotationItem.getDocumentLines().get(j).getItemDescription());
                                    documentLines.setId(oppItemTempList_gl.get(j).getId());
                                    documentLines.setTaxCode(oppItemTempList_gl.get(j).getTaxCode());
                                    documentLines.setTax(oppItemTempList_gl.get(j).getTax());//oppItemTempList_gl.get(j).getTax()
                                }*/

                                documentLines.setItemDescription(oppItemTempList_gl.get(j).getItemDescription());
                                documentLines.setId(oppItemTempList_gl.get(j).getId());
                                documentLines.setTaxCode(oppItemTempList_gl.get(j).getTaxCode());
                                documentLines.setTax(oppItemTempList_gl.get(j).getTax());//oppItemTempList_gl.get(j).getTax()
                                documentLines.setTaxRate(oppItemTempList_gl.get(j).getTaxRate());//oppItemTempList_gl.get(j).getTax()


                                documentLineArrayList.addAll(Collections.singleton(documentLines));
                            }
                        } else {
                            for (int j = 0; j < quotationItem.getDocumentLines().size(); j++) {
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
                        }

                        updateProformaInvoiceRequestModel.setDocumentLines(documentLineArrayList);

                        //todo new keys
                        updateProformaInvoiceRequestModel.setFreightCharge(binding.quotationTotalContent.etFrieghtCharge.getText().toString().trim());


                        /***** Add Address payload *****/
                        AddressExtensions addressExtension = new AddressExtensions();
                        addressExtension.setId(quotationItem.getAddressExtension().getId());
                        addressExtension.setQuotationID(quotationItem.getAddressExtension().getQuotationID());
                        addressExtension.setShipToBuilding(binding.quotationAddressContent.addressSection.shippingNameValue.getText().toString());
                        addressExtension.setShipToStreet(binding.quotationAddressContent.addressSection.shippingAddressValue.getText().toString());
                        addressExtension.setShipToCity(binding.quotationAddressContent.addressSection.shipcityValue.getText().toString());
                        addressExtension.setShipToZipCode(binding.quotationAddressContent.addressSection.zipcodeValue2.getText().toString());
                        addressExtension.setShipToState(shiptoStateCode);
                        addressExtension.setShipToCountry(shiptoCountrycode);
                        addressExtension.setU_SSTATE(shiptoState);
                        addressExtension.setU_SCOUNTRY(shiptoCountryName);
                        addressExtension.setU_SHPTYPS(ship_shiptype);

                        addressExtension.setBillToBuilding(binding.quotationAddressContent.addressSection.billingNameValue.getText().toString());
                        addressExtension.setBillToStreet(binding.quotationAddressContent.addressSection.billingAddressValue.getText().toString());
                        addressExtension.setBillToCity(binding.quotationAddressContent.addressSection.cityValue.getText().toString());
                        addressExtension.setBillToZipCode(binding.quotationAddressContent.addressSection.zipCodeValue.getText().toString());
                        addressExtension.setBillToState(billtoStateCode);
                        addressExtension.setBillToCountry(billtoCountrycode);
                        addressExtension.setU_BSTATE(billtoState);
                        addressExtension.setU_BCOUNTRY(billtoCountryName);
                        addressExtension.setU_SHPTYPB(billshiptype);
                        updateProformaInvoiceRequestModel.setAddressExtension(addressExtension);


                        if (Globals.checkInternet(getActivity())) {
                            updateQuotation(QT_ID, updateProformaInvoiceRequestModel);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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
                frameManager(binding.generalFrame, binding.totalFrame, binding.preparedFrame, binding.general, binding.total, binding.address);
                break;
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
            case R.id.edCreatedDate:
//                if (DISABLED)
                Globals.disablePastSelectDate(getContext(), binding.quotationGeneralContent.edCreatedDate);
                break;
            case R.id.valid_till_value:
            case R.id.validDate:
            case R.id.validCal:
//                if (DISABLED)
                Globals.disablePastSelectDate(getContext(), binding.quotationGeneralContent.validTillValue);
                break;
            case R.id.document_date_value:
            case R.id.docCal:
            case R.id.documentDate:
//                if (DISABLED)
                Globals.disablePastSelectDate(getContext(), binding.quotationGeneralContent.documentDateValue);
                break;
            case R.id.submit:
                String opp_name = binding.quotationGeneralContent.opportunityNameValue.getText().toString().trim();
//                if (validation(opp_name, remark_value.getText().toString().trim(), ContactPersonCode)) {
                frameManager(binding.totalFrame, binding.generalFrame, binding.preparedFrame, binding.total, binding.general, binding.address);
//                }
                break;
            case R.id.next_button:
                frameManager(binding.preparedFrame, binding.generalFrame, binding.totalFrame, binding.address, binding.general, binding.total);
                break;
            case R.id.done_button:
                binding.headerBottomroundEdit.ok.performClick();
                break;
            case R.id.item_frame:
                Globals.hideKeybaord(v, getContext());
                /*if (DISABLED) {
                    Globals.inventory_item_close = false;
                    Intent intent = new Intent(act, SelectedItems.class);
                    intent.putExtra("CardCode", mCardCode);
                    intent.putExtra("FromWhere", "QT_UP");
                    startActivityForResult(intent, SelectItemCode);
                } else {
                    Globals.inventory_item_close = true;
                    Intent intent = new Intent(act, SelectedItems.class);
                    intent.putExtra("CardCode", mCardCode);
                    intent.putExtra("FromWhere", "invoices");
                    startActivityForResult(intent, SelectItemCode);
                }*/

                Globals.inventory_item_close = false;
                Intent intent = new Intent(act, SelectedItems.class);
                intent.putExtra("CardCode", mCardCode);
                intent.putExtra("FromWhere", "QT_UP");
                startActivityForResult(intent, SelectItemCode);

                break;

            case R.id.opportunity_name_value:
            case R.id.opp_view:
                selectOpportunity();
                break;

            case R.id.bussinessPartner:
            case R.id.business_partner_value:
                selectBPartner();

                break;

        }


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


        jsonObject.addProperty("BPCode", mCardCode);
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
                        }

                        BranchTypeAllSearchableSpinnerAdapter sourceSearchableSpinnerAdapter = new BranchTypeAllSearchableSpinnerAdapter(requireContext(), branchTypeAddressDataList);

                        binding.quotationAddressContent.addressSection.saerchableSpinnerBillingAddress.setAdapter(sourceSearchableSpinnerAdapter);
                        binding.quotationAddressContent.addressSection.saerchableSpinnerShippingAddress.setAdapter(sourceSearchableSpinnerAdapter);


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


    public static int PARTNERCODE = 10000;
    public static int OPPCODE = 1001;

    private void selectBPartner() {
        Prefs.putString(Globals.BussinessPageType, "Quotation");
        Intent i = new Intent(act, BussinessPartners.class);
        startActivityForResult(i, PARTNERCODE);
    }

    private void selectOpportunity() {
        Prefs.putString(Globals.SelectOpportnity, "Quotation");
        Intent in = new Intent(act, Opportunities_Pipeline_Activity.class);
        startActivityForResult(in, OPPCODE);
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


    //todo calling quotation one api here for show particular details..
    QuotationOneAPiModel.Data quotationItem = null;

    private void callQuotationOneApi() {
        binding.loader.loader.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", quotationItem1.getId());

        Call<QuotationOneAPiModel> call = NewApiClient.getInstance().getApiService(requireContext()).oneQuotationApi1(jsonObject);
        call.enqueue(new Callback<QuotationOneAPiModel>() {
            @Override
            public void onResponse(Call<QuotationOneAPiModel> call, Response<QuotationOneAPiModel> response) {
                try {
                    if (response.isSuccessful()) {
                        binding.loader.loader.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().getStatus() == 200) {
                                if (response.body().getValue().size()>0){
                                    quotationItem = response.body().getValue().get(0);
                                    branchTypeSelected = quotationItem.getBPLID();
                                    payment_term = quotationItem.getPaymentGroupCode();
                                    binding.quotationTotalContent.etFrieghtCharge.setText(quotationItem.getFreightCharge());

                                    binding.quotationGeneralContent.saerchableSpinnerBranch.setSelection(Globals.getCompanyBranchPo(branchTypeDataList, branchTypeSelected));
                                    binding.quotationGeneralContent.paymentTermValue.setSelection(Globals.getPaymentTermPo(getPaymenterm, payment_term));

                                    callSalessApi();
                                }



//                                setData();

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
            public void onFailure(Call<QuotationOneAPiModel> call, Throwable t) {
                Log.e("TAG_APi_failure", "onFailure: " + t.getMessage());
                binding.loader.loader.setVisibility(View.GONE);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //todo select attachment ---
    private void intentDispatcher() {
        checkAndRequestPermissions();
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE);
    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }


    //todo quotation Attachment api calling---
    private void callQuotationAttachmentApi(String qt_id) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //todo get model data in multipart body request..
        builder.addFormDataPart("QuotationID", qt_id.trim());
        try {
            if (picturePath.isEmpty()) {
                builder.addFormDataPart("File", "");
            } else {
                builder.addFormDataPart("File", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            }
        } catch (Exception e) {
            Log.d("TAG===>", "AddQuotationApi: ");
        }

        MultipartBody requestBody = builder.build();

        Call<AttachmentResponseModel> call = NewApiClient.getInstance().getApiService(requireContext()).postAttachmentUploadApi(requestBody);
        call.enqueue(new Callback<AttachmentResponseModel>() {
            @Override
            public void onResponse(Call<AttachmentResponseModel> call, Response<AttachmentResponseModel> response) {

                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {
                        callQuotationOneApi();
                        Log.d("AttachmentResponse =>", "onResponse: Successful");
                    } else {
                        Log.d("AttachmentNot200Status", "onResponse: QuotAttachmentNot200Status");
                    }

                } else {
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                        Toast.makeText(getActivity(), mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachmentResponseModel> call, Throwable t) {
                Log.e("TAG_Attachment_Api", "onFailure: AttachmentAPi");
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SelectItemCode) {
            binding.quotationTotalContent.itemsCount.setText("Item (" + Globals.SelectedItems.size() + ")");

            totalAfterItemDiscount = Double.parseDouble(String.valueOf(Globals.foo(Globals.calculateTotalOfItem(Globals.SelectedItems))));

            binding.quotationTotalContent.totalBeforeDiscontValue.setText(String.valueOf(Globals.foo(Globals.calculateTotalOfItemAfterHeaderDiswithFreightCharge(Globals.SelectedItems, Double.parseDouble(quotationItem.getDiscountPercent()), quotationItem.getFreightCharge()))));

            getQuotationDocLin();
        }
        //todo for attachment selected---

        else if (requestCode == RESULT_LOAD_IMAGE) {
            Log.e(TAG, "onActivityResult: ");

            if (resultCode == RESULT_OK && data != null) {
                Bundle extras = data.getExtras();
                Uri selectedImage = data.getData();
//                ivQuotationImageSelected.setImageURI(selectedImage);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Log.e("picturePath", picturePath);
                    file = new File(picturePath);

                    callQuotationAttachmentApi(quotationItem1.getId());
                    Log.e("FILE>>>>", "onActivityResult: " + file.getName());
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == PARTNERCODE) {
            BusinessPartnerAllResponse.Datum customerItem = (BusinessPartnerAllResponse.Datum) data.getSerializableExtra(Globals.CustomerItemData);
            callBPOneAPi(customerItem.getCardCode());
//            setData(customerItem);
        } else if (resultCode == RESULT_OK && requestCode == OPPCODE) {
            NewOpportunityRespose oppItem = Globals.opp;
            if (oppItem != null) {
                oppItemLine = oppItem;
                setOppData(oppItem);
//            OPPID = oppItem.getId();
            }
        }


    }


    //todo calling bp one api here...
    private void callBPOneAPi(String BPCardCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CardCode", BPCardCode);
        Call<BusinessPartnerAllResponse> call = NewApiClient.getInstance().getApiService(requireContext()).callBPOneAPi(jsonObject);
        call.enqueue(new Callback<BusinessPartnerAllResponse>() {
            @Override
            public void onResponse(Call<BusinessPartnerAllResponse> call, Response<BusinessPartnerAllResponse> response) {
                if (response.body().getStatus() == 200) {
                    if (response.body().getData().size()>0){
                        BusinessPartnerAllResponse.Datum customerItem = response.body().getData().get(0);
                        binding.quotationGeneralContent.businessPartnerValue.setText(customerItem.getCardName());
                        setBPData(customerItem);
                    }





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

    String mCardCode = "";


    //todo get opportunity data for duplicate quot---
    @SuppressLint("ResourceType")
    private void setOppData(NewOpportunityRespose oppdata) {
        binding.quotationGeneralContent.businessPartnerValue.setTextColor(Color.parseColor(getString(R.color.black)));
        binding.quotationGeneralContent.opportunityNameValue.setClickable(true);
        binding.quotationGeneralContent.opportunityNameValue.setEnabled(true);
        binding.quotationGeneralContent.opportunityNameValue.setTextColor(Color.parseColor(getString(R.color.black)));
        binding.quotationGeneralContent.oppView.setClickable(true);
        binding.quotationGeneralContent.oppView.setEnabled(true);
        OPPID = oppdata.getId();
        OPP_Name = oppdata.getOpportunityName();
        binding.quotationGeneralContent.opportunityNameValue.setText(oppdata.getOpportunityName());
        binding.quotationGeneralContent.businessPartnerValue.setText(oppdata.getCustomerName());

//        callBPBillToAddressApi(oppdata.getCardCode(), oppdata.getBillToID());
//        callBPShipToAddressApi(oppdata.getCardCode(), oppdata.getShipToID());

        mCardCode = oppdata.getCardCode();
        CardName = oppdata.getCustomerName();
        salePCode = oppdata.getContactPerson();
        salesEmployeeCode = oppdata.getSalesPerson();


    }


    //todo set data from Bp selected
    private void setBPData(BusinessPartnerAllResponse.Datum customerItem) {

        Prefs.putString(Globals.BusinessPartnerCardCode, "");

        mCardCode = customerItem.getCardCode();
        CardName = customerItem.getCardName();

        callContactEmployeeApi(customerItem.getCardCode());


        Prefs.putString(Globals.BusinessPartnerCardCode, customerItem.getCardCode());


        binding.quotationGeneralContent.businessPartnerValue.setText(customerItem.getCardName());

        binding.quotationGeneralContent.purchaseDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.disablePastSelectDate(getContext(),binding.quotationGeneralContent.purchaseDateValue);
            }
        });


        if (customerItem.getSalesPersonCode().size() > 0 || !customerItem.getSalesPersonCode().isEmpty()) {
            binding.quotationGeneralContent.acSalesEmployee.setText(customerItem.getSalesPersonCode().get(0).getSalesEmployeeName());
            salesEmployeeCode = customerItem.getSalesPersonCode().get(0).getSalesEmployeeCode();
            salesEmployeeName = customerItem.getSalesPersonCode().get(0).getSalesEmployeeName();
        } else {
            binding.quotationGeneralContent.acSalesEmployee.setHint("NA");
            salesEmployeeCode = "";
            salesEmployeeName = "";
        }


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


        binding.quotationGeneralContent.paymentTermValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (getPaymenterm.size() > 0)
                    payment_term = getPaymenterm.get(position).getGroupNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (getPaymenterm.size()>0){
                    payment_term = getPaymenterm.get(0).getGroupNumber();
                }


            }
        });


    }


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


    String FLAG = "";

    //todo set default data--
    public void setDefaultData() {

        callCountryApi();

        binding.quotationGeneralContent.opportunityNameValue.setEnabled(false);
        binding.quotationGeneralContent.businessPartnerValue.setEnabled(false);
        binding.quotationGeneralContent.businessPartnerValue.setClickable(false);
        binding.quotationGeneralContent.bussinessPartner.setEnabled(false);
        binding.quotationGeneralContent.bussinessPartner.setClickable(false);
        binding.quotationGeneralContent.acContactPerson.setEnabled(true);
        binding.quotationGeneralContent.acSalesEmployee.setEnabled(false);
        binding.quotationGeneralContent.acSalesEmployee.setClickable(false);
        binding.quotationGeneralContent.quoNamevalue.setText(quotationItem.getU_QUOTNM());
        binding.quotationGeneralContent.edQuoteNo.setText(quotationItem.getQuoteNo());
        binding.quotationGeneralContent.opportunityNameValue.setText(quotationItem.getOpportunityName());
        binding.quotationGeneralContent.businessPartnerValue.setText(quotationItem.getCardName());

        OPPID = quotationItem.getU_OPPID();
        OPP_Name = quotationItem.getOpportunityName();
        CardName = quotationItem.getCardName();
        mCardCode = quotationItem.getCardCode();


        setUpBranchAllSpinner();
        if (!quotationItem.getContactPersonCode().isEmpty()) {
            binding.quotationGeneralContent.acContactPerson.setText(quotationItem.getContactPersonCodeDetails().get(0).getFirstName());
            ContactPersonCode = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getId());
            ContactPerson = String.valueOf(quotationItem.getContactPersonCodeDetails().get(0).getFirstName());
        } else {
            binding.quotationGeneralContent.acContactPerson.setText("NA");
        }

        Prefs.putString(Globals.BusinessPartnerCardCode, quotationItem.getCardCode());

        if (!quotationItem.getTaxDate().isEmpty()) {
            binding.quotationGeneralContent.edCreatedDate.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getCreateDate()));
        } else {
            binding.quotationGeneralContent.edCreatedDate.setText("NA");
        }

        //todo valid date
        binding.quotationGeneralContent.validTillValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getDocDueDate()));

        //todo docdate
        binding.quotationGeneralContent.documentDateValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(quotationItem.getDocDate()));


        if (!quotationItem.getComments().isEmpty()) {
            binding.quotationGeneralContent.remarkValue.setText(quotationItem.getComments());
        } else {
            binding.quotationGeneralContent.remarkValue.setText("NA");
        }

        //todo sales employee set---

        if (!quotationItem.getSalesPersonCode().isEmpty()) {
            binding.quotationGeneralContent.acSalesEmployee.setText(quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeName());
            salesEmployeeCode = quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeCode();
            salesEmployeeName = quotationItem.getSalesPersonCodeDetails().get(0).getSalesEmployeeName();
        } else {
            binding.quotationGeneralContent.acSalesEmployee.setHint("NA");
            salesEmployeeCode = "";
            salesEmployeeName = "";
        }


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


        //todo set document items line..
        Globals.SelectedItems.clear();

        Globals.SelectedItems.addAll(quotationItem.getDocumentLines());

        binding.quotationTotalContent.itemsCount.setText("Item (" + Globals.SelectedItems.size() + ")");

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

        //todo set bill to address data--
        if (quotationItem.getAddressExtension() != null) {

            if (!quotationItem.getAddressExtension().getBillToBuilding().isEmpty()) {
                binding.quotationAddressContent.addressSection.billingNameValue.setText(quotationItem.getAddressExtension().getBillToBuilding());
            } else {
                binding.quotationAddressContent.addressSection.billingNameValue.setText("NA");
            }
            if (!quotationItem.getAddressExtension().getBillToZipCode().isEmpty()) {
                binding.quotationAddressContent.addressSection.zipCodeValue.setText(quotationItem.getAddressExtension().getBillToZipCode());
            } else {
                binding.quotationAddressContent.addressSection.zipCodeValue.setText("NA");
            }

            billtoStateCode = quotationItem.getAddressExtension().getBillToState().trim();
            billtoState = quotationItem.getAddressExtension().getU_BSTATE().trim();

            billtoCountryName = quotationItem.getAddressExtension().getU_BCOUNTRY();
            billtoCountrycode = quotationItem.getAddressExtension().getBillToCountry();

            if (!quotationItem.getAddressExtension().getU_BCOUNTRY().isEmpty()) {
                binding.quotationAddressContent.addressSection.acCountry.setText(quotationItem.getAddressExtension().getU_BCOUNTRY());
            } else {
                binding.quotationAddressContent.addressSection.acCountry.setText("NA");
            }
            if (!quotationItem.getAddressExtension().getU_BSTATE().isEmpty()) {
                binding.quotationAddressContent.addressSection.acBillToState.setText(quotationItem.getAddressExtension().getU_BSTATE());
            } else {
                binding.quotationAddressContent.addressSection.acBillToState.setText("NA");
            }

            billshiptype = quotationItem.getAddressExtension().getU_SHPTYPB();
            if (billshiptype != null || !billshiptype.equalsIgnoreCase("")) {
                binding.quotationAddressContent.addressSection.shippingSpinner.setSelection(Globals.getShipTypePo(shippinngType, billshiptype));
            }
            if (!quotationItem.getAddressExtension().getBillToCity().isEmpty()) {
                binding.quotationAddressContent.addressSection.cityValue.setText(quotationItem.getAddressExtension().getBillToCity());
            } else {
                binding.quotationAddressContent.addressSection.cityValue.setText("NA");
            }
            if (!quotationItem.getAddressExtension().getBillToStreet().isEmpty()) {
                binding.quotationAddressContent.addressSection.billingAddressValue.setText(quotationItem.getAddressExtension().getBillToStreet());
            } else {
                binding.quotationAddressContent.addressSection.billingAddressValue.setText("NA");
            }

            callBillToStateApi(billtoCountrycode);

        }


        //todo set ship to address data--
        if (quotationItem.getAddressExtension() != null) {

            if (!quotationItem.getAddressExtension().getShipToBuilding().isEmpty()) {
                binding.quotationAddressContent.addressSection.shippingNameValue.setText(quotationItem.getAddressExtension().getShipToBuilding());
            } else {
                binding.quotationAddressContent.addressSection.shippingNameValue.setText("NA");
            }
            if (!quotationItem.getAddressExtension().getShipToZipCode().isEmpty()) {
                binding.quotationAddressContent.addressSection.zipcodeValue2.setText(quotationItem.getAddressExtension().getShipToZipCode());
            } else {
                binding.quotationAddressContent.addressSection.zipcodeValue2.setText("NA");
            }

            shiptoStateCode = quotationItem.getAddressExtension().getShipToState().trim();
            shiptoState = quotationItem.getAddressExtension().getU_SSTATE().trim();

            shiptoCountryName = quotationItem.getAddressExtension().getU_SCOUNTRY();
            shiptoCountrycode = quotationItem.getAddressExtension().getShipToCountry();

            if (!quotationItem.getAddressExtension().getU_SCOUNTRY().isEmpty()) {
                binding.quotationAddressContent.addressSection.acShipCountry.setText(quotationItem.getAddressExtension().getU_SCOUNTRY());
            } else {
                binding.quotationAddressContent.addressSection.acShipCountry.setText("NA");
            }
            if (!quotationItem.getAddressExtension().getU_SSTATE().isEmpty()) {
                binding.quotationAddressContent.addressSection.acShipToState.setText(quotationItem.getAddressExtension().getU_SSTATE());
            } else {
                binding.quotationAddressContent.addressSection.acShipCountry.setText("NA");
            }

            ship_shiptype = quotationItem.getAddressExtension().getU_SHPTYPS();
            if (ship_shiptype != null || !ship_shiptype.equalsIgnoreCase("")) {
                binding.quotationAddressContent.addressSection.shippingSpinner2.setSelection(Globals.getShipTypePo(shippinngType, ship_shiptype));
            }
            if (!quotationItem.getAddressExtension().getShipToCity().isEmpty()) {
                binding.quotationAddressContent.addressSection.shipcityValue.setText(quotationItem.getAddressExtension().getShipToCity());
            } else {
                binding.quotationAddressContent.addressSection.shipcityValue.setText("NA");
            }
            if (!quotationItem.getAddressExtension().getShipToStreet().isEmpty()) {
                binding.quotationAddressContent.addressSection.shippingAddressValue.setText(quotationItem.getAddressExtension().getShipToStreet());
            } else {
                binding.quotationAddressContent.addressSection.shippingAddressValue.setText("NA");
            }

            callShipToStateApi(shiptoCountrycode);
        }


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


        /*********** Set Data In Content Section**************/
        frameManager(binding.generalFrame, binding.totalFrame, binding.preparedFrame, binding.general, binding.total, binding.address);

    }


    public List<SalesEmployeeItem> salesEmployeeItemList = new ArrayList<>();

    //todo call sales employee api..
    private void callSalessApi() {
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getSalesEmployeeList().observe(getActivity(), new Observer<List<SalesEmployeeItem>>() {
            @Override
            public void onChanged(@Nullable List<SalesEmployeeItem> itemsList) {
                if (itemsList == null || itemsList.size() == 0) {
                    Globals.setmessage(getActivity());
                } else {
                    salesEmployeeItemList = itemsList;
                    try {
                        binding.quotationGeneralContent.acSalesEmployee.setAdapter(new SalesEmployeeAutoAdapter(getContext(), R.layout.drop_down_textview, salesEmployeeItemList));


                        callContactEmployeeApi(quotationItem.getCardCode());
                        setDefaultData();
                    } catch (Exception e) {
                        Log.e(TAG, "onChanged: ");
                    }
                   /* if (!itemsList.isEmpty() && !salesEmployeeCode.isEmpty())
                        binding.quotationGeneralContent.salesemployeeSpinner.setSelection(Globals.getSelectedSalesP(itemsList, salesEmployeeCode));
*/
                }
            }
        });
    }


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


    //todo calling country api here...

    ArrayList<CountryData> countyList = new ArrayList<>();

    private void callCountryApi() {
        Call<CountryResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getCountryList();
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.body().getStatus() == 200) {
                    if (response.body().getData().size() > 0) {

                        countyList.clear();
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
                        binding.quotationAddressContent.addressSection.acCountry.setAdapter(adapter);


                        //todo bill to and ship to address drop down item select..
                        binding.quotationAddressContent.addressSection.acCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    String countryName = (String) parent.getItemAtPosition(position);
                                    billtoCountryName = countryName;

                                    int pos = Globals.getCountryCodePos(countyList, countryName);
                                    billtoCountrycode = countyList.get(pos).getCode();

                                    if (countryName.isEmpty()) {
                                        binding.quotationAddressContent.addressSection.rlRecyclerViewLayout.setVisibility(View.GONE);
                                        binding.quotationAddressContent.addressSection.rvCountryList.setVisibility(View.GONE);
                                    } else {
                                        binding.quotationAddressContent.addressSection.rlRecyclerViewLayout.setVisibility(View.VISIBLE);
                                        binding.quotationAddressContent.addressSection.rvCountryList.setVisibility(View.VISIBLE);
                                    }

                                    if (!countryName.isEmpty()) {
                                        adapter.notifyDataSetChanged();
                                        binding.quotationAddressContent.addressSection.acCountry.setText(countryName);
                                        binding.quotationAddressContent.addressSection.acCountry.setSelection(binding.quotationAddressContent.addressSection.acCountry.length());

                                        callBillToStateApi(billtoCountrycode);
                                    } else {
                                        billtoCountryName = "";
                                        billtoCountrycode = "";
                                        binding.quotationAddressContent.addressSection.acCountry.setText("");
                                    }
                                } catch (Exception e) {
                                    Log.e("catch", "onItemClick: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });

                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.drop_down_textview, itemNames);
                        binding.quotationAddressContent.addressSection.acShipCountry.setAdapter(adapter);

                        //todo set on ship country Item Click
                        binding.quotationAddressContent.addressSection.acShipCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    String countryName = (String) parent.getItemAtPosition(position);
                                    shiptoCountryName = countryName;

                                    int pos = Globals.getCountryCodePos(countyList, countryName);
                                    shiptoCountrycode = countyList.get(pos).getCode();

                                    if (countryName.isEmpty()) {
                                        binding.quotationAddressContent.addressSection.rlShipREcyclerLayout.setVisibility(View.GONE);
                                        binding.quotationAddressContent.addressSection.rvShipCountryList.setVisibility(View.GONE);
                                    } else {
                                        binding.quotationAddressContent.addressSection.rlShipREcyclerLayout.setVisibility(View.VISIBLE);
                                        binding.quotationAddressContent.addressSection.rvShipCountryList.setVisibility(View.VISIBLE);
                                    }

                                    if (!countryName.isEmpty()) {
                                        adapter1.notifyDataSetChanged();
                                        binding.quotationAddressContent.addressSection.acShipCountry.setText(countryName);
                                        binding.quotationAddressContent.addressSection.acShipCountry.setSelection(binding.quotationAddressContent.addressSection.acShipCountry.length());

                                        callShipToStateApi(shiptoCountrycode);
                                    } else {
                                        shiptoCountryName = "";
                                        shiptoCountrycode = "";
                                        binding.quotationAddressContent.addressSection.acShipCountry.setText("");
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


    private void frameManager(FrameLayout visiblle_frame, FrameLayout f1, FrameLayout f2, TextView selected, TextView t1, TextView t2) {
        selected.setTextColor(getResources().getColor(R.color.colorPrimary));
        t1.setTextColor(getResources().getColor(R.color.black));
        t2.setTextColor(getResources().getColor(R.color.black));

        visiblle_frame.setVisibility(View.VISIBLE);
        f1.setVisibility(View.GONE);
        f2.setVisibility(View.GONE);

    }

    private boolean validation(String cardCode, String stagesCode, String remark) {
        if (cardCode.isEmpty()) {
            Globals.showMessage(getContext(), getString(R.string.can_not_empty));
            return false;
        } else if (stagesCode.isEmpty()) {
            Globals.showMessage(getContext(), getString(R.string.can_not_empty));
            return false;
        } else if (remark.isEmpty()) {
            Globals.showMessage(getContext(), getString(R.string.can_not_empty));
            return false;
        }

        return true;
    }

    /******************* Update API *********************/


    private boolean valiadtion(String businessPartnerValue, String salesPersonCode, ArrayList<DocumentLines> oppItemTempList_gl, int size, String quotationName, String contactPersonCode, String poDate) {
        if (businessPartnerValue.isEmpty()) {
//            binding.quotationGeneralContent.businessPartnerValue.requestFocus();
//            binding.quotationGeneralContent.businessPartnerValue.setError("Select Business Partner");
            Globals.showMessage(act, "Select Business Partner");
            return false;
        }

      /*  else if (edQuoteNo.getText().toString().isEmpty()) {
            binding.quotationGeneralContent.edQuoteNo.requestFocus();
            binding.quotationGeneralContent.edQuoteNo.setError("Enter Quote No");
            Globals.showMessage(act, "Enter Quote No");
            return false;
        }*/

        else if (quotationName.isEmpty()) {
            Globals.showMessage(act, "Enter Quotation Name Required !");
            return false;
        } else if (salesPersonCode.isEmpty()) {
            Globals.showMessage(act, "Select Sales Employee is Required ! ");
            return false;
        } else if (contactPersonCode.isEmpty()) {
            Globals.showMessage(act, "Select Contact Person ! ");
            return false;
        }
        if (size <= 0 && oppItemTempList_gl.size() <= 0) {
            Globals.showMessage(getActivity(), "Select atleast 1 item");
            return false;
        } /*else if (billtoStateCode.isEmpty()) {
            Globals.showMessage(getActivity(), "State is Required !");
            return false;
        } else if (shiptoStateCode.isEmpty()) {
            Globals.showMessage(getActivity(), "State is Required !");
            return false;
        }*//* else if (quotationName.isEmpty()) {
            binding.quotationGeneralContent.acQuotationType.requestFocus();
            binding.quotationGeneralContent.acQuotationType.setError("Quotation Name Required !");
            Globals.showMessage(act, "Quotation Name Required !");
            return false;
        }*/ else if (ContactPersonCode.isEmpty()) {
            binding.quotationGeneralContent.acContactPerson.requestFocus();
            binding.quotationGeneralContent.acContactPerson.setError("Contact Person Name Required !");
            Globals.showMessage(act, "Contact Person Name Required !");
            return false;
        } else if (poDate.isEmpty()) {
            binding.quotationGeneralContent.edCreatedDate.requestFocus();
            binding.quotationGeneralContent.edCreatedDate.setError("Created Date is Required !");
            Globals.showMessage(act, "Created Date is Required !");
            return false;
        } /*else if (vDate.isEmpty()) {
            binding.quotationGeneralContent.validTillValue.requestFocus();
            binding.quotationGeneralContent.validTillValue.setError("Valid Date is Required !");
            Globals.showMessage(act, "Valid Date is Required !");
            return false;
        } else if (docDate.isEmpty()) {
            binding.quotationGeneralContent.documentDateValue.requestFocus();
            binding.quotationGeneralContent.documentDateValue.setError("Document Date is Required !");
            Globals.showMessage(act, "Document Date is Required !");
            return false;
        }*/
        return true;
    }

    private void updateQuotation(String QT_ID, UpdateProformaInvoiceRequestModel in) {
        Gson gson = new Gson();
        String jsonTut = gson.toJson(in);
        Log.e("data", jsonTut);
        binding.loader.loader.setVisibility(View.VISIBLE);
        Call<QuotationUpdateModel> call = NewApiClient.getInstance().getApiService(requireContext()).updateQuotation(in);
        call.enqueue(new Callback<QuotationUpdateModel>() {
            @Override
            public void onResponse(Call<QuotationUpdateModel> call, Response<QuotationUpdateModel> response) {
                binding.loader.loader.setVisibility(View.GONE);

                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {
                        Globals.SelectedItems.clear();
                        Toasty.success(act, "Updated Successfully.", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        Toasty.warning(act, response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<QuotationUpdateModel> call, Throwable t) {
                binding.loader.loader.setVisibility(View.GONE);
                Toasty.error(act, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /******************* Update API *********************/
    ArrayList<UpdateQTDocumentLines> postlist;

    private ArrayList<UpdateQTDocumentLines> postJson(ArrayList<QuotationDocumentLines> list) {
        postlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
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
            dc.setItemDescription(list.get(i).getItemName());
            dc.setUnitPrice(String.valueOf(list.get(i).getUnitPrice()));
            dc.setDiscountPercent(Float.valueOf("0.0"));
            dc.setQuotationID(quotationItem.getId());
            dc.setId(list.get(i).getId());
            postlist.add(dc);
        }

        return postlist;
    }

    float Total_Before_Disscount = 0;


    private float Total_Before_Disscount(ArrayList<DocumentLines> list) {
        float result = 0;
        for (int i = 0; i < list.size(); i++) {
            result = result + Float.parseFloat(list.get(i).getQuantity()) * list.get(i).getUnitPrice();
        }
        return result;
    }


    SalesEmployeeAdapter salesadapter;


}