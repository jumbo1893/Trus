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
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.PlayerRepaymentRecycleViewAdapter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchAllViewModel;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.player.PlayerViewModelTODELETE;

import java.util.ArrayList;
import java.util.List;

public class RepaymentFragment extends CustomUserFragment {

    private static final String TAG = "RepaymentFragment";

    private RecyclerView rc_players;
    private PlayerRepaymentRecycleViewAdapter adapter;
    private ProgressBar progress_bar;
    private RepaymentViewModel repaymentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repayment, container, false);
        rc_players = view.findViewById(R.id.rc_players);
        rc_players.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        repaymentViewModel = new ViewModelProvider(requireActivity()).get(RepaymentViewModel.class);
        repaymentViewModel.init();
        repaymentViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> players) {
                Log.d(TAG, "onChanged: nacetli se hraci " + players);
                initRecycleView(players);
                setAdapter();
            }
        });
        repaymentViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        repaymentViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
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

    private void initRecycleView(List<Player> players) {
        Log.d(TAG, "initRecycleView: ");
        adapter = new PlayerRepaymentRecycleViewAdapter(players, getActivity(), this);
    }

    private void setAdapter() {
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    @Override
    protected void itemClick(int position) {
        sharedViewModel.setPickedPlayerForEdit(repaymentViewModel.getPlayers().getValue().get(position));
        proceedToNextFragment(23);
    }
}
