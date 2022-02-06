package com.jumbo.trus.season.edit;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.Model;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonHelperFragment;


public class SeasonEditFragment extends SeasonHelperFragment implements IFragment {

    private static final String TAG = "SeasonEditFragment";
    private SeasonsEditViewModel seasonsEditViewModel;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCommit.setText("Upravit");
        btnDelete.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");
        seasonsEditViewModel = new ViewModelProvider(requireActivity()).get(SeasonsEditViewModel.class);
        seasonsEditViewModel.init();
        seasonsEditViewModel.setPickedSeason(sharedViewModel.getPickedSeasonForEdit().getValue());
        seasonsEditViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        seasonsEditViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        seasonsEditViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
        seasonsEditViewModel.getSeason().observe(getViewLifecycleOwner(), new Observer<Season>() {
            @Override
            public void onChanged(Season season) {
                setTextsToEditSeason(season);
            }
        });
    }

    private void setTextsToEditSeason(Season season) {
        textName.getEditText().setText(season.getName());
        textCalendarBeginning.getEditText().setText(season.returnSeasonStartInStringFormat());
        textCalendarEnding.getEditText().setText(season.returnSeasonEndInStringFormat());
    }


    @Override
    protected void cancelClicked() {
        Log.d(TAG, "cancelClicked: ");
        displayDeleteConfirmationDialog(seasonsEditViewModel.getSeason().getValue(),this, "Smazat sezonu", "Opravdu chcete smazat tuto sezonu z databáze?");
    }

    @Override
    protected void commitClicked() {
        String name = textName.getEditText().getText().toString();
        String dateBeg = textCalendarBeginning.getEditText().getText().toString();
        String dateEnd = textCalendarEnding.getEditText().getText().toString();
        if (checkFieldsValidation(name, dateBeg, dateEnd, seasonsEditViewModel.getSeasons().getValue(), seasonsEditViewModel.getSeason().getValue())) {
            seasonsEditViewModel.editSeasonInRepository(name, dateBeg, dateEnd, user);
        }
    }

    @Override
    public boolean deleteModel(Model model) {
        seasonsEditViewModel.removeSeasonFromRepository(user);
        return true;
    }
}
