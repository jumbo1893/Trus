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

public class UserViewModel extends BaseViewModel implements ChangeListener {

    private static final String TAG = "LoginViewModel";

    private FirebaseRepository firebaseRepository;
    private List<User> users;
    private User user;
    private MutableLiveData<Integer> notificationColor = new MutableLiveData<>();
    private PasswordEncryption encryption;



    public void init(User user) {
        this.user = user;
        setNotificationColor(user);
        firebaseRepository = new FirebaseRepository(FirebaseRepository.USER_TABLE, this);
        encryption = new PasswordEncryption();
        if (users == null) {
            users = new ArrayList<>();
            firebaseRepository.loadUsersFromRepository();
            Log.d(TAG, "init: nacitam uživatele");
        }

    }

    private String checkNewPassword(final String password) {
        Validator validator = new Validator();
        if (!validator.fieldIsNotEmpty(password)) {
            return ("Není vyplněné heslo");
        }
        else if (!validator.checkPasswordFormat(password)) {
            return ("Heslo musí mít mezi dýlku 1 až 30 znaků, tak nevymejšlej píčoviny");
        }
        return null;
    }

    boolean editUserPasswordInRepository(final String password) {
        isUpdating.setValue(true);
        PasswordEncryption encryption = new PasswordEncryption();
        try {
            user.setPassword(encryption.hashPassword(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            firebaseRepository.editModel(user);
        } catch (Exception e) {
            alert.setValue("Nastala nějaká kokotina při ukládání změny hesla do db, tak se na to teď vyser");
            isUpdating.setValue(false);
            return false;
        }

        return true;
    }

    void changeColorOfUserInRepository() {
        isUpdating.setValue(true);
        user.setRandomCharColor();
        try {
            firebaseRepository.editModel(user);
        } catch (Exception e) {
            isUpdating.setValue(false);
            alert.setValue("Nastala nějaká kokotina při změně barvičky v db, tak se na to teď vyser");
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

    private void checkIfCurrentUserIsChanged(List<User> userList) {
        for (User user : userList) {
            if (user.equals(this.user)) {
                this.user = user;
                setNotificationColor(user);
            }
        }
    }

    private void setNotificationColor(User user) {
        notificationColor.setValue(user.getCharColor());
    }

    public LiveData<Integer> getNotificationColor() {
        return notificationColor;
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
        users = models;
        checkIfCurrentUserIsChanged(models);
    }

    @Override
    public void alertSent(String message) {

    }
}
