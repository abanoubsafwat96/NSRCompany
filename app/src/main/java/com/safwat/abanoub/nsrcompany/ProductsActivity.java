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

public class ProductsActivity extends AppCompatActivity {

    TextView noData;
    FloatingActionButton add_btn;
    ListView listView;

    ProductsAdapter productsAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("المنتجات");

        noData = findViewById(R.id.noData);
        add_btn = findViewById(R.id.fab);
        listView = findViewById(R.id.listView);

        userType = getIntent().getStringExtra("userType");
        if (userType.equals("Admin"))
            add_btn.setVisibility(View.VISIBLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Product> reversed_list = Utilities.getAllProducts(dataSnapshot);
                ArrayList<Product> list = Utilities.reverseProdustsList(reversed_list);

                if (list.size() == 0)
                    noData.setVisibility(View.VISIBLE);
                else {
                    noData.setVisibility(View.GONE);

                    productsAdapter = new ProductsAdapter(ProductsActivity.this, list);
                    listView.setAdapter(productsAdapter);
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
                    DialogFragment dialog = AddOrEditProductsDialogFragment.newInstance("edit"
                            , (Product) productsAdapter.getItem(i));
                    dialog.show(getSupportFragmentManager(), "tag");
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                if (userType.equals("Admin")) {

                    final Product product = (Product) adapterView.getAdapter().getItem(i);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductsActivity.this);
                    alertDialogBuilder.setMessage("هل تريد حذف هذا المنتج ؟");
                    alertDialogBuilder.setPositiveButton("حذف",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    databaseReference.child(product.pushID).setValue(null);
                                    productsAdapter.list.remove(i);
                                    productsAdapter.notifyDataSetChanged();

                                    Toast.makeText(ProductsActivity.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();

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
                DialogFragment dialog = AddOrEditProductsDialogFragment.newInstance("add", null);
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });
    }
}