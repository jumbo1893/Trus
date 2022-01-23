package com.jumbo.trus.user;

import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.validator.Validator;
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
        firebaseRepository = new FirebaseRepository(FirebaseRepository.USER_TABLE, this);
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

    boolean editUserPasswordInRepository(final String password, final String oldPassword, User user) {
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

    int changeColorOfUserInRepository(User user) {
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
                    if (user.getStatus() == User.Status.FORGOTTEN_PASSWORD) {
                        user.setStatus(User.Status.APPROVED);
                        changeUserInDBSimply(user);
                        alert.setValue("Vypadá to, že si " + user.getName() + ", vzpomněl na heslo. Tak abys ho po dnešní jízdě nezapomněl");
                    }
                    else {
                        alert.setValue("Vítejte pane " + user.getName() + ", přeji příjemné chlastání");
                    }
                    this.user.setValue(user);
                    isUpdating.setValue(false);
                    return false;
                }
                else if (name.equals(user.getName()) && !password.equals(user.getPassword()) && user.getStatus() == User.Status.PASSWORD_RESET) {
                    user.setStatus(User.Status.APPROVED);
                    user.setPassword(encryption.hashPassword(password));
                    changeUserInDBSimply(user);
                    this.user.setValue(user);
                    isUpdating.setValue(false);
                    alert.setValue("Vítej " + user.getName() + ". Heslo bylo změněno, dlužíš rundu");
                    return true;
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
                alert.setValue("Vítejte pane " + user.getName() + ", přeji příjemné chlastání");
                isUpdating.setValue(false);
                return true;
            }
            else if (name.equals(user.getName()) && !password.equals(user.getPassword())) {
                isUpdating.setValue(false);
                alert.setValue("Zadal si špatný heslo!");
                return false;
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
            user = new User(name, encryption.hashPassword(password), User.Permission.READ_ONLY, User.Status.WAITING_FOR_APPROVE);
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

    boolean changeUserStatusToForgottenPassword(final String name) {
        isUpdating.setValue(true);
        User user = null;
        if (!checkIfUsernameAlreadyExists(name)) {
            alert.setValue("Jestli si zapomněl heslo, musíš zadat svůj login do kolonky \"přihlašovací jméno\"");
            isUpdating.setValue(false);
            return false;
        }
        for (User dbUser : Objects.requireNonNull(getUsers().getValue())) {
            if (name.equals(dbUser.getName())) {
                user = dbUser;
                break;
            }
        }
        if (user != null) {
            user.setStatus(User.Status.FORGOTTEN_PASSWORD);
            try {
                firebaseRepository.editModel(user);
            } catch (Exception e) {
                alert.setValue("Chyba při komunikaci s db, sorry, zkus to jindy");
                isUpdating.setValue(false);
                return false;
            }
            alert.setValue("Zaslána žádost o reset hesla u uživatele " + name);
            return true;
        }
        alert.setValue("Chyba při vyhledání uživatele " + name + " v db");
        isUpdating.setValue(false);
        return false;
    }

    private void changeUserInDBSimply(User user) {
        try {
            firebaseRepository.editModel(user);
        } catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, sorry, zkus to jindy");
            isUpdating.setValue(false);
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

    private void setUserAsAdded(final User user, Flag flag) {
        String action = "";
        switch (flag) {
            case USER_PLUS:
                action = "Byl zaregistrován uživatel " + user.getName();
                //alert.setValue("Registrace uživatele " + user.getName() + " hotová, můžete se přihlásit");
                break;
            case USER_EDIT:
                //alert.setValue("Upraveny údaje uživatele");
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

    private void checkIfCurrentUserIsChanged(List<User> userList) {
        for (User user : userList) {
            if (user.equals(getUser().getValue())) {
                this.user.setValue(user);
            }
        }
    }

    public void setUser(User user) {
        this.user.setValue(user);
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
        checkIfCurrentUserIsChanged(models);
    }

    @Override
    public void alertSent(String message) {

    }
}
