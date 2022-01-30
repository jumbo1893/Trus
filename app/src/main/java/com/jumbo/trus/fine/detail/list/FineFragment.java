package com.jumbo.trus.fine.detail.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.FineViewModel;
import com.jumbo.trus.fine.IFineFragment;
import com.jumbo.trus.notification.Notification;

import java.util.List;

public class FineFragment extends CustomUserFragment {

    private static final String TAG = "FineFragment";


    private RecyclerView rc_fines;
    private ProgressBar progress_bar;
    private FloatingActionButton fab_plus;
    private FineListViewModel fineListViewModel;
    private SimpleRecycleViewAdapter finesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seasons, container, false);
        rc_fines = view.findViewById(R.id.rc_seasons);
        rc_fines.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        fab_plus = view.findViewById(R.id.fab_plus);
        Log.d(TAG, "onCreateView: ");

        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToNextFragment(21);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fineListViewModel = new ViewModelProvider(getActivity()).get(FineListViewModel.class);
        fineListViewModel.init();
        fineListViewModel.getFines().observe(getViewLifecycleOwner(), new Observer<List<Fine>>() {
            @Override
            public void onChanged(List<Fine> fines) {
                Log.d(TAG, "onChanged: nacetly se sezony " + fines);
                initFinesRecycleView(fines);
                setAdapter();
            }
        });

        fineListViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        fineListViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showItem(View button) {
        button.setVisibility(View.VISIBLE);
    }

    private void hideItem(View button) {
        button.setVisibility(View.GONE);
    }



    private void initFinesRecycleView(List<Fine> fines) {
        finesAdapter = new SimpleRecycleViewAdapter(fines, getActivity(), this);
    }

    private void setAdapter() {
        rc_fines.setAdapter(finesAdapter);
        rc_fines.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void itemClick(int position) {
        sharedViewModel.setPickedFineForEdit(fineListViewModel.getFines().getValue().get(position));
        proceedToNextFragment(22);
    }
}
