package com.jumbo.trus.fine.playerlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.FadeAnimation;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.array.MatchArrayAdapter;
import com.jumbo.trus.adapters.recycleview.MultiRecycleViewAdapter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.List;

public class FinePlayersFragment extends CustomUserFragment implements View.OnClickListener {

    private static final String TAG = "FinePlayersFragment";

    //widgety
    private FloatingActionButton btn_init_multi_selection, btn_check_non_players, btn_cancel_multi_selection;
    private AutoCompleteTextView tvMatch;
    private TextInputLayout textMatch;
    private LinearLayout match_toolbar;


    //vars
    private MultiRecycleViewAdapter adapter;
    private RecyclerView rc_players;

    private FinePlayersViewModel finePlayersViewModel;
    private FadeAnimation fadeAnimation;

    private MatchArrayAdapter matchArrayAdapter;


    //animace tlačítek

    private Animation toBottom;
    private Animation rotateClose;
    private Animation rotateOpen;
    private Animation fromBottom;


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fine_players, container, false);
        match_toolbar = view.findViewById(R.id.match_toolbar);
        match_toolbar.setVisibility(View.GONE);
        rc_players = view.findViewById(R.id.rc_players);
        rc_players.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        textMatch = view.findViewById(R.id.textMatch);
        tvMatch = view.findViewById(R.id.tvMatch);
        btn_init_multi_selection = view.findViewById(R.id.btn_init_multi_selection);
        btn_check_non_players = view.findViewById(R.id.btn_check_non_players);
        btn_cancel_multi_selection = view.findViewById(R.id.btn_cancel_multi_selection);
        btn_cancel_multi_selection.setOnClickListener(this);
        btn_init_multi_selection.setOnClickListener(this);
        btn_check_non_players.setOnClickListener(this);
        setButtonsTransparency();
        textMatch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onClick: " + tvMatch.getAdapter());
                tvMatch.showDropDown();
                return false;
            }
        });
        tvMatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: kliknuto na pozici" + i);
                finePlayersViewModel.setPickedMatch(finePlayersViewModel.getMatches().getValue().get(i));
            }
        });
        showMultiButtons(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fadeAnimation = new FadeAnimation(match_toolbar);
        fadeAnimation.fadeInAnimation(100);
        super.onViewCreated(view, savedInstanceState);
        finePlayersViewModel = new ViewModelProvider(requireActivity()).get(FinePlayersViewModel.class);
        finePlayersViewModel.init();
        finePlayersViewModel.setPickedMatch(sharedViewModel.getMainMatch().getValue());
        finePlayersViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged: " + s);
                }
            }
        });

        finePlayersViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: načetly se zápasy " + matches);
                setupMatchDropDownMenu(matches);
            }
        });
        finePlayersViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> players) {
                if (players != null) {
                    initMultiRecycleView(players);
                    setAdapter();
                    Log.d(TAG, "onChanged: " + players );
                }
            }
        });

        finePlayersViewModel.getCheckedPlayers().observe(getViewLifecycleOwner(), new Observer<List<Boolean>>() {
            @Override
            public void onChanged(List<Boolean> players) {
                if (players != null) {
                    checkNonPlayers(players);
                }
            }
        });
        finePlayersViewModel.getTitleText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setupMatchDropDownMenu(finePlayersViewModel.getMatches().getValue());
            }
        });
    }

    @Override
    public void onDestroyView() {
        finePlayersViewModel.removeReg();
        super.onDestroyView();
    }

    /**
     * nastaví a nasetuje adapter pro autocompletetextview. Zároveň nastaví text dle načteného zápasu. Nutno volat až po načtení zápasů
     */
    private void setupMatchDropDownMenu(List<Match> selectedMatches) {
        matchArrayAdapter = new MatchArrayAdapter(getActivity(),  selectedMatches);
        tvMatch.setText(finePlayersViewModel.getTitleText().getValue());
        tvMatch.setAdapter(matchArrayAdapter);
        tvMatch.dismissDropDown();
    }

    private void showMultiButtons(boolean show) {
        Log.d(TAG, "showMultiButtons: " + show);
        if (show) {
            btn_cancel_multi_selection.setVisibility(View.VISIBLE);
            btn_init_multi_selection.setVisibility(View.VISIBLE);
            btn_check_non_players.setVisibility(View.VISIBLE);
        }
        else {
            btn_cancel_multi_selection.setVisibility(View.INVISIBLE);
            btn_init_multi_selection.setVisibility(View.INVISIBLE);
            btn_check_non_players.setVisibility(View.INVISIBLE);
        }
    }

    private void onMultiCheckClick(boolean show) {
        setButtonsAnimation(show);
        showMultiButtons(show);
        Log.d(TAG, "onMultiCheckClick: " + show);
    }

    private void setButtonsAnimation(final boolean show) {
        toBottom = AnimationUtils.loadAnimation(requireActivity(), R.anim.to_bottom_anim);
        rotateClose = AnimationUtils.loadAnimation(requireActivity(), R.anim.rotate_close_anim);
        rotateOpen = AnimationUtils.loadAnimation(requireActivity(), R.anim.rotate_open_anim);
        fromBottom = AnimationUtils.loadAnimation(requireActivity(), R.anim.from_bottom_anim);

        if (!show) {
            btn_check_non_players.startAnimation(toBottom);
            btn_init_multi_selection.startAnimation(toBottom);
            btn_cancel_multi_selection.startAnimation(rotateOpen);
        }
        else {
            btn_check_non_players.startAnimation(fromBottom);
            btn_init_multi_selection.startAnimation(fromBottom);
            btn_cancel_multi_selection.startAnimation(rotateClose);
        }
    }

    private List<Player> initCheckedPlayers() {
        List<Player> checkedPlayers = new ArrayList<>();
        for (int i = 0; i < finePlayersViewModel.getPlayers().getValue().size(); i++) {
            if (adapter.getCheckedPlayers().get(i)) {
                checkedPlayers.add(finePlayersViewModel.getPlayers().getValue().get(i));
            }
        }
        return checkedPlayers;
    }

    private void checkNonPlayers(List<Boolean> checkedPlayers) {
        for (int i = 0; i < checkedPlayers.size(); i++) {
            adapter.showCheckbox(i, checkedPlayers.get(i));
        }
    }

    private void initMultiRecycleView(List<Player> selectedPlayers) {
        Log.d(TAG, "initMultiRecycleView: " + selectedPlayers);
        adapter = new MultiRecycleViewAdapter(selectedPlayers, getActivity(), this);
    }

    private void setAdapter() {
        Log.d(TAG, "setAdapter: ");
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private boolean checkIfAtLeastOnePlayerIsChecked() {
        return adapter.getCheckedPlayers().contains(Boolean.TRUE);
    }

    private void setButtonsTransparency() {
        btn_check_non_players.setAlpha(0.75f);
        btn_init_multi_selection.setAlpha(0.75f);
        btn_cancel_multi_selection.setAlpha(0.75f);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_multi_selection: {
                adapter.showCheckboxes(false);
                adapter.notifyDataSetChanged();
                onMultiCheckClick(false);
                break;
            }
            case R.id.btn_check_non_players: {
                finePlayersViewModel.checkNonPlayers();
                adapter.notifyDataSetChanged();
                break;
            }
            case R.id.btn_init_multi_selection: {
                if (checkIfAtLeastOnePlayerIsChecked()) {
                    initCheckedPlayers();
                    sharedViewModel.setMultiplayers(true);
                    sharedViewModel.setPickedPlayersForEdit(initCheckedPlayers());
                    sharedViewModel.setPickedMatchForEdit(finePlayersViewModel.getPickedMatch());
                    proceedToNextFragment(16);
                }
                else {
                    Toast.makeText(getActivity(), "Musí být označenej alespoň jeden hráč", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        if (adapter.isCheckboxes()) {
            if (adapter.getCheckedPlayers().get(position)) {
                adapter.showCheckbox(position, false);
            }
            else {
                adapter.showCheckbox(position, true);
            }
            adapter.notifyDataSetChanged();
        }
        else {
            Log.d(TAG, "onItemClick: ");
            if (finePlayersViewModel.getPlayers().getValue().get(position).isFan()) {
                Toast.makeText(getActivity(), "Fanouškovi nelze nastavit pokuty", Toast.LENGTH_LONG).show();
            } else {
                sharedViewModel.setMultiplayers(false);
                sharedViewModel.setPickedMatchForEdit(finePlayersViewModel.getPickedMatch());
                sharedViewModel.setPickedPlayerForEdit(finePlayersViewModel.getPlayers().getValue().get(position));
                proceedToNextFragment(16);
            }
        }
    }


    @Override
    public void onItemLongClick(int position) {
        Log.d(TAG, "onItemLongClick: ");
        adapter.showCheckbox(position, true);
        onMultiCheckClick(true);
    }
}
