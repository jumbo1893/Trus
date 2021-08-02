package com.jumbo.trus.playerlist;

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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.user.User;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.adapters.SimpleRecycleViewAdapter;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MatchListFragment extends CustomUserFragment implements OnListListener, IChangeFineListListener,
        IChangePlayerListListener, AdapterView.OnItemSelectedListener, INotificationSender {

    private static final String TAG = "MatchListFragment";

    private RecyclerView rc_matches;
    private SimpleRecycleViewAdapter adapter;
    private ArrayAdapter<String> seasonArrayAdapter;
    private List<Match> selectedMatches;
    private List<String> seasonsNames = new ArrayList<>();
    private ProgressBar progress_bar;
    private MatchViewModel matchViewModel;
    private SeasonsViewModel seasonsViewModel;
    private Spinner sp_seasons;
    private int spinnerPosition = 0;
    private Flag flag;

    public MatchListFragment(Flag flag) {
        this.flag = flag;
        Log.d(TAG, "MatchListFragment: " + this.flag);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beer, container, false);
        rc_matches = view.findViewById(R.id.rc_matches);
        rc_matches.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        sp_seasons = view.findViewById(R.id.sp_seasons);
        sp_seasons.setOnItemSelectedListener(this);
        initMainActivityViewModel();
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        matchViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: nacetli se hraci " + matches);
                useSeasonsFilter(matches);
                initMatchRecycleView();
                setAdapter();
                adapter.notifyDataSetChanged(); //TODO notifyItemInserted
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
                setSeasonAdapter();
                seasonArrayAdapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });
        matchViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        matchViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        return view;
    }

    private void setSeasonAdapter() {
        sp_seasons.setAdapter(seasonArrayAdapter);
    }

    private void initSpinnerSeasons() {
        seasonArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, seasonsNames);
        seasonArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initMatchRecycleView() {
        adapter = new SimpleRecycleViewAdapter(selectedMatches, getActivity(), this);
    }

    private void setAdapter() {
        Log.d(TAG, "setAdapter: " + selectedMatches);
        rc_matches.setAdapter(adapter);
        rc_matches.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    private void useSeasonsFilter(List<Match> matches) {
        Log.d(TAG, "useSeasonsFilter: prvni" + matches);
        if (spinnerPosition == 0) {
            Log.d(TAG, "useSeasonsFilter: v3echno" + matches);
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


    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onHracClick: kliknuto na pozici " + position + ", object: " + matchViewModel.getMatches().getValue() + flag);
        if (flag == Flag.BEER) {
            BeerDialog beerDialog = new BeerDialog(Flag.MATCH_EDIT, matchViewModel.getMatches().getValue().get(position));
            beerDialog.setTargetFragment(MatchListFragment.this, 1);
            beerDialog.show(getParentFragmentManager(), "dialogplus");
        }
        else if (flag == Flag.FINE) {
            FinePlayerDialog finePlayerDialog = new FinePlayerDialog(matchViewModel.getMatches().getValue().get(position));
            finePlayerDialog.setTargetFragment(MatchListFragment.this, 1);
            finePlayerDialog.show(getParentFragmentManager(), "dialogplus");
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: sezona " + parent.getItemAtPosition(position) + position);
        spinnerPosition = position;
        if (adapter != null) {
            useSeasonsFilter(matchViewModel.getMatches().getValue());
            initMatchRecycleView();
            setAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean editMatch(List<Player> playerList, Match match) {
        Result result = matchViewModel.editMatchBeers(playerList, match);
        Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        return result.isTrue();
    }

    @Override
    public boolean editPlayer(List<ReceivedFine> fineList, Player player, Match match) {
        Result result = matchViewModel.editMatchPlayerFines(fineList, player, match);
        Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        return result.isTrue();
    }


    @Override
    public void sendNotificationToRepository(Notification notification) {
        notification.setUser(user);
        matchViewModel.sendNotificationToRepository(notification);
    }
}
