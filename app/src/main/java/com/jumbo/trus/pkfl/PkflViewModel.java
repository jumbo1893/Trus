package com.jumbo.trus.pkfl;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.Validator;
import com.jumbo.trus.comparator.OrderByDate;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.web.RetreiveMatchesTask;
import com.jumbo.trus.web.TaskRunner;

import java.time.DateTimeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PkflViewModel extends ViewModel implements ItemLoadedListener {

    private static final String TAG = "PkflViewModel";

    private MutableLiveData<List<PkflMatch>> matches;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private MutableLiveData<PkflMatch> match = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;
    private String pkflUrl = null;
    private boolean waitingForLoad = false;


    public void init() {
        firebaseRepository = new FirebaseRepository(this);
        if (matches == null) {
            matches = new MutableLiveData<>();
            firebaseRepository.loadPkflUrlFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
    }

    public void loadMatchesFromPkfl() {
        isUpdating.setValue(true);
        if (pkflUrl != null) {
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new RetreiveMatchesTask(pkflUrl), new TaskRunner.Callback<List<PkflMatch>>() {
                @Override
                public void onComplete(List<PkflMatch> result) {
                    isUpdating.setValue(false);
                    matches.setValue(orderMatchesByTime(result));
                    setLastMatch();
                }
            });
        }
        else {
            waitingForLoad = true;
            firebaseRepository.loadPkflUrlFromRepository();
        }
    }


    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    public LiveData<List<PkflMatch>> getMatches() {
        return matches;
    }

    public LiveData<PkflMatch> getMatch() {
        return match;
    }

    private List<PkflMatch> orderMatchesByTime(List<PkflMatch> pkflMatches) {
        pkflMatches.sort(new OrderByDate(true));
        return pkflMatches;
    }

    private void setLastMatch() {
        long currentTime = System.currentTimeMillis();
        PkflMatch returnMatch = null;
        if (matches.getValue() != null) {
            for (PkflMatch pkflMatch : matches.getValue()) {
                if (pkflMatch.getDate() < currentTime) {
                    if (returnMatch == null || returnMatch.getDate() < pkflMatch.getDate()) {
                        returnMatch = pkflMatch;
                    }
                }
            }
            match.setValue(returnMatch);
        }
        else {
            loadMatchesFromPkfl();
        }
    }


    @Override
    public void itemLoaded(String value) {
        pkflUrl = value;
        if (waitingForLoad) {
            loadMatchesFromPkfl();
            waitingForLoad = false;
        }

    }
}
