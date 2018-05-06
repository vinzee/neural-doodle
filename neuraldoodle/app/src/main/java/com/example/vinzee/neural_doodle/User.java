package com.example.vinzee.neural_doodle;

public class User {
    public String name;
    public String email;
    public String phone;
    public String address;
    public String userType;

    public User(){

    }

    public User(String name, String email, String phone, String address, String userType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.userType = userType;
    }
}
