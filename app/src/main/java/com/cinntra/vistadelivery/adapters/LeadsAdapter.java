package com.cinntra.vistadelivery.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.bpActivity.AddBPCustomer;
import com.cinntra.vistadelivery.activities.LeadsActivity;
import com.cinntra.vistadelivery.fragments.CreateBPSelectionSheet;
import com.cinntra.vistadelivery.fragments.LeadBottomsheetFragment;
import com.cinntra.vistadelivery.fragments.LeadDetail;
import com.cinntra.vistadelivery.fragments.LeadInformation;
import com.cinntra.vistadelivery.fragments.ReminderSelectionSheet;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.ChatModel;
import com.cinntra.vistadelivery.model.ChatResponse;
import com.cinntra.vistadelivery.model.FollowUpData;
import com.cinntra.vistadelivery.model.FollowUpResponse;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.newapimodel.LeadValue;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class LeadsAdapter extends RecyclerView.Adapter<LeadsAdapter.ViewHolder> {
    Context context;
    List<LeadValue> leadValueList;
    List<LeadValue> tempList;

    public LeadsAdapter(Context c, List<LeadValue> leadValueList) {
        this.context = c;
        this.leadValueList = leadValueList;
        this.tempList = new ArrayList<>();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.leads_adapter_screen, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeadValue lv = leadValueList.get(position);
        holder.customerName.setText(lv.getCompanyName());
        holder.date.setText(lv.getDate());
        holder.cardNumber.setText(lv.getStatus());
        holder.assigned_view.setVisibility(View.VISIBLE);
        holder.assigned.setText(lv.getAssignedTo().getSalesEmployeeName());
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv.getJunk() != 1) {
                    showCreateBpbottomsheet(lv);
                } else {
                    Toasty.warning(context, "This lead is junk lead", Toast.LENGTH_SHORT).show();
                }
                //openOptionpopup(holder.option,lv);
            }
        });
        holder.follow_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderDialog(lv.getId(), lv.getCompanyName());

              //  showBottomSheet(lv.getId(), lv.getCompanyName());
