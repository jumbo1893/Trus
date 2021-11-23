package com.jumbo.trus.match;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.pkfl.PkflViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.List;

public class MatchDialog extends Dialog implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "MatchDialog";

    //widgety
    private TextView tv_nadpis;
    private EditText et_jmeno, et_datum;
    private Button btn_kalendar, btn_potvrdit, btn_smazat, btn_players, btn_load_pkfl_match;
    private Switch sw_switch;
    private Spinner sp_seasons;

    //vars
    private IMatchFragment iMatchFragment;
    private List<Season> seasons;
    private List<String> seasonsNames = new ArrayList<>();
    private List<Player> players;
    private List<Player> selectedPlayers;
    private PkflMatch pkflMatch = null;
    private ArrayAdapter<String> seasonArrayAdapter;

    private PkflViewModel pkflViewModel;

    public MatchDialog(Flag flag, List<Season> seasons, List<Player> players) {
        super(flag);
        this.seasons = seasons;
        this.players = players;
    }

    public MatchDialog(Flag flag, Model model, List<Season> seasons, List<Player> players) {
        super(flag, model);
        this.seasons = seasons;
        this.players = players;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_match, container, false);
        tv_nadpis = view.findViewById(R.id.tv_nadpis);
        et_jmeno = view.findViewById(R.id.et_jmeno);
        et_datum = view.findViewById(R.id.et_datum);
        btn_kalendar = view.findViewById(R.id.btn_kalendar);
        btn_potvrdit = view.findViewById(R.id.btn_potvrdit);
        btn_smazat = view.findViewById(R.id.btn_smazat);
        btn_players = view.findViewById(R.id.btn_players);
        btn_load_pkfl_match = view.findViewById(R.id.btn_load_pkfl_match);
        sp_seasons = view.findViewById(R.id.sp_seasons);
        sw_switch = view.findViewById(R.id.sw_switch);
        sw_switch.setChecked(false);
        decideTextsToShow();
        convertSeasonsToString(seasons);
        initSpinnerSeasons();
        setDefaultSelectedPlayers();
        btn_players.setOnClickListener(this);
        btn_potvrdit.setOnClickListener(this);
        btn_smazat.setOnClickListener(this);
        btn_kalendar.setOnClickListener(this);
        btn_load_pkfl_match.setOnClickListener(this);

        return view;
    }

    private void decideTextsToShow() {
        switch (flag) {
            case MATCH_PLUS:
                setTextsToAddMatch();
                pkflViewModel = new ViewModelProvider(requireActivity()).get(PkflViewModel.class);
                pkflViewModel.init();
                pkflViewModel.loadMatchesFromPkfl();
                pkflViewModel.getMatch().observe(getViewLifecycleOwner(), new Observer<PkflMatch>() {
                    @Override
                    public void onChanged(PkflMatch match) {
                        pkflMatch = match;
                    }
                });
                break;
            case MATCH_EDIT:
                setTextsToEditMatch();
                break;
        }
    }


    private void setTextsToAddMatch() {
        tv_nadpis.setText("Nový zápas");
        et_jmeno.setText("");
        et_jmeno.setHint("Jméno soupeře");
        btn_load_pkfl_match.setVisibility(View.VISIBLE);
        btn_potvrdit.setText("Přidat zápas");
        btn_smazat.setVisibility(View.GONE);
        sw_switch.setText("domácí?");
    }

    private void setTextsToEditMatch() {
        Match match = (Match)model;
        tv_nadpis.setText("Úprava zápasu");
        et_jmeno.setText(match.getOpponent());
        btn_load_pkfl_match.setVisibility(View.GONE);
        et_datum.setText(match.getDateOfMatchInStringFormat());
        btn_potvrdit.setText("Upravit zápas");
        btn_smazat.setVisibility(View.VISIBLE);
        sw_switch.setText("domácí?");
        sw_switch.setChecked(((Match) model).isHomeMatch());
    }

    private void initSpinnerSeasons() {

        seasonArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, seasonsNames);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_seasons.setAdapter(seasonArrayAdapter);
    }

    private void convertSeasonsToString(List<Season> seasons) {
        seasonsNames.add("Automaticky přiřadit sezonu");
        for (Season season : seasons) {
            seasonsNames.add(season.getName());
        }
    }

    @Override
    public void onClick(View view) {
        switch (flag) {
            case MATCH_PLUS: {
                switch (view.getId()) {
                    case R.id.btn_potvrdit: {
                        Log.d(TAG, "onClick: kliknuti na přidat zápas");
                        String opponent = et_jmeno.getText().toString();
                        String date = et_datum.getText().toString();
                        boolean homeMatch = sw_switch.isChecked();
                        Season season = selectPickedSeason(sp_seasons.getSelectedItemPosition());
                        if (iMatchFragment.createNewMatch(opponent, date, homeMatch, season, selectedPlayers)) {
                            getDialog().dismiss();

                        }
                        break;
                    }
                    case R.id.btn_kalendar: {
                        displayCalendarDialog(et_datum);
                        break;
                    }
                    case R.id.btn_players: {
                        displayPlayersDialog(null);
                        break;
                    }
                    case R.id.btn_load_pkfl_match: {
                        setMatchTextsFromPkfl();
                        break;
                    }

                }
            }
            break;
            case MATCH_EDIT: {
                switch (view.getId()) {
                    case R.id.btn_potvrdit: {
                        String opponent = et_jmeno.getText().toString();
                        String date = et_datum.getText().toString();
                        boolean homeMatch = sw_switch.isChecked();
                        Season season = selectPickedSeason(sp_seasons.getSelectedItemPosition());
                        if (iMatchFragment.editMatch(opponent, date, homeMatch, season, selectedPlayers, (Match) model)) {
                            getDialog().dismiss();
                        }
                        break;
                    }
                    case R.id.btn_smazat: {
                        Log.d(TAG, "onClick: kliknuti na smazat zápas");
                        displayDeleteConfirmationDialog(model, iMatchFragment, "Opravdu chceš smazat zápas?", null);
                        break;
                    }
                    case R.id.btn_kalendar: {
                        displayCalendarDialog(et_datum);
                        break;
                    }
                    case R.id.btn_players: {
                        displayPlayersDialog(((Match)model).returnPlayerListOnlyWithParticipants());
                        break;
                    }
                }
            }
            break;
        }

    }

    private void setMatchTextsFromPkfl() {
        if (pkflMatch == null) {
            pkflViewModel.loadMatchesFromPkfl();
            Toast.makeText(getActivity(), "Z neznámého důvodu se zatím nenačetly zápasy. Zkus to za chvíli", Toast.LENGTH_LONG).show();
        }
        else {
            et_jmeno.setText(pkflMatch.getOpponent());
            sw_switch.setChecked(pkflMatch.isHomeMatch());
            et_datum.setText(pkflMatch.getDateOfMatchInStringFormat());
        }
    }

    private Season selectPickedSeason(int position) {
        if (position == 0) {
            return null;
        }
        return seasons.get(position-1);
    }

    private void setDefaultSelectedPlayers() {
        if (flag ==Flag.MATCH_PLUS) {
            selectedPlayers = new ArrayList<>();
        }
        else {
            selectedPlayers = ((Match) model).returnPlayerListOnlyWithParticipants();
        }
    }

    private void displayPlayersDialog(List<Player> currentPlayers) {
        final List<Player> checkedPlayers;
        if (currentPlayers == null) {
            checkedPlayers = new ArrayList<>();
        }
        else {
            checkedPlayers = currentPlayers;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Vyber účastníky zápasu")
                .setMultiChoiceItems(getPlayerNames(), getCheckedPlayers(currentPlayers), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            checkedPlayers.add(players.get(which));
                        }
                        else {
                            checkedPlayers.remove(players.get(which));
                        }
                    }
                })
                .setPositiveButton("Vybrat hráče", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPlayers = checkedPlayers;
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("zrušit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();


    }

    private String[] getPlayerNames() {
        if (players == null) {
            return new String[0];
        }
        String[] playerStringList = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playerStringList[i] = players.get(i).getName();
        }
        return playerStringList;
    }

    private boolean[] getCheckedPlayers(List<Player> currentPlayers) {
        if (currentPlayers == null || players == null) {
            return null;
        }
        boolean[] checkedPlayersList = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if (currentPlayers.contains(players.get(i))) {
                checkedPlayersList[i] = true;
            }
            else {
                checkedPlayersList[i] = false;
            }
        }
        return checkedPlayersList;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iMatchFragment = (IMatchFragment) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
