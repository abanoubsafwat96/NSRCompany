package com.safwat.abanoub.nsrcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class SaleAdapter extends BaseAdapter{

    Context context;
    ArrayList<SaleItem> list;

    public SaleAdapter(Context context, ArrayList<SaleItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.sale_list_item, null);
        }

        ((TextView) view.findViewById(R.id.title)).setText(list.get(i).title);
        ((TextView) view.findViewById(R.id.description)).setText(list.get(i).description);

        return view;
    }
}
