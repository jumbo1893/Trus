package com.jumbo.trus.info;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.BuildConfig;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;

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
