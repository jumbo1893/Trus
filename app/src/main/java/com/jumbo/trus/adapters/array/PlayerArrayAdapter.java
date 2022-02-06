package com.jumbo.trus.adapters.array;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jumbo.trus.R;
import com.jumbo.trus.player.Player;

import java.util.List;

public class PlayerArrayAdapter extends ArrayAdapter<Player> {

    private static final String TAG = "PlayerArrayAdapter";

    private Context context;
    private List<Player> playerList;

    public PlayerArrayAdapter(@NonNull Context context, List<Player> playerList) {
        super(context, 0 , playerList);
        this.context = context;
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.dropdown_menu_popup_item, parent, false);
        }

        Player currentPlayer = playerList.get(position);

        TextView name = listItem.findViewById(R.id.tvText);
        name.setText(currentPlayer.toString());

        return listItem;
    }

    /*@Override
    public int getCount() {
        Log.d(TAG, "MatchArrayAdapter: " + matchList);
        Log.d(TAG, "MatchArrayAdapter: " + matchList.size());
        return matchList!=null ? matchList.size() : 0;
    }*/
}
