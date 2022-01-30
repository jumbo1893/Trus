package com.jumbo.trus.validator;

import android.text.Editable;
import android.text.TextWatcher;

public class AmountTextWatcher implements TextWatcher {

    private TextFieldValidator amountValidator;

    public AmountTextWatcher(TextFieldValidator amountValidator) {
        this.amountValidator = amountValidator;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        amountValidator.checkAmount(editable.toString());
    }
}
