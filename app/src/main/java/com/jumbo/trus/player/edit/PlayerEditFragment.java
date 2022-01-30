package com.jumbo.trus.player.edit;


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
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerHelperFragment;


public class PlayerEditFragment extends PlayerHelperFragment implements IFragment {

    private static final String TAG = "PlayerEditFragment";
    private PlayerEditViewModel playerEditViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swFan.setChecked(false);
        btnCommit.setText("Upravit");
        btnDelete.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");
        playerEditViewModel = new ViewModelProvider(requireActivity()).get(PlayerEditViewModel.class);
        playerEditViewModel.init();
        playerEditViewModel.setPickedPlayer(sharedViewModel.getPickedPlayerForEdit().getValue());
        playerEditViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        playerEditViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        playerEditViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
        playerEditViewModel.getPlayer().observe(getViewLifecycleOwner(), new Observer<Player>() {
            @Override
            public void onChanged(Player player) {
                setTextsToEditPlayer(player);
            }
        });

    }

    private void setTextsToEditPlayer(Player player) {
        textName.getEditText().setText(player.getName());
        textCalendar.getEditText().setText(player.returnBirthdayInStringFormat());
        swFan.setChecked(player.isFan());
    }


    @Override
    protected void cancelClicked() {
        Log.d(TAG, "cancelClicked: ");
        displayDeleteConfirmationDialog(playerEditViewModel.getPlayer().getValue(),this, "Smazat hráče", "Opravdu chcete smazat tohoto hráče z databáze?");
    }

    @Override
    protected void onCommitValidationTrue(String name, boolean swFan, String date) {
        playerEditViewModel.editPlayerInRepository(name, swFan, date, user);
    }

    @Override
    public boolean deleteModel(Model model) {
        playerEditViewModel.removePlayerFromRepository(user);
        return true;
    }
}
