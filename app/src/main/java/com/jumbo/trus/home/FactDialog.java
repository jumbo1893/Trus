package com.jumbo.trus.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.StringRecycleViewAdapter;

import java.util.List;

public class FactDialog extends DialogFragment implements View.OnClickListener, OnListListener {

    private static final String TAG = "FactDialog";

    //widgety
    private Button btn_commit;
    private RecyclerView rc_list;

    private List<String> factList;

    private StringRecycleViewAdapter stringRecycleViewAdapter;


    public FactDialog(List<String> factList) {
        this.factList = factList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fact, container, false);
        btn_commit = view.findViewById(R.id.btn_commit);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        btn_commit.setOnClickListener(this);
        initFinesRecycleView();
        setAdapter();
        return view;
    }


    private void initFinesRecycleView() {
        Log.d(TAG, "initFinesRecycleView: " + factList.size());
        stringRecycleViewAdapter = new StringRecycleViewAdapter(factList, getActivity(), this);
    }

    private void setAdapter() {
        if (stringRecycleViewAdapter != null) {
            rc_list.setAdapter(stringRecycleViewAdapter);
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
