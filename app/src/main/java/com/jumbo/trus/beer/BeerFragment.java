package com.jumbo.trus.beer;


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
import com.jumbo.trus.adapters.array.MatchArrayAdapter;
import com.jumbo.trus.layout.BeerLayout;
import com.jumbo.trus.layout.OnChangedPlayerListener;
import com.jumbo.trus.layout.OnLineFinishedListener;
import com.jumbo.trus.listener.OnSwipeTouchListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;
import java.util.Objects;

/**
 *
 */
public class BeerFragment extends CustomUserFragment implements OnLineFinishedListener, OnChangedPlayerListener, View.OnClickListener {

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

    private boolean lineDrawed; //příznak pomocí kterého poznáme, zda se již nakreslila čátka v layoutu. Defaultně true, pak vrací inteface hodnotu

    private BeerViewModel beerViewModel;

    private MatchArrayAdapter matchArrayAdapter;


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beer, container, false);
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
                tvMatch.showDropDown();
                return false;
            }
        });
        tvMatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: kliknuto na pozici" + i);
                beerViewModel.setPickedMatch(Objects.requireNonNull(beerViewModel.getMatches().getValue()).get(i));
            }
        });
        btn_commit.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        beer_layout.attachOnLineFinishedListener(this);
        beer_layout.attachOnChangedPlayerListener(this);
        initSwipeListener(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beerViewModel = new ViewModelProvider(requireActivity()).get(BeerViewModel.class);
        beerViewModel.init();
        beerViewModel.setPickedMatch(sharedViewModel.getMainMatch().getValue());
        beerViewModel.getMatches().observe(getViewLifecycleOwner(), new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: " + matches);
                setupMatchDropDownMenu(matches);
            }
        });
        beerViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        beerViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> playerList) {
                Log.d(TAG, "onChanged: " + playerList);
                beer_layout.loadPlayers(playerList);
                setPlayerTitle(playerList.get(0));
            }
        });
        beerViewModel.getTitleText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setupMatchDropDownMenu(beerViewModel.getMatches().getValue());
            }
        });
        beerViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
        beerViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                showProgressBar(b);

            }
        });
    }

    private void setupMatchDropDownMenu(List<Match> selectedMatches) {
        Log.d(TAG, "setupMatchDropDownMenu: " + selectedMatches);
        matchArrayAdapter = new MatchArrayAdapter(requireActivity(), selectedMatches);
        tvMatch.setText(beerViewModel.getTitleText().getValue());
        tvMatch.setAdapter(matchArrayAdapter);
        tvMatch.dismissDropDown();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                Log.d(TAG, "onClick: beer");
                sharedViewModel.setMainMatch(beerViewModel.editMatchBeers(beer_layout.getPlayerList(), user));
                break;
            case R.id.btn_back:
                if (lineDrawed) {
                    beer_layout.setPreviousPlayer();
                }
                break;

            case R.id.btn_forward:
                if (lineDrawed) {
                    beer_layout.setNextPlayer();
                }
                break;
        }
    }

    private void setPlayerTitle(Player player) {
        tv_title.setText(player.getName());
    }

    private void showProgressBar(boolean show) {
        if (show) {
            progress_bar.setVisibility(View.VISIBLE);
        } else {
            progress_bar.setVisibility(View.GONE);
        }
    }


    private void initSwipeListener(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Log.d(TAG, "onSwipeTop: " + lineDrawed);
                if (lineDrawed) {
                    lineDrawed = false;
                    beer_layout.removeLine();
                }
            }

            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight: ");
                if (lineDrawed) {
                    beer_layout.setPreviousPlayer();
                }
            }

            public void onSwipeLeft() {
                Log.d(TAG, "onSwipeLeft: ");
                if (lineDrawed) {
                    beer_layout.setNextPlayer();
                }
            }

            public void onSwipeBottom() {
                Log.d(TAG, "onSwipeBottom: ");
                if (lineDrawed) {
                    lineDrawed = false;
                    btn_back.setVisibility(View.GONE);
                    btn_forward.setVisibility(View.GONE);
                    if (!beer_layout.addLine()) {
                        Toast.makeText(getActivity(), "Víc jak " + BeerLayout.BEER_LIMIT + " piv nebo " + BeerLayout.LIQUOR_LIMIT + " kořalek se nedá zadat", Toast.LENGTH_SHORT).show();
                        btn_back.setVisibility(View.VISIBLE);
                        btn_back.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onLongClick() {
                Toast.makeText(getActivity(), beer_layout.changeBooze(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onLongClick: ");
            }
        });
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

    @Override
    public void playerChanged(Player player) {
        setPlayerTitle(player);
    }
}
