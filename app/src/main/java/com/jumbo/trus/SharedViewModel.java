package com.jumbo.trus;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.User;

import java.util.List;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Match> pickedMatchForEdit = new MutableLiveData<>();
    private MutableLiveData<Match> mainMatch = new MutableLiveData<>();
    private MutableLiveData<Player> pickedPlayerForEdit = new MutableLiveData<>();
    private MutableLiveData<List<Player>> pickedPlayersForEdit = new MutableLiveData<>();
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<Fine> pickedFineForEdit = new MutableLiveData<>();
    private MutableLiveData<Season> pickedSeasonForEdit = new MutableLiveData<>();
    private boolean multiplayers = false;


    public LiveData<Match> getPickedMatchForEdit() {
        if (pickedMatchForEdit.getValue() != null) {
            return pickedMatchForEdit;
        }
        return mainMatch;
    }

    public MutableLiveData<Player> getPickedPlayerForEdit() {
        if (pickedPlayerForEdit.getValue() == null) {
            pickedPlayerForEdit.setValue(mainMatch.getValue().getPlayerList().get(0));
        }
        return pickedPlayerForEdit;
    }

    public LiveData<Fine> getPickedFineForEdit() {
        if (pickedFineForEdit.getValue() == null) {
            pickedFineForEdit.setValue(new Fine("dummy", 1, true));
        }
        return pickedFineForEdit;
    }

    public MutableLiveData<Season> getPickedSeasonForEdit() {
        if (pickedSeasonForEdit.getValue() == null) {
            pickedSeasonForEdit.setValue(new Season().automaticSeason());
        }
        return pickedSeasonForEdit;
    }

    public MutableLiveData<List<Player>> getPickedPlayersForEdit() {
        return pickedPlayersForEdit;
    }

    public boolean isMultiplayers() {
        return multiplayers;
    }

    public LiveData<Match> getMainMatch() {
        Log.d("shared", "getMainMatch: " + mainMatch.getValue());
        return mainMatch;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setPickedPlayerForEdit(Player player) {
        pickedPlayerForEdit.setValue(player);
    }

    public void setPickedPlayersForEdit(List<Player> players) {
        pickedPlayersForEdit.setValue(players);
    }

    public void setMultiplayers(boolean multiplayers) {
        this.multiplayers = multiplayers;
    }

    public void setPickedMatchForEdit(Match match) {
        pickedMatchForEdit.setValue(match);
    }

    public void setMainMatch(Match match) {
        Log.d("shared", "setMainMatch: " + match);
        mainMatch.setValue(match);
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public void setPickedFineForEdit(Fine fine) {
        pickedFineForEdit.setValue(fine);
    }

    public void setPickedSeasonForEdit(Season season) {
        pickedSeasonForEdit.setValue(season);
    }
}
