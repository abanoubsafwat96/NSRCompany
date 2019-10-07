package com.safwat.abanoub.nsrcompany;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity {

    LinearLayout productsLinear;
    LinearLayout saleLinear;
    LinearLayout orderLinear;
    LinearLayout ratesLinear;
    LinearLayout previousOrdersLinear;
    LinearLayout pointsLinear;
    LinearLayout contactUsLinear;
    LinearLayout changePasswordLinear;
    LinearLayout logOutLinear;
    ImageView productsImage;
    ImageView saleImage;
    ImageView orderImage;
    ImageView ratesImage;
    ImageView previousOrdersImage;
    ImageView pointsImage;
    ImageView contactUsImage;
    ImageView changePasswordImage;
    ImageView logOutImage;

    String userType;

    SharedPreferences sharedPreferences;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        userType = sharedPreferences.getString("userType", "");

        if (userType.equals("User"))
            setContentView(R.layout.activity_user_main);
        else if (userType.equals("Admin")) {
            setContentView(R.layout.activity_admin_main);
        }

        if (!Utilities.isNetworkAvailable(this))
            showNetworkErrorToast();

        productsLinear = findViewById(R.id.productsLinear);
        saleLinear = findViewById(R.id.saleLinear);
        orderLinear = findViewById(R.id.orderLinear);
        ratesLinear = findViewById(R.id.ratesLinear);
        previousOrdersLinear = findViewById(R.id.previousOrderLinear);
        pointsLinear = findViewById(R.id.pointsLinear);
        contactUsLinear = findViewById(R.id.contactUsLinear);
        changePasswordLinear = findViewById(R.id.changePasswordLinear);
        logOutLinear = findViewById(R.id.logOutLinear);
        productsImage = findViewById(R.id.productsImage);
        saleImage = findViewById(R.id.saleImage);
        orderImage = findViewById(R.id.orderImage);
        ratesImage = findViewById(R.id.ratesImage);
        previousOrdersImage = findViewById(R.id.previousOrderImage);
        pointsImage = findViewById(R.id.pointsImage);
        contactUsImage = findViewById(R.id.contactUsImage);
        changePasswordImage = findViewById(R.id.changePasswordImage);
        logOutImage = findViewById(R.id.signOutImage);

        productsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductsActivity();
            }
        });

        saleLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSaleActivity();
            }
        });


        previousOrdersLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPreviousOrdersActivity();
            }
        });

        ratesLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRatesActivity();
            }
        });

        if (userType.equals("User")) {
            orderLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOrderActivity();
                }
            });

            orderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOrderActivity();
                }
            });
        }

        pointsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPointsActivity();
            }
        });

        contactUsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactUsActivity();
            }
        });


        logOutLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        productsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductsActivity();
            }
        });

        saleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSaleActivity();
            }
        });

        ratesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRatesActivity();
            }
        });

        previousOrdersImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPreviousOrdersActivity();
            }
        });

        pointsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPointsActivity();
            }
        });

        contactUsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactUsActivity();
            }
        });

        if (userType.equals("Admin")) {
            changePasswordLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openChangePasswordDialoug();
                }
            });

            changePasswordImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openChangePasswordDialoug();
                }
            });
        }

        logOutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void showNetworkErrorToast() {
        Toast.makeText(this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
    }

    private void openChangePasswordDialoug() {
        if (Utilities.isNetworkAvailable(this)) {

            DialogFragment dialog = ChangePasswordDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "tag");

        } else
            showNetworkErrorToast();
    }

    private void openProductsActivity() {
        if (Utilities.isNetworkAvailable(this)) {

            Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
        } else
            showNetworkErrorToast();
    }

    private void openSaleActivity() {
        if (Utilities.isNetworkAvailable(this)) {

            Intent intent = new Intent(MainActivity.this, SaleActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);

        } else
            showNetworkErrorToast();
    }

    private void openOrderActivity() {
        if (Utilities.isNetworkAvailable(this)) {

            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
        } else
            showNetworkErrorToast();
    }

    private void openRatesActivity() {
        if (Utilities.isNetworkAvailable(this)) {

            Intent intent;
            if (userType.equals("Admin"))
                intent = new Intent(MainActivity.this, AdminRatesActivity.class);
            else {
                intent = new Intent(MainActivity.this, RatesActivity.class);
                intent.putExtra("userType", userType);
            }
            startActivity(intent);
        } else
            showNetworkErrorToast();
    }

    private void openPreviousOrdersActivity() {
        if (Utilities.isNetworkAvailable(this)) {

            Intent intent = new Intent(MainActivity.this, PreviousOrdersActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
        } else
            showNetworkErrorToast();
    }

    private void openPointsActivity() {
        if (Utilities.isNetworkAvailable(this)) {

            Intent intent = new Intent(MainActivity.this, PointsActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
        } else
            showNetworkErrorToast();
    }

    private void openContactUsActivity() {
        if (Utilities.isNetworkAvailable(this)) {

            Intent intent = new Intent(MainActivity.this, ContactUsActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
        } else
            showNetworkErrorToast();
    }

    private void logOut() {

        if (Utilities.isNetworkAvailable(this)) {

            //editing into shared preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userType", "no users");
            editor.apply();

            String uid = Utilities.getCurrentUID();

            //deleting notifications_token from database when logging out
            if (userType.equals("Admin"))
                databaseReference = firebaseDatabase.getReference().child("Admins").child(uid).child("notifications_token");
            else
                databaseReference = firebaseDatabase.getReference().child("Users").child(uid).child("notifications_token");

            databaseReference.setValue(null);

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, UsersLogInActivity.class));
            MainActivity.this.finish();
        } else
            showNetworkErrorToast();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //exit app
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}