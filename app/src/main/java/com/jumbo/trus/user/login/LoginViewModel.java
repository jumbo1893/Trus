package com.jumbo.trus.user.login;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.user.PasswordEncryption;
import com.jumbo.trus.user.User;
import com.jumbo.trus.validator.Validator;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginViewModel extends BaseViewModel implements ChangeListener {

    private static final String TAG = "LoginViewModel";

    private FirebaseRepository firebaseRepository;
    private List<User> users = new ArrayList();
    private MutableLiveData<User> user;
    private PasswordEncryption encryption;
    private MutableLiveData<String> textViewHelperText = new MutableLiveData();
    private MutableLiveData<Boolean> loginEnabled = new MutableLiveData();
    private MutableLiveData<Boolean> registrationEnabled = new MutableLiveData();
    private MutableLiveData<String> passwordErrorText = new MutableLiveData();
    private MutableLiveData<String> loginErrorText = new MutableLiveData();
    private String rememberedLogin;
    private String rememberedPassword;
    private boolean autoLogin = false;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.USER_TABLE, this);
        encryption = new PasswordEncryption();
        if (user == null) {
            user = new MutableLiveData<>();
        }
        firebaseRepository.loadUsersFromRepository();
    }


    private boolean checkNewUserValidation(final String name, final String password) {
        Validator validator = new Validator();
        if (!validator.fieldIsNotEmpty(name)) {
            loginErrorText.setValue("Pro registraci musíš vyplnit jméno a heslo");
            passwordErrorText.setValue("Pro registraci musíš vyplnit jméno a heslo");
        }
        else if (!validator.checkFieldFormat(name, 20)) {
            loginErrorText.setValue("Uživatelský jméno je moc dlouhý nebo obsahuje nesmysly");
        }
        else if (!validator.fieldIsNotEmpty(password)) {
            passwordErrorText.setValue("Není vyplněné heslo");
        }
        else if (!validator.checkPasswordFormat(password)) {
            passwordErrorText.setValue("Heslo musí mít mezi dýlku 1 až 30 znaků, tak nevymejšlej píčoviny");
        }
        else if (checkIfUsernameAlreadyExists(name)) {
            loginErrorText.setValue("Uživatel se stejným jménem už existuje");
        }
        else {
            return true;
        }
        return false;
    }

    public void checkLoginTextForPermissions(String username, String password) {
        if(!(username.length() > 0) || !(password.length() > 0)) {
            loginEnabled.setValue(false);
            registrationEnabled.setValue(false);
            textViewHelperText.setValue("Pro přihlášení/registraci musíš vyplnit jméno a heslo");
            return;
        }
        for (User user : users) {
            if (username.equals(user.getName())) {
                if (user.getStatus() == User.Status.WAITING_FOR_APPROVE) {
                    textViewHelperText.setValue("Uživatel " + username + " čeká na schválení registrace. Do té doby jsou nastaveny read-only práva");
                    loginEnabled.setValue(true);
                    break;
                }
                else if (user.getStatus() == User.Status.DENIED) {
                    loginEnabled.setValue(false);
                    textViewHelperText.setValue("Přístup zamítnut PÍČO");
                    break;
                }
                else if (user.getStatus() == User.Status.FORGOTTEN_PASSWORD) {
                    loginEnabled.setValue(true);
                    textViewHelperText.setValue("Zaslána žádost o resetování hesla. Řekni nějakýmu Trusákovi ať ti to potvrdí. Do tý doby si dej pivo na paměť a můžeš to zkoušet");
                    break;
                }
                else if (user.getStatus() == User.Status.PASSWORD_RESET) {
                    loginEnabled.setValue(true);
                    textViewHelperText.setValue("Heslo bylo resetováno. Přihlaš se s libovolným novým a uloží se ti. A někam si ho radši zapiš DEBILE");
                    break;
                } else {
                    loginEnabled.setValue(true);
                    registrationEnabled.setValue(true);
                    textViewHelperText.setValue("");
                }
            }
            else {
                loginEnabled.setValue(true);
                registrationEnabled.setValue(true);
                textViewHelperText.setValue("");
            }
        }
    }

    private boolean checkIfUsernameAlreadyExists(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    boolean loginWithUsernameAndPassword(String name, String password) {
        isUpdating.setValue(true);
        if (users.size() == 0) {
            alert.setValue("Počkej až se načte db ");
            isUpdating.setValue(false);
            return false;
        }
        for (User user : users) {
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
                    passwordErrorText.setValue("Zadal si špatný heslo!");
                    return true;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                alert.setValue("Chyba při šifrování hesla!");
                return false;
            }
        }
        isUpdating.setValue(false);
        loginErrorText.setValue("Nikoho takovýho tu neznáme!");
        return false;
    }

    boolean loginWithHashedPassword(String name, String password) {
        isUpdating.setValue(true);
        if (users.size() == 0) {
            autoLogin = true;
            rememberedLogin = name;
            rememberedPassword = password;
            Log.d(TAG, "loginWithHashedPassword: zaznamenán pokus o přihlášení před načtenejma uživatelema" );
        }
        for (User user : users) {
            if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                this.user.setValue(user);
                alert.setValue("Vítejte pane " + user.getName() + ", přeji příjemné chlastání");
                isUpdating.setValue(false);
                return true;
            }
            else if (name.equals(user.getName()) && !password.equals(user.getPassword())) {
                isUpdating.setValue(false);
                passwordErrorText.setValue("Zadal si špatný heslo!");
                return false;
            }
        }
        isUpdating.setValue(false);
        loginErrorText.setValue("Nikoho takovýho tu neznáme!");
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
        for (User dbUser : users) {
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
    private void checkIfCurrentUserIsChanged(List<User> userList) {
        for (User user : userList) {
            if (user.equals(getUser().getValue())) {
                this.user.setValue(user);
            }
        }
    }

    public LiveData<String> getLoginErrorText() {
        return loginErrorText;
    }

    public LiveData<String> getPasswordErrorText() {
        return passwordErrorText;
    }

    public LiveData<String> getTextViewHelperText() {
        return textViewHelperText;
    }


    public LiveData<Boolean> isLoginEnabled() {
        return loginEnabled;
    }

    public LiveData<Boolean> isRegistrationEnabled() {
        return registrationEnabled;
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
        users = models;
        checkIfCurrentUserIsChanged(models);
        if (autoLogin) {
            loginWithHashedPassword(rememberedLogin, rememberedPassword);
        }
    }

    @Override
    public void alertSent(String message) {

    }
}
