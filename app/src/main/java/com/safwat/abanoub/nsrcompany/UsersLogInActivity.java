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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UsersLogInActivity extends AppCompatActivity {

    EditText fullname, mobile, verificationCode;
    Button sendVerification_code_btn, login_btn;
    TextView loginAsAdmin_link;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codeSent;

    SharedPreferences.Editor editor;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_log_in);

        fullname = findViewById(R.id.fullname);
        mobile = findViewById(R.id.mobile);
        verificationCode = findViewById(R.id.verificationCode);
        sendVerification_code_btn = findViewById(R.id.sendVerification_code_btn);
        login_btn = findViewById(R.id.usersLogin_btn);
        loginAsAdmin_link = findViewById(R.id.loginAsAdmin_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("en");
        firebaseDatabase = FirebaseDatabase.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     User action.
                Log.d("TAG", "onVerificationCompleted:" + credential);
                Toast.makeText(UsersLogInActivity.this, "اكتمل التحقق", Toast.LENGTH_SHORT).show();

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(UsersLogInActivity.this, "فشل التحقق" + "\n" + e, Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(UsersLogInActivity.this, "فشل التحقق" + "\n" + e, Toast.LENGTH_LONG).show();
                }

                // Show a message and update the UI
                // ...
                Toast.makeText(UsersLogInActivity.this, "فشل التحقق" + "\n" + e, Toast.LENGTH_LONG).show();
                verificationCode.setEnabled(false);
                login_btn.setEnabled(false);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the User to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);
                Toast.makeText(UsersLogInActivity.this, "تم ارسال كود التحقق", Toast.LENGTH_SHORT).show();

                // Save verification ID and resending token so we can use them later
                codeSent = verificationId;

                verificationCode.setEnabled(true);
                login_btn.setEnabled(true);
                fullname.setEnabled(false);
                mobile.setEnabled(false);
            }
        };

        sendVerification_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isNetworkAvailable(UsersLogInActivity.this)) {

                    String fullName = fullname.getText().toString();
                    String phoneNumber = mobile.getText().toString();

                    if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber)) {
                        Toast.makeText(UsersLogInActivity.this, R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();

                        checkFullNameIfEmpty(fullName);

                        checkPhoneNumberIfEmptyOrNotEqualElevenNumbers(phoneNumber);

                    } else {
                        sendVerificationCode();
                    }
                } else
                    Toast.makeText(UsersLogInActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeSent != null)
                    verifySignInCodeAndSignInIfTrue();
            }
        });

        loginAsAdmin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UsersLogInActivity.this, AdminLogInActivity.class));
            }
        });
    }

    private void verifySignInCodeAndSignInIfTrue() {
        String code = verificationCode.getText().toString();

        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast.makeText(this, "Invalid Credentials\n"+e , Toast.LENGTH_SHORT).show();
        }
    }

    private void sendVerificationCode() {

        String phoneNumber = mobile.getText().toString();

        checkPhoneNumberIfEmptyOrNotEqualElevenNumbers(phoneNumber);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+20" + phoneNumber,        // Phone number to verify
                60,                       // Timeout duration
                TimeUnit.SECONDS,           // Unit of timeout
                UsersLogInActivity.this,               // Activity (for callback binding)
                mCallbacks);                // OnVerificationStateChangedCallbacks
    }

    private void checkFullNameIfEmpty(String fullName) {
        if (TextUtils.isEmpty(fullName))
            fullname.setError("يجب ادخال الاسم");
    }

    private void checkPhoneNumberIfEmptyOrNotEqualElevenNumbers(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            mobile.setError("يجب ادخال رقم الهاتف");
            return;

        } else if (phoneNumber.length() != 11) {
            mobile.setError("رقم الهاتف غير صحيح");
            return;
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d("TAG", "signInWithCredential:success");

                            final String uid = task.getResult().getUser().getUid();
                            
                            databaseReference=firebaseDatabase.getReference().child("Users").child(uid);
                            valueEventListener=new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    
                                    final User old_user =Utilities.getUser(dataSnapshot);
                                    
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {

                                                    if (task.isSuccessful()) {
                                                        // Get new Instance ID token
                                                        String notifications_token = task.getResult().getToken();

                                                        User new_user;

                                                        if(old_user!=null)
                                                            new_user = new User(fullname.getText().toString(),
                                                                mobile.getText().toString(), notifications_token
                                                                ,old_user.points);
                                                        else
                                                            new_user = new User(fullname.getText().toString(),
                                                                    mobile.getText().toString(), notifications_token
                                                                    ,"0");

                                                        databaseReference.removeEventListener(valueEventListener);
                                                        databaseReference.setValue(new_user);

                                                        Toast.makeText(getApplicationContext(), "تم تسجيل الدخول", Toast.LENGTH_LONG).show();
                                                        setupSharedPreferences();
                                                    }
                                                }
                                            });           
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            databaseReference.addValueEventListener(valueEventListener);
                            
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "خظأ في كود التحقق", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void setupSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //editing into shared preference
        editor = sharedPreferences.edit();
        editor.putString("userType", "User");
        editor.apply();

        openMainActivity();
    }

    private void openMainActivity() {

        Intent intent = new Intent(UsersLogInActivity.this, MainActivity.class);
        startActivity(intent);
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
