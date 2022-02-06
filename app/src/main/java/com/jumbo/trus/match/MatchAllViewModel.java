package com.jumbo.trus.match;

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
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchAllViewModel extends ViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "MatchViewModel";

    private MutableLiveData<List<Match>> matches;
    private MutableLiveData<Season> selectedSeason = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this);
        if (matches == null) {
            matches = new MutableLiveData<>();
            firebaseRepository.loadMatchesFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
    }

    public Result addMatchToRepository(final String opponent, final boolean homeMatch, final String datum, final Season season,
                                       final List<Player> playerList, final List<Season> seasonList, List<Player> allPlayerList) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        try {
            long millis = date.convertTextDateToMillis(datum);
            Match match;
            if (season == null) {
                match = new Match(opponent, millis, homeMatch, createListOfPlayers(playerList, allPlayerList), seasonList);
            }
            else {
                match = new Match(opponent, millis, homeMatch, season, createListOfPlayers(playerList, allPlayerList));
            }
            firebaseRepository.insertNewModel(match);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "addMatchToRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Datum musí být ve formátu " + Date.DATE_PATTERN);
            isUpdating.setValue(false);
            return result;
        }
        result.setText("Přidávám zápas se soupeřem " + opponent);
        result.setTrue(true);
        return result;
    }

    public Result editMatchInRepository(final String opponent, final boolean homeMatch, final String datum, final Season season,
                                        final List<Player> playerList, final List<Season> seasonList, Match match) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        match.setOpponent(opponent);
        match.setHomeMatch(homeMatch);
        match.setPlayerList(editListOfPlayers(playerList, match.getPlayerList()));
        try {
            long millis = date.convertTextDateToMillis(datum);
            match.setDateOfMatch(millis);
            if (season == null) {
                match.calculateSeason(seasonList);
            }
            else {
                match.setSeason(season);
            }
            firebaseRepository.editModel(match);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Datum musí být ve formátu " + Date.DATE_PATTERN);
            return result;
        }

        result.setText("Přidávám zápas se soupeřem " + opponent);
        result.setTrue(true);
        return result;
    }

    public Result removeMatchFromRepository(Match match) {
        isUpdating.setValue(true);
        Result result = new Result(false);

        try {
            firebaseRepository.removeModel(match);
        }
        catch (Exception e) {
            Log.e(TAG, "removeMatchFromRepository: chyba při mazání zápasu", e);
            result.setText("Chyba při posílání požadavku do DB");
            return result;
        }

        result.setText("Mažu zápas " + match.getName());
        result.setTrue(true);
        return result;
    }

    private void setMatchAsAdded(final Match match, Flag flag) {
        Log.d(TAG, "setMatchAsAdded: " + match.getName());
        String action = "";
        switch (flag) {
            case MATCH_PLUS:
                action = "přidán";
                break;
            case MATCH_EDIT:
                action = "upraven";
                break;
            case MATCH_DELETE:
                action = "smazán";
                break;
        }
        alert.setValue("Zápas " + match.getName() + " úspěšně " + action);
        isUpdating.setValue(false);
    }

    public List<Match> recalculateMatchSeason(Season season, List<Season> seasonList) {
        List<Match> matchList = new ArrayList<>();
        for (Match match : matches.getValue()) {
            if (match.getSeason().equals(season)) {
                match.calculateSeason(seasonList);
                matchList.add(match);
                firebaseRepository.editModel(match);
            }
        }
        return matchList;
    }

    private List<Player> createListOfPlayers(List<Player> playerList, List<Player> allPlayerList) {//nový, původní
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : allPlayerList) {
            if (playerList.contains(player)) {
                player.setMatchParticipant(true);
            }
            else {
                player.setMatchParticipant(false);
            }
            newPlayerList.add(player);
        }
        return newPlayerList;
    }

    private List<Player> editListOfPlayers(List<Player> playerList, List<Player> currentPlayerList) {
        List<Player> newPlayerList = new ArrayList<>();
        for (Player player : currentPlayerList) {
            if (playerList.contains(player)) {
                player.setMatchParticipant(true);
                newPlayerList.add(player);
            }
            else {
                player.setMatchParticipant(false);
                newPlayerList.add(player);
            }

        }
        for (Player player : playerList) {
            if (!newPlayerList.contains(player)) {
                player.setMatchParticipant(true);
                newPlayerList.add(player);
            }
        }
        return newPlayerList;
    }

    public Match findLastMatch() {
        if (matches.getValue() != null && matches.getValue().size() > 0) {
            return matches.getValue().get(0);
        }
        return null;
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    public void setSelectedSeason(Season season) {
        selectedSeason.postValue(season);
        firebaseRepository.loadMatchesFromRepository();
    }

    private void useSeasonsFilter(List<Match> models) {
        Log.d(TAG, "useSeasonsFilter: načítám zápasy s filtren" + selectedSeason.getValue());
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

    @Override
    public void itemAdded(Model model) {
        setMatchAsAdded((Match) model, Flag.MATCH_PLUS);
    }

    @Override
    public void itemChanged(Model model) {
        setMatchAsAdded((Match) model, Flag.MATCH_EDIT);
    }

    @Override
    public void itemDeleted(Model model) {
        setMatchAsAdded((Match) model, Flag.MATCH_DELETE);
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Collections.sort(models, new Comparator<Match>() {
            @Override
            public int compare(Match o1, Match o2) {
                return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
            }
        });
        Log.d(TAG, "itemListLoaded: zapasy: " + models);
        useSeasonsFilter(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
