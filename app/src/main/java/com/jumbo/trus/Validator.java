package com.jumbo.trus;

import android.widget.EditText;

import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.player.Player;
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
        if (text.isEmpty()) {
            return false;
        }
        return true;
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
        String regex = "^[a-zA-Z0-9_ áčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ-]{0,100}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    public boolean checkIfStartIsBeforeEnd(String dateStart, String dateEnd) {
        Date date = new Date();
        long millisStart = date.convertTextDateToMillis(dateStart);
        long millisEnd = date.convertTextDateToMillis(dateEnd);
        if (millisEnd < millisStart) {
            return false;
        }
        return true;
    }

    public boolean checkSeasonOverlap(String seasonStart, String seasonEnd, List<Season> seasons, Season currentSeason) {
        Date date = new Date();
        long millisStart = date.convertTextDateToMillis(seasonStart);
        long millisEnd = date.convertTextDateToMillis(seasonEnd);
        if (currentSeason != null) {
            seasons.remove(currentSeason);
        }
        for (int i = 0; i < seasons.size(); i++) {
            Season season = seasons.get(i);
            if (millisStart >= season.getSeasonStart() && millisStart <= season.getSeasonEnd()) {
                return false;
            }
            else if (millisEnd >= season.getSeasonStart() && millisEnd <= season.getSeasonEnd()) {
                return false;
            }
            else if (millisStart < season.getSeasonStart() && millisEnd > season.getSeasonEnd()) {
                return false;
            }

        }
        return true;
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

    public boolean checkEqualityOfFine(String name, int amount, Fine currentFine) {
        if (currentFine == null) {
            return true;
        }
        Fine fine = new Fine(name, amount);
        return !(currentFine.equals(fine));
    }

    public boolean zvalidujPocetPiv (EditText piva) {
        String regex = "^[0-9]{0,2}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(piva.getText().toString());
        if (m.matches()) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean checkAmount(int amount) {
        String regex = "^[0-9]{0,5}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(String.valueOf(amount));
        if (m.matches()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isListEmpty(List<? extends Model> list) {
        if (list.isEmpty()) {
            return true;
        }
        return false;
    }
}
