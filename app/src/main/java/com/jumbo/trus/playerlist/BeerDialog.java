package com.jumbo.trus.playerlist;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnPlusButtonListener;
import com.jumbo.trus.R;
import com.jumbo.trus.adapters.PlusRecycleViewAdapter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.List;

public class BeerDialog extends Dialog implements OnPlusButtonListener {

    private static final String TAG = "BeerDialog";

    //widgety
    private RecyclerView rc_players;
    private Button btn_cancel, btn_commit;

    //vars
    private IChangePlayerListListener iChangePlayerListListener;
    private List<Player> selectedPlayers;
    private List<Integer> beerCompensation;
    private PlusRecycleViewAdapter adapter;
    private boolean commit = false;


    public BeerDialog(Flag flag, Model model) {
        super(flag, model);
        selectedPlayers = ((Match) model).getPlayerList();
        Log.d(TAG, "BeerDialog: " + ((Match) model).getPlayerList());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_beer, container, false);
        rc_players = view.findViewById(R.id.rc_players);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_cancel.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        initBeerCompensation();
        initMatchRecycleView();
        setAdapter();

        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                if (iChangePlayerListListener.editMatch(selectedPlayers, (Match) model)) {
                    commit = true;
                    getDialog().dismiss();
                }
                break;
            }
            case R.id.btn_cancel: {
                Log.d(TAG, "onClick: kliknuti na smazat zápas " + selectedPlayers);
                getDialog().dismiss();
                break;
            }
        }
    }

    private void initMatchRecycleView() {
        adapter = new PlusRecycleViewAdapter(selectedPlayers, getActivity(), this);
    }

    private void initBeerCompensation() {
        beerCompensation = new ArrayList<>();
        for (Player player : selectedPlayers) {
            beerCompensation.add(player.getNumberOfBeers());
        }
        Log.d(TAG, "initSelectedPlayers: " + selectedPlayers);
    }

    private void setAdapter() {
        Log.d(TAG, "setAdapter: ");
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void returnOriginalBeerNumber() {
        for (int i = 0; i < selectedPlayers.size(); i++) {
            selectedPlayers.get(i).setNumberOfBeers(beerCompensation.get(i));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iChangePlayerListListener = (IChangePlayerListListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!commit) {
            returnOriginalBeerNumber();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onPlusClick(int position) {
        selectedPlayers.get(position).addBeer();
    }

    @Override
    public void onMinusClick(int position) {
        selectedPlayers.get(position).removeBeer();
    }
}