//                openFollowUpdialog(lv.getId());
            }
        });
        // holder.amount.setText("Rs:" + lv.getTurnover());
        if (lv.getLeadType().equalsIgnoreCase("")) {
            holder.color_type.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        }
        else if (lv.getLeadType().equalsIgnoreCase("Hot")) {
            holder.color_type.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
        } else if (lv.getLeadType().equalsIgnoreCase("Warm")) {
            holder.color_type.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.yellow)));
        } else if (lv.getLeadType().equalsIgnoreCase("Cold")){
            holder.color_type.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.projected_title)));
        }
    }


    private void showBottomSheet(int id, String name) {

        ReminderSelectionSheet bottomSheetFragment = new ReminderSelectionSheet(id, name);
        bottomSheetFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());

    }

    private void showCreateBpbottomsheet(LeadValue id) {
        CreateBPSelectionSheet bottomSheetFragment = new CreateBPSelectionSheet(id);
        bottomSheetFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    private void callcreateApi(ChatModel chatModel) {

        Call<ChatResponse> call = NewApiClient.getInstance().getApiService(context).createChat(chatModel);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {

                if (response.body() != null) {
                    Toast.makeText(context, "Posted Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {

            }
        });
//        callContactApi(opportunityItem.getCardCode());

    }

    private void callfollowupApi(FollowUpData followUpData) {

        Call<FollowUpResponse> call = NewApiClient.getInstance().getApiService(context).createfollowUP(followUpData);
        call.enqueue(new Callback<FollowUpResponse>() {
            @Override
            public void onResponse(Call<FollowUpResponse> call, Response<FollowUpResponse> response) {

                if (response.code() == 200) {
                    Toast.makeText(context, "Created Successfully", Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<FollowUpResponse> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean validation(String date, String time, String comment) {
        if (date.length() == 0) {
            Toast.makeText(context, "Enter date", Toast.LENGTH_SHORT).show();
            return false;
        } else if (time.length() == 0) {
            Toast.makeText(context, "Enter time", Toast.LENGTH_SHORT).show();

            return false;

        } else if (comment.length() == 0) {
            Toast.makeText(context, "Enter comment", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private void openOptionpopup(LinearLayout option, LeadValue lv) {

        PopupMenu popup = new PopupMenu(context, option);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.createbpoption, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.createbp:
                        Prefs.putString(Globals.AddBp, "Lead");
                        Intent intent = new Intent(context, AddBPCustomer.class);
                        intent.putExtra(Globals.AddBp, lv);
                        context.startActivity(intent);

                        break;
                    case R.id.chat:

                       /* Bundle bundle = new Bundle();
                        bundle.putParcelable(Globals.Lead_Data,lv);
                        LeadFollowUpFragment chatterFragment = new LeadFollowUpFragment();
                        chatterFragment.setArguments(bundle);
                        FragmentTransaction chattransaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                        chattransaction.add(R.id.customer_lead, chatterFragment).addToBackStack(null);
                        chattransaction.commit();*/
                        Bundle b = new Bundle();
                        b.putParcelable(Globals.LeadDetails, lv);
                        b.putString("From", "Lead");
                        LeadDetail fragment = new LeadDetail(context);
                        fragment.setArguments(b);
                        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.customer_lead, fragment).addToBackStack(null);
                        transaction.commit();
                        break;


                }
                return true;

            }
        });
        popup.show();
    }


    @Override
    public int getItemCount() {
        return leadValueList.size();
    }


    public void AllData(List<LeadValue> tmp) {
        tempList.clear();
        tempList.addAll(tmp);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        tempList.clear();
        tempList.addAll(leadValueList);
        leadValueList.clear();
        if (charText.length() == 0) {
            leadValueList.addAll(tempList);
        } else {
            for (LeadValue st : tempList) {
                if (st.getCompanyName() != null && !st.getCompanyName().isEmpty()) {
                    if (st.getCompanyName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        leadValueList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }

    public void leadfilter(String text) {

        text = text.toLowerCase(Locale.getDefault());
        leadValueList.clear();
        if (text.length() == 0) {
            leadValueList.addAll(tempList);
        } else {
            for (LeadValue st : tempList) {
                if (st.getJunk() != null && st.getJunk() != 1) {
                    if (st.getStatus() != null && !st.getStatus().isEmpty()) {
                        if (st.getStatus().toLowerCase(Locale.getDefault()).contains(text)) {
                            leadValueList.add(st);
                        }
                    }
                }
            }

        }
        notifyDataSetChanged();
    }


    public void updateList(List<LeadValue> newList, String startDate, String endDate) {
        leadValueList = newList;
        filterListByDateRange(startDate, endDate);
        notifyDataSetChanged();
    }

    public void filterListByDateRange(String startDate, String endDate) {
        tempList.clear();
        for (LeadValue item : leadValueList) {
            if (item.getDate().compareTo(startDate) >= 0 && item.getDate().compareTo(endDate) <= 0) {
                tempList.add(item);
            }
        }
    }


    public void priorityfilter(String text) {
        text = text.toLowerCase(Locale.getDefault());
        leadValueList.clear();
        if (text.length() == 0) {
            leadValueList.addAll(tempList);
        } else {
            for (LeadValue st : tempList) {
//                if (st.getJunk() != null && st.getJunk() != 1) {
                if (st.getLeadType() != null && !st.getLeadType().isEmpty()) {
                    if (st.getLeadType().toLowerCase(Locale.getDefault()).contains(text)) {
                        leadValueList.add(st);
                    }
                }
//                }
            }

        }
        notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void datefilter(String ele, String enddate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startdate = LocalDate.parse(ele, formatter);
        LocalDate enDdate = LocalDate.parse(enddate, formatter);
        tempList.clear();
        tempList.addAll(leadValueList);
        leadValueList.clear();
        if (ele.length() == 0) {
            // leadValueList.addAll(tempList);
        } else {
            for (LeadValue st : tempList) {
                if (st.getJunk() != null && st.getJunk() != 1) {
                    if (st.getDate() != null && !st.getDate().isEmpty()) {
                        String sDate1 = st.getDate();
                        LocalDate date1 = null;
                        try {
                            date1 = LocalDate.parse(sDate1, formatter);
                            if ((date1.isBefore(enDdate) && date1.isAfter(startdate)) || st.getDate().equalsIgnoreCase(ele) || st.getDate().equalsIgnoreCase(enddate)) {
                                leadValueList.add(st);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
            }

        }
        notifyDataSetChanged();
    }

    public void sourcefilter(ArrayList<String> sourcechecklist) {
        tempList.clear();
        tempList.addAll(leadValueList);
        leadValueList.clear();
        if (sourcechecklist.size() == 0) {
            leadValueList.addAll(tempList);

        } else {
            for (LeadValue lv : tempList) {
                if (lv.getJunk() != null && lv.getJunk() != 1) {
                    if (sourcechecklist.contains(lv.getSource())) {
                        leadValueList.add(lv);
                    }
                }
            }
        }
        notifyDataSetChanged();

    }

    public void junkfilter() {
        leadValueList.clear();
        for (LeadValue st : tempList) {
            if (st.getJunk() != null && st.getJunk() == 1) {

                leadValueList.add(st);

            }
        }
        notifyDataSetChanged();
    }


    private void reminderDialog(Integer id, String companyName) {
        final String[] comm_Mode = {""};
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        //dialog.setTitle("FollowUp Reminder");
        dialog.setContentView(R.layout.followup_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        EditText date_value = dialog.findViewById(R.id.date_value);
        EditText time_value = dialog.findViewById(R.id.time_value);
        EditText comment_value = dialog.findViewById(R.id.comment_value);
        Button add = dialog.findViewById(R.id.add);
        Spinner communication_spinner = dialog.findViewById(R.id.communication_spinner);
        TextView communication_txt = dialog.findViewById(R.id.communication_txt);
        communication_spinner.setVisibility(View.VISIBLE);
        communication_txt.setVisibility(View.VISIBLE);
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Follow Up");


        communication_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                comm_Mode[0] = communication_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                comm_Mode[0] = communication_spinner.getSelectedItem().toString();
            }
        });

        date_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.disablePastSelectDate(dialog.getContext(), date_value);

            }
        });


        time_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.selectTime(context, time_value);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(date_value.getText().toString().trim());
                String time = time_value.getText().toString().trim();
                String comment = comment_value.getText().toString().trim();
                if (validation(date, time, comment)) {
                    FollowUpData followUpData = new FollowUpData();
                    followUpData.setSubject(companyName);
                    followUpData.setSourceID(id.toString());
                    followUpData.setSourceType("Lead");
                    followUpData.setComment(comment);
                    followUpData.setFrom(date);
                    followUpData.setTime(time);
                    followUpData.setComm_mode(comm_Mode[0]);
                    followUpData.setType("Followup");
                    followUpData.setCreateDate(Globals.getTodaysDatervrsfrmt());
                    followUpData.setCreateTime(Globals.getTCurrentTime());
                    followUpData.setLeadType("");
                    followUpData.setEmp(Integer.parseInt(Prefs.getString(Globals.EmployeeID, "")));
                    followUpData.setEmpName(Prefs.getString(Globals.Employee_Name, ""));

                    callfollowupApi(followUpData);
                    dialog.cancel();
                }


            }
        });


        dialog.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, cardNumber, date, assigned, follow_up;
        LinearLayout assigned_view;
        LinearLayout option;
        ImageView color_type, person;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.item_title);
            cardNumber = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.item_date);
            assigned = itemView.findViewById(R.id.assigned);
            assigned_view = itemView.findViewById(R.id.assigned_view);
            option = itemView.findViewById(R.id.option);
            follow_up = itemView.findViewById(R.id.follow_up);
            color_type = itemView.findViewById(R.id.color_type);
            person = itemView.findViewById(R.id.person);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Prefs.getString(Globals.BussinessPageType, "").equalsIgnoreCase("DashBoard")) {
                       /* Bundle b = new Bundle();
                        b.putParcelable(Globals.LeadDetails, leadValueList.get(getAdapterPosition()));
                        b.putString("From","Lead");
                        LeadDetail fragment = new LeadDetail(context);
                        fragment.setArguments(b);
                        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.customer_lead, fragment).addToBackStack(null);
                        transaction.commit();*/
                        Bundle b = new Bundle();
                        b.putParcelable(Globals.LeadDetails, leadValueList.get(getAdapterPosition()));
                        b.putString("From", "Lead");
                        LeadInformation fragment = new LeadInformation(context);
                        fragment.setArguments(b);
                        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.customer_lead, fragment).addToBackStack(null);
                        transaction.commit();
                    } else {
                        if (leadValueList.get(getAdapterPosition()).getJunk() != 1) {
                            Intent intent = new Intent();
                            intent.putExtra(Globals.Lead_Data, leadValueList.get(getAdapterPosition()));
                            ((AppCompatActivity) context).setResult(RESULT_OK, intent);
                            ((AppCompatActivity) context).finish();
                        } else {
                            Toasty.warning(context, "This is junk lead", Toasty.LENGTH_LONG).show();
                        }
                    }
                }
            });

            person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putParcelable(Globals.LeadDetails, leadValueList.get(getAdapterPosition()));
                    LeadBottomsheetFragment bottomSheetFragment = new LeadBottomsheetFragment(context);
                    bottomSheetFragment.setArguments(b);
                    bottomSheetFragment.show(((LeadsActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
            });

        }
    }
}