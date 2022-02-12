package com.jumbo.trus.statistics.match.beer.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.comparator.OrderByBeerThenName;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.statistics.player.ListTexts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeerMatchStatisticsDetailViewModel extends ViewModel {

    private static final String TAG = "BeerMatchStatisticsDetailViewModel";

    private MutableLiveData<List<ListTexts>> textPlayersList;
    private MutableLiveData<String> titleText = new MutableLiveData<>();


    public void init(Match match) {
        if (textPlayersList == null) {
            textPlayersList = new MutableLiveData<>();
        }
        setTitleText(match);
        setTextPlayerList(match);
    }

    private void setTitleText(Match match) {
        titleText.setValue(match.toStringNameWithOpponent());
    }

    private void setTextPlayerList(Match match) {
        List<ListTexts> returnText = new ArrayList<>();
        List<Player> playerList = new ArrayList<>(match.returnPlayerListOnlyWithParticipants());
        Collections.sort(playerList, new OrderByBeerThenName(true));
        for (Player player : playerList) {
            if ((player.getNumberOfBeers() + player.getNumberOfLiquors()) != 0) {
                String title = player.toString();
                String text = (player.isFan() ? "Fanoušek" : "Hráč") + " vypil v tomto zápase celkem " + setBeerText(player.getNumberOfBeers()) + " a " + setLiquorText(player.getNumberOfLiquors()) +
                ", celkem tedy " + setOverallBoozeText(player.getNumberOfBeers()+player.getNumberOfLiquors());
                returnText.add(new ListTexts(title, text));
            }
        }
        textPlayersList.setValue(returnText);
    }

    private String setBeerText(int beerNumber) {
        if (beerNumber == 1) {
            return beerNumber + " pivo";
        }
        else if (beerNumber < 5 && beerNumber != 0) {
            return beerNumber + " piva";
        }
        else {
            return beerNumber + " piv";
        }
    }

    private String setLiquorText(int liquorNumber) {
        if (liquorNumber == 1) {
            return liquorNumber + " panák";
        }
        else if (liquorNumber < 5 && liquorNumber != 0) {
            return liquorNumber + " panáky";
        }
        else if (liquorNumber == 0) {
            return "žádného panáka";
        }
        else {
            return liquorNumber + " panáků";
        }
    }

    private String setOverallBoozeText(int boozeNumber) {
        if (boozeNumber == 1) {
            return boozeNumber + " jednotku chlastu.";
        }
        else if (boozeNumber < 5 && boozeNumber != 0) {
            return boozeNumber + " jednotky chlastu.";
        }
        else {
            return boozeNumber + " jednotek chlastu.";
        }
    }

    public LiveData<List<ListTexts>> getTextPlayersList() {
        return textPlayersList;
    }

    public LiveData<String> getTitleText() {
        return titleText;
    }
}
