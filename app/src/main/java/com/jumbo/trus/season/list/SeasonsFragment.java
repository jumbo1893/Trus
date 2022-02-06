package com.jumbo.trus.season.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsRecycleViewAdapter;

import java.util.List;

public class SeasonsFragment extends CustomUserFragment {

    private static final String TAG = "SeasonsFragment";

    private RecyclerView rc_seasons;
    private ProgressBar progress_bar;
    private FloatingActionButton fab_plus;

    private SeasonsListViewModel seasonsListViewModel;

    private SeasonsRecycleViewAdapter seasonsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seasons, container, false);
        rc_seasons = view.findViewById(R.id.rc_seasons);
        rc_seasons.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        fab_plus = view.findViewById(R.id.fab_plus);
        seasonsListViewModel = new ViewModelProvider(requireActivity()).get(SeasonsListViewModel.class);
        seasonsListViewModel.init();
        Log.d(TAG, "onCreateView: ");

        seasonsListViewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasons) {
                Log.d(TAG, "onChanged: nacetly se sezony " + seasons);
                initSeasonsRecycleView(seasons);
                setAdapter();
            }
        });

        seasonsListViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showItem(progress_bar);
                } else {
                    hideItem(progress_bar);
                }
            }
        });
        seasonsListViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToNextFragment(19);
            }
        });
        return view;
    }

    private void showItem(View button) {
        button.setVisibility(View.VISIBLE);
    }

    private void hideItem(View button) {
        button.setVisibility(View.GONE);
    }


    private void initSeasonsRecycleView(List<Season> seasons) {
        seasonsAdapter = new SeasonsRecycleViewAdapter(seasons, getActivity(), this);
    }

    private void setAdapter() {
        rc_seasons.setAdapter(seasonsAdapter);
        rc_seasons.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void itemClick(int position) {
        sharedViewModel.setPickedSeasonForEdit(seasonsListViewModel.getSeasons().getValue().get(position));
        proceedToNextFragment(20);
    }
}
