package com.jumbo.trus.playerlist;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.OnPlusButtonListener;
import com.jumbo.trus.listener.OnSwipeTouchListener;
import com.jumbo.trus.R;
import com.jumbo.trus.comparator.OrderByBeerThenName;
import com.jumbo.trus.layout.BeerLayout;
import com.jumbo.trus.layout.OnLineFinishedListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class BeerDialog extends Dialog implements OnPlusButtonListener, OnLineFinishedListener, GestureDetector.OnGestureListener {

    private static final String TAG = "BeerDialog";

    //widgety
    private Button btn_cancel, btn_commit;
    private TextView tv_title;
    private BeerLayout beer_layout;
    private ImageButton btn_back, btn_forward;


    //vars
    private IChangePlayerListListener iChangePlayerListListener;
    private INotificationSender iNotificationSender;
    private List<Player> selectedPlayers;
    private List<Integer> beerCompensation;
    private Player player;
    private int selectedPlayersSize;
    private int selectedPlayer;
    private boolean commit = false;
    private boolean lineDrawed; //příznak pomocí kterého poznáme, zda se již nakreslila čátka v layoutu. Defaultně true, pak vrací inteface hodnotu


    public BeerDialog(Flag flag, Model model) {
        super(flag, model);
        selectedPlayers = ((Match) model).returnPlayerListOnlyWithParticipants();
        Collections.sort(selectedPlayers, new OrderByBeerThenName());
        selectedPlayersSize = selectedPlayers.size();
        player = selectedPlayers.get(0);
        selectedPlayer = 0;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_beer2, container, false);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_commit = view.findViewById(R.id.btn_commit);
        tv_title = view.findViewById(R.id.tv_title);
        beer_layout = view.findViewById(R.id.beer_layout);
        btn_back = view.findViewById(R.id.btn_back);
        btn_forward = view.findViewById(R.id.btn_forward);
        btn_cancel.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        setPlayerTitle();
        beer_layout.loadPlayers(selectedPlayers);
        beer_layout.attachListener(this);
        beer_layout.drawBeers(player);
        //lineDrawed = true;
        initBeerCompensation();
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Log.d(TAG, "onSwipeTop: " + lineDrawed);
                if (lineDrawed) {
                    lineDrawed = false;
                    player.removeBeer();
                    beer_layout.removeBeer(player);
                }
            }
            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight: ");
                if (lineDrawed) {
                    setPreviousPlayer();
                }

            }
            public void onSwipeLeft() {
                Log.d(TAG, "onSwipeLeft: ");
                if (lineDrawed) {
                    setNextPlayer();
                }
            }
            public void onSwipeBottom() {
                Log.d(TAG, "onSwipeBottom: ");
                if (player.getNumberOfBeers() < BeerLayout.BEER_LIMIT) {
                btn_back.setVisibility(View.GONE);
                btn_forward.setVisibility(View.GONE);
                    if (lineDrawed) {
                        lineDrawed = false;
                        player.addBeer();
                        beer_layout.addBeer(player);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Víc jak " + BeerLayout.BEER_LIMIT + " se nedá zadat, tolik si stejně neměl", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLongClick() {
                Log.d(TAG, "onLongClick: ");
                beer_layout.drawLiquorText(player);
            }
            
        });

        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (iChangePlayerListListener.editMatch(selectedPlayers, (Match) model)) {
                    commit = true;
                    getDialog().dismiss();
                    iNotificationSender.sendNotificationToRepository(new Notification((Match) model, selectedPlayers, beerCompensation));
                }
                break;
            case R.id.btn_cancel:
                Log.d(TAG, "onClick: kliknuti na smazat zápas " + selectedPlayers);
                getDialog().dismiss();
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

    /**
     * načte seznam piv v v původní nezměněné podobě
     */
    private void initBeerCompensation() {
        beerCompensation = new ArrayList<>();
        for (Player player : selectedPlayers) {
            beerCompensation.add(player.getNumberOfBeers());
        }
        Log.d(TAG, "initSelectedPlayers: " + selectedPlayers);
    }

    /**
     * vezme z listu počet piv načtených před přidáváním nových piv a nastaví je zpět
     */
    private void returnOriginalBeerNumber() {
        for (int i = 0; i < selectedPlayers.size(); i++) {
            selectedPlayers.get(i).setNumberOfBeers(beerCompensation.get(i));
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


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iChangePlayerListListener = (IChangePlayerListListener) getTargetFragment();
            iNotificationSender = (INotificationSender) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!commit) {
            returnOriginalBeerNumber();
        }
        super.onDismiss(dialog);
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

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
