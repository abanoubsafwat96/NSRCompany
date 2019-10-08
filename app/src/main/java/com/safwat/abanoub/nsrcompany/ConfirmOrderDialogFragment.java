package com.safwat.abanoub.nsrcompany;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class ConfirmOrderDialogFragment extends DialogFragment {

    ListView listView;
    CheckBox checkBox;
    TextView totalPrice_TV, totalPrice_TV2;
    Button order_btn;

    ArrayList<Product> products_list;
    int[] productsQuantities;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, myCurrentPointsReference, pointsReference;
    private ValueEventListener myCurrentPointsEventListener;
    private PointsItem pointsItems;
    private int earnedPoints, myCurrentPoints, newMyCurrentPoints;
    float totalPrice,discount;
    private ProgressDialog progressDialog1;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static ConfirmOrderDialogFragment newInstance(ArrayList<Product> products_list, int[] productsQuantities, float totalPrice) {

        ConfirmOrderDialogFragment f = new ConfirmOrderDialogFragment();
        f.products_list = products_list;
        f.productsQuantities = productsQuantities;
        f.totalPrice = totalPrice;
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.confirm_order_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_order_dialog, container, false);

        listView = view.findViewById(R.id.listView);
        checkBox = view.findViewById(R.id.checkbox);
        totalPrice_TV = view.findViewById(R.id.totalPrice_TV);
        totalPrice_TV2 = view.findViewById(R.id.totalPrice_TV2);
        order_btn = view.findViewById(R.id.order_btn);

        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog1 = new ProgressDialog(getContext());
        progressDialog1.setTitle("جاري الطلب");
        progressDialog1.setMessage("من فضلك انتظر...");

        final ArrayList<Product> products_list2 = new ArrayList<>();
        final ArrayList<Integer> productsQuantities2 = new ArrayList<>();

        for (int i = 0; i < products_list.size(); i++) {
            if (productsQuantities[i] > 0) {
                products_list2.add(products_list.get(i));
                productsQuantities2.add(productsQuantities[i]);
            }
        }

        OrderAdapter adapter = new OrderAdapter(getContext(), null, null
                , products_list2, productsQuantities2, "ConfirmOrder");
        listView.setAdapter(adapter);
        Utilities.getTotalHeightofListView(listView, 200);

        progressDialog1.show();

        //points
        pointsReference = firebaseDatabase.getReference().child("Points");
        pointsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pointsItems = Utilities.getPointsItems(dataSnapshot);

                if (pointsItems != null) {

                    if (totalPrice >= Integer.parseInt(pointsItems.line1Number1)
                            && totalPrice <= Integer.parseInt(pointsItems.line1Number2))
                        earnedPoints = Integer.parseInt(pointsItems.line1Number3);

                    else if (totalPrice >= Integer.parseInt(pointsItems.line2Number1)
                            && totalPrice <= Integer.parseInt(pointsItems.line2Number2))
                        earnedPoints = Integer.parseInt(pointsItems.line2Number3);

                    else if (totalPrice >= Integer.parseInt(pointsItems.line3Number1)
                            && totalPrice <= Integer.parseInt(pointsItems.line3Number2))
                        earnedPoints = Integer.parseInt(pointsItems.line3Number3);

                    else if (totalPrice >= Integer.parseInt(pointsItems.line4Number1)
                            && totalPrice <= Integer.parseInt(pointsItems.line4Number2))
                        earnedPoints = Integer.parseInt(pointsItems.line4Number3);

                    else if (totalPrice >= Integer.parseInt(pointsItems.line5Number1)
                            && totalPrice <= Integer.parseInt(pointsItems.line5Number2))
                        earnedPoints = Integer.parseInt(pointsItems.line5Number3);
                    else
                        earnedPoints = 0;

                    myCurrentPointsReference = firebaseDatabase.getReference().child("Users")
                            .child(Utilities.getCurrentUID()).child("points");

                    myCurrentPointsEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String currentPoints = Utilities.getValueIfNotNull(dataSnapshot);

                            if (currentPoints != null)
                                myCurrentPoints = Integer.parseInt(currentPoints);
                            else
                                myCurrentPoints = 0;

                            newMyCurrentPoints = myCurrentPoints + earnedPoints;

                            discount = newMyCurrentPoints * Float.parseFloat(pointsItems.pointPrice);

                            totalPrice_TV.setText(totalPrice + "");
                            totalPrice_TV2.setText((totalPrice - discount) + "");

                            if (progressDialog1.isShowing()) {
                                progressDialog1.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    myCurrentPointsReference.addValueEventListener(myCurrentPointsEventListener);

                } else
                    Toast.makeText(getContext(), "خطأ بسبب مشكلة في النقاط", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (newMyCurrentPoints != 0 && pointsItems != null)
                        discount = newMyCurrentPoints * Float.parseFloat(pointsItems.pointPrice);
                } else {
                    if (discount != 0)
                        discount = 0;
                }
            }
        });

        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(getContext())) {

                    progressDialog1.show();

                    if (pointsItems != null) {

                        databaseReference = firebaseDatabase.getReference().child("Orders").child(Utilities.getCurrentUID())
                                .child(Calendar.getInstance().getTimeInMillis() + "");

                        databaseReference.child("status").setValue("لم يبدأ تحضير طلبك بعد ...");

                        for (int i = 0; i < products_list2.size(); i++) {

                            Product product = products_list2.get(i);
                            OrderItem orderItem = new OrderItem(product.pushID, productsQuantities2.get(i).toString());

                            String pushID = databaseReference.push().getKey();
                            databaseReference.child(pushID).setValue(orderItem);
                        }

                        //notification ref edit to start cloud function
                        DatabaseReference notif_ref = firebaseDatabase.getReference()
                                .child("orderNotification").child("randomkey");
                        notif_ref.setValue(notif_ref.push().getKey());

                        //apply discount or not
                        if (checkBox.isChecked()) {

                            if (discount != 0)
                                databaseReference.child("discount").setValue(discount + "");

                            myCurrentPointsReference.setValue("0");
                        }else {
                            databaseReference.child("discount").setValue("0");

                            if (myCurrentPoints != newMyCurrentPoints) {

                                myCurrentPointsReference.removeEventListener(myCurrentPointsEventListener);
                                myCurrentPointsReference.setValue(newMyCurrentPoints);
                            }
                        }

                        if (getContext() != null)
                            Toast.makeText(getContext(), "تم الطلب بنجاح", Toast.LENGTH_SHORT).show();

                        if (progressDialog1.isShowing()) {
                            progressDialog1.dismiss();
                        }
                    }
                    dismiss();

                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
