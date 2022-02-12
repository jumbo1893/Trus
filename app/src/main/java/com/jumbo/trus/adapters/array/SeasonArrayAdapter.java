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
import com.jumbo.trus.season.Season;

import java.util.List;

public class SeasonArrayAdapter extends ArrayAdapter<Season> {

    private Context context;
    private List<Season> seasonList;

    public SeasonArrayAdapter(@NonNull Context context, List<Season> seasonList) {
        super(context, 0 , seasonList);
        this.context = context;
        this.seasonList = seasonList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.dropdown_menu_popup_item, parent, false);
        }

        Season currentSeason = seasonList.get(position);

        TextView name = listItem.findViewById(R.id.tvText);
        name.setText(currentSeason.getName());

        return listItem;
    }



}
