package com.jumbo.trus.fine.detail.add;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.fine.detail.FineHelperFragment;


public class FinePlusFragment extends FineHelperFragment {

    private static final String TAG = "FinePlusFragment";

    private FinePlusViewModel finePlusViewModel;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        finePlusViewModel = new ViewModelProvider(requireActivity()).get(FinePlusViewModel.class);
        finePlusViewModel.init();
        swPlayer.setChecked(true);
        swNonPlayer.setChecked(false);
        btnCommit.setText("Potvrdit");
        btnDelete.setVisibility(View.GONE);

        finePlusViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        finePlusViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        finePlusViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
    }

    @Override
    protected void onCommitValidationTrue(String name, boolean swNonPlayer, int amount) {
        finePlusViewModel.addFineToRepository(name, amount, swNonPlayer, user);
    }
}
