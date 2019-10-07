package com.safwat.abanoub.nsrcompany;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLogInActivity extends AppCompatActivity {

    EditText username, password;
    Button login_btn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.loginAsAdmin_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("en");
        firebaseDatabase = FirebaseDatabase.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utilities.isNetworkAvailable(AdminLogInActivity.this)) {

                    final String username_str = username.getText().toString();
                    final String password_str = password.getText().toString();

                    if (TextUtils.isEmpty(username_str) || TextUtils.isEmpty(password_str)) {
                        Toast.makeText(AdminLogInActivity.this, R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();

                        checkUsernameIfEmpty(username_str);

                        checkPasswordIfEmpty(password_str);
                    } else {
                        String email = username_str + "@nsrcompany.com";
                        firebaseAuth.signInWithEmailAndPassword(email, password_str)
                                .addOnCompleteListener(AdminLogInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            final String uid = task.getResult().getUser().getUid();
                                            FirebaseInstanceId.getInstance().getInstanceId()
                                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                                                            if (task.isSuccessful()) {
                                                                // Get new Instance ID token
                                                                String notifications_token = task.getResult().getToken();

                                                                //updating notifications_token to the new logged in device
                                                                databaseReference = firebaseDatabase.getReference().child("Admins").child(uid)
                                                                        .child("notifications_token");
                                                                databaseReference.setValue(notifications_token);

                                                                Toast.makeText(getApplicationContext(), "تم تسجيل الدخول", Toast.LENGTH_LONG).show();
                                                                setupSharedPreferences();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // If sign in fails, display a message to the User.
                                            Toast.makeText(AdminLogInActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else
                    Toast.makeText(AdminLogInActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUsernameIfEmpty(String username_str) {
        if (TextUtils.isEmpty(username_str))
            username.setError("يجب ادخال اسم المستخدم");
    }

    private void checkPasswordIfEmpty(String password_str) {
        if (TextUtils.isEmpty(password_str))
            password.setError("يجب ادخال كلمة المرور");
        else if (password_str.length() < 6)
            password.setError("كلمة المرور يجب ان تكون مكونة من 6 احرف او اكثر");
    }

    private void setupSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //editing into shared preference
        editor = sharedPreferences.edit();
        editor.putString("userType", "Admin");
        editor.apply();

        openMainActivity();
    }

    private void openMainActivity() {

        Intent intent = new Intent(AdminLogInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}




