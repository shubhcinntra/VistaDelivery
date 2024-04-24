package com.cinntra.vistadelivery.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.adapters.ActivityTasksAdapter;
import com.cinntra.vistadelivery.databinding.FragmentTasksBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.EventResponse;
import com.cinntra.vistadelivery.model.EventValue;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivityTasks_Fragment extends Fragment {

    ArrayList<EventValue> alltasklist ;
    ActivityTasksAdapter adapter;
    String id;
    public ActivityTasks_Fragment(String id) {
        this.id = id;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    FragmentTasksBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentTasksBinding.inflate(inflater,container,false);
        View v=inflater.inflate(R.layout.fragment_tasks, container, false);
      //  ButterKnife.bind(this,v);

    //    loadData();
        callApi(id);
       return binding.getRoot() ;
    }
    private void callApi(String id) {
        alltasklist = new ArrayList<>();

        EventValue eventValue = new EventValue();
        eventValue.setEmp(Integer.valueOf(Prefs.getString(Globals.EmployeeID,"")));
//        eventValue.setEmp(Integer.valueOf((Globals.TeamEmployeeID)));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Emp", Prefs.getString(Globals.EmployeeID, ""));
        jsonObject.addProperty("date", Globals.CurrentSelectedDate);

//        Call<EventResponse> call = NewApiClient.getInstance().getApiService(this).getallevent(eventValue);
        Call<EventResponse> call = NewApiClient.getInstance().getApiService(requireContext()).getCalendarData(jsonObject);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {

                if(response.code()==200||response.body()!=null)
                {
                    if(response.body().getData().size()>0){
                        alltasklist.clear();
                        alltasklist.addAll(response.body().getData());
                        setAdapter();
                    }
                    else
                        Toast.makeText(getContext(),"No data found",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s =response.errorBody().string();
                        mError= gson.fromJson(s,QuotationResponse.class);
                        Toast.makeText(getActivity(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }
    public ArrayList<EventValue> filter(String text ) {

        ArrayList<EventValue> templist= new ArrayList<>();
        templist.clear();
        for (EventValue st : alltasklist) {
            if(st.getOppId().toString().equalsIgnoreCase(id)) {
                if (st.getType().equals(text)) {

                    templist.add(st);

                }
            }


        }

        return templist;
    }
    private void setAdapter() {
       binding. taskList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        adapter = new ActivityTasksAdapter(getActivity(), filter("Task"));

        binding. taskList.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        if(adapter.getItemCount()==0){
            binding.noDatafound.setVisibility(View.VISIBLE);
        }else{
            binding.noDatafound.setVisibility(View.GONE);
        }

    }
    private ArrayList<EventValue> geTasks(ArrayList<EventValue> list)
        {
        ArrayList<EventValue> events = new ArrayList<>();
        for (EventValue event :list
        ) {
            if(event.getType().equals("Task") &&Globals.CurrentSelectedDate.equalsIgnoreCase(event.getTo()))
                events.add(event);
        }

        return events;
    }












}