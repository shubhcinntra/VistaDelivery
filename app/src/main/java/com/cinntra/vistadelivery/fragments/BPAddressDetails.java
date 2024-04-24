package com.cinntra.vistadelivery.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cinntra.vistadelivery.activities.AddContact;
import com.cinntra.vistadelivery.activities.bpActivity.AddBranchActivity;
import com.cinntra.vistadelivery.adapters.BusinessPartnerDetail;
import com.cinntra.vistadelivery.adapters.bpAdapters.BPAttachmentAdapter;
import com.cinntra.vistadelivery.adapters.bpAdapters.BPContactPersonAdapter;
import com.cinntra.vistadelivery.adapters.bpAdapters.BillAddressListAdapter;
import com.cinntra.vistadelivery.databinding.FragmentBPAddressDetailsBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.BPModel.AtatchmentListModel;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.cinntra.vistadelivery.model.BPModel.BussinessPartnerDetailModel;
import com.cinntra.vistadelivery.model.BPModel.ContactOneAPiModel;
import com.cinntra.vistadelivery.model.BranchResponse;
import com.cinntra.vistadelivery.model.OpportunityModels.OppAddressResponseModel;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPAddressDetails extends Fragment implements BPAttachmentAdapter.OnItemClickListener, BPContactPersonAdapter.OnItemClickListener, BillAddressListAdapter.OnItemClickListener{
    BusinessPartnerAllResponse.Datum customerItem;
//    BusinessPartnerData customerItem;
    FragmentBPAddressDetailsBinding binding;
    BussinessPartnerDetailModel.Datum dataModel_gl = new BussinessPartnerDetailModel.Datum();
    List<Uri> mSelected = new ArrayList<>();
    List<String> path = new ArrayList<>();
    private static final int RESULT_LOAD_IMAGE = 102;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    File file;
    String picturePath = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public BPAddressDetails(BusinessPartnerDetail businessPartnerDetail, BusinessPartnerAllResponse.Datum customerItem) {
        this.customerItem = customerItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentBPAddressDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loaderLayout.loader.setVisibility(View.GONE);
        callBpAddressAPI();

        binding.addBillBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddBranchActivity.class);
                intent.putExtra("cardCode", dataModel_gl.getCardCode()); // Replace "key" and "value" with your data
                intent.putExtra("flag", "BillTO"); // Replace "key" and "value" with your data
                intent.putExtra("BPLID", dataModel_gl.getBPAddresses().get(0).getBPID()); // Replace "key" and "value" with your data
                startActivity(intent);
            }
        });

        binding.addShipBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddBranchActivity.class);
                intent.putExtra("cardCode", dataModel_gl.getCardCode()); // Replace "key" and "value" with your data
                intent.putExtra("flag", "ShipTO");
                startActivity(intent);
            }
        });

        binding.addContactPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContact.class);
                intent.putExtra("cardCode", dataModel_gl.getCardCode()); // Replace "key" and "value" with your data
                intent.putExtra("flag", "AddContact");
                intent.putExtra("BPLID", dataModel_gl.getBPAddresses().get(0).getBPID());
                startActivity(intent);
            }
        });


        binding.attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentDispatcher();
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        callBpAddressAPI();
        callAllBranchApi();
    }

    //todo cal/ing default function for calling api here...
    private void setDefaultData() {

        callAllBranchApi();


        if (dataModel_gl.getBPAddresses().size() > 0){
            Prefs.putString(Globals.STOREBPLID, dataModel_gl.getBPAddresses().get(0).getBPID());
        }
        //todo bind contact listing here
        if (dataModel_gl.getContactEmployees().size() > 0){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            BPContactPersonAdapter adapter = new BPContactPersonAdapter(getContext(), dataModel_gl.getContactEmployees());
            binding.rvContactPersonList.setLayoutManager(linearLayoutManager);
            binding.rvContactPersonList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(BPAddressDetails.this);
            binding.tvCPNoDataFound.setVisibility(View.GONE);
        }
        else {
            binding.tvCPNoDataFound.setVisibility(View.VISIBLE);
            binding.rvContactPersonList.setVisibility(View.GONE);
        }

    }


    //todo calling bp api here..
    private void callBpAddressAPI() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CardCode", customerItem.getCardCode());
        Call<BussinessPartnerDetailModel> call = NewApiClient.getInstance().getApiService(getActivity()).businessPartnerDetailOne(jsonObject);
        call.enqueue(new Callback<BussinessPartnerDetailModel>() {
            @Override
            public void onResponse(Call<BussinessPartnerDetailModel> call, Response<BussinessPartnerDetailModel> response) {
                if (response.body().getStatus() == 200) {
                    dataModel_gl = response.body().getData().get(0);
                    setDefaultData();
                    callAttachmentAPi();
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
            public void onFailure(Call<BussinessPartnerDetailModel> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    //todo calling bp api here..
    private void callAttachmentAPi() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cust_id", Integer.parseInt(dataModel_gl.getId()));
        jsonObject.addProperty("SearchText","");
        Call<AtatchmentListModel> call = NewApiClient.getInstance().getApiService(getActivity()).getAllBPAttchmentList(jsonObject);
        call.enqueue(new Callback<AtatchmentListModel>() {
            @Override
            public void onResponse(Call<AtatchmentListModel> call, Response<AtatchmentListModel> response) {
                if (response.body().getStatus() == 200) {

                    ArrayList<AtatchmentListModel.Data> responseList = response.body().getData();
                    if (responseList.size() > 0){
                        binding.tvAttchNoDataFound.setVisibility(View.GONE);
                        BPAttachmentAdapter adapter = new BPAttachmentAdapter(getActivity(), responseList);
                        binding.prevattachment.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
                        binding.prevattachment.setAdapter(adapter);
                        adapter.setOnItemClickListener(BPAddressDetails.this);
                        adapter.notifyDataSetChanged();
                    }else {
                        binding.prevattachment.setVisibility(View.GONE);
                        binding.tvAttchNoDataFound.setVisibility(View.VISIBLE);
                    }

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    AtatchmentListModel mError = new AtatchmentListModel();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, AtatchmentListModel.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AtatchmentListModel> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    //todo calling branch to address api here..
    private void callAllBranchApi() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("BPCode", customerItem.getCardCode());
        jsonObject.addProperty("SearchText","");
        jsonObject.addProperty("order_by_field",Globals.orderbyField_id);
        jsonObject.addProperty("order_by_value",Globals.orderbyvalueDesc);
        JsonObject json=new JsonObject();
                jsonObject.add("field",json);



        Call<OppAddressResponseModel> call = NewApiClient.getInstance().getApiService(getActivity()).getShipToAddress(jsonObject);
        call.enqueue(new Callback<OppAddressResponseModel>() {
            @Override
            public void onResponse(Call<OppAddressResponseModel> call, Response<OppAddressResponseModel> response) {
                if (response.body().getStatus() == 200) {
                    ArrayList<OppAddressResponseModel.Data> billToAddressList_gl = new ArrayList<>();
                    billToAddressList_gl.clear();
                    billToAddressList_gl.addAll(response.body().getData());

                    if (billToAddressList_gl.size() > 0) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        BillAddressListAdapter adapter = new BillAddressListAdapter(getContext(), billToAddressList_gl, dataModel_gl.getCardType());
                        binding.rvBillToDetailList.setLayoutManager(linearLayoutManager);
                        binding.rvBillToDetailList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        adapter.setOnItemClickListener(BPAddressDetails.this);
                        binding.tvBillToAddrNoDataFound.setVisibility(View.GONE);
                    }else {
                        binding.tvBillToAddrNoDataFound.setVisibility(View.VISIBLE);
                        binding.rvBillToDetailList.setVisibility(View.GONE);
                    }

                } else {

                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OppAddressResponseModel> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void intentDispatcher() {
        checkAndRequestPermissions();

        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Bundle extras = data.getExtras();
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.e("picturePath", picturePath);
                    file = new File(picturePath);
                    Log.e("FILE>>>>", "onActivityResult: " + file.getName());
                    binding.loaderLayout.loader.setVisibility(View.VISIBLE);
                    updateAttachment();
                }
            }
        }else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }


    }

    private void updateAttachment() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("cust_id", dataModel_gl.getId());
        builder.addFormDataPart("CreateDate", Globals.getTodaysDatervrsfrmt());
        builder.addFormDataPart("CreateTime", Globals.getTCurrentTime());
        if (!picturePath.isEmpty()){
            File file ;
            try {
                file = new File(picturePath);
                MultipartBody.Part attach  = MultipartBody.Part.createFormData("Attach", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),file));
                builder.addPart(attach);

            } catch (Exception e) {
                builder.addFormDataPart("Attach", "", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                e.printStackTrace();
            }
        }else{
            builder.addFormDataPart("Attach","");
        }


        MultipartBody requestBody = builder.build();
        Call<AtatchmentListModel> call = NewApiClient.getInstance().getApiService(getActivity()).createBPAttachment(requestBody);
        call.enqueue(new Callback<AtatchmentListModel>() {
            @Override
            public void onResponse(Call<AtatchmentListModel> call, Response<AtatchmentListModel> response) {
                binding.loaderLayout.loader.setVisibility(View.GONE);

                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatus() == 200) {
                        Toasty.success(getActivity(), "Add Successfully", Toast.LENGTH_LONG).show();
                        callBpAddressAPI();
                    } else {
                        Toasty.warning(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<AtatchmentListModel> call, Throwable t) {
                binding.loaderLayout.loader.setVisibility(View.GONE);
                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    //todo delete contact function override---

    @Override
    public void onItemClick(int position, BussinessPartnerDetailModel.ContactEmployee data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Contact Person ?")
                .setMessage("Are you want to Delete this Contact Person ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteContactDataFunc(data.getId());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteContactDataFunc(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", Integer.parseInt(id));
        Call<ContactOneAPiModel> call = NewApiClient.getInstance().getApiService(getActivity()).deleteContact(jsonObject);
        call.enqueue(new Callback<ContactOneAPiModel>() {
            @Override
            public void onResponse(Call<ContactOneAPiModel> call, Response<ContactOneAPiModel> response) {
                if (response.body().getStatus() == 200) {

                    Toasty.success(getActivity(), "Delete Contact Successful");
                    callBpAddressAPI();

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    ContactOneAPiModel mError = new ContactOneAPiModel();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, ContactOneAPiModel.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactOneAPiModel> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //todo delete bill address data--
    @Override
    public void onItemClick(int position, OppAddressResponseModel.Data data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Branch ?")
                .setMessage("Are you want to Delete this Branch ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteBranchListFunch(data.getId());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBranchListFunch(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", Integer.parseInt(id));
        Call<BranchResponse> call = NewApiClient.getInstance().getApiService(getActivity()).deleteBranch(jsonObject);
        call.enqueue(new Callback<BranchResponse>() {
            @Override
            public void onResponse(Call<BranchResponse> call, Response<BranchResponse> response) {
                if (response.body().getStatus() == 200) {

                    Toasty.success(getActivity(), "Delete Branch Successful");
                    callBpAddressAPI();

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    BranchResponse mError = new BranchResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, BranchResponse.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BranchResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //todo call delete image function override here---
   @Override
    public void onItemClick(int position, AtatchmentListModel.Data data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Attachment?")
                .setMessage("Are You Sure ? You Want to Delete this Picture")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAttachmentAPi(data.getId(), data.getCustId());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void deleteAttachmentAPi(int id, int custId){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cust_id", custId);
        jsonObject.addProperty("id", id);
        Call<AtatchmentListModel> call = NewApiClient.getInstance().getApiService(getActivity()).deleteBPAttachment(jsonObject);
        call.enqueue(new Callback<AtatchmentListModel>() {
            @Override
            public void onResponse(Call<AtatchmentListModel> call, Response<AtatchmentListModel> response) {
                if (response.body().getStatus() == 200) {

                    Toasty.success(getActivity(), "Delete Successful");
                    callBpAddressAPI();

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    AtatchmentListModel mError = new AtatchmentListModel();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, AtatchmentListModel.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AtatchmentListModel> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}