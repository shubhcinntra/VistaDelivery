package com.cinntra.vistadelivery.fragments;

import android.app.Dialog;
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
import com.cinntra.vistadelivery.activities.AddOrderAct;
import com.cinntra.vistadelivery.activities.OrderActivity;
import com.cinntra.vistadelivery.adapters.Open_Order_Adapter;
import com.cinntra.vistadelivery.databinding.FragmentOpenOrderBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.interfaces.CommentStage;
import com.cinntra.vistadelivery.interfaces.FragmentRefresher;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.cinntra.vistadelivery.model.DataBusinessPartnerDropDown;
import com.cinntra.vistadelivery.model.OpportunityModels.DepartmentResponseModel;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.PerformaInvoiceListRequestModel;
import com.cinntra.vistadelivery.model.QuotationItem;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.ResponseBusinessPartnerDropDown;
import com.cinntra.vistadelivery.model.TokenExpireModel;
import com.cinntra.vistadelivery.model.orderModels.OrderListModel;
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


public class Open_Order extends Fragment implements CommentStage, FragmentRefresher {

    private SearchView searchView;
    int currentpage = 0;
    boolean recallApi = true;

    int page = 1;
    Boolean apicall = true;
    Boolean isScrollingpage = false;
    int maxItem = 10;
    ArrayList<OrderListModel.Data> orderList_gl = new ArrayList<>();
    String DEP_NAME = "";
    ArrayList<DepartmentResponseModel.Data> departmentList_gl = new ArrayList<>();
    private String searchTextValue = "";
    private String filterTextValue = "";
    ArrayList<QuotationItem> AllItemList = new ArrayList<>();

    ArrayList<QuotationItem> AllItemNewList = new ArrayList<>();
    LinearLayoutManager layoutManager;
    FragmentOpenOrderBinding binding;

    String BPCardCode = "";
    private boolean isCardCodePresent = false;
    private boolean isCreateDatePresent = false;

    public Open_Order() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Open_Order newInstance(String param1, String param2) {
        Open_Order fragment = new Open_Order();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    Open_Order_Adapter adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOpenOrderBinding.inflate(getLayoutInflater());
//        View v = inflater.inflate(R.layout.fragment_open_order, container, false);
        return binding.getRoot();

    }

