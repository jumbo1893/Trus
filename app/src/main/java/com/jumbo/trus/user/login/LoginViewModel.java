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
            loginErrorText.setValue("Pro registraci mus???? vyplnit jm??no a heslo");
            passwordErrorText.setValue("Pro registraci mus???? vyplnit jm??no a heslo");
        }
        else if (!validator.checkFieldFormat(name, 20)) {
            loginErrorText.setValue("U??ivatelsk?? jm??no je moc dlouh?? nebo obsahuje nesmysly");
        }
        else if (!validator.fieldIsNotEmpty(password)) {
            passwordErrorText.setValue("Nen?? vypln??n?? heslo");
        }
        else if (!validator.checkPasswordFormat(password)) {
            passwordErrorText.setValue("Heslo mus?? m??t mezi d??lku 1 a?? 30 znak??, tak nevymej??lej p????oviny");
        }
        else if (checkIfUsernameAlreadyExists(name)) {
            loginErrorText.setValue("U??ivatel se stejn??m jm??nem u?? existuje");
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
            textViewHelperText.setValue("Pro p??ihl????en??/registraci mus???? vyplnit jm??no a heslo");
            return;
        }
        for (User user : users) {
            if (username.equals(user.getName())) {
                if (user.getStatus() == User.Status.WAITING_FOR_APPROVE) {
                    textViewHelperText.setValue("U??ivatel " + username + " ??ek?? na schv??len?? registrace. Do t?? doby jsou nastaveny read-only pr??va");
                    loginEnabled.setValue(true);
                    break;
                }
                else if (user.getStatus() == User.Status.DENIED) {
                    loginEnabled.setValue(false);
                    textViewHelperText.setValue("P????stup zam??tnut P????O");
                    break;
                }
                else if (user.getStatus() == User.Status.FORGOTTEN_PASSWORD) {
                    loginEnabled.setValue(true);
                    textViewHelperText.setValue("Zasl??na ????dost o resetov??n?? hesla. ??ekni n??jak??mu Trus??kovi a?? ti to potvrd??. Do t?? doby si dej pivo na pam???? a m????e?? to zkou??et");
                    break;
                }
                else if (user.getStatus() == User.Status.PASSWORD_RESET) {
                    loginEnabled.setValue(true);
                    textViewHelperText.setValue("Heslo bylo resetov??no. P??ihla?? se s libovoln??m nov??m a ulo???? se ti. A n??kam si ho rad??i zapi?? DEBILE");
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
            alert.setValue("Po??kej a?? se na??te db ");
            isUpdating.setValue(false);
            return false;
        }
        for (User user : users) {
            try {
                if (name.equals(user.getName()) && encryption.compareHashedPassword(user.getPassword(), password)) {
                    if (user.getStatus() == User.Status.FORGOTTEN_PASSWORD) {
                        user.setStatus(User.Status.APPROVED);
                        changeUserInDBSimply(user);
                        alert.setValue("Vypad?? to, ??e si " + user.getName() + ", vzpomn??l na heslo. Tak abys ho po dne??n?? j??zd?? nezapomn??l");
                    }
                    else {
                        alert.setValue("V??tejte pane " + user.getName() + ", p??eji p????jemn?? chlast??n??");
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
                    alert.setValue("V??tej " + user.getName() + ". Heslo bylo zm??n??no, dlu?????? rundu");
                    return true;
                }
                else if (name.equals(user.getName()) && !password.equals(user.getPassword())) {
                    isUpdating.setValue(false);
                    passwordErrorText.setValue("Zadal si ??patn?? heslo!");
                    return true;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                alert.setValue("Chyba p??i ??ifrov??n?? hesla!");
                return false;
            }
        }
        isUpdating.setValue(false);
        loginErrorText.setValue("Nikoho takov??ho tu nezn??me!");
        return false;
    }

    boolean loginWithHashedPassword(String name, String password) {
        isUpdating.setValue(true);
        if (users.size() == 0) {
            autoLogin = true;
            rememberedLogin = name;
            rememberedPassword = password;
            Log.d(TAG, "loginWithHashedPassword: zaznamen??n pokus o p??ihl????en?? p??ed na??tenejma u??ivatelema" );
        }
        for (User user : users) {
            if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                this.user.setValue(user);
                alert.setValue("V??tejte pane " + user.getName() + ", p??eji p????jemn?? chlast??n??");
                isUpdating.setValue(false);
                return true;
            }
            else if (name.equals(user.getName()) && !password.equals(user.getPassword())) {
                isUpdating.setValue(false);
                passwordErrorText.setValue("Zadal si ??patn?? heslo!");
                return false;
            }
        }
        isUpdating.setValue(false);
        loginErrorText.setValue("Nikoho takov??ho tu nezn??me!");
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
            alert.setValue("Chyba p??i ??ifrov??n?? hesla!");
            isUpdating.setValue(false);
            return false;
        }
        try {
            firebaseRepository.insertNewModel(user);
        }
        catch (Exception e) {
            alert.setValue("Chyba p??i komunikaci s db, ????dn?? registrace nebude");
            isUpdating.setValue(false);
            return false;
        }
        alert.setValue("Registruji u??ivatele " + name);
        return true;
    }

    boolean changeUserStatusToForgottenPassword(final String name) {
        isUpdating.setValue(true);
        User user = null;
        if (!checkIfUsernameAlreadyExists(name)) {
            alert.setValue("Jestli si zapomn??l heslo, mus???? zadat sv??j login do kolonky \"p??ihla??ovac?? jm??no\"");
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
                alert.setValue("Chyba p??i komunikaci s db, sorry, zkus to jindy");
                isUpdating.setValue(false);
                return false;
            }
            alert.setValue("Zasl??na ????dost o reset hesla u u??ivatele " + name);
            return true;
        }
        alert.setValue("Chyba p??i vyhled??n?? u??ivatele " + name + " v db");
        isUpdating.setValue(false);
        return false;
    }

    private void changeUserInDBSimply(User user) {
        try {
            firebaseRepository.editModel(user);
        } catch (Exception e) {
            alert.setValue("Chyba p??i komunikaci s db, sorry, zkus to jindy");
            isUpdating.setValue(false);
        }
    }

    private void setUserAsAdded(final User user, Flag flag) {
        String action = "";
        switch (flag) {
            case USER_PLUS:
                action = "Byl zaregistrov??n u??ivatel " + user.getName();
                //alert.setValue("Registrace u??ivatele " + user.getName() + " hotov??, m????ete se p??ihl??sit");
                break;
            case USER_EDIT:
                //alert.setValue("Upraveny ??daje u??ivatele");
                action = "Upraveny ??daje u??ivatele";
                break;
            case USER_DELETE:
                alert.setValue("Smaz??n u??ivatel" + user.getName());
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
