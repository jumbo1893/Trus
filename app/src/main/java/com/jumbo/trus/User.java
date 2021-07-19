package com.jumbo.trus;

import com.jumbo.trus.Model;

public class User extends Model {

    private String password;
    private long registrationDate;


    public User(String name, String password) {
        super(name);
        this.password = password;
    }

    public User(String name) {
        super(name);
    }

    public User() {
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }
}
