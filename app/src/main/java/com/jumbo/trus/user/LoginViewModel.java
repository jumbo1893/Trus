package com.jumbo.trus.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.graphics.Color;
import android.util.Log;

import com.jumbo.trus.ChangeListener;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.Validator;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

public class LoginViewModel extends ViewModel implements ChangeListener {

    private static final String TAG = "LoginViewModel";

    private FirebaseRepository firebaseRepository;
    private MutableLiveData<List<User>> users;
    private MutableLiveData<User> user;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private PasswordEncryption encryption;

    public void init() {
        firebaseRepository = new FirebaseRepository("user", this);
        encryption = new PasswordEncryption();
        if (users == null) {
            users = new MutableLiveData<>();
            firebaseRepository.loadUsersFromRepository();
            Log.d(TAG, "init: nacitam uživatele");
        }
        if (user == null) {
            user = new MutableLiveData<>();
        }
    }

    private boolean checkNewUserValidation(final String name, final String password) {
        Validator validator = new Validator();
        if (!validator.fieldIsNotEmpty(name)) {
            alert.setValue("Pro registraci musíš vyplnit jméno a heslo");
        }
        else if (!validator.checkFieldFormat(name, 20)) {
            alert.setValue("Uživatelský jméno je moc dlouhý nebo obsahuje nesmysly");
        }
        else if (!validator.fieldIsNotEmpty(password)) {
            alert.setValue("Není vyplněné heslo");
        }
        else if (!validator.checkPasswordFormat(password)) {
            alert.setValue("Heslo musí mít mezi dýlku 1 až 30 znaků, tak nevymejšlej píčoviny");
        }
        else if (checkIfUsernameAlreadyExists(name)) {
            alert.setValue("Uživatel se stejným jménem už existuje");
        }
        /*else if (!validator.fieldIsNotEmpty(mail)) {
            response = "Není vyplněn mail";
        }
        else if (!validator.checkEmailFormat(mail)) {
            response = "Mail není ve správném formátu";
        }*/
        else {
            return true;
        }
        return false;
    }

    private boolean checkNewPassword(final String password) {
        Validator validator = new Validator();
        if (!validator.fieldIsNotEmpty(password)) {
            alert.setValue("Není vyplněné heslo");
        }
        else if (!validator.checkPasswordFormat(password)) {
            alert.setValue("Heslo musí mít mezi dýlku 1 až 30 znaků, tak nevymejšlej píčoviny");
        }
        else {
            return true;
        }
        return false;
    }

    public boolean editUserPasswordInRepository(final String password, final String oldPassword, User user) {
        isUpdating.setValue(true);
        PasswordEncryption encryption = new PasswordEncryption();
        try {
            if (!encryption.compareHashedPassword(user.getPassword(), oldPassword)) {
                isUpdating.setValue(false);
                alert.setValue("Nezadal si stejný heslo");
                return false;
            } else if (!checkNewPassword(password)) {
                isUpdating.setValue(false);
                return false;
            }
            user.setPassword(encryption.hashPassword(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            alert.setValue("Chyba při šifrování, soráč");
            isUpdating.setValue(false);
            return false;
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

    public int changeColorOfUserInRepository(User user) {
        isUpdating.setValue(true);
        user.setRandomCharColor();
        try {
            firebaseRepository.editModel(user);
        } catch (Exception e) {
            isUpdating.setValue(false);
            alert.setValue("Nastala nějaká kokotina při změně barvičky v db, tak se na to teď vyser");
            return 0;
        }
        return user.getCharColor();
    }

    private boolean checkIfUsernameAlreadyExists(String name) {
        for (User user : Objects.requireNonNull(users.getValue())) {
            if (user.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    boolean loginWithUsernameAndPassword(String name, String password) {
        isUpdating.setValue(true);
        for (User user : Objects.requireNonNull(getUsers().getValue())) {
            try {
                if (name.equals(user.getName()) && encryption.compareHashedPassword(user.getPassword(), password)) {
                    this.user.setValue(user);
                    isUpdating.setValue(false);
                    alert.setValue("Vítejte pane " + user.getName() + ", přeji příjemné chlastání");
                    return false;
                }
                else if (name.equals(user.getName()) && !password.equals(user.getPassword())) {
                    isUpdating.setValue(false);
                    alert.setValue("Zadal si špatný heslo!");
                    return true;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                alert.setValue("Chyba při šifrování hesla!");
                return false;
            }
        }
        isUpdating.setValue(false);
        alert.setValue("Nikoho takovýho tu neznáme!");
        return false;
    }

    boolean loginWithHashedPassword(String name, String password) {
        isUpdating.setValue(true);

        for (User user : Objects.requireNonNull(getUsers().getValue())) {
            if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                this.user.setValue(user);
                isUpdating.setValue(false);
                alert.setValue("Vítejte pane " + user.getName() + ", přeji příjemné chlastání");
                return false;
            }
            else if (name.equals(user.getName()) && !password.equals(user.getPassword())) {
                isUpdating.setValue(false);
                alert.setValue("Zadal si špatný heslo!");
                return true;
            }
        }
        isUpdating.setValue(false);
        alert.setValue("Nikoho takovýho tu neznáme!");
        return false;
    }

    boolean addUserToRepository (final String name, final String password) {
        isUpdating.setValue(true);
        if (!checkNewUserValidation(name, password)) {
            isUpdating.setValue(false);
            return false;
        }
        User user;
        try {
            user = new User(name, encryption.hashPassword(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            alert.setValue("Chyba při šifrování hesla!");
            isUpdating.setValue(false);
            return false;
        }
        try {
            firebaseRepository.insertNewModel(user);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, žádná registrace nebude");
            isUpdating.setValue(false);
            return false;
        }
        alert.setValue("Registruji uživatele " + name);
        return true;
    }

    private void setUserAsAdded(final User user, Flag flag) {
        String action = "";
        switch (flag) {
            case USER_PLUS:
                action = "Byl zaregistrován uživatel " + user.getName();
                alert.setValue("Registrace uživatele " + user.getName() + " hotová, můžete se přihlásit");
                break;
            case USER_EDIT:
                alert.setValue("Upraveny údaje uživatele");
                action = "Upraveny údaje uživatele";
                break;
            case USER_DELETE:
                alert.setValue("Smazán uživatel" + user.getName());
                break;
        }
        if (flag == Flag.USER_PLUS) {
            Notification newNotification = new Notification(action, new User("admin", Color.parseColor("#FF8303")));
            sendNotificationToRepository(newNotification);
        }
        isUpdating.setValue(false);
    }

    private void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }
    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<User> getUser() {
        return user;
    }


    @Override
    public void itemAdded(Model model) {
        setUserAsAdded((User) model, Flag.USER_PLUS);
    }

    @Override
    public void itemChanged(Model model) {
        setUserAsAdded((User) model, Flag.USER_EDIT);
    }

    @Override
    public void itemDeleted(Model model) {
        setUserAsAdded((User) model, Flag.USER_DELETE);
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Log.d(TAG, "itemListLoaded: " + models);
        users.setValue(models);
    }

    @Override
    public void alertSent(String message) {

    }
}
