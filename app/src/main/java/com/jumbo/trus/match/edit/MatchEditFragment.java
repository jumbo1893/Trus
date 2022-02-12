package com.jumbo.trus.match.edit;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.Model;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchHelperFragment;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.List;


public class MatchEditFragment extends MatchHelperFragment implements IFragment {

    private static final String TAG = "MatchEditFragment";

    private MatchEditViewModel matchEditViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swHome.setChecked(false);
        btnCommit.setText("Upravit");
        btnDelete.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");
        matchEditViewModel = new ViewModelProvider(requireActivity()).get(MatchEditViewModel.class);
        matchEditViewModel.init(sharedViewModel.getPickedMatchForEdit().getValue());
        matchEditViewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasonList) {
                setupSeasonDropDownMenu(seasonList);
            }
        });
        matchEditViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProgressBar();
                }
                else {
                    hideProgressBar();
                }
            }
        });
        matchEditViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        matchEditViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
        matchEditViewModel.getCheckedPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> players) {
                setPlayersToTextView(players);
            }
        });

        matchEditViewModel.getMatch().observe(getViewLifecycleOwner(), new Observer<Match>() {
            @Override
            public void onChanged(Match match) {
                setTextsToEditMatch(match);
            }
        });

        matchEditViewModel.getCheckedSeason().observe(getViewLifecycleOwner(), new Observer<Season>() {
            @Override
            public void onChanged(Season season) {
                tvSeason.setText(season.getName());
                setupSeasonDropDownMenu(matchEditViewModel.getSeasons().getValue());
            }
        });
        matchEditViewModel.getNewMainMatch().observe(getViewLifecycleOwner(), new Observer<Match>() {
            @Override
            public void onChanged(Match match) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    sharedViewModel.setMainMatch(match);
                }
            }
        });
        textPlayers.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPlayersDialog(matchEditViewModel.getCheckedPlayers().getValue(), matchEditViewModel.getPlayers().getValue(), matchEditViewModel);
            }
        });
        tvPlayers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayPlayersDialog(matchEditViewModel.getCheckedPlayers().getValue(), matchEditViewModel.getPlayers().getValue(), matchEditViewModel);

                }
            }
        });
        tvSeason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                matchEditViewModel.setCheckedSeason(seasonArrayAdapter.getItem(i));
            }
        });
    }

    @Override
    public void onDestroyView() {
        matchEditViewModel.removeReg();
        super.onDestroyView();
    }

    private void setTextsToEditMatch(Match match) {
        textOpponent.getEditText().setText(match.getOpponent());
        textCalendar.getEditText().setText(match.returnDateOfMatchInStringFormat());
        swHome.setChecked(match.isHomeMatch());
    }


    @Override
    protected void cancelClicked() {
        Log.d(TAG, "cancelClicked: ");
        displayDeleteConfirmationDialog(matchEditViewModel.getMatch().getValue(),this, "Smazat zápas", "Opravdu chcete smazat tento zápas z databáze?");
    }

    @Override
    protected void onCommitValidationTrue(String opponent, boolean homeMatch, String date) {
        matchEditViewModel.editMatchInRepository(opponent, homeMatch, date, user);
    }

    @Override
    protected void commitClicked() {
        String name = textOpponent.getEditText().getText().toString();
        String date = textCalendar.getEditText().getText().toString();
        if (checkFieldsValidation(name, date, matchEditViewModel.getCheckedPlayers().getValue())) {
            onCommitValidationTrue(name, swHome.isChecked(), date);
        }
    }

    @Override
    public boolean deleteModel(Model model) {
        matchEditViewModel.removeMatchFromRepository(user);
        return true;
    }
}