    String cardcode = "";
    String createDate = "";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.loader.loader.setVisibility(View.GONE);
        AllItemList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);


        if (getActivity().getIntent() != null) {
            BPCardCode = getActivity().getIntent().getStringExtra("BPCardCodeShortCut");
            // Use the passed data as needed
        }

        //todo pagination for list..
        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                Log.e("current1--->", String.valueOf(totalskipCount(lastCompletelyVisibleItemPosition)));
                if (isScrollingpage && lastCompletelyVisibleItemPosition == orderList_gl.size() - 2 && apicall) {
                    page++;
                    Log.e("page--->", String.valueOf(page));
                    getOrderListOtherPageApi(binding.loader.loader, maxItem, page, "", "", "");
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

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isCreateDatePresent = false;
                isCardCodePresent = false;

                searchTextValue = "";
                filterTextValue = "";
                if (Globals.checkInternet(getActivity())) {
                    page = 1;
                    apicall = true;
                    callApi(binding.loader.loader, maxItem, page, "", "", "");
                } else
                    binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (Globals.checkInternet(getActivity())) {
            binding.loader.loader.setVisibility(View.VISIBLE);

            if (BPCardCode == null) {
                isCardCodePresent = false;
                callApi(binding.loader.loader, maxItem, page, "", "", "");
            } else {
                isCardCodePresent = true;
                callApi(binding.loader.loader, maxItem, page, "", "", "");
            }


        }

        setAdapter();
    }

    //todo count items..
    int totalskipCount(int current) {
        int total = maxItem * page;
        return total;
    }


    //todo set adapter...
    private void setAdapter() {
        adapter = new Open_Order_Adapter(getContext(), orderList_gl);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        if (Globals.checkInternet(getActivity())) {
            binding.loader.loader.setVisibility(View.VISIBLE);
            callApi(binding.loader.loader, maxItem, page, "", "", "");
        }
    }

    //todo call order ist api...
    private void callApi(SpinKitView loader, int maxItem, int page, String cardCode, String fromDate, String toDate) {
        binding.noDatafound.setVisibility(View.GONE);

        PerformaInvoiceListRequestModel opportunityAllListRequest = new PerformaInvoiceListRequestModel();
        opportunityAllListRequest.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        opportunityAllListRequest.setMaxItem(maxItem);
        opportunityAllListRequest.setPageNo(page);
        opportunityAllListRequest.setSearchText(searchTextValue);
        opportunityAllListRequest.setOrder_by_field(Globals.orderbyField_id);
        opportunityAllListRequest.setOrder_by_value(Globals.orderbyvalueDesc);
        PerformaInvoiceListRequestModel.Field field = new PerformaInvoiceListRequestModel.Field();
        field.setDepartement("2");
        if (isCardCodePresent) {
            field.setCardCode(BPCardCode);
        }


        if (isCreateDatePresent) {
            if (!fromDate.isEmpty()) {
                field.setFromDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(fromDate));
            } else {
                field.setFromDate("");
            }
        }



       /* field.setCardCode(cardCode);
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
        loader.setVisibility(View.VISIBLE);

        Call<OrderListModel> call = NewApiClient.getInstance().getApiService(requireContext()).OrdersList(opportunityAllListRequest);
        call.enqueue(new Callback<OrderListModel>() {
            @Override
            public void onResponse(Call<OrderListModel> call, Response<OrderListModel> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body().getStatus() == 200) {
                                loader.setVisibility(View.GONE);
                                if (response.body().getValue().size() == 0) {

                                    orderList_gl.clear();
                                    orderList_gl.addAll(response.body().getValue());
                                    apicall = false;
                                    adapter.notifyDataSetChanged();
                                    loader.setVisibility(View.GONE);
                                    binding.noDatafound.setVisibility(View.VISIBLE);

                                } else {
                                    if (response.body().getValue().size() > 0 && response.body().getValue() != null) {
                                        List<OrderListModel.Data> quotationItems = response.body().getValue();
                                        binding.recyclerview.setVisibility(View.VISIBLE);
                                        if (page == 1) {
//                                            setAdapter();
                                            orderList_gl.clear();
                                            orderList_gl.addAll(quotationItems);
                                            adapter.AllData(quotationItems);
                                        } else {
//                                            setAdapter();
                                            orderList_gl.addAll(quotationItems);
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

                            } else {
                                binding.noDatafound.setVisibility(View.VISIBLE);
                                loader.setVisibility(View.GONE);
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
                        } else if (response.code() == 401) {
                            Gson gson = new GsonBuilder().create();
                            TokenExpireModel mError = new TokenExpireModel();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, TokenExpireModel.class);
                                Toast.makeText(getContext(), mError.getDetail(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen(getActivity());
                            } catch (IOException e) {
                                // handle failure to read error
                            }
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
            public void onFailure(Call<OrderListModel> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getOrderListOtherPageApi(SpinKitView loader, int maxItem, int page, String cardCode, String fromDate, String toDate) {
        binding.noDatafound.setVisibility(View.GONE);

        PerformaInvoiceListRequestModel opportunityAllListRequest = new PerformaInvoiceListRequestModel();
        opportunityAllListRequest.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        opportunityAllListRequest.setMaxItem(maxItem);
        opportunityAllListRequest.setPageNo(page);
        opportunityAllListRequest.setSearchText(searchTextValue);
        opportunityAllListRequest.setOrder_by_field(Globals.orderbyField_id);
        opportunityAllListRequest.setOrder_by_value(Globals.orderbyvalueDesc);
        PerformaInvoiceListRequestModel.Field field = new PerformaInvoiceListRequestModel.Field();
        field.setDepartement("2");
        if (isCardCodePresent) {
            field.setCardCode(BPCardCode);
        }

        if (isCreateDatePresent) {
            if (!fromDate.isEmpty()) {
                field.setFromDate(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(fromDate));
            } else {
                field.setFromDate("");
            }
        }

      /*  field.setCardCode(cardCode);
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
        loader.setVisibility(View.VISIBLE);

        Call<OrderListModel> call = NewApiClient.getInstance().getApiService(requireContext()).OrdersList(opportunityAllListRequest);
        call.enqueue(new Callback<OrderListModel>() {
            @Override
            public void onResponse(Call<OrderListModel> call, Response<OrderListModel> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body().getStatus() == 200) {
                                loader.setVisibility(View.GONE);
                                if (response.body().getValue().size() == 0) {
                                    //   opportunityResposeArrayList_gl.clear();
                                    orderList_gl.addAll(response.body().getValue());
                                    apicall = false;
                                    adapter.notifyDataSetChanged();
                                    loader.setVisibility(View.GONE);
                                    // binding.noDatafound.setVisibility(View.VISIBLE);

                                } else if (response.body().getValue().size() > 0 && response.body().getValue() != null) {
                                    List<OrderListModel.Data> opportunityResposeList = response.body().getValue();
                                    if (page == 1) {
                                        //  opportunityResposeArrayList_gl.clear();
                                        orderList_gl.addAll(opportunityResposeList);
                                        adapter.AllData(opportunityResposeList);
                                    } else {
                                        orderList_gl.addAll(opportunityResposeList);
                                        adapter.AllData(opportunityResposeList);
                                    }
                                    binding.swipeRefreshLayout.setRefreshing(false);

                                    adapter.notifyDataSetChanged();

                                    binding.noDatafound.setVisibility(View.GONE);

                                    if (opportunityResposeList.size() < 10)
                                        apicall = false;
                                }
                            } else {
                                binding.noDatafound.setVisibility(View.VISIBLE);
                                loader.setVisibility(View.GONE);
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
                        } else if (response.code() == 401) {
                            Gson gson = new GsonBuilder().create();
                            TokenExpireModel mError = new TokenExpireModel();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, TokenExpireModel.class);
                                Toast.makeText(getContext(), mError.getDetail(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen(getActivity());
                            } catch (IOException e) {
                                // handle failure to read error
                            }
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
            public void onFailure(Call<OrderListModel> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        inflater.inflate(R.menu.order_filter, menu);

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.search);
        MenuItem itemPlus = menu.findItem(R.id.plus);

//        itemPlus.setVisible(false);
        SearchView searchView = new SearchView(((OrderActivity) getContext()).getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search Orders");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                page = 1;
                searchTextValue = query;
                if (!searchTextValue.isEmpty())
                    callApi(binding.loader.loader, maxItem, page, "", "", "");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                page = 1;
                searchTextValue = newText;
                if (!searchTextValue.isEmpty())
                    callApi(binding.loader.loader, maxItem, page, "", "", "");
                return false;
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plus:
                Prefs.putString(Globals.FromQuotation, "Order");
                startActivity(new Intent(getContext(), AddOrderAct.class));

                break;
            case R.id.filter:
                adapter.notifyDataSetChanged();
                page = 1;
                apicall = true;
                showAllFilterDialog();
                break;
         /*   case R.id.all:
                    page = 1;
                    apicall = true;
                    filterTextValue = "";
                    callApi(binding.loaderLayout.loader, maxItem, page, "", "", "", "", "");
                adapter.notifyDataSetChanged();


                break;
            case R.id.pending:


                    page = 1;
                    apicall = true;
                    filterTextValue = "Pending";
                    callApi(binding.loaderLayout.loader, maxItem, page, "", "", "", "", "");
                    adapter.notifyDataSetChanged();

                break;

            case R.id.approval:

                    adapter.notifyDataSetChanged();
                    page = 1;
                    apicall = true;
                    filterTextValue = "Approved";
                    callApi(binding.loaderLayout.loader, maxItem, page, cardCode, solIdName, edtFromDate.getText().toString(), edtToDate.getText().toString(), edtMrNo.getText().toString());
                    adapter.notifyDataSetChanged();

                break;
            case R.id.reject:

                    adapter.notifyDataSetChanged();
                    page = 1;
                    apicall = true;
                    filterTextValue = "Rejected";
                    callApi(binding.loaderLayout.loader, maxItem, page, cardCode, solIdName, edtFromDate.getText().toString(), edtToDate.getText().toString(), edtMrNo.getText().toString());
                    adapter.notifyDataSetChanged();

                break;*/

            case R.id.customer:
