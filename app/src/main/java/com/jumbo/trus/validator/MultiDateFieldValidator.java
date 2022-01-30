package com.jumbo.trus.validator;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.Result;
import com.jumbo.trus.season.Season;

import java.util.List;

public class MultiDateFieldValidator implements ITextFieldValidator {

    private TextInputLayout dateInputLayoutBeg;
    private TextInputLayout dateInputLayoutEnd;
    private Validator validator;

    public MultiDateFieldValidator(TextInputLayout dateInputLayoutBeg, TextInputLayout dateInputLayoutEnd) {
        this.dateInputLayoutBeg = dateInputLayoutBeg;
        this.dateInputLayoutEnd = dateInputLayoutEnd;
        this.validator = new Validator();
    }

    @Override
    public boolean checkNameField(String name) {
        return false;
    }

    @Override
    public boolean checkDateField(String date) {
        return false;
    }

    @Override
    public boolean checkListField(List<?> list) {
        return false;
    }

    @Override
    public boolean checkEmptyField(String text) {
        return false;
    }

    @Override
    public boolean checkIfDateStartIsBeforeEnd(String dateBeg, String dateEnd) {
        Result result = validator.checkIfStartIsBeforeEnd(dateBeg, dateEnd);
        if (!result.isTrue()) {
            dateInputLayoutBeg.setError(result.getText());
            dateInputLayoutEnd.setError(result.getText());
        }
        else {
            dateInputLayoutBeg.setError(null);
            dateInputLayoutEnd.setError(null);
        }
        return result.isTrue();
    }

    @Override
    public boolean checkSeasonOverlap(String dateBeg, String dateEnd, List<Season> seasonList, Season season) {
        if (seasonList != null) {
            Result nameResult = validator.checkSeasonOverlap(dateBeg, dateEnd, seasonList, season);
            if (!nameResult.isTrue()) {
                dateInputLayoutBeg.setError(nameResult.getText());
                dateInputLayoutEnd.setError(nameResult.getText());
            } else {
                dateInputLayoutBeg.setError(null);
                dateInputLayoutEnd.setError(null);
            }
            return nameResult.isTrue();
        }
        dateInputLayoutEnd.setError("Chyba při načítání sezon z db, zkus to později");
        return false;
    }
}
