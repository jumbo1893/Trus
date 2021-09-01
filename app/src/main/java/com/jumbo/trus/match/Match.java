package com.jumbo.trus.match;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
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

    public List<Player> getPlayerListOnlyWithParticipants() {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            if (player.isMatchParticipant()) {
                newPlayerList.add(player);
            }
        }
        return newPlayerList;
    }

    public List<Player> getPlayerListOnlyWithParticipantsAndWithoutFans() {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            if (player.isMatchParticipant() && !player.isFan()) {
                newPlayerList.add(player);
            }
        }
        return newPlayerList;
    }

    public List<Player> getPlayerListOnlyWithoutParticipantsAndWithoutFans() {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            if (!player.isMatchParticipant() && !player.isFan()) {
                newPlayerList.add(player);
            }
        }
        return newPlayerList;
    }

    public List<Player> getPlayerListWithoutFans() {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            if (!player.isFan()) {
                newPlayerList.add(player);
            }
        }
        return newPlayerList;
    }

    public List<Player> getPlayerListOnlyWithFine(ReceivedFine receivedFine) {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            for (ReceivedFine playerFine : player.getReceivedFines()) {
                if (playerFine.equals(receivedFine) && playerFine.getCount() > 0) {
                    newPlayerList.add(player);
                }
            }
        }
        return newPlayerList;
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
        int playerNumber = 0;
        for (Player player : playerList) {
            if (player.isMatchParticipant()) {
                playerNumber++;
            }
        }
        return playerNumber;
    }

    /**
     * @return počet hráčů v zápase
     */
    public int getNumberOfPlayersInMatch() {
        int playerNumber = 0;
        for (Player player : playerList) {
            if (!player.isFan() && player.isMatchParticipant()) {
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

    /**
     * @param fine Pokuta, jejíž počet chceme nalézt
     * @return kolikrát se tato pokuta udělila v tomto zápase
     */
    public int getNumberOfReceviedFineInMatch(Fine fine) {
        int count = 0;
        for (Player player : playerList) {
            count += player.getNumberOfReceviedFine(fine);
        }
        return count;
    }

    /**
     * @param fine Pokuta, u které chceme znát kolik se za ní v tomto zápase vybralo
     * @return počet peněz, které v tomto zápase přinesla tato pokuta
     */
    public int getAmountOfReceviedFineInMatch(Fine fine) {
        int count = 0;
        for (Player player : playerList) {
            count += player.getAmountOfReceviedFine(fine);
        }
        return count;
    }

    public String toStringNameWithOpponent() {
        if (homeMatch) {
            return "Liščí trus - " + opponent;
        }
        return opponent + " - Liščí Trus";
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
