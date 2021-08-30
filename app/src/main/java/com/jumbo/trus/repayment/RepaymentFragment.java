package com.jumbo.trus.repayment;

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
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.PlayerRepaymentRecycleViewAdapter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.IPlayerFragment;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerDialog;
import com.jumbo.trus.player.PlayerRecycleViewAdapter;
import com.jumbo.trus.player.PlayerViewModel;

import java.util.ArrayList;
import java.util.List;

public class RepaymentFragment extends CustomUserFragment implements OnListListener, IRepaymentFragment {

    private static final String TAG = "RepaymentFragment";

    private FloatingActionButton fab_plus;
    private RecyclerView rc_hraci;
    private PlayerRepaymentRecycleViewAdapter adapter;
    private ProgressBar progress_bar;
    private PlayerViewModel playerViewModel;
    private MatchViewModel matchViewModel;
    private List<Player> selectedPlayers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        fab_plus = view.findViewById(R.id.fab_plus);
        fab_plus.setVisibility(View.GONE);
        rc_hraci = view.findViewById(R.id.rc_hraci);
        rc_hraci.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        initMainActivityViewModel();
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.init();
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        playerViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> hraci) {
                Log.d(TAG, "onChanged: nacetli se hraci " + hraci);
                filterPlayers(hraci);
                if (adapter == null) {
                    initHracRecycleView();
                }
                if (matchViewModel.getMatches().getValue() != null) {
                    enhancePlayers(matchViewModel.getMatches().getValue());
                }
                setAdapter();
                adapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });
        matchViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: nacetly se zapasy " + matches);
                if (selectedPlayers != null) {
                    enhancePlayers(matches);
                }
                setAdapter();
                adapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });
        playerViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        playerViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
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

    private void initHracRecycleView() {
        adapter = new PlayerRepaymentRecycleViewAdapter(selectedPlayers, getActivity(), this);
    }

    private void setAdapter() {
        rc_hraci.setAdapter(adapter);
        rc_hraci.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    private void filterPlayers(List<Player> players) {
        selectedPlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.isFan()) {
                selectedPlayers.add(player);
            }
        }
    }

    private void enhancePlayers(List<Match> matches) {
        for (Player player : selectedPlayers) {
            player.calculateAllFinesNumber(matches);
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onHracClick: kliknuto na pozici " + position + ", object: " + playerViewModel.getPlayers().getValue());
        PlayerDialog playerDialog = new PlayerDialog(Flag.PLAYER_EDIT, playerViewModel.getPlayers().getValue().get(position));
        playerDialog.setTargetFragment(RepaymentFragment.this, 1);
        playerDialog.show(getParentFragmentManager(), "dialogplus");
    }

    @Override
    public boolean createNewRepayment(int amount, String note) {
        return false;
    }

    @Override
    public boolean deleteModel(Model model) {
        Result removePlayerFromRepositoryResult = playerViewModel.removePlayerFromRepository((Player) model);
        Toast.makeText(getActivity(), removePlayerFromRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
        if (removePlayerFromRepositoryResult.isTrue()) {
            String text = " narozen " + ((Player) model).getBirthdayInStringFormat();
            createNotification(new Notification("Smazán " + (((Player) model).isFan() ? "fanoušek " : "hráč" ) + model.getName(), text), playerViewModel);
        }
        return removePlayerFromRepositoryResult.isTrue();
    }
}