//                showCustomerListDialog();
                break;
            case R.id.posting:

                break;
            case R.id.valid:
                if (adapter != null)
                    adapter.ValidDate();
                break;
            case R.id.order:
                LocalDate dateObj2 = LocalDate.parse(Globals.curntDate);
                LocalDate afterdate2 = dateObj2.minusDays(8);
                adapter.PostingDate(afterdate2, dateObj2);
                break;
        }
        return true;
    }


    String solIdName="";
           String cardCode = "";

    String CardName = "";
    String fromDate = "";
    String toDate = "";
    String mrNo = "";

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
                Globals.enableAllCalenderDateSelect(getContext(), edtFromDate);

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
                callApi(binding.loader.loader, maxItem, page, cardCode, edtFromDate.getText().toString(), edtToDate.getText().toString());

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    //todo call customer api here...
    ArrayList<DataBusinessPartnerDropDown> customerList_gl = new ArrayList<>();

    private void callCustomerApi(AutoCompleteTextView acCustomer, LinearLayout rlRecyclerViewLayout, RecyclerView rvCustomerSearchList) {
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
                                List<DataBusinessPartnerDropDown> itemsList = response.body().getData();
                                // itemsList = filterList(response.body().getData());
                                customerList_gl.clear();
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
                Toast.makeText(binding.loader.loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<BusinessPartnerAllResponse.Datum> filterList(ArrayList<BusinessPartnerAllResponse.Datum> value) {
        List<BusinessPartnerAllResponse.Datum> tempList = new ArrayList<>();
        for (BusinessPartnerAllResponse.Datum customer : value) {
            if (!customer.getCardName().equals("admin")) {
                tempList.add(customer);
            }
        }
        return tempList;
    }


    private void nodatafound() {
        if (adapter.getItemCount() == 0)
            binding.noDatafound.setVisibility(View.VISIBLE);
        else
            binding.noDatafound.setVisibility(View.GONE);
    }


    @Override
    public void stagecomment(String id, String name) {
        if (adapter != null) {
            adapter.Customernamefilter(name);
            if (adapter.getItemCount() == 0)
                binding.noDatafound.setVisibility(View.VISIBLE);
            else
                binding.noDatafound.setVisibility(View.GONE);
        }
    }
}