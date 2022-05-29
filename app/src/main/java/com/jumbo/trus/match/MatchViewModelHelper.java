package com.jumbo.trus.match;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.List;

public class MatchViewModelHelper extends BaseViewModel implements ICheckedPlayers {


    protected MutableLiveData<Season> checkedSeason = new MutableLiveData<>();
    protected MutableLiveData<List<Player>> checkedPlayers = new MutableLiveData<>();
    protected MutableLiveData<List<Player>> checkedFans = new MutableLiveData<>();
    protected MutableLiveData<Match> newMainMatch = new MutableLiveData<>();

    public void setCheckedSeason(Season season) {
        Log.d("TAG", "setCheckedSeason: " + season);
        checkedSeason.setValue(season);
        Log.d("TAG", "setCheckedSeason: " + checkedSeason.getValue());
    }

    public LiveData<List<Player>> getCheckedPlayers() {
        return checkedPlayers;
    }

    public LiveData<List<Player>> getCheckedFans() {
        return checkedFans;
    }

    public LiveData<Season> getCheckedSeason() {
        return checkedSeason;
    }


    public LiveData<Match> getNewMainMatch() {
        return newMainMatch;
    }

    protected List<Player> mergeArrayLists(List<Player> a, List<Player> b) {
        List<Player> returnList = new ArrayList(a);
        returnList.addAll(b);
        return returnList;
    }

    @Override
    public void setCheckedPlayers(List<Player> player) {
        this.checkedPlayers.setValue(player);
    }

    @Override
    public void setCheckedFans(List<Player> fans) {
        this.checkedFans.setValue(fans);
    }
}
