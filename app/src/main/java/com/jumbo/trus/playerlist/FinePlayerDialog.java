package com.jumbo.trus.playerlist;



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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.MultiRecycleViewAdapter;
import com.jumbo.trus.comparator.OrderByNonPlayerThenName;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinePlayerDialog extends Dialog implements OnListListener {

    private static final String TAG = "FinePlayerDialog";

    //widgety
    private RecyclerView rc_players;
    private Button btn_commit, btn_init_multi_selection, btn_cancel_multi_selection, btn_check_non_players;
    private TextView tv_title;

    //vars
    private List<Player> selectedPlayers;
    private List<Player> checkedPlayers;
    private MultiRecycleViewAdapter adapter;


    public FinePlayerDialog(Model model) {
        super(model);
        selectedPlayers = ((Match) model).returnPlayerListWithoutFans();
        Collections.sort(selectedPlayers, new OrderByNonPlayerThenName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_fine_player, container, false);
        rc_players = view.findViewById(R.id.rc_players);
        rc_players.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_init_multi_selection = view.findViewById(R.id.btn_init_multi_selection);
        btn_cancel_multi_selection = view.findViewById(R.id.btn_cancel_multi_selection);
        btn_check_non_players = view.findViewById(R.id.btn_check_non_players);
        tv_title = view.findViewById(R.id.tv_title);
        btn_commit.setOnClickListener(this);
        btn_init_multi_selection.setOnClickListener(this);
        btn_cancel_multi_selection.setOnClickListener(this);
        btn_check_non_players.setOnClickListener(this);
        setTexts();
        showMultiButtons(false);

        initMultiRecycleView();
        setAdapter();
        return view;
    }


    private void setTexts() {
        tv_title.setText("Přidat pokuty");
    }

    private void showMultiButtons(boolean show) {
        if (show) {
            btn_cancel_multi_selection.setVisibility(View.VISIBLE);
            btn_init_multi_selection.setVisibility(View.VISIBLE);
            btn_check_non_players.setVisibility(View.VISIBLE);
        }
        else {
            btn_cancel_multi_selection.setVisibility(View.GONE);
            btn_init_multi_selection.setVisibility(View.GONE);
            btn_check_non_players.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                getDialog().dismiss();
                break;
            }
            case R.id.btn_cancel_multi_selection: {
                adapter.showCheckboxes(false);
                adapter.notifyDataSetChanged();
                showMultiButtons(false);
                break;
            }
            case R.id.btn_check_non_players: {
                checkNonPlayers();
                adapter.notifyDataSetChanged();
                break;
            }
            case R.id.btn_init_multi_selection: {
                if (checkIfAtLeastOnePlayerIsChecked()) {
                    initCheckedPlayers();
                    AddFineDialog addFineDialog = new AddFineDialog(model, checkedPlayers);
                    addFineDialog.setTargetFragment(getTargetFragment(), 1);
                    addFineDialog.show(getFragmentManager(), "dialogplus");
                }
                else {
                    Toast.makeText(getActivity(), "Musí být označenej alespoň jeden hráč", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void initCheckedPlayers() {
        checkedPlayers = new ArrayList<>();
        for (int i = 0; i < selectedPlayers.size(); i++) {
            if (adapter.getCheckedPlayers().get(i)) {
                checkedPlayers.add(selectedPlayers.get(i));
            }
        }
    }

    private void checkNonPlayers() {
        for (int i = 0; i < selectedPlayers.size(); i++) {
            if (!selectedPlayers.get(i).isMatchParticipant()) {
                adapter.showCheckbox(i, true);
            }
            else {
                adapter.showCheckbox(i, false);
            }
        }
    }

    private void initMultiRecycleView() {
        adapter = new MultiRecycleViewAdapter(selectedPlayers, getActivity(), this);
    }

    private void setAdapter() {
        Log.d(TAG, "setAdapter: ");
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private boolean checkIfAtLeastOnePlayerIsChecked() {
        if (adapter.getCheckedPlayers().contains(Boolean.TRUE)) {
            return true;
        }
        return false;
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
        if (adapter.isCheckboxes()) {
            if (adapter.getCheckedPlayers().get(position)) {
                adapter.showCheckbox(position, false);
            }
            else {
                adapter.showCheckbox(position, true);
            }
            adapter.notifyDataSetChanged();
        }
        else {
            Log.d(TAG, "onItemClick: ");
            if (selectedPlayers.get(position).isFan()) {
                Toast.makeText(getActivity(), "Fanouškovi nelze nastavit pokuty", Toast.LENGTH_LONG).show();
            } else {
                AddFineDialog addFineDialog = new AddFineDialog((Match) model, selectedPlayers.get(position));
                addFineDialog.setTargetFragment(getTargetFragment(), 1);
                addFineDialog.show(getFragmentManager(), "dialogplus");
            }
        }
    }

    @Override
    public void onItemLongClick(int position) {
        Log.d(TAG, "onItemLongClick: ");
        adapter.showCheckbox(position, true);
        showMultiButtons(true);
    }
}
