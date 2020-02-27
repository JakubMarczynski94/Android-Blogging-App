package com.example.app;

public class UserDetails {
    public String email = "";
    public String password = "";
    public String chatWith = "";

    public UserDetails() {}
    public UserDetails(String email, String password, String chatWith){
        this.email = email;
        this.password = password;
        this.chatWith = chatWith;
    }
}