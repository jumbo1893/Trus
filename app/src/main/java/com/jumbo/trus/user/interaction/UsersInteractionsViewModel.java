package com.jumbo.trus.user.interaction;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.user.PasswordEncryption;
import com.jumbo.trus.user.User;
import com.jumbo.trus.validator.Validator;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UsersInteractionsViewModel extends BaseViewModel implements ChangeListener {

    private static final String TAG = "LoginViewModel";

    private FirebaseRepository firebaseRepository;
    private MutableLiveData<List<User>> waitingForPasswordResetUsers = new MutableLiveData<>();
    private MutableLiveData<List<User>> waitingForApproveUsers = new MutableLiveData<>();
    private MutableLiveData<List<User>> users;



    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.USER_TABLE, this);
        if (users == null) {
            users = new MutableLiveData<>();
            firebaseRepository.loadUsersFromRepository();
            Log.d(TAG, "init: nacitam uživatele");
        }

    }

    boolean changeUserStatus(final User user, User.Status newStatus) {
        isUpdating.setValue(true);
        user.setStatus(newStatus);
        try {
            firebaseRepository.editModel(user);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, sorry, zkus to jindy");
            isUpdating.setValue(false);
            return false;
        }
        alert.setValue("Potvrzuji změnu uživatele na " + newStatus);
        return true;
    }

    boolean changeUserPermission(final User user, User.Permission newPermission) {
        isUpdating.setValue(true);
        user.setPermission(newPermission);
        try {
            firebaseRepository.editModel(user);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, sorry, zkus to jindy");
            isUpdating.setValue(false);
            return false;
        }
        alert.setValue("Přiděluji uživatelovi práva: " + newPermission);
        return true;
    }

    boolean changeUserPermissionAndStatus(final User user, User.Permission newPermission, User.Status newStatus) {
        isUpdating.setValue(true);
        user.setPermission(newPermission);
        user.setStatus(newStatus);
        try {
            firebaseRepository.editModel(user);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, sorry, zkus to jindy");
            isUpdating.setValue(false);
            return false;
        }
        alert.setValue("Potvrzuji změnu uživatele na " + newStatus + " a přiděluji mu práva: " + newPermission);
        return true;
    }

    private void setUsers(List<User> users) {
        List<User> waitingForApproveUsers = new ArrayList<>();
        List<User> waitingForPasswordResetUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getStatus() == User.Status.WAITING_FOR_APPROVE) {
                waitingForApproveUsers.add(user);
            }
            if (user.getStatus() == User.Status.FORGOTTEN_PASSWORD) {
                waitingForPasswordResetUsers.add(user);
            }
        }
        this.waitingForPasswordResetUsers.setValue(waitingForPasswordResetUsers);
        this.waitingForApproveUsers.setValue(waitingForApproveUsers);
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<List<User>> getWaitingForPasswordResetUsers() {
        return waitingForPasswordResetUsers;
    }

    public LiveData<List<User>> getWaitingForApproveUsers() {
        return waitingForApproveUsers;
    }

    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {
        alert.setValue("Upraveny údaje uživatele" + model.getName());
        isUpdating.setValue(false);
    }

    @Override
    public void itemDeleted(Model model) {
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Log.d(TAG, "itemListLoaded: " + models);
        users.setValue(models);
        setUsers(models);
    }

    @Override
    public void alertSent(String message) {

    }
}
