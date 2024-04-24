package com.cinntra.vistadelivery.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.databinding.ActivityAddTaskDialogueLayoutBinding;
import com.cinntra.vistadelivery.databinding.FragmentAddTaskBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.CreateCalenderActivityRequest;
import com.cinntra.vistadelivery.model.EventResponse;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.newapimodel.NewOpportunityRespose;
import com.cinntra.vistadelivery.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityAddTaskDialogue extends DialogFragment implements View.OnClickListener {

    String priority = "";
    String allday = "";
    String repeated = "";
    String progresstatus = "";
    int t1hr, t1min;
    EventPrerioritySpinner eventPrerioritySpinner;
    EventTextSpinner eventTextSpinner;
    TaskProgressSpinner taskProgressSpinner;
    NewOpportunityRespose opportunityItem;
    ArrayList<String> categories = new ArrayList<>();
    ArrayList<Integer> circleimage = new ArrayList<>();
    ArrayList<String> progress_status = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    private AlarmManager alarmManager;
    private Calendar myTime;

    public ActivityAddTaskDialogue() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ActivityAddTaskDialogue newInstance(String title) {
        ActivityAddTaskDialogue frag = new ActivityAddTaskDialogue();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    ActivityAddTaskDialogueLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityAddTaskDialogueLayoutBinding.inflate(inflater, container, false);
        View v = inflater.inflate(R.layout.fragment_add_task, container);
        //  ButterKnife.bind(this,v);
        setDefaults();
//     loadData();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            opportunityItem = (NewOpportunityRespose) b.getParcelable(Globals.OpportunityItem);
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.loaderLayout.loader.setVisibility(View.GONE);
        binding.headerLayout.headTitle.setText("New Task");

        binding.headerLayout.backPress.setOnClickListener(this);
        binding.submitButton.setOnClickListener(this);
        binding.simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    repeated = "";
                    binding.spinnerview.setVisibility(View.INVISIBLE);
                } else {
                    binding.spinnerview.setVisibility(View.VISIBLE);
//                    repeated = "Repeated";
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_press) {
            getDialog().dismiss();
        } else if (v.getId() == R.id.submit_button) {

            String title = binding.titleText.getText().toString().trim();
            String date = binding.fromValue.getText().toString().trim();
            String location = binding.addLocationText.getText().toString().trim();
            String host = binding.hostText.getText().toString().trim();
            String desc = binding.descriptionText.getText().toString().trim();
            String time = binding.timeValue.getText().toString().trim();


            if (validation(title, date, location, host, time)) {
                CreateCalenderActivityRequest activityRequest = new CreateCalenderActivityRequest();


                activityRequest.setSourceID(opportunityItem.getId());
                activityRequest.setTitle(title);
                activityRequest.setDescription(desc);
                activityRequest.setFrom(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(date));
                activityRequest.setTo(Globals.convert_dd_MM_yyyy_to_yyyy_MM_dd(date));
                activityRequest.setEmp(Prefs.getString(Globals.EmployeeID, ""));
                activityRequest.setCreateTime(Globals.getTCurrentTime());
                activityRequest.setCreateDate(Globals.getTodaysDatervrsfrmt());
                activityRequest.setType("Task");
                activityRequest.setSourceType("Opportunity");
                activityRequest.setParticipants("");
                activityRequest.setComment("");
                activityRequest.setSubject("");
                activityRequest.setTime(time);
                activityRequest.setDocument("");
                activityRequest.setRelatedTo("hi");
                activityRequest.setLocation(location);
                activityRequest.setHost(host);
                activityRequest.setAllday("false");
                activityRequest.setName(opportunityItem.getCustomerName());
                activityRequest.setProgressStatus("WIP");
                activityRequest.setPriority("low");
                activityRequest.setRepeated(repeated);
                activityRequest.setLeadType("");


                if (Globals.checkInternet(getContext())) {
                    binding.loaderLayout.loader.setVisibility(View.VISIBLE);
                    callApi(activityRequest);
                }

            }
        }
    }

    private void callApi(CreateCalenderActivityRequest activityRequest) {

        Call<EventResponse> call = NewApiClient.getInstance().getApiService(requireContext()).createnewevent(activityRequest);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getStatus() == 200) {
                    binding.loaderLayout.loader.setVisibility(View.GONE);

                    Toasty.success(getContext(), "Add Successfully", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();

                } else {
                    binding.loaderLayout.loader.setVisibility(View.GONE);
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(getActivity(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                binding.loaderLayout.loader.setVisibility(View.GONE);
                Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validation(String title, String fromDate, String location,
                               String host, String time) {
        if (title.isEmpty()) {
            binding.titleText.setError(getResources().getString(R.string.title_error));
            return false;
        } else if (fromDate.isEmpty()) {
            binding.fromValue.setError(getResources().getString(R.string.fromdate_error));
            return false;
        }else if (location.isEmpty()) {
            binding.addLocationText.setError(getResources().getString(R.string.location_error));
            return false;
        } else if (host.isEmpty()) {
            binding.hostText.setError(getResources().getString(R.string.host_error));
            return false;
        } else if (time.isEmpty()) {
            binding.timeValue.setError(getResources().getString(R.string.time_error));
            return false;
        }
        return true;
    }

    private void setDefaults() {
        binding.headerLayout.add.setVisibility(View.GONE);
        circleimage.add(R.drawable.red_dot);
        circleimage.add(R.drawable.ic_green_dot);
        circleimage.add(R.drawable.yellow_dot);

        categories.add("Repeat");
        categories.add("Once");
        categories.add("Daily");
        categories.add("Weekly");
        categories.add("Monthly");

        progress_status.add("In Progress");
        progress_status.add("Completed");


        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            binding.fromValue.setText(sdf.format(myCalendar.getTime()));
        };


      /*  eventPrerioritySpinner = new EventPrerioritySpinner(getActivity(), circleimage);
        colorspin.setAdapter(eventPrerioritySpinner);
        colorspin.setDropDownVerticalOffset(120);
        colorspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    priority = "high";
                } else if (position == 1) {
                    priority = "medium";
                } else {
                    priority = "low";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priority = "high";
            }
        });*/


        eventTextSpinner = new EventTextSpinner(getActivity(), categories);
        binding.spinner.setAdapter(eventTextSpinner);
        binding.spinner.setDropDownVerticalOffset(60);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repeated = binding.spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                repeated = categories.get(0);
            }
        });

        taskProgressSpinner = new TaskProgressSpinner(getActivity(), progress_status);
        binding.progressSpinner.setAdapter(taskProgressSpinner);
        binding.progressSpinner.setDropDownVerticalOffset(60);
        binding.progressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progresstatus = binding.spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                progresstatus = progress_status.get(0);
            }
        });


        binding.fromValue.setOnClickListener(v -> {
            Globals.enableAllCalenderDateSelect(getContext(), binding.fromValue);

        });


        binding.timeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        t1hr = hourOfDay;
                        t1min = minute;
                        myTime = Calendar.getInstance();
//                        myTime.set(0,0,0,t1hr,t1min);
                        myTime.set(Calendar.HOUR_OF_DAY, t1hr);
                        myTime.set(Calendar.MINUTE, t1min);
                        myTime.set(Calendar.SECOND, 0);
                        myTime.set(Calendar.MILLISECOND, 0);
                        binding.timeValue.setText(DateFormat.format("hh:mm aa", myTime));
                        //setAlarm();
                    }
                }, 12, 0, false
                );
                timePickerDialog.updateTime(t1hr, t1min);
                timePickerDialog.show();

            }

        });


    }

}
