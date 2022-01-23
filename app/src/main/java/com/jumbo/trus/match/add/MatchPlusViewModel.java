package com.jumbo.trus.match.add;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.web.RetreiveMatchesTask;
import com.jumbo.trus.web.TaskRunner;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MatchPlusViewModel extends ViewModel implements ChangeListener, INotificationSender, ItemLoadedListener {

    private static final String TAG = "MatchViewModel";

    private MutableLiveData<PkflMatch> pkflMatch;
    private MutableLiveData<List<Season>> seasons;
    private MutableLiveData<List<Player>> players;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
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

    public void addMatchToRepository(final String opponent, final boolean homeMatch, final String datum,
                                       final List<Player> playerList) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        try {
            long millis = date.convertTextDateToMillis(datum);
            Match match;
            if (checkedSeason.equals(new Season().automaticSeason())) {
                match = new Match(opponent, millis, homeMatch, seasons.getValue());
                match.createListOfPlayers(playerList, Objects.requireNonNull(players.getValue()));
            } else {
                match = new Match(opponent, millis, homeMatch, checkedSeason);
                match.createListOfPlayers(playerList, Objects.requireNonNull(players.getValue()));
            }
            firebaseRepository.insertNewModel(match);
        } catch (DateTimeException e) {
            Log.e(TAG, "addMatchToRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Datum musí být ve formátu " + Date.DATE_PATTERN);
            isUpdating.setValue(false);
        }
    }

    private void setMatchAsAdded(final Match match) {
        Log.d(TAG, "setMatchAsAdded: " + match.getName());
        alert.setValue("Zápas " + match.getName() + " úspěšně přidán");
        isUpdating.setValue(false);
    }

    public void setCheckedSeason(Season season) {
        this.checkedSeason = season;
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }


    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    public LiveData<PkflMatch> getPkflMatch() {
        return pkflMatch;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }


    private void loadMatchesFromPkfl(final String pkflUrl) {
        isUpdating.setValue(true);
        if (pkflUrl != null) {
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new RetreiveMatchesTask(pkflUrl), new TaskRunner.Callback<List<PkflMatch>>() {
                @Override
                public void onComplete(List<PkflMatch> result) {
                    isUpdating.setValue(false);
                    if (result == null || result.size() == 0) {
                        alert.setValue("Nelze načíst zápasy. Je správně url " + pkflUrl + " nebo nemá web pkfl výpadek?");
                        Log.e(TAG, "loadMatchesFromPkfl : onComplete: nelze načíst zápasy z pkfl z adresy " + pkflUrl);
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
        pkflMatch.setValue(returnMatch);
    }

    @Override
    public void itemAdded(Model model) {
        setMatchAsAdded((Match) model);
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
            Collections.sort(list, new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            players.setValue(list);
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
}
