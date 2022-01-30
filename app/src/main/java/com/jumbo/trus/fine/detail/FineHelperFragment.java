package com.jumbo.trus.fine.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomAddFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.validator.AmountTextWatcher;
import com.jumbo.trus.validator.DateTextWatcher;
import com.jumbo.trus.validator.NameTextWatcher;
import com.jumbo.trus.validator.TextFieldValidator;

import java.util.Objects;

public class FineHelperFragment extends CustomAddFragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "FineHelperFragment";

    protected TextInputLayout textAmount, textName;
    private ProgressBar progress_bar;
    protected AppCompatButton btnCommit, btnDelete;
    protected Switch swPlayer, swNonPlayer;

    private TextFieldValidator nameValidator;
    private TextFieldValidator amountValidator;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fine_plus, container, false);
        progress_bar = view.findViewById(R.id.progress_bar);
        btnCommit = view.findViewById(R.id.btnCommit);
        btnDelete = view.findViewById(R.id.btnDelete);
        swPlayer = view.findViewById(R.id.swPlayer);
        swNonPlayer = view.findViewById(R.id.swNonPlayer);
        textName = view.findViewById(R.id.textName);
        textAmount = view.findViewById(R.id.textAmount);
        nameValidator = new TextFieldValidator(textName);
        amountValidator = new TextFieldValidator(textAmount);
        Objects.requireNonNull(textName.getEditText()).addTextChangedListener(new NameTextWatcher(nameValidator));
        Objects.requireNonNull(textAmount.getEditText()).addTextChangedListener(new AmountTextWatcher(amountValidator));
        btnCommit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        nullValidationsErrors(textName, textAmount);
        setEditTextBold(textName.getEditText(), textAmount.getEditText());
        swPlayer.setOnCheckedChangeListener(this);
        swNonPlayer.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onStart() {
        nullValidationsErrors(textName, textAmount);
        super.onStart();
    }

    protected void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }



    private boolean checkFieldsValidation(String name, String amount) {
        boolean nameCheck = nameValidator.checkNameField(name);
        boolean dateCheck = amountValidator.checkAmount(amount);
        return nameCheck && dateCheck;
    }

    protected void onCommitValidationTrue(final String name, final boolean swNonPlayer, final int amount) {

    }

    @Override
    protected void commitClicked() {
        String name = textName.getEditText().getText().toString();
        String amount = textAmount.getEditText().getText().toString();
        if (checkFieldsValidation(name, amount)) {
            onCommitValidationTrue(name, swNonPlayer.isChecked(), Integer.parseInt(amount));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            switch (compoundButton.getId()) {
                case R.id.swPlayer: {
                    swNonPlayer.setChecked(false);
                    break;
                }
                case R.id.swNonPlayer: {
                    swPlayer.setChecked(false);
                    Log.d(TAG, "onCheckedChanged: " + swPlayer.isChecked());
                    break;
                }
            }
        } else {
            checkNoButtonSwitched(compoundButton);
        }
    }

    private void checkNoButtonSwitched(CompoundButton buttonView) {
        if (!swPlayer.isChecked() && !swNonPlayer.isChecked()) {
            buttonView.setChecked(true);
        }
    }
}
