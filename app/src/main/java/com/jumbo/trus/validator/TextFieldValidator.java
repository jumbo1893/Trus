package com.jumbo.trus.validator;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.Result;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.PasswordEncryption;

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
            textInputLayout.setError("Nen?? vypln??n?? heslo");
            return false;
        }
        else if (!validator.checkPasswordFormat(password)) {
            textInputLayout.setError("Heslo mus?? m??t mezi d??lku 1 a?? 30 znak??, tak nevymej??lej p????oviny");
            return false;
        }
        return true;
    }


    public boolean comparePasswords(final String userHashedPassword, final String oldPassword) {
        PasswordEncryption encryption = new PasswordEncryption();
        try {
            if (!encryption.compareHashedPassword(userHashedPassword, oldPassword)) {
                textInputLayout.setError("Nezadal si stejn?? heslo");
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
