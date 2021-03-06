package com.jumbo.trus.fine.playerlist;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.comparator.OrderByNonPlayerThenName;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FinePlayersViewModel extends BaseViewModel implements ChangeListener {

    private static final String TAG = "FinePlayersViewModel";

    private MutableLiveData<List<Match>> matches;
    private Match pickedMatch;
    private List<Player> allPlayerList = new ArrayList<>();
    private MutableLiveData<Season> selectedSeason = new MutableLiveData<>();
    private MutableLiveData<List<Player>> players = new MutableLiveData<>();
    private MutableLiveData<List<Boolean>> checkedPlayers = new MutableLiveData<>();
    private MutableLiveData<String> titleText = new MutableLiveData<>();

    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this);
        if (matches == null) {
            matches = new MutableLiveData<>();
            firebaseRepository.loadMatchesFromRepository();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
    }

    public void removeReg() {
        firebaseRepository.removeListener();
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public LiveData<List<Boolean>> getCheckedPlayers() {
        return checkedPlayers;
    }

    public LiveData<String> getTitleText() {
        return titleText;
    }

    public void setSelectedSeason(Season season) {
        selectedSeason.postValue(season);
        firebaseRepository.loadMatchesFromRepository();
    }

    public void setPickedMatch(Match match) {
        pickedMatch = findMatchFromRepo(match);
        setTitleText(pickedMatch);
        updatePlayerList();
        setPlayerList(pickedMatch);
    }

    private void updatePlayerList() {
        if (allPlayerList.size() > 0) {
            pickedMatch.mergePlayerListsWithoutReplace(allPlayerList);
        }
    }

    private void setPlayerList(Match match) {
        List<Player> selectedPlayers = match.returnPlayerListWithoutFans();
        Collections.sort(selectedPlayers, new OrderByNonPlayerThenName());
        players.setValue(selectedPlayers);
    }

    private Match findMatchFromRepo(Match match) {
        if (matches.getValue() != null) {
            for (Match repoMatch : matches.getValue()) {
                if (repoMatch.equals(match)) {
                    return repoMatch;
                }
            }
        }
        return match;
    }

    public void checkNonPlayers() {
        if (checkedPlayers.getValue() == null || checkedPlayers.getValue().isEmpty()) {
            List<Boolean> checkedPlayers = new ArrayList();
            if (players.getValue() != null) {
                for (int i = 0; i < players.getValue().size(); i++) {
                    checkedPlayers.add(!players.getValue().get(i).isMatchParticipant());
                }
            }
            this.checkedPlayers.setValue(checkedPlayers);
        } else {
            invertCheckedPlayers();
        }
    }

    private void invertCheckedPlayers() {
        List<Boolean> checkedPlayers = new ArrayList();
        for (Boolean bool : Objects.requireNonNull(this.checkedPlayers.getValue())) {
            checkedPlayers.add(!bool);

        }
        this.checkedPlayers.setValue(checkedPlayers);
    }

    public void uncheckPlayers() {
        if (checkedPlayers.getValue() != null) {
            checkedPlayers.getValue().clear();
        }
        /*List<Boolean> checkedPlayers = new ArrayList();
        if (players.getValue() != null) {
            for (int i = 0; i < players.getValue().size(); i++) {
                checkedPlayers.add(false);
            }
        }
        this.checkedPlayers.setValue(checkedPlayers);*/
    }

    private void setTitleText(Match match) {
        if (match != null) {
            titleText.setValue(match.toStringNameWithOpponent());
            return;
        }
        titleText.setValue("...na????t??m");
    }

    private void useSeasonsFilter(List<Match> models) {
        Log.d(TAG, "useSeasonsFilter: na????t??m z??pasy s filtren" + selectedSeason.getValue());
        if (selectedSeason.getValue() == null) {
            matches.setValue(models);
            return;
        }
        List<Match> selectedMatches = new ArrayList<>();
        for (Match match : models) {
            if (match.getSeason().equals(selectedSeason.getValue())) {
                selectedMatches.add(match);
            }
        }
        matches.setValue(selectedMatches);
    }

    public Match getPickedMatch() {
        return pickedMatch;
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
    public void itemListLoaded(List models, Flag flag) {
        List list = new ArrayList(models);
        Log.d(TAG, "itemListLoaded: zapasy: " + models);
        if (flag.equals(Flag.MATCH)) {
            Collections.sort(list, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
            useSeasonsFilter(list);
            setPickedMatch(pickedMatch);
        } else if (flag.equals(Flag.PLAYER)) {
            allPlayerList = list;
            updatePlayerList();
            setPlayerList(pickedMatch);
        }
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
