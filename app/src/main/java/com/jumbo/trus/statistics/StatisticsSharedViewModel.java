package com.jumbo.trus.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.List;

public class StatisticsSharedViewModel extends ViewModel {
    private MutableLiveData<Match> pickedMatch = new MutableLiveData<>();
    private MutableLiveData<Player> pickedPlayer = new MutableLiveData<>();
    private MutableLiveData<List<Player>> pickedPlayers = new MutableLiveData<>();
    private MutableLiveData<List<Match>> pickedMatches = new MutableLiveData<>();
    private MutableLiveData<Season> pickedSeason = new MutableLiveData<>();
    private Match dummyMatch;
    private boolean multiplayers = false;


    public LiveData<Match> getPickedMatch() {
        if (pickedMatch.getValue() == null) {
            pickedMatch.setValue(dummyMatch);
        }
        return pickedMatch;
    }

    public MutableLiveData<Player> getPickedPlayer() {
        if (pickedPlayer.getValue() == null) {
            pickedPlayer.setValue(dummyMatch.getPlayerList().get(0));
        }
        return pickedPlayer;
    }

    public MutableLiveData<Season> getPickedSeason() {
        if (pickedSeason.getValue() == null) {
            pickedSeason.setValue(new Season().automaticSeason());
        }
        return pickedSeason;
    }

    public MutableLiveData<List<Player>> getPickedPlayers() {
        if (pickedPlayers.getValue() == null) {
            pickedPlayers.setValue(dummyMatch.getPlayerList());
        }
        return pickedPlayers;
    }

    public MutableLiveData<List<Match>> getPickedMatches() {
        if (pickedMatches.getValue() == null) {
            pickedMatches.setValue(new ArrayList<Match>());
        }
        return pickedMatches;
    }

    public void setPickedPlayer(Player player) {
        pickedPlayer.setValue(player);
    }

    public void setPickedPlayers(List<Player> players) {
        pickedPlayers.setValue(players);
    }

    public void setPickedMatches(List<Match> matches) {
        pickedMatches.setValue(matches);
    }

    public void setPickedMatch(Match match) {
        pickedMatch.setValue(match);
    }

    public void setPickedSeason(Season season) {
        pickedSeason.setValue(season);
    }

    public void setDummyAttributes(Match match) {
    }

}
