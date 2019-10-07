package com.safwat.abanoub.nsrcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class PreviousOrdersAdapter extends BaseAdapter {
    private Context context;
    public ArrayList<String> finalOrdersDates_list, finalOrdersStatus_list;
    private ArrayList<User> finalOrdersOwners_list;
    private String userType;

    public PreviousOrdersAdapter(Context context, ArrayList<String> finalOrdersDates_list
            , ArrayList<String> finalOrdersStatus_list, String userType) {
        this.context = context;
        this.finalOrdersDates_list = finalOrdersDates_list;
        this.finalOrdersStatus_list = finalOrdersStatus_list;
        this.userType = userType;
    }

    public PreviousOrdersAdapter(Context context, ArrayList<String> finalOrdersDates_list
            , ArrayList<User> finalOrdersOwners_list, ArrayList<String> finalOrdersStatus_list, String userType) {
        this.context = context;
        this.finalOrdersDates_list = finalOrdersDates_list;
        this.finalOrdersOwners_list = finalOrdersOwners_list;
        this.finalOrdersStatus_list = finalOrdersStatus_list;
        this.userType = userType;
    }

    @Override
    public int getCount() {
        return finalOrdersDates_list.size();
    }

    @Override
    public Object getItem(int i) {
        return finalOrdersDates_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.previous_orders_list_item, null);
        }

        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(finalOrdersDates_list.get(i));

        if (finalOrdersOwners_list != null) {

            TextView orderOwnerFullname = (TextView) view.findViewById(R.id.orderOwnerFullname);
            TextView orderOwnerPhoneNumber = (TextView) view.findViewById(R.id.orderOwnerPhoneNumber);

            orderOwnerFullname.setText(finalOrdersOwners_list.get(i).fullname);
            orderOwnerPhoneNumber.setText(finalOrdersOwners_list.get(i).phoneNumber);

            orderOwnerFullname.setVisibility(View.VISIBLE);
            orderOwnerPhoneNumber.setVisibility(View.VISIBLE);
        }

        if (finalOrdersStatus_list != null) {

            String status = finalOrdersStatus_list.get(i);
            if (status.equals("لم يبدأ تحضير طلبك بعد ...")) {
                date.setTextColor(context.getResources().getColor(R.color.yellow));
                view.setBackgroundColor(context.getResources().getColor(R.color.smawy2));

            } else if (status.equals("جاري تحضير طلبك ...")) {
                date.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                view.setBackgroundColor(context.getResources().getColor(R.color.smawy));
            }
        }

        return view;
    }
}
