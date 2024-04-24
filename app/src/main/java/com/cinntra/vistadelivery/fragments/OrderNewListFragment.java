package com.cinntra.vistadelivery.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.adapters.DemoAdapter;
import com.cinntra.vistadelivery.databinding.OrderNewListFragmentBlankBinding;
import com.cinntra.vistadelivery.model.QuotationItem;

import java.util.ArrayList;


public class OrderNewListFragment extends Fragment {
//    @BindView(R.id.recyclerview)
//    RecyclerView recyclerview;
//    @BindView(R.id.loader)
//    ProgressBar loader;
//    @BindView(R.id.no_datafound)
//    ImageView no_datafound;
//    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;
    OrderNewListFragmentBlankBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        binding = OrderNewListFragmentBlankBinding.inflate(getLayoutInflater());
        View v = inflater.inflate(R.layout.fragment_open_order, container, false);
        return v.getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // ButterKnife.bind((Activity) requireContext());
      //  recyclerview = view.findViewById(R.id.recyclerview);
//        callApi();
    }

    ArrayList<QuotationItem> AllItemNewList = new ArrayList<>();
    /*void callApi(){
        HashMap<String, String> hd = new HashMap<>();
        hd.put("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService(requireContext()).OrdersList(hd);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                if (response.body().getStatus() == 200){
                    AllItemNewList.clear();
                    AllItemNewList.addAll(response.body().getValue());
                    Toast.makeText(requireContext(), "Adapter List==>" + AllItemNewList.size(), Toast.LENGTH_SHORT).show();

                    setAdapter();
                }else {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/


    private void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
     //   recyclerview.setLayoutManager(layoutManager);
        DemoAdapter adapter = new DemoAdapter(getActivity(), AllItemNewList);
       // recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}