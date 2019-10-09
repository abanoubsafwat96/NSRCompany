package com.safwat.abanoub.nsrcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class OrderAdapter extends BaseAdapter {

    Context context;
    OrderHelper orderHelper;
    OrderHelper.Image orderHelperImage;
    ArrayList<Product> products_list;
    ArrayList<Integer> productsQuantities2;
    String usingType;

    int[] productsQuantities;

    public OrderAdapter(Context context, OrderHelper orderHelper, OrderHelper.Image orderHelperImage,
                        ArrayList<Product> products_list, String usingType) {

        this.context = context;
        this.orderHelper = orderHelper;
        this.orderHelperImage = orderHelperImage;
        this.products_list = products_list;
        this.usingType = usingType;

        productsQuantities = new int[products_list.size()];
    }

    public OrderAdapter(Context context, OrderHelper orderHelper, OrderHelper.Image orderHelperImage
            , ArrayList<Product> products_list, ArrayList<Integer> productsQuantities2, String usingType) {
        this.context = context;
        this.orderHelper = orderHelper;
        this.orderHelperImage = orderHelperImage;
        this.products_list = products_list;
        this.productsQuantities2 = productsQuantities2;
        this.usingType = usingType;
    }

    @Override
    public int getCount() {
        return products_list.size();
    }

    @Override
    public Object getItem(int i) {
        return products_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.order_list_item, null);
        }

        Product product = products_list.get(i);

        ((TextView) view.findViewById(R.id.title)).setText(product.title);
        TextView price_TV = (TextView) view.findViewById(R.id.price);
        price_TV.setText(product.price);

        final String image = product.image;
        final ImageView image_view = (ImageView) view.findViewById(R.id.image);

        if (image != null && context != null)
            Glide.with(context)
                    .load(image)
                    .into(image_view);
        else
            image_view.setImageDrawable(context.getResources().getDrawable(R.drawable.products));

        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!usingType.equals("ConfirmOrder")) {
                    orderHelperImage.imageClicked(image);
                }
            }
        });

        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

        if (usingType.equals("OrderItem")) {

            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(500);

            //Gets whether the selector wheel wraps when reaching the min/max value.
            numberPicker.setWrapSelectorWheel(true);

            //Set a value change listener for NumberPicker
            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    productsQuantities[i] = newVal;
                    orderHelper.updateTotalPrice();
                }
            });
        } else {

            numberPicker.setVisibility(View.GONE);
            ((LinearLayout) view.findViewById(R.id.quantityLinear)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.quantity)).setText(productsQuantities2.get(i).toString());

            if (usingType.equals("ConfirmOrder")) {

                price_TV.setTextColor(context.getResources().getColor(R.color.black));
                ((TextView) view.findViewById(R.id.gneh)).setTextColor(context.getResources().getColor(R.color.black));

            } else if (usingType.equals("PreviousOrder")) {

                ((TextView) view.findViewById(R.id.quantity)).setTextColor(context.getResources().getColor(R.color.white));
                ((TextView) view.findViewById(R.id.dasta)).setTextColor(context.getResources().getColor(R.color.white));
            }
        }

        return view;
    }
}
