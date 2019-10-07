package com.safwat.abanoub.nsrcompany;

public class Admin {
    public String uid;
    public String username;
    public String password;

    public Admin() {
    }

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Admin(String uid, String username, String password) {
        this.uid = uid;
        this.username = username;
        this.password = password;
    }
}
