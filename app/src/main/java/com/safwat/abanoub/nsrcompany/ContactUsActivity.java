package com.safwat.abanoub.nsrcompany;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ContactUsActivity extends AppCompatActivity {

    TextView phoneNumber,email;
    EditText phoneNumber_ED,email_ED;
    FloatingActionButton edit_fab,done_fab;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle("اتصل بنا");

        userType=getIntent().getStringExtra("userType");

        phoneNumber=findViewById(R.id.mobileNumber);
        email=findViewById(R.id.email);
        phoneNumber_ED=findViewById(R.id.mobileNumberEditText);
        email_ED=findViewById(R.id.emailEditText);
        edit_fab=findViewById(R.id.edit_fab);
        done_fab=findViewById(R.id.done_fab);

        if (userType.equals("Admin")){
            edit_fab.setVisibility(View.VISIBLE);
        }

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("ContactUs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ContactUsItem contactUsItem=Utilities.getContactUsItem(dataSnapshot);

                if (contactUsItem!=null){
                    phoneNumber.setText(contactUsItem.phoneNumber);
                    email.setText(contactUsItem.email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done_fab.setVisibility(View.VISIBLE);
                edit_fab.setVisibility(View.GONE);

                phoneNumber.setVisibility(View.GONE);
                phoneNumber_ED.setVisibility(View.VISIBLE);
                phoneNumber_ED.setText(phoneNumber.getText());
                email.setVisibility(View.GONE);
                email_ED.setVisibility(View.VISIBLE);
                email_ED.setText(email.getText());
            }
        });


        done_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_fab.setVisibility(View.VISIBLE);
                done_fab.setVisibility(View.GONE);

                phoneNumber_ED.setVisibility(View.GONE);
                phoneNumber.setVisibility(View.VISIBLE);
                phoneNumber.setText(phoneNumber_ED.getText());
                email_ED.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                email.setText(email_ED.getText());

                ContactUsItem contactUsItem=new ContactUsItem(phoneNumber_ED.getText().toString(),email_ED.getText().toString());
                databaseReference.setValue(contactUsItem);

                Toast.makeText(ContactUsActivity.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
