package com.jumbo.trus.match;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomAddFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.adapters.array.SeasonArrayAdapter;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;
import com.jumbo.trus.validator.DateTextWatcher;
import com.jumbo.trus.validator.EmptyTextWatcher;
import com.jumbo.trus.validator.NameTextWatcher;
import com.jumbo.trus.validator.TextFieldValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchHelperFragment extends CustomAddFragment {

    private static final String TAG = "MatchHelperFragment";

    protected LinearLayout info_toolbar;
    protected TextInputLayout textCalendar, textOpponent, textSeason, textPlayers;
    protected AutoCompleteTextView tvSeason, tvPlayers, tvCalendar;
    private ProgressBar progress_bar;
    protected AppCompatButton btnCommit, btnDelete;
    protected Switch swHome;

    protected List<Player> selectedPlayers;
    protected List<Player> players = new ArrayList<>();

    protected SeasonArrayAdapter seasonArrayAdapter;

    private TextFieldValidator nameValidator;
    private TextFieldValidator dateValidator;
    private TextFieldValidator listValidator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_plus, container, false);
        progress_bar = view.findViewById(R.id.progress_bar);
        info_toolbar = view.findViewById(R.id.info_toolbar);
        info_toolbar.setVisibility(View.GONE);
        btnCommit = view.findViewById(R.id.btnCommit);
        btnDelete = view.findViewById(R.id.btnDelete);
        swHome = view.findViewById(R.id.swHome);
        textOpponent = view.findViewById(R.id.textOpponent);
        textCalendar = view.findViewById(R.id.textCalendar);
        tvCalendar = view.findViewById(R.id.tvCalendar);
        textSeason = view.findViewById(R.id.textSeason);
        textPlayers = view.findViewById(R.id.textPlayers);
        tvSeason = view.findViewById(R.id.tvSeason);
        tvPlayers = view.findViewById(R.id.tvPlayers);
        nameValidator = new TextFieldValidator(textOpponent);
        dateValidator = new TextFieldValidator(textCalendar);
        listValidator = new TextFieldValidator(textPlayers);
        Objects.requireNonNull(textOpponent.getEditText()).addTextChangedListener(new NameTextWatcher(nameValidator));
        Objects.requireNonNull(textCalendar.getEditText()).addTextChangedListener(new DateTextWatcher(dateValidator));
        Objects.requireNonNull(textPlayers.getEditText()).addTextChangedListener(new EmptyTextWatcher(listValidator));
        btnCommit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        nullValidationsErrors(textOpponent, textPlayers, textCalendar);
        setEditTextBold(textOpponent.getEditText(), textCalendar.getEditText(), textPlayers.getEditText(), textSeason.getEditText());
        textCalendar.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCalendarDialog(textCalendar.getEditText());
            }
        });
        setTodaysDate(textCalendar.getEditText());
        tvCalendar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayCalendarDialog(textCalendar.getEditText());
                }
            }
        });
        textSeason.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSeason.showDropDown();
            }
        });
        textPlayers.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPlayersDialog(selectedPlayers);
            }
        });
        tvPlayers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayPlayersDialog(selectedPlayers);
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        nullValidationsErrors(textPlayers, textCalendar, textOpponent);
        super.onStart();
    }

    protected void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    protected void setupSeasonDropDownMenu(List<Season> seasonList) {

        seasonArrayAdapter = new SeasonArrayAdapter(getActivity(), seasonList);
        tvSeason.setText(seasonList.get(0).getName());
        tvSeason.setAdapter(seasonArrayAdapter);
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
                .setMultiChoiceItems(getPlayerNames(players), getCheckedPlayers(currentPlayers), new DialogInterface.OnMultiChoiceClickListener() {
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
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        setPlayersToTextView(selectedPlayers);
                    }
                })
                .create().show();
    }

    protected void setPlayersToTextView(List<Player> selectedPlayers) {
        tvPlayers.setText(returnPlayerNamesForTextView(selectedPlayers));
    }

    private String[] getPlayerNames(List<Player> players) {
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

    private String returnPlayerNamesForTextView(List<Player> selectedPlayers) {
        String[] players = getPlayerNames(selectedPlayers);
        StringBuilder names = new StringBuilder();
        int size = players.length;
        for (int i = 0; i < size; i++) {
            names.append(players[i]);
            if (i < size-1) {
                names.append(", ");
            }
        }
        return names.toString();
    }

    private boolean checkFieldsValidation(String name, String date, List<Player> playerList) {
        boolean nameCheck = nameValidator.checkNameField(name);
        boolean dateCheck = dateValidator.checkDateField(date);
        boolean playerCheck = listValidator.checkListField(playerList);
        return nameCheck && dateCheck && playerCheck;
    }

    protected void onCommitValidationTrue(final String opponent, final boolean homeMatch, final String date,
                                          final List<Player> playerList) {

    }


    @Override
    protected void commitClicked() {
        String name = textOpponent.getEditText().getText().toString();
        String date = textCalendar.getEditText().getText().toString();
        if (checkFieldsValidation(name, date, selectedPlayers)) {
            onCommitValidationTrue(name, swHome.isChecked(), date, selectedPlayers);
        }
    }
}
