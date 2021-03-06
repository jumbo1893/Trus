package com.jumbo.trus.validator;

import android.util.Log;
import android.widget.EditText;

import com.jumbo.trus.Date;
import com.jumbo.trus.Result;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.season.Season;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private static final String TAG = "Validator";

    //konstruktor
    public Validator() {

    }

    public boolean fieldIsNotEmpty (String text) {
        return !text.isEmpty();
    }

    public boolean isDateInCorrectFormat (String datum) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Date.DATE_PATTERN);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(datum);
        }
        catch (ParseException e) {
            return false;
        }
        return true;
    }

    public boolean checkNameFormat (String name) {
        String regex = "^[a-zA-Z0-9_ áčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ.()-]{0,100}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    public boolean checkFieldFormat (String name, int nameLength) {
        String regex = "^[a-zA-Z0-9_ áčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ-]{0," + nameLength + "}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    public boolean checkPasswordFormat (String password) {
        String regex = "^[a-zA-Z0-9_.! áčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ-]{1,30}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public boolean checkEmailFormat (String mail) {
        String regex = "^\\S+@\\S+\\.\\S+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mail);
        return m.matches();
    }

    public boolean checkEqualityOfSeason(String name, String seasonStart, String seasonEnd, Season currentSeason) {
        if (currentSeason == null) {
            return true;
        }
        Date date = new Date();
        long millisStart = date.convertTextDateToMillis(seasonStart);
        long millisEnd = date.convertTextDateToMillis(seasonEnd);
        Season season = new Season(name, millisStart, millisEnd);
        return !(currentSeason.equalsForSeasonsFields(season));
    }

    public boolean checkEqualityOfFine(String name, int amount, boolean forNonPlayers, Fine currentFine) {
        if (currentFine == null) {
            return true;
        }
        Fine fine = new Fine(name, amount, forNonPlayers);
        return !(currentFine.equals(fine));
    }

    public boolean zvalidujPocetPiv (EditText piva) {
        String regex = "^[0-9]{0,2}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(piva.getText().toString());
        return !m.matches();
    }

    public boolean checkAmount(int amount) {
        String regex = "^[0-9]{0,5}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(String.valueOf(amount));
        return m.matches();
    }

    public Result checkFineAmount(String amount) {
        String response = "";
        boolean result = false;
        if (!fieldIsNotEmpty(amount)) {
            response = "Není vyplněná částka";
            return new Result(false, response);
        }
        String regex = "^[0-9]{0,5}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(amount);
        if (!m.matches()) {
            response = "Částka je moc vysoká nebo sou v ní nesmysly";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result checkTextFieldFormat(String name, int nameLength) {
        String regex = "^[a-zA-Z0-9_ áčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ-]{0," + nameLength + "}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        if (m.matches()) {
            return new Result(true, "");
        }
        return new Result(false, "Text je moc dlouhý nebo obsahuje nesmysly");
    }

    public boolean isListEmpty(List<? extends Object> list) {
        return list == null || list.isEmpty();
    }

    public Result checkNameValidation(final String name) {
        String response = "";
        boolean result = false;
        if (!fieldIsNotEmpty(name)) {
            response = "Není vyplněné jméno";
        }
        else if (!checkNameFormat(name)) {
            response = "Jméno je moc dlouhý nebo obsahuje nesmysly";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result checkDateValidation(final String datum) {
        String response = "";
        boolean result = false;
        if (!fieldIsNotEmpty(datum)) {
            response = "Není vyplněné datum";
        }
        else if (!isDateInCorrectFormat(datum)) {
            response = "Datum musí být ve formátu " + Date.DATE_PATTERN;
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result checkEmptyListValidation(final List<? extends Object> list) {
        String response = "";
        boolean result = false;
        if (isListEmpty(list)) {
            response = "Tadyto musíš vyplnit";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result checkEmptyField(String text) {
        Log.d(TAG, "checkEmptyField: " + text);
        String response = "";
        boolean result = false;
        if (!fieldIsNotEmpty(text)) {
            response = "Tadyto musíš vyplnit";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result checkIfStartIsBeforeEnd(String dateStart, String dateEnd) {
        Date date = new Date();
        String response = "";
        boolean result = false;
        long millisStart = date.convertTextDateToMillis(dateStart);
        long millisEnd = date.convertTextDateToMillis(dateEnd);
        if (millisEnd < millisStart) {
            response = "Počáteční datum nesmí být starší než konečné datum";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result checkSeasonOverlap(String seasonStart, String seasonEnd, List<Season> seasons, Season currentSeason) {
        Date date = new Date();
        String response = "";
        long millisStart = date.convertTextDateToMillis(seasonStart);
        long millisEnd = date.convertTextDateToMillis(seasonEnd);
        if (currentSeason != null) {
            seasons.remove(currentSeason);
        }
        for (int i = 0; i < seasons.size(); i++) {
            Season season = seasons.get(i);
            if ((millisStart >= season.getSeasonStart() && millisStart <= season.getSeasonEnd()) || (millisEnd >= season.getSeasonStart() && millisEnd <= season.getSeasonEnd()) || (millisStart < season.getSeasonStart() && millisEnd > season.getSeasonEnd())) {
                response = "Datum se kryje s jinými sezonami";
                return new Result(false, response);
            }

        }
        return new Result(true, response);
    }
}
