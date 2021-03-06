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

    /** vypo????t?? a p??i??ad?? spr??vnou sezonu k z??pasu. Pokud ????dn?? neodpov??d??, p??i??ad?? se sezona ostatn??
     * @param seasonList seznam dostupn??ch sezon
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
     * najde hr????e v seznamu hr?????? a p??i??ad?? mu nov?? pokuty
     * @param player Hr????, kter??mu se m?? zm??nit pokuta
     * @param fineList seznam nov??ch pokut
     * @return true pokud je hr???? nalezen
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
     * @return po??et piv, kter?? se vypila v z??pase
     */
    public int returnNumberOfBeersInMatch() {
        int beerNumber = 0;
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            beerNumber += player.getNumberOfBeers();
        }
        return beerNumber;
    }

    /**
     * @return po??et tvrd??ho, kter?? se vypila v z??pase
     */
    public int returnNumberOfLiquorsInMatch() {
        int liquorNumber = 0;
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            liquorNumber += player.getNumberOfLiquors();
        }
        return liquorNumber;
    }

    /**
     * @return po??et piv, kter?? se vypila v z??pase
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
     * @return po??et ????astn??k?? z??pasu
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
     * @return po??et hr?????? v z??pase
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
     * @return po??et piv, kte???? v z??pase vypili hr????i
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
     * @return po??et piv, kte???? v z??pase vypili hr????i
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
     * @return vr??t?? celkov?? po??et pokut, kter?? padly v z??pase
     */
    public int returnNumberOfFinesInMatch() {
        int fineNumber = 0;
        for (Player player : playerList) {
            fineNumber += player.returnNumberOfAllReceviedFines();
        }
        return fineNumber;
    }

    /**
     * @return vr??t?? celkovou ????stku, kter?? padla v z??pase za pokuty
     */
    public int returnAmountOfFinesInMatch() {
        int fineAmount = 0;
        for (Player player : playerList) {
            fineAmount += player.returnAmountOfAllReceviedFines();
        }
        return fineAmount;
    }

    /**
     * @param fine Pokuta, jej???? po??et chceme nal??zt
     * @return kolikr??t se tato pokuta ud??lila v tomto z??pase
     */
    public int returnNumberOfReceviedFineInMatch(Fine fine) {
        int count = 0;
        for (Player player : playerList) {
            count += player.returnNumberOfReceviedFine(fine);
        }
        return count;
    }

    /**
     * @param fine Pokuta, u kter?? chceme zn??t kolik se za n?? v tomto z??pase vybralo
     * @return po??et pen??z, kter?? v tomto z??pase p??inesla tato pokuta
     */
    public int returnAmountOfReceviedFineInMatch(Fine fine) {
        int count = 0;
        for (Player player : playerList) {
            count += player.returnAmountOfReceviedFine(fine);
        }
        return count;
    }


    /**
     * @param fine Pokuta, u kter?? chceme zn??t kolik se za n?? v tomto z??pase vybralo
     * @param player Hr???? u kter??ho zji????ujeme tu v????i
     * @return po??et pen??z kter?? vynesla tato pokuta u tohoto hr????e
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
     * metoda vezme existuj??c?? playerList u tohoto ma??e a p??ipoj?? k n??mu nov?? playerlist
     * Stejn?? hr????e nahrad?? z toho nov??ho listu, pokud neexistuj??, tak je p??id??
     * @param players nov?? hr????i
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
     * @return vr??t?? po??et pan??k?? na jednoho ????astn??ka z??pasu
     */
    public float returnNumberOfLiquorPerParticipant() {
        return returnNumberOfLiquorsInMatch() / (float) returnNumberOfPlayersAndFansInMatch();
    }

    /**
     * @param player Hr????, kter??m chceme prohledat playerlist
     * @return true pokud dan?? hr???? m?? alespo?? jednu pokutu
     */
    public boolean isInMatchPlayerWithFine(Player player) {
        if (!playerList.contains(player)) {
            return false;
        }
        return playerList.get(playerList.indexOf(player)).returnNumberOfAllReceviedFines() > 0;
    }

    /**
     * @param player hr???? u kter??ho chceme zn??t po??et
     * @return vr??t?? po??et vypit??ho chlastu v z??pase
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
     * @param player hr???? u kter??ho chceme zn??t ????stku
     * @return vr??t?? ????stku pokuty v z??pase
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
            return "Li?????? trus - " + opponent;
        }
        return opponent + " - Li?????? Trus";
    }

    public String compareIfMatchWasChanged(List<Integer> beerCompensation,  List<Integer> liquorCompensation, Match match) {
        Log.d(TAG, "compareIfMatchWasChanged: ");
        if (!equalsForPlayerList(match.getPlayerList())) {
            Log.d(TAG, "compareIfMatchWasChanged: hr????i");
            return "Byla provedena zm??na v seznamu hr??????, mus??m to reloadnout";
        }
        if (!equalsBeerAndLiquorCompensation(beerCompensation, liquorCompensation)) {
            Log.d(TAG, "compareIfMatchWasChanged: piva");
            return "N??kdo pr??v?? na????ral nov?? piva v aktu??ln?? zobrazen??m z??pase, mus??m to reloadnout";
        }
        else if (!equalsForMatchDetails(match)) {
            Log.d(TAG, "compareIfMatchWasChanged: ma??");
            return "N??jakej inteligent pr??v?? zm??nil aktu??ln?? zobrazen?? z??pas, mus??m to reloadnout";
        }
        return null;
    }

    public void createListOfPlayers(List<Player> playerList, List<Player> allPlayerList) {//nov??, p??vodn??
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

    public void createListOfPlayersWithOriginalPlayers(List<Player> playerList, List<Player> allPlayerList) {//nov??, p??vodn??

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
     * Funkce porovn??v?? po????te??n?? stavy piv. Pom????e rozpoznat, jestli v mezi??ase kdy m?? u??ivatel otev??en?? ????rky ne????rkoval n??kdo jinej
     * @param beerCompensation p??vodn?? ????rky piva
     * @param liquorCompensation p??vodn?? ????rky chlastu
     * @return true pokud se po????te??n?? stavy piv neli????, false pokud se li????
     */
    private boolean equalsBeerAndLiquorCompensation(List<Integer> beerCompensation,  List<Integer> liquorCompensation) {
        List<Integer> compareBeerCompensation = new ArrayList<>();
        List<Integer> compareLiquorCompensation = new ArrayList<>();
        for (Player player : returnPlayerListOnlyWithParticipants()) {
            compareBeerCompensation.add(player.getNumberOfBeers());
            compareLiquorCompensation.add(player.getNumberOfLiquors());
        }
        Log.d(TAG, "equalsBeerAndLiquorCompensation: p??vodn?? kompenzace \n" + beerCompensation + " nov?? pivka \n" + compareBeerCompensation);
        return compareBeerCompensation.equals(beerCompensation) && compareLiquorCompensation.equals(liquorCompensation);
    }

    /**
     * Funkce porovn??v?? po????te??n?? pokut. Pom????e rozpoznat, jestli v mezi??ase kdy m?? u??ivatel otev??enou editaci z??pasu neud??lal zm??ny n??kdo jinej
     * @param fineCompensation p??vodn?? pokuty
     * @return true pokud se po????te??n?? stavy pokut neli????, false pokud se li????
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
     * Funkce porovn??v?? porovn??v?? 2 z??pasy dle detail?? co se daj?? navolit p??i ??prav?? z??pasu
     * @param match z??pas k porovn??n??
     * @return true pokud se po????te??n?? stavy neli????, false pokud se li????
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
