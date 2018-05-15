package com.example.vinzee.neural_doodle;

import java.io.Serializable;

public class User implements Serializable {
    public String uID;
    public String name;
    public String email;
    public String phone;
    public String address;
    public String userType;
    public String userBio;
    String icon;
    //public String interests;

    public User(){
    }

    public User( String name, String email, String phone, String address, String userType, String userBio/*, String interests*/) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.userType = userType;
        this.userBio = userBio;
        //this.interests = interests;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", userType='" + userType + '\'' +
                ", userBio='" + userBio + '\'' +
                /*", interests='" + interests + '\'' +*/
                '}';
    }
}
