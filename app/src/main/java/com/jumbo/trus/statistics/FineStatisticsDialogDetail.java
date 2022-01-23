package com.jumbo.trus.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.FinesPlayerNumberRecycleViewAdapter;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;

public class FineStatisticsDialogDetail extends Dialog implements OnListListener {

    private static final String TAG = "FineStatisticsDialogDetail";

    //widgety
    private TextView tv_title;
    private Button btn_commit;
    private RecyclerView rc_list;

    private StatisticsViewModel statisticsViewModel;

    private List<ReceivedFine> receivedFines;
    private ReceivedFine receivedFine;

    private SimpleRecycleViewAdapter simpleRecycleViewAdapter;
    private FinesPlayerNumberRecycleViewAdapter finesPlayerNumberRecycleViewAdapter;


    public FineStatisticsDialogDetail(Flag flag, Model model) {
        super(flag, model);
    }
    public FineStatisticsDialogDetail(Flag flag, Model model, ReceivedFine receivedFine) {
        super(flag, model);
        this.receivedFine = receivedFine;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fine_statistics_detail, container, false);
        tv_title = view.findViewById(R.id.tv_title);
        btn_commit = view.findViewById(R.id.btn_commit);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        decideTextsToShow();
        btn_commit.setOnClickListener(this);
        setAdapter();

        return view;
    }

    private void decideTextsToShow() {
        if (flag == Flag.PLAYER) {
            tv_title.setText(model.getName());
            initFinesRecycleView();
        }
        else {
            tv_title.setText(receivedFine.getFine().getName());
            initPlayersRecycleView();
        }
    }


    private void initFinesRecycleView() {
        Log.d(TAG, "initRecycleView: ");
        receivedFines = ((Player) model).returnReceivedFineWithCount();
        simpleRecycleViewAdapter = new SimpleRecycleViewAdapter(receivedFines, getActivity(), this);
    }

    private void initPlayersRecycleView() {
        Log.d(TAG, "initRecycleView: ");
        finesPlayerNumberRecycleViewAdapter = new FinesPlayerNumberRecycleViewAdapter(((Match)model).returnPlayerListOnlyWithFine(receivedFine), receivedFine, getActivity(), this);
    }

    private void setAdapter() {
        if (simpleRecycleViewAdapter != null) {
            rc_list.setAdapter(simpleRecycleViewAdapter);
        }
        else if (finesPlayerNumberRecycleViewAdapter != null) {
            rc_list.setAdapter(finesPlayerNumberRecycleViewAdapter);
        }
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                getDialog().dismiss();
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        //dialog_fine_statistics_detail
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
