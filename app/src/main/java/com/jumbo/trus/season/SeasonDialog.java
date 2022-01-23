package com.jumbo.trus.season;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;

public class SeasonDialog extends Dialog {

    private static final String TAG = "DialogSeasons";

    //widgety
    private TextView tv_headline;
    private EditText et_name, et_date_beginning, et_date_ending;
    private Button btn_calendar_beginning, btn_calendar_ending, btn_commit, btn_delete;

    //vars
    private ISeasonFragment iSeasonFragment;


    public SeasonDialog(Flag flag) {
        super(flag);
    }

    public SeasonDialog(Flag flag, Model season) {
        super(flag, season);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_seasons_plus, container, false);
        tv_headline = view.findViewById(R.id.tv_headline);
        et_name = view.findViewById(R.id.et_name);
        et_date_beginning = view.findViewById(R.id.et_date_beginning);
        et_date_ending = view.findViewById(R.id.et_date_ending);
        btn_calendar_beginning = view.findViewById(R.id.btn_calendar_beginning);
        btn_calendar_ending = view.findViewById(R.id.btn_calendar_ending);
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_delete = view.findViewById(R.id.btn_delete);
        decideTextsToShow();

        btn_commit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_calendar_beginning.setOnClickListener(this);
        btn_calendar_ending.setOnClickListener(this);

        return view;
    }

    private void decideTextsToShow() {
        switch (flag) {
            case SEASON_PLUS:
                setTextsToAddSeason();
                break;
            case SEASON_EDIT:
                setTextsToEditSeason();
                break;
        }
    }

    private void setTextsToAddSeason() {
        tv_headline.setText("Nová sezona");
        et_name.setText("");
        et_name.setHint("Název sezony");
        //et_datum.setText("Přidat hráče");
        //btn_kalendar.setText("Přidat hráče");
        btn_commit.setText("Přidat sezonu");
        btn_delete.setVisibility(View.GONE);
    }

    private void setTextsToEditSeason() {
        tv_headline.setText("Úprava sezony");
        et_name.setText(model.getName());
        et_date_beginning.setText(((Season)model).returnSeasonStartInStringFormat());
        et_date_ending.setText(((Season)model).returnSeasonEndInStringFormat());
        btn_commit.setText("Upravit sezonu");
        btn_delete.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_calendar_beginning: {
                displayCalendarDialog(et_date_beginning);
                break;
            }
            case R.id.btn_calendar_ending: {
                displayCalendarDialog(et_date_ending);
                break;
            }

        }
        switch (flag) {
            case SEASON_PLUS: {
                switch (view.getId()) {
                    case R.id.btn_commit: {
                        Log.d(TAG, "onClick: kliknuti na přidat sezonu");
                        String name = et_name.getText().toString();
                        String dateBeginning = et_date_beginning.getText().toString();
                        String dateEnding = et_date_ending.getText().toString();
                        if (iSeasonFragment.createNewSeason(name, dateBeginning, dateEnding)) {
                            getDialog().dismiss();
                        }
                        break;
                    }
                }
            }
            break;
            case SEASON_EDIT: {
                switch (view.getId()) {
                    case R.id.btn_commit: {
                        Log.d(TAG, "onClick: kliknuti na editovat sezonu");
                        String name = et_name.getText().toString();
                        String dateBeginning = et_date_beginning.getText().toString();
                        String dateEnding = et_date_ending.getText().toString();
                        if (iSeasonFragment.editSeason(name, dateBeginning, dateEnding, (Season)model)) {
                            getDialog().dismiss();
                        }
                        break;
                    }
                    case R.id.btn_delete: {
                        Log.d(TAG, "onClick: kliknuti na smazat hráče");
                        displayDeleteConfirmationDialog(model, iSeasonFragment, "Opravdu chceš smazat sezonu?", "Zápasům obsahující tuto" +
                                " sezonu se automaticky přiřadí nová sezona podle data, případně sezona Ostatní");
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
            iSeasonFragment = (ISeasonFragment) getTargetFragment();
        }
        catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }

    }

}
