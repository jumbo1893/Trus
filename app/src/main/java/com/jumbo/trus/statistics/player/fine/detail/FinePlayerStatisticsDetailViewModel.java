package com.jumbo.trus.statistics.player.fine.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.player.ListTexts;

import java.util.ArrayList;
import java.util.List;

public class FinePlayerStatisticsDetailViewModel extends ViewModel implements ChangeListener {

    private static final String TAG = "FinePlayerStatisticsDetailViewModel";

    private MutableLiveData<List<ListTexts>> textMatchesList;
    private MutableLiveData<String> titleText = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;
    private Player player;
    private Season season;
    private List<Match> matches = new ArrayList<>();


    public void init(Player player, Season season) {
        Log.d(TAG, "init: " + player + season);
        this.player = player;
        this.season = season;
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this );
        if (textMatchesList == null) {
            textMatchesList = new MutableLiveData<>();
            firebaseRepository.loadMatchesFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
        setTitleText(player, season);
        if (matches != null || matches.size() == 0) {
            Log.d(TAG, "init: " + matches);
            setTextMatchesList(matches);
        }
    }

    private void setTitleText(Player player, Season season) {
        if (season.equals(new Season().allSeason())) {
            titleText.setValue(player.getName() + " | " + "všechny sezony");
        }
        else {
            titleText.setValue(player.getName() + " | sezona " + season.getName() );
        }
    }

    private void setTextMatchesList(List<Match> matches) {
        List<ListTexts> returnText = new ArrayList<>();
        for (Match match : matches) {
            if ((match.getSeason().equals(season) || season.equals(new Season().allSeason())) && match.returnPlayerListWithoutFans().contains(player)) {
                Log.d(TAG, "setTextMatchesList: " + match.getName());
                String title = match.toStringNameWithOpponent();
                StringBuilder text = new StringBuilder();
                Player matchPlayer = match.returnPlayerFromMatch(player);
                if (matchPlayer.returnNumberOfAllReceviedFines() != 0) {
                    for (String fineString : matchPlayer.returnListOfFineInStringList()) {
                        text.append(fineString + "\n");
                    }
                    text.append(returnOverallFineText(matchPlayer));
                    returnText.add(new ListTexts(title, text.toString()));
                }
            }
        }
        textMatchesList.setValue(returnText);
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

    public LiveData<List<ListTexts>> getTextMatchesList() {
        return textMatchesList;
    }

    public LiveData<String> getTitleText() {
        return titleText;
    }


    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {

    }

    @Override
    public void itemDeleted(Model model) {

    }

    @Override
    public void alertSent(String message) {

    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        matches = models;
        setTextMatchesList(matches);
    }

}
