package com.cinntra.vistadelivery.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.EditExpenseActivity;
import com.cinntra.vistadelivery.adapters.PreviousImageViewAdapter;
import com.cinntra.vistadelivery.databinding.AddExpenseBinding;
import com.cinntra.vistadelivery.databinding.ExpenseDetailLayoutBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.ExpenseDataModel;
import com.cinntra.vistadelivery.model.TokenExpireModel;
import com.cinntra.vistadelivery.model.expenseModels.ExpenseOneDataResponseModel;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Objects;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseDetailFragment extends Fragment{

    ExpenseDataModel expenseDataModel;

    int salesEmployeeCode = 0;

    ExpenseDetailLayoutBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            expenseDataModel = (ExpenseDataModel) b.getSerializable(Globals.ExpenseData);

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ExpenseDetailLayoutBinding.inflate(getLayoutInflater());
       /* View v = inflater.inflate(R.layout.add_expense, container, false);
        ButterKnife.bind(this, v);*/

        binding.headerLayout.headTitle.setText("Expense Detail");
        binding.headerLayout.backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        binding.headerLayout.editExpense.setVisibility(View.VISIBLE);
        binding.headerLayout.editExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditExpenseActivity.class);
                intent.putExtra("id", expenseDataModel.getId());
                startActivity(intent);

            }
        });


        if (Globals.checkInternet(getContext())) {
            callOneDetailApi();
        }

        return binding.getRoot();
    }

    //todo call expense one api here..
    private void callOneDetailApi(){
        binding.loaderLayout.loader.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", expenseDataModel.getId());
        Call<ExpenseOneDataResponseModel> call = NewApiClient.getInstance().getApiService(requireContext()).getExpenseOneData(jsonObject);
        call.enqueue(new Callback<ExpenseOneDataResponseModel>() {
            @Override
            public void onResponse(Call<ExpenseOneDataResponseModel> call, Response<ExpenseOneDataResponseModel> response) {
                try {
                    if (response.code() == 200){
                        if (response.body().getStatus() == 200){
                            if (response.body().getData().size() > 0 && response.body().getData() != null){
                                binding.loaderLayout.loader.setVisibility(View.GONE);
                                setDefaultData(response.body().getData().get(0));
                            }

                        }else {
                            binding.loaderLayout.loader.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else if (response.code() == 401){
                        binding.loaderLayout.loader.setVisibility(View.GONE);
                        if (response.body().getStatus() == 301){
                            Gson gson = new GsonBuilder().create();
                            TokenExpireModel mError = new TokenExpireModel();
                            try {
                                String s = response.errorBody().string();
                                mError = gson.fromJson(s, TokenExpireModel.class);
                                Toast.makeText(getActivity(), mError.getDetail(), Toast.LENGTH_LONG).show();
//                                Globals.logoutScreen(getActivity());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }catch (Exception e){
                    binding.loaderLayout.loader.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ExpenseOneDataResponseModel> call, Throwable t) {
                binding.loaderLayout.loader.setVisibility(View.GONE);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultData(ExpenseOneDataResponseModel.Data modelData) {
        binding.tvEmployeeName.setText(modelData.getCreatedBy().get(0).getFirstName());
        binding.tvTripName.setText(modelData.getTrip_name());
        binding.tvExpenseName.setText(modelData.getType_of_expense());
        binding.tvTotalAmount.setText(modelData.getTotalAmount());
        if (modelData.getEmployeeId().size() > 0){
            binding.tvTravelBy.setText(modelData.getEmployeeId().get(0).getFirstName());
        }

        if (modelData.getExpense_from().isEmpty()){
            binding.tvFromExpenseDate.setText("NA");
        }else {
            binding.tvFromExpenseDate.setText(modelData.getExpense_from());
        }

        if (modelData.getExpense_to().isEmpty()){
            binding.tvToExpenseDate.setText("NA");
        }else {
            binding.tvToExpenseDate.setText(modelData.getExpense_to());
        }

        if (modelData.getRemarks().isEmpty()){
            binding.tvRemarrks.setText("NA");
        }else {
            binding.tvRemarrks.setText(modelData.getRemarks());
        }

        PreviousImageViewAdapter adapter = new PreviousImageViewAdapter(getContext(), modelData.getAttach(), "");
        binding.prevattachment.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        binding.prevattachment.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
    }


}
