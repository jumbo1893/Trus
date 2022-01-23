package com.jumbo.trus.match.matchlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.SharedViewModel;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.season.Season;

import java.util.List;

public class MatchFragment extends CustomUserFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MatchFragment";


    private FloatingActionButton fab_plus;
    private RecyclerView rc_zapas;
    private SimpleRecycleViewAdapter adapter;
    private ArrayAdapter<Season> seasonArrayAdapter;
    private ProgressBar progress_bar;
    private MatchListViewModel matchListViewModel;
    private SharedViewModel sharedViewModel;
    private Spinner sp_seasons;

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_match, container, false);
        fab_plus = view.findViewById(R.id.fab_plus);
        rc_zapas = view.findViewById(R.id.rc_zapasy);
        rc_zapas.addItemDecoration(new SimpleDividerItemDecoration(requireActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        sp_seasons = view.findViewById(R.id.sp_seasons);
        sp_seasons.setOnItemSelectedListener(this);
        matchListViewModel = new ViewModelProvider(requireActivity()).get(MatchListViewModel.class);
        matchListViewModel.init();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        matchListViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: nacetli se hraci " + matches);
                initMatchRecycleView(matches);
                setAdapter();
            }
        });
        matchListViewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasons) {
                Log.d(TAG, "onChanged: nacetli se sezony " + seasons);
                initSpinnerSeasons(seasons);
                setSeasonAdapter();
            }
        });
        matchListViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        matchListViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
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
                proceedToNextFragment(13);
            }
        });
        return view;
    }

    private void setSeasonAdapter() {
        sp_seasons.setAdapter(seasonArrayAdapter);
    }

    private void initSpinnerSeasons(List<Season> seasons) {
        seasonArrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, seasons);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initMatchRecycleView(List<Match> matches) {
        adapter = new SimpleRecycleViewAdapter(matches, getActivity(), this);
    }

    private void setAdapter() {
        rc_zapas.setAdapter(adapter);
        rc_zapas.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }


    @Override
    protected void itemClick(int position) {
        sharedViewModel.setPickedMatchForEdit(matchListViewModel.getMatches().getValue().get(position));
        proceedToNextFragment(14);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: sezona " + parent.getItemAtPosition(position) + position);
        if (adapter != null) {
            matchListViewModel.setSelectedSeason((Season) parent.getItemAtPosition(position));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
