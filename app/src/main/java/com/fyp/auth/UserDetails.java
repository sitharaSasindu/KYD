package com.fyp.auth;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDetails {

    public String name;
    public String mobile;
    public String email;
    public String role;
    public String publicKey;

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getEncryptedPvtKey() {
        return encryptedPvtKey;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String encryptedPvtKey;
    public String userStatus;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public UserDetails() {
    }

    public UserDetails(String name, String email, String mobile, String role, String publicKey,String pvtKey, String status) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.publicKey = publicKey;
this.encryptedPvtKey= pvtKey;
this.userStatus =status;
    }
}