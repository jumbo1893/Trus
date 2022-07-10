package com.jumbo.trus.match;

import android.util.Log;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Match(String opponent, long dateOfMatch, boolean homeMatch, List<Season> seasonList) {
        super(opponent);
        this.opponent = opponent;
        this.dateOfMatch = dateOfMatch;
        this.homeMatch = homeMatch;
        calculateSeason(seasonList);
        this.playerList = new ArrayList<>();
    }

    public Match(String opponent, long dateOfMatch, boolean homeMatch, Season season) {
        super(opponent);
        this.opponent = opponent;
        this.dateOfMatch = dateOfMatch;
        this.homeMatch = homeMatch;
        this.season = season;
        this.playerList = new ArrayList<>();
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

    public List<Player> returnPlayerOrFansListOnlyWithParticipants(boolean isFan) {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : playerList) {
            if (player.isMatchParticipant()) {
                if (isFan == player.isFan()) {
                    newPlayerList.add(player);
                }
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

    public String returnDateOfMatchInStringFormat() {
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
        season = new Season().otherSeason();
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
     * @return počet piv, která se vypila v zápase
     */
    public int returnNumberOfBeersAndLiquorsInMatch() {
        int boozeNumber = 0;
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            boozeNumber += player.getNumberOfBeers();
            boozeNumber += player.getNumberOfLiquors();
        }
        return boozeNumber;
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
     * @param fine Pokuta, u které chceme znát kolik se za ní v tomto zápase vybralo
     * @param player Hráč u kterýho zjišťujeme tu výši
     * @return počet peněz které vynesla tato pokuta u tohoto hráče
     */
    public int returnAmountOfReceviedFineInMatch(Fine fine, Player player) {
        int count = 0;
        for (Player playerInMatch : playerList) {
            if (playerInMatch.equals(player)) {
                return player.returnAmountOfReceviedFine(fine);
            }
        }
        return 0;
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

    public void mergePlayerListsWithoutReplace(List<Player> players) {
        for (Player player : players) {
            if (!playerList.contains(player)) {
                player.setMatchParticipant(false);
                playerList.add(player);
            }
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
        return playerList.get(playerList.indexOf(player)).returnNumberOfAllReceviedFines() > 0;
    }

    /**
     * @param player hráč u kterýho chceme znát počet
     * @return vrátí počet vypitýho chlastu v zápase
     */
    public int returnNumberOfBeersAndLiquorsForPlayer(Player player) {
        for (Player playerInMatch : playerList) {
            if (playerInMatch.equals(player)) {
                return playerInMatch.getNumberOfBeers()+playerInMatch.getNumberOfLiquors();
            }
        }
        return 0;
    }

    public int returnNumberOfBeersForPlayer(Player player) {
        for (Player playerInMatch : playerList) {
            if (playerInMatch.equals(player)) {
                return playerInMatch.getNumberOfBeers();
            }
        }
        return 0;
    }

    public int returnNumberOfLiquorsForPlayer(Player player) {
        for (Player playerInMatch : playerList) {
            if (playerInMatch.equals(player)) {
                return playerInMatch.getNumberOfLiquors();
            }
        }
        return 0;
    }

    /**
     * @param player hráč u kterýho chceme znát částku
     * @return vrátí částku pokuty v zápase
     */
    public int returnAmountOfFinesInMatch(Player player) {
        for (Player playerInMatch : playerList) {
            if (playerInMatch.equals(player)) {
                return playerInMatch.returnAmountOfAllReceviedFines();
            }
        }
        return 0;
    }

    public Player returnPlayerFromMatch(Player player) {
        for (Player matchPlayer : playerList) {
            if (player.equals(matchPlayer)) {
                return matchPlayer;
            }
        }
        return null;
    }

    public String toStringNameWithOpponent() {
        if (homeMatch) {
            return "Liščí trus - " + opponent;
        }
        return opponent + " - Liščí Trus";
    }

    public String compareIfMatchWasChanged(List<Integer> beerCompensation,  List<Integer> liquorCompensation, Match match) {
        Log.d(TAG, "compareIfMatchWasChanged: ");
        if (!equalsForPlayerList(match.getPlayerList())) {
            Log.d(TAG, "compareIfMatchWasChanged: hráči");
            return "Byla provedena změna v seznamu hráčů, musím to reloadnout";
        }
        if (!equalsBeerAndLiquorCompensation(beerCompensation, liquorCompensation)) {
            Log.d(TAG, "compareIfMatchWasChanged: piva");
            return "Někdo právě načáral nový piva v aktuálně zobrazeném zápase, musím to reloadnout";
        }
        else if (!equalsForMatchDetails(match)) {
            Log.d(TAG, "compareIfMatchWasChanged: mač");
            return "Nějakej inteligent právě změnil aktuálně zobrazený zápas, musím to reloadnout";
        }
        return null;
    }

    public void createListOfPlayers(List<Player> playerList, List<Player> allPlayerList) {//nový, původní
        this.playerList.clear();
        for (Player player : playerList) {
            player.setMatchParticipant(true);
            this.playerList.add(player);
        }
        for (Player player : allPlayerList) {
            if (!this.playerList.contains(player)) {
                player.setMatchParticipant(false);
                this.playerList.add(player);
            }
        }
        Log.d(TAG, "createListOfPlayers: " + this.playerList);
    }

    public void createListOfPlayersWithOriginalPlayers(List<Player> playerList, List<Player> allPlayerList) {//nový, původní

        for (Player player : allPlayerList) {
            setPlayerMatchParticipantByList(player, playerList.contains(player));
        }
        Log.d(TAG, "createListOfPlayers: " + this.playerList);
    }

    private void setPlayerMatchParticipantByList(Player player, boolean participant) {
        for (Player currentPlayer : this.playerList) {
            if (player.equals(currentPlayer)) {
                currentPlayer.setMatchParticipant(participant);
                return;
            }
        }
        player.setMatchParticipant(participant);
        this.playerList.add(player);
    }

    /**
     * Funkce porovnává počáteční stavy piv. Pomůže rozpoznat, jestli v mezičase kdy má uživatel otevřený čárky nečárkoval někdo jinej
     * @param beerCompensation původní čárky piva
     * @param liquorCompensation původní čárky chlastu
     * @return true pokud se počáteční stavy piv neliší, false pokud se liší
     */
    private boolean equalsBeerAndLiquorCompensation(List<Integer> beerCompensation,  List<Integer> liquorCompensation) {
        List<Integer> compareBeerCompensation = new ArrayList<>();
        List<Integer> compareLiquorCompensation = new ArrayList<>();
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            compareBeerCompensation.add(player.getNumberOfBeers());
            compareLiquorCompensation.add(player.getNumberOfLiquors());
        }
        Log.d(TAG, "equalsBeerAndLiquorCompensation: původní kompenzace \n" + beerCompensation + " nový pivka \n" + compareBeerCompensation);
        return compareBeerCompensation.equals(beerCompensation) && compareLiquorCompensation.equals(liquorCompensation);
    }

    /**
     * Funkce porovnává počáteční pokut. Pomůže rozpoznat, jestli v mezičase kdy má uživatel otevřenou editaci zápasu neudělal změny někdo jinej
     * @param fineCompensation původní pokuty
     * @return true pokud se počáteční stavy pokut neliší, false pokud se liší
     */
    private boolean equalsFineCompensation(List<List<Integer>> fineCompensation) {

        for (int i = 0; i < returnPlayerListWithoutFans().size(); i++) {
            if (!returnPlayerListWithoutFans().get(i).compareNumberOfReceivedFines(fineCompensation.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Funkce porovnává porovnává 2 zápasy dle detailů co se dají navolit při úpravě zápasu
     * @param match zápas k porovnání
     * @return true pokud se počáteční stavy neliší, false pokud se liší
     */
    private boolean equalsForMatchDetails(Match match) {
        return (opponent.equals(match.getOpponent()) && dateOfMatch == match.getDateOfMatch() && homeMatch == match.isHomeMatch() && season.equals(match.getSeason()));
    }

    private boolean equalsForPlayerList(List<Player> playerList) {
        Log.d(TAG, "equalsForPlayerList: ");
        boolean result = true;
        for (Player player : playerList) {
            if (this.playerList.contains(player)) {
                if (returnPlayerFromMatch(player).isMatchParticipant() != player.isMatchParticipant()) {
                    result = false;
                    break;
                }
            }
            else {
                this.playerList.add(player);
                result = false;
            }
        }
        return result;
    }


    public boolean equalsByOpponentName (Match match) {
        if (this == match) return true;
        if (match == null || getClass() != match.getClass()) return false;
        String opponent = match.getOpponent();
        return Objects.equals(this.opponent, opponent);
    }

    @Override
    public String toString() {
        return toStringNameWithOpponent();
    }
}
