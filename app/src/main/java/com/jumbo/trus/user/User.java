package com.jumbo.trus.user;

import android.graphics.Color;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;

import java.util.Random;

public class User extends Model {

    private String password;
    private long registrationDate;
    private int charColor;
    private Permission permission;
    private Status status;

    public enum Permission {
        ADMIN,
        USER,
        READ_ONLY
    }

    public enum Status {
        APPROVED,
        WAITING_FOR_APPROVE,
        DENIED,
        FORGOTTEN_PASSWORD,
        PASSWORD_RESET
    }

    public User(String name, String password, Permission permission, Status status) {
        super(name);
        this.password = password;
        this.permission = permission;
        this.status = status;
        setRandomCharColor();
        registrationDate = System.currentTimeMillis();
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

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String returnRegistrationDateInStringFormat() {
        Date date = new Date();
        return date.convertMillisToStringTimestamp(registrationDate);
    }

    @Override
    public String toString() {
        return "User{" +
                "registrationDate=" + registrationDate +
                ", charColor=" + charColor +
                ", permission=" + permission +
                ", status=" + status +
                '}';
    }
}
