package com.cinntra.vistadelivery.fragments;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.adapters.OrderDropDownSpinnerAdapter;
import com.cinntra.vistadelivery.adapters.QuotationDropDownSpinnerAdapter;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.interfaces.CommentStage;
import com.cinntra.vistadelivery.interfaces.FragmentRefresher;
import com.cinntra.vistadelivery.model.CompleteStageResponse;
import com.cinntra.vistadelivery.model.OpportunityModels.OpportunityStageResponse;
import com.cinntra.vistadelivery.model.StagesValue;
import com.cinntra.vistadelivery.newapimodel.ResponseOrderListDropDown;
import com.cinntra.vistadelivery.newapimodel.ResponseQuoteListDropDown;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    Context context;
    List<StagesValue> itemsList;
    String currentstageno;
    FragmentRefresher fragmentRefresher;
    CommentStage commentStage;
    String selectedStage= "";

    public TimelineAdapter(Opportunity_Detail_NewFragment fra,Context context, List<StagesValue> itemsList, String currentStageNo) {
        this.context = context;
        this.commentStage = fra;
        this.itemsList = itemsList;
        this.currentstageno = currentStageNo;
        this.fragmentRefresher = fra;
    }

    @NonNull
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.opportunity_timelineview, parent, false);
        return new TimelineAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.ViewHolder holder, int position) {

        StagesValue obj = getItem(position);

        holder.stage_name.setText(obj.getName());
        if(position==itemsList.size()-1){
            holder.divider.setVisibility(View.GONE);
        }
//        holder.date.setText(obj.get());
        if (obj.getStatus() ==1){
            holder.tick_green.setBackground(context.getResources().getDrawable(R.drawable.tick_square_blue));
            holder.divider.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
        }else if (obj.getStatus() ==0) {
            holder.tick_green.setBackground(context.getResources().getDrawable(R.drawable.tick_square_grey));
            holder.divider.setBackgroundColor(ContextCompat.getColor(context,R.color.lightGrey));
        }else if (obj.getStatus()==2){
            holder.tick_green.setBackground(context.getResources().getDrawable(R.drawable.tick_square_green));
            holder.divider.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               commentStage.stagecomment(obj.getStageno(),obj.getName());
                if(position==itemsList.size() ){
                    if(obj.getStatus()==2){

                    }
                    else
                    openfinaldialog(obj.getOpp_Id());
                }else if (obj.getStatus()==0||obj.getStatus()==1){
                    opencommentdialog(obj.getStageno(),obj.getOpp_Id(),obj.getId(),obj.getName());
                }
            }
        });

    }

    private void opencommentdialog(String stageno, String oppId,Integer id,String name) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.addcomment_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        Button done = dialog.findViewById(R.id.done);
        EditText comment = dialog.findViewById(R.id.comment);
        EditText startDate = dialog.findViewById(R.id.start_date_value);
        EditText endDate = dialog.findViewById(R.id.end_date_value);

        LinearLayout linearDateSelector = dialog.findViewById(R.id.linearDateSelector);


        Spinner spinnerQuotation = dialog.findViewById(R.id.spinnerSelectQuote);
        LinearLayout linearSelectQuotation = dialog.findViewById(R.id.linearSelectQuotation);

        Spinner spinnerorder = dialog.findViewById(R.id.spinnerSelectOrder);
        LinearLayout linearSelectOrder = dialog.findViewById(R.id.linearSelectOrder);


        RelativeLayout relativeDateStart = dialog.findViewById(R.id.startDateRelative);
        RelativeLayout relativeDateend = dialog.findViewById(R.id.endDateRelative);


        linearDateSelector.setVisibility(View.VISIBLE);

        if (stageno.equals("3.0")||stageno.equals("4.0")){
            linearSelectQuotation.setVisibility(View.VISIBLE);

            loadQuotDropDownlist(spinnerQuotation);
        }

        if (stageno.equals("5.0")){
            linearSelectOrder.setVisibility(View.VISIBLE);

            loadOrderDropDownlist(spinnerorder);
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.COMMENT = comment.getText().toString();
                if(!comment.getText().toString().trim().isEmpty()){

                callUpdatestageapi(stageno,oppId,id,name,startDate,endDate,selectedQuote);
                dialog.cancel();
                }else{
                    Toast.makeText(context,"Please write some Comment",Toast.LENGTH_LONG).show();
                }
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate(startDate);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closetDate(endDate);
            }
        });

        dialog.show();
    }

    List<ResponseQuoteListDropDown.Datum> quoteDropDowntypelist = new ArrayList<>();
    String selectedQuote="";

    List<ResponseOrderListDropDown.Datum> orderDropDowntypelist = new ArrayList<>();


    private void loadQuotDropDownlist(Spinner quotaion) {
        /*{
            "departement": 2,
                "SalesPersonCode": "64"
        }*/
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("departement","2");
        jsonObject.addProperty("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode,""));
        Call<ResponseQuoteListDropDown> call = NewApiClient.getInstance().getApiService(context).callQuoteListDropDown(jsonObject);
        call.enqueue(new Callback<ResponseQuoteListDropDown>() {
            @Override
            public void onResponse(Call<ResponseQuoteListDropDown> call, Response<ResponseQuoteListDropDown> response) {
                if (response != null) {
                    if (response.isSuccessful()){
                        if (response.body().getStatus().equals("200")){
                            if (response.body().getData().size()>0){
                                quoteDropDowntypelist.clear();
                                quoteDropDowntypelist.addAll(response.body().getData());
                                quotaion.setAdapter(new QuotationDropDownSpinnerAdapter(context, quoteDropDowntypelist));
                                selectedQuote = quoteDropDowntypelist.get(0).getId().toString();

                                quotaion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (quoteDropDowntypelist.size() > 0)
                                            selectedQuote = quoteDropDowntypelist.get(position).getId().toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseQuoteListDropDown> call, Throwable t) {

            }
        });
    }

    private void loadOrderDropDownlist(Spinner order) {
        /*{
            "departement": 2,
                "SalesPersonCode": "64"
        }*/
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("departement","2");
        jsonObject.addProperty("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode,""));
        Call<ResponseOrderListDropDown> call = NewApiClient.getInstance().getApiService(context).callOrderListDropDown(jsonObject);
        call.enqueue(new Callback<ResponseOrderListDropDown>() {
            @Override
            public void onResponse(Call<ResponseOrderListDropDown> call, Response<ResponseOrderListDropDown> response) {
                if (response != null) {
                    if (response.isSuccessful()){
                        if (response.body().getStatus().equals("200")){
                            if (response.body().getData().size()>0){
                                orderDropDowntypelist.clear();
                                orderDropDowntypelist.addAll(response.body().getData());
                                order.setAdapter(new OrderDropDownSpinnerAdapter(context, orderDropDowntypelist));
                                selectedQuote = orderDropDowntypelist.get(0).getId().toString();

                                order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (orderDropDowntypelist.size() > 0)
                                            selectedQuote = orderDropDowntypelist.get(position).getId().toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseOrderListDropDown> call, Throwable t) {

            }
        });
    }


    private void startDate(EditText startDateValue) {
        Globals.selectDate(context, startDateValue);
    }

    private void closetDate(EditText endDateValue) {
        Globals.enableAllCalenderDateSelect(context, endDateValue);
    }

    private void callUpdatestageapi(String currentStageNo, String oppId,Integer id,String name,EditText startDate,EditText endDate,String docId) {
        StagesValue stval = new StagesValue();
        stval.setOpp_Id(String.valueOf(oppId));
        stval.setUpdateDate(Globals.getTodaysDate());
        stval.setUpdateDate(Globals.getTCurrentTime());
        stval.setStageno(currentStageNo);
        stval.setSequenceNo(currentStageNo);
        stval.setComment(Globals.COMMENT);
        stval.setFile("");
        stval.setId(id);
        stval.setName(name);


        stval.setDocId(docId);
        stval.setStatus(0);
        stval.setStartDate(startDate.getText().toString());
        stval.setEndDate(endDate.getText().toString());
        stval.setCancelled("tNO");

        Call<OpportunityStageResponse> call = NewApiClient.getInstance().getApiService(context).updatestage(stval);
        call.enqueue(new Callback<OpportunityStageResponse>() {
            @Override
            public void onResponse(Call<OpportunityStageResponse> call, Response<OpportunityStageResponse> response) {

                if(response.code()==200){
                    fragmentRefresher.onRefresh();
                    Log.e("pass","PASS");
                }
            }
            @Override
            public void onFailure(Call<OpportunityStageResponse> call, Throwable t) {
                Log.e("failure",""+t.getMessage());
            }
        });
    }

    private void openfinaldialog(String oppId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.completestage_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        Button done = dialog.findViewById(R.id.save);
        Spinner previous_stage = dialog.findViewById(R.id.previous_stage);
        EditText comments_val = dialog.findViewById(R.id.comments_val);
        previous_stage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStage = previous_stage.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStage = previous_stage.getSelectedItem().toString();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!comments_val.getText().toString().trim().isEmpty()) {
                    CompleteStageResponse completeStageResponse = new CompleteStageResponse();
                    completeStageResponse.setOppId(Integer.valueOf(oppId));
                    completeStageResponse.setStatus(selectedStage);
                    completeStageResponse.setRemarks(comments_val.getText().toString());
                    completeStageResponse.setUpdateDate(Globals.getTodaysDate());
                    completeStageResponse.setUpdateTime(Globals.getTCurrentTime());
                    callcompletestageApi(completeStageResponse);
                    dialog.cancel();
                }
            }
        });

        dialog.show();
    }
    private void callcompletestageApi(CompleteStageResponse completeStageResponse) {


        Call<OpportunityStageResponse> call = NewApiClient.getInstance().getApiService(context).completestage(completeStageResponse);
        call.enqueue(new Callback<OpportunityStageResponse>() {
            @Override
            public void onResponse(Call<OpportunityStageResponse> call, Response<OpportunityStageResponse> response) {

                if(response.code()==200){
                    fragmentRefresher.onRefresh();

                }
            }
            @Override
            public void onFailure(Call<OpportunityStageResponse> call, Throwable t) {
                Log.e("failure",""+t.getMessage());
            }
        });
    }

    public StagesValue getItem(int po) {
        return itemsList.get(po);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView stage_name,date;
        ImageView tick_green;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stage_name = itemView.findViewById(R.id.stage_name);
            date = itemView.findViewById(R.id.date);
            tick_green = itemView.findViewById(R.id.tick_green);
            divider = itemView.findViewById(R.id.divider);





        }
    }
}
