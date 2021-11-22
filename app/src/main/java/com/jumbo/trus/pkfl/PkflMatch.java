package com.jumbo.trus.pkfl;

import android.util.Log;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;

public class PkflMatch extends Model {

    private static final String TAG = "PkflMatch";

    private long date;
    private String opponent;
    private int round;
    private String league;
    private String stadium;
    private String referee;
    private String result;
    private boolean homeMatch;

    public PkflMatch(String dateText, String time, String homeTeam, String awayTeam, int round, String league, String stadium, String referee, String result) {
        Date date = new Date();
        this.date = date.convertPkflTextDateToMillis(dateText+ " " + time);
        Log.d(TAG, "PkflMatch: " + getDate());
        if (homeTeam.trim().equals("Liščí trus")) {
            opponent = awayTeam;
            homeMatch = true;
        }
        else {
            opponent = homeTeam;
            homeMatch = false;
        }
        this.round = round;
        this.league = league;
        this.stadium = stadium;
        this.referee = referee;
        this.result = result;
    }

    public long getDate() {
        return date;
    }


    public String getOpponent() {
        return opponent;
    }


    public int getRound() {
        return round;
    }


    public String getLeague() {
        return league;
    }


    public String getStadium() {
        return stadium;
    }

    public String getReferee() {
        return referee;
    }

    public String getResult() {
        return result;
    }

    public boolean isHomeMatch() {
        return homeMatch;
    }

    public String toStringNameWithOpponent() {
        if (homeMatch) {
            return "Liščí trus - " + opponent;
        }
        return opponent + " - Liščí Trus";
    }

    public String getDateAndTimeOfMatchInStringFormat() {
        Date d = new Date();
        return d.convertMillisToStringTimestamp(date);
    }

    public String getDateOfMatchInStringFormat() {
        Date d = new Date();
        return d.convertMillisSecToTextDate(date);
    }
}
