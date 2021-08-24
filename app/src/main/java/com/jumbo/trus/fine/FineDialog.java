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
import com.jumbo.trus.season.ISeasonFragment;
import com.jumbo.trus.season.Season;

public class FineDialog extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "FineDialog";

    //widgety
    private TextView tv_headline;
    private EditText et_name, et_amount;
    private Button btn_commit, btn_cancel, btn_delete;
    private Switch sw_player, sw_otherplayer, sw_nonplayer;
    private Fine.Type type = Fine.Type.PLAYER;

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
        sw_otherplayer = view.findViewById(R.id.sw_otherplayer);
        sw_nonplayer = view.findViewById(R.id.sw_nonplayer);
        sw_player.setChecked(true);
        decideTextsToShow();

        btn_commit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        sw_player.setOnCheckedChangeListener(this);
        sw_otherplayer.setOnCheckedChangeListener(this);
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
        setSwitchType(((Fine) model).getType());
    }

    private void setSwitchType(Fine.Type type) {
        switch (type) {
            case PLAYER: {
                sw_player.setChecked(true);
                sw_otherplayer.setChecked(false);
                sw_nonplayer.setChecked(false);
                this.type = Fine.Type.PLAYER;
                break;
            }
            case OTHER_PLAYERS: {
                sw_otherplayer.setChecked(true);
                sw_player.setChecked(false);
                sw_nonplayer.setChecked(false);
                this.type = Fine.Type.OTHER_PLAYERS;
                break;
            }
            case NONPLAYERS: {
                sw_nonplayer.setChecked(true);
                sw_otherplayer.setChecked(false);
                sw_player.setChecked(false);
                this.type = Fine.Type.NONPLAYERS;
                break;
            }
            default:
                sw_nonplayer.setChecked(false);
                sw_otherplayer.setChecked(false);
                sw_player.setChecked(false);
                this.type = Fine.Type.PLAYER;
                break;
        }
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
                        if (iFineFragment.createNewFine(name, amount, type)) {
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
                        if (iFineFragment.editFine(name, amount, type, (Fine) model)) {
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
            break;
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
                    Log.d(TAG, "onCheckedChanged: sw_player" );
                    sw_otherplayer.setChecked(false);
                    sw_nonplayer.setChecked(false);
                    type = Fine.Type.PLAYER;
                    break;
                }
                case R.id.sw_otherplayer: {
                    Log.d(TAG, "onCheckedChanged: sw_otherplayer" );
                    sw_player.setChecked(false);
                    sw_nonplayer.setChecked(false);
                    type = Fine.Type.OTHER_PLAYERS;
                    break;
                }
                case R.id.sw_nonplayer: {
                    Log.d(TAG, "onCheckedChanged: sw_nonplayer" );
                    sw_otherplayer.setChecked(false);
                    sw_player.setChecked(false);
                    type = Fine.Type.NONPLAYERS;
                    break;
                }
            }
        }
    }
}
