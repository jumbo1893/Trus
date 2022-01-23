package com.jumbo.trus.player;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;


public class PlayerEditFragment extends PlayerHelperFragment implements IFragment {

    private static final String TAG = "PlayerEditFragment";

    private Player player;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swFan.setChecked(false);
        btnCommit.setText("Upravit");
        btnDelete.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");
        if (playerViewModel.getPickedPlayerForEdit().getValue() != null) {
            player = playerViewModel.getPickedPlayerForEdit().getValue();
        }
        playerViewModel.getPickedPlayerForEdit().observe(getViewLifecycleOwner(), new Observer<Player>() {
            @Override
            public void onChanged(Player pickedPlayer) {
                if (pickedPlayer != null) {
                    player = pickedPlayer;
                    setTextsToEditPlayer();
                }
                else {
                    Log.d(TAG, "načtení zápasu: nenašel se uložený zápas ve viewmodelu " );
                    btnCommit.setEnabled(false);
                    btnDelete.setEnabled(false);
                }
            }
        });
    }

    private void setTextsToEditPlayer() {
        textName.getEditText().setText(player.getName());
        textCalendar.getEditText().setText(player.returnBirthdayInStringFormat());
        swFan.setChecked(player.isFan());
    }


    @Override
    protected void cancelClicked() {
        Log.d(TAG, "cancelClicked: ");
        displayDeleteConfirmationDialog(player,this, "Smazat hráče", "Opravdu chcete smazat tohoto hráče z databáze?");
    }

    @Override
    protected void onCommitValidationTrue(String name, boolean swFan, String date) {
        Result result = playerViewModel.editPlayerInRepository(name, swFan, date, player);
        hideProgressBar();
        if (result.isTrue()) {
            playerViewModel.alertSent(result.getText());
            openPreviousFragment();
        }
    }

    @Override
    public boolean deleteModel(Model model) {
        showProgressBar();
        Result result = playerViewModel.removePlayerFromRepository((Player) model);
        Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        hideProgressBar();
        return result.isTrue();
    }
}
