package com.cinntra.vistadelivery.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.AddQuotationAct;
import com.cinntra.vistadelivery.activities.QuotationActivity;
import com.cinntra.vistadelivery.adapters.BPAddressAdapter;
import com.cinntra.vistadelivery.adapters.Quotation_Adapter;
import com.cinntra.vistadelivery.databinding.FragmentQuotesListBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.interfaces.CommentStage;
import com.cinntra.vistadelivery.interfaces.FragmentRefresher;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.cinntra.vistadelivery.model.DataBusinessPartnerDropDown;
import com.cinntra.vistadelivery.model.OpportunityModels.OppAddressResponseModel;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.PerformaInvoiceListRequestModel;
import com.cinntra.vistadelivery.model.QuotationItem;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.ResponseBusinessPartnerDropDown;
import com.cinntra.vistadelivery.model.ResponseModel;
import com.cinntra.vistadelivery.model.TokenExpireModel;
import com.cinntra.vistadelivery.newapimodel.LeadResponse;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Quotation_Open_Fragment extends Fragment implements View.OnClickListener, FragmentRefresher, CommentStage, Quotation_Adapter.AdapterCallback {

    private SearchView searchView;
    private Quotation_Adapter adapter;
    LinearLayoutManager layoutManager;
    int currentPage = 0;
    //    private boolean isLastPage = false;
    ArrayList<QuotationItem> quotationItemArrayList_gl;
    private boolean isLoading = false;
    boolean recallApi = true;
    private Context mContext;

    int page = 1;
    Boolean apicall = true;
    Boolean isScrollingpage = false;
    int maxItem = 10;
    private String searchTextValue = "";
    private String filterTextValue = "";


    public Quotation_Open_Fragment() {
        //Required empty public constructor

    }

    FragmentQuotesListBinding binding;
    String  BPCardCode = "";
    private boolean isCardCodePresent=false;
    private boolean isCreateDatePresent = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        binding = FragmentQuotesListBinding.inflate(getLayoutInflater());
        mContext = getActivity();
        quotationItemArrayList_gl = new ArrayList<>();


        //todo pagination for list..
        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                Log.e("current1--->", String.valueOf(totalskipCount(lastCompletelyVisibleItemPosition)));
                if (isScrollingpage && lastCompletelyVisibleItemPosition == quotationItemArrayList_gl.size() - 2 && apicall) {
                    page++;
                    Log.e("page--->", String.valueOf(page));
                    getQuotationListOtherPageApi(binding.loaderLayout.loader, maxItem, page, "", "", "");
                    isScrollingpage = false;
                } else {
                    recyclerView.setPadding(0, 0, 0, 0);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //it means we are scrolling
                    isScrollingpage = true;
                }
            }
        });


