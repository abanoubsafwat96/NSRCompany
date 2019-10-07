package com.safwat.abanoub.nsrcompany;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.fragment.app.DialogFragment;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class AddOrEditSaleItemsDialogFragment extends DialogFragment {

    TextView dialogTitle;
    EditText title, description;
    Button add_btn;

    String usingType;
    SaleItem oldSaleItem;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static AddOrEditSaleItemsDialogFragment newInstance(String usingType, SaleItem oldSaleItem) {

        AddOrEditSaleItemsDialogFragment f = new AddOrEditSaleItemsDialogFragment();
        f.usingType = usingType;
        f.oldSaleItem = oldSaleItem;
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.add_sale_item_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_sale_item_dialog, container, false);

        dialogTitle = view.findViewById(R.id.dialogTitle);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        add_btn = (Button) view.findViewById(R.id.add_btn);

        if (usingType.equals("edit") && oldSaleItem != null) {
            dialogTitle.setText("تعديل العرض");
            add_btn.setText("تعديل");
            title.setText(oldSaleItem.title);
            description.setText(oldSaleItem.description);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Sale");

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(description.getText()) == false)
                    add_btn.setEnabled(true);
                else
                    add_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(title.getText()) == false)
                    add_btn.setEnabled(true);
                else
                    add_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(getContext())) {
                    if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(description.getText()))
                        Toast.makeText(getContext(), R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    else {

                        SaleItem saleItem = new SaleItem(title.getText().toString(), description.getText().toString());

                        if (usingType.equals("add")) {

                            String pushID = databaseReference.push().getKey();
                            databaseReference.child(pushID).setValue(saleItem);

                            Toast.makeText(getContext(), "تم اضافة عرض جديد", Toast.LENGTH_SHORT).show();

                        } else if (usingType.equals("edit") && saleItem != null) {
                            databaseReference.child(oldSaleItem.pushID).setValue(saleItem);

                            Toast.makeText(getContext(), "تم تعديل العرض بنجاح", Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                    }
                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
