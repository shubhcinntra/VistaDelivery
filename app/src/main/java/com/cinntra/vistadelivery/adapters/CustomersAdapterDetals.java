package com.cinntra.vistadelivery.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Explode;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.AddOpportunityActivity;
import com.cinntra.vistadelivery.activities.MapsActivity;
import com.cinntra.vistadelivery.fragments.PaymentCollection;
import com.cinntra.vistadelivery.fragments.UpdateBusinessPartnerFragment;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CustomersAdapterDetals extends RecyclerView.Adapter<CustomersAdapterDetals.ViewHolder> {
    Context context;
    List<BusinessPartnerAllResponse.Datum> customerList;
//    List<BusinessPartnerData> customerList;

    public CustomersAdapterDetals(Context context, List<BusinessPartnerAllResponse.Datum> customerList) {

        this.context = context;
        this.customerList = customerList;
        this.tempList = new ArrayList<BusinessPartnerAllResponse.Datum>();


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.customers_item, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BusinessPartnerAllResponse.Datum obj = getItem(position);
        holder.customerName.setText(obj.getCardName());
        holder.nameHolder.setText(obj.getCardName()+" : ");
        holder.cardNumber.setText("" + obj.getCardCode());


        holder.date.setVisibility(View.GONE);
        holder.date.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(obj.getCreateDate()));



      /*  if (!obj.getuType().isEmpty())
            holder.amount.setText(obj.getuType().get(0).getType());
        else
            holder.amount.setText("N/A");*/

        if (!obj.getEmailAddress().isEmpty()){
            holder.amount.setText(obj.getEmailAddress());
        }else {
            holder.amount.setText("N/A");
        }

        if (!obj.getPhone1().equalsIgnoreCase(""))
            holder.tvPhoneno.setText(obj.getPhone1());
        else
            holder.tvPhoneno.setText("N/A");

        if (obj.getU_RATING() != null && obj.getU_RATING().matches("\\d*\\.\\d\\d"))
            holder.ratingBar.setRating(Float.valueOf(obj.getU_RATING()));


        holder.threeDotsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.threeDotsLayout, obj.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public BusinessPartnerAllResponse.Datum getItem(int position) {
        return customerList.get(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, cardNumber, date, amount, credit_limit_value, payment_collection, tvPhoneno,nameHolder;
        ImageView gps_icon, map_icon;
        RatingBar ratingBar;
        LinearLayout threeDotsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerName);
            cardNumber = itemView.findViewById(R.id.cardNumber);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            credit_limit_value = itemView.findViewById(R.id.credit_limit_value);
            nameHolder = itemView.findViewById(R.id.credit_limit);
            tvPhoneno = itemView.findViewById(R.id.tvPhoneno);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            payment_collection = itemView.findViewById(R.id.payment_collection);
            gps_icon = itemView.findViewById(R.id.gps_icon);
            map_icon = itemView.findViewById(R.id.map_icon);
            threeDotsLayout = itemView.findViewById(R.id.threeDotsLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  /*Bundle b = new Bundle();
                  b.putSerializable(Globals.BussinessItemData,customerList.get(getAdapterPosition()));
                  // Opportunity_Detail_Fragment fragment = new Opportunity_Detail_Fragment();
                  Update_BussinessPartner_Fragment fragment = new Update_BussinessPartner_Fragment();
                  fragment.setArguments(b);
                  FragmentTransaction transaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                  transaction.replace(R.id.main_edit_qt_frame, fragment);
                  transaction.addToBackStack(null);
                  transaction.commit();*/
                    Bundle b = new Bundle();
                    b.putSerializable(Globals.BussinessItemData, customerList.get(getAdapterPosition()));
                    // Opportunity_Detail_Fragment fragment = new Opportunity_Detail_Fragment();
                    BusinessPartnerDetail fragment = new BusinessPartnerDetail();
                    fragment.setArguments(b);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_edit_qt_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            payment_collection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PaymentCollection fragment = new PaymentCollection();
                    Explode fade = new Explode();
                    fragment.setEnterTransition(fade);
                    fragment.setExitTransition(fade);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_edit_qt_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });


            gps_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putSerializable(Globals.BussinessItemData, customerList.get(getAdapterPosition()));
                    Intent i = new Intent(context, MapsActivity.class);
                    i.putExtras(b);
                    context.startActivity(i);
                }
            });

            map_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }


    //todo Show the popup menu
    private void showPopupMenu(View view, String id, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.editdeletemenu, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.createOppo).setVisible(true);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:

                        Bundle b = new Bundle();
                        b.putSerializable(Globals.BussinessItemData, customerList.get(position));
                        UpdateBusinessPartnerFragment fragment = new UpdateBusinessPartnerFragment(context); //Update_BussinessPartner_Fragment
                        fragment.setArguments(b);
                        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_edit_qt_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        return true;

                    case R.id.createOppo:
                        Intent intent = new Intent(context, AddOpportunityActivity.class);
                        intent.putExtra("BPCardCodeShortCut", customerList.get(position).getCardCode());
                        context.startActivity(intent);

                        return true;

                    default:
                        return false;
                }

            }
        });
        popupMenu.show();
    }

    List<BusinessPartnerAllResponse.Datum> tempList = null;

    public void filter(String charText) {
        customerList.clear();
        tempList.addAll(customerList);
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() == 0) {
            customerList.addAll(tempList);
        } else {
            for (BusinessPartnerAllResponse.Datum st : tempList) {
                if (st.getCardName() != null && !st.getCardName().isEmpty() && st.getCardCode() != null && !st.getCardCode().isEmpty()) {
                    if (st.getCardName().toLowerCase(Locale.getDefault()).contains(charText) || st.getCardCode().toLowerCase(Locale.getDefault()).contains(charText)) {
                        customerList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }

    public void StateFilter(String state) {
        customerList.clear();
        if (state.length() == 0) {
            customerList.addAll(tempList);
        } else {
            for (BusinessPartnerAllResponse.Datum bde : tempList) {
                if (!bde.getBpAddresses().isEmpty()) {
                    if (state.trim().equalsIgnoreCase(bde.getBpAddresses())) {
                        customerList.add(bde);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void Typefilter(String name) {

        customerList.clear();
        if (name.length() == 0) {
            customerList.addAll(tempList);
        } else {
            for (BusinessPartnerAllResponse.Datum bde : tempList) {
                if (bde.getuType().size() > 0) {
                    if (!bde.getuType().isEmpty()) {
                        if (name.trim().equalsIgnoreCase(bde.getuType().get(0).getType())) {
                            customerList.add(bde);
                        }
                    }
                }

            }
        }
        notifyDataSetChanged();

    }

/*    public void AllData() {
        customerList.clear();
        customerList.addAll(tempList);
        notifyDataSetChanged();
    }*/

    public void AllData(List<BusinessPartnerAllResponse.Datum> tmp) {

        tempList.addAll(tmp);
        notifyDataSetChanged();
    }

    public void Customerfilter() {
        customerList.clear();
        for (BusinessPartnerAllResponse.Datum st : tempList) {
            if (st.getCardName() != null && !st.getCardName().isEmpty())
                customerList.add(st);
        }

        Collections.sort(customerList, new Comparator<BusinessPartnerAllResponse.Datum>() {
            @Override
            public int compare(BusinessPartnerAllResponse.Datum o1, BusinessPartnerAllResponse.Datum o2) {

                return o1.getCardName().compareTo(o2.getCardName());
            }
        });
        notifyDataSetChanged();
    }
}
