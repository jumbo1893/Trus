package com.jumbo.trus.player;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.SimpleDividerItemDecoration;

import java.util.List;

public class PlayerFragment extends Fragment implements OnListListener, IPlayerFragment {

    private static final String TAG = "HracFragment";

    private FloatingActionButton fab_plus;
    private RecyclerView rc_hraci;
    private PlayerRecycleViewAdapter adapter;
    private ProgressBar progress_bar;
    private PlayerViewModel playerViewModel;

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hrac, container, false);
        fab_plus = view.findViewById(R.id.fab_plus);
        rc_hraci = view.findViewById(R.id.rc_hraci);
        rc_hraci.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.init();
        playerViewModel.getPlayers().observe(this, new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> hraci) {
                Log.d(TAG, "onChanged: nacetli se hraci " + hraci);
                if (adapter == null) {
                    initHracRecycleView();
                }
                setAdapter();
                adapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });
        playerViewModel.isUpdating().observe(this, new Observer<Boolean>() {
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
        playerViewModel.getAlert().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerDialog playerDialog = new PlayerDialog(Flag.PLAYER_PLUS);
                playerDialog.setTargetFragment(PlayerFragment.this, 1);
                playerDialog.show(getFragmentManager(), "dialogplus");
            }
        });
        return view;
    }

    private void initHracRecycleView() {
        adapter = new PlayerRecycleViewAdapter(playerViewModel.getPlayers().getValue(), getActivity(), this);
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

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onHracClick: kliknuto na pozici " + position + ", object: " + playerViewModel.getPlayers().getValue());
        PlayerDialog playerDialog = new PlayerDialog(Flag.PLAYER_EDIT, playerViewModel.getPlayers().getValue().get(position));
        playerDialog.setTargetFragment(PlayerFragment.this, 1);
        playerDialog.show(getFragmentManager(), "dialogplus");
    }

    @Override
    public boolean createNewPlayer(String jmeno, String birthday, boolean fan) {

        Result result = playerViewModel.checkNewPlayerValidation(jmeno, birthday);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result addPlayerToRepositoryResult = playerViewModel.addPlayerToRepository(jmeno, fan, birthday);
            Toast.makeText(getActivity(), addPlayerToRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
        }
        return result.isTrue();

    }

    @Override
    public boolean editPlayer(String jmeno, String birthday, boolean fan, Player player) {
        Result result = playerViewModel.checkNewPlayerValidation(jmeno, birthday);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result editPlayerInRepositoryResult = playerViewModel.editPlayerInRepository(jmeno, fan, birthday, player);
            Toast.makeText(getActivity(), editPlayerInRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
        }
        return result.isTrue();
    }

    @Override
    public boolean deleteModel(Model model) {
        Result removePlayerFromRepositoryResult = playerViewModel.removePlayerFromRepository((Player) model);
        Toast.makeText(getActivity(), removePlayerFromRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
        return removePlayerFromRepositoryResult.isTrue();
    }
}
