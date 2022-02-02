package com.jumbo.trus.statistics.match.fine.detail;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.StringTitleAndTextRecycleViewAdapter;
import com.jumbo.trus.statistics.match.beer.detail.BeerMatchStatisticsDetailViewModel;
import com.jumbo.trus.statistics.player.ListTexts;

import java.util.List;

public class FineMatchStatisticsDetailFragment extends CustomUserFragment {

    private static final String TAG = "FineMatchStatisticsDetailFragment";

    //widgety
    private RecyclerView rc_list;
    private AutoCompleteTextView tvStatisticsDetail;

    //vars
    private StringTitleAndTextRecycleViewAdapter adapter;

    private FineMatchStatisticsDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_detail, container, false);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(requireActivity()));
        tvStatisticsDetail = view.findViewById(R.id.tvStatisticsDetail);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FineMatchStatisticsDetailViewModel.class);
        viewModel.init(sharedViewModel.getPickedMatchForEdit().getValue());
        viewModel.getTextPlayersList().observe(getViewLifecycleOwner(), new Observer<List<ListTexts>>() {
            @Override
            public void onChanged(List<ListTexts> texts) {
                initRecycleView(texts);
                setAdapter();
            }
        });
        viewModel.getTitleText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvStatisticsDetail.setText(s);
            }
        });
    }

    private void initRecycleView(List<ListTexts> texts) {
        adapter = new StringTitleAndTextRecycleViewAdapter(texts, getActivity(), this);
    }

    private void setAdapter() {
        rc_list.setAdapter(adapter);
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onItemClick(int position) {

    }

}
