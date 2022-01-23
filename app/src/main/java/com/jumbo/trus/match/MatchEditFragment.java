package com.jumbo.trus.match;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.SharedViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.List;


public class MatchEditFragment extends MatchHelperFragment implements IFragment {

    private static final String TAG = "MatchEditFragment";

    private Match match;
    private SharedViewModel sharedViewModel;
    private MatchAllViewModel matchAllViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swHome.setChecked(false);
        btnCommit.setText("Upravit");
        btnDelete.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (sharedViewModel.getPickedMatchForEdit().getValue() != null) {
            match = sharedViewModel.getPickedMatchForEdit().getValue();
        }
        sharedViewModel.getPickedMatchForEdit().observe(getViewLifecycleOwner(), new Observer<Match>() {
            @Override
            public void onChanged(Match pickedMatch) {
                if (pickedMatch != null) {
                    match = pickedMatch;
                    setTextsToEditMatch();
                }
                else {
                    Log.d(TAG, "načtení zápasu: nenašel se uložený zápas ve viewmodelu " );
                    btnCommit.setEnabled(false);
                    btnDelete.setEnabled(false);
                }
            }
        });
    }

    private void setTextsToEditMatch() {
        textOpponent.getEditText().setText(match.getOpponent());
        textCalendar.getEditText().setText(match.returnDateOfMatchInStringFormat());
        swHome.setChecked(match.isHomeMatch());
        selectedPlayers = match.returnPlayerListOnlyWithParticipants();
        setPlayersToTextView(selectedPlayers);
    }


    @Override
    protected void cancelClicked() {
        Log.d(TAG, "cancelClicked: ");
        displayDeleteConfirmationDialog(match,this, "Smazat zápas", "Opravdu chcete smazat tento zápas z databáze?");
    }

    @Override
    protected void onCommitValidationTrue(String opponent, boolean homeMatch, String date, List<Player> playerList) {
        Result result = matchAllViewModel.editMatchInRepository(opponent, homeMatch, date, null, playerList, null, match);
        matchAllViewModel.alertSent(result.getText());
        if (result.isTrue()) {
            openPreviousFragment();
        }
    }

    @Override
    public boolean deleteModel(Model model) {
        Result result = matchAllViewModel.removeMatchFromRepository((Match) model);
        Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        return result.isTrue();
    }
}
