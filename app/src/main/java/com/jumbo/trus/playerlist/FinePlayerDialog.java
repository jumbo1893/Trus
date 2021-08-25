package com.jumbo.trus.playerlist;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.OnPlusButtonListener;
import com.jumbo.trus.R;
import com.jumbo.trus.adapters.PlusRecycleViewAdapter;
import com.jumbo.trus.adapters.SimpleRecycleViewAdapter;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.FineViewModel;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.List;

public class FinePlayerDialog extends Dialog implements OnListListener {

    private static final String TAG = "FinePlayerDialog";

    //widgety
    private RecyclerView rc_players;
    private Button btn_cancel, btn_commit;
    private TextView tv_title;

    //vars
    //private IChangePlayerListListener iChangePlayerListListener;
    private List<Player> selectedPlayers;
    //private List<Integer> fineCompensation;
    private SimpleRecycleViewAdapter adapter;


    public FinePlayerDialog(Model model) {
        super(model);
        selectedPlayers = ((Match) model).getPlayerListOnlyWithParticipantsAndWithoutFans();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fine_player, container, false);
        rc_players = view.findViewById(R.id.rc_players);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_commit = view.findViewById(R.id.btn_commit);
        tv_title = view.findViewById(R.id.tv_title);
        btn_cancel.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        setTexts();

        initPlusRecycleView();
        setAdapter();

        return view;
    }

    private void setTexts() {
        tv_title.setText("Přidat pokuty");
        btn_cancel.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                /*if (iChangePlayerListListener.editMatch(selectedPlayers, (Match) model)) {
                    commit = true;
                    getDialog().dismiss();
                }*/
                getDialog().dismiss();
                break;
            }
            case R.id.btn_cancel: {
                Log.d(TAG, "onClick: kliknuti na smazat zápas " + selectedPlayers);
                getDialog().dismiss();
                break;
            }
        }
    }

    private void initPlusRecycleView() {
        adapter = new SimpleRecycleViewAdapter(selectedPlayers, getActivity(), this);

    }

    private void setAdapter() {
        Log.d(TAG, "setAdapter: ");
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


   /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iChangePlayerListListener = (IChangePlayerListListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }
    }*/

    @Override
    public void onItemClick(int position) {
        if (selectedPlayers.get(position).isFan()) {
            Toast.makeText(getActivity(), "Fanouškovi nelze nastavit pokuty", Toast.LENGTH_LONG).show();
        }
        else {
            AddFineDialog addFineDialog = new AddFineDialog(selectedPlayers.get(position), (Match) model);
            addFineDialog.setTargetFragment(getTargetFragment(), 1);
            addFineDialog.show(getFragmentManager(), "dialogplus");
        }
    }
}
