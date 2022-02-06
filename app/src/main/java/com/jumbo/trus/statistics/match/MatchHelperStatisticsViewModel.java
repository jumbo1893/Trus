package com.jumbo.trus.statistics.match;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Date;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchHelperStatisticsViewModel extends BaseViewModel {

    private static final String TAG = "MatchHelperStatisticsViewModel";

    protected List<Match> matches = new ArrayList<>();
    protected MutableLiveData<Season> season = new MutableLiveData<>();
    protected MutableLiveData<List<Season>> seasons = new MutableLiveData<>();
    protected String keyword;
    private boolean orderByDate = false;

    protected List<Match> filterMatches(List<Match> matchList, boolean beer) {
        List<Match> selectedMatches = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            selectedMatches.addAll(matchList);
        } else {
            for (Match match : matchList) {
                if (match.getOpponent().toLowerCase().contains(keyword.trim().toLowerCase()) || match.returnDateOfMatchInStringFormat().toLowerCase().contains(keyword.trim().toLowerCase())) {
                    selectedMatches.add(match);
                }
            }
        }
        if (orderByDate) {
            Collections.sort(selectedMatches, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
        }
        else if (beer) {
            Collections.sort(selectedMatches, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.returnNumberOfBeersAndLiquorsInMatch(), o1.returnNumberOfBeersAndLiquorsInMatch());
                }
            });
        }
        else {
            Collections.sort(selectedMatches, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.returnAmountOfFinesInMatch(), o1.returnAmountOfFinesInMatch());
                }
            });
        }
        return filterMatchesBySeason(selectedMatches);
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
        orderByDate = !orderByDate;
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    public LiveData<Season> getSeason() {
        return season;
    }
}
