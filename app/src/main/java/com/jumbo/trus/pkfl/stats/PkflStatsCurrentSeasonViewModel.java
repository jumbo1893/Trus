package com.jumbo.trus.pkfl.stats;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.comparator.pkfl.OrderByBestPlayerNumber;
import com.jumbo.trus.comparator.pkfl.OrderByBestPlayerRatio;
import com.jumbo.trus.comparator.pkfl.OrderByGoalMatchesRatio;
import com.jumbo.trus.comparator.pkfl.OrderByGoalNumber;
import com.jumbo.trus.comparator.pkfl.OrderByGoalkeepingMinutesNumber;
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

public class PkflStatsCurrentSeasonViewModel extends PkflStatsViewModel implements ItemLoadedListener {

    private static final String TAG = "PkflStatsCurrentSeasonViewModel";

    public void init() {
        Log.d(TAG, "init: ");
        firebaseRepository = new FirebaseRepository(this);
        if (recycleViewList == null) {
            recycleViewList = new MutableLiveData<>();
            firebaseRepository.loadPkflUrlFromRepository();
            isUpdating.setValue(true);
            loadingAlert.setValue("Připojuji se k webu PKFL...");
        }
    }

    @Override
    public void itemLoaded(String value) {
        pkflUrl = value;
        Log.d(TAG, "itemLoaded: " + value);
        if (waitingForLoad) {
            loadSeasonUrlsFromPkfl(true);
            waitingForLoad = false;
        }

    }
}
