package com.jumbo.trus.beer;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.comparator.OrderByBeerThenName;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Compensation;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BeerViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "BeerViewModel";

    private MutableLiveData<List<Match>> matches;
    private MutableLiveData<List<Player>> players = new MutableLiveData<>();
    private Match pickedMatch;
    private List<Player> allPlayerList = new ArrayList<>();
    private MutableLiveData<Season> selectedSeason = new MutableLiveData<>();
    private MutableLiveData<String> titleText = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;
    private Compensation matchCompensation;
    private boolean changeAlertEnabled = false;

    public void init() {
        changeAlertEnabled = false;
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this);
        if (matches == null) {
            matches = new MutableLiveData<>();
        }
        firebaseRepository.loadMatchesFromRepository();
        firebaseRepository.loadPlayersFromRepository();
    }

    public Match editMatchBeers(final List<Player> playerList, User user) {
        isUpdating.setValue(true);
        changeAlertEnabled = false;
        pickedMatch.mergePlayerLists(playerList);
        try {
            firebaseRepository.editModel(pickedMatch);
        } catch (Exception e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Něco se posralo při přidávání do db, zkus to znova");
            isUpdating.setValue(false);
            return pickedMatch;
        }

        Notification notification = new Notification(pickedMatch, playerList, matchCompensation.getBeerCompensation(), matchCompensation.getLiquorCompensation());
        sendNotification(notification, user);
        Log.d(TAG, "editMatchBeers: " + pickedMatch.getOpponent());
        alert.setValue("Měním počty piv u zápasu se soupeřem " + pickedMatch.getOpponent());
        return pickedMatch;
    }

    private void sendNotification(Notification notification, User user) {
        notification.setUser(user);
        sendNotificationToRepository(notification);
    }

    public void setPickedMatch(Match match) {
        pickedMatch = findMatchFromRepo(match);
        setTitleText(pickedMatch);
        updatePlayerList();
        setPlayerList(pickedMatch);
    }

    private void setPlayerList(Match match) {
        List<Player> selectedPlayers = match.returnPlayerListOnlyWithParticipants();
        Collections.sort(selectedPlayers, new OrderByBeerThenName(true));
        Log.d(TAG, "setPlayerList: " + selectedPlayers);
        players.setValue(selectedPlayers);
    }

    private void updatePlayerList() {
        Log.d(TAG, "updatePlayerList: " + allPlayerList.size());
        Log.d(TAG, "updatePlayerList: " + pickedMatch.getPlayerList().size());
        Log.d(TAG, "updatePlayerList: " + pickedMatch.returnPlayerListOnlyWithParticipants().get(0).getNumberOfBeers());
        if (allPlayerList.size() > 0) {
            pickedMatch.mergePlayerListsWithoutReplace(allPlayerList);
        }
        initCompensationVariables();
    }

    private void setTitleText(Match match) {
        if (match != null) {
            titleText.setValue(match.toStringNameWithOpponent());
            return;
        }
        titleText.setValue("...načítám");
    }

    private Match findMatchFromRepo(Match match) {
        Log.d(TAG, "findMatchFromRepo: " + match);
        if (matches.getValue() != null) {
            for (Match repoMatch : matches.getValue()) {
                if (repoMatch.equals(match)) {
                    Log.d(TAG, "findMatchFromRepo: " + repoMatch.returnPlayerListOnlyWithParticipants().get(0).getNumberOfBeers());
                    return repoMatch;
                }
            }
        }
        return match;
    }

    private void updateMatchFromLoadedMatches(List<Match> matches) {
        Log.d(TAG, "updateMatchFromLoadedMatches: init");
        for (Match match : matches) {
            if (match.equals(pickedMatch)) {
                if (changeAlertEnabled) {
                    Log.d(TAG, "updateMatchFromLoadedMatches: nastala změna");
                    alert.setValue("Proběhla změna zápasu, reloaduji nové údaje");
                }
                else {
                    changeAlertEnabled = true;
                }
                pickedMatch = match;
                initCompensationVariables();
                setPlayerList(pickedMatch);
                setTitleText(match);
                break;
            }
        }
    }

    private void initCompensationVariables() {
        matchCompensation = new Compensation(pickedMatch);
        matchCompensation.initBeerAndLiquorCompensation();
        matchCompensation.initFineCompensation();
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public LiveData<String> getTitleText() {
        return titleText;
    }

    public void setSelectedSeason(Season season) {
        selectedSeason.postValue(season);
        firebaseRepository.loadMatchesFromRepository();
    }

    private void useSeasonsFilter(List<Match> models) {
        Log.d(TAG, "useSeasonsFilter: načítám zápasy s filtren" + selectedSeason.getValue());
        if (selectedSeason.getValue() == null) {
            Log.d(TAG, "useSeasonsFilter: nastavuji v3echnz zapasy");
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

    public void removeReg() {
        firebaseRepository.removeListener();
    }

    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    @Override
    public void itemDeleted(Model model) {

    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        List list = new ArrayList(models);
        if (flag.equals(Flag.MATCH)) {
            Match match = (Match) list.get(0);
            Log.d(TAG, "itemListLoaded: " + match.returnPlayerListOnlyWithParticipants().get(0).getNumberOfBeers());
            Collections.sort(list, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
            useSeasonsFilter(list);

            updateMatchFromLoadedMatches(list);

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
