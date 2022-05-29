package com.jumbo.trus.pkfl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;

import java.util.List;

public class LoadedMatchesFragment extends CustomUserFragment {

    private static final String TAG = "LoadedMatchesFragment";

    private RecyclerView rc_pkfl_matches;
    private ProgressBar progress_bar;
    private FloatingActionButton fab_plus;

    private PkflViewModel pkflViewModel;

    private SimpleRecycleViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pkfl, container, false);
        rc_pkfl_matches = view.findViewById(R.id.rc_pkfl_matches);
        rc_pkfl_matches.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        fab_plus = view.findViewById(R.id.fab_plus);
        pkflViewModel = new ViewModelProvider(requireActivity()).get(PkflViewModel.class);
        pkflViewModel.init();
        pkflViewModel.loadMatchesFromPkfl();
        pkflViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showItem(progress_bar);
                }
                else {
                    hideItem(progress_bar);
                }
            }
        });
        pkflViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        pkflViewModel.getMatchDetail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    showDialog(s);
                }
            }
        });
        pkflViewModel.getmatchSecondDetail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    showDialogDetail(s);
                }
            }
        });
        pkflViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<PkflMatch>>() {
            @Override
            public void onChanged(List<PkflMatch> matches) {
                initRecycleView();
                setAdapter();
                adapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });


        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pkflViewModel.loadMatchesFromPkfl();
            }
        });

        return view;
    }

    private void showItem(View button) {
        button.setVisibility(View.VISIBLE);
    }

    private void hideItem(View button) {
        button.setVisibility(View.GONE);
    }



    private void initRecycleView() {
        adapter = new SimpleRecycleViewAdapter(pkflViewModel.getMatches().getValue(), getActivity(), this);
    }

    private void setAdapter() {
        rc_pkfl_matches.setAdapter(adapter);
        rc_pkfl_matches.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showDialog(String message) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(pkflViewModel.getMatchName());
        if (pkflViewModel.isDetailEnabled()) {
            alert.setNeutralButton("detail", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    pkflViewModel.setMatchSecondDetail();
                }
            });
        }
        alert.setMessage(message);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d(TAG, "onClick: positive");
            }
        });
        alert.show();
    }

    private void showDialogDetail(String message) {
        Log.d(TAG, "showDialogDetail: ");
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(pkflViewModel.getMatchName());
        alert.setNeutralButton("zpět", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pkflViewModel.setMatchDetailData();
            }
        });
        alert.setMessage(message);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d(TAG, "onClick: positive");
            }
        });
        alert.show();
    }

    @Override
    protected void itemClick(final int position) {
        pkflViewModel.setPickedPkflMatch(position);
    }
}
