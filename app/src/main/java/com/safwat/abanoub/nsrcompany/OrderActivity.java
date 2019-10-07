package com.safwat.abanoub.nsrcompany;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class OrderActivity extends AppCompatActivity implements OrderHelper {

    TextView noData, price;
    ListView listView;
    Button confirmOrder;

    ArrayList<Product> reversed_products_list, products_list;
    OrderAdapter orderAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String userType;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("اطلب");

        noData = findViewById(R.id.noData);
        price = findViewById(R.id.price);
        listView = findViewById(R.id.listView);
        confirmOrder = findViewById(R.id.confirmOrder);

        userType = getIntent().getStringExtra("userType");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reversed_products_list = Utilities.getAllProducts(dataSnapshot);
                products_list = Utilities.reverseProdustsList(reversed_products_list);

                if (products_list.size() == 0)
                    noData.setVisibility(View.VISIBLE);
                else {
                    noData.setVisibility(View.GONE);

                    orderAdapter = new OrderAdapter(OrderActivity.this
                            , OrderActivity.this, products_list
                            , "OrderItem");
                    listView.setAdapter(orderAdapter);
                    Utilities.getTotalHeightofListView(listView, 200);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int[] productsQuantities = orderAdapter.productsQuantities;

                boolean notEqualZeroItemsFound = false;
                for (int i = 0; i < productsQuantities.length; i++) {
                    if (productsQuantities[i] != 0) {
                        notEqualZeroItemsFound = true;
                        break;
                    }
                }

                if (notEqualZeroItemsFound) {
                    DialogFragment dialog = ConfirmOrderDialogFragment.newInstance(products_list, productsQuantities, totalPrice);
                    dialog.show(getSupportFragmentManager(), "tag");
                } else
                    Toast.makeText(OrderActivity.this, "من فضلك اختر كمية من منتج واحد علي الاقل", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void updateTotalPrice() {

        totalPrice = 0;
        int[] productsQuantities = orderAdapter.productsQuantities;

        for (int i = 0; i < products_list.size(); i++) {
            totalPrice += productsQuantities[i] * Integer.parseInt(products_list.get(i).price);
        }
        price.setText(totalPrice + "");
    }
}