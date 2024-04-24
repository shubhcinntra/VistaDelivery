package com.cinntra.vistadelivery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.model.ResponseIndustrySaas;

import java.util.List;


public class IndustrySpinnerSaasAdapter extends BaseAdapter {
    Context context;
    List<ResponseIndustrySaas.Datum> stagesList;
    LayoutInflater inflter;

    public IndustrySpinnerSaasAdapter(Context context, List<ResponseIndustrySaas.Datum> stagesList) {
        this.context = context;
        this.stagesList = stagesList;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return stagesList.size();
    }

    @Override
    public ResponseIndustrySaas.Datum getItem(int position) {
        return stagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflter.inflate(R.layout.stages_spinner_item, null);
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(stagesList.get(position).getIndustry_name());
        return v;
    }
}
