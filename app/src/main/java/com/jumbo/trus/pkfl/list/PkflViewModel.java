package com.jumbo.trus.pkfl.list;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.comparator.OrderByDate;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.pkfl.PkflMatchDetail;
import com.jumbo.trus.pkfl.PkflMatchPlayer;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.web.RetrieveMatchDetailTask;
import com.jumbo.trus.web.RetrieveMatchesTask;
import com.jumbo.trus.web.TaskRunner;

import java.util.List;

public class PkflViewModel extends ViewModel implements ItemLoadedListener {

    private static final String TAG = "PkflViewModel";

    private MutableLiveData<List<PkflMatch>> matches;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private MutableLiveData<String> matchDetail = new MutableLiveData<>();
    private MutableLiveData<String> matchSecondDetail = new MutableLiveData<>();
    private int pickedPkflMatch;
    private String matchName;
    private boolean detailEnabled;

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
            taskRunner.executeAsync(new RetrieveMatchesTask(pkflUrl), new TaskRunner.Callback<List<PkflMatch>>() {
                @Override
                public void onComplete(List<PkflMatch> result) {
                    isUpdating.setValue(false);
                    if (result == null || result.size() == 0) {
                        alert.setValue("Nelze načíst zápasy. Je zadaná správná url nebo nemá web pkfl výpadek?");
                    } else {
                        matches.setValue(orderMatchesByTime(result));
                    }
                }
            });
        } else {
            waitingForLoad = true;
            firebaseRepository.loadPkflUrlFromRepository();
        }
    }

    public void loadMatchDetailFromPkfl(String url) {
        isUpdating.setValue(true);
        if (pkflUrl != null) {
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new RetrieveMatchDetailTask(url), new TaskRunner.Callback<PkflMatchDetail>() {
                @Override
                public void onComplete(PkflMatchDetail result) {
                    isUpdating.setValue(false);
                    if (result == null) {
                        alert.setValue("Nelze načíst zápas. Nemá web pkfl výpadek?");
                    } else {
                        Log.d(TAG, "onComplete: " + result.getPkflPlayers());
                        matchSecondDetail.setValue(setMatchDetailText(result));
                    }
                }
            });
        } else {
            waitingForLoad = true;
            firebaseRepository.loadPkflUrlFromRepository();
        }
    }

    public void setPickedPkflMatch(int pickedPkflMatch) {
        this.pickedPkflMatch = pickedPkflMatch;
        setMatchDetailData();
    }

    public void setMatchDetailData() {

        PkflMatch match = getMatches().getValue().get(pickedPkflMatch);
        matchName = match.toStringNameWithOpponent();
        String result;
        if (match.getResult().equals(":")) {
            result = "Zápas se ještě nehrál";
            detailEnabled = false;
        } else {
            result = match.getResult();
            detailEnabled = true;
        }
        String firstDetail = match.getRound() + ". kolo " + match.getLeague() + " hrané " + match.getDateAndTimeOfMatchInStringFormat() + "\n" +
                "Stadion: " + match.getStadium() + "\n" +
                "Rozhodčí: " + match.getReferee() + "\n" +
                "Výsledek: " + result;
        matchDetail.setValue(firstDetail);
    }

    public void setMatchSecondDetail() {
        PkflMatch match = getMatches().getValue().get(pickedPkflMatch);
        loadMatchDetailFromPkfl(match.getUrlResult());
        Log.d(TAG, "setMatchSecondDetail: " + match.getUrlResult());
    }


    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    public LiveData<String> getMatchDetail() {
        return matchDetail;
    }

    public LiveData<String> getmatchSecondDetail() {
        return matchSecondDetail;
    }


    public LiveData<List<PkflMatch>> getMatches() {
        return matches;
    }

    public String getMatchName() {
        return matchName;
    }

    public boolean isDetailEnabled() {
        return detailEnabled;
    }

    private List<PkflMatch> orderMatchesByTime(List<PkflMatch> pkflMatches) {
        pkflMatches.sort(new OrderByDate(false));
        return pkflMatches;
    }

    private String setMatchDetailText(PkflMatchDetail pkflMatchDetail) {
        StringBuilder text = new StringBuilder(pkflMatchDetail.getRefereeComment());
        text.append(returnBestPlayerText(pkflMatchDetail.getBestPlayer()) + returnGoalScorersText(pkflMatchDetail.getGoalScorers()) +
                returnOwnGoalScorersText(pkflMatchDetail.getOwnGoalScorers()) + returnYellowCardPlayersText(pkflMatchDetail.getYellowCardPlayers()) +
                returnRedCardPlayersText(pkflMatchDetail.getRedCardPlayers()));
        return text.toString();
    }

    private String returnBestPlayerText(PkflMatchPlayer bestPlayer) {
        if (bestPlayer != null) {
            return ("\n\nHvězda zápasu: " + bestPlayer.getName());
        }
        return "";
    }

    private String returnGoalScorersText(List<PkflMatchPlayer> pkflMatchPlayers) {
        StringBuilder text = new StringBuilder("");
        if (pkflMatchPlayers.size() > 0) {
            text.append("\n\nStřelci:\n");
            for (PkflMatchPlayer pkflMatchPlayer : pkflMatchPlayers) {
                int goalNumber = pkflMatchPlayer.getGoals();
                text.append(pkflMatchPlayer.getName() + ": " + pkflMatchPlayer.getGoals() + ((goalNumber == 1) ? " gól" : " góly") + "\n");
            }
        }
        return text.toString();
    }

    private String returnOwnGoalScorersText(List<PkflMatchPlayer> pkflMatchPlayers) {
        StringBuilder text = new StringBuilder("");
        if (pkflMatchPlayers.size() > 0) {
            text.append("\nStřelci vlastňáků:\n");
            for (PkflMatchPlayer pkflMatchPlayer : pkflMatchPlayers) {
                text.append(pkflMatchPlayer.getName() + ": " + pkflMatchPlayer.getOwnGoals() + " vlastňák\n");
            }
        }
        return text.toString();
    }

    private String returnYellowCardPlayersText(List<PkflMatchPlayer> pkflMatchPlayers) {
        StringBuilder text = new StringBuilder("");
        if (pkflMatchPlayers.size() > 0) {
            text.append("\nŽluté karty:\n");
            for (PkflMatchPlayer pkflMatchPlayer : pkflMatchPlayers) {
                text.append(pkflMatchPlayer.getName() + ": " + pkflMatchPlayer.getYellowCards() + "\n");
            }
        }
        return text.toString();
    }

    private String returnRedCardPlayersText(List<PkflMatchPlayer> pkflMatchPlayers) {
        StringBuilder text = new StringBuilder("");
        if (pkflMatchPlayers.size() > 0) {
            text.append("\nČervené karty:\n");
            for (PkflMatchPlayer pkflMatchPlayer : pkflMatchPlayers) {
                text.append(pkflMatchPlayer.getName() + ": " + pkflMatchPlayer.getRedCards() + "\n");
            }
        }
        return text.toString();
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
