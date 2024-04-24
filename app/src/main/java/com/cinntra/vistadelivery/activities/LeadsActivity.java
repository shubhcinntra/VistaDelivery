package com.cinntra.vistadelivery.activities;

import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.adapters.LeadsAdapter;
import com.cinntra.vistadelivery.adapters.SourceAdpater;
import com.cinntra.vistadelivery.adapters.leadAdapter.FilterSourceAdapter;
import com.cinntra.vistadelivery.adapters.leadAdapter.LeadPriorityAdapter;
import com.cinntra.vistadelivery.databinding.FragmentLeadBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.globals.MainBaseActivity;
import com.cinntra.vistadelivery.model.EmployeeValue;
import com.cinntra.vistadelivery.model.LeadTypeData;
import com.cinntra.vistadelivery.model.LeadTypeResponse;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.SaleEmployeeResponse;
import com.cinntra.vistadelivery.model.SalesEmployeeItem;
import com.cinntra.vistadelivery.modelfilter.FieldFilter;
import com.cinntra.vistadelivery.modelfilter.FilterOverAll;
import com.cinntra.vistadelivery.newapimodel.LeadResponse;
import com.cinntra.vistadelivery.newapimodel.LeadValue;
import com.cinntra.vistadelivery.spinneradapter.SourceSearchableSpinnerAdapter;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LeadsActivity extends MainBaseActivity implements View.OnClickListener, com.borax12.materialdaterangepicker.date.DatePickerDialog.OnDateSetListener {

    public Context mContext;
    List<LeadValue> leadValueList = new ArrayList<>();
    private String searchTextValue = "";
    int pageNo = 1;
    LeadsAdapter adapter;
    FilterOverAll value = new FilterOverAll();
    boolean apicall = true;
    int pageno = 1;
    String maxItem = "10";
    boolean dropdownApi = false;
    Boolean isScrollingpage = false;
    LinearLayoutManager layoutManager;
    String filterType = "";
    ArrayList<LeadTypeData> sourceData = new ArrayList<>();


    public LeadsActivity() {
        // Required empty public constructor
    }

    FragmentLeadBinding binding;

    // TODO: Rename and change types and number of parameters

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentLeadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24);

        getSupportActionBar().show();

        Globals.sourcechecklist.clear();
        mContext = LeadsActivity.this;
        value.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        binding.calendar.setOnClickListener(this);
        binding.allList.setOnClickListener(this);

