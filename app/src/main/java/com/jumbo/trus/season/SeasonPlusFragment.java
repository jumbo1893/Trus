package com.jumbo.trus.season;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jumbo.trus.Result;
import com.jumbo.trus.player.PlayerHelperFragment;


public class SeasonPlusFragment extends SeasonHelperFragment {

    private static final String TAG = "SeasonPlusFragment";



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCommit.setText("Potvrdit");
        btnDelete.setVisibility(View.GONE);


    }

    @Override
    protected void onCommitValidationTrue(String name, String dateBegin, String dateEnd) {
        Result result = seasonsViewModel.addSeasonToRepository(name, dateBegin, dateEnd);
        hideProgressBar();
        if (result.isTrue()) {
            seasonsViewModel.alertSent(result.getText());
            openPreviousFragment();
        }
    }
}
