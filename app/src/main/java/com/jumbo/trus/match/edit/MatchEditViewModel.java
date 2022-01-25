package com.jumbo.trus.match.edit;

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
import com.jumbo.trus.match.MatchViewModelHelper;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.User;
import com.jumbo.trus.web.RetreiveMatchesTask;
import com.jumbo.trus.web.TaskRunner;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MatchEditViewModel extends MatchViewModelHelper implements ChangeListener, INotificationSender{

    private static final String TAG = "MatchViewModel";

    private MutableLiveData<List<Season>> seasons;
    private MutableLiveData<List<Player>> players;
    private Match pickedMatch;
    private MutableLiveData<Match> match = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;
    private boolean init;

    public void init() {
        init = true;
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this, false, true);
        firebaseRepository.loadMatchesFromRepository();
        Log.d(TAG, "init: nacitam zapasy");
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

    public void editMatchInRepository(final String opponent, final boolean homeMatch, final String datum, User user) {
        Log.d(TAG, "editMatchInRepository: " + pickedMatch.getId());
        isUpdating.setValue(true);
        Date date = new Date();
        Match match = pickedMatch;
        match.setOpponent(opponent);
        match.setHomeMatch(homeMatch);
        match.createListOfPlayers(checkedPlayers.getValue(), Objects.requireNonNull(players.getValue()));
        try {
            long millis = date.convertTextDateToMillis(datum);
            match.setDateOfMatch(millis);
            if (checkedSeason.getValue().equals(new Season().automaticSeason())) {
                match.calculateSeason(seasons.getValue());
            } else {
                match.setSeason(checkedSeason.getValue());
            }
            firebaseRepository.editModel(match);
        } catch (DateTimeException e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Datum musí být ve formátu " + Date.DATE_PATTERN);
        }
        String text = "Zápas byl změněn na " + (homeMatch ? "domácí zápas" : "venkovní zápas") + " se soupeřem " + opponent + " hraný " + datum;
        sendNotificationToRepository(new Notification("Upraven zápas " + opponent, text, user));
    }

    public void removeMatchFromRepository(User user) {
        isUpdating.setValue(true);

        try {
            firebaseRepository.removeModel(pickedMatch);
        } catch (Exception e) {
            Log.e(TAG, "removeMatchFromRepository: chyba při mazání zápasu", e);
            alert.setValue("Chyba při posílání požadavku do DB");
        }
        String text = "hraný " + pickedMatch.returnDateOfMatchInStringFormat();
        sendNotificationToRepository(new Notification("Smazán zápas " + pickedMatch.getOpponent(), text, user));
    }

    public void setPickedMatch(Match match) {
        pickedMatch = match;
        setMatch(match);
        setPlayerList(pickedMatch);
        setCheckedSeason(pickedMatch.getSeason());
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    private void updatePickedMatch(List<Match> matches) {
        pickedMatch = findMatchFromRepo(pickedMatch, matches);
        setMatch(pickedMatch);
        setPlayerList(pickedMatch);
        setCheckedSeason(pickedMatch.getSeason());
        if (!init) {
            alert.setValue("Právě někdo jiný upravil zápas. Reloaduji nové údaje...");
        }
        else {
            init = false;
        }
    }

    private void setPlayerList(Match match) {
        List<Player> selectedPlayers = match.returnPlayerListOnlyWithParticipants();
        checkedPlayers.setValue(selectedPlayers);
    }

    private Match findMatchFromRepo(Match match, List<Match> matches) {
        if (matches != null) {
            for (Match repoMatch : matches) {
                if (repoMatch.equals(match)) {
                    return repoMatch;
                }
            }
        }
        return match;
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<Match> getMatch() {
        return match;
    }

    private void setMatch(Match match) {
        this.match.setValue(match);
    }

    @Override
    public void itemAdded(Model model) {
    }

    @Override
    public void itemChanged(Model model) {
        alert.setValue("Zápas " + ((Match) model).getOpponent() + " úspěšně upraven");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    @Override
    public void itemDeleted(Model model) {
        alert.setValue("Zápas " + ((Match) model).getOpponent() + " úspěšně smazán");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
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
            pickedMatch.mergePlayerListsWithoutReplace(list);
            players.setValue(pickedMatch.getPlayerList());
        } else if (flag.equals(Flag.SEASON)) {
            list.add(0, new Season().automaticSeason());
            seasons.setValue(list);

        } else if (flag.equals(Flag.MATCH)) {
            Collections.sort(list, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
            updatePickedMatch(list);
        }
        isUpdating.setValue(false);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
