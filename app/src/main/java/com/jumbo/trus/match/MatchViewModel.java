package com.jumbo.trus.match;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.ChangeListener;
import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.User;
import com.jumbo.trus.Validator;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchViewModel extends ViewModel implements ChangeListener {

    private static final String TAG = "MatchViewModel";

    private MutableLiveData<List<Match>> matches;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;
    private User user = new User("test_user");

    public void init() {
        firebaseRepository = new FirebaseRepository("match", this);
        if (matches == null) {
            matches = new MutableLiveData<>();
            firebaseRepository.loadMatchesFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
    }

    public Result checkNewMatchValidation(final String name, final String datum, List<Player> playerList) {
        Validator validator = new Validator();
        String response = "";
        boolean result = false;
        if (!validator.fieldIsNotEmpty(name)) {
            response = "Není vyplněné jméno soupeře";
        }
        else if (!validator.checkNameFormat(name)) {
            response = "Jméno soupeře je moc dlouhý nebo obsahuje nesmysly";
        }
        else if (!validator.fieldIsNotEmpty(datum)) {
            response = "Není vyplněné datum";
        }
        else if (!validator.isDateInCorrectFormat(datum)) {
            response = "Datum musí být ve formátu " + Date.DATE_PATTERN;
        }
        else if (validator.isListEmpty(playerList)) {
            response = "Seznam hráčů je prázdný, aspoň někdo snad hrále ne";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result addMatchToRepository(final String opponent, final boolean homeMatch, final String datum, final Season season,
                                       final List<Player> playerList, final List<Season> seasonList) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        try {
            long millis = date.convertTextDateToMillis(datum);
            Match match;
            if (season == null) {
                match = new Match(opponent, millis, homeMatch, playerList, seasonList);
            }
            else {
                match = new Match(opponent, millis, homeMatch, season, playerList);
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
        match.setPlayerList(playerList);
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

    public Result editMatchBeers(final List<Player> playerList, Match match) {
        isUpdating.setValue(true);
        Result result = new Result(false);
        match.setPlayerList(playerList);
        try {
            firebaseRepository.editModel(match);
        }
        catch (Exception e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Něco se posralo při přidávání do db, zkus to znova");
            isUpdating.setValue(true);
            return result;
        }

        result.setText("Měním pivka u zápasu se soupeřem " + match.getOpponent());
        result.setTrue(true);
        return result;
    }

    public Result editMatchPlayerFines (List<ReceivedFine> fineList, Player player, Match match) {
        isUpdating.setValue(true);
        Result result = new Result(false);
        if (!match.changePlayerFinesInPlayerList(player, fineList)) {
            result.setText("Nelze nalézt hráče v zápase, nesmazal ho nějakej zmrd?");
            isUpdating.setValue(true);
            return result;
        }
        try {
            firebaseRepository.editModel(match);
        }
        catch (Exception e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Něco se posralo při přidávání do db, zkus to znova");
            isUpdating.setValue(true);
            return result;
        }

        result.setText("Měním pokuty u zápasu se soupeřem " + match.getOpponent() + " u hráče " + player.getName());
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
        if (flag != Flag.MATCH_EDIT) {
            Notification newNotification = new Notification("Zápas " + match.getName() + " " + action, user);
            sendNotificationToRepository(newNotification);
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
                return o1.getDateOfMatch() > o2.getDateOfMatch() ? -1 : (o1.getDateOfMatch() < o2.getDateOfMatch()) ? 1 : 0;
            }
        });
        Log.d(TAG, "itemListLoaded: zapasy: " + models);
        matches.setValue(models);

    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
