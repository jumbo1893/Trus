package com.jumbo.trus.user;

import com.jumbo.trus.Model;

import java.io.Serializable;

public class User extends Model implements Serializable {

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

    public boolean nameEquals(User user) {
        if (this == user) return true;
        if (user == null || getClass() != user.getClass()) return false;
        return user.getName().equals(this.getName());
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
