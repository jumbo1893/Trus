package com.jumbo.trus.validator;

import android.util.Log;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.Result;
import com.jumbo.trus.season.Season;

import java.util.List;

public class TextFieldValidator implements ITextFieldValidator {

    private TextInputLayout textInputLayout;
    private Validator validator;

    public TextFieldValidator(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
        this.validator = new Validator();
    }

    @Override
    public boolean checkNameField(String name) {
        Result nameResult = validator.checkNameValidation(name);
        if (!nameResult.isTrue()) {
            textInputLayout.setError(nameResult.getText());
        }
        else {
            textInputLayout.setError(null);
        }
        return nameResult.isTrue();
    }

    @Override
    public boolean checkDateField(String date) {
        Result nameResult = validator.checkDateValidation(date);
        if (!nameResult.isTrue()) {
            textInputLayout.setError(nameResult.getText());
        }
        else {
            textInputLayout.setError(null);
        }
        return nameResult.isTrue();
    }

    @Override
    public boolean checkListField(List<? extends Object> list) {
        Result nameResult = validator.checkEmptyListValidation(list);
        if (!nameResult.isTrue()) {
            textInputLayout.setError(nameResult.getText());
        }
        else {
            textInputLayout.setError(null);
        }
        return nameResult.isTrue();
    }

    @Override
    public boolean checkEmptyField(String text) {
        Result fieldResult = validator.checkEmptyField(text);
        if (!fieldResult.isTrue()) {
            textInputLayout.setError(fieldResult.getText());
        }
        else {
            textInputLayout.setError(null);
        }
        return fieldResult.isTrue();
    }

    @Override
    public boolean checkIfDateStartIsBeforeEnd(String dateBeg, String dateEnd) {
        return false;
    }

    @Override
    public boolean checkSeasonOverlap(String dateBeg, String dateEnd, List<Season> seasonList, Season seaon) {
        return false;
    }
}
