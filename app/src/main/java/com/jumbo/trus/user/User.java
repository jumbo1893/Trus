package com.jumbo.trus.user;

import android.graphics.Color;

import com.jumbo.trus.Model;

import java.util.Random;

public class User extends Model {

    private String password;
    private long registrationDate;
    private int charColor;


    public User(String name, String password) {
        super(name);
        this.password = password;
        setRandomCharColor();
    }

    public User(String name, int charColor) {
        super(name);
        this.charColor = charColor;
    }

    public User() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRandomCharColor() {
        Random randomColor = new Random();
        charColor = Color.argb(255, randomColor.nextInt(256), randomColor.nextInt(256), randomColor.nextInt(256));
    }

    public int getCharColor() {
        return charColor;
    }

    public void setCharColor(int charColor) {
        this.charColor = charColor;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }
}
