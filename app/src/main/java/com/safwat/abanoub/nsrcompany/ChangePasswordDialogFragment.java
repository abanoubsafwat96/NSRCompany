package com.safwat.abanoub.nsrcompany;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class ChangePasswordDialogFragment extends DialogFragment {

    String oldPassword_str;
    EditText oldPassword, newPassword, confirmPassword;
    Button update_btn;

    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static ChangePasswordDialogFragment newInstance() {

        ChangePasswordDialogFragment f = new ChangePasswordDialogFragment();
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.change_password_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_dialog, container, false);
        // Do something else

        oldPassword = (EditText) view.findViewById(R.id.oldPassword);
        newPassword = (EditText) view.findViewById(R.id.newPassword);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        update_btn = (Button) view.findViewById(R.id.update_btn);

        firebaseUser = Utilities.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("Admins").child(Utilities.getCurrentUID());

        firebaseDatabase.getReference().child("Admins").child(Utilities.getCurrentUID()).child("password")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        oldPassword_str = Utilities.getValueIfNotNull(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(newPassword.getText()) == false
                        && TextUtils.isEmpty(confirmPassword.getText()) == false)
                    update_btn.setEnabled(true);
                else
                    update_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(oldPassword.getText()) == false
                        && TextUtils.isEmpty(confirmPassword.getText()) == false)
                    update_btn.setEnabled(true);
                else
                    update_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(newPassword.getText()) == false
                        && TextUtils.isEmpty(oldPassword.getText()) == false)
                    update_btn.setEnabled(true);
                else
                    update_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(getContext())) {
                    if (TextUtils.isEmpty(oldPassword.getText()) || TextUtils.isEmpty(newPassword.getText())
                            || TextUtils.isEmpty(confirmPassword.getText()))
                        Toast.makeText(getContext(), R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    else {
                        if (oldPassword_str != null) {
                            if (oldPassword.getText().toString().equals(oldPassword_str)) {
                                if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                                    firebaseUser.updatePassword(newPassword.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // updating password success
                                                        databaseReference.child("password").setValue(newPassword.getText().toString());
                                                        Toast.makeText(getContext(), "تم تعديل كلمة السر بنجاح", Toast.LENGTH_SHORT).show();
                                                        dismiss();
                                                    } else {
                                                        // If updating fails, display a message to the user.
                                                        Toast.makeText(getContext(), "فشل تعديل كلمة السر", Toast.LENGTH_SHORT).show();
//                                                        Toast.makeText(getContext(), task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else
                                    Toast.makeText(getContext(), "كلمات السر غير متطابقة", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getContext(), "كلمة السر القديمة غير صحيحة", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
