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
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.FineViewModel;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
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
    private List<Integer> finesNumber;
    private List<Integer> finesCompesation;
    private FinesRecycleViewAdapter adapter;
    private boolean commit = false;
    private FineViewModel fineViewModel;
    private Match match;


    public AddFineDialog(Model model, Match match) {
        super(model);
        this.match = match;
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
        fineViewModel = new ViewModelProvider(requireActivity()).get(FineViewModel.class);
        fineViewModel.init();
        fineViewModel.getFines().observe(getViewLifecycleOwner(), new Observer<List<Fine>>() {
            @Override
            public void onChanged(List<Fine> fines) {
                Log.d(TAG, "onChanged: nacetly se sezony " + fines);
                ((Player)model).mergeFineLists(fines);
                playerFines = ((Player)model).getReceivedFines();
                Log.d(TAG, "onChanged: " + ((Player)model).getReceivedFines());
                initFineCompensation();
                initRecycleView();

                setAdapter();
                adapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });
        //initFineCompensation();
        //initRecycleView();

        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                if (iChangeFineListListener.editPlayer(playerFines, (Player) model, match)) {
                    commit = true;
                    iNotificationSender.sendNotificationToRepository(new Notification(match, (Player) model, playerFines, finesCompesation));
                    getDialog().dismiss();
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

    private void initFineCompensation() {
        finesCompesation = new ArrayList<>();
        for (ReceivedFine receivedFine : ((Player)model).getReceivedFines()) {
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
        if (!commit) {
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
