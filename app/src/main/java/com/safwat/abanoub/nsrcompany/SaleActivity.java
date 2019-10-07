package com.safwat.abanoub.nsrcompany;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class SaleActivity extends AppCompatActivity {

    TextView noData;
    FloatingActionButton add_btn;
    ListView listView;

    SaleAdapter saleAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("العروض");

        noData = findViewById(R.id.noData);
        add_btn = findViewById(R.id.fab);
        listView = findViewById(R.id.listView);

        userType = getIntent().getStringExtra("userType");
        if (userType.equals("Admin"))
            add_btn.setVisibility(View.VISIBLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Sale");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<SaleItem> reversed_list = Utilities.getAllSaleItems(dataSnapshot);
                ArrayList<SaleItem> list = Utilities.reverseSaleItemsList(reversed_list);

                if (list.size() == 0)
                    noData.setVisibility(View.VISIBLE);
                else {
                    noData.setVisibility(View.GONE);

                    saleAdapter = new SaleAdapter(SaleActivity.this, list);
                    listView.setAdapter(saleAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (userType.equals("Admin")) {
                    DialogFragment dialog = AddOrEditSaleItemsDialogFragment.newInstance("edit"
                            , (SaleItem) saleAdapter.getItem(i));
                    dialog.show(getSupportFragmentManager(), "tag");
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                if (userType.equals("Admin")) {

                    final SaleItem saleItem = (SaleItem) adapterView.getAdapter().getItem(i);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SaleActivity.this);
                    alertDialogBuilder.setMessage("هل تريد حذف هذا العرض ؟");
                    alertDialogBuilder.setPositiveButton("حذف",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    databaseReference.child(saleItem.pushID).setValue(null);
                                    saleAdapter.list.remove(i);
                                    saleAdapter.notifyDataSetChanged();

                                    Toast.makeText(SaleActivity.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();

                                }
                            });
                    alertDialogBuilder.setNegativeButton("الغاء",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    alertDialogBuilder.create().show();
                }
                return true;
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = AddOrEditSaleItemsDialogFragment.newInstance("add", null);
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });
    }
}