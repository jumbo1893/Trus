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
    private List<List<ReceivedFine>> finesCompesation;

    public Compensation(Match match) {
        this.match = match;
    }

    public List<Integer> getBeerCompensation() {
        return beerCompensation;
    }

    public List<Integer> getLiquorCompensation() {
        return liquorCompensation;
    }

    public List<List<ReceivedFine>> getFinesCompesation() {
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
        finesCompesation = new ArrayList<>();
        for (Player player : match.returnPlayerListWithoutFans()) {
            List<ReceivedFine> fines = new ArrayList<>(player.getReceivedFines());
            finesCompesation.add(fines);
        }
        Log.d(TAG, "initFineCompensation: " + finesCompesation);

    }
}
