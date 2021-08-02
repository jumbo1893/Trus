package com.jumbo.trus.user;

import android.app.Activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jumbo.trus.MainActivity;
import com.jumbo.trus.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "LoginActivity";

    private EditText et_username, et_password;
    private TextView tv_emptyfields;
    private Button btn_login, btn_register;
    private ProgressBar progress_bar;

    private LoginViewModel loginViewModel;

    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.init();
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_emptyfields = findViewById(R.id.tv_emptyfields);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        progress_bar = findViewById(R.id.progress_bar);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        et_username.addTextChangedListener(this);
        et_password.addTextChangedListener(this);

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

    private void switchToMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String name = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_login:
                loginViewModel.loginWithUsernameAndPassword(name, password);

                break;
            case R.id.btn_register:
                loginViewModel.checkNewUserValidation(name, password);
                break;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkFieldsForEmptyValues();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
