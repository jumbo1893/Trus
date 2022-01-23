package com.jumbo.trus.season;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;


public class SeasonEditFragment extends SeasonHelperFragment implements IFragment {

    private static final String TAG = "SeasonEditFragment";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCommit.setText("Upravit");
        btnDelete.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");
        if (seasonsViewModel.getPickedSeasonForEdit().getValue() != null) {
            season = seasonsViewModel.getPickedSeasonForEdit().getValue();
        }
        seasonsViewModel.getPickedSeasonForEdit().observe(getViewLifecycleOwner(), new Observer<Season>() {
            @Override
            public void onChanged(Season pickedSeason) {
                if (pickedSeason != null) {
                    season = pickedSeason;
                    setTextsToEditSeason();
                }
                else {
                    Log.d(TAG, "načtení zápasu: nenašel se uložený zápas ve viewmodelu " );
                    btnCommit.setEnabled(false);
                    btnDelete.setEnabled(false);
                }
            }
        });
    }

    private void setTextsToEditSeason() {
        textName.getEditText().setText(season.getName());
        textCalendarBeginning.getEditText().setText(season.returnSeasonStartInStringFormat());
        textCalendarEnding.getEditText().setText(season.returnSeasonEndInStringFormat());
    }


    @Override
    protected void cancelClicked() {
        Log.d(TAG, "cancelClicked: ");
        displayDeleteConfirmationDialog(season,this, "Smazat sezonu", "Opravdu chcete smazat tuto sezonu z databáze?");
    }

    @Override
    protected void onCommitValidationTrue(String name, String dateBegin, String dateEnd) {
        Result result = seasonsViewModel.editSeasonInRepository(name, dateBegin, dateEnd, season);
        hideProgressBar();
        if (result.isTrue()) {
            seasonsViewModel.alertSent(result.getText());
            openPreviousFragment();
        }
    }

    @Override
    public boolean deleteModel(Model model) {
        Result result = seasonsViewModel.removeSeasonFromRepository(season);
        if (result.isTrue()) {
            seasonsViewModel.alertSent(result.getText());
            openPreviousFragment();
        }
        return result.isTrue();
    }
}
