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

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;

import java.util.ArrayList;
import java.util.List;

public class BeerStatisticsDialog extends Dialog implements AdapterView.OnItemSelectedListener/*, View.OnTouchListener*/ {

    private static final String TAG = "BeerStatisticsDialog";

    //widgety
    private TextView tv_title, tv_list;
    private Spinner sp_select_player_season;
    private Button btn_commit;

    private PlayerViewModel playerViewModel;
    private MatchViewModel matchViewModel;
    private SeasonsViewModel seasonsViewModel;
    private StatisticsViewModel statisticsViewModel;

    private ArrayAdapter<String> seasonArrayAdapter;

    private List<Match> selectedMatches;

    private List<String> seasonsNames = new ArrayList<>();
    private List<String> playerSpinnerOptions = new ArrayList<>();

    private int spinnerPosition = 0;
    private boolean userSelect = false;


    public BeerStatisticsDialog(Flag flag, Model model) {
        super(flag, model);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_beer_statistics, container, false);
        tv_title = view.findViewById(R.id.tv_title);
        sp_select_player_season = view.findViewById(R.id.sp_select_player_season);
        tv_list = view.findViewById(R.id.tv_list);
        btn_commit = view.findViewById(R.id.btn_commit);
        sp_select_player_season.setOnItemSelectedListener(this);
        //sp_select_player_season.setOnTouchListener(this);
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.init();
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        decideTextsToShow();

        btn_commit.setOnClickListener(this);

        /*seasonsViewModel.getSeasons().observe(this, new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasons) {
                Log.d(TAG, "onChanged: nacetli se sezony " + seasons);
                if (seasonArrayAdapter == null) {
                    initSpinnerSeasons();
                }
                seasonsNames.clear();
                seasonsNames.add("Všechny sezony");
                for (Season season : seasons) {
                    seasonsNames.add(season.getName());
                }
                setSpinnerAdapter();
                seasonArrayAdapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });*/

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
    private void initSpinnerPlayers() {
        seasonArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, playerSpinnerOptions);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private void addPlayerText() {
        List<Match> matchesWithPlayer = statisticsViewModel.findAllMatchesWithPlayerParticipant(selectedMatches, (Player) model);
        String text = "";
        int allBeers = 0;
        for (Match match : matchesWithPlayer) {
            if (match.isHomeMatch()) {
                text += "Domácí ";
            }
            else {
                text += "Venkovní ";
            }
            text += "zápas proti " + match.getOpponent() + ", počet piv: " + match.getPlayerListOnlyWithParticipants().get(match.getPlayerListOnlyWithParticipants().indexOf(model)).getNumberOfBeers() + "\n\n";
            allBeers += match.getPlayerListOnlyWithParticipants().get(match.getPlayerListOnlyWithParticipants().indexOf(model)).getNumberOfBeers();
        }
        tv_list.setText("Celkový počet piv: " + allBeers + "\n\n" + text);
    }

    private void addMatchText() {
        String text = "";
        int allBeers = 0;
        for (Player player : ((Match)model).getPlayerListOnlyWithParticipants()) {
            if (spinnerPosition == 0) {
                text += player.getName() + " v zápase vypil " + player.getNumberOfBeers() + "\n\n";
                allBeers += player.getNumberOfBeers();
            }
            else if (spinnerPosition == 1) {
                if (!player.isFan()) {
                    text += player.getName() + " v zápase vypil " + player.getNumberOfBeers() + "\n\n";
                    allBeers += player.getNumberOfBeers();
                }
            }
            else {
                if (player.isFan()) {
                    text += player.getName() + " v zápase vypil " + player.getNumberOfBeers() + "\n\n";
                    allBeers += player.getNumberOfBeers();
                }
            }
        }
        tv_list.setText("Celkový počet piv: " + allBeers + "\n\n" + text);
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
    }

    private void setLayoutToMatches() {
        tv_title.setText(model.getName());
        addPlayerSpinnerOptions();
        initSpinnerPlayers();
        setSpinnerAdapter();
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
        spinnerPosition = position;
        switch (flag) {
            case PLAYER:
                useSeasonsFilter(matchViewModel.getMatches().getValue());
                addPlayerText();
                break;
            case MATCH:
                addMatchText();
                break;
        }
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
