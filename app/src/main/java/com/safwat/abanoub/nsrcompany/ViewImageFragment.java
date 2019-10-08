package com.safwat.abanoub.nsrcompany;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class ViewImageFragment extends Fragment {

    ZoomageView imageView;
    String imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_image_fragment, container, false);

        imagePath = getArguments().getString("image_path");

        imageView = view.findViewById(R.id.image);

        if (imagePath != null)
            Glide.with(getContext())
                    .load(imagePath)
                    .into(imageView);

        return view;
    }
}
