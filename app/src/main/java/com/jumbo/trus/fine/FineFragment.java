package com.jumbo.trus.fine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.Flag;
import com.jumbo.trus.MainActivityViewModel;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.SimpleRecycleViewAdapter;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.user.User;

import java.util.List;

public class FineFragment extends CustomUserFragment implements OnListListener, IFineFragment {

    private static final String TAG = "FineFragment";


    private RecyclerView rc_fines;
    private ProgressBar progress_bar;
    private FloatingActionButton fab_plus;
    private FineViewModel fineViewModel;
    private SimpleRecycleViewAdapter finesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seasons, container, false);
        rc_fines = view.findViewById(R.id.rc_seasons);
        rc_fines.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        fab_plus = view.findViewById(R.id.fab_plus);
        initMainActivityViewModel();
        fineViewModel = new ViewModelProvider(getActivity()).get(FineViewModel.class);
        fineViewModel.init();
        //hideItem(rc_settings);
        Log.d(TAG, "onCreateView: ");

        fineViewModel.getFines().observe(getViewLifecycleOwner(), new Observer<List<Fine>>() {
            @Override
            public void onChanged(List<Fine> fines) {
                Log.d(TAG, "onChanged: nacetly se sezony " + fines);
                if (finesAdapter == null) {
                    initFinesRecycleView();
                }
                setAdapter();
                finesAdapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });

        fineViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        fineViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });



        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FineDialog fineDialog = new FineDialog(Flag.FINE_PLUS);
                fineDialog.setTargetFragment(FineFragment.this, 1);
                fineDialog.show(getFragmentManager(), "dialogplus");
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



    private void initFinesRecycleView() {
        finesAdapter = new SimpleRecycleViewAdapter(fineViewModel.getFines().getValue(), getActivity(), this);
    }

    private void setAdapter() {
        rc_fines.setAdapter(finesAdapter);
        rc_fines.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onItemClick(int position) {
        FineDialog dialog = new FineDialog(Flag.FINE_EDIT, fineViewModel.getFines().getValue().get(position));
        dialog.setTargetFragment(FineFragment.this, 1);
        dialog.show(getParentFragmentManager(), "dialogplus");
    }



    @Override
    public boolean createNewFine(String name, int amount, Fine.Type type) {
        Result result = fineViewModel.checkNewFineValidation(name, amount, null);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result addFineToRepositoryResult = fineViewModel.addFineToRepository(name, amount, type);
            Toast.makeText(getActivity(), addFineToRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
            if (addFineToRepositoryResult.isTrue()) {
                String text = "Byla vytvořena pokuta " + name + " s částkou " + amount + " Kč";
                createNotification(new Notification("Přidána pokuta " + name, text), fineViewModel);
            }
        }
        return result.isTrue();
    }

    @Override
    public boolean editFine(String name, int amount, Fine.Type type, Fine fine) {
        Result result = fineViewModel.checkNewFineValidation(name, amount, fine);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result editFineInRepositoryResult = fineViewModel.editFineInRepository(name, amount, type, fine);
            Toast.makeText(getActivity(), editFineInRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
            if (editFineInRepositoryResult.isTrue()) {
                String text = "Pokuta změněna na " + name + " s částkou " + amount + " Kč";
                createNotification(new Notification("Upravena pokuta " + fine.getName(), text), fineViewModel);
            }
        }
        return result.isTrue();
    }

    @Override
    public boolean deleteModel(Model model) {
        Result removeFineFromRepositoryResult = fineViewModel.removeFineFromRepository((Fine) model);
        Toast.makeText(getActivity(), removeFineFromRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
        if (removeFineFromRepositoryResult.isTrue()) {
            String text = "ve výši " + ((Fine) model).getAmount() + " Kč";
            createNotification(new Notification("Smazaná pokuta " + model.getName(), text), fineViewModel);
        }
        return removeFineFromRepositoryResult.isTrue();
    }
}
