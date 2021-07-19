package com.jumbo.trus.player;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;


import java.util.Calendar;

public class PlayerDialog extends Dialog {

    private static final String TAG = "PlayerDialog";

    //widgety
    private TextView tv_nadpis;
    private EditText et_jmeno, et_datum;
    private Button btn_kalendar, btn_potvrdit, btn_smazat;
    private Switch sw_switch;

    //vars
    private IPlayerFragment iPlayerFragment;

    public PlayerDialog(Flag flag) {
        super(flag);
    }

    public PlayerDialog(Flag flag, Model model) {
        super(flag, model);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_player, container, false);
        tv_nadpis = view.findViewById(R.id.tv_nadpis);
        et_jmeno = view.findViewById(R.id.et_jmeno);
        et_datum = view.findViewById(R.id.et_datum);
        btn_kalendar = view.findViewById(R.id.btn_kalendar);
        btn_potvrdit = view.findViewById(R.id.btn_potvrdit);
        btn_smazat = view.findViewById(R.id.btn_smazat);
        sw_switch = view.findViewById(R.id.sw_switch);
        sw_switch.setChecked(false);
        decideTextsToShow();

        btn_potvrdit.setOnClickListener(this);
        btn_smazat.setOnClickListener(this);
        btn_kalendar.setOnClickListener(this);

        return view;
    }

    private void decideTextsToShow() {
        switch (flag) {
            case PLAYER_PLUS:
                setTextsToAddPlayer();
                break;
            case PLAYER_EDIT:
                setTextsToEditPlayer();
                break;
        }
    }

    private void setTextsToAddPlayer() {
        tv_nadpis.setText("Nový hráč");
        et_jmeno.setText("");
        et_jmeno.setHint("Jméno hráče");
        //et_datum.setText("Přidat hráče");
        //btn_kalendar.setText("Přidat hráče");
        btn_potvrdit.setText("Přidat hráče");
        btn_smazat.setVisibility(View.GONE);
        sw_switch.setText("fanoušek?");
    }

    private void setTextsToEditPlayer() {
        tv_nadpis.setText("Úprava hráče");
        et_jmeno.setText(model.getName());
        //et_jmeno.setHint("Jméno hráče");
        et_datum.setText(((Player)model).getBirthdayInStringFormat());
        btn_potvrdit.setText("Upravit hráče");
        btn_smazat.setVisibility(View.VISIBLE);
        sw_switch.setText("domácí?");
    }


    @Override
    public void onClick(View view) {
        switch (flag) {
            case PLAYER_PLUS: {
                switch (view.getId()) {
                    case R.id.btn_potvrdit: {
                        Log.d(TAG, "onClick: kliknuti na přidat hráče");
                        String name = et_jmeno.getText().toString();
                        String date = et_datum.getText().toString();
                        boolean fan = sw_switch.isChecked();
                        if (iPlayerFragment.createNewPlayer(name, date, fan)) {
                            getDialog().dismiss();
                        }
                        break;
                    }
                    case R.id.btn_kalendar: {
                        displayCalendarDialog(et_datum);
                    }
                    break;
                }
            }
            break;
            case PLAYER_EDIT: {
                switch (view.getId()) {
                    case R.id.btn_potvrdit: {
                        Log.d(TAG, "onClick player edit: " + iPlayerFragment);
                        Log.d(TAG, "onClick: kliknuti na upravit hráče");
                        String name = et_jmeno.getText().toString();
                        String date = et_datum.getText().toString();
                        boolean fan = sw_switch.isChecked();
                        if (iPlayerFragment.editPlayer(name, date, fan, (Player) model)) {
                            getDialog().dismiss();
                        }
                        break;
                    }
                    case R.id.btn_smazat: {
                        Log.d(TAG, "onClick: kliknuti na smazat hráče");
                        displayDeleteConfirmationDialog(model, iPlayerFragment, "Opravdu chceš smazat hráče?");
                        break;
                    }
                    case R.id.btn_kalendar: {
                        displayCalendarDialog(et_datum);
                    }
                    break;
                }
            }
            break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iPlayerFragment = (IPlayerFragment) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }


    }

}
