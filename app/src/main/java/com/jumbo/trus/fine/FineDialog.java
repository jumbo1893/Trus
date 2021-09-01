package com.jumbo.trus.fine;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;

public class FineDialog extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "FineDialog";

    //widgety
    private TextView tv_headline;
    private EditText et_name, et_amount;
    private Button btn_commit, btn_cancel, btn_delete;
    private Switch sw_player, sw_nonplayer;
    private boolean forNonPlayers = false;

    //vars
    private IFineFragment iFineFragment;


    public FineDialog(Flag flag) {
        super(flag);
    }

    public FineDialog(Flag flag, Model season) {
        super(flag, season);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fine_plus, container, false);
        tv_headline = view.findViewById(R.id.tv_headline);
        et_name = view.findViewById(R.id.et_name);
        et_amount = view.findViewById(R.id.et_amount);
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        sw_player = view.findViewById(R.id.sw_player);
        sw_nonplayer = view.findViewById(R.id.sw_nonplayer);
        sw_player.setChecked(true);
        decideTextsToShow();

        btn_commit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        sw_player.setOnCheckedChangeListener(this);
        sw_nonplayer.setOnCheckedChangeListener(this);
        return view;
    }

    private void decideTextsToShow() {
        switch (flag) {
            case FINE_PLUS:
                setTextsToAddSeason();
                break;
            case FINE_EDIT:
                setTextsToEditSeason();
                break;
        }
    }

    private void setTextsToAddSeason() {
        tv_headline.setText("Nová pokuta");
        et_name.setText("");
        et_name.setHint("Název pokuty");
        //et_datum.setText("Přidat hráče");
        //btn_kalendar.setText("Přidat hráče");
        btn_commit.setText("Přidat pokutu");
        btn_delete.setVisibility(View.GONE);
    }

    private void setTextsToEditSeason() {
        tv_headline.setText("Úprava pokuty");
        et_name.setText(model.getName());
        et_amount.setText(String.valueOf(((Fine)model).getAmount()));
        btn_commit.setText("Upravit");
        btn_delete.setVisibility(View.VISIBLE);
        setSwitchType(((Fine) model).isForNonPlayers());
    }

    private void setSwitchType(boolean forNonPlayers) {
        if (forNonPlayers) {
            sw_player.setChecked(false);
            sw_nonplayer.setChecked(true);
        }
        else {
            sw_player.setChecked(true);
            sw_nonplayer.setChecked(false);
        }
        this.forNonPlayers = forNonPlayers;
    }

    private boolean checkNoButtonSwitched(CompoundButton buttonView) {
        if (!sw_player.isChecked() && !sw_nonplayer.isChecked()) {
            buttonView.setChecked(true);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (flag) {
            case FINE_PLUS: {
                switch (view.getId()) {
                    case R.id.btn_commit: {
                        Log.d(TAG, "onClick: kliknuti na přidat pokutu");
                        String name = et_name.getText().toString();
                        int amount;
                        if (!et_amount.getText().toString().isEmpty()) {
                            amount = Integer.parseInt(et_amount.getText().toString());
                        }
                        else {
                            amount = 0;
                        }
                        if (iFineFragment.createNewFine(name, amount, forNonPlayers)) {
                            getDialog().dismiss();
                        }
                        break;
                    }
                    case R.id.btn_cancel: {
                        getDialog().dismiss();
                        break;
                    }
                }
            }
            break;
            case FINE_EDIT: {
                switch (view.getId()) {
                    case R.id.btn_commit: {
                        Log.d(TAG, "onClick: kliknuti na editovat sezonu");
                        String name = et_name.getText().toString();
                        int amount;
                        if (!et_amount.getText().toString().isEmpty()) {
                            amount = Integer.parseInt(et_amount.getText().toString());
                        }
                        else {
                            amount = 0;
                        }
                        if (iFineFragment.editFine(name, amount, forNonPlayers, (Fine) model)) {
                            getDialog().dismiss();
                        }
                        break;
                    }
                    case R.id.btn_delete: {
                        Log.d(TAG, "onClick: kliknuti na smazat pokutu");
                        displayDeleteConfirmationDialog(model, iFineFragment, "Opravdu chceš smazat pokutu?", null);
                        break;
                    }
                    case R.id.btn_cancel: {
                        getDialog().dismiss();
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iFineFragment = (IFineFragment) getTargetFragment();
        }
        catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.sw_player: {
                    sw_nonplayer.setChecked(false);
                    forNonPlayers = false;
                    break;
                }
                case R.id.sw_nonplayer: {
                    sw_player.setChecked(false);
                    forNonPlayers = true;
                    break;
                }
            }
        } else {
            checkNoButtonSwitched(buttonView);
        }
    }
}
