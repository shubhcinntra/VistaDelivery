package com.cinntra.vistadelivery.fragments;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.InvoiceActivity;
import com.cinntra.vistadelivery.adapters.Invoices_Adapter;
import com.cinntra.vistadelivery.databinding.FragmentQuotesListBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.InvoiceNewData;
import com.cinntra.vistadelivery.model.InvoiceResponse;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.PerformaInvoiceListRequestModel;
import com.cinntra.vistadelivery.viewModel.QuotationList_ViewModel;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.pixplicity.easyprefs.library.Prefs;

import java.time.LocalDate;
import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Invoices_Override_Fragment extends Fragment implements View.OnClickListener {

    private QuotationList_ViewModel viewModel;
//  @BindView(R.id.recyclerview)
//  RecyclerView recyclerview;
//  @BindView(R.id.loader)
//  ProgressBar loader;
//    @BindView(R.id.no_datafound)
//    ImageView no_datafound;
//    @BindView(R.id.swipeRefreshLayout)
//    PullRefreshLayout swipeRefreshLayout;


    private SearchView searchView;
    private Invoices_Adapter adapter;
    int currentpage = 0;
    boolean recallApi = true;
    LinearLayoutManager layoutManager;
    ArrayList<InvoiceNewData> AllItemList;


    public Invoices_Override_Fragment() {
        //Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Invoices_Override_Fragment newInstance(String param1, String param2) {
        Invoices_Override_Fragment fragment = new Invoices_Override_Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);


    }

    FragmentQuotesListBinding binding;
    Boolean isScrollingpage = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        binding = FragmentQuotesListBinding.inflate(inflater, container, false);
        View v = inflater.inflate(R.layout.fragment_quotes_list, container, false);
        //   ButterKnife.bind(this,v);
        AllItemList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        eventSearchManager();
        if (Globals.checkInternet(getActivity()))
            callApi(binding.loaderLayout.loader);

        binding.noDatafound.setVisibility(View.VISIBLE);

        //todo pagination for list..
        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition();


                if (isScrollingpage && lastCompletelyVisibleItemPosition == AllItemList.size() - 2) {
                    page++;
                    Log.e("page--->", String.valueOf(page));
                    callAllPageApi(binding.loaderLayout.loader);
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
                if (Globals.checkInternet(getActivity())) {
                    page = 1;

                    callApi(binding.loaderLayout.loader);
                } else
                    binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        return binding.getRoot();
    }

    private void eventSearchManager() {
        searchView = (SearchView) getActivity().findViewById(R.id.searchView);
        searchView.setBackgroundColor(Color.parseColor("#00000000"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page = 1;
                searchTextValue = query;
                callApi(binding.loaderLayout.loader);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                page = 1;
                searchTextValue = newText;
                callApi(binding.loaderLayout.loader);
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
        /*  case R.id.new_quatos:
          fragment = new New_Quotation();
        FragmentManager fm       = getFragmentManager();
        FragmentTransaction transaction  = fm.beginTransaction();
       //FragmentTransaction transaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.quatoes_main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
           break;*/


        }


    }

    int page = 1;
    int maxItem = 10;
    String searchTextValue = "";

    private void callApi(ProgressBar loader) {
        loader.setVisibility(View.VISIBLE);

        PerformaInvoiceListRequestModel opportunityAllListRequest = new PerformaInvoiceListRequestModel();
        opportunityAllListRequest.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        opportunityAllListRequest.setMaxItem(maxItem);
        opportunityAllListRequest.setPageNo(page);
        opportunityAllListRequest.setSearchText(searchTextValue);
        opportunityAllListRequest.setOrder_by_field(Globals.orderbyField_id);
        opportunityAllListRequest.setOrder_by_value(Globals.orderbyvalueDesc);
        PerformaInvoiceListRequestModel.Field field = new PerformaInvoiceListRequestModel.Field();
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
        Call<InvoiceResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getAllInvoiceFilter(opportunityAllListRequest);
        call.enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(Call<InvoiceResponse> call, Response<InvoiceResponse> response) {

                if (response.code() == 200) {
                    if (response.body().getStatus().equals(200)) {
                        AllItemList.clear();
                        AllItemList.addAll(response.body().getValue());

                        adapter = new Invoices_Adapter(getContext(), AllItemList);
                        binding.recyclerview.setLayoutManager(layoutManager);
                        binding.recyclerview.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if (adapter.getItemCount() == 0)
                            binding.noDatafound.setVisibility(View.VISIBLE);
                        else
                            binding.noDatafound.setVisibility(View.GONE);
                    } else if (response.body().getStatus().equals(201)) {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
                loader.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<InvoiceResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.e("ONRESPONSE", "onFailure: "+t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callAllPageApi(ProgressBar loader) {
        loader.setVisibility(View.VISIBLE);

        PerformaInvoiceListRequestModel opportunityAllListRequest = new PerformaInvoiceListRequestModel();
        opportunityAllListRequest.setSalesPersonCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        opportunityAllListRequest.setMaxItem(maxItem);
        opportunityAllListRequest.setPageNo(page);
        opportunityAllListRequest.setSearchText(searchTextValue);
        opportunityAllListRequest.setOrder_by_field(Globals.orderbyField_id);
        opportunityAllListRequest.setOrder_by_value(Globals.orderbyvalueDesc);
        PerformaInvoiceListRequestModel.Field field = new PerformaInvoiceListRequestModel.Field();
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
        Call<InvoiceResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getAllInvoiceFilter(opportunityAllListRequest);
        call.enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(Call<InvoiceResponse> call, Response<InvoiceResponse> response) {

                if (response.code() == 200) {
                    if (response.body().getStatus().equals(200)) {

                        AllItemList.addAll(response.body().getValue());


                        adapter.notifyDataSetChanged();

                    } else if (response.body().getStatus().equals(201)) {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
                loader.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<InvoiceResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        //menu.clear();
        inflater.inflate(R.menu.invoice_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.search);

        SearchView searchView = new SearchView(((InvoiceActivity) getContext()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page=1;
                searchTextValue=query;
                callApi(binding.loaderLayout.loader);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

              page=1;
              searchTextValue=newText;
              callApi(binding.loaderLayout.loader);
                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                          }
                                      }
        );


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all:
            /*    if (adapter != null)
                    adapter.AllData();*/
                break;
            case R.id.my:
                if (adapter != null)
                    adapter.Customername();
                break;
            case R.id.my_team:
                break;
            case R.id.valid:
                if (adapter != null)
                    adapter.ValidDate();
                break;
            case R.id.newest:
                LocalDate dateObj1 = LocalDate.parse(Globals.curntDate);
                LocalDate afterdate1 = dateObj1.minusDays(8);
                adapter.PostingDate(afterdate1, dateObj1);
                break;
            case R.id.oldest:
                Toast.makeText(getContext(), "Existing", Toast.LENGTH_LONG).show();
                break;

        }
        return true;
    }


}