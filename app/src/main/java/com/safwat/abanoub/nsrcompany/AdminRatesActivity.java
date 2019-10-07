package com.safwat.abanoub.nsrcompany;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AdminRatesActivity extends AppCompatActivity {

    ListView listView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<String> raters_UIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rates);

        listView = findViewById(R.id.listView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Rates");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                raters_UIDs = Utilities.getUIDs(dataSnapshot);

                final ArrayList<String> ratersNames = new ArrayList<>();

                for (int i = 0; i < raters_UIDs.size(); i++) {
                    final int position = i;

                    firebaseDatabase.getReference().child("Users").child(raters_UIDs.get(position)).child("fullname")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String fullname = Utilities.getValueIfNotNull(dataSnapshot);
                                    ratersNames.add(fullname);

                                    if (position == raters_UIDs.size() - 1) {
                                        RatersNamesAdapter adapter=new RatersNamesAdapter(
                                                AdminRatesActivity.this,ratersNames);
                                        listView.setAdapter(adapter);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(AdminRatesActivity.this, RatesActivity.class);
                intent.putExtra("userType", "Admin");
                intent.putExtra("clicked_rater_UID", raters_UIDs.get(i));
                startActivity(intent);
            }
        });
    }
}
