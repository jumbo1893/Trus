package com.jumbo.trus.match;

import android.util.Log;

import com.jumbo.trus.Date;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.Model;
import com.jumbo.trus.player.Player;

import java.util.List;

public class Match extends Model {

    private static final String TAG = "Match";

    private String opponent;
    private long dateOfMatch;
    private boolean homeMatch;
    private Season season;
    private List<Player> playerList;

    public Match(String opponent, long dateOfMatch, boolean homeMatch, Season season, List<Player> playerList) {
        super(opponent);
        this.opponent = opponent;
        this.dateOfMatch = dateOfMatch;
        this.homeMatch = homeMatch;
        this.season = season;
        this.playerList = playerList;
    }

    public Match(String opponent, long dateOfMatch, boolean homeMatch, List<Player> playerList, List<Season> seasonList) {
        super(opponent);
        this.opponent = opponent;
        this.dateOfMatch = dateOfMatch;
        this.homeMatch = homeMatch;
        calculateSeason(seasonList);
        this.playerList = playerList;
    }

    public Match() {
    }


    public void setName(String opponent) {
        name = (opponent);
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
        setName(opponent);
    }

    public long getDateOfMatch() {
        return dateOfMatch;
    }

    public void setDateOfMatch(long dateOfMatch) {
        this.dateOfMatch = dateOfMatch;
    }

    public boolean isHomeMatch() {
        return homeMatch;
    }

    public void setHomeMatch(boolean homeMatch) {
        this.homeMatch = homeMatch;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public String getDateOfMatchInStringFormat() {
        Date date = new Date();
        return date.convertMillisToTextDate(dateOfMatch);
    }

    /** vypočítá a přiřadí správnou sezonu k zápasu. Pokud žádná neodpovídá, přiřadí se sezona ostatní
     * @param seasonList seznam dostupných sezon
     */
    public void calculateSeason(List<Season> seasonList) {
        for (Season season : seasonList) {
            if (season.getSeasonStart() <= dateOfMatch && season.getSeasonEnd() >= dateOfMatch) {
                this.season = season;
                return;
            }
        }
        season = seasonList.get(seasonList.size()-1);
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    /**
     * najde hráče v seznamu hráčů a přiřadí mu nové pokuty
     * @param player Hráč, kterému se má změnit pokuta
     * @param fineList seznam nových pokut
     * @return true pokud je hráč nalezen
     */
    public boolean changePlayerFinesInPlayerList(Player player, List<ReceivedFine> fineList) {
        for (int i = 0; i < playerList.size(); i++) {
            if (player.equals(playerList.get(i))) {
                playerList.get(i).setReceivedFines(fineList);
                return true;
            }
        }
        return false;
    }

    /**
     * @return počet piv, která se vypila v zápase
     */
    public int getNumberOfBeersInMatch() {
        int beerNumber = 0;
        for (Player player : playerList) {
            beerNumber += player.getNumberOfBeers();
        }
        return beerNumber;
    }
    /**
     * @return počet účastníků zápasu
     */
    public int getNumberOfPlayersAndFansInMatch() {
        return playerList.size();
    }

    /**
     * @return počet hráčů v zápase
     */
    public int getNumberOfPlayersInMatch() {
        int playerNumber = 0;
        for (Player player : playerList) {
            if (!player.isFan()) {
                playerNumber++;
            }
        }
        return playerNumber;
    }

    /**
     * @return počet piv, kteří v zápase vypili hráči
     */
    public int getNumberOfBeersInMatchForPlayers() {
        int beerNumber = 0;
        for (Player player : playerList) {
            if (!player.isFan()) {
                beerNumber += player.getNumberOfBeers();
            }
        }
        return beerNumber;
    }


    /**
     * @return vrátí celkový počet pokut, které padly v zápase
     */
    public int getNumberOfFinesInMatch() {
        int fineNumber = 0;
        for (Player player : playerList) {
            fineNumber += player.getNumberOfAllReceviedFines();
        }
        return fineNumber;
    }

    /**
     * @return vrátí celkovou částku, která padla v zápase za pokuty
     */
    public int getAmountOfFinesInMatch() {
        int fineAmount = 0;
        for (Player player : playerList) {
            fineAmount += player.getAmountOfAllReceviedFines();
        }
        return fineAmount;
    }

    public String toStringNameWithOpponent() {
        if (homeMatch) {
            return "Liščí trus - " + opponent;
        }
        return opponent + " - Liščí Trus";
    }

    public String toStringForStatisticsBeerRecycleView() {
        if (homeMatch) {
            return "Liščí trus - " + opponent + ", počet pivek: " + getNumberOfBeersInMatch();
        }
        return opponent + " - Liščí Trus, počet pivek: " + getNumberOfBeersInMatch();
    }

    public String toStringForStatisticsFineRecycleView() {
        if (homeMatch) {
            return "Liščí trus - " + opponent + ", počet pokut: " + getNumberOfFinesInMatch() + " v celkové výši: " + getAmountOfFinesInMatch() + " Kč";
        }
        return opponent + " - Liščí Trus, počet pokut: " + getNumberOfFinesInMatch() + " v celkové výši: " + getAmountOfFinesInMatch()  + " Kč";
    }

    @Override
    public String toString() {
        return "Match{" +
                "opponent='" + opponent + '\'' +
                ", dateOfMatch=" + dateOfMatch +
                ", homeMatch=" + homeMatch +
                '}';
    }
}
