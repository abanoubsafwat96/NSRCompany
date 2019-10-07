package com.safwat.abanoub.nsrcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class RatersNamesAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> ratersNames;

    public RatersNamesAdapter(Context context, ArrayList<String> ratersNames) {
        this.context = context;
        this.ratersNames = ratersNames;
    }

    @Override
    public int getCount() {
        return ratersNames.size();
    }

    @Override
    public Object getItem(int position) {
        return ratersNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.raters_names_list_item, null);
        }

        ((TextView) view.findViewById(R.id.name)).setText(ratersNames.get(position));

        return view;
    }
}
