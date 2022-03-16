package com.jumbo.trus.user.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.MainActivity;
import com.jumbo.trus.R;
import com.jumbo.trus.update.CheckPermissionsCompat;
import com.jumbo.trus.update.DownloadController;
import com.jumbo.trus.update.FinishActivityCallback;
import com.jumbo.trus.update.ForceUpdateChecker;
import com.jumbo.trus.user.User;

import java.util.Objects;

public class LoginActivity extends CheckPermissionsCompat implements View.OnClickListener, TextWatcher, ForceUpdateChecker.OnUpdateNeededListener, FinishActivityCallback {

    private static final String TAG = "LoginActivity";
    private static final int PERMISSION_REQUEST_STORAGE = 0;

    private TextInputLayout textLogin, textPassword;
    private TextView tv_help;
    private Button btnLogin, btn_register, btn_password_forgotten;
    private Switch sw_remember;
    private ProgressBar progress_bar, download_bar;

    private boolean logout;
    private boolean forceUpdate = false;
    private boolean usersLoaded = false;
    private DownloadController downloadController;

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
        textLogin = findViewById(R.id.textLogin);
        download_bar = findViewById(R.id.download_bar);
        textPassword = findViewById(R.id.textPassword);
        tv_help = findViewById(R.id.tv_help);
        btnLogin = findViewById(R.id.btnLogin);
        btn_register = findViewById(R.id.btn_register);
        btn_password_forgotten = findViewById(R.id.btn_password_forgotten);
        sw_remember = findViewById(R.id.sw_remember);
        progress_bar = findViewById(R.id.progress_bar);
        btnLogin.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_password_forgotten.setOnClickListener(this);
        Objects.requireNonNull(textLogin.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(textPassword.getEditText()).addTextChangedListener(this);
        setRememberedCredentials();
        disableButton(btnLogin);
        disableButton(btn_register);
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
        loginViewModel.getTextViewHelperText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    tv_help.setText(s);
                }
            }
        });
        loginViewModel.isLoginEnabled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    enableButton(btnLogin);
                }
                else {
                    disableButton(btnLogin);
                }
            }
        });
        loginViewModel.isRegistrationEnabled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    enableButton(btn_register);
                }
                else {
                    disableButton(btn_register);
                }
            }
        });
        loginViewModel.getLoginErrorText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    textLogin.setError(s);
                }
            }
        });
        loginViewModel.getPasswordErrorText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    textPassword.setError(s);
                }
            }
        });
        checkAndroidPermissions();
    }

    private void checkAndroidPermissions() {
        //installtion permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!getPackageManager().canRequestPackageInstalls()){
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse(String.format("package:%s", getPackageName()))), 1);
            }
        }
//Storage Permission

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    private void enableButton(Button button) {
        button.setEnabled(true);
    }

    private void disableButton(Button button) {
        button.setEnabled(false);
    }

    private void switchToMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void loginWithRememberedLogin() {
        Log.d(TAG, "loginWithRememberedLogin: " + logout + forceUpdate);
        if (pref.getBoolean("remember", false) && !logout && !forceUpdate) {
            loginViewModel.loginWithHashedPassword(pref.getString("username", ""), pref.getString("password", ""));
        }
    }

    private void setRememberedCredentials() {
        if (pref.getBoolean("remember", false)) {
            Objects.requireNonNull(textLogin.getEditText()).setText(pref.getString("username", ""));
            sw_remember.setChecked(pref.getBoolean("remember", false));
        }
    }

    @Override
    public void onClick(View v) {
        String name = textLogin.getEditText().getText().toString();
        String password = textPassword.getEditText().getText().toString();
        switch (v.getId()) {
            case R.id.btnLogin:
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
        loginViewModel.checkLoginTextForPermissions(textLogin.getEditText().getText().toString(), textPassword.getEditText().getText().toString());
        textLogin.setError(null);
        textPassword.setError(null);
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

    private void redirectStore(String updateUrl) {
        Log.d(TAG, "redirectStore: " + updateUrl);
        downloadController = new DownloadController(updateUrl, this, this);
        checkStoragePermission();
        //StorageManager storageManager = new StorageManager(this, updateUrl);
        //storageManager.downloadNewApp();
        //InstallAPK downloadAndInstall = new InstallAPK();
        //downloadAndInstall.setContext(this, download_bar);
        //downloadAndInstall.downloadNewApp(fileName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // Request for camera permission.

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload();
            } else {
                // Permission request was denied.
                Toast.makeText(this, "Storage permission request was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkStoragePermission() {
        Log.d(TAG, "checkStoragePermission: ");
        // Check if the storage permission has been granted
        if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "checkStoragePermission: " + 1);
            // start downloading
            downloadController.enqueueDownload();
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        String[] permissionsArray = new String[1];
        permissionsArray[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage access is required to downloading the file", Toast.LENGTH_SHORT).show();
            requestPermissionsCompat(permissionsArray, PERMISSION_REQUEST_STORAGE);

        } else {
            requestPermissionsCompat(permissionsArray, PERMISSION_REQUEST_STORAGE);
        }
    }

    @Override
    public void finishActivity() {
       finish();
    }
}
