package com.jumbo.trus.pkfl.stats;

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

import com.jumbo.trus.adapters.array.PkflSpinnerArrayAdapter;
import com.jumbo.trus.adapters.recycleview.BeerStatisticsRecycleViewAdapter;
import com.jumbo.trus.adapters.recycleview.StringTitleAndTextRecycleViewAdapter;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.StatisticsHelperFragment;
import com.jumbo.trus.statistics.player.ListTexts;
import com.jumbo.trus.statistics.player.beer.BeerPlayerStatisticsViewModel;

import java.util.List;
import java.util.Objects;


public class PkflSeasonStatisticsFragment extends StatisticsHelperFragment implements OnListListener {

    private static final String TAG = "PkflSeasonStatisticsFragment";

    private StringTitleAndTextRecycleViewAdapter adapter;
    private PkflStatsCurrentSeasonViewModel viewModel;
    private PkflSpinnerArrayAdapter pkflSpinnerArrayAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PkflStatsCurrentSeasonViewModel.class);
        viewModel.init();
        viewModel.getRecycleViewList().observe(getViewLifecycleOwner(), new Observer<List<ListTexts>>() {
            @Override
            public void onChanged(List<ListTexts> listTexts) {
                initRecycleView(listTexts);
                setAdapter();
            }
        });
        viewModel.getSpinnerOptions().observe(getViewLifecycleOwner(), new Observer<List<SpinnerOption>>() {
            @Override
            public void onChanged(List<SpinnerOption> spinnerOptions) {
                if (viewModel.getPickedSpinnerOption().getValue() != null) {
                    tvSeason.setText(viewModel.getPickedSpinnerOption().getValue().getName());
                }
                setupSpinnerOptionsDropdownMenu(spinnerOptions);
            }
        });
        viewModel.getPickedSpinnerOption().observe(getViewLifecycleOwner(), new Observer<SpinnerOption>() {
            @Override
            public void onChanged(SpinnerOption spinnerOption) {
                tvSeason.setText(spinnerOption.getName());
                if (viewModel.getSpinnerOptions().getValue() != null) {
                    setupSpinnerOptionsDropdownMenu(viewModel.getSpinnerOptions().getValue());
                }
            }
        });
        viewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProgressBar();
                } else {
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

        viewModel.getLoadingAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    if (s.equals("")) {
                        tv_loading.setVisibility(View.GONE);
                    }
                    else {
                        tv_loading.setVisibility(View.VISIBLE);
                        tv_loading.setText(s);
                    }
                }
            }
        });

        tvSeason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setSpinnerOption(pkflSpinnerArrayAdapter.getItem(i));
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.changeOrder();
            }
        });

        Objects.requireNonNull(textSearch.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //viewModel.setKeyword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setupSpinnerOptionsDropdownMenu(List<SpinnerOption> spinnerOptions) {
        if (spinnerOptions != null) {
            pkflSpinnerArrayAdapter = new PkflSpinnerArrayAdapter(getActivity(), spinnerOptions);
            tvSeason.setAdapter(pkflSpinnerArrayAdapter);
            tvSeason.dismissDropDown();
        }
    }

    @Override
    public void onDestroy() {
        viewModel.removeReg();
        super.onDestroy();
    }

    private void initRecycleView(List<ListTexts> listTexts) {
        adapter = new StringTitleAndTextRecycleViewAdapter(listTexts, getActivity(), this);
    }

    private void setAdapter() {
        rc_list.setAdapter(adapter);
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onItemClick(int position) {
    }
}
