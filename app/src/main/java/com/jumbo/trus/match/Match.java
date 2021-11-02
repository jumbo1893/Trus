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

    public List<Player> returnPlayerListOnlyWithParticipants() {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            if (player.isMatchParticipant()) {
                newPlayerList.add(player);
            }
        }
        return newPlayerList;
    }

    public List<Player> returnPlayerListWithoutFans() {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            if (!player.isFan()) {
                newPlayerList.add(player);
            }
        }
        return newPlayerList;
    }

    public List<Player> returnPlayerListOnlyWithFine(ReceivedFine receivedFine) {
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
    public int returnNumberOfBeersInMatch() {
        int beerNumber = 0;
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            beerNumber += player.getNumberOfBeers();
        }
        return beerNumber;
    }

    /**
     * @return počet tvrdýho, která se vypila v zápase
     */
    public int returnNumberOfLiquorsInMatch() {
        int liquorNumber = 0;
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            liquorNumber += player.getNumberOfLiquors();
        }
        return liquorNumber;
    }
    /**
     * @return počet účastníků zápasu
     */
    public int returnNumberOfPlayersAndFansInMatch() {
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
    public int returnNumberOfPlayersInMatch() {
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
    public int returnNumberOfBeersInMatchForPlayers() {
        int beerNumber = 0;
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            if (!player.isFan()) {
                beerNumber += player.getNumberOfBeers();
            }
        }
        return beerNumber;
    }

    /**
     * @return počet piv, kteří v zápase vypili hráči
     */
    public int returnNumberOfLiquorsInMatchForPlayers() {
        int liquorNumber = 0;
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            if (!player.isFan()) {
                liquorNumber += player.getNumberOfLiquors();
            }
        }
        return liquorNumber;
    }


    /**
     * @return vrátí celkový počet pokut, které padly v zápase
     */
    public int returnNumberOfFinesInMatch() {
        int fineNumber = 0;
        for (Player player : playerList) {
            fineNumber += player.returnNumberOfAllReceviedFines();
        }
        return fineNumber;
    }

    /**
     * @return vrátí celkovou částku, která padla v zápase za pokuty
     */
    public int returnAmountOfFinesInMatch() {
        int fineAmount = 0;
        for (Player player : playerList) {
            fineAmount += player.returnAmountOfAllReceviedFines();
        }
        return fineAmount;
    }

    /**
     * @param fine Pokuta, jejíž počet chceme nalézt
     * @return kolikrát se tato pokuta udělila v tomto zápase
     */
    public int returnNumberOfReceviedFineInMatch(Fine fine) {
        int count = 0;
        for (Player player : playerList) {
            count += player.returnNumberOfReceviedFine(fine);
        }
        return count;
    }

    /**
     * @param fine Pokuta, u které chceme znát kolik se za ní v tomto zápase vybralo
     * @return počet peněz, které v tomto zápase přinesla tato pokuta
     */
    public int returnAmountOfReceviedFineInMatch(Fine fine) {
        int count = 0;
        for (Player player : playerList) {
            count += player.returnAmountOfReceviedFine(fine);
        }
        return count;
    }

    /**
     * metoda vezme existující playerList u tohoto mače a připojí k němu nový playerlist
     * Stejné hráče nahradí z toho novýho listu, pokud neexistují, tak je přidá
     * @param players noví hráči
     */
    public void mergePlayerLists(List<Player> players) {
        for (Player player : players) {
            playerList.remove(player);
            playerList.add(player);

        }
    }

    /**
     * @return vrátí počet panáků na jednoho účastníka zápasu
     */
    public float returnNumberOfLiquorPerParticipant() {
        return returnNumberOfLiquorsInMatch() / (float) returnNumberOfPlayersAndFansInMatch();
    }

    /**
     * @param player Hráč, kterým chceme prohledat playerlist
     * @return true pokud daný hráč má alespoň jednu pokutu
     */
    public boolean isInMatchPlayerWithFine(Player player) {
        if (!playerList.contains(player)) {
            return false;
        }
        if (playerList.get(playerList.indexOf(player)).returnNumberOfAllReceviedFines() > 0) {
            return true;
        }
        return false;
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
