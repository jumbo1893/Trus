package com.jumbo.trus.pkfl.stats;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.comparator.pkfl.OrderByBestPlayerNumber;
import com.jumbo.trus.comparator.pkfl.OrderByBestPlayerRatio;
import com.jumbo.trus.comparator.pkfl.OrderByCleanSheetNumber;
import com.jumbo.trus.comparator.pkfl.OrderByGoalMatchesRatio;
import com.jumbo.trus.comparator.pkfl.OrderByGoalNumber;
import com.jumbo.trus.comparator.pkfl.OrderByGoalkeepingMinutesNumber;
import com.jumbo.trus.comparator.pkfl.OrderByHattrickNumber;
import com.jumbo.trus.comparator.pkfl.OrderByMatchesNumber;
import com.jumbo.trus.comparator.pkfl.OrderByOwnGoalNumber;
import com.jumbo.trus.comparator.pkfl.OrderByReceivedGoalNumber;
import com.jumbo.trus.comparator.pkfl.OrderByReceivedGoalsRatio;
import com.jumbo.trus.comparator.pkfl.OrderByRedCardsNumber;
import com.jumbo.trus.comparator.pkfl.OrderByYellowCardRatio;
import com.jumbo.trus.comparator.pkfl.OrderByYellowCardsNumber;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.pkfl.PkflMatchDetail;
import com.jumbo.trus.pkfl.PkflMatchPlayer;
import com.jumbo.trus.pkfl.PkflSeason;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.statistics.player.ListTexts;
import com.jumbo.trus.web.RetrieveMatchDetailTask;
import com.jumbo.trus.web.RetrieveMatchesTask;
import com.jumbo.trus.web.RetrieveSeasonUrlTask;
import com.jumbo.trus.web.TaskRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PkflStatsViewModel extends ViewModel {

    private static final String TAG = "PkflStatsViewModel";

    protected MutableLiveData<List<ListTexts>> recycleViewList;
    protected MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    protected MutableLiveData<String> loadingAlert = new MutableLiveData<>();
    protected MutableLiveData<String> alert = new MutableLiveData<>();
    protected MutableLiveData<List<SpinnerOption>> spinnerOptions = new MutableLiveData<>();
    protected MutableLiveData<SpinnerOption> pickedSpinnerOption = new MutableLiveData<>();
    protected List<PkflMatch> matches = new ArrayList<>();
    protected HashMap<PkflMatchPlayer, PkflPlayerStats> pkflPlayerStatsHashMap = new HashMap<>();
    protected boolean descendingOrder = true;
    protected List<PkflSeason> pkflSeasons = new ArrayList<>();
    protected String baseLoadingAlert = "";

    protected FirebaseRepository firebaseRepository;
    protected String pkflUrl = null;
    protected boolean waitingForLoad = true;
    private int matchNumber = 0;
    private int allMatchesNumber;


    private void initSpinnerOptions() {
        List<SpinnerOption> spinnerOptionsList = new ArrayList<>();
        spinnerOptionsList.add(SpinnerOption.GOALS);
        spinnerOptionsList.add(SpinnerOption.BEST_PLAYERS);
        spinnerOptionsList.add(SpinnerOption.YELLOW_CARDS);
        spinnerOptionsList.add(SpinnerOption.RED_CARDS);
        spinnerOptionsList.add(SpinnerOption.OWN_GOALS);
        spinnerOptionsList.add(SpinnerOption.RECEIVED_GOALS);
        spinnerOptionsList.add(SpinnerOption.GOALKEEPING_MINUTES);
        spinnerOptionsList.add(SpinnerOption.MATCHES);
        spinnerOptionsList.add(SpinnerOption.HATTRICK);
        spinnerOptionsList.add(SpinnerOption.CLEAN_SHEET);
        spinnerOptionsList.add(SpinnerOption.GOAL_RATIO);
        spinnerOptionsList.add(SpinnerOption.BEST_PLAYER_RATIO);
        spinnerOptionsList.add(SpinnerOption.RECEIVED_GOALS_RATIO);
        spinnerOptionsList.add(SpinnerOption.YELLOW_CARD_RATIO);
        spinnerOptionsList.add(SpinnerOption.CARD_DETAIL);
        spinnerOptions.setValue(spinnerOptionsList);
    }

    protected void loadSeasonUrlsFromPkfl(boolean currentSeason) {
        Log.d(TAG, "loadSeasonUrlsFromPkfl: " + pkflUrl);
        if (pkflUrl != null) {
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new RetrieveSeasonUrlTask(pkflUrl, currentSeason), new TaskRunner.Callback<List<PkflSeason>>() {
                @Override
                public void onComplete(List<PkflSeason> result) {
                    if (result == null || result.size() == 0) {
                        alert.setValue("Nelze na????st z??pasy. Je zadan?? spr??vn?? url nebo nem?? web pkfl v??padek?");
                        isUpdating.setValue(false);
                    } else {
                        pkflSeasons = result;
                        allMatchesNumber = calculateApproximateNumberOfMatches(result.size());
                        loadMatchesFromSeasons();
                        Log.d(TAG, "onComplete: " + pkflSeasons);
                    }
                }
            });
        } else {
            waitingForLoad = true;
            firebaseRepository.loadPkflUrlFromRepository();
        }
    }

    private void loadMatchesFromPkfl(String url) {
        Log.d(TAG, "loadMatchesFromPkfl: " + url);
        if (url != null) {
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new RetrieveMatchesTask(url), new TaskRunner.Callback<List<PkflMatch>>() {
                @Override
                public void onComplete(List<PkflMatch> result) {
                    if (result == null || result.size() == 0) {
                        alert.setValue("Nelze na????st z??pasy. Je zadan?? spr??vn?? url nebo nem?? web pkfl v??padek?");
                    } else {
                        matches.addAll(result);
                        loadMatchDetails(matches);
                    }
                }
            });
        } else {
            waitingForLoad = true;
            firebaseRepository.loadPkflUrlFromRepository();
        }
    }

    private void loadMatchesFromSeasons() {
        isUpdating.setValue(true);
        if (!pkflSeasons.isEmpty()) {
            PkflSeason pkflSeason = pkflSeasons.remove(0);
            baseLoadingAlert = "Na????t??m ??daje ze sezony " + pkflSeason.getName();
            loadingAlert.setValue(baseLoadingAlert);
            loadMatchesFromPkfl(pkflSeason.getUrl());
            return;
        }
        Log.d(TAG, "loadMatchDetails: hotovo");
        initPlayerStatsList();
        initSpinnerOptions();
        setSpinnerOption(spinnerOptions.getValue().get(0));
        isUpdating.setValue(false);
        loadingAlert.setValue("");

    }

    private void loadMatchDetails(List<PkflMatch> pkflMatches) {

        Log.d(TAG, "loadMatchDetails: ");
        for (PkflMatch pkflMatch : pkflMatches) {
            if (pkflMatch.getPkflMatchDetail() == null) {
                loadingAlert.setValue(baseLoadingAlert + "\n z??pas: " + pkflMatch.getOpponent() + "\n" + calculateLoadPercentage() + " %");
                loadMatchDetailFromPkfl(pkflMatch);
                return;
            }
        }
        loadMatchesFromSeasons();
    }

    private void initPlayerStatsList() {
        for (PkflMatch pkflMatch : matches) {
            for (PkflMatchPlayer pkflMatchPlayer : pkflMatch.getPkflMatchDetail().getPkflPlayers()) {
                PkflPlayerStats hashStatPlayer = pkflPlayerStatsHashMap.get(pkflMatchPlayer);
                if (hashStatPlayer == null) {
                    PkflPlayerStats pkflPlayerStats = new PkflPlayerStats(pkflMatchPlayer.getName());
                    pkflPlayerStats.enhanceWithPlayerDetail(pkflMatchPlayer);
                    pkflPlayerStatsHashMap.put(pkflMatchPlayer, pkflPlayerStats);
                } else {
                    hashStatPlayer.enhanceWithPlayerDetail(pkflMatchPlayer);
                }
            }
        }
    }

    private void loadMatchDetailFromPkfl(final PkflMatch pkflMatch) {
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new RetrieveMatchDetailTask(pkflMatch.getUrlResult()), new TaskRunner.Callback<PkflMatchDetail>() {
            @Override
            public void onComplete(PkflMatchDetail result) {
                if (result == null) {
                    alert.setValue("Nelze na????st z??pas. Nem?? web pkfl v??padek?");
                } else {
                    Log.d(TAG, "onComplete: " + result.getPkflPlayers());
                    pkflMatch.setPkflMatchDetail(result);
                    loadMatchDetails(matches);
                }
            }
        });
    }

    private int calculateApproximateNumberOfMatches(int seasonNumber) {
        int NUMBER_OF_MATCHES_IN_SEASON = 11; //konstanta
        return seasonNumber*NUMBER_OF_MATCHES_IN_SEASON;
    }

    private int calculateLoadPercentage() {
        matchNumber++;
        int percentage = 100*matchNumber/allMatchesNumber;
        if (percentage > 100) {
            return 100;
        }
        return percentage;
    }

    public void setSpinnerOption(SpinnerOption spinnerOption) {
        this.pickedSpinnerOption.setValue(spinnerOption);
        descendingOrder = true;
        showStatisticsToFragment(spinnerOption);
    }

    private void showStatisticsToFragment(SpinnerOption spinnerOption) {
        Log.d(TAG, "showStatisticsToFragment: " + spinnerOption);
        switch (spinnerOption) {
            case GOALS:
                showGoalStatistics();
                break;
            case BEST_PLAYERS:
                showBestPlayerStatistics();
                break;
            case OWN_GOALS:
                showOwnGoalStatistics();
                break;
            case RED_CARDS:
                showRedCardStatistics();
                break;
            case YELLOW_CARDS:
                showYellowCardStatistics();
                break;
            case RECEIVED_GOALS:
                showReceivedGoalsStatistics();
                break;
            case MATCHES:
                showMatchesStatistics();
                break;
            case GOALKEEPING_MINUTES:
                showGoalkeepingMinutesStatistics();
                break;
            case GOAL_RATIO:
                showGoalMatchesRatio();
                break;
            case BEST_PLAYER_RATIO:
                showBestPlayerRatio();
                break;
            case YELLOW_CARD_RATIO:
                showYellowCardRatio();
                break;
            case RECEIVED_GOALS_RATIO:
                showReceivedGoalsRatio();
                break;
            case HATTRICK:
                showHattrickStatistics();
                break;
            case CLEAN_SHEET:
                showCleanSheetStatistics();
                break;
            case CARD_DETAIL:
                showCardComment();
                break;
        }
    }

    private void showGoalStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByGoalNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et g??l?? " + player.getGoals() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showMatchesStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByMatchesNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showReceivedGoalsStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByReceivedGoalNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et obdr??en??ch g??l?? " + player.getReceivedGoals() + ", po??et odchytan??ch z??pas?? " + player.getGoalkeepingMinutes() / 60));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showOwnGoalStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByOwnGoalNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et vlast????k?? " + player.getOwnGoals() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showGoalkeepingMinutesStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByGoalkeepingMinutesNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et odchytan??ch minut " + player.getGoalkeepingMinutes() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showYellowCardStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByYellowCardsNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et ??lutejch " + player.getYellowCards() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showRedCardStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByRedCardsNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et ??ervenejch " + player.getRedCards() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showBestPlayerStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByBestPlayerNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et hv??zd utk??n?? " + player.getBestPlayer() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showGoalMatchesRatio() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByGoalMatchesRatio(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et g??l?? na z??pas " + player.getGoalMatchesRatio() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showBestPlayerRatio() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByBestPlayerRatio(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et hv??zd utk??n?? na z??pas " + player.getBestPlayerMatchesRatio() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showReceivedGoalsRatio() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByReceivedGoalsRatio(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            if (player.getGoalkeepingMinutes() != 0) {
                listTexts.add(new ListTexts(player.getName(), "po??et obdr??en??ch g??l?? na z??pas " + player.getReceivedGoalsGoalkeepingMinutesRatio() + ", po??et odchytan??ch z??pas?? " + player.getGoalkeepingMinutes() / 60));
            }
        }
        recycleViewList.setValue(listTexts);
    }

    private void showYellowCardRatio() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByYellowCardRatio(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et ??lut??ch na z??pas " + player.getYellowCardMatchesRatio() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showHattrickStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByHattrickNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            listTexts.add(new ListTexts(player.getName(), "po??et hattrick?? " + player.getHattrick() + ", po??et z??pas?? " + player.getMatches()));
        }
        recycleViewList.setValue(listTexts);
    }

    private void showCleanSheetStatistics() {
        List<PkflPlayerStats> playerStats = new ArrayList<>(pkflPlayerStatsHashMap.values());
        Collections.sort(playerStats, new OrderByCleanSheetNumber(descendingOrder));
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflPlayerStats player : playerStats) {
            if (player.getGoalkeepingMinutes() != 0) {
                listTexts.add(new ListTexts(player.getName(), "po??et ??istejch kont " + player.getCleanSheet() + ", po??et odchytan??ch z??pas?? " + (float)player.getGoalkeepingMinutes() / 60));
            }
        }
        recycleViewList.setValue(listTexts);
    }

    private void showCardComment() {
        List<ListTexts> listTexts = new ArrayList<>();
        for (PkflMatch pkflMatch : matches) {
            for (PkflMatchPlayer pkflMatchPlayer : pkflMatch.getPkflMatchDetail().getPkflPlayers()) {
                if (pkflMatchPlayer.getRedCardComment() != null && !pkflMatchPlayer.getRedCardComment().isEmpty()) {
                    listTexts.add(new ListTexts(pkflMatchPlayer.getName(), "??erven?? karta v z??pase " + pkflMatch.toStringNameWithOpponent() + ", hran??m " + pkflMatch.getDateAndTimeOfMatchInStringFormat() + " s kone??n??m v??sledkem " + pkflMatch.getResult() +
                            ".\n Koment???? sud??ho: " + pkflMatchPlayer.getRedCardComment()));
                }
                if (pkflMatchPlayer.getYellowCardComment() != null && !pkflMatchPlayer.getYellowCardComment().isEmpty()) {
                    listTexts.add(new ListTexts(pkflMatchPlayer.getName(), "??lut?? karta v z??pase " + pkflMatch.toStringNameWithOpponent() + ", hran??m " + pkflMatch.getDateAndTimeOfMatchInStringFormat() + " s kone??n??m v??sledkem " + pkflMatch.getResult() +
                            ".\n Koment???? sud??ho: " + pkflMatchPlayer.getYellowCardComment()));
                }
            }
        }
        recycleViewList.setValue(listTexts);
    }


    public void changeOrder() {
        descendingOrder = !descendingOrder;
        if (pickedSpinnerOption.getValue() != null) {
            showStatisticsToFragment(pickedSpinnerOption.getValue());
        }
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    public LiveData<String> getLoadingAlert() {
        return loadingAlert;
    }

    public LiveData<List<ListTexts>> getRecycleViewList() {
        return recycleViewList;
    }

    public LiveData<List<SpinnerOption>> getSpinnerOptions() {
        return spinnerOptions;
    }

    public LiveData<SpinnerOption> getPickedSpinnerOption() {
        return pickedSpinnerOption;
    }

    public void removeReg() {
        firebaseRepository.removeListener();
    }
}
