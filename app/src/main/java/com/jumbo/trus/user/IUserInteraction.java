package com.jumbo.trus.user;

public interface IUserInteraction {
    boolean approveNewUser(User user, boolean approve);
    boolean resetPassword(User user);
    boolean changeUserStatus(User user, User.Status status);
    boolean changeUserPermission(User user, User.Permission permission);
}
