package com.jumbo.trus.statistics;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.jumbo.trus.Date;
import com.jumbo.trus.Dialog;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.FineViewModel;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.web.GoogleSheetRequestSender;
import com.jumbo.trus.web.IRequestListener;
import com.jumbo.trus.web.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TableBeerStatisticsDialog extends Dialog implements IRequestListener {

    private static final String TAG = "TableBeerStatisticsDialog";

    //widgety
    private TableLayout table_main;
    private Button btn_commit, btn_change, btn_export;
    private ProgressBar progress_bar;

    private List<Match> selectedMatches;
    private List<Player> selectedPlayers;

    private FineViewModel fineViewModel;
    private StatisticsViewModel statisticsViewModel;
    private boolean fineMatch;
    private List<List<String>> rowList;

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
        btn_export = view.findViewById(R.id.btn_export);
        progress_bar = view.findViewById(R.id.progress_bar);
        enableButton(btn_change, false);
        btn_commit.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        btn_export.setOnClickListener(this);
        statisticsViewModel = new ViewModelProvider(getActivity()).get(StatisticsViewModel.class);
        if (flag == Flag.BEER) {
            rowList = statisticsViewModel.makeTextsForBeersTable(selectedMatches, selectedPlayers);
            initTable(rowList);
            btn_change.setVisibility(View.GONE);
        }
        else {
            rowList = statisticsViewModel.makeTextsForFineMatches(selectedMatches, selectedPlayers);
            initTable(rowList);
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

    private void sendToGoogle(String action, List<List<String>> rowList) throws JSONException {
        GoogleSheetRequestSender sender = new GoogleSheetRequestSender(this, getActivity());
        JsonParser jsonParser = new JsonParser();
        Date date = new Date();
        String footer = "Exportováno z Trusí appky " + date.convertMillisToStringTimestamp(System.currentTimeMillis());
        sender.sendRequest(jsonParser.convertStatsToJsonObject(action, rowList, footer));
    }

    private void saveTextToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    private void showItem(View button) {
        button.setVisibility(View.VISIBLE);
    }

    private void hideItem(View button) {
        button.setVisibility(View.GONE);
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
                    rowList = statisticsViewModel.makeTextsForFineDetail(selectedMatches, selectedPlayers, fineViewModel.getFines().getValue());
                    initTable(rowList);
                    fineMatch = false;
                }
                else {
                    rowList = statisticsViewModel.makeTextsForFineMatches(selectedMatches, selectedPlayers);
                    initTable(rowList);
                    fineMatch = true;
                }
                break;
            }
            case R.id.btn_export: {
                String action;
                if (flag == Flag.BEER) {
                    action = "beer";
                }
                else if (fineMatch) {
                    action = "fine_match";
                }
                else {
                    action = "fine_detail";
                }
                try {
                    sendToGoogle(action, rowList);
                    showItem(progress_bar);
                    enableButton(btn_export, false);
                } catch (JSONException e) {
                    e.printStackTrace();
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

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, "onResponse: " + response);
        hideItem(progress_bar);
        enableButton(btn_export, true);
        try {
            JsonParser jsonParser = new JsonParser(response);
            saveTextToClipboard("trus stats", jsonParser.getURL());
            Toast.makeText(getActivity(), "Do tabulky " + jsonParser.getSheetName() + " přidáno " + jsonParser.getRowsNumber() +
                    " řádků. Odkaz na tabulku byl uložen do clipboard paměti(ctrl c).", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(String error) {
        Log.d(TAG, "onErrorResponse: " + error);
        hideItem(progress_bar);
        enableButton(btn_export, true);
        Toast.makeText(getActivity(), error + ". Zkus to později", Toast.LENGTH_SHORT).show();
    }
}
