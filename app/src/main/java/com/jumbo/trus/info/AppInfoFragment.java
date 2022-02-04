package com.jumbo.trus.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.BuildConfig;
import com.jumbo.trus.CustomAddFragment;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.user.LoginActivity;
import com.jumbo.trus.validator.AmountTextWatcher;
import com.jumbo.trus.validator.NameTextWatcher;
import com.jumbo.trus.validator.TextFieldValidator;

import java.util.Objects;

public class AppInfoFragment extends CustomUserFragment {

    private static final String TAG = "AppInfoFragment";

    private TextInputLayout textAppInfo;
    private ProgressBar progress_bar;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_info, container, false);
        progress_bar = view.findViewById(R.id.progress_bar);
        textAppInfo = view.findViewById(R.id.textAppInfo);
        textAppInfo.getEditText().setText("Verze aplikace je " + BuildConfig.VERSION_NAME + ", code: " + BuildConfig.VERSION_CODE);
        return view;
    }
}