//        binding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                CardName = "";
//                solIdName = "";
//                fromDate = "";
//                toDate = "";
//                searchTextValue = "";
//                filterTextValue = "";
//                if (Globals.checkInternet(getActivity())) {
//                    page = 1;
//                    apicall = true;
//                    callApi(binding.loaderLayout.loader, maxItem, page, "", "", "", "");
//                } else{
//                    Toast.makeText(mContext, "No Internet", Toast.LENGTH_SHORT).show();
//                //    binding.swipeRefreshLayout.setRefreshing(false);
//                }
//
//            }
//        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getActivity().getIntent() != null) {
            BPCardCode  = getActivity().getIntent().getStringExtra("BPCardCodeShortCut");
            // Use the passed data as needed
        }
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isCardCodePresent=false;
                isCreateDatePresent=false;
                CardName = "";
                solIdName = "";
                fromDate = "";
                toDate = "";
                searchTextValue = "";
                filterTextValue = "";
                fromAmount = "";
                toAmount = "";
                if (Globals.checkInternet(getActivity())) {
                    page = 1;
                    apicall = true;
                    getQuotationListApi(binding.loaderLayout.loader, maxItem, page, "", "", "");
                } else{
                    Toast.makeText(mContext, "No Internet", Toast.LENGTH_SHORT).show();
                    //    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    //todo count items..
    int totalskipCount(int current) {
        int total = maxItem * page;
        return total;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Globals.checkInternet(getActivity())) {
            binding.loaderLayout.loader.setVisibility(View.VISIBLE);
            if (BPCardCode == null){
                isCardCodePresent=false;
                getQuotationListApi(binding.loaderLayout.loader, maxItem, page, "", "", "");
            }else {
                isCardCodePresent=true;
                getQuotationListApi(binding.loaderLayout.loader, maxItem, page, BPCardCode, "", "");
            }
        }

        setAdapter();
    }

    //todo set adapter for list..
    private void setAdapter() {
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter = new Quotation_Adapter(Quotation_Open_Fragment.this, getContext(), quotationItemArrayList_gl, Quotation_Open_Fragment.this);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
        }
    }


    /***********  API Calling of quotation list  ************/

    private void callApi(SpinKitView loader, int maxItem, int page, String cardCode, String fromDate, String toDate) {
        binding.noDatafound.setVisibility(View.GONE);

        loader.setVisibility(View.VISIBLE);

        PerformaInvoiceListRequestModel opportunityAllListRequest = new PerformaInvoiceListRequestModel();
        opportunityAllListRequest.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        opportunityAllListRequest.setMaxItem(maxItem);
        opportunityAllListRequest.setPageNo(page);
        opportunityAllListRequest.setSearchText(searchTextValue);
        PerformaInvoiceListRequestModel.Field field = new PerformaInvoiceListRequestModel.Field();
        field.setCardCode(cardCode);
        field.setCardName("");
        field.setStatus("");
        field.setToDate(toDate);
        field.setFromDate(fromDate);
        field.setOppotype("");
        field.setSource("");

        opportunityAllListRequest.setField(field);

        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getQuotationList(opportunityAllListRequest);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body().getStatus() == 200) {
                                loader.setVisibility(View.GONE);
                                if (response.body().getValue().isEmpty()) {
                                    setAdapter();
                                    loader.setVisibility(View.GONE);
                                    binding.recyclerview.setVisibility(View.GONE);
                                    binding.noDatafound.setVisibility(View.VISIBLE);
                                } else {
                                    if (response.body().getValue().size() > 0 && response.body().getValue() != null) {
                                        List<QuotationItem> quotationItems = response.body().getValue();
                                        binding.recyclerview.setVisibility(View.VISIBLE);
                                        if (page == 1) {
                                            quotationItemArrayList_gl.clear();
                                            quotationItemArrayList_gl.addAll(quotationItems);
                                            adapter.AllData(quotationItems);
                                        } else {
                                            quotationItemArrayList_gl.addAll(quotationItems);
                                            adapter.AllData(quotationItems);
                                        }
                                        binding.swipeRefreshLayout.setRefreshing(false);

                                        adapter.notifyDataSetChanged();

                                        binding.noDatafound.setVisibility(View.GONE);

                                        if (quotationItems.size() < 10)
                                            apicall = false;

                                    } else {
                                        binding.recyclerview.setAdapter(null);
                                        binding.noDatafound.setVisibility(View.VISIBLE);
                                    }
                                }

                            }

                        }
                        else if (response.code() ==404){
                            Toast.makeText(getContext(), "No Quotation Found From Server ! ", Toast.LENGTH_LONG).show();
                        }
                        else if (response.code() ==500){
                            Toast.makeText(getContext(), "Internal Server error ! ", Toast.LENGTH_LONG).show();
                        }
                        else if (response.code() == 401) {
//                    if (response.body().getStatus() == 301){
                            Gson gson = new GsonBuilder().create();
                            QuotationResponse mError = new QuotationResponse();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, QuotationResponse.class);
                                Toast.makeText(getContext(), mError.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                            }
