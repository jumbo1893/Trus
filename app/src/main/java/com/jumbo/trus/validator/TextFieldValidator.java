package com.jumbo.trus.validator;

import android.util.Log;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.Result;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.PasswordEncryption;
import com.jumbo.trus.user.User;

import java.security.NoSuchAlgorithmException;
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

    public boolean checkAmount(String amount) {
        Result amountResult = validator.checkFineAmount(amount);
        if (!amountResult.isTrue()) {
            textInputLayout.setError(amountResult.getText());
        }
        else {
            textInputLayout.setError(null);
        }
        return amountResult.isTrue();
    }

    public boolean checkNoteField(String text) {
        Result nameResult = validator.checkTextFieldFormat(text, 200);
        if (!nameResult.isTrue()) {
            textInputLayout.setError(nameResult.getText());
        }
        else {
            textInputLayout.setError(null);
        }
        return nameResult.isTrue();
    }

    public boolean checkNewPassword(final String password) {
        if (!validator.fieldIsNotEmpty(password)) {
            textInputLayout.setError("Není vyplněné heslo");
            return false;
        }
        else if (!validator.checkPasswordFormat(password)) {
            textInputLayout.setError("Heslo musí mít mezi dýlku 1 až 30 znaků, tak nevymejšlej píčoviny");
            return false;
        }
        return true;
    }


    public boolean comparePasswords(final String userHashedPassword, final String oldPassword) {
        PasswordEncryption encryption = new PasswordEncryption();
        try {
            if (!encryption.compareHashedPassword(userHashedPassword, oldPassword)) {
                textInputLayout.setError("Nezadal si stejný heslo");
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return true;
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
