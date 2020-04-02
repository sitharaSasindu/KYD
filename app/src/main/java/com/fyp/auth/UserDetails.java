package com.fyp.auth;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDetails {

    public String name;
    public String mobile;
    public String email;
    public String role;
    public String publicKey;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public UserDetails() {
    }

    public UserDetails(String name, String email, String mobile, String role, String publicKey) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.publicKey = publicKey;

    }
}