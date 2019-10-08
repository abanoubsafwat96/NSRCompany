package com.safwat.abanoub.nsrcompany;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String uid;
    public String fullname;
    public String phoneNumber;
    public String notifications_token;
    public String points;

    public User() {
    }

    public User(String fullname, String phoneNumber, String notifications_token,String points) {
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.notifications_token=notifications_token;
        this.points=points;
    }

    protected User(Parcel in) {
        uid = in.readString();
        fullname = in.readString();
        phoneNumber = in.readString();
        notifications_token=in.readString();
        points=in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(fullname);
        parcel.writeString(phoneNumber);
        parcel.writeString(notifications_token);
        parcel.writeString(points);
    }
}
