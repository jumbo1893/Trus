package com.jumbo.trus.statistics;

import android.annotation.SuppressLint;
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
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.FinesStatsPlayerRecycleViewAdapter;
import com.jumbo.trus.adapters.SimpleRecycleViewAdapter;
import com.jumbo.trus.adapters.SimpleStringRecycleViewAdapter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;

import java.util.ArrayList;
import java.util.List;

public class BeerStatisticsDialog extends Dialog implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "BeerStatisticsDialog";

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
    private SimpleStringRecycleViewAdapter adapter;

    private List<Match> selectedMatches;
    private List<String> adapterTexts;

    private List<String> seasonsNames = new ArrayList<>();
    private List<String> playerSpinnerOptions = new ArrayList<>();

    private int spinnerPosition;
    private int firstSpinnerPosition;


    public BeerStatisticsDialog(Flag flag, Model model, int spinnerPosition) {
        super(flag, model);
        this.firstSpinnerPosition = spinnerPosition;
    }

    public BeerStatisticsDialog(Flag flag, Model model) {
        super(flag, model);
        spinnerPosition = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fine_statistics, container, false);
        tv_title = view.findViewById(R.id.tv_title);
        sp_select_player_season = view.findViewById(R.id.sp_select_player_season);
        tv_overall = view.findViewById(R.id.tv_overall);
        tv_overall.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv_overall.setTextSize(16);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        btn_commit = view.findViewById(R.id.btn_commit);
        sp_select_player_season.setOnItemSelectedListener(this);
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.init();
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        decideTextsToShow();
        btn_commit.setOnClickListener(this);

        return view;
    }

    private void useSeasonsFilter(List<Match> matches) {
        Log.d(TAG, "useSeasonsFilter: " + matches);
        Log.d(TAG, "useSeasonsFilter: " + spinnerPosition);
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
    private void initSpinnerPlayers() {
        seasonArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, playerSpinnerOptions);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initRecycleView() {
        Log.d(TAG, "initRecycleView: ");
        adapter = new SimpleStringRecycleViewAdapter(adapterTexts, getActivity());
    }

    private void setAdapter() {
        rc_list.setAdapter(adapter);
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void addPlayerSpinnerOptions() {
        playerSpinnerOptions.clear();
        playerSpinnerOptions.add("Zobraz vše");
        playerSpinnerOptions.add("Hráči");
        playerSpinnerOptions.add("Fanoušci");
    }

    private void addSeasonSpinnerOptions() {
        seasonsNames.clear();
        seasonsNames.add("Všechny sezony");
        for (Season season : seasonsViewModel.getSeasons().getValue()) {
            seasonsNames.add(season.getName());
        }
    }

    @SuppressLint("SetTextI18n")
    private void addPlayerText() {
        List<Match> matchesWithPlayer = statisticsViewModel.findAllMatchesWithPlayerParticipant(selectedMatches, (Player) model);
        adapterTexts = new ArrayList<>();
        int allBeers = 0;
        int allLiquors = 0;
        int allMatches = 0;
        for (Match match : matchesWithPlayer) {
            StringBuilder text = new StringBuilder();
            if (match.isHomeMatch()) {
                text.append("Domácí ");
            }
            else {
                text.append("Venkovní ");
            }
            int beerNumber = match.returnPlayerListOnlyWithParticipants().get(match.returnPlayerListOnlyWithParticipants().indexOf(model)).getNumberOfBeers();
            int liquorNumber = match.returnPlayerListOnlyWithParticipants().get(match.returnPlayerListOnlyWithParticipants().indexOf(model)).getNumberOfLiquors();
            text.append("zápas proti " + match.getOpponent() + ", počet piv: " + beerNumber + ", počet panáků " + liquorNumber + ", dohromady: " + (beerNumber+liquorNumber));
            adapterTexts.add(text.toString());
            allBeers += match.returnPlayerListOnlyWithParticipants().get(match.returnPlayerListOnlyWithParticipants().indexOf(model)).getNumberOfBeers();
            allLiquors += match.returnPlayerListOnlyWithParticipants().get(match.returnPlayerListOnlyWithParticipants().indexOf(model)).getNumberOfLiquors();
            allMatches++;
        }
        tv_overall.setText("Celkový počet piv: " + allBeers + ", panáků: " + allLiquors + ", dohromady: " + (allBeers+allLiquors) + "\n" +
                "Průmerný počet piv: " + ((float)allBeers/allMatches) + ", panáků: " + ((float)allLiquors/allMatches)  + ", dohromady: " + (((float)allBeers+(float)allLiquors)/allMatches) + "\n");
        initRecycleView();
        setAdapter();
    }

    @SuppressLint("SetTextI18n")
    private void addMatchText() {
        int allBeers = 0;
        int allLiquors = 0;
        adapterTexts = new ArrayList<>();
        for (Player player : ((Match)model).returnPlayerListOnlyWithParticipants()) {
            String text = "";
            int beerNumber;
            int liquorNumber;
            if (spinnerPosition == 0) {
                beerNumber = player.getNumberOfBeers();
                liquorNumber = player.getNumberOfLiquors();
                text += player.getName() + " v zápase vypil " + beerNumber + "piv, " + liquorNumber + " panáků, tedy " + (beerNumber+liquorNumber) + " jednotek chlastu";
                allBeers += player.getNumberOfBeers();
                allLiquors += player.getNumberOfLiquors();
                adapterTexts.add(text);
            }
            else if (spinnerPosition == 1) {
                if (!player.isFan()) {
                    beerNumber = player.getNumberOfBeers();
                    liquorNumber = player.getNumberOfLiquors();
                    text += player.getName() + " v zápase vypil " + beerNumber + "piv, " + liquorNumber + " panáků, tedy " + (beerNumber+liquorNumber) + " jednotek chlastu";
                    allBeers += player.getNumberOfBeers();
                    allLiquors += player.getNumberOfLiquors();
                    adapterTexts.add(text);
                }
            }
            else {
                if (player.isFan()) {
                    beerNumber = player.getNumberOfBeers();
                    liquorNumber = player.getNumberOfLiquors();
                    text += player.getName() + " v zápase vypil " + beerNumber + "piv, " + liquorNumber + " panáků, tedy " + (beerNumber+liquorNumber) + " jednotek chlastu";
                    allBeers += player.getNumberOfBeers();
                    allLiquors += player.getNumberOfLiquors();
                    adapterTexts.add(text);
                }
            }
        }
        tv_overall.setText("Celkový počet piv: " + allBeers + ", panáků: " + allLiquors + ", dohromady: " + (allBeers+allLiquors));
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
        addSeasonSpinnerOptions();
        useSeasonsFilter(matchViewModel.getMatches().getValue());
        initSpinnerSeasons();
        setSpinnerAdapter();
        addPlayerText();
        initRecycleView();
        setAdapter();
    }

    private void setLayoutToMatches() {
        tv_title.setText(model.getName());
        addPlayerSpinnerOptions();
        initSpinnerPlayers();
        setSpinnerAdapter();
        addMatchText();
        initRecycleView();
        setAdapter();
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
        switch (flag) {
            case PLAYER:
                useSeasonsFilter(matchViewModel.getMatches().getValue());
                addPlayerText();
                break;
            case MATCH:
                addMatchText();
                break;
        }
        initRecycleView();
        setAdapter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        userSelect = true;
        return false;
    }*/
}