//        setAdapter();

        binding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isAssignedToPresent=false;
                isSourcePresent=false;

                searchTextValue = "";
                SourceFilter = "";
                StatusFilter = "";
                assignToNameValue = "";
                assignToCode = "";
                priorityType = "";
                fromDate = "";
                toDate = "";
                Globals.sourcechecklist.clear();
                if (Globals.checkInternet(LeadsActivity.this)) {
                    pageno = 1;
                    apicall = true;
                    isAssignedTo=false;
                    isSourceTo=false;
                    if (Prefs.getString(Globals.BussinessPageType, "AddBPLead").equalsIgnoreCase("AddBPLead") || Prefs.getString(Globals.BussinessPageType, "AddProposal").equalsIgnoreCase("AddProposal")) {
                        StatusFilter = "Qualified";

                        callApi(value, binding.loaderLayout.loader, maxItem, pageno);
                    } else {
                        callApi(value, binding.loaderLayout.loader, maxItem, pageno);
                    }
                    binding.dateText.setVisibility(View.GONE);
                    binding.leadTypeSpinner.setSelection(0);
                } else {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }

            }
        });

        //todo pagination for list..
        binding.leadRecyclerViewList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (Globals.checkInternet(LeadsActivity.this) && apicall) {
                    if (isScrollingpage && lastCompletelyVisibleItemPosition == leadValueList.size() - 2 && apicall) {
                        pageno++;
                        Log.e("page--->", String.valueOf(pageno));
                        callApiForPage(value, binding.loaderLayout.loader, maxItem, pageno);
                        binding.leadTypeSpinner.setSelection(0);
                        isScrollingpage = false;
                    }
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


        binding.leadTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null) {
                    binding.dateText.setVisibility(View.GONE);
                    pageno = 1;
                    apicall = true;

                    if (position == 0 && dropdownApi) {
                        filterType = "";
                        binding.dotcolor.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        value.setSearchText("");
                        leadValueList.clear();
                        binding.dotcolor.setBackground(getDrawable(R.drawable.white_dot));
                        callApi(value, binding.loaderLayout.loader, maxItem, pageno);
                    } else if (position == 0) {
                        dropdownApi = true;
                        binding.dotcolor.setBackground(getDrawable(R.drawable.white_dot));
                        leadValueList.clear();
                        callApi(value, binding.loaderLayout.loader, maxItem, pageno);

//                        adapter.priorityfilter("All");
                        adapter.notifyDataSetChanged();
                    } else if (position == 1) {
                        binding.dotcolor.setBackground(getDrawable(R.drawable.ic_blue_dot));
                        dropdownApi = true;
                        leadValueList.clear();
//                        adapter.priorityfilter("High");
                        callApi(value, binding.loaderLayout.loader, maxItem, pageno);

                        adapter.notifyDataSetChanged();
                    } else if (position == 2) {
                        dropdownApi = true;
                        binding.dotcolor.setBackground(getDrawable(R.drawable.orange_dot));
                        leadValueList.clear();
//                        adapter.priorityfilter("Medium");
                        callApi(value, binding.loaderLayout.loader, maxItem, pageno);

                        adapter.notifyDataSetChanged();
                    } else if (position == 3) {
                        dropdownApi = true;
                        binding.dotcolor.setBackground(getDrawable(R.drawable.project_title_));
                        leadValueList.clear();
//                        adapter.priorityfilter("Low");
                        callApi(value, binding.loaderLayout.loader, maxItem, pageno);

                        adapter.notifyDataSetChanged();
                    }
                    nodatafoundimage();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.customer_lead);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = LeadsActivity.this;
        if (Globals.checkInternet(mContext)) {
            binding.loaderLayout.loader.setVisibility(View.VISIBLE);
            pageno = 1;
            apicall = true;
            if (Prefs.getString(Globals.BussinessPageType, "AddBPLead").equalsIgnoreCase("AddBPLead") || Prefs.getString(Globals.BussinessPageType, "AddProposal").equalsIgnoreCase("AddProposal")) {
                StatusFilter = "Qualified";
                callApi(value, binding.loaderLayout.loader, maxItem, pageno);
            } else {
                StatusFilter = "";
                callApi(value, binding.loaderLayout.loader, maxItem, pageno);
            }

            setAdapter();
        } else {
            Toasty.error(mContext, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    //todo add variables to add filters key
    private boolean isAssignedTo = false;
    private boolean isSourceTo = false;

    // Create a list of assignedTo_id__in and source__in if needed
    ArrayList<String> assignedToList = new ArrayList<>();


    ArrayList<String> sourceList = new ArrayList<>();


    //todo lead list api..
    private void callApi(FilterOverAll leadValue, ProgressBar loader, String maxItem, int pageNo) {

        loader.setVisibility(View.VISIBLE);
        leadValue.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        leadValue.setMaxItem(maxItem);
        leadValue.setPageNo(pageNo);
        leadValue.setSearchText(searchTextValue);
        leadValue.setLeadType(Globals.lead);
        leadValue.setOrder_by_value(Globals.orderbyvalueDesc);
        leadValue.setOrder_by_field(Globals.orderbyField_id);
        FieldFilter fieldFilter = new FieldFilter();
        if (isAssignedToPresent) {
            assignedToList.add(assignToCode);
            fieldFilter.setAssignedTo_id__in(assignedToList);
        }

        if (isSourcePresent) {
            sourceList.add(SourceFilter);
            fieldFilter.setSource__in(sourceList);
        }
        leadValue.setField(fieldFilter);

        Call<LeadResponse> call = NewApiClient.getInstance().getApiService(this).getAllLead(leadValue);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {
                if (response.code() == 200) {

                    if (response.body().getStatus() == 200) {
                        loader.setVisibility(View.GONE);
                        if (response.body().getData().size() == 0) {
                            leadValueList.clear();
                            leadValueList.addAll(response.body().getData());
                            apicall = false;
                            adapter.notifyDataSetChanged();
                            loader.setVisibility(View.GONE);
                            binding.noDatafound.setVisibility(View.VISIBLE);

                        } else if (response.body().getData().size() > 0 && response.body().getData() != null) {
                            List<LeadValue> valueList = response.body().getData();
                            if (pageNo == 1) {
                                leadValueList.clear();
                                leadValueList.addAll(valueList);
                                adapter.AllData(valueList);
                            } else {
                                leadValueList.addAll(valueList);
                                adapter.AllData(valueList);
                            }
                            binding.swipeRefreshLayout.setRefreshing(false);

                            adapter.notifyDataSetChanged();

                            binding.noDatafound.setVisibility(View.GONE);

                            if (valueList.size() < 10) apicall = false;
                        }
                    } else {
                        binding.noDatafound.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        //  Toast.makeText(LeadsActivity.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                nodatafoundimage();
                loader.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                LeadsActivity.this.binding.loaderLayout.loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LeadResponse> call, Throwable t) {
                binding.swipeRefreshLayout.setRefreshing(false);
                LeadsActivity.this.binding.loaderLayout.loader.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);
                Toast.makeText(LeadsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callApiForPage(FilterOverAll leadValue, ProgressBar loader, String maxItem, int pageNo) {
        loader.setVisibility(View.VISIBLE);
        leadValue.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        leadValue.setMaxItem(maxItem);
        leadValue.setPageNo(pageNo);
        leadValue.setSearchText(searchTextValue);
        leadValue.setLeadType(Globals.lead);
        leadValue.setOrder_by_value(Globals.orderbyvalueDesc);
        leadValue.setOrder_by_field(Globals.orderbyField_id);
        FieldFilter fieldFilter = new FieldFilter();
        if (isAssignedToPresent) {
            assignedToList.add(assignToCode);
            fieldFilter.setAssignedTo_id__in(assignedToList);
        }

        if (isSourcePresent) {
            sourceList.add(SourceFilter);
            fieldFilter.setSource__in(sourceList);
        }
        leadValue.setField(fieldFilter);
        Call<LeadResponse> call = NewApiClient.getInstance().getApiService(this).getAllLead(leadValue);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus() == 200) {
                            binding.swipeRefreshLayout.setRefreshing(false);
                            loader.setVisibility(View.GONE);
                            if (response.body().getData().size() == 0) {
                                //   opportunityResposeArrayList_gl.clear();
                                leadValueList.addAll(response.body().getData());
                                apicall = false;
                                adapter.notifyDataSetChanged();
                                loader.setVisibility(View.GONE);
                                // binding.noDatafound.setVisibility(View.VISIBLE);

                            } else if (response.body().getData().size() > 0 && response.body().getData() != null) {
                                List<LeadValue> valueList = response.body().getData();
                                if (pageNo == 1) {
                                    //  opportunityResposeArrayList_gl.clear();
                                    leadValueList.addAll(valueList);
                                    adapter.AllData(valueList);
                                } else {
                                    leadValueList.addAll(valueList);
                                    adapter.AllData(valueList);
                                }
                                binding.swipeRefreshLayout.setRefreshing(false);

                                adapter.notifyDataSetChanged();

                                binding.noDatafound.setVisibility(View.GONE);

                                if (valueList.size() < 10) apicall = false;
                            }
                        } else if (response.body().getStatus() == 401) {
                            Gson gson = new GsonBuilder().create();
                            QuotationResponse mError = new QuotationResponse();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, QuotationResponse.class);
                                Toast.makeText(LeadsActivity.this, mError.getMessage(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen(getActivity());
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                        } else {
                            binding.noDatafound.setVisibility(View.VISIBLE);
                            loader.setVisibility(View.GONE);
                            Toast.makeText(LeadsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        loader.setVisibility(View.GONE);
                        Gson gson = new GsonBuilder().create();
                        QuotationResponse mError = new QuotationResponse();
                        try {
                            String s = response.errorBody().string();
                            mError = gson.fromJson(s, QuotationResponse.class);
                            Toast.makeText(LeadsActivity.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<LeadResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(loader.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    //todo set adpater.

    private void setAdapter() {
        layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        binding.leadRecyclerViewList.setLayoutManager(layoutManager);
        adapter = new LeadsAdapter(mContext, leadValueList);
        binding.leadRecyclerViewList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void showsourcedialog() {
        Dialog dialog = new Dialog(this);
        // LayoutInflater layoutInflater = context.getLayoutInflater();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View custom_dialog = layoutInflater.inflate(R.layout.dialog_recycler, null);
        dialog.setContentView(custom_dialog);
        //dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RecyclerView recyclerview = dialog.findViewById(R.id.dist);
        Button done = dialog.findViewById(R.id.done);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(new SourceAdpater(LeadsActivity.this, sourceData));


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.sourcefilter(Globals.sourcechecklist);
                }
//                nodatafoundimage();
                Log.e("list==>", String.valueOf(Globals.sourcechecklist));
                dialog.cancel();
            }
        });

        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lead_filter, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(((LeadsActivity) mContext).getSupportActionBar().getThemedContext());

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setQueryHint("Search Lead");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                pageNo = 1;
                searchTextValue = query;
                if (!searchTextValue.isEmpty()) {
                    isAssignedTo = false;
                    isSourceTo = false;
                    callApi(value, binding.loaderLayout.loader, maxItem, pageno);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.plus:
                startActivity(new Intent(this, AddLead.class));
                break;

            case R.id.filter:
                showAllFilterDialog();
                break;

            case android.R.id.home:
                this.finish();
                return true;
        }
        return true;
    }


    List<String> filterStatusList_gl = Arrays.asList(Globals.lead_status_list);
    String StatusFilter = "";
    String priorityType = "";
    String SourceFilter = "";
    String fromDate = "";
    String toDate = "";
    String assignToNameValue = "";
    String assignToCode = "";

    boolean isSourcePresent=false;
    boolean isAssignedToPresent=false;

    private void showAllFilterDialog() {
        Dialog dialog = new Dialog(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View custom_dialog = layoutInflater.inflate(R.layout.lead_filter_layout, null);
        dialog.setContentView(custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        AutoCompleteTextView acSource = dialog.findViewById(R.id.acSource);
        AutoCompleteTextView acStatus = dialog.findViewById(R.id.acStatus);
        AutoCompleteTextView acLeadPriority = dialog.findViewById(R.id.acLeadPriority);
        AutoCompleteTextView acAssignedTo = dialog.findViewById(R.id.acAssignedTo);
        LinearLayout assignRecyclerViewLayout = dialog.findViewById(R.id.assignRecyclerViewLayout);
        LinearLayout sourceRecyclerViewLayout = dialog.findViewById(R.id.sourceRecyclerViewLayout);
        RecyclerView rvCustomerSearchList = dialog.findViewById(R.id.rvCustomerSearchList);
        RecyclerView rvSourceSearchList = dialog.findViewById(R.id.rvSourceSearchList);
        TextInputEditText edtFromDate = dialog.findViewById(R.id.edtFromDate);
        TextInputEditText edtToDate = dialog.findViewById(R.id.edtToDate);
        MaterialButton resetBtn = dialog.findViewById(R.id.resetBtn);
        MaterialButton applyBtn = dialog.findViewById(R.id.applyBtn);
        ImageView ivCrossIcon = dialog.findViewById(R.id.ivCrossIcon);

        SearchableSpinner spinnerSource = dialog.findViewById(R.id.spinnerSearchable);
        spinnerSource.setTitle("Source Type");


        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SPINNER SEARCH", "onItemSelected: " + sourceData.get(i).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //todo set filter name in status--
        acStatus.setText(StatusFilter);
        acSource.setText(SourceFilter);
        acLeadPriority.setText(priorityType);
        acAssignedTo.setText(assignToNameValue);
        edtFromDate.setText(fromDate);
        edtToDate.setText(toDate);



        ivCrossIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        callSourceApi(acSource, sourceRecyclerViewLayout, rvSourceSearchList, spinnerSource);

        callleadTypeApi(acLeadPriority);

        callAssignToApi(acAssignedTo, assignRecyclerViewLayout, rvCustomerSearchList);

        //todo bind Status adapter ..
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeadsActivity.this, R.layout.drop_down_textview, filterStatusList_gl);
        acStatus.setAdapter(arrayAdapter);

        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.disableFutureDates(LeadsActivity.this, edtFromDate);

            }
        });


        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.enableAllCalenderDateSelect(LeadsActivity.this, edtToDate);

            }
        });

        //todo source drop down item select..
        acSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (sourceData.size() > 0) {
                    SourceFilter = sourceData.get(position).getName();
                    acSource.setText(sourceData.get(position).getName());
                }
            }
        });

        //todo status item click..
        acStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (filterStatusList_gl.size() > 0) {

                    StatusFilter = filterStatusList_gl.get(position);
                    acStatus.setText(filterStatusList_gl.get(position));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeadsActivity.this, R.layout.drop_down_textview, filterStatusList_gl);
                acStatus.setAdapter(arrayAdapter);
            }
        });


        //todo set item click of lead type..
        acLeadPriority.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LeadTypeData leadTypeData = leadTypeDataList_gl.get(position);
                priorityType = leadTypeData.getName();
                acLeadPriority.setText(leadTypeData.getName());
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAssignedToPresent=false;
                isSourcePresent=false;
                acSource.setText("");
                acStatus.setText("");
                acLeadPriority.setText("");
                acAssignedTo.setText("");
                edtFromDate.setText("");
                edtToDate.setText("");

                SourceFilter = "";
                StatusFilter = "";
                fromDate = "";
                toDate = "";
                assignToCode = "";
                priorityType = "";
            }
        });


        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDate = edtFromDate.getText().toString();
                toDate = edtToDate.getText().toString();

                if (SourceFilter.isEmpty()){
                    isSourcePresent=false;
                }else {
                    isSourcePresent=true;
                }

                if (assignToCode.isEmpty()){
                    isAssignedToPresent=false;
                }else {
                    isAssignedToPresent=true;
                }

                callApi(value, binding.loaderLayout.loader, maxItem, pageNo);

                dialog.dismiss();

                if (priorityType.equalsIgnoreCase("High")) {
                    binding.leadTypeSpinner.setSelection(1);
                    binding.dotcolor.setBackground(getDrawable(R.drawable.ic_blue_dot));
                } else if (priorityType.equalsIgnoreCase("Medium")) {
                    binding.dotcolor.setBackground(getDrawable(R.drawable.orange_dot));
                    binding.leadTypeSpinner.setSelection(2);
                } else if (priorityType.equalsIgnoreCase("Low")) {
                    binding.dotcolor.setBackground(getDrawable(R.drawable.project_title_));
                    binding.leadTypeSpinner.setSelection(3);
                } else {
                    binding.dotcolor.setBackground(getDrawable(R.drawable.white_dot));
                    binding.leadTypeSpinner.setSelection(0);
                }
            }
        });


        dialog.show();
    }


    ArrayList<SalesEmployeeItem> assignToResponseModelArrayList_gl = new ArrayList<>();

    //todo caling assign to api here..
    private void callAssignToApi(AutoCompleteTextView acAssignedTo, LinearLayout assignRecyclerViewLayout, RecyclerView rvCustomerSearchList) {
        EmployeeValue employeeValue = new EmployeeValue();
        employeeValue.setSalesEmployeeCode(Prefs.getString(Globals.SalesEmployeeCode, ""));

        Call<SaleEmployeeResponse> call = NewApiClient.getInstance().getApiService(this).getSalesEmplyeeList(employeeValue);
        call.enqueue(new Callback<SaleEmployeeResponse>() {
            @Override
            public void onResponse(Call<SaleEmployeeResponse> call, Response<SaleEmployeeResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getValue() != null && response.body().getValue().size() > 0) {
                                assignToResponseModelArrayList_gl.clear();
                                List<SalesEmployeeItem> itemsList = response.body().getValue();
                                itemsList = filterList(response.body().getValue());
                                assignToResponseModelArrayList_gl.addAll(itemsList);

                                List<String> itemNames = new ArrayList<>();
                                List<String> cardCodeName = new ArrayList<>();
                                for (SalesEmployeeItem item : assignToResponseModelArrayList_gl) {
                                    itemNames.add(item.getFirstName());
                                    cardCodeName.add(String.valueOf(item.getId()));
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(LeadsActivity.this, R.layout.drop_down_textview, itemNames);
                                acAssignedTo.setAdapter(adapter);

                                //todo item click on assign to---
                                acAssignedTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        String assignToName = itemNames.get(position);
                                        String assignToName = (String) parent.getItemAtPosition(position);
                                        assignToNameValue = assignToName;


                                        int pos = Globals.getAssignToPos(assignToResponseModelArrayList_gl, assignToName);
                                        assignToCode = String.valueOf(assignToResponseModelArrayList_gl.get(pos).getId());

                                        if (assignToName.isEmpty()) {
                                            assignRecyclerViewLayout.setVisibility(View.GONE);
                                            rvCustomerSearchList.setVisibility(View.GONE);
                                        } else {
                                            assignRecyclerViewLayout.setVisibility(View.VISIBLE);
                                            rvCustomerSearchList.setVisibility(View.VISIBLE);
                                        }

                                        if (!assignToName.isEmpty()) {
                                            adapter.notifyDataSetChanged();
                                            acAssignedTo.setText(assignToName);
                                            acAssignedTo.setSelection(acAssignedTo.length());

                                        } else {
                                            acAssignedTo.setText("");
                                        }
                                    }

                                });

                            } else {
                                Toast.makeText(LeadsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LeadsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(LeadsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SaleEmployeeResponse> call, Throwable t) {
                Toast.makeText(LeadsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<SalesEmployeeItem> filterList(ArrayList<SalesEmployeeItem> value) {
        List<SalesEmployeeItem> tempList = new ArrayList<>();
        for (SalesEmployeeItem customer : value) {
            if (!customer.getFirstName().equals("admi")) {
                tempList.add(customer);
            }
        }
        return tempList;
    }


    //todo calling source api here..
    String sourceValue = "";

    private void callSourceApi(AutoCompleteTextView acSource, LinearLayout sourceRecyclerViewLayout, RecyclerView rvSourceSearchList, SearchableSpinner searchableSourceSpinner) {
        Call<LeadTypeResponse> call = NewApiClient.getInstance().getApiService(this).getsourceType();
        call.enqueue(new Callback<LeadTypeResponse>() {
            @Override
            public void onResponse(Call<LeadTypeResponse> call, Response<LeadTypeResponse> response) {

                if (response.code() == 200) {
                    sourceData.clear();
                    sourceData.addAll(response.body().getData());
                    FilterSourceAdapter adapter = new FilterSourceAdapter(LeadsActivity.this, R.layout.drop_down_textview, sourceData);

                    SourceSearchableSpinnerAdapter sourceSearchableSpinnerAdapter = new SourceSearchableSpinnerAdapter(LeadsActivity.this, sourceData);
                    acSource.setAdapter(sourceSearchableSpinnerAdapter);
                    searchableSourceSpinner.setAdapter(sourceSearchableSpinnerAdapter);
                    //todo item click on assign to---
                    acSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        String assignToName = itemNames.get(position);
                            String assignToName = (String) parent.getItemAtPosition(position);
                            SourceFilter = assignToName;


                            int pos = Globals.getSourcePos(sourceData, assignToName);
                            //  Source = String.valueOf(sourceData.get(pos).getId());

                            if (assignToName.isEmpty()) {
                                sourceRecyclerViewLayout.setVisibility(View.GONE);
                                rvSourceSearchList.setVisibility(View.GONE);
                            } else {
                                sourceRecyclerViewLayout.setVisibility(View.VISIBLE);
                                rvSourceSearchList.setVisibility(View.VISIBLE);
                            }

                            if (!assignToName.isEmpty()) {
                                adapter.notifyDataSetChanged();
                                acSource.setText(assignToName);
                                acSource.setSelection(acSource.length());

                            } else {
                                acSource.setText("");
                            }
                        }

                    });


                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                        Toast.makeText(LeadsActivity.this, mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                }

            }

            @Override
            public void onFailure(Call<LeadTypeResponse> call, Throwable t) {

                Toast.makeText(LeadsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ArrayList<LeadTypeData> leadTypeDataList_gl = new ArrayList<>();

    //todo call lead type api here.
    private void callleadTypeApi(AutoCompleteTextView acLeadPriority) {
        Call<LeadTypeResponse> call = NewApiClient.getInstance().getApiService(this).getLeadType();
        call.enqueue(new Callback<LeadTypeResponse>() {
            @Override
            public void onResponse(Call<LeadTypeResponse> call, Response<LeadTypeResponse> response) {
                if (response.code() == 200) {
                    leadTypeDataList_gl.clear();
                    leadTypeDataList_gl.addAll(response.body().getData());

                    LeadPriorityAdapter leadTypeAdapter = new LeadPriorityAdapter(LeadsActivity.this, R.layout.drop_down_textview, leadTypeDataList_gl);
                    acLeadPriority.setAdapter(leadTypeAdapter);

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                        Toast.makeText(LeadsActivity.this, mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                }
            }

            @Override
            public void onFailure(Call<LeadTypeResponse> call, Throwable t) {
                Toast.makeText(LeadsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }

    }

    public void nodatafoundimage() {
        if (adapter.getItemCount() == 0) {
            binding.noDatafound.setVisibility(View.VISIBLE);
        } else {
            binding.noDatafound.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar:
                //openDateDialog();
                opencalendardialog();
                break;
            case R.id.all_list:
                binding.loaderLayout.loader.setVisibility(View.VISIBLE);
                pageno = 1;
                apicall = true;
                leadValueList.clear();
                callApi(value, binding.loaderLayout.loader, maxItem, pageno);
                binding.dateText.setVisibility(View.GONE);
                break;
        }
    }

    private void opencalendardialog() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        com.borax12.materialdaterangepicker.date.DatePickerDialog datePickerDialog = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(LeadsActivity.this, mYear, mMonth, mDay);
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");

    }

    String[] Date = new String[2];

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(com.borax12.materialdaterangepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        Date[0] = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        Date[1] = dayOfMonthEnd + "-" + (monthOfYearEnd + 1) + "-" + yearEnd;

        SimpleDateFormat format1 = new SimpleDateFormat("dd-M-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date startdate = null;
        java.util.Date enddate = null;
        try {
            startdate = format1.parse(Date[0]);
            enddate = format1.parse(Date[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        binding.dateText.setVisibility(View.VISIBLE);
        binding.dateText.setText(format2.format(startdate) + " to " + format2.format(enddate));
        adapter.datefilter(format2.format(startdate), format2.format(enddate));

        nodatafoundimage();
    }
}