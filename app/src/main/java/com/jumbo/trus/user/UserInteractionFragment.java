package com.jumbo.trus.user;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.MainActivityViewModel;
import com.jumbo.trus.R;

import java.util.List;


public class UserInteractionFragment extends CustomUserFragment implements View.OnClickListener, TextWatcher {

    private static final String TAG = "UserInteractionFragment";

    private EditText et_old_password, et_new_password;
    private Button btn_password_change, btn_notification_color_change;
    private Switch sw_change_preferences;
    private ProgressBar progress_bar;

    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_interaction, container, false);
        et_old_password = view.findViewById(R.id.et_old_password);
        et_new_password = view.findViewById(R.id.et_new_password);
        btn_password_change = view.findViewById(R.id.btn_password_change);
        btn_notification_color_change = view.findViewById(R.id.btn_notification_color_change);
        sw_change_preferences = view.findViewById(R.id.sw_change_preferences);
        progress_bar = view.findViewById(R.id.progress_bar);
        btn_password_change.setOnClickListener(this);
        btn_notification_color_change.setOnClickListener(this);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginViewModel.init();
        showProgressBar();
        initMainActivityViewModelAndChangeColor();


        loginViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        loginViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                hideProgressBar();
                et_new_password.setFocusableInTouchMode(true);
                et_old_password.setFocusableInTouchMode(true);
            }
        });

        loginViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "změna hesla " + user);
                if (sw_change_preferences.isChecked()) {
                    requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                            .edit()
                            .putString("password", user.getPassword())
                            .apply();
                }
                mainActivityViewModel.setUser(user);
            }
        });
        return view;
    }

    private void initMainActivityViewModelAndChangeColor() {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUser(user);
                btn_notification_color_change.setBackgroundColor(user.getCharColor());
            }
        });
    }


    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        String newPassword = et_new_password.getText().toString().trim();
        String oldPassword = et_old_password.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_password_change:
                loginViewModel.editUserPasswordInRepository(newPassword, oldPassword, user);
                break;
            case R.id.btn_notification_color_change:
                int color = loginViewModel.changeColorOfUserInRepository(user);
                if (color != 0) {
                    btn_notification_color_change.setBackgroundColor(color);
                }
                break;
        }
    }
}

