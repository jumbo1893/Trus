package com.jumbo.trus.player;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.FadeAnimation;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.match.MatchHelperFragment;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.pkfl.PkflViewModel;
import com.jumbo.trus.season.Season;

import java.util.List;


public class PlayerPlusFragment extends PlayerHelperFragment {

    private static final String TAG = "PlayerPlusFragment";



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swFan.setChecked(false);
        btnCommit.setText("Potvrdit");
        btnDelete.setVisibility(View.GONE);


    }

    @Override
    protected void onCommitValidationTrue(String name, boolean swFan, String date) {
        Result result = playerViewModel.addPlayerToRepository(name, swFan, date);
        hideProgressBar();
        if (result.isTrue()) {
            playerViewModel.alertSent(result.getText());
            openPreviousFragment();
        }
    }
}
