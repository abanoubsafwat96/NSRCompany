package com.safwat.abanoub.nsrcompany;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;

class Utilities {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }

    public static String getCurrentUID() {
        return getCurrentUser().getUid();
    }

    public static String getCurrentUsername() {
        return getCurrentEmail().replace("@nsrcompany.com", "");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static ArrayList<SaleItem> getAllSaleItems(DataSnapshot dataSnapshot) {

        ArrayList<SaleItem> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                SaleItem saleItem = child.getValue(SaleItem.class);
                saleItem.pushID = child.getKey();

                list.add(saleItem);
            }
        }
        return list;
    }

    public static ContactUsItem getContactUsItem(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {
            ContactUsItem contactUsItem = dataSnapshot.getValue(ContactUsItem.class);
            return contactUsItem;
        } else
            return null;
    }

    public static PointsItem getPointsItems(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {
            PointsItem pointsItem = dataSnapshot.getValue(PointsItem.class);
            return pointsItem;
        } else
            return null;
    }

    public static ArrayList<Product> getAllProducts(DataSnapshot dataSnapshot) {
        ArrayList<Product> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Product product = child.getValue(Product.class);
                product.pushID = child.getKey();

                list.add(product);
            }
        }
        return list;
    }

    public static ArrayList<String> getUIDs(DataSnapshot dataSnapshot) {
        ArrayList<String> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                list.add(child.getKey());
            }
        }
        return list;
    }

    public static String getValueIfNotNull(DataSnapshot dataSnapshot) {
        String value = null;

        if (dataSnapshot.getValue() != null) {
            value = dataSnapshot.getValue().toString();
        }
        return value;
    }

    public static ArrayList<String> getProductsPushIDs(DataSnapshot dataSnapshot) {
        ArrayList<String> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                String key = child.getKey();
                if (!key.equals("comment"))
                    list.add(child.getKey());
            }
        }
        return list;
    }

    //method to calculate the height of listview inside scrollview to show full list without need scrolling
    public static void getTotalHeightofListView(ListView listView,int extraSpace) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        params.height += extraSpace;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static ArrayList<OrderItem> getOrderItems(DataSnapshot dataSnapshot) {

        ArrayList<OrderItem> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                String key=child.getKey();
                if (!key.equals("status")&&!key.equals("discount")) {
                    OrderItem orderItem = child.getValue(OrderItem.class);
                    orderItem.pushID = key;

                    list.add(orderItem);
                }
            }
        }
        return list;
    }

    public static Product getProduct(DataSnapshot dataSnapshot) {
        Product product;

        if (dataSnapshot.getValue() != null) {
             product =dataSnapshot.getValue(Product.class);
             product.pushID=dataSnapshot.getKey();

             return product;
        }
        return null;
    }

    public static User getUser(DataSnapshot dataSnapshot) {
        User user = null;

        if (dataSnapshot.getValue() != null) {
            user = dataSnapshot.getValue(User.class);
            user.uid = dataSnapshot.getKey();
        }
        return user;
    }

    public static ArrayList<String> reverseStringsList(ArrayList<String> list) {
        ArrayList<String> reversed_list = new ArrayList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            reversed_list.add(list.get(i));
        }

        return reversed_list;
    }

    public static ArrayList<Product> reverseProdustsList(ArrayList<Product> list) {
        ArrayList<Product> reversed_list = new ArrayList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            reversed_list.add(list.get(i));
        }

        return reversed_list;
    }

    public static ArrayList<SaleItem> reverseSaleItemsList(ArrayList<SaleItem> list) {
        ArrayList<SaleItem> reversed_list = new ArrayList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            reversed_list.add(list.get(i));
        }

        return reversed_list;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
