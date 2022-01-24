package com.jumbo.trus.match;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.List;

public class MatchViewModelHelper extends ViewModel implements ICheckedPlayers {

    protected MutableLiveData<Boolean> closeFragment = new MutableLiveData<>();
    protected MutableLiveData<Season> checkedSeason = new MutableLiveData<>();
    protected MutableLiveData<List<Player>> checkedPlayers = new MutableLiveData<>();
    protected MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    protected MutableLiveData<String> alert = new MutableLiveData<>();

    public void setCheckedSeason(Season season) {
        Log.d("TAG", "setCheckedSeason: " + season);
        checkedSeason.setValue(season);
        Log.d("TAG", "setCheckedSeason: " + checkedSeason.getValue());
    }

    public LiveData<Boolean> closeFragment() {
        return closeFragment;
    }

    public LiveData<List<Player>> getCheckedPlayers() {
        return checkedPlayers;
    }

    public LiveData<Season> getCheckedSeason() {
        return checkedSeason;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    @Override
    public void setCheckedPlayers(List<Player> player) {
        this.checkedPlayers.setValue(player);
    }
}
