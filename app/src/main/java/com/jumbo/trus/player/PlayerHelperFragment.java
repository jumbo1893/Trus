package com.jumbo.trus.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomAddFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.validator.DateTextWatcher;
import com.jumbo.trus.validator.NameTextWatcher;
import com.jumbo.trus.validator.TextFieldValidator;

import java.util.Objects;

public class PlayerHelperFragment extends CustomAddFragment {

    private static final String TAG = "MatchHelperFragment";

    protected TextInputLayout textCalendar, textName;
    private AutoCompleteTextView tvCalendar;
    private ProgressBar progress_bar;
    protected AppCompatButton btnCommit, btnDelete;
    protected Switch swFan;

    private TextFieldValidator nameValidator;
    private TextFieldValidator dateValidator;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_plus, container, false);
        progress_bar = view.findViewById(R.id.progress_bar);
        btnCommit = view.findViewById(R.id.btnCommit);
        btnDelete = view.findViewById(R.id.btnDelete);
        swFan = view.findViewById(R.id.swFan);
        textName = view.findViewById(R.id.textName);
        textCalendar = view.findViewById(R.id.textCalendar);
        tvCalendar = view.findViewById(R.id.tvCalendar);
        nameValidator = new TextFieldValidator(textName);
        dateValidator = new TextFieldValidator(textCalendar);
        Objects.requireNonNull(textName.getEditText()).addTextChangedListener(new NameTextWatcher(nameValidator));
        Objects.requireNonNull(textCalendar.getEditText()).addTextChangedListener(new DateTextWatcher(dateValidator));
        btnCommit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        nullValidationsErrors(textName, textCalendar);
        setEditTextBold(textName.getEditText(), textCalendar.getEditText());
        textCalendar.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCalendarDialog(textCalendar.getEditText());
            }
        });
        setTodaysDate(textCalendar.getEditText());
        tvCalendar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayCalendarDialog(textCalendar.getEditText());
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        nullValidationsErrors(textName, textCalendar);
        super.onStart();
    }

    protected void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }



    private boolean checkFieldsValidation(String name, String date) {
        boolean nameCheck = nameValidator.checkNameField(name);
        boolean dateCheck = dateValidator.checkDateField(date);
        return nameCheck && dateCheck;
    }

    protected void onCommitValidationTrue(final String name, final boolean swFan, final String date) {

    }

    @Override
    protected void commitClicked() {
        String name = textName.getEditText().getText().toString();
        String date = textCalendar.getEditText().getText().toString();
        if (checkFieldsValidation(name, date)) {
            onCommitValidationTrue(name, swFan.isChecked(), date);
        }
    }
}
