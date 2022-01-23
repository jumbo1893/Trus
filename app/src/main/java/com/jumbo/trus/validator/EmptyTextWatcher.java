package com.jumbo.trus.validator;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class EmptyTextWatcher implements TextWatcher {

    private TextFieldValidator fieldValidator;

    public EmptyTextWatcher(TextFieldValidator fieldValidator) {
        this.fieldValidator = fieldValidator;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        fieldValidator.checkEmptyField(editable.toString());
    }


}
