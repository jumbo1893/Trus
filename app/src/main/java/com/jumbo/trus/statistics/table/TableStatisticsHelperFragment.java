package com.jumbo.trus.statistics.table;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
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

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.adapters.array.SeasonArrayAdapter;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.table.beer.TableBeerStatisticsViewModel;

import org.json.JSONException;

import java.util.List;


public class TableStatisticsHelperFragment extends CustomUserFragment implements View.OnClickListener {

    private static final String TAG = "TableStatisticsHelperFragment";

    //widgety
    private TableLayout table_main;
    protected ImageButton btnDownload;
    private ProgressBar progress_bar;
    protected AutoCompleteTextView tvSeason;
    protected TextInputLayout textSeason;

    protected SeasonArrayAdapter seasonArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_table_pager, container, false);
        table_main = view.findViewById(R.id.table_main);
        btnDownload = view.findViewById(R.id.btnDownload);
        progress_bar = view.findViewById(R.id.progress_bar);
        textSeason = view.findViewById(R.id.textSeason);
        tvSeason = view.findViewById(R.id.tvSeason);
        btnDownload.setOnClickListener(this);
        return view;
    }

    protected void setupSeasonDropDownMenu(List<Season> seasonList) {
        if (seasonList != null) {
            seasonArrayAdapter = new SeasonArrayAdapter(getActivity(), seasonList);
            tvSeason.setAdapter(seasonArrayAdapter);
            tvSeason.dismissDropDown();
        }
    }

    protected void initTable(List<List<String>> rowList) {
        table_main.removeAllViews();
        for (int i = 0; i < rowList.size(); i++) {
            List<String> rowTexts = rowList.get(i);
            TableRow tbrow = new TableRow(getActivity());

            for (int j = 0; j < rowTexts.size(); j++) {
                if (i == 0) {
                    tbrow.addView(setBorder(returnSetTextView(rowTexts.get(j)), 5) );
                }
                else {
                    tbrow.addView(setBorder(returnSetTextView(rowTexts.get(j)), 1) );
                }
                tbrow.setOnClickListener(this);
            }
            table_main.addView(tbrow);
        }
    }

    private TextView setBorder(TextView textView, float width) {
        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setColor(Color.BLACK);
        border.getPaint().setStrokeWidth(width);
        textView.setBackground(border);
        return textView;
    }

    private TextView returnSetTextView(String text) {
        TextView tv = new TextView(getActivity());
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    protected void saveTextToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    protected void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    protected void onDownloadClick() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnDownload) {
            onDownloadClick();
        } else if (view instanceof TableRow) {
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                if (((ColorDrawable) background).getColor() == Color.GRAY) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    view.setBackgroundColor(Color.GRAY);
                }
            } else {
                view.setBackgroundColor(Color.GRAY);
            }
        }
    }
}
