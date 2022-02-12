package com.jumbo.trus.fine.add;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.adapters.recycleview.FinesRecycleViewAdapter;
import com.jumbo.trus.adapters.recycleview.MultiFinesRecycleViewAdapter;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.listener.OnPlusButtonListener;

import java.util.List;

public class FineAddFragment extends CustomUserFragment implements OnPlusButtonListener, View.OnClickListener {

    private static final String TAG = "FineAddFragment";

    //widgety
    private RecyclerView rc_fines;
    private AppCompatButton btn_commit;

    private ProgressBar progress_bar;
    private AutoCompleteTextView tvMatch;

    //vars
    private FinesRecycleViewAdapter adapter;
    private MultiFinesRecycleViewAdapter multiAdapter;

    private FineAddViewModel fineAddViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fine_add_to_player, container, false);
        rc_fines = view.findViewById(R.id.rc_fines);
        progress_bar = view.findViewById(R.id.progress_bar);
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_commit.setVisibility(View.VISIBLE);
        tvMatch = view.findViewById(R.id.tvMatch);
        btn_commit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fineAddViewModel = new ViewModelProvider(requireActivity()).get(FineAddViewModel.class);
        fineAddViewModel.init(sharedViewModel.getPickedMatchForEdit().getValue(), sharedViewModel.getPickedPlayerForEdit().getValue(), sharedViewModel.getPickedPlayersForEdit().getValue(), sharedViewModel.isMultiplayers());
        fineAddViewModel.getFines().observe(getViewLifecycleOwner(), new Observer<List<ReceivedFine>>() {
            @Override
            public void onChanged(List<ReceivedFine> fines) {
                Log.d(TAG, "onChanged: nacetly se pokuty " + fines);
                setRecycleViews(fines);
            }
        });
        fineAddViewModel.getTitleText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvMatch.setText(s);
            }
        });
        fineAddViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        fineAddViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                showProgressBar(b);
            }
        });
        fineAddViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        fineAddViewModel.removeReg();
        super.onDestroyView();
    }

    private void setRecycleViews(List<ReceivedFine> fines) {
        if (!sharedViewModel.isMultiplayers()) {
            initRecycleView(fines);
            setAdapter();
            adapter.notifyDataSetChanged(); //TODO notifyItemInserted
        } else {
            initMultiRecycleView(fines);
            setMultiAdapter();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_commit) {
            Log.d(TAG, "onClick: fine");
            if (sharedViewModel.isMultiplayers()) {
                sharedViewModel.setMainMatch(fineAddViewModel.editMatchPlayersFines(multiAdapter.getFinesNumber(), user));
            } else {
                sharedViewModel.setMainMatch(fineAddViewModel.editMatchPlayersFines(adapter.getFinesNumber(), user));
            }
        }
    }

    private void initRecycleView(List<ReceivedFine> playerFines) {
        adapter = new FinesRecycleViewAdapter(playerFines, getActivity(), this);
    }

    private void initMultiRecycleView(List<ReceivedFine> fines) {
        multiAdapter = new MultiFinesRecycleViewAdapter(fines, getActivity());
    }

    private void setMultiAdapter() {
        rc_fines.setAdapter(multiAdapter);
        rc_fines.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setAdapter() {
        Log.d(TAG, "setAdapter: ");
        rc_fines.setAdapter(adapter);
        rc_fines.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    private void showProgressBar(boolean show) {
        if (show) {
            progress_bar.setVisibility(View.VISIBLE);
        } else {
            progress_bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlusClick(int position) {

    }

    @Override
    public void onMinusClick(int position) {

    }
}
