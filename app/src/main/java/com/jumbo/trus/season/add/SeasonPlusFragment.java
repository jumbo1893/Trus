package com.jumbo.trus.season.add;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.Result;
import com.jumbo.trus.player.PlayerHelperFragment;
import com.jumbo.trus.season.SeasonHelperFragment;


public class SeasonPlusFragment extends SeasonHelperFragment {

    private static final String TAG = "SeasonPlusFragment";
    private SeasonsPlusViewModel seasonsPlusViewModel;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCommit.setText("Potvrdit");
        btnDelete.setVisibility(View.GONE);
        seasonsPlusViewModel = new ViewModelProvider(requireActivity()).get(SeasonsPlusViewModel.class);
        seasonsPlusViewModel.init();
        seasonsPlusViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        seasonsPlusViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        seasonsPlusViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });

    }

    @Override
    protected void commitClicked() {
        String name = textName.getEditText().getText().toString();
        String dateBeg = textCalendarBeginning.getEditText().getText().toString();
        String dateEnd = textCalendarEnding.getEditText().getText().toString();
        if (checkFieldsValidation(name, dateBeg, dateEnd, seasonsPlusViewModel.getSeasons().getValue(), null)) {
            seasonsPlusViewModel.addSeasonToRepository(name, dateBeg, dateEnd, user);
        }
    }
}
