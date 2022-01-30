package com.jumbo.trus.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.BuildConfig;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.listener.OnSwipeTouchListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.user.LoginActivity;

public class HomeFragment extends CustomUserFragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private TextView tvBirthday, tvRandomFact, tvPkflMatch;
    private ImageView btnRandomFact;
    private ProgressBar progressBar;

    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        tvRandomFact = view.findViewById(R.id.tvRandomFact);
        progressBar = view.findViewById(R.id.progress_bar);
        tvPkflMatch = view.findViewById(R.id.tvPkflMatch);
        btnRandomFact = view.findViewById(R.id.btnRandomFact);
        btnRandomFact.setOnClickListener(this);
        tvRandomFact.setOnClickListener(this);
        showProgressBar();

        initSwipeListener(tvRandomFact);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.init();
        homeViewModel.getRandomFact().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvRandomFact.setText(s);
            }
        });
        homeViewModel.getPlayerBirthday().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvBirthday.setText(s);
            }
        });
        homeViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        homeViewModel.getPkflMatch().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
               tvPkflMatch.setText(s);

            }
        });
        homeViewModel.getLastMainMatch().observe(getViewLifecycleOwner(), new Observer<Match>() {
            @Override
            public void onChanged(Match match) {
                sharedViewModel.setMainMatch(match);
                hideProgressBar();
            }
        });
    }

    private void initSwipeListener(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight: ");
               homeViewModel.setNextRandomFact(false);
            }
            public void onSwipeLeft() {
                Log.d(TAG, "onSwipeLeft: ");
                homeViewModel.setNextRandomFact(true);
            }
        });
    }

    private void startRefreshNavigation(final ImageView imageView) {
        Animation anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.rotate);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                homeViewModel.randomlySetNewFact();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(anim);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRandomFact:
                startRefreshNavigation((ImageView) v);
                break;
        }
    }
}
