package com.safwat.abanoub.nsrcompany;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RatesActivity extends AppCompatActivity {

    TextView noData, comment_textView;
    EditText comment_editText;
    Button send_btn;
    ListView listView;
    LinearLayout addCommentLinear;

    ArrayList<Product> products_list, reversed_products_list;
    RatesAdapter ratesAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference productsReference;

    String userType;
    String clicked_rater_UID;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("تقييم و تعليق");

        noData = findViewById(R.id.noData);
        comment_editText = findViewById(R.id.comment_editText);
        send_btn = findViewById(R.id.send);
        listView = findViewById(R.id.listView);
        addCommentLinear = findViewById(R.id.addCommentLinear);
        comment_textView = findViewById(R.id.comment_textView);

        userType = getIntent().getStringExtra("userType");

        if (userType.equals("Admin")) {
            clicked_rater_UID = getIntent().getStringExtra("clicked_rater_UID");
        } else {
            addCommentLinear.setVisibility(View.VISIBLE);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        productsReference = firebaseDatabase.getReference().child("Products");
        productsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reversed_products_list = Utilities.getAllProducts(dataSnapshot);
                products_list = Utilities.reverseProdustsList(reversed_products_list);

                if (products_list.size() == 0)
                    noData.setVisibility(View.VISIBLE);
                else {
                    noData.setVisibility(View.GONE);

                    if (userType.equals("Admin")) {

                        firebaseDatabase.getReference().child("Rates").child(clicked_rater_UID).child("comment")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String comment = Utilities.getValueIfNotNull(dataSnapshot);
                                        if (comment != null && !TextUtils.isEmpty(comment)) {
                                            comment_textView.setText("تعليق: " + comment);
                                            comment_textView.setVisibility(View.VISIBLE);
                                        } else
                                            comment_textView.setVisibility(View.GONE);

                                        firebaseDatabase.getReference().child("Rates").child(clicked_rater_UID)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        ArrayList<String> reversed_productsPushIDs_list
                                                                = Utilities.getProductsPushIDs(dataSnapshot);
                                                        final ArrayList<String> productsPushIDs_list
                                                                = Utilities
                                                                .reverseStringsList(reversed_productsPushIDs_list);

                                                        final ArrayList<Product> rateProducts_list = new ArrayList<>();
                                                        for (int i = 0; i < productsPushIDs_list.size(); i++) {
                                                            final int position = i;
                                                            final String pushID = productsPushIDs_list.get(position);

                                                            firebaseDatabase.getReference().child("Products")
                                                                    .child(pushID)
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                            Product value = Utilities.getProduct(dataSnapshot);
                                                                            if (value != null)
                                                                                rateProducts_list.add(value);

                                                                            if (position == productsPushIDs_list.size() - 1) {

                                                                                final ArrayList<String> productsRates_list
                                                                                        = new ArrayList<>();
                                                                                for (int j = 0; j < rateProducts_list.size(); j++) {

                                                                                    final int position2 = j;
                                                                                    final String pushID2 = rateProducts_list.get(position2).pushID;

                                                                                    firebaseDatabase.getReference().child("Rates")
                                                                                            .child(clicked_rater_UID)
                                                                                            .child(pushID2).child("rate")
                                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                                                    productsRates_list.add(Utilities
                                                                                                            .getValueIfNotNull(dataSnapshot));

                                                                                                    if (position2
                                                                                                            == rateProducts_list.size() - 1) {

                                                                                                        RatesAdapter adapter = new RatesAdapter(
                                                                                                                RatesActivity.this
                                                                                                                , rateProducts_list
                                                                                                                , productsRates_list
                                                                                                                , userType);
                                                                                                        listView.setAdapter(adapter);
                                                                                                        Utilities.getTotalHeightofListView(listView, 250);
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                }
                                                                                            });
                                                                                }
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
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    } else
                        fillListView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] ratesList = ratesAdapter.userRatesList;
                boolean foundEmptyItem = false;

                for (int i = 0; i < ratesList.length; i++) {
                    if (TextUtils.isEmpty(ratesList[i])) {
                        foundEmptyItem = true;
                    }
                }

                if (foundEmptyItem) {
                    Toast.makeText(RatesActivity.this, "من فضلك: قيم كل المنتجات قبل الارسال", Toast.LENGTH_LONG).show();
                } else {
                    String currentUID = Utilities.getCurrentUID();

                    for (int i = 0; i < products_list.size(); i++) {
                        firebaseDatabase.getReference().child("Rates").child(currentUID)
                                .child(products_list.get(i).pushID).child("rate").setValue(ratesList[i]);
                    }

                    String comment_str = comment_editText.getText().toString();
                    firebaseDatabase.getReference().child("Rates").child(currentUID).child("comment")
                            .setValue(comment_str);

                    final DatabaseReference fullname_ref = firebaseDatabase.getReference().child("Users")
                            .child(currentUID).child("fullname");
                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String currentFullname = Utilities.getValueIfNotNull(dataSnapshot);

                            fullname_ref.removeEventListener(valueEventListener);

                            if (currentFullname != null) {
                                //notification ref edit to start cloud function
                                DatabaseReference notif_ref = firebaseDatabase.getReference().child("rateNotification");
                                Map<String, String> valuesMap = new HashMap<>();
                                valuesMap.put("randomkey", notif_ref.push().getKey());
                                valuesMap.put("fullname", currentFullname);
                                notif_ref.setValue(valuesMap);

                                Toast.makeText(RatesActivity.this, "تم الارسال بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    fullname_ref.addValueEventListener(valueEventListener);
                }
            }
        });
    }

    private void fillListView() {
        ratesAdapter = new RatesAdapter(RatesActivity.this, products_list, userType);
        listView.setAdapter(ratesAdapter);
        Utilities.getTotalHeightofListView(listView, 250);
    }
}

