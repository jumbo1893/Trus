package com.jumbo.trus.statistics.match.fine.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.comparator.OrderByBeerThenName;
import com.jumbo.trus.comparator.OrderPlayersByFinesAmountThenName;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.statistics.player.ListTexts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FineMatchStatisticsDetailViewModel extends ViewModel {

    private static final String TAG = "FineMatchStatisticsDetailViewModel";

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
        List<Player> playerList = new ArrayList<>(match.returnPlayerListWithoutFans());
        Collections.sort(playerList, new OrderPlayersByFinesAmountThenName());
        Log.d(TAG, "setTextPlayerList: " + playerList);
        for (Player player : playerList) {
            if ((player.returnAmountOfAllReceviedFines()) != 0) {
                String title = player.toString();
                StringBuilder text = new StringBuilder();
                for (String fineString : player.returnListOfFineInStringList()) {
                    text.append(fineString + "\n");
                }
                text.append(returnOverallFineText(player));
                returnText.add(new ListTexts(title, text.toString()));
            }
        }
        textPlayersList.setValue(returnText);
    }

    private String returnOverallFineText(Player player) {
        int fineNumber = player.returnNumberOfAllReceviedFines();
        StringBuilder text = new StringBuilder(fineNumber + " ");

        if (fineNumber == 1) {
            text.append("pokuta ");
        }
        else if (fineNumber < 5) {
            text.append("pokuty ");
        }
        else {
            text.append("pokut ");
        }
        text.append("celkem, v součtu " + player.returnAmountOfAllReceviedFines() + " Kč");
        return text.toString();
    }

    public LiveData<List<ListTexts>> getTextPlayersList() {
        return textPlayersList;
    }

    public LiveData<String> getTitleText() {
        return titleText;
    }
}
