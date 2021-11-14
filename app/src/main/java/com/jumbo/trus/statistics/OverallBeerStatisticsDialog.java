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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.SimpleStringRecycleViewAdapter;
import com.jumbo.trus.listener.OnListListener;

import java.util.List;

public class OverallBeerStatisticsDialog extends Dialog {

    private static final String TAG = "FineStatisticsDialog";

    //widgety
    private TextView tv_title;
    private Button btn_commit;
    private RecyclerView rc_list;

    private List<String> overallList;

    private SimpleStringRecycleViewAdapter stringRecycleViewAdapter;



    public OverallBeerStatisticsDialog(Flag flag, Model model, List<String> overallList) {
        super(flag, model);
        this.overallList = overallList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_statistics_overall, container, false);
        tv_title = view.findViewById(R.id.tv_title);
        btn_commit = view.findViewById(R.id.btn_commit);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        decideTextToShow();
        initRecycleView();
        setAdapter();
        btn_commit.setOnClickListener(this);

        return view;
    }

    private void decideTextToShow() {
        if (flag == Flag.BEER) {
            tv_title.setText("Celkovej chlast");
        }
        else {
            tv_title.setText("Celkový pokuty");
        }

    }


    private void initRecycleView() {
        Log.d(TAG, "initRecycleView: ");
        stringRecycleViewAdapter = new SimpleStringRecycleViewAdapter(overallList, getActivity());
    }


    private void setAdapter() {
        rc_list.setAdapter(stringRecycleViewAdapter);
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
}
