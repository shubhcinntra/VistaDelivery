package com.cinntra.vistadelivery.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.adapters.Invoice_Item_Adapter;
import com.cinntra.vistadelivery.databinding.ActivitySelectedItemBinding;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.globals.MainBaseActivity;



import static com.cinntra.vistadelivery.activities.CreateContact.ITEMSCODE;


public class InvoiceItemList extends MainBaseActivity implements View.OnClickListener {

//     @BindView(R.id.head_title)
//    TextView head_title;
//     @BindView(R.id.back_press)
//     RelativeLayout back_press;
//     @BindView(R.id.new_quatos)
//     RelativeLayout new_quatos;
//     @BindView(R.id.itemsRecycler)
//     RecyclerView itemsRecycler;
//     @BindView(R.id.done)
//     Button done;
//     @BindView(R.id.loader)
//     ProgressBar loader;


    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    ActivitySelectedItemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectedItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //ButterKnife.bind(this);
        setDefaults();


    }

    private void setDefaults() {
        String fromwhere = getIntent().getStringExtra("FromWhere");
        if (fromwhere.equalsIgnoreCase("invoices")) {
            binding.quotesHeader.newQuatos.setVisibility(View.GONE);
            binding.quotesHeader.newQuatos.setClickable(false);
        }

        binding.quotesHeader.headTitle.setText(getResources().getString(R.string.selected_items));
        binding.quotesHeader.backPress.setOnClickListener(this);
        binding.quotesHeader.newQuatos.setOnClickListener(this);
        binding.done.setOnClickListener(this);

        setAdapter();
    }

    Invoice_Item_Adapter adapter;

    private void setAdapter() {
        adapter = new Invoice_Item_Adapter(InvoiceItemList.this, Globals.SelectedItems);
        binding.itemsRecycler.setLayoutManager(new LinearLayoutManager(com.cinntra.vistadelivery.activities.InvoiceItemList.this, RecyclerView.VERTICAL, false));
        binding.itemsRecycler.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_press:
                Intent ii = new Intent();
                setResult(RESULT_OK, ii);
                finish();
                break;
            case R.id.new_quatos:
                Intent intent = new Intent(com.cinntra.vistadelivery.activities.InvoiceItemList.this, ItemsList.class);
                startActivityForResult(intent, ITEMSCODE);
                break;
            case R.id.done:
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setAdapter();

        }
    }


}