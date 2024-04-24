package com.cinntra.vistadelivery.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.WebViewToPdf;
import com.cinntra.vistadelivery.fragments.OrderDetailFragment;
import com.cinntra.vistadelivery.fragments.Order_Update_Fragment;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.model.orderModels.OrderListModel;
import com.pixplicity.easyprefs.library.Prefs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


public class Open_Order_Adapter extends RecyclerView.Adapter<Open_Order_Adapter.ViewHolder> {
    Context context;

    List<OrderListModel.Data> itemsList;

    public Open_Order_Adapter(Context context, List<OrderListModel.Data> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
        this.tempList = new ArrayList<OrderListModel.Data>();
        this.tempList.addAll(itemsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.order_new_screen, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderListModel.Data obj = getItem(position);
        holder.title.setText(obj.getCardName());

        holder.order_docNum.setText(obj.getId());
        if (obj.getTaxDate().isEmpty()){
            holder.tvPostingDate.setText("");
        }else {
            holder.tvPostingDate.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(obj.getTaxDate()));
        }

        if (obj.getDocDate().isEmpty()){
            holder.tvDocDate.setText("");
        }else {
            holder.tvDocDate.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(obj.getDocDate()));
        }

        if (obj.getDocDueDate().isEmpty()){
            holder.doc_date.setText("");
        }else {
            holder.doc_date.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(obj.getDocDueDate()));
        }

        holder.tvTotalAmount.setText(obj.getNetTotal());
        //    holder.amount.setText(Globals.getAmmount(obj.getDocCurrency(),obj.getDocTotal()));
        //   holder.status.setText(Globals.viewStatus(obj.getDocumentStatus()));

        if (Globals.viewStatus(obj.getDocumentStatus()) == "Open") {
            holder.status.setText("Open");
            holder.status.setBackgroundResource(R.drawable.openroundedgreen);
        } else {
            holder.status.setText("Closed");
            holder.status.setBackgroundResource(R.drawable.saffron_rounded);
        }

        holder.preview_file.setVisibility(View.VISIBLE);

        holder.preview_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WebViewToPdf.class);
                i.putExtra("PDfFrom", "Order");
                i.putExtra("PdfID", obj.getId());
                context.startActivity(i);
            }
        });


        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.option);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.edit_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editWHole:
                           /*     Prefs.putString(Globals.FromQuotation, "Quotation");
                                Globals.quotationOrder.clear();
                                Intent intent = new Intent(context, AddOrderAct.class);
                               // Globals.quotationOrder.add(itemsList.get(position));
                                intent.putExtra("QuotationObject", itemsList.get(position));

                                context.startActivity(intent);*/

                                if (Prefs.getString(Globals.SelectQuotation, "").equalsIgnoreCase("Dashboard")) {
                                    Bundle b = new Bundle();
                                    b.putSerializable(Globals.QuotationItem, itemsList.get(position));
                                    b.putString("Flag", "Update");
                                    b.putString("id",itemsList.get(position).getId());

                                    Order_Update_Fragment fragment = new Order_Update_Fragment();
                                    fragment.setArguments(b);
                                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.main_container, fragment);
                                    transaction.addToBackStack("Test");
                                    transaction.commit();
                                }

                                break;



                        }
                        return true;

                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public OrderListModel.Data getItem(int po) {
        return itemsList.get(po);
    }


    public void Customernamefilter(String name) {
        name = name.toLowerCase(Locale.getDefault()).trim();
        itemsList.clear();
        if (name.length() == 0) {
            itemsList.addAll(tempList);
        } else {
            for (OrderListModel.Data st : tempList) {

                if (st.getCardName() != null && !st.getCardName().isEmpty()) {
                    if (st.getCardName().toLowerCase().trim().equalsIgnoreCase(name)) {
                        itemsList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, doc_date, amount, status, order_docNum, tvPostingDate, tvDocDate, tvTotalAmount;
        LinearLayout option;
        ImageView preview_file;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            doc_date = itemView.findViewById(R.id.doc_date);
            amount = itemView.findViewById(R.id.amount);
            status = itemView.findViewById(R.id.status);
            tvPostingDate = itemView.findViewById(R.id.tvPostingDate);
            tvDocDate = itemView.findViewById(R.id.tvDocDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            preview_file = itemView.findViewById(R.id.preview_file);
            order_docNum = itemView.findViewById(R.id.order_docNum);
            option = itemView.findViewById(R.id.option);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   /* Bundle b = new Bundle();
                    b.putSerializable(Globals.QuotationItem, itemsList.get(getAdapterPosition()));
                    //Quotation_Update_Fragment fragment = new Quotation_Update_Fragment();
                    Order_Update_Fragment fragment = new Order_Update_Fragment();
                    fragment.setArguments(b);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_container, fragment);
                    transaction.addToBackStack("Test");
                    transaction.commit();*/

                    Bundle b = new Bundle();
                    b.putSerializable(Globals.QuotationItem, itemsList.get(getAdapterPosition()));
                    b.putString("Flag", "Detail");
                    OrderDetailFragment fragment = new OrderDetailFragment();
                    fragment.setArguments(b);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_container, fragment);
                    transaction.addToBackStack("Test");
                    transaction.commit();

                    Toasty.warning(context, "Work in Progress!");

                }
            });
        }
    }


    List<OrderListModel.Data> tempList = null;

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemsList.clear();
        if (charText.length() == 0) {
            itemsList.addAll(tempList);
        } else {
            for (OrderListModel.Data st : tempList) {
                if (st.getCardName() != null && !st.getCardName().isEmpty() || st.getU_MR_NO() != null && !st.getU_MR_NO().isEmpty()) {
                    if (st.getCardName().toLowerCase(Locale.getDefault()).contains(charText) || st.getU_MR_NO().toLowerCase(Locale.getDefault()).contains(charText)) {
                        itemsList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ValidDate() {
        itemsList.clear();
        itemsList.addAll(tempList);
        Collections.sort(itemsList, new Comparator<OrderListModel.Data>() {
            @Override
            public int compare(OrderListModel.Data o1, OrderListModel.Data o2) {
                return o1.getDocDueDate().compareTo(o2.getDocDueDate());
            }
        });
        notifyDataSetChanged();


        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void PostingDate(LocalDate afterdate, LocalDate dateObj) {
        itemsList.clear();
        for (OrderListModel.Data st : tempList) {

            if (st.getCreationDate() != null && !st.getCreationDate().isEmpty()) {
                String sDate1 = st.getCreationDate();
                LocalDate date1 = LocalDate.parse(sDate1);
                if ((date1.isBefore(afterdate) || date1.isEqual(afterdate)) && date1.isAfter(dateObj)) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show();
                    itemsList.add(st);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void Customerfilter() {
        itemsList.clear();
        itemsList.addAll(tempList);
        Collections.sort(itemsList, new Comparator<OrderListModel.Data>() {
            @Override
            public int compare(OrderListModel.Data o1, OrderListModel.Data o2) {
                return o1.getCardName().compareTo(o2.getCardName());
            }
        });
        notifyDataSetChanged();

    }

    public void allData() {
        itemsList.clear();
        itemsList.addAll(tempList);
        notifyDataSetChanged();
    }

    public void AllData(List<OrderListModel.Data> tmp) {
        tempList.clear();
        tempList.addAll(tmp);
        notifyDataSetChanged();
    }

    public void CustomerFilter(ArrayList<String> stringArrayList) {
        itemsList.clear();
        if (stringArrayList.size() == 0) {
            itemsList.addAll(tempList);

        } else {
            for (OrderListModel.Data lv : tempList) {
                if (stringArrayList.contains(lv.getCardName())) {
                    itemsList.add(lv);
                }
            }
        }
        notifyDataSetChanged();
    }

}