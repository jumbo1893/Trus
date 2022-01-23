package com.jumbo.trus.adapters.array;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jumbo.trus.R;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.season.Season;

import java.util.List;

public class MatchArrayAdapter extends ArrayAdapter<Match> {

    private static final String TAG = "MatchArrayAdapter";

    private Context context;
    private List<Match> matchList;

    public MatchArrayAdapter(@NonNull Context context, List<Match> matchList) {
        super(context, 0 , matchList);
        this.context = context;
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.dropdown_menu_popup_item, parent, false);
        }

        Match currentMatch = matchList.get(position);

        TextView name = listItem.findViewById(R.id.tvText);
        name.setText(currentMatch.toStringNameWithOpponent());

        return listItem;
    }

    /*@Override
    public int getCount() {
        Log.d(TAG, "MatchArrayAdapter: " + matchList);
        Log.d(TAG, "MatchArrayAdapter: " + matchList.size());
        return matchList!=null ? matchList.size() : 0;
    }*/
}
