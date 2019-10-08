package com.safwat.abanoub.nsrcompany;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PreviousOrdersActivity extends AppCompatActivity {

    TextView noData;
    ListView listView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String userType;
    private ArrayList<String> orders_dates_in_milliSeconds_list, ordersOwners_UIDs;
    private SimpleDateFormat dateFormat;

    private ArrayList<String> allOrdersDatesInMilliSeconds_list, allOrdersDates_list, allOrdersStatus_list;
    private ArrayList<User> allOrdersOwners_list;
    private PreviousOrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("الطلبات السابقة");

        noData = findViewById(R.id.noData);
        listView = findViewById(R.id.listView);

        userType = getIntent().getStringExtra("userType");

        dateFormat = new SimpleDateFormat("EEEE ',' dd MMMM yyyy ',' الساعة hh:mm a"
                , new Locale("ar"));

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (userType.equals("Admin")) {

            databaseReference = firebaseDatabase.getReference().child("Orders");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ordersOwners_UIDs = Utilities.getUIDs(dataSnapshot);
                    final ArrayList<User> orderOwners = new ArrayList<>();

                    allOrdersDatesInMilliSeconds_list = new ArrayList<>();
                    allOrdersDates_list = new ArrayList<>();
                    allOrdersOwners_list = new ArrayList<>();
                    allOrdersStatus_list = new ArrayList<>();

                    if (ordersOwners_UIDs.size() == 0)
                        noData.setVisibility(View.VISIBLE);
                    else {
                        noData.setVisibility(View.GONE);

                        for (int i = 0; i < ordersOwners_UIDs.size(); i++) {
                            final int position = i;

                            firebaseDatabase.getReference().child("Users").child(ordersOwners_UIDs.get(position))
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = Utilities.getUser(dataSnapshot);
                                            orderOwners.add(user);

                                            firebaseDatabase.getReference().child("Orders")
                                                    .child(ordersOwners_UIDs.get(position))
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            orders_dates_in_milliSeconds_list = Utilities.getUIDs(dataSnapshot);

                                                            for (int j = 0; j < orders_dates_in_milliSeconds_list.size(); j++) {

                                                                String orderDateInMilliSeconds = orders_dates_in_milliSeconds_list.get(j);

                                                                allOrdersDatesInMilliSeconds_list
                                                                        .add(orderDateInMilliSeconds);

                                                                Time orderTime = new Time(Long
                                                                        .parseLong(orderDateInMilliSeconds));

                                                                allOrdersDates_list.add(dateFormat.format(orderTime));
                                                                allOrdersOwners_list.add(orderOwners.get(position));
                                                            }

                                                            //get status of every order
                                                            if (dataSnapshot.getValue() != null) {
                                                                for (DataSnapshot child : dataSnapshot.getChildren()) {

                                                                    allOrdersStatus_list.add(child.child("status")
                                                                            .getValue(String.class));
                                                                }
                                                            }

                                                            if (position == ordersOwners_UIDs.size() - 1) {

                                                                //order orders by date
                                                                for (int k = 0; k < allOrdersDatesInMilliSeconds_list.size(); k++) {

                                                                    for(int m=k+1;m<allOrdersDatesInMilliSeconds_list.size();m++){

                                                                        String kFromList=allOrdersDatesInMilliSeconds_list
                                                                                .get(k);
                                                                        String mFromList=allOrdersDatesInMilliSeconds_list
                                                                                .get(m);

                                                                        Date tempK=new Date(Long.parseLong(kFromList));
                                                                        Date tempM=new Date(Long.parseLong(mFromList));

                                                                        String tempK2 = allOrdersDates_list.get(k);
                                                                        String tempM2 = allOrdersDates_list.get(m);

                                                                        User tempK3 = allOrdersOwners_list.get(k);
                                                                        User tempM3 = allOrdersOwners_list.get(m);

                                                                        String tempK4 = allOrdersStatus_list.get(k);
                                                                        String tempM4 = allOrdersStatus_list.get(m);

                                                                        if(tempK.before(tempM)){
                                                                            allOrdersDatesInMilliSeconds_list.set(k,mFromList);
                                                                            allOrdersDatesInMilliSeconds_list.set(m,kFromList);

                                                                            allOrdersDates_list.set(k,tempM2);
                                                                            allOrdersDates_list.set(m,tempK2);

                                                                            allOrdersOwners_list.set(k,tempM3);
                                                                            allOrdersOwners_list.set(m,tempK3);

                                                                            allOrdersStatus_list.set(k,tempM4);
                                                                            allOrdersStatus_list.set(m,tempK4);
                                                                        }
                                                                    }
                                                                }

                                                                adapter = new PreviousOrdersAdapter(
                                                                        PreviousOrdersActivity.this
                                                                        , allOrdersDates_list
                                                                        , allOrdersOwners_list
                                                                        , allOrdersStatus_list
                                                                        , userType);

                                                                listView.setAdapter(adapter);
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
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else { //user
            databaseReference = firebaseDatabase.getReference().child("Orders").child(Utilities.getCurrentUID());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    orders_dates_in_milliSeconds_list = Utilities.getUIDs(dataSnapshot);

                    allOrdersDates_list = new ArrayList<>();
                    allOrdersStatus_list = new ArrayList<>();

                    if (orders_dates_in_milliSeconds_list.size() == 0)
                        noData.setVisibility(View.VISIBLE);
                    else {
                        noData.setVisibility(View.GONE);

                        for (int i = 0; i < orders_dates_in_milliSeconds_list.size(); i++) {
                            int position = i;

                            Time time = new Time(Long.parseLong(orders_dates_in_milliSeconds_list.get(position)));
                            allOrdersDates_list.add(dateFormat.format(time));
                        }

                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {

                                allOrdersStatus_list.add(child.child("status")
                                        .getValue(String.class));
                            }
                        }

                        //order orders by date
                        for (int k = 0; k < orders_dates_in_milliSeconds_list.size(); k++) {

                            for(int m=k+1;m<orders_dates_in_milliSeconds_list.size();m++){

                                String kFromList=orders_dates_in_milliSeconds_list
                                        .get(k);
                                String mFromList=orders_dates_in_milliSeconds_list
                                        .get(m);

                                Date tempK=new Date(Long.parseLong(kFromList));
                                Date tempM=new Date(Long.parseLong(mFromList));

                                String tempK2 = allOrdersDates_list.get(k);
                                String tempM2 = allOrdersDates_list.get(m);

                                String tempK4 = allOrdersStatus_list.get(k);
                                String tempM4 = allOrdersStatus_list.get(m);

                                if(tempK.before(tempM)){
                                    orders_dates_in_milliSeconds_list.set(k,mFromList);
                                    orders_dates_in_milliSeconds_list.set(m,kFromList);

                                    allOrdersDates_list.set(k,tempM2);
                                    allOrdersDates_list.set(m,tempK2);

                                    allOrdersStatus_list.set(k,tempM4);
                                    allOrdersStatus_list.set(m,tempK4);
                                }
                            }
                        }

                        adapter = new PreviousOrdersAdapter(
                                PreviousOrdersActivity.this
                                , allOrdersDates_list
                                , allOrdersStatus_list
                                , userType);

                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PreviousOrdersActivity.this, PreviousOrders2Activity.class);
                intent.putExtra("userType", userType);
                intent.putExtra("orders_dates_in_dayFormat", allOrdersDates_list.get(i));


                if (userType.equals("Admin")) {
                    intent.putExtra("orderOwner", allOrdersOwners_list.get(i));
                    intent.putExtra("order_date_in_milliSeconds", allOrdersDatesInMilliSeconds_list.get(i));

                } else if (userType.equals("User"))
                    intent.putExtra("order_date_in_milliSeconds", orders_dates_in_milliSeconds_list.get(i));

                startActivity(intent);
            }
        });
    }
}