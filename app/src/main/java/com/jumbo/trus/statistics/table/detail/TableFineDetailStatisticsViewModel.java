package com.jumbo.trus.statistics.table.detail;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.match.MatchHelperStatisticsViewModel;
import com.jumbo.trus.web.GoogleSheetRequestSender;
import com.jumbo.trus.web.IRequestListener;
import com.jumbo.trus.web.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TableFineDetailStatisticsViewModel extends MatchHelperStatisticsViewModel implements ChangeListener, IRequestListener {

    private static final String TAG = "TableFineDetailStatisticsViewModel";

    private MutableLiveData<List<List<String>>> rowLists;
    private MutableLiveData<String> clipboardText = new MutableLiveData<>();
    private List<Player> allPlayers = new ArrayList<>();
    private List<Fine> allFines = new ArrayList<>();
    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this, true, true);
        changeOrderBy();
        if (rowLists == null) {
            rowLists = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
        firebaseRepository.loadMatchesFromRepository();
        firebaseRepository.loadSeasonsFromRepository();
        firebaseRepository.loadFinesFromRepository();
    }

    public void removeReg() {
        firebaseRepository.removeListener();
    }


    public void setSeason (Season season) {
        this.season.setValue(season);
        startMakingTexts(matches, allPlayers, allFines);
    }

    private void makeTextsForFineDetail(List<Match> selectedMatches, List<Player> selectedPlayers, List<Fine> fineList) {
        //prvn?? ????dek
        List<List<String>> rowList = new ArrayList<>();
        List<String> row0 = new ArrayList<>();
        row0.add(" Hr???? ");
        //hr????i v prvn??m ????dku
        for (Fine fine : fineList) {
            row0.add(" " + fine.getName() + " ");
        }
        //posledn?? sloupec prvn??ho ????dku
        row0.add(" Celkem ");
        rowList.add(row0);

        //dal???? ????dky podle hr??????
        for (int i = 0; i < selectedPlayers.size(); i++) {
            Player player = selectedPlayers.get(i);
            if (!player.isFan()) {
                List<String> row = new ArrayList<>();
                row.add(" " + player.getName() + " ");
                int matchFines = 0;
                for (int j = 0; j < fineList.size(); j++) {
                    Fine fine = fineList.get(j);
                    int finesNumber = player.returnFineNumber(selectedMatches, fine);
                    matchFines += finesNumber;
                    row.add(" " + finesNumber + " K?? ");
                }
                row.add(" " + matchFines + " K?? ");
                rowList.add(row);
            }
        }
        //posledn?? ????dek
        List<String> rowLast = new ArrayList<>();
        rowLast.add(" Celkem ");
        for (Fine fine : fineList) {
            rowLast.add(" " + fine.returnAmountOfFineInMatches(selectedMatches) + " K??");
        }
        rowList.add(rowLast);

        this.rowLists.setValue(rowList);
    }

    private void startMakingTexts(List<Match> matchList, List<Player> playerList, List<Fine> fineList) {
        if (matchList != null && playerList != null && fineList != null) {
            makeTextsForFineDetail(filterMatches(matchList, false), playerList, fineList);
        }
    }

    public void sendToGoogle(String action, Context context) throws JSONException {
        isUpdating.setValue(true);
        GoogleSheetRequestSender sender = new GoogleSheetRequestSender(this, context);
        JsonParser jsonParser = new JsonParser();
        Date date = new Date();
        String footer = "Exportov??no z Trus?? appky " + date.convertMillisToStringTimestamp(System.currentTimeMillis());
        sender.sendRequest(jsonParser.convertStatsToJsonObject(action, rowLists.getValue(), footer));
    }

    public LiveData<List<List<String>>> getRowlists() {
        return rowLists;
    }

    public LiveData<String> getClipBoardText() {
        return clipboardText;
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
    public void itemListLoaded(List models, Flag flag) {
        List list = new ArrayList(models);
        Log.d(TAG, "itemListLoaded: zapasy: " + models);
        if (flag.equals(Flag.MATCH)) {
            matches = list;
            startMakingTexts(list, allPlayers, allFines);
        } else if (flag.equals(Flag.PLAYER)) {
            allPlayers = list;
            startMakingTexts(matches, list, allFines);
        } else if (flag.equals(Flag.FINE)) {
            allFines = list;
            startMakingTexts(matches, allPlayers, list);
        } else if (flag.equals(Flag.SEASON)) {
            if (season.getValue() == null) {
                setSeason(returnCurrentSeason(list));
            }
            seasons.setValue(list);
        }
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

    @Override
    public void onResponse(JSONObject response) {
        isUpdating.setValue(false);
        Log.d(TAG, "onResponse: " + response);
        try {
            JsonParser jsonParser = new JsonParser(response);
            clipboardText.setValue(jsonParser.getURL());
           alert.setValue("Do tabulky " + jsonParser.getSheetName() + " p??id??no " + jsonParser.getRowsNumber() +
                    " ????dk??. Odkaz na tabulku byl ulo??en do clipboard pam??ti(ctrl c).");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(String error) {
        isUpdating.setValue(false);
        Log.e(TAG, "onErrorResponse: " + error);
        alert.setValue(error + ". Zkus to pozd??ji");
    }
}
