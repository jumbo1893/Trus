package com.jumbo.trus.match.add;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.listener.ModelLoadedListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchViewModelHelper;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.User;
import com.jumbo.trus.web.RetrieveMatchesTask;
import com.jumbo.trus.web.TaskRunner;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MatchPlusViewModel extends MatchViewModelHelper implements ItemLoadedListener, ChangeListener, INotificationSender, ModelLoadedListener {

    private static final String TAG = "MatchViewModel";

    private MutableLiveData<List<Season>> seasons;
    private MutableLiveData<List<Player>> players;
    private MutableLiveData<List<Player>> fans = new MutableLiveData<>();

    private MutableLiveData<PkflMatch> pkflMatch;
    private FirebaseRepository firebaseRepository;

    private Season checkedSeason = new Season().automaticSeason();

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this, false, true);

        if (pkflMatch == null) {
            pkflMatch = new MutableLiveData<>();
            firebaseRepository.setItemLoadedListener(this);
            firebaseRepository.loadPkflUrlFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
        if (seasons == null) {
            seasons = new MutableLiveData<>();
            firebaseRepository.loadSeasonsFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
        if (players == null) {
            players = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
    }

    public void addMatchToRepository(final String opponent, final boolean homeMatch, final String datum, User user) {
        isUpdating.setValue(true);
        Date date = new Date();
        try {
            long millis = date.convertTextDateToMillis(datum);
            Match match;
            if (checkedSeason.equals(new Season().automaticSeason())) {
                match = new Match(opponent, millis, homeMatch, seasons.getValue());
            } else {
                match = new Match(opponent, millis, homeMatch, checkedSeason);
            }

            match.createListOfPlayers(mergeArrayLists(checkedPlayers.getValue(), checkedFans.getValue()), mergeArrayLists(players.getValue(), fans.getValue()));
            firebaseRepository.insertNewModel(match);
        } catch (DateTimeException e) {
            Log.e(TAG, "addMatchToRepository: toto by nem??lo nastat, o??et??eno validac??", e);
            alert.setValue("Datum mus?? b??t ve form??tu " + Date.DATE_PATTERN);
            isUpdating.setValue(false);
            return;
        }
        String text = "Byl vytvo??en " +  (homeMatch ? "dom??c?? z??pas" : "venkovn?? z??pas") + " se soupe??em " + opponent + " hran?? " + datum;
        sendNotificationToRepository(new Notification("P??id??n z??pas se soupe??em " + opponent, text, user));
    }

    private void setMatchAsAdded(final Match match) {
        Log.d(TAG, "setMatchAsAdded: " + match.getName());
        alert.setValue("Z??pas " + match.getName() + " ??sp????n?? p??id??n");
        isUpdating.setValue(false);
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public LiveData<List<Player>> getFans() {
        return fans;
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<PkflMatch> getPkflMatch() {
        return pkflMatch;
    }

    private void loadMatchesFromPkfl(final String pkflUrl) {
        isUpdating.setValue(true);
        if (pkflUrl != null) {
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new RetrieveMatchesTask(pkflUrl), new TaskRunner.Callback<List<PkflMatch>>() {
                @Override
                public void onComplete(List<PkflMatch> result) {
                    isUpdating.setValue(false);
                    if (result == null || result.size() == 0) {
                        alert.setValue("Nelze na????st z??pasy. Je spr??vn?? url " + pkflUrl + " nebo nem?? web pkfl v??padek?");
                        Log.e(TAG, "loadMatchesFromPkfl : onComplete: nelze na????st z??pasy z pkfl z adresy " + pkflUrl);
                    } else {
                        setLastMatch(result);
                    }
                }
            });
        } else {
            firebaseRepository.loadPkflUrlFromRepository();
        }
    }

    private void setLastMatch(List<PkflMatch> pkflMatches) {
        long currentTime = System.currentTimeMillis();
        PkflMatch returnMatch = null;
        for (PkflMatch pkflMatch : pkflMatches) {
            if (pkflMatch.getDate() < currentTime) {
                if (returnMatch == null || returnMatch.getDate() < pkflMatch.getDate()) {
                    returnMatch = pkflMatch;
                }
            }
        }
        if (returnMatch != null) {
            pkflMatch.setValue(returnMatch);
        }
        else {
            Log.e(TAG, "setLastMatch: nelze naj??t p??edchoz?? pkfl z??pas!" );
        }
    }

    private void filterPlayersAndFans(List<Player> list) {
        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        List<Player> fansList = new ArrayList<>();
        List<Player> playerList = new ArrayList<>();
        for (Player player : list) {
            if (player.isFan()) {
                fansList.add(player);
            }
            else {
                playerList.add(player);
            }
        }
        players.setValue(playerList);
        fans.setValue(fansList);
    }

    @Override
    public void itemAdded(Model model) {
        setMatchAsAdded((Match) model);
        Log.d(TAG, "itemAdded: " + model.getId());
        newMainMatch.setValue((Match) model);
        closeFragment.setValue(true);
    }

    @Override
    public void itemChanged(Model model) {
    }

    @Override
    public void itemDeleted(Model model) {
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        isUpdating.setValue(true);
        List list = new ArrayList(models);
        if (flag.equals(Flag.PLAYER)) {
            filterPlayersAndFans(list);
        }
        else if (flag.equals(Flag.SEASON)) {
            list.add(0, new Season().automaticSeason());
            seasons.setValue(list);
        }
        isUpdating.setValue(false);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

    @Override
    public void itemLoaded(String url) {
        loadMatchesFromPkfl(url);
    }

    @Override
    public void itemLoaded(Model model) {

    }
}
