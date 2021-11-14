package com.jumbo.trus.statistics;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.FineViewModel;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.List;


public class TableBeerStatisticsDialog extends Dialog {

    private static final String TAG = "TableBeerStatisticsDialog";

    //widgety
    private TableLayout table_main;
    private Button btn_commit, btn_change;

    private List<Match> selectedMatches;
    private List<Player> selectedPlayers;

    private FineViewModel fineViewModel;
    private boolean fineMatch;

    public TableBeerStatisticsDialog(Flag flag, Model model, List<Match> selectedMatches, List<Player> selectedPlayers) {
        super(flag, model);
        this.selectedMatches = selectedMatches;
        this.selectedPlayers = selectedPlayers;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_table, container, false);
        table_main = view.findViewById(R.id.table_main);
        btn_commit = view.findViewById(R.id.btn_commit);
        btn_change = view.findViewById(R.id.btn_change);
        enableButton(btn_change, false);
        btn_commit.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        if (flag == Flag.BEER) {
            initTable(makeTextsForBeersTable());
            btn_change.setVisibility(View.GONE);
        }
        else {
            initTable(makeTextsForFineMatches());
            fineMatch = true;
            btn_change.setVisibility(View.VISIBLE);
            fineViewModel = new ViewModelProvider(getActivity()).get(FineViewModel.class);
            fineViewModel.init();

            fineViewModel.getFines().observe(getViewLifecycleOwner(), new Observer<List<Fine>>() {
                @Override
                public void onChanged(List<Fine> fines) {
                    Log.d(TAG, "onChanged: nacetly se pokuty " + fines);
                    enableButton(btn_change, true);

                }
            });
        }


        return view;
    }

    private void enableButton(Button button, boolean b) {
        button.setEnabled(b);
        if (b) {
            button.setTextColor(Color.RED);
        }
        else {
            button.setTextColor(Color.GRAY);
        }
    }

    private List<List<String>> makeTextsForBeersTable() {
        //první řádek
        List<List<String>> rowList = new ArrayList<>();
        List<String> row0 = new ArrayList<>();
        row0.add(" Hráč ");
        //hráči v prvním řádku
        for (Match match : selectedMatches) {
            row0.add(" " + match.getOpponent() + " ");
        }
        //poslední sloupec prvního řádku
        row0.add(" Celkem ");
        rowList.add(row0);
        //další řádky podle hráčů
        for (int i = 0; i < selectedPlayers.size(); i++) {
            Player player = selectedPlayers.get(i);
            List<String> row = new ArrayList<>();
            row.add(" " + player.getName() + " ");
            int matchBeers = 0;
            for (int j = 0; j < selectedMatches.size(); j++) {
                Match match = selectedMatches.get(j);
                int beerNumber = match.returnNumberOfBeersAndLiquorsForPlayer(player);
                matchBeers += beerNumber;
                row.add(" " + beerNumber + " ");
            }
            row.add(" " + matchBeers + " ");
            rowList.add(row);
        }
        //poslední řádek
        List<String> rowLast = new ArrayList<>();
        rowLast.add(" Celkem ");
        for (Match match : selectedMatches) {
            rowLast.add(" " + (match.returnNumberOfBeersInMatch()+match.returnNumberOfLiquorsInMatch()) + " ");
        }
        rowList.add(rowLast);
        return rowList;
    }

    private List<List<String>> makeTextsForFineMatches() {
        //první řádek
        List<List<String>> rowList = new ArrayList<>();
        List<String> row0 = new ArrayList<>();
        row0.add(" Hráč ");
        //hráči v prvním řádku
        for (Match match : selectedMatches) {
            row0.add(" " + match.getOpponent() + " ");
        }
        //poslední sloupec prvního řádku
        row0.add(" Celkem ");
        rowList.add(row0);
        //další řádky podle hráčů
        for (int i = 0; i < selectedPlayers.size(); i++) {
            Player player = selectedPlayers.get(i);
            if (!player.isFan()) {
                List<String> row = new ArrayList<>();
                row.add(" " + player.getName() + " ");
                int matchFines = 0;
                for (int j = 0; j < selectedMatches.size(); j++) {
                    Match match = selectedMatches.get(j);
                    int finesNumber = match.returnAmountOfFinesInMatch(player);
                    matchFines += finesNumber;
                    row.add(" " + finesNumber + " Kč ");
                }
                row.add(" " + matchFines + " Kč ");
                rowList.add(row);
            }
        }
        //poslední řádek
        List<String> rowLast = new ArrayList<>();
        rowLast.add(" Celkem ");
        for (Match match : selectedMatches) {
            rowLast.add(" " + match.returnAmountOfFinesInMatch() + " Kč ");
        }
        rowList.add(rowLast);
        return rowList;
    }

    private List<List<String>> makeTextsForFineDetail() {
        List<Fine> fineList = fineViewModel.getFines().getValue();
        //první řádek
        List<List<String>> rowList = new ArrayList<>();
        List<String> row0 = new ArrayList<>();
        row0.add(" Hráč ");
        //hráči v prvním řádku
        for (Fine fine : fineList) {
            row0.add(" " + fine.getName() + " ");
        }
        //poslední sloupec prvního řádku
        row0.add(" Celkem ");
        rowList.add(row0);

        //další řádky podle hráčů
        for (int i = 0; i < selectedPlayers.size(); i++) {
            Player player = selectedPlayers.get(i);
            if (!player.isFan()) {
                List<String> row = new ArrayList<>();
                row.add(" " + player.getName() + " ");
                int matchFines = 0;
                for (int j = 0; j < fineList.size(); j++) {
                    Fine fine = fineList.get(j);
                    int finesNumber = player.returnFineNumber(selectedMatches, fine);
                    matchFines += finesNumber;
                    row.add(" " + finesNumber + " Kč ");
                }
                row.add(" " + matchFines + " Kč ");
                rowList.add(row);
            }
        }
        //poslední řádek
        List<String> rowLast = new ArrayList<>();
        rowLast.add(" Celkem ");
        for (Fine fine : fineList) {
            rowLast.add(" " + fine.returnAmountOfFineInMatches(selectedMatches) + " Kč");
        }
        rowList.add(rowLast);

        return rowList;
    }

    private void initTable(List<List<String>> rowList) {
        table_main.removeAllViews();
        for (int i = 0; i < rowList.size(); i++) {
            List<String> rowTexts = rowList.get(i);
            TableRow tbrow = new TableRow(getActivity());
            for (int j = 0; j < rowTexts.size(); j++) {
                tbrow.addView(returnSetTextView(rowTexts.get(j)));
                tbrow.setOnClickListener(this);
            }
            table_main.addView(tbrow);
        }
    }

    private TextView returnSetTextView(String text) {
        TextView tv = new TextView(getActivity());
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                getDialog().dismiss();
                break;
            }
            case R.id.btn_change: {
                if (fineMatch) {
                    initTable(makeTextsForFineDetail());
                    fineMatch = false;
                }
                else {
                    initTable(makeTextsForFineMatches());
                    fineMatch = true;
                }
                break;
            }
        }
        if (view instanceof TableRow) {
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                if (((ColorDrawable) background).getColor() == Color.GRAY) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                else {
                    view.setBackgroundColor(Color.GRAY);
                }
            }
            else {
                view.setBackgroundColor(Color.GRAY);
            }
        }
    }
}
