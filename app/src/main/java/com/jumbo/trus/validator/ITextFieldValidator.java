package com.jumbo.trus.validator;

import com.jumbo.trus.season.Season;

import java.util.List;

public interface ITextFieldValidator {

    boolean checkNameField(String name);
    boolean checkDateField(String date);
    boolean checkListField(List<? extends Object> list);
    boolean checkEmptyField(String text);
    boolean checkIfDateStartIsBeforeEnd(String dateBeg, String dateEnd);
    boolean checkSeasonOverlap(String dateBeg, String dateEnd, List<Season> seasonList, Season seaon);

}
