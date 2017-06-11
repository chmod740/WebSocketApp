package me.hupeng.websocketapp.bean;


import com.google.gson.Gson;

public class User {

    private int id;

    private String username;

    private String password;

    private String salt;

    private String accessKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    private static User currentUser = null;

    public static void setCurrentUser(User user){
        User.currentUser = user;
    }

    public static void setCurrentUser(String userStr){
        try {
            User.currentUser = new Gson().fromJson(userStr, User.class);
        }catch (Exception e){
            
        }
    }

    public static User getCurrentUser(){
        return User.currentUser;
    }
}
