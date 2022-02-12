package com.jumbo.trus.validator;

import android.text.Editable;
import android.text.TextWatcher;

public class NameTextWatcher implements TextWatcher {

    private TextFieldValidator nameValidator;

    public NameTextWatcher(TextFieldValidator nameValidator) {
        this.nameValidator = nameValidator;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        nameValidator.checkNameField(editable.toString());
    }
}
