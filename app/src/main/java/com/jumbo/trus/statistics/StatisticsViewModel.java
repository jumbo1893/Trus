package com.jumbo.trus.statistics;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewModel extends ViewModel {

    private static final String TAG = "StatisticsViewModel";

    /**
     * nutno zavolat pokud chceme vědět počet piv a kořalek ze všech zvolených zápasů pro zobrazení v recycleview
     * @param playerList hráči, které chceme obohatit počtem piv a tvrdýho ze zápasů
     * @param matchList zápasy, kterými obohacujeme počet piv a tvrdýho
     */
    public List<Player> enhancePlayersWithBeersAndLiquorsFromMatches(List<Player> playerList, List<Match> matchList) {
        List<Player> returnPlayers = new ArrayList<>();
        for (Player player : playerList) {
            returnPlayers.add(player);
        }
        for (Player player : returnPlayers) {
          player.calculateAllBeersNumber(matchList);
          player.calculateAllLiquorsNumber(matchList);
        }
        return returnPlayers;
    }
    /**
     * nutno zavolat pokud chceme vědět počet pokut/výši ze všech zvolených zápasů pro zobrazení v recycleview
     * @param playerList hráči, které chceme obohatit počtem/výší pokut ze zápasů
     * @param matchList zápasy, kterými obohacujeme počet pokut
     */
    public List<Player> enhancePlayersWithFinesFromMatches(List<Player> playerList, List<Match> matchList) {
        List<Player> returnPlayers = new ArrayList<>();
        for (Player player : playerList) {
            if (!player.isFan()) {
                returnPlayers.add(player);
            }
        }
        for (Player player : returnPlayers) {
            player.calculateAllFinesNumber(matchList);
        }
        return returnPlayers;
    }

    /**
     * @param playerList hráči u kterých chceme získat počet piv
     * @param matchList zápasy, kde hledáme hráče
     * @return celkový počet piv který hráči z listu vypili v listu zápasů
     */
    public int countNumberOfAllBeers(List<Player> playerList, List<Match> matchList) {
        int beerNumber = 0;
        for (Player player : playerList) {
            player.calculateAllBeersNumber(matchList);
            beerNumber += player.getNumberOfBeersInMatches();
        }
        return beerNumber;
    }

    /**
     * @param playerList hráči u kterých chceme získat počet panáků
     * @param matchList zápasy, kde hledáme hráče
     * @return celkový počet panáků který hráči z listu vypili v listu zápasů
     */
    public int countNumberOfAllLiquors(List<Player> playerList, List<Match> matchList) {
        int liquorNumber = 0;
        for (Player player : playerList) {
            player.calculateAllLiquorsNumber(matchList);
            liquorNumber += player.getNumberOfLiquorsInMatches();
        }
        return liquorNumber;
    }
    /**
     * @param playerList hráči u kterých chceme získat počet pokut
     * @param matchList zápasy, kde hledáme hráče
     * @return Arry o velikosti 2 polí.
     * 1. pole: celkový počet pokut který hráči z listu dostali v listu zápasů
     * 2. pole: celková částka co byla zaplacena
     */
    public int[] countNumberOfAllFines(List<Player> playerList, List<Match> matchList) {
        int[] fine = {0,0};
        for (Player player : playerList) {
            player.calculateAllFinesNumber(matchList);
            fine[0] += player.getNumberOfFinesInMatches();
            fine[1] += player.getAmountOfFinesInMatches();
        }
        return fine;
    }

    public int countNumberOfAllBeers(List<Player> playerList, List<Match> matchList, boolean fan) {
        int beerNumber = 0;
        for (Player player : playerList) {
            if (player.isFan() == fan) {
                player.calculateAllBeersNumber(matchList);
                beerNumber += player.getNumberOfBeersInMatches();
            }
        }
        return beerNumber;
    }

    public int countNumberOfAllLiquors(List<Player> playerList, List<Match> matchList, boolean fan) {
        int liquorNumber = 0;
        for (Player player : playerList) {
            if (player.isFan() == fan) {
                player.calculateAllLiquorsNumber(matchList);
                liquorNumber += player.getNumberOfLiquorsInMatches();
            }
        }
        return liquorNumber;
    }


    /**
     * @param playerList seznam hráčů u kterých chceme zjistit počet piv
     * @param matchList seznam všech dostupných zápasů
     * @param season sezona ve které se mají zápasy hledat
     * @return počet piv u vložených hráčů v sezoně
     */
    public int countNumberOfAllBeersBySeason(List<Player> playerList, List<Match> matchList, Season season) {
        int beerNumber = 0;
        List<Match> filteredMatches = new ArrayList<>();
        for (Match match : matchList) {
            if (match.getSeason().equals(season))
            filteredMatches.add(match);
        }
        for (Player player : playerList) {
            player.calculateAllBeersNumber(filteredMatches);
            beerNumber += player.getNumberOfBeersInMatches();
        }
        return beerNumber;
    }

    /**
     * @param playerList seznam hráčů u kterých chceme zjistit počet panáků
     * @param matchList seznam všech dostupných zápasů
     * @param season sezona ve které se mají zápasy hledat
     * @return počet panáků u vložených hráčů v sezoně
     */
    public int countNumberOfAllLiquorsBySeason(List<Player> playerList, List<Match> matchList, Season season) {
        int liquorNumber = 0;
        List<Match> filteredMatches = new ArrayList<>();
        for (Match match : matchList) {
            if (match.getSeason().equals(season))
                filteredMatches.add(match);
        }
        for (Player player : playerList) {
            player.calculateAllLiquorsNumber(filteredMatches);
            liquorNumber += player.getNumberOfLiquorsInMatches();
        }
        return liquorNumber;
    }

    /**
     * @param playerList seznam hráčů u kterých chceme zjistit počet pokut a výši
     * @param matchList seznam všech dostupných zápasů
     * @param season sezona ve které se mají zápasy hledat
     * @return pole 0: počet pokut u hráčů v sezoně, pole 1: celková výše pokut
     */
    public int[] countNumberOfAllFinesBySeason(List<Player> playerList, List<Match> matchList, Season season) {
        int[] fine = {0,0};
        List<Match> filteredMatches = new ArrayList<>();
        for (Match match : matchList) {
            if (match.getSeason().equals(season))
                filteredMatches.add(match);
        }
        for (Player player : playerList) {
            player.calculateAllFinesNumber(filteredMatches);
            fine[0] += player.getNumberOfFinesInMatches();
            fine[1] += player.getAmountOfFinesInMatches();
        }
        return fine;
    }

    public List<Player> filterPlayers (List<Player> players, String searchText) {
        List<Player> filteredPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getName().toLowerCase().contains(searchText.toLowerCase()) || player.getBirthdayInStringFormat().contains(searchText)) {
                filteredPlayers.add(player);
            }
        }
        return filteredPlayers;
    }

    public List<Match> filterMatches (List<Match> matches, String searchText) {
        List<Match> filteredMatches = new ArrayList<>();
        for (Match match : matches) {
            if (match.getOpponent().toLowerCase().contains(searchText.toLowerCase()) || match.getDateOfMatchInStringFormat().contains(searchText)) {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    public List<Match> findAllMatchesWithPlayer (List<Match> matches, Player player) {
        List<Match> filteredMatches = new ArrayList<>();
        for (Match match : matches) {
            if (match.getPlayerList().contains(player)) {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    public List<Match> findAllMatchesWithPlayerParticipant (List<Match> matches, Player player) {
        List<Match> filteredMatches = new ArrayList<>();
        for (Match match : matches) {
            if (match.returnPlayerListOnlyWithParticipants().contains(player)) {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    public List<ReceivedFine> returnListOfAllFinesInMatch(Match match) {
        List<ReceivedFine> receivedFines = new ArrayList<>();
        List<Player> playerList = match.getPlayerList();
        for (Player player : playerList) {
            for (ReceivedFine receivedFine : player.getReceivedFines()) {
                if (receivedFine.getCount() <= 0) {

                }
                else if (receivedFines.contains(receivedFine)) {
                    receivedFines.get(receivedFines.indexOf(receivedFine)).addFineCount(receivedFine.getCount());
                }
                else {
                    Log.d(TAG, "returnListOfAllFinesInMatch: " + receivedFine + " add");
                    ReceivedFine newReceivedFine = new ReceivedFine();
                    newReceivedFine.setFine(receivedFine.getFine());
                    newReceivedFine.setCount(receivedFine.getCount());
                    receivedFines.add(newReceivedFine);
                }
            }
        }
        return receivedFines;
    }

    public List<String> returnOverallBeerStringList(List<Match> selectedMatches, List<Player> selectedPlayers) {
        List<String> returnList = new ArrayList<>();
        int overallBeers = countNumberOfAllBeers(selectedPlayers, selectedMatches);
        int overallLiquors = countNumberOfAllLiquors(selectedPlayers, selectedMatches);
        int fanBeers = countNumberOfAllBeers(selectedPlayers, selectedMatches, true);
        int fanLiquors = countNumberOfAllLiquors(selectedPlayers, selectedMatches, true);
        int playerBeers = countNumberOfAllBeers(selectedPlayers, selectedMatches, false);
        int playerLiquors = countNumberOfAllLiquors(selectedPlayers, selectedMatches, false);
        returnList.add("Celkový počet chlastu v zobrazených zápasech: " + (overallBeers+overallLiquors) +
                "\nz toho piv: " + overallBeers +
                "\nz toho panáků: " + overallLiquors);
        returnList.add("Počet vypitýho chlastu fanoušky: " + (fanBeers+fanLiquors) +
                "\nz toho piv: " + fanBeers +
                "\nz toho panáků: " + fanLiquors);
        returnList.add("Počet vypitýho chlastu hráči: " + (playerBeers+playerLiquors) +
                "\nz toho piv: " + playerBeers +
                "\nz toho panáků: " + playerLiquors);
        return returnList;
    }
}
