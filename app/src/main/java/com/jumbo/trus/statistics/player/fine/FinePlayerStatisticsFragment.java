package com.jumbo.trus.statistics.player.fine;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jumbo.trus.adapters.recycleview.FineStatisticsRecycleViewAdapter;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.StatisticsHelperFragment;

import java.util.List;
import java.util.Objects;


public class FinePlayerStatisticsFragment extends StatisticsHelperFragment implements OnListListener {

    private static final String TAG = "FinePlayerStatisticsFragment";

    private FineStatisticsRecycleViewAdapter adapter;
    private FinePlayerStatisticsViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FinePlayerStatisticsViewModel.class);
        viewModel.init();
        viewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> playerList) {
                Log.d(TAG, "onChanged: " + playerList.size());
                initRecycleView(playerList);
                setAdapter();
            }
        });
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
                }
                else {
                    hideProgressBar();
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

        tvSeason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + seasonArrayAdapter.getItem(i));
                viewModel.setSeason(seasonArrayAdapter.getItem(i));
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.changeOrderBy();
            }
        });

        Objects.requireNonNull(textSearch.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                viewModel.setKeyword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initRecycleView(List<Player> playerList) {
        Log.d(TAG, "initRecycleView: " + playerList.size());
        adapter = new FineStatisticsRecycleViewAdapter(playerList, getActivity(), this);
    }

    private void setAdapter() {
        rc_list.setAdapter(adapter);
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onItemClick(int position) {
        sharedViewModel.setPickedPlayerForEdit(Objects.requireNonNull(viewModel.getPlayers().getValue()).get(position));
        sharedViewModel.setPickedSeasonForEdit(viewModel.getSeason().getValue());
        proceedToNextFragment(25);
    }
}
