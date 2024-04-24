package com.cinntra.vistadelivery.adapters.leadAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.model.LeadTypeData;

import java.util.ArrayList;

public class FilterSourceAdapter extends ArrayAdapter<LeadTypeData> {
    Context context;
    int resourceId;
    ArrayList<LeadTypeData> items;

    public FilterSourceAdapter(@NonNull Context context, int resourceId, ArrayList<LeadTypeData> items) {
        super(context, resourceId, items);
        this.items = items;
        this.context = context;
        this.resourceId = resourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Customize the appearance of the selected item in the dropdown (optional)
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            v = inflater.inflate(R.layout.drop_down_textview, null);
        }
        TextView title = v.findViewById(R.id.title);
        title.setText(items.get(position).getName());
        return v;
    }


    @Nullable
    @Override
    public LeadTypeData getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



}

