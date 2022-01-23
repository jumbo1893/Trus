package com.jumbo.trus.match;

import android.util.Log;

import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Compensation {

    private static final String TAG = "Compensation";

    private Match match;
    private List<Integer> beerCompensation;
    private List<Integer> liquorCompensation;
    private List<List<Integer>> finesCompesation;

    public Compensation(Match match) {
        this.match = match;
    }

    public List<Integer> getBeerCompensation() {
        return beerCompensation;
    }

    public List<Integer> getLiquorCompensation() {
        return liquorCompensation;
    }

    public List<List<Integer>> getFinesCompesation() {
        return finesCompesation;
    }

    /**
     * načte seznam piv v v původní nezměněné podobě
     */
    public void initBeerAndLiquorCompensation() {
        Log.d(TAG, "initBeerAndLiquorCompensation: ");
        beerCompensation = new ArrayList<>();
        liquorCompensation = new ArrayList<>();
        for (Player player : match.returnPlayerListOnlyWithParticipants()) {
            beerCompensation.add(player.getNumberOfBeers());
            liquorCompensation.add(player.getNumberOfLiquors());
        }
    }

    /**
     * vezme z listu počet piv načtených před přidáváním nových piv a nastaví je zpět
     */
    public void setOriginalBeerAndLiquorNumber() {
        Log.d(TAG, "returnOriginalBeerAndLiquorNumber: ");
        for (int i = 0; i < match.returnPlayerListOnlyWithParticipants().size(); i++) {
            match.returnPlayerListOnlyWithParticipants().get(i).setNumberOfBeers(beerCompensation.get(i));
            match.returnPlayerListOnlyWithParticipants().get(i).setNumberOfLiquors(liquorCompensation.get(i));
        }
    }

    public void initFineCompensation() {
        Log.d(TAG, "initFineCompensation: ukládám do proměnné současný seznam pokut pro zápas " + match + ", velikost hráčů je " + match.returnPlayerListWithoutFans());
        finesCompesation = new ArrayList<>();
        for (Player player : match.returnPlayerListWithoutFans()) {
            Log.d(TAG, "initFineCompensation: " + player.getName() + player.returnNumberOfFines());
            finesCompesation.add(player.returnNumberOfFines());
        }
        Log.d(TAG, "initFineCompensation: " + finesCompesation);

    }
    public void setOriginalFineNumberForAllPlayers() {
        Log.d(TAG, "returnOriginalFineNumberForAllPlayers: ");
        for (int i = 0; i < match.returnPlayerListWithoutFans().size(); i++) {
            match.returnPlayerListWithoutFans().get(i).setNewFineCountsToAllReceivedFines(finesCompesation.get(i));
        }
    }

    public void setOriginalFineNumberForPlayer(Player player) {
        Log.d(TAG, "returnOriginalFineNumberForPlayer: " + match.returnPlayerListWithoutFans().size() + finesCompesation.size());
        for (int i = 0; i < match.returnPlayerListWithoutFans().size(); i++) {
            if (match.returnPlayerListWithoutFans().get(i).equals(player)) {
                match.returnPlayerListWithoutFans().get(i).setNewFineCountsToAllReceivedFines(finesCompesation.get(i));
            }
        }
    }
}
