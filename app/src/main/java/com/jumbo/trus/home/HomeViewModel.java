package com.jumbo.trus.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeViewModel extends ViewModel implements ChangeListener {

    private static final String TAG = "HomeViewModel";
    private List<String> randomFacts;
    private MutableLiveData<String> randomFact = new MutableLiveData<>();
    private MutableLiveData<String> playerBirthday = new MutableLiveData<>();
    private MutableLiveData<List<Player>> players;
    private MutableLiveData<List<Match>> matches;
    private MutableLiveData<List<Season>> seasons;
    private MutableLiveData<List<Fine>> fines;
    private boolean seasonsLoaded = false;
    private boolean playersLoaded = false;
    private boolean matchesLoaded = false;
    private boolean finesLoaded = false;
    private boolean firstFact = true;

    private RandomFact fact;


    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(this);
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
    }

    public void reloadDatabaseModels() {
        seasonsLoaded = false;
        playersLoaded = false;
        matchesLoaded = false;
        finesLoaded = false;
        firebaseRepository.loadPlayersFromRepository();
        firebaseRepository.loadMatchesFromRepository();
        firebaseRepository.loadSeasonsFromRepository();
        firebaseRepository.loadFinesFromRepository();
    }

    public void setRandomFacts() {
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

    public void setRandomFact() {
        if (seasonsLoaded && finesLoaded && playersLoaded && matchesLoaded) {
            setRandomFacts();
            Random rand = new Random();
            int index = rand.nextInt(randomFacts.size());
            randomFact.setValue("Náhodná zajímavost: " + randomFacts.get(index));
        }
        else {
            randomFact.setValue("načítám...");
        }
        Log.d(TAG, "setRandomFact: " + getRandomFact().getValue());
    }

    public void setPlayerBirthday() {
        if (playersLoaded && fact != null) {
            playerBirthday.setValue(fact.getPlayerWithEarliestBirthDay());
        }
        else {
                playerBirthday.setValue("načítám...");
            }
    }


    public LiveData<String> getRandomFact() {
        return randomFact;
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
        switch (flag) {
            case PLAYER:
                List<Player> playerList = new ArrayList<>();
                for (Model model : models) {
                    if (model instanceof Player) {
                        playerList.add((Player) model);
                    }
                }
                players.setValue(playerList);
                Log.d(TAG, "itemListLoaded: hraci "+ getPlayers().getValue().size());
                playersLoaded = true;
                break;
            case MATCH:
                List<Match> matchList = new ArrayList<>();
                for (Model model : models) {
                    if (model instanceof Match) {
                        matchList.add((Match) model);
                    }
                }
                matches.setValue(matchList);
                Log.d(TAG, "itemListLoaded: zapasy "+ getMatches().getValue().size());
                matchesLoaded = true;
                break;
            case SEASON:
                List<Season> seasonList = new ArrayList<>();
                for (Model model : models) {
                    if (model instanceof Season) {
                        seasonList.add((Season) model);
                    }
                }
                Season season = new Season("Ostatní", 999999999, 999999999); //TODO tohle v seasons view modelu je to hardcodovaný, msuí se vyřešit jinak
                seasonList.add(season);
                seasons.setValue(seasonList);
                Log.d(TAG, "itemListLoaded: sesonz "+ getSeasons().getValue().size());
                seasonsLoaded = true;
                break;
            case FINE:
                List<Fine> fineList = new ArrayList<>();
                for (Model model : models) {
                    if (model instanceof Fine) {
                        fineList.add((Fine) model);
                    }
                }
                fines.setValue(fineList);
                finesLoaded = true;
                break;
        }
        if (firstFact && seasonsLoaded && finesLoaded && playersLoaded && matchesLoaded) {
            firstFact = false;
            setRandomFact();
            setPlayerBirthday();
        }
    }

    @Override
    public void alertSent(String message) {

    }
}
