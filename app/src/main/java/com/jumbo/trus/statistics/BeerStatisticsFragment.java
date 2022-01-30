package com.jumbo.trus.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Flag;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.BeerStatisticsRecycleViewAdapter;
import com.jumbo.trus.comparator.OrderByBeerAndLiquorNumber;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchAllViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModelTODELETE;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;

import java.util.ArrayList;
import java.util.List;

public class BeerStatisticsFragment extends Fragment implements OnListListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = "BeerStatisticsFragment";

    private RecyclerView rc_list;
    private ProgressBar progress_bar;
    private Button btn_overall, btn_order_rc, btn_search, btn_table;
    private EditText et_search;
    private Switch sw_player_match;
    private Spinner sp_select_player_season;

    private PlayerViewModelTODELETE playerViewModelTODELETE;
    private MatchAllViewModel matchAllViewModel;
    private SeasonsViewModel seasonsViewModel;
    private StatisticsViewModel statisticsViewModel;

    private BeerStatisticsRecycleViewAdapter adapter;
    private ArrayAdapter<String> seasonArrayAdapter;

    private List<Player> selectedPlayers;
    private List<Match> selectedMatches;
    private List<String> seasonsNames = new ArrayList<>();

    private boolean checkedPlayers = false;
    private boolean orderByDesc = true;
    private int spinnerPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        sw_player_match = view.findViewById(R.id.sw_player_match);
        sw_player_match.setChecked(false);
        sp_select_player_season = view.findViewById(R.id.sp_select_player_season);
        sp_select_player_season.setOnItemSelectedListener(this);
        btn_overall = view.findViewById(R.id.btn_overall);
        btn_order_rc = view.findViewById(R.id.btn_order_rc);
        btn_table = view.findViewById(R.id.btn_table);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        btn_search = view.findViewById(R.id.btn_search);
        et_search = view.findViewById(R.id.et_search);
        initSpinnerSeasons();
        setSpinnerAdapter();
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        playerViewModelTODELETE = new ViewModelProvider(requireActivity()).get(PlayerViewModelTODELETE.class);
        playerViewModelTODELETE.init();
        matchAllViewModel = new ViewModelProvider(requireActivity()).get(MatchAllViewModel.class);
        matchAllViewModel.init();
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        sw_player_match.setOnCheckedChangeListener(this);
        btn_order_rc.setOnClickListener(this);
        btn_overall.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_table.setOnClickListener(this);

        matchAllViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: nacetli se zapasy " + matches);
                useSeasonsFilter(matches);
                if (!checkedPlayers) {
                    initRecycleViewForMatches();
                    setAdapter();
                    adapter.notifyDataSetChanged(); //TODO notifyItemInserted
                }
            }
        });
        matchAllViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!checkedPlayers) {
                    if (aBoolean) {
                        showProgressBar();
                    } else {
                        hideProgressBar();
                    }
                }
            }
        });
        playerViewModelTODELETE.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> hraci) {
                Log.d(TAG, "onChanged: nacetli se hraci " + hraci);
                selectedPlayers = hraci;
                if (adapter == null && checkedPlayers) {
                    enhancePlayersFromStatisticViewModel();
                    initRecycleViewForPlayers();
                    setAdapter();
                    adapter.notifyDataSetChanged(); //TODO notifyItemInserted
                }
            }
        });
        playerViewModelTODELETE.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (checkedPlayers) {
                    if (aBoolean) {
                        showProgressBar();
                    } else {
                        hideProgressBar();
                    }
                }
            }
        });
        seasonsViewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasons) {
                Log.d(TAG, "onChanged: nacetli se sezony " + seasons);
                if (seasonArrayAdapter == null) {
                    initSpinnerSeasons();
                }
                seasonsNames.clear();
                seasonsNames.add("Všechny sezony");
                for (Season season : seasons) {
                    seasonsNames.add(season.getName());
                }
                setSpinnerAdapter();
                seasonArrayAdapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });


        return view;
    }

    private void useSeasonsFilter(List<Match> matches) {
        Log.d(TAG, "useSeasonsFilter: " + matches);
        if (spinnerPosition == 0) {
            selectedMatches = matches;
            return;
        }
        selectedMatches = new ArrayList<>();
        Season season = seasonsViewModel.getSeasons().getValue().get(spinnerPosition-1);
        for (Match match : matches) {
            if (match.getSeason().equals(season)) {
                selectedMatches.add(match);
            }
        }
    }

    private void enhancePlayersFromStatisticViewModel() {
        selectedPlayers = statisticsViewModel.enhancePlayersWithBeersAndLiquorsFromMatches(playerViewModelTODELETE.getPlayers().getValue(), selectedMatches);
    }


    private void setSpinnerAdapter() {
        sp_select_player_season.setAdapter(seasonArrayAdapter);
    }

    private void initSpinnerSeasons() {
        seasonArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, seasonsNames);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initRecycleViewForPlayers() {
        Log.d(TAG, "initRecycleViewForPlayers: " + matchAllViewModel.getMatches().getValue());
        adapter = new BeerStatisticsRecycleViewAdapter(selectedPlayers, getActivity(), this);
    }

    private void initRecycleViewForMatches() {
        Log.d(TAG, "initRecycleViewForMatches: ");
        adapter = new BeerStatisticsRecycleViewAdapter(selectedMatches, getActivity(), this);
    }

    private void setAdapter() {
        rc_list.setAdapter(adapter);
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    private void displayOverallDialog() {
        OverallBeerStatisticsDialog overallBeerStatisticsDialog = new OverallBeerStatisticsDialog(Flag.BEER, null, statisticsViewModel.returnOverallBeerStringList(selectedMatches, selectedPlayers));
        overallBeerStatisticsDialog.setTargetFragment(BeerStatisticsFragment.this, 1);
        overallBeerStatisticsDialog.show(getFragmentManager(), "dialogplus");
    }

    private void displayTableDialog() {
        TableBeerStatisticsDialog dialog = new TableBeerStatisticsDialog(Flag.BEER, null, selectedMatches, selectedPlayers);
        dialog.setTargetFragment(BeerStatisticsFragment.this, 1);
        dialog.show(getFragmentManager(), "dialogplus");
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onHracClick: kliknuto na pozici " + position + ", object: " + playerViewModelTODELETE.getPlayers().getValue());
        BeerStatisticsDialog beerStatisticsDialog;
        if (checkedPlayers) {
            beerStatisticsDialog = new BeerStatisticsDialog(Flag.PLAYER, selectedPlayers.get(position), spinnerPosition);
        }
        else {
            beerStatisticsDialog = new BeerStatisticsDialog(Flag.MATCH, selectedMatches.get(position));
        }
        beerStatisticsDialog.setTargetFragment(BeerStatisticsFragment.this, 1);
        beerStatisticsDialog.show(getFragmentManager(), "dialogplus");
    }

    @Override
    public void onItemLongClick(int position) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: " + isChecked);
        if (isChecked) {
            checkedPlayers = true;
            sw_player_match.setText("Přepni pro zobrazení zápasů");
            setSpinnerAdapter();
            enhancePlayersFromStatisticViewModel();
            initRecycleViewForPlayers();
            setAdapter();
        }
        else {
            checkedPlayers = false;
            sw_player_match.setText("Přepni pro zobrazení hráčů");
            initSpinnerSeasons();
            setSpinnerAdapter();
            initRecycleViewForMatches();
            setAdapter();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: sezona " + parent.getItemAtPosition(position) + position);
        spinnerPosition = position;
        useSeasonsFilter(matchAllViewModel.getMatches().getValue());
        if (adapter != null && !checkedPlayers) {
            initRecycleViewForMatches();
            setAdapter();
            adapter.notifyDataSetChanged();
        }
        else if (adapter != null) {
            enhancePlayersFromStatisticViewModel();
            initRecycleViewForPlayers();
            setAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order_rc: {
                if (checkedPlayers) {
                    selectedPlayers.sort(new OrderByBeerAndLiquorNumber(orderByDesc));
                }
                else {
                    selectedMatches.sort(new OrderByBeerAndLiquorNumber(orderByDesc));
                }
                setAdapter();
                orderByDesc = !orderByDesc;
                break;
            }
            case R.id.btn_overall: {
                displayOverallDialog();
                break;
            }
            case R.id.btn_table: {
                displayTableDialog();
                break;
            }
            case R.id.btn_search: {
                if (checkedPlayers) {
                    enhancePlayersFromStatisticViewModel();
                    selectedPlayers = statisticsViewModel.filterPlayers(selectedPlayers, et_search.getText().toString());
                    initRecycleViewForPlayers();
                    setAdapter();
                }
                else {
                    useSeasonsFilter(matchAllViewModel.getMatches().getValue());
                    selectedMatches = statisticsViewModel.filterMatches(selectedMatches, et_search.getText().toString());
                    initRecycleViewForMatches();
                    setAdapter();
                }
                break;
            }

        }
    }
}
