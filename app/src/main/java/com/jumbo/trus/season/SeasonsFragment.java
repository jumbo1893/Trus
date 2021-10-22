package com.jumbo.trus.season;

import android.os.Bundle;
import android.os.Handler;
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
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.notification.Notification;

import java.util.List;

public class SeasonsFragment extends CustomUserFragment implements OnListListener, ISeasonFragment {

    private static final String TAG = "SeasonsFragment";

    private RecyclerView rc_seasons;
    private ProgressBar progress_bar;
    private FloatingActionButton fab_plus;

    private SeasonsViewModel seasonsViewModel;
    private MatchViewModel matchViewModel;

    private SeasonsRecycleViewAdapter seasonsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seasons, container, false);
        rc_seasons = view.findViewById(R.id.rc_seasons);
        rc_seasons.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        progress_bar = view.findViewById(R.id.progress_bar);
        fab_plus = view.findViewById(R.id.fab_plus);
        initMainActivityViewModel();
        matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.init();
        seasonsViewModel = new ViewModelProvider(getActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        Log.d(TAG, "onCreateView: ");

        seasonsViewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasons) {
                Log.d(TAG, "onChanged: nacetly se sezony " + seasons);
                initSeasonsRecycleView();
                setAdapter();
                seasonsAdapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });

        seasonsViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        seasonsViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
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
                SeasonDialog seasonDialog = new SeasonDialog(Flag.SEASON_PLUS);
                seasonDialog.setTargetFragment(SeasonsFragment.this, 1);
                seasonDialog.show(getParentFragmentManager(), "dialogplus");
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



    private void initSeasonsRecycleView() {
        seasonsAdapter = new SeasonsRecycleViewAdapter(seasonsViewModel.getSeasons().getValue(), getActivity(), this);
    }

    private void setAdapter() {
        rc_seasons.setAdapter(seasonsAdapter);
        rc_seasons.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onItemClick(int position) {
        SeasonDialog dialog = new SeasonDialog(Flag.SEASON_EDIT, seasonsViewModel.getSeasons().getValue().get(position));
        dialog.setTargetFragment(SeasonsFragment.this, 1);
        dialog.show(getParentFragmentManager(), "dialogplus");
    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public boolean createNewSeason(String name, String seasonStart, String seasonEnd) {

        Result result = seasonsViewModel.checkNewSeasonValidation(name, seasonStart, seasonEnd, null);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result addSeasonToRepositoryResult = seasonsViewModel.addSeasonToRepository(name, seasonStart, seasonEnd);
            Toast.makeText(getActivity(), addSeasonToRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
            if (addSeasonToRepositoryResult.isTrue()) {
                String text = "se začátkem " + seasonStart + " a koncem " + seasonEnd;
                createNotification(new Notification("Přidána sezona " + name, text), seasonsViewModel);
            }
        }
        return result.isTrue();
    }

    @Override
    public boolean editSeason(String name, String seasonStart, String seasonEnd, Season season) {
        Result result = seasonsViewModel.checkNewSeasonValidation(name, seasonStart, seasonEnd, season);
        if (!result.isTrue()) {
            Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
        }
        else {
            Result editSeasonInRepositoryResult = seasonsViewModel.editSeasonInRepository(name, seasonStart, seasonEnd, season);
            Toast.makeText(getActivity(), editSeasonInRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
            if (editSeasonInRepositoryResult.isTrue()) {
                String text = "se začátkem " + seasonStart + " a koncem " + seasonEnd;
                createNotification(new Notification("Upravena sezona " + name, text), seasonsViewModel);
            }
        }
        return result.isTrue();
    }


    @Override
    public boolean deleteModel(Model model) {
        Result removeSeasonFromRepositoryResult = seasonsViewModel.removeSeasonFromRepository((Season)model);
        if (removeSeasonFromRepositoryResult.isTrue()) {
            Toast.makeText(getActivity(), removeSeasonFromRepositoryResult.getText(), Toast.LENGTH_SHORT).show();
            List <Season> seasonList = seasonsViewModel.getSeasons().getValue();
            seasonList.remove(model);
            final List<Match> matchList = matchViewModel.recalculateMatchSeason((Season) model, seasonList);
            String text = "se začátkem " + ((Season) model).getSeasonStartInStringFormat() + " a koncem " + ((Season) model).getSeasonEndInStringFormat();
            createNotification(new Notification("Smazána sezona " + model.getName(), text), seasonsViewModel);
            createNotification(new Notification().prepareNotificationAboutChangedSeasons(matchList), seasonsViewModel);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getActivity(), "Změněno " + matchList.size() + " sezon", Toast.LENGTH_SHORT).show();
                }
            }, 2000);
        }
        return removeSeasonFromRepositoryResult.isTrue();
    }
}
