package com.jumbo.trus.match;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.MainActivityViewModel;
import com.jumbo.trus.Model;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.user.User;
import com.jumbo.trus.adapters.SimpleRecycleViewAdapter;
import com.jumbo.trus.Flag;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.season.SeasonsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MatchFragment extends Fragment implements OnListListener, IMatchFragment, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MatchFragment";

    private User user;

    private FloatingActionButton fab_plus;
    private RecyclerView rc_zapas;
    private SimpleRecycleViewAdapter adapter;
    private ArrayAdapter<String> seasonArrayAdapter;
    private List<Player> players = new ArrayList<>();
    private List<Match> selectedMatches;
    private List<String> seasonsNames = new ArrayList<>();
    private ProgressBar progress_bar;
    private MatchViewModel matchViewModel;
    private SeasonsViewModel seasonsViewModel;
    private PlayerViewModel playerViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private Spinner sp_seasons;
    private int spinnerPosition = 0;

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_match, container, false);
        fab_plus = view.findViewById(R.id.fab_plus);
        rc_zapas = view.findViewById(R.id.rc_zapasy);
        rc_zapas.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        sp_seasons = view.findViewById(R.id.sp_seasons);
        sp_seasons.setOnItemSelectedListener(this);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUser(user);
            }
        });
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.init();
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

        playerViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> playerList) {
                players = playerList;
            }
        });

        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + seasonsViewModel.getSeasons().getValue() + seasonsNames);
                MatchDialog matchDialog = new MatchDialog(Flag.MATCH_PLUS, seasonsViewModel.getSeasons().getValue(), players);
                matchDialog.setTargetFragment(MatchFragment.this, 1);
                matchDialog.show(getFragmentManager(), "dialogplus");
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
        rc_zapas.setAdapter(adapter);
        rc_zapas.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private void createNotification(Notification notification) {
        notification.setUser(user);
        matchViewModel.sendNotificationToRepository(notification);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onHracClick: kliknuto na pozici " + position + ", object: " + matchViewModel.getMatches().getValue());
        MatchDialog matchDialog = new MatchDialog(Flag.MATCH_EDIT, matchViewModel.getMatches().getValue().get(position), seasonsViewModel.getSeasons().getValue(), players);
        matchDialog.setTargetFragment(MatchFragment.this, 1);
        matchDialog.show(getParentFragmentManager(), "dialogplus");
    }


    @Override
    public boolean createNewMatch(String opponent, String date, boolean homeMatch, Season season, List<Player> playerList) {
        Result result = matchViewModel.checkNewMatchValidation(opponent, date, playerList);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result addMatchToRepositoryResult = matchViewModel.addMatchToRepository(opponent, homeMatch, date, season, playerList,
                    seasonsViewModel.getSeasons().getValue());
            Toast.makeText(getActivity(), addMatchToRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
            if (addMatchToRepositoryResult.isTrue()) {
                String text = "Byl vytvořen " +  (homeMatch ? "domácí zápas" : "venkovní zápas") + " se soupeřem " + opponent + " hraný " + date;
                createNotification(new Notification("Přidán zápas se soupeřem " + opponent, text));
            }
        }
        return result.isTrue();
    }

    @Override
    public boolean editMatch(String opponent, String date, boolean homeMatch, Season season, List<Player> playerList, Match match) {
        Result result = matchViewModel.checkNewMatchValidation(opponent, date, playerList);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result editMatchInRepositoryResult = matchViewModel.editMatchInRepository(opponent, homeMatch, date, season, playerList,
                    seasonsViewModel.getSeasons().getValue(), match);
            Toast.makeText(getActivity(), editMatchInRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
            if (editMatchInRepositoryResult.isTrue()) {
                String text = "Zápas byl změněn na " + (homeMatch ? "domácí zápas" : "venkovní zápas") + " se soupeřem " + opponent + " hraný " + date;
                createNotification(new Notification("Upraven zápas " + match.getOpponent(), text));
            }
        }
        return result.isTrue();
    }
    @Override
    public boolean deleteModel(Model model) {
        Result removeMatchFromRepositoryResult = matchViewModel.removeMatchFromRepository((Match) model);
        Toast.makeText(getActivity(), removeMatchFromRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
        if (removeMatchFromRepositoryResult.isTrue()) {
            String text = "hraný " + ((Match) model).getDateOfMatchInStringFormat();
            createNotification(new Notification("Smazán zápas " + ((Match) model).getOpponent(), text));
        }
        return removeMatchFromRepositoryResult.isTrue();
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

    public void setUser(User user) {
        this.user = user;
    }
}
