package com.jumbo.trus.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.FinesStatsPlayerRecycleViewAdapter;
import com.jumbo.trus.adapters.SimpleRecycleViewAdapter;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FineStatisticsDialog extends Dialog implements AdapterView.OnItemSelectedListener, OnListListener {

    private static final String TAG = "FineStatisticsDialog";

    //widgety
    private TextView tv_title, tv_overall;
    private Spinner sp_select_player_season;
    private Button btn_commit;
    private RecyclerView rc_list;

    private PlayerViewModel playerViewModel;
    private MatchViewModel matchViewModel;
    private SeasonsViewModel seasonsViewModel;
    private StatisticsViewModel statisticsViewModel;

    private ArrayAdapter<String> seasonArrayAdapter;

    private List<Match> selectedMatches;
    private List<Player> selectedPlayers;
    private List<ReceivedFine> receivedFinesInMatch;

    private FinesStatsPlayerRecycleViewAdapter adapter;
    private SimpleRecycleViewAdapter simpleRecycleViewAdapter;

    private List<String> seasonsNames = new ArrayList<>();

    private int spinnerPosition;
    private int firstSpinnerPosition;

    public FineStatisticsDialog(Flag flag, Model model, int spinnerPosition) {
        super(flag, model);
        this.firstSpinnerPosition = spinnerPosition;
        Log.d(TAG, "FineStatisticsDialog: " + flag + model);
    }

    public FineStatisticsDialog(Flag flag, Model model) {
        super(flag, model);
        spinnerPosition = 0;
        Log.d(TAG, "FineStatisticsDialog: " + flag + model);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fine_statistics, container, false);
        tv_title = view.findViewById(R.id.tv_title);
        sp_select_player_season = view.findViewById(R.id.sp_select_player_season);
        tv_overall = view.findViewById(R.id.tv_overall);
        btn_commit = view.findViewById(R.id.btn_commit);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        sp_select_player_season.setOnItemSelectedListener(this);
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.init();
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        decideTextsToShow();
        tv_overall.setOnClickListener(this);
        btn_commit.setOnClickListener(this);

        return view;
    }

    private void useSeasonsFilter(List<Match> matches) {
        Log.d(TAG, "useSeasonsFilter: " + matches);
        if (spinnerPosition == 0) {
            selectedMatches = matches;
            return;
        }
        selectedMatches = new ArrayList<>();
        Season season = seasonsViewModel.getSeasons().getValue().get(spinnerPosition-1);
        for (Match match : matches) {
            if (match.getSeason().equals(season)) {
                selectedMatches.add(match);
            }
        }
    }

    private void setSpinnerAdapter() {
        sp_select_player_season.setAdapter(seasonArrayAdapter);
    }

    private void initSpinnerSeasons() {
        seasonArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, seasonsNames);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void addSeasonSpinnerOptions() {
        seasonsNames.clear();
        seasonsNames.add("Všechny sezony");
        for (Season season : seasonsViewModel.getSeasons().getValue()) {
            seasonsNames.add(season.getName());
        }
    }

    private void addPlayerText() {
        Log.d(TAG, "addPlayerText: ");
        List<Match> matchesWithPlayer = statisticsViewModel.findAllMatchesWithPlayer(selectedMatches, (Player) model);
        selectedPlayers = new ArrayList<>();
        int allfines[] = {0,0};
        for (Match match : matchesWithPlayer) {
            Player player = match.getPlayerList().get(match.getPlayerList().indexOf(model));
            selectedPlayers.add(player);
            allfines[0] += match.getPlayerList().get(match.getPlayerList().indexOf(model)).returnNumberOfAllReceviedFines();
            allfines[1] += match.getPlayerList().get(match.getPlayerList().indexOf(model)).returnAmountOfAllReceviedFines();
        }
        tv_overall.setText("Celkem hráč dostal " + allfines[0] + " pokut v celkové částce " + allfines[1] + " Kč.");
        initRecycleView();
        setAdapter();
    }

    private void initRecycleView() {
        Log.d(TAG, "initRecycleView: ");
        adapter = new FinesStatsPlayerRecycleViewAdapter(selectedPlayers, selectedMatches, getActivity(), this);
    }

    private void initSimpleRecycleView() {
        Log.d(TAG, "initSimpleRecycleView: ");
        simpleRecycleViewAdapter = new SimpleRecycleViewAdapter(receivedFinesInMatch, getActivity(), this);
    }

    private void setAdapter() {
        rc_list.setAdapter(adapter);
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setSimpleAdapter() {
        rc_list.setAdapter(simpleRecycleViewAdapter);
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void addMatchText() {
        Log.d(TAG, "addMatchText: ");
        receivedFinesInMatch = statisticsViewModel.returnListOfAllFinesInMatch((Match) model);
        initSimpleRecycleView();
        setSimpleAdapter();
        if (receivedFinesInMatch.isEmpty()) {
            tv_overall.setVisibility(View.VISIBLE);
            tv_overall.setText("Pro tento zápas se ještě nerozdaly pokuty");
        }
        else {
            tv_overall.setVisibility(View.GONE);
        }
    }

    private void decideTextsToShow() {
        switch (flag) {
            case PLAYER:
                setLayoutToPlayers();
                break;
            case MATCH:
                setLayoutToMatches();
                break;
        }
    }

    private void setLayoutToPlayers() {
        Log.d(TAG, "setLayoutToPlayers: ");
        tv_title.setText(model.getName());
        sp_select_player_season.setVisibility(View.VISIBLE);
        addSeasonSpinnerOptions();
        useSeasonsFilter(returnMatchesWithPlayer(Objects.requireNonNull(matchViewModel.getMatches().getValue())));
        initSpinnerSeasons();
        setSpinnerAdapter();
        addPlayerText();
    }

    private List<Match> returnMatchesWithPlayer(List<Match> matches) {
        List<Match> returnMatches = new ArrayList<>();
        for (Match match : matches) {
            if (match.isInMatchPlayerWithFine((Player) model)) {
                returnMatches.add(match);
            }
        }
        return returnMatches;
    }

    private void setLayoutToMatches() {
        tv_title.setText(model.getName());
        sp_select_player_season.setVisibility(View.INVISIBLE);
        addMatchText();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                getDialog().dismiss();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (firstSpinnerPosition != 0) {
            spinnerPosition = firstSpinnerPosition;
            firstSpinnerPosition = 0;
        }
        else {
            spinnerPosition = position;
        }
        useSeasonsFilter(returnMatchesWithPlayer(Objects.requireNonNull(matchViewModel.getMatches().getValue())));
        addPlayerText();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: test");
        FineStatisticsDialogDetail fineStatisticsDialogDetail;
        if (flag == Flag.PLAYER) {
            fineStatisticsDialogDetail = new FineStatisticsDialogDetail(flag, selectedPlayers.get(position));
        }
        else {
            fineStatisticsDialogDetail = new FineStatisticsDialogDetail(flag, model, receivedFinesInMatch.get(position));
        }

        fineStatisticsDialogDetail.setTargetFragment(getTargetFragment(), 1);
        fineStatisticsDialogDetail.show(getFragmentManager(), "dialogplus");

    }

    @Override
    public void onItemLongClick(int position) {

    }
}
