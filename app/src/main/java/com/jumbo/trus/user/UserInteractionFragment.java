package com.jumbo.trus.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.Flag;
import com.jumbo.trus.R;

import java.util.ArrayList;
import java.util.List;


public class UserInteractionFragment extends CustomUserFragment implements View.OnClickListener, TextWatcher, IUserInteraction {

    private static final String TAG = "UserInteractionFragment";

    private EditText et_old_password, et_new_password;
    private Button btn_password_change, btn_notification_color_change, btn_approve_new_users, btn_approve_forgotten_password, btn_admin_interaction;
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
        btn_approve_new_users = view.findViewById(R.id.btn_approve_new_users);
        btn_approve_forgotten_password = view.findViewById(R.id.btn_approve_forgotten_password);
        btn_admin_interaction = view.findViewById(R.id.btn_admin_interaction);
        sw_change_preferences = view.findViewById(R.id.sw_change_preferences);
        progress_bar = view.findViewById(R.id.progress_bar);
        btn_password_change.setOnClickListener(this);
        btn_notification_color_change.setOnClickListener(this);
        btn_approve_new_users.setOnClickListener(this);
        btn_approve_forgotten_password.setOnClickListener(this);
        btn_admin_interaction.setOnClickListener(this);
        btn_admin_interaction.setVisibility(View.INVISIBLE);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginViewModel.init();
        showProgressBar();
        initLoginViewModelAndChangeColor();


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
                hideButtonsToUserWithoutPermissions(user);
            }
        });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    private void initLoginViewModelAndChangeColor() {
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: zmena uzivatele " + user);
                setUser(user);
                checkIfUserIsNotDenied(user);
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

    private List<User> getListOfUsersWaitingForApprove() {
        List<User> userList = new ArrayList<>();
        for (User user : loginViewModel.getUsers().getValue()) {
            if (user.getStatus() == User.Status.WAITING_FOR_APPROVE) {
                userList.add(user);
            }
        }
        return userList;
    }

    private List<User> getListOfUsersWaitingForPasswordReset() {
        List<User> userList = new ArrayList<>();
        for (User user : loginViewModel.getUsers().getValue()) {
            if (user.getStatus() == User.Status.FORGOTTEN_PASSWORD) {
                userList.add(user);
            }
        }
        return userList;
    }

    private void hideButtonsToUserWithoutPermissions(User user) {
        if (user.getPermission() == User.Permission.READ_ONLY) {
            btn_approve_forgotten_password.setVisibility(View.INVISIBLE);
            btn_approve_new_users.setVisibility(View.INVISIBLE);
            btn_admin_interaction.setVisibility(View.INVISIBLE);
        }
        else if (user.getPermission() == User.Permission.ADMIN) {
            btn_approve_forgotten_password.setVisibility(View.VISIBLE);
            btn_approve_new_users.setVisibility(View.VISIBLE);
            btn_admin_interaction.setVisibility(View.VISIBLE);
        }
        else {
            btn_approve_forgotten_password.setVisibility(View.VISIBLE);
            btn_approve_new_users.setVisibility(View.VISIBLE);
            btn_admin_interaction.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void setUser(User user) {
        super.setUser(user);
        hideButtonsToUserWithoutPermissions(user);
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
            case R.id.btn_approve_new_users:
                UserListDialog newUserListDialog = new UserListDialog(getListOfUsersWaitingForApprove(), Flag.USER_APPROVE, this);
                newUserListDialog.show(getParentFragmentManager(), "approve");
                break;

            case R.id.btn_approve_forgotten_password:
                UserListDialog passwordUserListDialog = new UserListDialog(getListOfUsersWaitingForPasswordReset(), Flag.USER_RESET_PASSWORD, this);
                passwordUserListDialog.show(getParentFragmentManager(), "forgot");
                break;
            case R.id.btn_admin_interaction:
                UserListDialog userListDialog = new UserListDialog(loginViewModel.getUsers().getValue(), Flag.USER_ALL, this);
                userListDialog.show(getParentFragmentManager(), "all");
                break;
        }
    }

    @Override
    public boolean approveNewUser(User user, boolean approve) {
        if (approve) {
            if (loginViewModel.changeUserPermissionAndStatus(user, User.Permission.USER, User.Status.APPROVED)) {
                return true;
            }
        }
        else {
            if (loginViewModel.changeUserStatus(user, User.Status.DENIED)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean resetPassword(User user) {
        if (loginViewModel.changeUserStatus(user, User.Status.PASSWORD_RESET)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean changeUserStatus(User user, User.Status status) {
        if (loginViewModel.changeUserStatus(user, status)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean changeUserPermission(User user, User.Permission permission) {
        if (loginViewModel.changeUserPermission(user, permission)) {
            return true;
        }
        return false;
    }
}