//                    }
                        } else {
                            loader.setVisibility(View.GONE);
                            binding.noDatafound.setVisibility(View.VISIBLE);
                            Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Gson gson = new GsonBuilder().create();
                            QuotationResponse mError = new QuotationResponse();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, QuotationResponse.class);
                                Toast.makeText(requireContext(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    //todo quotaion all list api with pagination..
    private void getQuotationListApi(SpinKitView loader, int maxItem, int page, String cardCode, String fromDate, String toDate) {
        binding.noDatafound.setVisibility(View.GONE);

        loader.setVisibility(View.VISIBLE);

        PerformaInvoiceListRequestModel opportunityAllListRequest = new PerformaInvoiceListRequestModel();
        opportunityAllListRequest.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        opportunityAllListRequest.setMaxItem(maxItem);
        opportunityAllListRequest.setPageNo(page);
        opportunityAllListRequest.setSearchText(searchTextValue);
        opportunityAllListRequest.setOrder_by_field(Globals.orderbyField_id);
        opportunityAllListRequest.setOrder_by_value(Globals.orderbyvalueDesc);
        PerformaInvoiceListRequestModel.Field field = new PerformaInvoiceListRequestModel.Field();
        if (isCardCodePresent){
            field.setCardCode(BPCardCode);
        }

        if (isCreateDatePresent) {
            if (!fromDate.isEmpty()) {
                field.setFromDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(fromDate));
            } else {
                field.setFromDate("");
            }
        }

        field.setDepartement("2");

  /*      field.setCardCode(cardCode);
        field.setCardName("");
        field.setStatus("");

        if (!fromDate.isEmpty()) {
            field.setFromDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(fromDate));
        }else{
            field.setFromDate("");
        }
        if (!toDate.isEmpty()) {
            field.setToDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(toDate));
        }else{
            field.setToDate("");
        }
        field.setOppotype("");
        field.setSource("");*/

        opportunityAllListRequest.setField(field);

        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getQuotationList(opportunityAllListRequest);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus() == 200) {
                            binding.swipeRefreshLayout.setRefreshing(false);
                            loader.setVisibility(View.GONE);

                            if (response.body().getValue().size()>0){
                                quotationItemArrayList_gl.clear();
                                quotationItemArrayList_gl.addAll(response.body().getValue());
                                adapter.AllData(response.body().getValue());
                                binding.noDatafound.setVisibility(View.GONE);
                            }else {
                                quotationItemArrayList_gl.clear();
                                quotationItemArrayList_gl.addAll(response.body().getValue());

                                binding.noDatafound.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();


                       /*
                            if (response.body().getValue().size() == 0) {
                                quotationItemArrayList_gl.clear();
                                quotationItemArrayList_gl.addAll(response.body().getValue());
                                apicall = false;
                                adapter.notifyDataSetChanged();
                                loader.setVisibility(View.GONE);
                                binding.noDatafound.setVisibility(View.VISIBLE);

                            } else if (response.body().getValue().size() > 0 && response.body().getValue() != null) {
                                List<QuotationItem> quotationItemList = response.body().getValue();
                                if (page == 1) {
                                    quotationItemArrayList_gl.clear();
                                    quotationItemArrayList_gl.addAll(quotationItemList);
                                    adapter.AllData(quotationItemList);
                                } else {
                                    quotationItemArrayList_gl.addAll(quotationItemList);
                                    adapter.AllData(quotationItemList);
                                }
                                binding.swipeRefreshLayout.setRefreshing(false);

                                adapter.notifyDataSetChanged();

                                binding.noDatafound.setVisibility(View.GONE);


                            }*/
                        } else if (response.body().getStatus() == 401) {
                            Gson gson = new GsonBuilder().create();
                            QuotationResponse mError = new QuotationResponse();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, QuotationResponse.class);
                                Toast.makeText(getActivity(), mError.getMessage(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen(getActivity());
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                        } else {
                            binding.noDatafound.setVisibility(View.VISIBLE);
                            loader.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        loader.setVisibility(View.GONE);
                        Gson gson = new GsonBuilder().create();
                        QuotationResponse mError = new QuotationResponse();
                        try {
                            String s = response.errorBody().string();
                            mError = gson.fromJson(s, QuotationResponse.class);
                            Toast.makeText(requireContext(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getQuotationListOtherPageApi(SpinKitView loader, int maxItem, int page, String cardCode, String fromDate, String toDate) {
        binding.noDatafound.setVisibility(View.GONE);

        loader.setVisibility(View.VISIBLE);

        PerformaInvoiceListRequestModel opportunityAllListRequest = new PerformaInvoiceListRequestModel();
        opportunityAllListRequest.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        opportunityAllListRequest.setMaxItem(maxItem);
        opportunityAllListRequest.setPageNo(page);
        opportunityAllListRequest.setSearchText(searchTextValue);
        opportunityAllListRequest.setOrder_by_field(Globals.orderbyField_id);
        opportunityAllListRequest.setOrder_by_value(Globals.orderbyvalueDesc);
        PerformaInvoiceListRequestModel.Field field = new PerformaInvoiceListRequestModel.Field();
        if (isCardCodePresent){
            field.setCardCode(BPCardCode);
        }

        if (isCreateDatePresent) {
            if (!fromDate.isEmpty()) {
                field.setFromDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(fromDate));
            } else {
                field.setFromDate("");
            }
        }

        /*       field.setCardCode(cardCode);
        field.setCardName("");
        field.setStatus("");
        if (!fromDate.isEmpty()) {
            field.setFromDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(fromDate));
        }else{
            field.setFromDate("");
        }
        if (!toDate.isEmpty()) {
            field.setToDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(toDate));
        }else{
            field.setToDate("");
        }

        field.setOppotype("");
        field.setSource("");*/

        opportunityAllListRequest.setField(field);

        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getQuotationList(opportunityAllListRequest);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus() == 200) {
                            loader.setVisibility(View.GONE);
                            if (response.body().getValue().size() == 0) {
                                //   opportunityResposeArrayList_gl.clear();
                                quotationItemArrayList_gl.addAll(response.body().getValue());
                                apicall = false;
                                adapter.notifyDataSetChanged();
                                loader.setVisibility(View.GONE);
                                // binding.noDatafound.setVisibility(View.VISIBLE);

                            } else if (response.body().getValue().size() > 0 && response.body().getValue() != null) {
                                List<QuotationItem> quotationItemList = response.body().getValue();
                                if (page == 1) {
                                    //  opportunityResposeArrayList_gl.clear();
                                    quotationItemArrayList_gl.addAll(quotationItemList);
                                    adapter.AllData(quotationItemList);
                                } else {
                                    quotationItemArrayList_gl.addAll(quotationItemList);
                                    adapter.AllData(quotationItemList);
                                }
                                binding.swipeRefreshLayout.setRefreshing(false);

                                adapter.notifyDataSetChanged();

                                binding.noDatafound.setVisibility(View.GONE);

                                if (quotationItemList.size() < 10)
                                    apicall = false;
                            }
                        } else if (response.body().getStatus() == 401) {
                            Gson gson = new GsonBuilder().create();
                            QuotationResponse mError = new QuotationResponse();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, QuotationResponse.class);
                                Toast.makeText(getActivity(), mError.getMessage(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen(getActivity());
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                        } else {
                            binding.noDatafound.setVisibility(View.VISIBLE);
                            loader.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        loader.setVisibility(View.GONE);
                        Gson gson = new GsonBuilder().create();
                        QuotationResponse mError = new QuotationResponse();
                        try {
                            String s = response.errorBody().string();
                            mError = gson.fromJson(s, QuotationResponse.class);
                            Toast.makeText(requireContext(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //menu.clear();
        inflater.inflate(R.menu.quotation_filter, menu);

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(((QuotationActivity) mContext).getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search Quotation");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                page = 1;
                searchTextValue = query;
                if (!searchTextValue.isEmpty()){
                    getQuotationListApi(binding.loaderLayout.loader, maxItem, page, "", "", "");
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                page = 1;
                searchTextValue = newText;
                if (!searchTextValue.isEmpty()){
                    getQuotationListApi(binding.loaderLayout.loader, maxItem, page, "", "", "");
                }
                return false;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plus:
                Intent i = new Intent(getContext(), AddQuotationAct.class);
                startActivity(i);
                break;

            case R.id.filter:
                adapter.notifyDataSetChanged();
                page = 1;
                apicall = true;
                showAllFilterDialog();
                break;

            case R.id.newest:
                LocalDate date = LocalDate.parse(Globals.curntDate);
                LocalDate dateafter = date.minusDays(8);
                adapter.PostingDate(dateafter, date);
                break;
            case R.id.my:
                if (adapter != null)
                    adapter.Favfilter("Y");
                break;
        }
        return true;
    }

    String solIdName = "";
    String cardCode = "";
    String CardName = "";
    String fromDate = "";
    String toDate = "";
    String fromAmount = "";
    String toAmount = "";


    List<String> status_list_gl = Arrays.asList(Globals.status_list);

    private void showAllFilterDialog() {
        Dialog dialog = new Dialog(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View custom_dialog = layoutInflater.inflate(R.layout.quotation_filter_layout, null);
        dialog.setContentView(custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        AutoCompleteTextView acCustomer = dialog.findViewById(R.id.acCustomer);
        TextInputEditText edtFromDate = dialog.findViewById(R.id.edtFromDate);
        TextInputEditText edtToDate = dialog.findViewById(R.id.edtToDate);
        MaterialButton resetBtn = dialog.findViewById(R.id.resetBtn);
        MaterialButton applyBtn = dialog.findViewById(R.id.applyBtn);
        ImageView ivCrossIcon = dialog.findViewById(R.id.ivCrossIcon);
        LinearLayout rlRecyclerViewLayout = dialog.findViewById(R.id.rlRecyclerViewLayout);
        RecyclerView rvCustomerSearchList = dialog.findViewById(R.id.rvCustomerSearchList);


        ivCrossIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        acCustomer.setText(CardName);
        edtFromDate.setText(fromDate);
        edtToDate.setText(toDate);


        callCustomerApi(acCustomer, rlRecyclerViewLayout, rvCustomerSearchList);


        //todo bill to and ship to address drop down item select..
        acCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (customerList_gl.size() > 0) {
                    cardCode = customerList_gl.get(position).getCardCode();
                    CardName = customerList_gl.get(position).getCardName();
                    acCustomer.setText(customerList_gl.get(position).getCardName());


                }
            }
        });



        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.disableFutureDates(getContext(), edtFromDate);

            }
        });


        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.enableAllCalenderDateSelect(getContext(), edtToDate);

            }
        });


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCardCodePresent=false;
                isCreateDatePresent=false;

                acCustomer.setText("");
                edtFromDate.setText("");
                edtToDate.setText("");

                CardName = "";
                fromDate = "";
                toDate = "";

            }
        });


        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDate = edtFromDate.getText().toString();
                toDate = edtToDate.getText().toString();
                if (fromDate.isEmpty()) {
                    isCreateDatePresent = false;
                } else {
                    isCreateDatePresent = true;
                }

                if (cardCode.isEmpty()) {
                    isCardCodePresent = false;
                } else {
                    isCardCodePresent = true;
                }
                BPCardCode=cardCode;


                getQuotationListApi(binding.loaderLayout.loader, maxItem, page, cardCode, edtFromDate.getText().toString(), edtToDate.getText().toString());

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    //todo call customer api here...
    ArrayList<DataBusinessPartnerDropDown> customerList_gl = new ArrayList<>();

    private void callCustomerApi(AutoCompleteTextView acCustomer, LinearLayout rlRecyclerViewLayout, RecyclerView rvCustomerSearchList){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));


        Call<ResponseBusinessPartnerDropDown> call = NewApiClient.getInstance().getApiService(requireContext()).getBusinessPartnerListInDropDown(jsonObject);
        call.enqueue(new Callback<ResponseBusinessPartnerDropDown>() {
            @Override
            public void onResponse(Call<ResponseBusinessPartnerDropDown> call, Response<ResponseBusinessPartnerDropDown> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals("200")) {
                            if (response.body().getData() != null && response.body().getData().size() > 0) {

                                customerList_gl.clear();
                                List<DataBusinessPartnerDropDown> itemsList = response.body().getData();
                               // itemsList = filterList(response.body().getData());
                                customerList_gl.addAll(itemsList);

                                List<String> itemNames = new ArrayList<>();
                                List<String> cardCodeName = new ArrayList<>();
                                for (DataBusinessPartnerDropDown item : customerList_gl) {
                                    itemNames.add(item.getCardName());
                                    cardCodeName.add(item.getCardCode());
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_textview, itemNames);
                                acCustomer.setAdapter(adapter);

                                //todo bill to and ship to address drop down item select..
                                acCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        cardCode = cardCodeName.get(position);
                                        CardName = (String) parent.getItemAtPosition(position);

                                        int pos = Globals.getCustomerDropDownPos(customerList_gl, CardName);
                                        cardCode = customerList_gl.get(pos).getCardCode();

                                        if (CardName.isEmpty()) {
                                            rlRecyclerViewLayout.setVisibility(View.GONE);
                                            rvCustomerSearchList.setVisibility(View.GONE);
                                        } else {
                                            rlRecyclerViewLayout.setVisibility(View.VISIBLE);
                                            rvCustomerSearchList.setVisibility(View.VISIBLE);
                                        }

                                        if (!CardName.isEmpty()) {
                                            adapter.notifyDataSetChanged();
                                            acCustomer.setText(CardName);
                                            acCustomer.setSelection(acCustomer.length());


                                        } else {
                                            acCustomer.setText("");
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Gson gson = new GsonBuilder().create();
                        QuotationResponse mError = new QuotationResponse();
                        try {
                            String s = response.errorBody().string();
                            mError = gson.fromJson(s, QuotationResponse.class);
                            Toast.makeText(requireContext(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBusinessPartnerDropDown> call, Throwable t) {
                Toast.makeText(binding.loaderLayout.loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //todo filter for customer search ..
    private List<BusinessPartnerAllResponse.Datum> filterList(ArrayList<BusinessPartnerAllResponse.Datum> value) {
        List<BusinessPartnerAllResponse.Datum> tempList = new ArrayList<>();
        for (BusinessPartnerAllResponse.Datum customer : value) {
            if (!customer.getCardName().equals("admin")) {
                tempList.add(customer);
            }
        }
        return tempList;
    }

    ArrayList<OppAddressResponseModel.Data> solIdList_gl = new ArrayList<>();

    //todo ship to address.
    private void callBPShipToAddressApi(String cardCode, AutoCompleteTextView acSolID) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("BPCode", cardCode);
        jsonObject.addProperty("SearchText","");
        jsonObject.addProperty("order_by_field",Globals.orderbyField_id);
        jsonObject.addProperty("order_by_value",Globals.orderbyvalueDesc);
        JsonObject json=new JsonObject();
        jsonObject.add("field",json);
        Call<OppAddressResponseModel> call = NewApiClient.getInstance().getApiService(requireContext()).getShipToAddress(jsonObject);
        call.enqueue(new Callback<OppAddressResponseModel>() {
            @Override
            public void onResponse(Call<OppAddressResponseModel> call, Response<OppAddressResponseModel> response) {

                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {
                        solIdList_gl.clear();
                        solIdList_gl.addAll(response.body().getData());
                        BPAddressAdapter leadTypeAdapter = new BPAddressAdapter(getContext(), R.layout.drop_down_textview, solIdList_gl);
                        acSolID.setAdapter(leadTypeAdapter);
                    }

                } else {
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                        Toast.makeText(getContext(), mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                }
                binding.loaderLayout.loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<OppAddressResponseModel> call, Throwable t) {
                binding.loaderLayout.loader.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    Dialog dialog;


    @Override
    public void onRefresh() {
        if (Globals.checkInternet(getActivity())) {
            binding.loaderLayout.loader.setVisibility(View.VISIBLE);
            getQuotationListApi(binding.loaderLayout.loader, maxItem, page, "", "", "");
        }
    }

    @Override
    public void stagecomment(String id, String name) {
        dialog.cancel();
        if (adapter != null) {
            adapter.Customernamefilter(id, name);
            if (adapter.getItemCount() == 0)
                binding.noDatafound.setVisibility(View.VISIBLE);
            else
                binding.noDatafound.setVisibility(View.GONE);
        }
    }


    //todo calling quotation approval api here..

    private void callQuotationApproveApi(String id, String remark, String status, Dialog dialog) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("FinalStatus", status);
        jsonObject.addProperty("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode, ""));
        jsonObject.addProperty("Remarks", remark);

        Call<ResponseModel> call = NewApiClient.getInstance().getApiService(requireContext()).quotationApprove(jsonObject);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body().getStatus() == 200) {
                                onRefresh();
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.code() == 401) {
                        if (response.body().getStatus() == 301) {
                            Gson gson = new GsonBuilder().create();
                            TokenExpireModel mError = new TokenExpireModel();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, TokenExpireModel.class);
                                Toast.makeText(getActivity(), mError.getDetail(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen((Activity) getActivity());
                            } catch (IOException e) {
                                // handle failure to read error
                            }

                        }
                    } else {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Gson gson = new GsonBuilder().create();
                        QuotationResponse mError = new QuotationResponse();
                        try {
                            String s = response.errorBody().string();
                            mError = gson.fromJson(s, QuotationResponse.class);
                            Toast.makeText(getActivity(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("TAG_APi_failure", "onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //todo calling adapter override function..
    @Override
    public void onItemClicked(String id, String remark, String status, Dialog dialog) {
        callQuotationApproveApi(id, remark, status, dialog);

    }
}