package com.jumbo.trus.playerlist.beer;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.Result;
import com.jumbo.trus.SharedViewModel;
import com.jumbo.trus.adapters.array.MatchArrayAdapter;
import com.jumbo.trus.comparator.OrderByBeerThenName;
import com.jumbo.trus.layout.BeerLayout;
import com.jumbo.trus.layout.OnLineFinishedListener;
import com.jumbo.trus.listener.OnPlusButtonListener;
import com.jumbo.trus.listener.OnSwipeTouchListener;
import com.jumbo.trus.match.Compensation;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class BeerFragment extends CustomUserFragment implements OnPlusButtonListener, OnLineFinishedListener, View.OnClickListener {

    private static final String TAG = "BeerFragment";

    //widgety
    private Button btn_commit;
    private TextView tv_title;
    private AutoCompleteTextView tvMatch;
    private BeerLayout beer_layout;
    private ImageButton btn_back, btn_forward;
    private TextInputLayout textMatch;
    private ProgressBar progress_bar;


    //vars
    private List<Match> selectedMatches;
    private List<Player> selectedPlayers;
    private Player player;
    private int selectedPlayersSize;
    private int selectedPlayer;
    private boolean commit;
    private boolean lineDrawed; //příznak pomocí kterého poznáme, zda se již nakreslila čátka v layoutu. Defaultně true, pak vrací inteface hodnotu
    private boolean beerDraw; //switch, podle kterýho rozlišujem jestli kreslit kořalky nebo piva
    private Compensation matchCompensation;
    private SharedViewModel sharedViewModel;

    private BeerViewModel beerViewModel;
    private Match match;

    private MatchArrayAdapter matchArrayAdapter;


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_beer, container, false);
        //nechápu proč se onCreateView volá když nikdo ten fragment nikdo nevolá. Pak to nemá bundle a padá to... musim jak čůrák sem napřímo odkázat na zápas z mainActivity. Možná je to společným předkem
        commit = false;
        Log.d(TAG, "onCreateView: " + match);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        beerViewModel = new ViewModelProvider(requireActivity()).get(BeerViewModel.class);
        beerViewModel.init();
        match = sharedViewModel.getMainMatch().getValue();
        sharedViewModel.getMainMatch().observe(getViewLifecycleOwner(), new Observer<Match>() {
            @Override
            public void onChanged(Match mainMatch) {
                Log.d(TAG, "načet se hlaní zápas" );
                if (mainMatch != null && match == null) {
                    match = mainMatch;
                }
                else {
                    Log.d(TAG, "načtení zápasu: nenašel se uložený zápas ve viewmodelu " );
                }
            }
        });
        progress_bar = view.findViewById(R.id.progress_bar);
        btn_commit = view.findViewById(R.id.btn_commit);
        tv_title = view.findViewById(R.id.tv_title);
        beer_layout = view.findViewById(R.id.beer_layout);
        textMatch = view.findViewById(R.id.textMatch);
        tvMatch = view.findViewById(R.id.tvMatch);
        btn_back = view.findViewById(R.id.btn_back);
        btn_forward = view.findViewById(R.id.btn_forward);

        textMatch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onClick: " + tvMatch.getAdapter());
                tvMatch.showDropDown();
                return false;
            }
        });
        beerViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: načetly se zápasy " + matches);
                if (match == null) {
                    sharedViewModel.findLastMatch(matches);

                }
                if (commit) {
                    showProgressBar(false);
                    sharedViewModel.updateMainMatch(matches);
                    openPreviousFragment();
                    commit = false;
                }
                else {
                    reloadMatch(matches);
                    setupSeasonDropDownMenu();
                }
            }
        });
        tvMatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: kliknuto na pozici" + i);
                match = beerViewModel.getMatches().getValue().get(i);
                initVariablesFromMatch();
                beer_layout.reloadPlayers(selectedPlayers);
                beer_layout.drawBeers(player);
            }
        });
        initVariablesFromMatch();
        btn_commit.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        beer_layout.attachListener(this);
        beer_layout.loadPlayers(selectedPlayers);
        beer_layout.drawBeers(player);
        initSwipeListener(view);
        beerViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    /**
     * Pokud se z db načte aktuálně zobrazený zápas (= někdo změnil zápas na jiném telefonu), tak se načtou aktuální čárky a zobrazí toast
     * @param matches nově načtené zápasy
     */
    private void reloadMatch(List<Match> matches) {
        for (Match loadedMatch : matches) {
            if (loadedMatch.equals(match)) {
                String compareResult = loadedMatch.compareIfMatchWasChanged(matchCompensation.getBeerCompensation(), matchCompensation.getLiquorCompensation(), matchCompensation.getFinesCompesation(), match);
                if (compareResult != null) {
                    match = loadedMatch;
                    Log.d(TAG, "reloadMatch: ");
                    initVariablesFromMatch();
                    if (beer_layout.reloadPlayers(selectedPlayers)) { //nutno ošetřit podmínkou, protože se zápasy načítali ještě před onMeasure v layoutu
                        beer_layout.drawBeers(player);
                        if (isResumed()) { //pokud je fragment právě zobrazenej
                            Toast.makeText(getActivity(), compareResult, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        if (!commit) {
            matchCompensation.setOriginalBeerAndLiquorNumber();
            //returnOriginalBeerAndLiquorNumber();
        }
    }

    /**
     * nastaví a nasetuje adapter pro autocompletetextview. Zároveň nastaví text dle načteného zápasu. Nutno volat až po načtení zápasů
     */
    private void setupSeasonDropDownMenu() {
        selectedMatches = new ArrayList<>();
        if (beerViewModel.getMatches().getValue() != null) {
            selectedMatches.addAll(beerViewModel.getMatches().getValue());
            Log.d(TAG, "setupSeasonDropDownMenu: " + selectedMatches.size());
        }
        matchArrayAdapter = new MatchArrayAdapter(getActivity(),  selectedMatches);
        tvMatch.setText(match.toStringNameWithOpponent());
        tvMatch.setAdapter(matchArrayAdapter);
        tvMatch.dismissDropDown();
    }

    /**
     * vezme zápas a vytěží z něj proměný
     */
    private void initVariablesFromMatch() {
        selectedPlayers = match.returnPlayerListOnlyWithParticipants();
        Collections.sort(selectedPlayers, new OrderByBeerThenName());
        selectedPlayersSize = selectedPlayers.size();
        player = selectedPlayers.get(0);
        selectedPlayer = 0;
        beerDraw = true;
        matchCompensation = new Compensation(match);
        matchCompensation.initBeerAndLiquorCompensation();
        matchCompensation.initFineCompensation();
        setPlayerTitle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                showProgressBar(true);
                Result result = beerViewModel.editMatchBeers(selectedPlayers, match);
                if (result.isTrue()) {
                    commit = true;
                    Notification notification = new Notification(match, selectedPlayers, matchCompensation.getBeerCompensation(), matchCompensation.getLiquorCompensation());
                    notification.setUser(user);
                    beerViewModel.sendNotificationToRepository(notification);
                    openPreviousFragment();
                }
                else {
                    showProgressBar(false);
                }
                break;
            case R.id.btn_back:
                if (lineDrawed) {
                    setPreviousPlayer();
                }
                break;

            case R.id.btn_forward:
                if (lineDrawed) {
                    setNextPlayer();
                }
                break;

        }
    }


    private void setNextPlayer() {
        Log.d(TAG, "setNextPlayer: selectedPlayer: " + selectedPlayer + ", size: " + selectedPlayersSize);
        if (!(selectedPlayer == selectedPlayersSize-1)) {
            selectedPlayer++;
        }
        else {
            selectedPlayer = 0;
        }
        player = selectedPlayers.get(selectedPlayer);
        setPlayerTitle();
        beer_layout.drawBeers(player);
    }

    private void setPreviousPlayer() {
        if (selectedPlayer > 0) {
            selectedPlayer--;
        }
        else {
            selectedPlayer = selectedPlayersSize-1;
        }
        player = selectedPlayers.get(selectedPlayer);
        setPlayerTitle();
        beer_layout.drawBeers(player);
    }

    private void setPlayerTitle() {
        tv_title.setText(player.getName());
    }

    private void showProgressBar(boolean show) {
        if (show) {
            progress_bar.setVisibility(View.VISIBLE);
        }
        else {
            progress_bar.setVisibility(View.GONE);
        }
    }


    private void initSwipeListener(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Log.d(TAG, "onSwipeTop: " + lineDrawed);
                if (beerDraw) {
                    if (lineDrawed) {
                        lineDrawed = false;
                        player.removeBeer();
                        beer_layout.removeBeer(player);
                    }
                }
                else {
                    if (lineDrawed) {
                        lineDrawed = false;
                        player.removeLiquor();
                        beer_layout.removeLiquor(player);
                    }
                }
            }
            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight: ");
                if (lineDrawed) {
                    setPreviousPlayer();
                    beerDraw = true;
                }
            }
            public void onSwipeLeft() {
                Log.d(TAG, "onSwipeLeft: ");
                if (lineDrawed) {
                    setNextPlayer();
                    beerDraw = true;
                }
            }
            public void onSwipeBottom() {
                Log.d(TAG, "onSwipeBottom: ");
                if (beerDraw) {
                    if (player.getNumberOfBeers() < BeerLayout.BEER_LIMIT) {
                        btn_back.setVisibility(View.GONE);
                        btn_forward.setVisibility(View.GONE);
                        if (lineDrawed) {
                            lineDrawed = false;
                            player.addBeer();
                            beer_layout.addBeer(player);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Víc jak " + BeerLayout.BEER_LIMIT + " se nedá zadat, tolik si stejně neměl", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (player.getNumberOfLiquors() < BeerLayout.LIQUOR_LIMIT) {
                        btn_back.setVisibility(View.GONE);
                        btn_forward.setVisibility(View.GONE);
                        if (lineDrawed) {
                            lineDrawed = false;
                            player.addLiquor();
                            beer_layout.addLiquor(player);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Víc jak " + BeerLayout.LIQUOR_LIMIT + " kořalek se nedá zadat, tolik si stejně neměl", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onLongClick() {
                if (beerDraw) {
                    beerDraw = false;
                    beer_layout.drawLiquorText(player);
                    Toast.makeText(getActivity(), "Přitvrdíme", Toast.LENGTH_SHORT).show();
                }
                else {
                    beerDraw = true;
                    if (player.getNumberOfLiquors() == 0) {
                        beer_layout.removeLiquorText(player);
                    }
                    Toast.makeText(getActivity(), "Zpátky k pivku", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onLongClick: ");
            }
        });
    }

    @Override
    public void onPlusClick(int position) {
        selectedPlayers.get(position).addBeer();
    }

    @Override
    public void onMinusClick(int position) {
        selectedPlayers.get(position).removeBeer();
    }

    /**
     * @param finished vrací false, pokud započal proces kreslení čárky
     *                 vrací true pokud skončil proces kreslení čárky
     */
    @Override
    public void drawFinished(boolean finished) {
        Log.d(TAG, "drawFinished: " + finished);
        lineDrawed = finished;
        if (finished) {
            btn_back.setVisibility(View.VISIBLE);
            btn_forward.setVisibility(View.VISIBLE);
        }
    }
}
