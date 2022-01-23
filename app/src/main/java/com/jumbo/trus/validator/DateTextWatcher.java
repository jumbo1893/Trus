package com.jumbo.trus.validator;

import android.text.Editable;
import android.text.TextWatcher;

public class DateTextWatcher implements TextWatcher {

    private TextFieldValidator dateValidator;

    public DateTextWatcher(TextFieldValidator dateValidator) {
        this.dateValidator = dateValidator;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        dateValidator.checkDateField(editable.toString());
    }
}
