package com.jumbo.trus.match.add;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.FadeAnimation;
import com.jumbo.trus.R;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.MatchHelperFragment;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.season.Season;

import java.util.List;


public class MatchPlusFragment extends MatchHelperFragment {

    private static final String TAG = "MatchPlusFragment";


    private TextView tv_interest;
    private Button btn_commit, btn_refuse;

    private MatchPlusViewModel matchPlusViewModel;

    private PkflMatch pkflMatch;

    private FadeAnimation fadeAnimation;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swHome.setChecked(false);
        btnCommit.setText("Potvrdit");
        btnDelete.setVisibility(View.GONE);
        tv_interest = view.findViewById(R.id.tv_interest);
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_refuse = view.findViewById(R.id.btn_refuse);
        fadeAnimation = new FadeAnimation(info_toolbar);
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMatchTextFromPkfl();
                showPkflToolbar(false);
            }
        });
        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPkflToolbar(false);
            }
        });
        matchPlusViewModel = new ViewModelProvider(requireActivity()).get(MatchPlusViewModel.class);
        matchPlusViewModel.init();
        matchPlusViewModel.getPkflMatch().observe(getViewLifecycleOwner(), new Observer<PkflMatch>() {
            @Override
            public void onChanged(PkflMatch match) {
                pkflMatch = match;
                checkPkflMatch();
            }
        });
        matchPlusViewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasonList) {
                tvSeason.setText(seasonList.get(0).getName());
                setupSeasonDropDownMenu(seasonList);
            }
        });
        matchPlusViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProgressBar();
                }
                else {
                    hideProgressBar();
                }
            }
        });
        matchPlusViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        matchPlusViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
        matchPlusViewModel.getNewMainMatch().observe(getViewLifecycleOwner(), new Observer<Match>() {
            @Override
            public void onChanged(Match match) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    sharedViewModel.setMainMatch(match);
                }
            }
        });
        tvSeason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                matchPlusViewModel.setCheckedSeason(seasonArrayAdapter.getItem(i));
            }
        });
        textPlayers.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPlayersDialog(matchPlusViewModel.getCheckedPlayers().getValue(), matchPlusViewModel.getPlayers().getValue(), matchPlusViewModel);
            }
        });
        tvPlayers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayPlayersDialog(matchPlusViewModel.getCheckedPlayers().getValue(), matchPlusViewModel.getPlayers().getValue(), matchPlusViewModel);
                }
            }
        });

    }

    private void showPkflToolbar(boolean show) {
        if (show) {
            fadeAnimation.fadeInAnimation();
        }
        else {
            fadeAnimation.fadeOutAnimation();
        }
    }
    private void checkPkflMatch() {
        if (pkflMatch != null) {
            String tvText = "Chcete načíst z PKFL posledně hraný zápas z " + pkflMatch.getDateAndTimeOfMatchInStringFormat() + " ?";
            tv_interest.setText(tvText);
            showPkflToolbar(true);
        }
    }

    private void setMatchTextFromPkfl() {
        textOpponent.getEditText().setText(pkflMatch.getOpponent());
        swHome.setChecked(pkflMatch.isHomeMatch());
        textCalendar.getEditText().setText(pkflMatch.getDateOfMatchInStringFormat());
    }

    @Override
    protected void onCommitValidationTrue(String opponent, boolean homeMatch, String date) {
        matchPlusViewModel.addMatchToRepository(opponent, homeMatch, date, user);
    }

    @Override
    protected void commitClicked() {
        String name = textOpponent.getEditText().getText().toString();
        String date = textCalendar.getEditText().getText().toString();
        if (checkFieldsValidation(name, date, matchPlusViewModel.getCheckedPlayers().getValue())) {
            onCommitValidationTrue(name, swHome.isChecked(), date);
        }
    }
}
