package com.safwat.abanoub.nsrcompany;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class AddOrEditProductsDialogFragment extends DialogFragment {

    TextView dialogTitle;
    EditText title, price;
    ImageView imageView;
    Button add_btn;

    String usingType;
    Product oldProduct;
    Uri selectedImageUri;
    private static final int RC_PHOTO_PICKER = 2;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;


    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static AddOrEditProductsDialogFragment newInstance(String usingType, Product oldProduct) {

        AddOrEditProductsDialogFragment f = new AddOrEditProductsDialogFragment();
        f.usingType = usingType;
        f.oldProduct = oldProduct;
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.add_product_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product_dialog, container, false);

        dialogTitle = view.findViewById(R.id.dialogTitle);
        title = view.findViewById(R.id.title);
        price = view.findViewById(R.id.price);
        imageView = view.findViewById(R.id.image);
        add_btn = (Button) view.findViewById(R.id.add_btn);

        if (usingType.equals("edit") && oldProduct != null) {
            dialogTitle.setText("تعديل المنتج");
            add_btn.setText("تعديل");

            if (oldProduct.image != null)
                Glide.with(getContext())
                        .load(oldProduct.image)
                        .into(imageView);

            title.setText(oldProduct.title);
            price.setText(oldProduct.price);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Define Needed Permissions for android Marshmallow and higher
            // The request code used in ActivityCompat.requestPermissions()
            // and returned in the Activity's onRequestPermissionsResult()
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };

            if(!Utilities.hasPermissions(getContext(), PERMISSIONS)){
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Products");
        firebaseStorage = FirebaseStorage.getInstance();

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(price.getText()) == false)
                    add_btn.setEnabled(true);
                else
                    add_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(title.getText()) == false)
                    add_btn.setEnabled(true);
                else
                    add_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(getContext())) {
                    if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(price.getText()))
                        Toast.makeText(getContext(), R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    else {

                        if (selectedImageUri != null) {

                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setTitle("جاري تحميل الصورة");
                            progressDialog.show();

                            //Add file to reference
                            UploadTask uploudTask = storageReference.putFile(selectedImageUri);
                            uploudTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return storageReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {

                                        String profilePicture = task.getResult().toString();

                                        Product product = new Product(title.getText().toString(), price.getText().toString()
                                                , profilePicture);

                                        addProductToDatabase(product);

                                        progressDialog.dismiss(); //Dimiss progressDialog when success
                                        dismiss(); //Dismiss AddOrEditProductsDialogFragment
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    //Dimiss progressDialog when error
                                    progressDialog.dismiss();
                                    //Display err toast msg
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            uploudTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                    //Show upload progress
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    progressDialog.setMessage("تم تحميل " + (int) progress + "%");
                                }
                            });

                        } else {
                            Product product;

                            if (usingType.equals("edit") && oldProduct != null)
                                product = new Product(title.getText().toString(), price.getText().toString(), oldProduct.image);
                            else
                                product = new Product(title.getText().toString(), price.getText().toString());

                            addProductToDatabase(product);
                            dismiss(); //Dismiss AddOrEditProductsDialogFragment
                        }
                    }
                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void addProductToDatabase(Product product) {
        if (usingType.equals("add")) {

            String pushID = databaseReference.push().getKey();
            databaseReference.child(pushID).setValue(product);

            Toast.makeText(getContext(), "تم اضافة منتج جديد", Toast.LENGTH_SHORT).show();

        } else if (usingType.equals("edit") && product != null) {
            databaseReference.child(oldProduct.pushID).setValue(product);

            Toast.makeText(getContext(), "تم تعديل المنتج بنجاح", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            List<String> pathSegments = data.getData().getPathSegments();
            String[] pathSegmentsArr = (pathSegments.get(pathSegments.size() - 1)).toString().split("/");
            storageReference = firebaseStorage.getReference().child("ProductsPictures")
                    .child(pathSegmentsArr[pathSegmentsArr.length-1]);

            selectedImageUri = data.getData();

            if (selectedImageUri != null)
                Glide.with(getContext())
                        .load(selectedImageUri)
                        .into(imageView);
        }
    }
}
