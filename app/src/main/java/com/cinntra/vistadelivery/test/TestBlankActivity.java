package com.cinntra.vistadelivery.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.cinntra.vistadelivery.databinding.ActivityTestBlankBinding;

public class TestBlankActivity extends AppCompatActivity {
    private ActivityTestBlankBinding binding;


    double discountTotalValue = 0;
    double freightChargesValue = 0;
    double totalAfterItemDiscount = 0;

    double totalvalueFromList=100.0;




    //todo calculate DISCOUNT VALUE OF HEADER sum of all edit text ..
    private TextWatcher NumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.equals("")) {
                Log.e("TEXTWATCHER>>>", "onTextChanged: 0");
                DiscountCalculateSum(0.0);
            } else {
                Log.e("TEXTWATCHER>>>", "onTextChanged: " + charSequence);
                Log.e("TEXTWATCHER>>>", "total: " + discountTotalValue);
                DiscountCalculateSum(discountTotalValue);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

 /*   private void DiscountCalculateSum(double total) {
        double num1 = parseDiscountEditTextValue(binding.discontValue);

        double sum = total - (total * num1) / 100;

//        totalAfterItemDiscount = sum;

        binding.etTotalValues.setText(String.valueOf(sum));
    }*/

    private int parseDiscountEditTextValue(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(text);
        }
    }


    //todo calculation for FREIGHT CHARGES--
    private TextWatcher FreightTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.equals("")) {
                Log.e("FreightTEXTWATCHER>>>", "onTextChanged: 0");
                FreightChargeCalculateSum(0.0);
            } else {
                Log.e("FreightTEXTWATCHER>>>", "onTextChanged: " + charSequence);
                Log.e("FreightTEXTWATCHER>>>", "total: " + freightChargesValue);
                FreightChargeCalculateSum(freightChargesValue);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

/*    private void FreightChargeCalculateSum(double total) {
        double num1 = parseFreightChargeEditTextValue(binding.edFreightCharge);

        double abc = num1 * 18 / 100;
        double xyz = num1 + abc;

        double sum = total + xyz;

//        totalAfterItemDiscount = sum;

        binding.etTotalValues.setText(String.valueOf(sum));
    }*/

    private int parseFreightChargeEditTextValue(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(text);
        }
    }


    //todo calculation for BUY BACK --
    private TextWatcher BuyBackTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {


            String userInput = editable.toString();

            if (!userInput.isEmpty() && !userInput.equals("-")) {
                // If the first character is a digit and there is no "-", insert it
                if (Character.isDigit(userInput.charAt(0)) && !userInput.contains("-")) {
                    editable.insert(0, "-");
                }
            }

            if (userInput.equals("")) {
                Log.e("BuyBackTEXTWATCHER>>>", "onTextChanged: 0");
                BuyBackCalculateSum(totalAfterItemDiscount);
            } else {
                Log.e("BuyBackTEXTWATCHER>>>", "onTextChanged: " + userInput);
                Log.e("BuyBackTEXTWATCHER>>>", "total: " + totalAfterItemDiscount);
                BuyBackCalculateSum(totalAfterItemDiscount);
            }

        }
    };

/*    private void BuyBackCalculateSum(double total) {
        double num1 = parseBuyBackEditTextValue(binding.edBuyBuck);

        double sum = total - num1;

//        totalAfterItemDiscount = sum;

        binding.etTotalValues.setText(String.valueOf(sum));
    }*/

    private double parseBuyBackEditTextValue(EditText editText) {
        String text = editText.getText().toString();

        if (text.isEmpty() || text.equals("-")) {
            return 0.0;
        } else {
            // Remove leading hyphen if present
            if (text.charAt(0) == '-') {
                text = text.substring(1);
            }

            return Double.parseDouble(text);
        }

    }




    //todo new functions
    private void DiscountCalculateSum(double total) {
        double num1 = parseDiscountEditTextValue(binding.discontValue);

        double sum;
        if (num1 > 0) {
            sum = total - (total * num1) / 100;
        } else {
            sum = totalvalueFromList;
        }

        binding.etTotalValues.setText(String.valueOf(sum));
    }

    private void FreightChargeCalculateSum(double total) {
        double num1 = parseFreightChargeEditTextValue(binding.edFreightCharge);

        if (num1 > 0) {
            double abc = num1 * 18 / 100;
            double xyz = num1 + abc;

            total = total + xyz;
        }

        binding.etTotalValues.setText(String.valueOf(total));
    }

    private void BuyBackCalculateSum(double total) {
        double num1 = parseBuyBackEditTextValue(binding.edBuyBuck);

        if (num1 > 0) {
            total -= num1;
        }

        binding.etTotalValues.setText(String.valueOf(total));
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBlankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.discontValue.addTextChangedListener(NumberTextWatcher);
        binding.edFreightCharge.addTextChangedListener(FreightTextWatcher);
        binding.edBuyBuck.addTextChangedListener(BuyBackTextWatcher);


    }





}