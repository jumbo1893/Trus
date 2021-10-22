package com.jumbo.trus.statistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.FineStatisticsRecycleViewAdapter;
import com.jumbo.trus.comparator.OrderByFineAmount;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;

import java.util.ArrayList;
import java.util.List;

public class FineStatisticsFragment extends Fragment implements OnListListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = "FineStatisticsFragment";

    private RecyclerView rc_list;
    private ProgressBar progress_bar;
    private Button btn_overall, btn_order_rc, btn_search;
    private EditText et_search;
    private Switch sw_player_match;
    private Spinner sp_select_player_season;

    private PlayerViewModel playerViewModel;
    private MatchViewModel matchViewModel;
    private SeasonsViewModel seasonsViewModel;
    private StatisticsViewModel statisticsViewModel;

    private FineStatisticsRecycleViewAdapter adapter;
    private ArrayAdapter<String> seasonArrayAdapter;

    private List<Player> selectedPlayers;
    private List<Match> selectedMatches;
    private List<String> seasonsNames = new ArrayList<>();
    private List<String> playerSpinnerOptions = new ArrayList<>();

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
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        btn_search = view.findViewById(R.id.btn_search);
        et_search = view.findViewById(R.id.et_search);
        addPlayerSpinnerOptions();
        initSpinnerSeasons();
        setSpinnerAdapter();
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.init();
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        sw_player_match.setOnCheckedChangeListener(this);
        btn_order_rc.setOnClickListener(this);
        btn_overall.setOnClickListener(this);
        btn_search.setOnClickListener(this);

        matchViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: nacetly se zapasy " + matches);
                useSeasonsFilter(matches);
                if (!checkedPlayers) {
                    initRecycleViewForMatches();
                    setAdapter();
                    adapter.notifyDataSetChanged(); //TODO notifyItemInserted
                }
            }
        });
        matchViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        playerViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> hraci) {
                Log.d(TAG, "onChanged: nacetli se hraci " + hraci);
                usePlayerFilter(hraci);
                if (adapter == null && checkedPlayers) {
                    initRecycleViewForPlayers();
                    setAdapter();
                    adapter.notifyDataSetChanged(); //TODO notifyItemInserted
                }
            }
        });
        playerViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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

    private void usePlayerFilter(List<Player> players) {
        Log.d(TAG, "usePlayerFilter: " + players);
        selectedPlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.isFan()) {
                selectedPlayers.add(player);
            }
        }
    }
    private void setSpinnerAdapter() {
        sp_select_player_season.setAdapter(seasonArrayAdapter);
    }

    private void initSpinnerSeasons() {
        seasonArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, seasonsNames);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void addPlayerSpinnerOptions() {
        playerSpinnerOptions.clear();
        playerSpinnerOptions.add("Zobraz vše");
        playerSpinnerOptions.add("Hráči");
        playerSpinnerOptions.add("Fanoušci");
    }

    private void initRecycleViewForPlayers() {
        Log.d(TAG, "initRecycleViewForPlayers: " + matchViewModel.getMatches().getValue());
        statisticsViewModel.enhancePlayersWithFinesFromMatches(selectedPlayers, matchViewModel.getMatches().getValue());
        adapter = new FineStatisticsRecycleViewAdapter(selectedPlayers, getActivity(), this);
    }

    private void initRecycleViewForMatches() {
        Log.d(TAG, "initRecycleViewForMatches: ");
        adapter = new FineStatisticsRecycleViewAdapter(selectedMatches, getActivity(), this);
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

    private void displayOverallMatchDialog() {
        Log.d(TAG, "displayOverallMatchDialog zobrazen");
        int[] fine = statisticsViewModel.countNumberOfAllFines(playerViewModel.getPlayers().getValue(), selectedMatches);

        String text = "Celkový počet pokut v zobrazených zápasech: " + fine[0] + " v celkové částce " + fine[1] +  " Kč";
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("celkové pokuty");
        alert.setMessage(text);

        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

        private void displayOverallPlayerDialog() {
        Log.d(TAG, "displayOverallPlayerDialog zobrazen");
        int[] fineOverall = statisticsViewModel.countNumberOfAllFines(selectedPlayers, matchViewModel.getMatches().getValue());
        String text = "Celkový počet pokut u zobrazených hráčů: " + fineOverall[0] + " v celkové částce " + fineOverall[1] +  " Kč";
        for (Season season : seasonsViewModel.getSeasons().getValue()) {
            int[] fineSeason = statisticsViewModel.countNumberOfAllFinesBySeason(selectedPlayers, matchViewModel.getMatches().getValue(), season);
            text += "\n\nPro sezonu " + season.getName() + " dostali vybraní hráči: " + fineSeason[0] + " pokut v celkové částce " + fineSeason[1] + " Kč";
        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("celkové pokuty");
        alert.setMessage(text);

        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: kliknuto na pozici " + position + ", object: " + playerViewModel.getPlayers().getValue() + checkedPlayers);
        FineStatisticsDialog fineStatisticsDialog;
        if (checkedPlayers) {
            fineStatisticsDialog = new FineStatisticsDialog(Flag.PLAYER, selectedPlayers.get(position));
        }
        else {
            fineStatisticsDialog = new FineStatisticsDialog(Flag.MATCH, selectedMatches.get(position));
        }
        fineStatisticsDialog.setTargetFragment(FineStatisticsFragment.this, 1);
        fineStatisticsDialog.show(getFragmentManager(), "dialogplus");
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
            sp_select_player_season.setVisibility(View.INVISIBLE);
            initRecycleViewForPlayers();
            setAdapter();
        }
        else {
            checkedPlayers = false;
            sw_player_match.setText("Přepni pro zobrazení hráčů");
            sp_select_player_season.setVisibility(View.VISIBLE);
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
        if (adapter != null && !checkedPlayers) {
            useSeasonsFilter(matchViewModel.getMatches().getValue());
            initRecycleViewForMatches();
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
                    selectedPlayers.sort(new OrderByFineAmount(orderByDesc));
                }
                else {
                    selectedMatches.sort(new OrderByFineAmount(orderByDesc));
                }
                setAdapter();
                orderByDesc = !orderByDesc;
                break;
            }
            case R.id.btn_overall: {
                if (checkedPlayers) {
                    displayOverallPlayerDialog();
                }
                else {
                    displayOverallMatchDialog();
                }
                break;
            }
            case R.id.btn_search: {
                if (checkedPlayers) {
                    usePlayerFilter(playerViewModel.getPlayers().getValue());
                    selectedPlayers = statisticsViewModel.filterPlayers(selectedPlayers, et_search.getText().toString());
                    initRecycleViewForPlayers();
                    setAdapter();
                }
                else {
                    useSeasonsFilter(matchViewModel.getMatches().getValue());
                    selectedMatches = statisticsViewModel.filterMatches(selectedMatches, et_search.getText().toString());
                    initRecycleViewForMatches();
                    setAdapter();
                }
                break;
            }

        }
    }
}
