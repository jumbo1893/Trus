package com.jumbo.trus.statistics.table.fine;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.table.TableStatisticsHelperFragment;

import org.json.JSONException;

import java.util.List;


public class TableFineMatchStatisticsFragment extends TableStatisticsHelperFragment {

    private static final String TAG = "TableBeerStatisticsDialog";

    //widgety


    private TableFineMatchStatisticsViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TableFineMatchStatisticsViewModel.class);
        viewModel.init();
        viewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasonList) {
                if (viewModel.getSeason().getValue() != null) {
                    tvSeason.setText(viewModel.getSeason().getValue().getName());
                }
                setupSeasonDropDownMenu(seasonList);
            }
        });
        viewModel.getSeason().observe(getViewLifecycleOwner(), new Observer<Season>() {
            @Override
            public void onChanged(Season season) {
                tvSeason.setText(season.getName());
                if (viewModel.getSeasons().getValue() != null) {
                    setupSeasonDropDownMenu(viewModel.getSeasons().getValue());
                }
            }
        });
        viewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProgressBar();
                    btnDownload.setEnabled(false);
                } else {
                    hideProgressBar();
                    btnDownload.setEnabled(true);
                }
            }
        });
        viewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getRowlists().observe(getViewLifecycleOwner(), new Observer<List<List<String>>>() {
            @Override
            public void onChanged(List<List<String>> rowLists) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                initTable(rowLists);
            }
        });
        viewModel.getClipBoardText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    saveTextToClipboard("trus stats", s);
                }
            }
        });
        tvSeason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + seasonArrayAdapter.getItem(i));
                viewModel.setSeason(seasonArrayAdapter.getItem(i));
            }
        });
    }

    @Override
    public void onDestroy() {
        viewModel.removeReg();
        super.onDestroy();
    }

    @Override
    protected void onDownloadClick() {
        try {
            viewModel.sendToGoogle("fine_match", requireActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
