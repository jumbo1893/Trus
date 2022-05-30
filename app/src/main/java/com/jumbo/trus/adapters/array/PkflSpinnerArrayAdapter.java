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
import com.jumbo.trus.pkfl.stats.SpinnerOption;

import java.util.List;

public class PkflSpinnerArrayAdapter extends ArrayAdapter<SpinnerOption> {

    private Context context;
    private List<SpinnerOption> spinnerOptions;

    public PkflSpinnerArrayAdapter(@NonNull Context context, List<SpinnerOption> spinnerOptions) {
        super(context, 0 , spinnerOptions);
        this.context = context;
        this.spinnerOptions = spinnerOptions;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.dropdown_menu_popup_item, parent, false);
        }

        SpinnerOption spinnerOption = spinnerOptions.get(position);

        TextView name = listItem.findViewById(R.id.tvText);
        name.setText(spinnerOption.getName());

        return listItem;
    }



}
