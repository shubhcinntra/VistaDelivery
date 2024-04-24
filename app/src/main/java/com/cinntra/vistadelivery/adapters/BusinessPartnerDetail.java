package com.cinntra.vistadelivery.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.activities.Opportunities_Pipeline_Activity;
import com.cinntra.vistadelivery.activities.OrderActivity;
import com.cinntra.vistadelivery.activities.PaymentDetails;

import com.cinntra.vistadelivery.activities.QuotationActivity;
import com.cinntra.vistadelivery.databinding.CustomersdetailBinding;
import com.cinntra.vistadelivery.fragments.BPAddressDetails;
import com.cinntra.vistadelivery.fragments.BPDetailFragment;

import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.interfaces.DatabaseClick;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;


public class BusinessPartnerDetail extends Fragment implements View.OnClickListener, DatabaseClick {

    //    BusinessPartnerData customerItem;
    BusinessPartnerAllResponse.Datum customerItem;

    CustomersdetailBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            Bundle b = getArguments();
            customerItem = (BusinessPartnerAllResponse.Datum) b.getSerializable(Globals.BussinessItemData);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CustomersdetailBinding.inflate(inflater, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setDefaults();
        return binding.getRoot();
    }


    private String[] tabs = {"General", "Contact"};
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private void setDefaults() {
        Prefs.putString(Globals.SelectAddress, "NoUpdate");
//        binding.headerBottomRounded.headTitle.setText(getResources().getString(R.string.customer));
        binding.headerLayout.headTitle.setText(customerItem.getCardName());
        /*fragments.add(new Update_BussinessPartner_Fragment(BusinessPartnerDetail.this, customerItem));
        fragments.add(new BusinessPartnerContact(customerItem));
        fragments.add(new BusinessPartnerBranch(BusinessPartnerDetail.this, customerItem));*/

        fragments.add(new BPDetailFragment(BusinessPartnerDetail.this, customerItem));
        fragments.add(new BPAddressDetails(BusinessPartnerDetail.this, customerItem));

        BusinessPagerAdapter pagerAdapter = new BusinessPagerAdapter(getChildFragmentManager(), fragments, tabs);
        binding.viewpager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);
        binding.headerLayout.backPress.setOnClickListener(this);
//        binding.option.setOnClickListener(this);


        binding.companyName.setText(customerItem.getCardName());
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int randomColor = generator.getRandomColor();
        if (!customerItem.getCardName().isEmpty()) {
            TextDrawable drawable = TextDrawable.builder().beginConfig().withBorder(4).endConfig()
                    .buildRound(String.valueOf(customerItem.getCardName().charAt(0)), randomColor);

            binding.nameIcon.setImageDrawable(drawable);
        }

        binding.headerLayout.backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        binding.opprotunityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Opportunities_Pipeline_Activity.class);// todo comment by me bcz doing it later --- Opportunities_Pipeline_Activity
                intent.putExtra("BPCardCodeShortCut", customerItem.getCardCode());
                startActivity(intent);

            }
        });


        binding.proformaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getActivity(), AddQuotationAct.class); //AddQuotationAct
                Intent intent = new Intent(getActivity(), QuotationActivity.class); //AddQuotationAct
                intent.putExtra("BPCardCodeShortCut", customerItem.getCardCode());
                startActivity(intent);

//                startActivity(new Intent(getActivity(), QuotationActivity.class));
            }
        });


        binding.onOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("BPCardCodeShortCut", customerItem.getCardCode());
                startActivity(intent);
//                startActivity(new Intent(getActivity(), OrderActivity.class));
            }
        });

   /*     binding.onOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddOrderAct.class);
                intent.putExtra("BPCardCodeShortCut", customerItem.getCardCode());
            }
        });*/


        binding.invoiceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PaymentDetails.class));
            }
        });


    }


    @Override
    public void onDetach() {
        super.onDetach();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_press:
                Globals.branchData.clear();
                getActivity().onBackPressed();
                break;
            case R.id.option:
//                openPopup();
                break;
        }
    }

 /*   private void openPopup() {
        PopupMenu popup = new PopupMenu(getContext());
        popup.getMenuInflater().inflate(R.menu.businesspartnermenu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.opportunity:
                    startActivity(new Intent(getActivity(), Opportunities_Pipeline_Activity.class));
                    break;
                case R.id.quotation:
                    startActivity(new Intent(getActivity(), QuotationActivity.class));
                    break;
                case R.id.order:
                    startActivity(new Intent(getActivity(), OrderActivity.class));
                    break;
                case R.id.invoice:
                    startActivity(new Intent(getActivity(), InvoiceActivity.class));
                    break;

            }
            return true;
        });
        popup.show();


    }*/

    @Override
    public void onClick(int po) {
        binding.viewpager.setCurrentItem(po);
    }
}
