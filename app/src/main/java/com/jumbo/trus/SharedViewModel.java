package com.jumbo.trus;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Match> pickedMatchForEdit = new MutableLiveData<>();
    private MutableLiveData<Match> mainMatch = new MutableLiveData<>();
    private MutableLiveData<Player> pickedPlayerForEdit = new MutableLiveData<>();
    private MutableLiveData<List<Player>> pickedPlayersForEdit = new MutableLiveData<>();
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

    public MutableLiveData<List<Player>> getPickedPlayersForEdit() {
        return pickedPlayersForEdit;
    }

    public boolean isMultiplayers() {
        return multiplayers;
    }

    public LiveData<Match> getMainMatch() {
        return mainMatch;
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
        mainMatch.setValue(match);
    }

    public void updateMainMatch(List<Match> matches) {
        for (Match match : matches) {
            if (match.equals(getMainMatch().getValue())) {
                setMainMatch(match);
            }
        }
    }

    public void findLastMatch(List<Match> matches) {
        if (matches != null && matches.size() > 0) {
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
            setMainMatch(matches.get(0));
        }
    }
}
