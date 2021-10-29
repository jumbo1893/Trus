package com.jumbo.trus.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.MainActivity;
import com.jumbo.trus.R;
import com.jumbo.trus.update.ForceUpdateChecker;
import com.jumbo.trus.update.StorageManager;

import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, ForceUpdateChecker.OnUpdateNeededListener {

    private static final String TAG = "LoginActivity";

    private EditText et_username, et_password;
    private TextView tv_emptyfields, tv_permissions;
    private Button btn_login, btn_register, btn_password_forgotten;
    private Switch sw_remember;
    private ProgressBar progress_bar;

    private boolean logout;
    private boolean forceUpdate = false;
    private boolean usersLoaded = false;

    private LoginViewModel loginViewModel;

    private SharedPreferences pref;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logout = getIntent().getBooleanExtra("logout", false);
        setContentView(R.layout.activity_login);
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        pref = getSharedPreferences("Preferences", MODE_PRIVATE);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.init();
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_emptyfields = findViewById(R.id.tv_emptyfields);
        tv_permissions = findViewById(R.id.tv_permissions);
        tv_permissions.setVisibility(View.GONE);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_password_forgotten = findViewById(R.id.btn_password_forgotten);
        sw_remember = findViewById(R.id.sw_remember);
        progress_bar = findViewById(R.id.progress_bar);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_password_forgotten.setOnClickListener(this);
        et_username.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        showProgressBar();
        et_username.setFocusable(false);
        et_password.setFocusable(false);
        setRememberedCredentials();

        loginViewModel.isUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProgressBar();
                }
                else {
                    hideProgressBar();
                }
            }
        });
        loginViewModel.getAlert().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                hideProgressBar();
                et_username.setFocusableInTouchMode(true);
                et_password.setFocusableInTouchMode(true);
                loginWithRememberedLogin();
                usersLoaded = true;
                checkLoginForEmptyPermissions();
            }
        });

        loginViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "přihlášen " + user);
                if (sw_remember.isChecked()) {
                    getSharedPreferences("Preferences", MODE_PRIVATE)
                            .edit()
                            .putString("username", user.getName())
                            .putString("password", user.getPassword())
                            .putBoolean("remember", sw_remember.isChecked())
                            .apply();
                }
                getSharedPreferences("Preferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("remember", sw_remember.isChecked())
                        .apply();
                switchToMainActivity(user);
            }
        });
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    private void checkFieldsForEmptyValues() {

        String name = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if(!(name.length() > 0) || !(password.length() > 0)) {
            btn_login.setEnabled(false);
            btn_register.setEnabled(false);
            tv_emptyfields.setVisibility(View.VISIBLE);
        } else {
            btn_login.setEnabled(true);
            btn_register.setEnabled(true);
            tv_emptyfields.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkLoginForEmptyPermissions() {
        String name = et_username.getText().toString().trim();
        for (User user : Objects.requireNonNull(loginViewModel.getUsers().getValue())) {
            if (name.equals(user.getName())) {
                if (user.getStatus() == User.Status.WAITING_FOR_APPROVE) {
                    tv_permissions.setVisibility(View.VISIBLE);
                    tv_permissions.setText("Uživatel " + name + " čeká na schválení registrace. Do té doby jsou nastaveny read-only práva");
                    break;
                }
                else if (user.getStatus() == User.Status.DENIED) {
                    tv_permissions.setVisibility(View.VISIBLE);
                    btn_login.setEnabled(false);
                    tv_permissions.setText("Přístup zamítnut PÍČO");
                    break;
                }
                else if (user.getStatus() == User.Status.FORGOTTEN_PASSWORD) {
                    tv_permissions.setVisibility(View.VISIBLE);
                    tv_permissions.setText("Zaslána žádost o resetování hesla. Řekni nějakýmu Trusákovi ať ti to potvrdí. Do tý doby si dej pivo na paměť a můžeš to zkoušet");
                    break;
                }
                else if (user.getStatus() == User.Status.PASSWORD_RESET) {
                    tv_permissions.setVisibility(View.VISIBLE);
                    tv_permissions.setText("Heslo bylo resetováno. Přihlaš se s libovolným novým a uloží se ti. A někam si ho radši zapiš DEBILE");
                    break;
                } else {
                    tv_permissions.setVisibility(View.GONE);
                }
            }
            else {
                tv_permissions.setVisibility(View.GONE);
            }
        }
    }

    private void switchToMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void loginWithRememberedLogin() {
        Log.d(TAG, "loginWithRememberedLogin: ");
        if (pref.getBoolean("remember", false) && !logout && !forceUpdate && usersLoaded) {
            loginViewModel.loginWithHashedPassword(pref.getString("username", ""), pref.getString("password", ""));
        }
    }

    private void setRememberedCredentials() {
        if (pref.getBoolean("remember", false)) {
            et_username.setText(pref.getString("username", ""));
            sw_remember.setChecked(pref.getBoolean("remember", false));
        }
    }

    @Override
    public void onClick(View v) {
        String name = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_login:
                if (loginViewModel.loginWithUsernameAndPassword(name, password) && sw_remember.isChecked()) {
                }

                break;
            case R.id.btn_register:
                loginViewModel.addUserToRepository(name, password);
                break;
            case R.id.btn_password_forgotten:
                loginViewModel.changeUserStatusToForgottenPassword(name);
                break;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkFieldsForEmptyValues();
        if (loginViewModel.getUsers().getValue() != null) {
            checkLoginForEmptyPermissions();
        }

    }

    @Override
    public void afterTextChanged(Editable s) {


    }

    @Override
    public void onUpdateNeeded(final String updateUrl, final String appVersion, boolean update) {
        forceUpdate = update;
        Log.d(TAG, "onUpdateNeeded: " + update);
        if (update) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Aktualizace aplikace")
                    .setMessage("Prosím, stáhněte si novou verzi aplikace " + appVersion + " pro správnou trusí funkčnost")
                    .setPositiveButton("Stáhnout",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    redirectStore(updateUrl);
                                }
                            }).setNegativeButton("Ne, díky",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishAndRemoveTask();
                                }
                            }).create();
            dialog.show();
        }
        else {
            loginWithRememberedLogin();
        }
    }

    private void redirectStore(String fileName) {
        StorageManager storageManager = new StorageManager(this, fileName);
        storageManager.downloadNewApp();
    }
}
