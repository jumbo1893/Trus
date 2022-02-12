package com.jumbo.trus.player.add;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.player.PlayerHelperFragment;


public class PlayerPlusFragment extends PlayerHelperFragment {

    private static final String TAG = "PlayerPlusFragment";

    private PlayerPlusViewModel playerPlusViewModel;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerPlusViewModel = new ViewModelProvider(requireActivity()).get(PlayerPlusViewModel.class);
        playerPlusViewModel.init();
        swFan.setChecked(false);
        btnCommit.setText("Potvrdit");
        btnDelete.setVisibility(View.GONE);

        playerPlusViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        playerPlusViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        playerPlusViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
    }

    @Override
    protected void onCommitValidationTrue(String name, boolean swFan, String date) {
        playerPlusViewModel.addPlayerToRepository(name, swFan, date, user);
    }
}
