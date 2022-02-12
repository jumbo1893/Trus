package com.jumbo.trus.user.interaction;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.user.User;
import com.jumbo.trus.validator.TextFieldValidator;


public class UserInteractionFragment extends CustomUserFragment implements View.OnClickListener, TextWatcher {

    private static final String TAG = "UserInteractionFragment";

    private TextInputLayout textOldPassword, textNewPassword;
    private Button btnChangePassword, btnChangeColor, btnApproveNewUser, btnApprovePasswordReset, btnAdminInteraction;
    private ProgressBar progress_bar;

    private UserViewModel userViewModel;

    private TextFieldValidator oldPasswordValidator;
    private TextFieldValidator newPasswordValidator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_interaction, container, false);
        textOldPassword = view.findViewById(R.id.textOldPassword);
        textNewPassword = view.findViewById(R.id.textNewPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangeColor = view.findViewById(R.id.btnChangeColor);
        btnApproveNewUser = view.findViewById(R.id.btnApproveNewUser);
        btnApprovePasswordReset = view.findViewById(R.id.btnApprovePasswordReset);
        btnAdminInteraction = view.findViewById(R.id.btnAdminInteraction);
        progress_bar = view.findViewById(R.id.progress_bar);
        btnChangePassword.setOnClickListener(this);
        btnChangeColor.setOnClickListener(this);
        btnApproveNewUser.setOnClickListener(this);
        btnApprovePasswordReset.setOnClickListener(this);
        btnAdminInteraction.setOnClickListener(this);
        btnAdminInteraction.setVisibility(View.INVISIBLE);
        oldPasswordValidator = new TextFieldValidator(textOldPassword);
        newPasswordValidator = new TextFieldValidator(textNewPassword);
        textOldPassword.getEditText().addTextChangedListener(this);
        textNewPassword.getEditText().addTextChangedListener(this);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.init(sharedViewModel.getUser().getValue());
        userViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        userViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        userViewModel.getNotificationColor().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
               btnChangeColor.getBackground().setTint(i);
            }
        });
        hideButtonsToUserWithoutPermissions(user);
    }


    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }


    private void hideButtonsToUserWithoutPermissions(User user) {
        if (user.getPermission() == User.Permission.READ_ONLY) {
            btnApprovePasswordReset.setVisibility(View.INVISIBLE);
            btnApproveNewUser.setVisibility(View.INVISIBLE);
            btnAdminInteraction.setVisibility(View.INVISIBLE);
        }
        else if (user.getPermission() == User.Permission.ADMIN) {
            btnApprovePasswordReset.setVisibility(View.VISIBLE);
            btnApproveNewUser.setVisibility(View.VISIBLE);
            btnAdminInteraction.setVisibility(View.VISIBLE);
        }
        else {
            btnApprovePasswordReset.setVisibility(View.VISIBLE);
            btnApproveNewUser.setVisibility(View.VISIBLE);
            btnAdminInteraction.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void setUser(User user) {
        super.setUser(user);
        hideButtonsToUserWithoutPermissions(user);
    }

    private boolean checkFieldsValidation(String oldPassword, String newPassword) {
        boolean oldCheck = oldPasswordValidator.comparePasswords(user.getPassword(), oldPassword);
        boolean newCheck = newPasswordValidator.checkNewPassword(newPassword);
        return oldCheck && newCheck;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textOldPassword.setError(null);
        textNewPassword.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnChangePassword:
                String newPassword = textNewPassword.getEditText().getText().toString().trim();
                String oldPassword = textOldPassword.getEditText().getText().toString().trim();
                if (checkFieldsValidation(oldPassword, newPassword)) {
                    userViewModel.editUserPasswordInRepository(newPassword);
                }
                break;
            case R.id.btnChangeColor:
                userViewModel.changeColorOfUserInRepository();
                break;
            case R.id.btnApproveNewUser:
                proceedToNextFragment(30);
                break;
            case R.id.btnApprovePasswordReset:
                proceedToNextFragment(31);
                break;
            case R.id.btnAdminInteraction:
                proceedToNextFragment(32);
                break;
        }
    }
}

