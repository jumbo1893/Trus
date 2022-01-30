package com.jumbo.trus.fine.detail.edit;


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
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.detail.FineHelperFragment;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerHelperFragment;
import com.jumbo.trus.player.edit.PlayerEditViewModel;


public class FineEditFragment extends FineHelperFragment implements IFragment {

    private static final String TAG = "FineEditFragment";
    private FineEditViewModel fineEditViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCommit.setText("Upravit");
        btnDelete.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");
        fineEditViewModel = new ViewModelProvider(requireActivity()).get(FineEditViewModel.class);
        fineEditViewModel.init();
        fineEditViewModel.setPickedFine(sharedViewModel.getPickedFineForEdit().getValue());
        fineEditViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        fineEditViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        fineEditViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
        fineEditViewModel.getFine().observe(getViewLifecycleOwner(), new Observer<Fine>() {
            @Override
            public void onChanged(Fine fine) {
                setTextsToEditFine(fine);
            }
        });

    }

    private void setTextsToEditFine(Fine fine) {
        textName.getEditText().setText(fine.getName());
        textAmount.getEditText().setText(String.valueOf(fine.getAmount()));
        swPlayer.setChecked(!fine.isForNonPlayers());
        swPlayer.setChecked(!fine.isForNonPlayers());
        swNonPlayer.setChecked(fine.isForNonPlayers());
    }


    @Override
    protected void cancelClicked() {
        Log.d(TAG, "cancelClicked: ");
        displayDeleteConfirmationDialog(fineEditViewModel.getFine().getValue(),this, "Smazat pokutu", "Opravdu chcete smazat tuto pokutu z databáze?");
    }

    @Override
    protected void onCommitValidationTrue(String name, boolean swNonPlayer, int amount) {
        fineEditViewModel.editFineInRepository(name, amount, swNonPlayer, user);
    }

    @Override
    public boolean deleteModel(Model model) {
        fineEditViewModel.removeFineFromRepository(user);
        return true;
    }
}
