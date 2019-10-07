package com.safwat.abanoub.nsrcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

class ProductsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> list;

    public ProductsAdapter(Context context, ArrayList<Product> list) {
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
            view = inflater.inflate(R.layout.products_list_item, null);
        }

        ((TextView) view.findViewById(R.id.title)).setText(list.get(i).title);
        ((TextView) view.findViewById(R.id.price)).setText(list.get(i).price);

        final String image = list.get(i).image;
        final ImageView image_view = (ImageView) view.findViewById(R.id.image);

        if (image != null)
            Glide.with(context)
                    .load(image)
                    .into(image_view);
        else
            image_view.setImageDrawable(context.getResources().getDrawable(R.drawable.products));

        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = ViewImageDialogFragment.newInstance(image);
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "tag");
            }
        });

        return view;
    }
}
