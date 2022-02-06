package com.jumbo.trus.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.web.RetreiveMatchesTask;
import com.jumbo.trus.web.TaskRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class HomeViewModel extends BaseViewModel implements ChangeListener, ItemLoadedListener {

    private static final String TAG = "HomeViewModel";
    private List<String> randomFacts;
    private MutableLiveData<String> randomFact = new MutableLiveData<>();
    private MutableLiveData<String> playerBirthday = new MutableLiveData<>();
    private MutableLiveData<String> pkflMatch = new MutableLiveData<>();
    private MutableLiveData<List<Player>> players;
    private MutableLiveData<List<Match>> matches;
    private MutableLiveData<List<Season>> seasons;
    private MutableLiveData<List<Fine>> fines;
    private MutableLiveData<Match> pickedMainMatch = new MutableLiveData<>();

    private RandomFact fact;
    private int factNumberToShow = -1;



    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(this, this);
        if (randomFact == null) {
            randomFact.setValue("načítám...");
        }
        if (randomFacts == null) {
            randomFacts = new ArrayList<>();
            //firebaseRepository.loadNotificationsFromRepository();
            Log.d(TAG, "init: načítám zajimavosti");
        }
        if (players == null) {
            players = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
        if (matches == null) {
            matches = new MutableLiveData<>();
            firebaseRepository.loadMatchesFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
        if (seasons == null) {
            seasons = new MutableLiveData<>();
            firebaseRepository.loadSeasonsFromRepository();
            Log.d(TAG, "init: nacitam sezony");
        }
        if (fines == null) {
            fines = new MutableLiveData<>();
            firebaseRepository.loadFinesFromRepository();
            Log.d(TAG, "init: nacitam pokuty");
        }
        firebaseRepository.loadPkflUrlFromRepository();
    }

    private void setRandomFacts() {
        Log.d(TAG, "setRandomFacts: ");
        fact = new RandomFact(players.getValue(), matches.getValue(), seasons.getValue(), fines.getValue());
        randomFacts.clear();
        randomFacts.add(fact.getPlayerWithMostBeers()); //vrátí hráče (1 či víc) co za celou historii vypil nejvíce piv
        randomFacts.add(fact.getMatchWithMostBeers()); // vrátí zápas(y), ve kterých padlo nejvíce piv
        randomFacts.add(fact.getNumberOfBeersInCurrentSeason()); //vrátí počet piv v aktuální sezoně dle data
        randomFacts.add(fact.getMatchWithMostBeersInCurrentSeason()); //vrátí zápas aktuální sezony kde se nejvíc pilo
        randomFacts.add(fact.getSeasonWithMostBeers()); //vrátí sezonu s nejvíce pivy
        randomFacts.add(fact.getAverageNumberOfBeersInMatchForPlayersAndFans()); //vrátí průměrný počet piv na hráče a fanouška
        randomFacts.add(fact.getAverageNumberOfBeersInMatchForPlayers()); //vrátí průměrný počet vypitejch piv na hráče
        randomFacts.add(fact.getAverageNumberOfBeersInMatchForFans()); //vrátí průměrný počet vypitejch piv na fanouška
        randomFacts.add(fact.getAverageNumberOfBeersInMatch()); //vrátí průměrný počet vypitejch piv v jednom zápase
        randomFacts.add(fact.getMatchWithHighestAverageBeers()); //vrátí zápas s nejvyšším průměrem piv
        randomFacts.add(fact.getMatchWithLowestAverageBeers()); //vrátí zápas s nejnižším průměrem piv
        randomFacts.add(fact.getAverageNumberOfBeersInHomeAndAwayMatch()); // vrátí průměrný piv v domácím a venkovním zápase
        randomFacts.add(fact.getHighestAttendanceInMatch()); //vrátí zápas s nejvyšší účastí
        randomFacts.add(fact.getLowestAttendanceInMatch()); //vrátí zápas s nejnižší účastí
        randomFacts.add(fact.getMatchWithBirthday()); //vrátí zápas na kterém někdo slavil narozky

        randomFacts.add(fact.getPlayerWithMostLiquors()); //vrátí hráče (1 či víc) co za celou historii vypil nejvíce piv
        randomFacts.add(fact.getMatchWithMostLiquors()); // vrátí zápas(y), ve kterých padlo nejvíce piv
        randomFacts.add(fact.getNumberOfLiquorsInCurrentSeason()); //vrátí počet piv v aktuální sezoně dle data
        randomFacts.add(fact.getMatchWithMostLiquorsInCurrentSeason()); //vrátí zápas aktuální sezony kde se nejvíc pilo
        randomFacts.add(fact.getSeasonWithMostLiquors()); //vrátí sezonu s nejvíce pivy
        randomFacts.add(fact.getAverageNumberOfLiquorsInMatchForPlayersAndFans()); //vrátí průměrný počet piv na hráče a fanouška
        randomFacts.add(fact.getAverageNumberOfLiquorsInMatchForPlayers()); //vrátí průměrný počet vypitejch piv na hráče
        randomFacts.add(fact.getAverageNumberOfLiquorsInMatchForFans()); //vrátí průměrný počet vypitejch piv na fanouška
        randomFacts.add(fact.getAverageNumberOfLiquorsInMatch()); //vrátí průměrný počet vypitejch piv v jednom zápase
        randomFacts.add(fact.getMatchWithHighestAverageLiquors()); //vrátí zápas s nejvyšším průměrem piv
        randomFacts.add(fact.getMatchWithLowestAverageLiquors()); //vrátí zápas s nejnižším průměrem piv
        randomFacts.add(fact.getAverageNumberOfLiquorsInHomeAndAwayMatch()); // vrátí průměrný piv v domácím a venkovním zápase

        randomFacts.add(fact.getPlayerWithMostFines()); //vrátí hráče s největším počtem pokut
        randomFacts.add(fact.getMatchWithMostFines()); //vrátí zápas s největším počtem pokut
        randomFacts.add(fact.getPlayerWithMostFinesAmount()); //vrátí hráče co zaplatil nejvíc na pokutách
        randomFacts.add(fact.getMatchWithMostFinesAmount()); //vrátí zápas kde se nejvíc vydělalo na pokutách
        randomFacts.add(fact.getNumberOfFinesInCurrentSeason()); //vrátí počet pokut v aktuální sezoně
        randomFacts.add(fact.getAmountOfFinesInCurrentSeason()); //vrátí kolik se vybralo v aktuální sezoně
        randomFacts.add(fact.getMatchWithMostFinesInCurrentSeason()); //vrátí zápas aktuální sezony kde padlo nejvíc pokut
        randomFacts.add(fact.getMatchWithMostFinesAmountInCurrentSeason()); //vrátí zápas aktuální sezony kde se nejvíc vydělalo
        randomFacts.add(fact.getSeasonWithMostFines()); //vrátí sezonu s nejvíc pokutama
        randomFacts.add(fact.getSeasonWithMostFinesAmount()); //vrátí sezonu kde se vybralo nejvíc peněz
        randomFacts.add(fact.getAverageNumberOfFinesInMatchForPlayers()); //vrátí průměrný počet pokut na hráče a zápas
        randomFacts.add(fact.getAverageNumberOfFinesAmountInMatchForPlayers()); //vrátí průměrný výdělek na hráče a zápas
        randomFacts.add(fact.getAverageNumberOfFinesInMatch()); //vrátí průměrný počet pokut na zápas
        randomFacts.add(fact.getAverageNumberOfFinesAmountInMatch()); //vrátí průměrný výdělek na pokutách za zápas
        randomFacts.add(fact.getTheMostCommonFineInAllMatches()); //vrátí nejčastěji udělovanou pokutu
        randomFacts.add(fact.getTheMostProfitableFineInAllMatches()); //vrátí nejvýdělečnější pokutu
    }

    public void initRandomFact() {
        if (seasons.getValue() != null && fines.getValue() != null && players.getValue() != null && matches.getValue() != null) {
            setRandomFacts();
            if (factNumberToShow == -1) {
                randomlySetNewFact();
            }
        }
        else {
            randomFact.setValue("načítám...");
        }
    }

    private void setPlayerBirthday() {
        if (fact != null) {
            playerBirthday.setValue(fact.getPlayerWithEarliestBirthDay());
        }
        else {
                playerBirthday.setValue("načítám...");
            }
    }

    public void randomlySetNewFact() {
        if (randomFacts != null) {
            Random rand = new Random();
            factNumberToShow = rand.nextInt(randomFacts.size());
            randomFact.setValue(randomFacts.get(factNumberToShow));
        }
        else {
            alert.setValue("Vydrž než se načtou všechny potřebný data");
        }
    }

    public void setNextRandomFact(boolean next) {
        if (randomFacts != null) {
            int randomFactSize = randomFacts.size()-1;
            if (next) {
                if (randomFactSize == factNumberToShow) {
                    factNumberToShow = 0;
                }
                else factNumberToShow++;
            }
            else {
                if (factNumberToShow == 0) {
                    factNumberToShow = randomFactSize;
                }
                else {
                    factNumberToShow--;
                }
            }
            setRandomFact();
        }
        else {
            alert.setValue("Vydrž než se načtou všechny potřebný data");
        }
    }

    private void setRandomFact() {
        randomFact.setValue(randomFacts.get(factNumberToShow));
    }

    public void loadMatchesFromPkfl(String pkflUrl) {
        isUpdating.setValue(true);
        if (pkflUrl != null) {
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new RetreiveMatchesTask(pkflUrl), new TaskRunner.Callback<List<PkflMatch>>() {
                @Override
                public void onComplete(List<PkflMatch> result) {
                    isUpdating.setValue(false);
                    if (result == null || result.size() == 0) {
                        alert.setValue("Nelze načíst zápasy. Je zadaná správná url nebo nemá web pkfl výpadek?");
                    }
                    else {
                        setNextMatch(result);
                    }
                }
            });
        }
    }

    private void setNextMatch(List<PkflMatch> matches) {
        long currentTime = System.currentTimeMillis();
        PkflMatch returnMatch = null;
        if (matches != null) {
            for (PkflMatch pkflMatch : matches) {
                if (pkflMatch.getDate() > currentTime) {
                    if (returnMatch == null || returnMatch.getDate() > pkflMatch.getDate()) {
                        returnMatch = pkflMatch;
                    }
                }
            }
            if (returnMatch != null) {
                setPkflMatchText(returnMatch);
            }
            else {
                pkflMatch.setValue("Nelze najít žádný další zápas Liščího Trusu!");
            }
        }
    }

    private void setPkflMatchText(PkflMatch match) {
        String result = match.toStringNameWithOpponent() + ", v čase " + match.getDateAndTimeOfMatchInStringFormat() +
                ". Jedná se o " + match.getRound() + ". kolo a bude se hrát na hřišti " + match.getStadium() +
                ". Pískat bude " + (match.getReferee().isEmpty() ? "zatím neznámý rozhodčí" : match.getReferee())  + ".";
        pkflMatch.setValue(result);
    }

    private void findLastMatch(List<Match> matches) {
        if (matches != null && matches.size() > 0) {
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
            pickedMainMatch.setValue(matches.get(0));
        }
    }

    public LiveData<String> getRandomFact() {
        return randomFact;
    }

    public LiveData<Match> getLastMainMatch() {
        return pickedMainMatch;
    }

    public LiveData<String> getPlayerBirthday() {
        return playerBirthday;
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }

    public LiveData<List<Fine>> getFines() {
        return fines;
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    public List<String> getRandomFacts() {
        return randomFacts;
    }

    public  LiveData<String> getPkflMatch() {
        return pkflMatch;
    }


    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {

    }

    @Override
    public void itemDeleted(Model model) {

    }

    @Override
    public void itemListLoaded(List<Model> models, Flag flag) {
        List list = new ArrayList(models);
        switch (flag) {
            case PLAYER:
                players.setValue(list);
                break;
            case MATCH:
                matches.setValue(list);
                if (pickedMainMatch.getValue() == null) {
                    Log.d(TAG, "itemListLoaded: ");
                    findLastMatch(list);
                }
                break;
            case SEASON:
                Season season = new Season().otherSeason();
                list.add(season);
                seasons.setValue(list);
                break;
            case FINE:
                fines.setValue(list);
                break;
        }
        initRandomFact();
        setPlayerBirthday();
    }

    @Override
    public void alertSent(String message) {

    }

    @Override
    public void itemLoaded(String value) {
        loadMatchesFromPkfl(value);
    }
}
