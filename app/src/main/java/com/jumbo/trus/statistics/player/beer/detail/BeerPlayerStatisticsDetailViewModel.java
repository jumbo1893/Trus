package com.jumbo.trus.statistics.player.beer.detail;

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

public class BeerPlayerStatisticsDetailViewModel extends ViewModel implements ChangeListener {

    private static final String TAG = "BeerPlayerStatisticsDetailViewModel";

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

    public void removeReg() {
        firebaseRepository.removeListener();
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
            if ((match.getSeason().equals(season) || season.equals(new Season().allSeason())) && match.returnPlayerListOnlyWithParticipants().contains(player)) {
                Log.d(TAG, "setTextMatchesList: " + match.getName());
                String title = match.toStringNameWithOpponent();
                String text = "Počet piv: " + match.returnNumberOfBeersForPlayer(player) + ", panáků: " + match.returnNumberOfLiquorsForPlayer(player) +
                        ", dohromady: " + match.returnNumberOfBeersAndLiquorsForPlayer(player);
                returnText.add(new ListTexts(title, text));
            }
        }
        textMatchesList.setValue(returnText);
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
