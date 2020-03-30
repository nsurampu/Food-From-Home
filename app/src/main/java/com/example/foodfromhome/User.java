package com.example.foodfromhome;

public class User {

    private String email;
    private String password;
    private String name;
    private String mobile;
    private String community;

    public User(){

    }

    public User(String email, String password, String name, String mobile, String community) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.mobile = mobile;
        this.community = community;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCommunity() {
        return community;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    @Override
    public String toString() {
        return email + " - " + password + " - " + name + " - " + mobile + " - " + community;
    }

}
