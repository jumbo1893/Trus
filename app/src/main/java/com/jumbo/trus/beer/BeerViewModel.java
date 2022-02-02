package com.jumbo.trus.beer;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BeerViewModel extends ViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "BeerViewModel";

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

    public Result editMatchBeers(final List<Player> playerList, Match match) {
        isUpdating.setValue(true);
        Result result = new Result(false);
        match.mergePlayerLists(playerList);
        try {
            firebaseRepository.editModel(match);
        }
        catch (Exception e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Něco se posralo při přidávání do db, zkus to znova");
            alert.setValue(result.getText());
            isUpdating.setValue(true);
            return result;
        }

        result.setText("Měním pivka u zápasu se soupeřem " + match.getOpponent());
        result.setTrue(true);
        alert.setValue(result.getText());
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
