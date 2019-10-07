package com.safwat.abanoub.nsrcompany;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PreviousOrders2Activity extends AppCompatActivity {

    ListView listView;
    TextView price, status;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, statusReference;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders2);

        userType = getIntent().getStringExtra("userType");
        String orders_dates_in_dayFormat = getIntent().getStringExtra("orders_dates_in_dayFormat");
        String order_date_in_milliSeconds = getIntent().getStringExtra("order_date_in_milliSeconds");

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(orders_dates_in_dayFormat);

        listView = findViewById(R.id.listView);
        price = findViewById(R.id.price);
        status = findViewById(R.id.status);

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (userType.equals("Admin")) {

            User orderOwner = getIntent().getParcelableExtra("orderOwner");

            ((TextView) findViewById(R.id.orderOwnerFullname)).setText(orderOwner.fullname);
            ((TextView) findViewById(R.id.orderOwnerPhoneNumber)).setText(orderOwner.phoneNumber);

            RelativeLayout orderOwnerData_Relative = findViewById(R.id.orderOwnerData_Relative);
            orderOwnerData_Relative.setVisibility(View.VISIBLE);

            //seen
            statusReference = firebaseDatabase.getReference().child("Orders").child(orderOwner.uid)
                    .child(order_date_in_milliSeconds).child("status");
            statusReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String status = Utilities.getValueIfNotNull(dataSnapshot);

                    if (status.equals("لم يبدأ تحضير طلبك بعد ...")) {
                        statusReference.setValue("جاري تحضير طلبك ...");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference = firebaseDatabase.getReference().child("Orders")
                    .child(orderOwner.uid).child(order_date_in_milliSeconds);
        } else {
            databaseReference = firebaseDatabase.getReference().child("Orders")
                    .child(Utilities.getCurrentUID()).child(order_date_in_milliSeconds);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<OrderItem> orderItems_list = Utilities.getOrderItems(dataSnapshot);

                final ArrayList<Product> products_list = new ArrayList<>();
                final ArrayList<Integer> productsQuantities = new ArrayList<>();

                for (int i = 0; i < orderItems_list.size(); i++) {
                    final int position = i;
                    final OrderItem orderItem = orderItems_list.get(position);

                    firebaseDatabase.getReference().child("Products").child(orderItem.productPushID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Product product = Utilities.getProduct(dataSnapshot);
                                    if (product != null) {
                                        products_list.add(product);
                                        productsQuantities.add(Integer.parseInt(orderItem.quantity));
                                    }

                                    if (position == orderItems_list.size() - 1) {
                                        OrderAdapter orderAdapter = new OrderAdapter(PreviousOrders2Activity.this
                                                , products_list, productsQuantities, "PreviousOrder");
                                        listView.setAdapter(orderAdapter);
                                        Utilities.getTotalHeightofListView(listView, 500);

                                        int totalPrice = 0;
                                        for (int i = 0; i < products_list.size(); i++) {
                                            totalPrice += Integer.parseInt(products_list.get(i).price) * productsQuantities.get(i);
                                        }
                                        price.setText(totalPrice + "");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (userType.equals("User")) {
            databaseReference.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String status_str = Utilities.getValueIfNotNull(dataSnapshot);

                    if (status_str != null)
                        status.setText(status_str);
                    else
                        status.setText("لم يبدأ تجهيز طلبك بعد ...");

                    status.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
