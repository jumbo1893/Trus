package com.jumbo.trus.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Model;
import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;

public class FineStatisticsRecycleViewAdapter extends RecyclerView.Adapter<FineStatisticsRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "FineStatisticsRecycleViewAdapter";

    private List<? extends Model> models;
    private Context context;
    private OnListListener onListListener;

    public FineStatisticsRecycleViewAdapter(List<? extends Model> models, Context context, OnListListener onListListener) {
        this.models = models;
        this.context = context;
        this.onListListener = onListListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onListListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if (models.get(position) instanceof Match) {
            holder.tv_title.setText(((Match) models.get(position)).toStringNameWithOpponent());
            holder.tv_text.setText("Počet pokut: " + ((Match) models.get(position)).returnNumberOfFinesInMatch() + " v celkové výši: "
                    + ((Match) models.get(position)).returnAmountOfFinesInMatch() + " Kč");
        }
        else if (models.get(position) instanceof Player) {
            holder.tv_title.setText((models.get(position)).getName());
            holder.tv_text.setText("Počet pokut: " + ((Player) models.get(position)).getNumberOfFinesInMatches() + " v celkové částce "
                    + ((Player) models.get(position)).getAmountOfFinesInMatches() + " Kč");
        }
        else {
            holder.tv_title.setText(models.get(position).toString());
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_title, tv_text;
        RelativeLayout layout_parent;
        OnListListener onListListener;

        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_text = itemView.findViewById(R.id.tv_text);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            this.onListListener = onListListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListListener.onItemClick(getAdapterPosition());
        }
    }

}
