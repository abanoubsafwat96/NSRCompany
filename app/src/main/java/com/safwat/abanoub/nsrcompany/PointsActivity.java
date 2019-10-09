package com.safwat.abanoub.nsrcompany;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PointsActivity extends AppCompatActivity {

    TextView line1Number1, line1Number2, line1Number3, line2Number1, line2Number2, line2Number3, line3Number1
            , line3Number2, line3Number3, line4Number1, line4Number2, line4Number3, line5Number1, line5Number2
            , line5Number3,raseed, points, nokta,pointPrice;
    EditText line1Number1_ED, line1Number2_ED, line1Number3_ED, line2Number1_ED, line2Number2_ED, line2Number3_ED,
            line3Number1_ED, line3Number2_ED, line3Number3_ED, line4Number1_ED, line4Number2_ED, line4Number3_ED,
            line5Number1_ED, line5Number2_ED, line5Number3_ED,pointPrice_ED;
    FloatingActionButton edit_fab, done_fab;
    Button pointsNotify_btn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, pointsReference;

    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("النقاط");

        userType = getIntent().getStringExtra("userType");

        line1Number1 = findViewById(R.id.line1Number1);
        line1Number2 = findViewById(R.id.line1Number2);
        line1Number3 = findViewById(R.id.line1Number3);
        line2Number1 = findViewById(R.id.line2Number1);
        line2Number2 = findViewById(R.id.line2Number2);
        line2Number3 = findViewById(R.id.line2Number3);
        line3Number1 = findViewById(R.id.line3Number1);
        line3Number2 = findViewById(R.id.line3Number2);
        line3Number3 = findViewById(R.id.line3Number3);
        line4Number1 = findViewById(R.id.line4Number1);
        line4Number2 = findViewById(R.id.line4Number2);
        line4Number3 = findViewById(R.id.line4Number3);
        line5Number1 = findViewById(R.id.line5Number1);
        line5Number2 = findViewById(R.id.line5Number2);
        line5Number3 = findViewById(R.id.line5Number3);
        line1Number1_ED = findViewById(R.id.line1Number1_ED);
        line1Number2_ED = findViewById(R.id.line1Number2_ED);
        line1Number3_ED = findViewById(R.id.line1Number3_ED);
        line2Number1_ED = findViewById(R.id.line2Number1_ED);
        line2Number2_ED = findViewById(R.id.line2Number2_ED);
        line2Number3_ED = findViewById(R.id.line2Number3_ED);
        line3Number1_ED = findViewById(R.id.line3Number1_ED);
        line3Number2_ED = findViewById(R.id.line3Number2_ED);
        line3Number3_ED = findViewById(R.id.line3Number3_ED);
        line4Number1_ED = findViewById(R.id.line4Number1_ED);
        line4Number2_ED = findViewById(R.id.line4Number2_ED);
        line4Number3_ED = findViewById(R.id.line4Number3_ED);
        line5Number1_ED = findViewById(R.id.line5Number1_ED);
        line5Number2_ED = findViewById(R.id.line5Number2_ED);
        line5Number3_ED = findViewById(R.id.line5Number3_ED);
        raseed = findViewById(R.id.raseed);
        points = findViewById(R.id.points);
        nokta = findViewById(R.id.nokta);
        edit_fab = findViewById(R.id.edit_fab);
        done_fab = findViewById(R.id.done_fab);
        pointPrice= findViewById(R.id.pointPrice);
        pointPrice_ED= findViewById(R.id.pointsPrice_ED);
        pointsNotify_btn= findViewById(R.id.pointsNotify_btn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Points");

        if (userType.equals("Admin")) {

            edit_fab.setVisibility(View.VISIBLE);
            pointsNotify_btn.setVisibility(View.VISIBLE);

        } else if (userType.equals("User")) {

            raseed.setVisibility(View.VISIBLE);
            points.setVisibility(View.VISIBLE);
            nokta.setVisibility(View.VISIBLE);

            pointsReference = firebaseDatabase.getReference().child("Users").child(Utilities.getCurrentUID()).child("points");
            pointsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String currentPoints = Utilities.getValueIfNotNull(dataSnapshot);

                    if (currentPoints != null)
                        points.setText(currentPoints);
                    else
                        points.setText("0");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PointsItem pointsItem = Utilities.getPointsItems(dataSnapshot);

                if (pointsItem != null) {
                    line1Number1.setText(pointsItem.line1Number1);
                    line1Number2.setText(pointsItem.line1Number2);
                    line1Number3.setText(pointsItem.line1Number3);
                    line2Number1.setText(pointsItem.line2Number1);
                    line2Number2.setText(pointsItem.line2Number2);
                    line2Number3.setText(pointsItem.line2Number3);
                    line3Number1.setText(pointsItem.line3Number1);
                    line3Number2.setText(pointsItem.line3Number2);
                    line3Number3.setText(pointsItem.line3Number3);
                    line4Number1.setText(pointsItem.line4Number1);
                    line4Number2.setText(pointsItem.line4Number2);
                    line4Number3.setText(pointsItem.line4Number3);
                    line5Number1.setText(pointsItem.line5Number1);
                    line5Number2.setText(pointsItem.line5Number2);
                    line5Number3.setText(pointsItem.line5Number3);
                    pointPrice.setText(pointsItem.pointPrice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pointsNotify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference pointsNotify_ref=firebaseDatabase.getReference().child("pointsNotify");
                String randomkey=pointsNotify_ref.push().getKey();
                pointsNotify_ref.child("randomkey").setValue(randomkey);
                Toast.makeText(PointsActivity.this, "تم ارسال تذكيرا لكل المستخدمين", Toast.LENGTH_SHORT).show();
            }
        });

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done_fab.setVisibility(View.VISIBLE);
                edit_fab.setVisibility(View.GONE);

                line1Number1.setVisibility(View.GONE);
                line1Number2.setVisibility(View.GONE);
                line1Number3.setVisibility(View.GONE);
                line2Number1.setVisibility(View.GONE);
                line2Number2.setVisibility(View.GONE);
                line2Number3.setVisibility(View.GONE);
                line3Number1.setVisibility(View.GONE);
                line3Number2.setVisibility(View.GONE);
                line3Number3.setVisibility(View.GONE);
                line4Number1.setVisibility(View.GONE);
                line4Number2.setVisibility(View.GONE);
                line4Number3.setVisibility(View.GONE);
                line5Number1.setVisibility(View.GONE);
                line5Number2.setVisibility(View.GONE);
                line5Number3.setVisibility(View.GONE);
                pointPrice.setVisibility(View.GONE);

                line1Number1_ED.setText(line1Number1.getText());
                line1Number2_ED.setText(line1Number2.getText());
                line1Number3_ED.setText(line1Number3.getText());
                line2Number1_ED.setText(line2Number1.getText());
                line2Number2_ED.setText(line2Number2.getText());
                line2Number3_ED.setText(line2Number3.getText());
                line3Number1_ED.setText(line3Number1.getText());
                line3Number2_ED.setText(line3Number2.getText());
                line3Number3_ED.setText(line3Number3.getText());
                line4Number1_ED.setText(line4Number1.getText());
                line4Number2_ED.setText(line4Number2.getText());
                line4Number3_ED.setText(line4Number3.getText());
                line5Number1_ED.setText(line5Number1.getText());
                line5Number2_ED.setText(line5Number2.getText());
                line5Number3_ED.setText(line5Number3.getText());
                pointPrice_ED.setText(pointPrice.getText());

                line1Number1_ED.setVisibility(View.VISIBLE);
                line1Number2_ED.setVisibility(View.VISIBLE);
                line1Number3_ED.setVisibility(View.VISIBLE);
                line2Number1_ED.setVisibility(View.VISIBLE);
                line2Number2_ED.setVisibility(View.VISIBLE);
                line2Number3_ED.setVisibility(View.VISIBLE);
                line3Number1_ED.setVisibility(View.VISIBLE);
                line3Number2_ED.setVisibility(View.VISIBLE);
                line3Number3_ED.setVisibility(View.VISIBLE);
                line4Number1_ED.setVisibility(View.VISIBLE);
                line4Number2_ED.setVisibility(View.VISIBLE);
                line4Number3_ED.setVisibility(View.VISIBLE);
                line5Number1_ED.setVisibility(View.VISIBLE);
                line5Number2_ED.setVisibility(View.VISIBLE);
                line5Number3_ED.setVisibility(View.VISIBLE);
                pointPrice_ED.setVisibility(View.VISIBLE);
            }
        });

        done_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_fab.setVisibility(View.VISIBLE);
                done_fab.setVisibility(View.GONE);

                line1Number1_ED.setVisibility(View.GONE);
                line1Number2_ED.setVisibility(View.GONE);
                line1Number3_ED.setVisibility(View.GONE);
                line2Number1_ED.setVisibility(View.GONE);
                line2Number2_ED.setVisibility(View.GONE);
                line2Number3_ED.setVisibility(View.GONE);
                line3Number1_ED.setVisibility(View.GONE);
                line3Number2_ED.setVisibility(View.GONE);
                line3Number3_ED.setVisibility(View.GONE);
                line4Number1_ED.setVisibility(View.GONE);
                line4Number2_ED.setVisibility(View.GONE);
                line4Number3_ED.setVisibility(View.GONE);
                line5Number1_ED.setVisibility(View.GONE);
                line5Number2_ED.setVisibility(View.GONE);
                line5Number3_ED.setVisibility(View.GONE);
                pointPrice_ED.setVisibility(View.GONE);

                line1Number1.setText(line1Number1_ED.getText());
                line1Number2.setText(line1Number2_ED.getText());
                line1Number3.setText(line1Number3_ED.getText());
                line2Number1.setText(line2Number1_ED.getText());
                line2Number2.setText(line2Number2_ED.getText());
                line2Number3.setText(line2Number3_ED.getText());
                line3Number1.setText(line3Number1_ED.getText());
                line3Number2.setText(line3Number2_ED.getText());
                line3Number3.setText(line3Number3_ED.getText());
                line4Number1.setText(line4Number1_ED.getText());
                line4Number2.setText(line4Number2_ED.getText());
                line4Number3.setText(line4Number3_ED.getText());
                line5Number1.setText(line5Number1_ED.getText());
                line5Number2.setText(line5Number2_ED.getText());
                line5Number3.setText(line5Number3_ED.getText());
                pointPrice.setText(pointPrice_ED.getText());

                line1Number1.setVisibility(View.VISIBLE);
                line1Number2.setVisibility(View.VISIBLE);
                line1Number3.setVisibility(View.VISIBLE);
                line2Number1.setVisibility(View.VISIBLE);
                line2Number2.setVisibility(View.VISIBLE);
                line2Number3.setVisibility(View.VISIBLE);
                line3Number1.setVisibility(View.VISIBLE);
                line3Number2.setVisibility(View.VISIBLE);
                line3Number3.setVisibility(View.VISIBLE);
                line4Number1.setVisibility(View.VISIBLE);
                line4Number2.setVisibility(View.VISIBLE);
                line4Number3.setVisibility(View.VISIBLE);
                line5Number1.setVisibility(View.VISIBLE);
                line5Number2.setVisibility(View.VISIBLE);
                line5Number3.setVisibility(View.VISIBLE);
                pointPrice.setVisibility(View.VISIBLE);

                PointsItem pointsItem = new PointsItem(line1Number1_ED.getText().toString(), line1Number2_ED.getText().toString(),
                        line1Number3_ED.getText().toString(), line2Number1_ED.getText().toString(), line2Number2_ED.getText().toString(),
                        line2Number3_ED.getText().toString(), line3Number1_ED.getText().toString(), line3Number2_ED.getText().toString(),
                        line3Number3_ED.getText().toString(), line4Number1_ED.getText().toString(), line4Number2_ED.getText().toString(),
                        line4Number3_ED.getText().toString(), line5Number1_ED.getText().toString(), line5Number2_ED.getText().toString(),
                        line5Number3_ED.getText().toString(),pointPrice_ED.getText().toString());

                databaseReference.setValue(pointsItem);

                Toast.makeText(PointsActivity.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
