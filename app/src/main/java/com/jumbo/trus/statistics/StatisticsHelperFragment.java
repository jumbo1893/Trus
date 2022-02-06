package com.jumbo.trus.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.array.SeasonArrayAdapter;
import com.jumbo.trus.season.Season;

import java.util.List;

public class StatisticsHelperFragment extends CustomUserFragment {

    private static final String TAG = "StatisticsHelperFragment";

    protected RecyclerView rc_list;
    protected ImageButton btnOrder;
    protected ProgressBar progress_bar;
    protected AutoCompleteTextView tvSeason;
    protected TextInputLayout textSeason, textSearch;
    protected TextInputEditText tvSearch;

    protected SeasonArrayAdapter seasonArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        rc_list = view.findViewById(R.id.rc_list);
        btnOrder = view.findViewById(R.id.btnOrder);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(requireActivity()));
        textSeason = view.findViewById(R.id.textSeason);
        tvSeason = view.findViewById(R.id.tvSeason);
        tvSearch = view.findViewById(R.id.tvSearch);
        textSearch = view.findViewById(R.id.textSearch);
        progress_bar = view.findViewById(R.id.progress_bar);
        return view;
    }

    protected void setupSeasonDropDownMenu(List<Season> seasonList) {
        if (seasonList != null) {
            seasonArrayAdapter = new SeasonArrayAdapter(getActivity(), seasonList);
            tvSeason.setAdapter(seasonArrayAdapter);
            tvSeason.dismissDropDown();
        }
    }

    protected void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }
}
