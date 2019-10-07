package com.safwat.abanoub.nsrcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

class RatesAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> productArrayList;
    String userType;
    ArrayList<String> productsRates_list;

    String[] userRatesList;

    public RatesAdapter(Context context, ArrayList<Product> productArrayList, String userType) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.userType = userType;

        userRatesList = new String[productArrayList.size()];
    }

    public RatesAdapter(Context context, ArrayList<Product> productArrayList
            , ArrayList<String> productsRates_list, String userType) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.productsRates_list = productsRates_list;
        this.userType = userType;
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return productArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rates_list_item, null);
        }

        ((TextView) view.findViewById(R.id.title)).setText(productArrayList.get(i).title);

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        if (userType.equals("Admin")) {

            ratingBar.setIsIndicator(true); //disable ratingbar
            ratingBar.setRating(Float.parseFloat(productsRates_list.get(i)));
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                userRatesList[i] = v + "";
            }
        });

        return view;
    }
}
