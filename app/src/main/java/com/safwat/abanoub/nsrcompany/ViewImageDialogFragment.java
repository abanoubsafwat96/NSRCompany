package com.safwat.abanoub.nsrcompany;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.fragment.app.DialogFragment;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class ViewImageDialogFragment extends DialogFragment {

    ImageView imageView;
    String imagePath;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static ViewImageDialogFragment newInstance(String imagePath) {

        ViewImageDialogFragment f = new ViewImageDialogFragment();
        f.imagePath = imagePath;
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.view_image_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_image_dialog, container, false);

        imageView = view.findViewById(R.id.image);

        if (imagePath != null)
            Glide.with(getContext())
                    .load(imagePath)
                    .into(imageView);

        return view;
    }
}
