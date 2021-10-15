package com.codetree.contact;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String username;
    public String img;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String img) {
        this.username = username;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public String getUsername() {
        return username;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("img", img);

        return result;
    }
}
