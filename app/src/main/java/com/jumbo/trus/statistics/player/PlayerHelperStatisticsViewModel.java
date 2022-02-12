package com.jumbo.trus.statistics.player;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Date;
import com.jumbo.trus.comparator.OrderByBeerAndLiquorNumber;
import com.jumbo.trus.comparator.OrderByFineAmount;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerHelperStatisticsViewModel extends BaseViewModel {

    private static final String TAG = "PlayerHelperStatisticsViewModel";

    protected List<Match> matches = new ArrayList<>();
    protected MutableLiveData<Season> season = new MutableLiveData<>();
    protected MutableLiveData<List<Season>> seasons = new MutableLiveData<>();
    protected String keyword;
    private boolean orderByName = false;

    protected List<Player> filterPlayers(List<Player> playerList, boolean beer) {
        List<Player> players = new ArrayList<>();
        if (beer) {
            players = playerList;
        }
        else {
            for (Player player : playerList) {
                if (!player.isFan()) {
                    players.add(player);
                }
            }
        }
        List<Player> selectedPlayers = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return enhancePlayers(filterMatchesBySeason(matches), players, beer);
        } else {
            for (Player player : players) {
                if (player.getName().toLowerCase().contains(keyword.trim().toLowerCase())) {
                    selectedPlayers.add(player);
                }
            }
            return enhancePlayers(filterMatchesBySeason(matches), selectedPlayers, beer);
        }
    }

    private List<Player> enhancePlayers(List<Match> matches, List<Player> selectedPlayers, boolean beer) {
        if (matches != null || selectedPlayers != null) {
            Log.d(TAG, "enhancePlayers: " + matches.size() + selectedPlayers.size());
            for (Player player : selectedPlayers) {
                if (beer) {
                    player.calculateAllBeersNumber(matches);
                    player.calculateAllLiquorsNumber(matches);
                }
                else {
                    player.calculateAllFinesNumber(matches);
                }

            }
            if (orderByName) {
                Collections.sort(selectedPlayers, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }
            else if (beer) {
                Collections.sort(selectedPlayers, new OrderByBeerAndLiquorNumber(true));
            }
            else {
                Collections.sort(selectedPlayers, new OrderByFineAmount(true));
            }
        }
        return selectedPlayers;
    }

    protected Season returnCurrentSeason(List<Season> seasonList) {
        Date date = new Date();
        for (Season season : seasonList) {
            if (season.getSeasonStart() <= date.getCurrentDateInMillis() && season.getSeasonEnd() >= date.getCurrentDateInMillis()) {
                return season;
            }
        }
        return new Season().otherSeason();
    }

    private List<Match> filterMatchesBySeason(List<Match> matches) {
        Log.d(TAG, "filterMatchesBySeason: " + matches.size());
        List<Match> filteredMatches = new ArrayList<>();
        if (season.getValue() == null || season.getValue().equals(new Season().allSeason())) {
            return matches;
        } else {
            for (Match match : matches) {
                if (match.getSeason().equals(season.getValue())) {
                    filteredMatches.add(match);
                }
            }
            return filteredMatches;
        }
    }

    public void changeOrderBy() {
        orderByName = !orderByName;
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    public LiveData<Season> getSeason() {
        return season;
    }
}
