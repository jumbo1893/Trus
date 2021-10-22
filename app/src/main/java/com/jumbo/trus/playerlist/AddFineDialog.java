package com.jumbo.trus.playerlist;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnPlusButtonListener;
import com.jumbo.trus.R;
import com.jumbo.trus.adapters.FinesRecycleViewAdapter;
import com.jumbo.trus.adapters.MultiFinesRecycleViewAdapter;
import com.jumbo.trus.comparator.OrderByNonplayerFine;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.FineViewModel;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddFineDialog extends Dialog implements OnPlusButtonListener {

    private static final String TAG = "AddFineDialog";

    //widgety
    private RecyclerView rc_players;
    private Button btn_cancel, btn_commit;

    //vars
    private IChangeFineListListener iChangeFineListListener;
    private INotificationSender iNotificationSender;
    private List<ReceivedFine> playerFines;
    private List<Integer> finesCompesation;
    private FinesRecycleViewAdapter adapter;
    private MultiFinesRecycleViewAdapter multiAdapter;
    private boolean commit = false;
    private FineViewModel fineViewModel;
    private Player player; //pokud je player null, tak se rozdávají multi-pokuty. Poznávací znamení
    private List<Player> multiPlayers;


    public AddFineDialog(Model match, Player player) {
        super(match);
        this.player = player;
    }

    public AddFineDialog(Model match, List<Player> multiPlayers) {
        super(match);
        this.multiPlayers = multiPlayers;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fine_player, container, false);
        rc_players = view.findViewById(R.id.rc_players);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_cancel.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        fineViewModel = new ViewModelProvider(requireActivity()).get(FineViewModel.class);
        fineViewModel.init();
        fineViewModel.getFines().observe(getViewLifecycleOwner(), new Observer<List<Fine>>() {
            @Override
            public void onChanged(List<Fine> fines) {
                Log.d(TAG, "onChanged: nacetly se pokuty " + fines);
                if (player != null) {
                    player.mergeFineLists(fines);
                    if (player.isMatchParticipant()) {
                        Collections.sort(player.getReceivedFines(), new OrderByNonplayerFine(true));
                    } else {
                        Collections.sort(player.getReceivedFines(), new OrderByNonplayerFine(false));
                    }
                    playerFines = player.getReceivedFines();
                    initFineCompensation();
                    initRecycleView();
                    setAdapter();
                    adapter.notifyDataSetChanged(); //TODO notifyItemInserted
                }
                else {
                    for (Player player : multiPlayers) {
                        player.mergeFineLists(fines);
                    }
                    initMultiRecycleView(fines);
                    setMultiAdapter();
                }
            }
        });

        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                if (player != null) {
                    if (iChangeFineListListener.editPlayer(playerFines, player, (Match) model)) {
                        commit = true;
                        iNotificationSender.sendNotificationToRepository(new Notification((Match) model, player, playerFines, finesCompesation));
                        getDialog().dismiss();
                    }
                }
                else {
                    StringBuilder notificationText = new StringBuilder();
                    StringBuilder notificationTitle = new StringBuilder("V zápase proti " + ((Match) model).getOpponent() + " byly změněny pokuty u hráčů: ");
                    //pro notifikaci, ať to neprojíždí přes všechny hráče
                    for (int i = 0; i < multiAdapter.getItemCount(); i++) {
                        int count =  multiAdapter.getFinesNumber().get(i);
                        if (count > 0) {
                            notificationText.append(multiAdapter.getFines().get(i).getName()).append(" navýšeno o ").append(count).append("\n");

                        }
                    }
                    for (Player player : multiPlayers) {
                      notificationTitle.append(player.getName() + ", ");
                    }
                    if (iChangeFineListListener.editMatchFines(multiPlayers, multiAdapter.getFines(), multiAdapter.getFinesNumber(), (Match) model)) {
                        commit = true;
                        Notification notification = new Notification(notificationTitle.toString(), notificationText.toString());
                        iNotificationSender.sendNotificationToRepository(notification);
                        getDialog().dismiss();
                    }
                }
                break;
            }
            case R.id.btn_cancel: {
                getDialog().dismiss();
                break;
            }
        }
    }


    private void initRecycleView() {
        adapter = new FinesRecycleViewAdapter(playerFines, getActivity(), this);
    }

    private void initMultiRecycleView(List<Fine> fines) {
        multiAdapter = new MultiFinesRecycleViewAdapter(fines, getActivity());
    }

    private void setMultiAdapter() {
        rc_players.setAdapter(multiAdapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initFineCompensation() {
        finesCompesation = new ArrayList<>();
        for (ReceivedFine receivedFine : player.getReceivedFines()) {
            finesCompesation.add(receivedFine.getCount());
        }
    }

    private void setAdapter() {
        Log.d(TAG, "setAdapter: ");
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void returnOriginalFineNumber() {
        Log.d(TAG, "returnOriginalFineNumber: " + playerFines);
        for (int i = 0; i < playerFines.size(); i++) {
            playerFines.get(i).setCount(finesCompesation.get(i));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iChangeFineListListener = (IChangeFineListListener) getTargetFragment();
            iNotificationSender = (INotificationSender) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!commit && (player != null)) {
            returnOriginalFineNumber();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onPlusClick(int position) {
        playerFines.get(position).addFineCount();
    }

    @Override
    public void onMinusClick(int position) {
        playerFines.get(position).removeFineCount();
    }
}
