package com.jumbo.trus.adapters;

import android.annotation.SuppressLint;
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
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;

public class BeerStatisticsRecycleViewAdapter extends RecyclerView.Adapter<BeerStatisticsRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "BeerStatisticsRecycleViewAdapter";

    private List<? extends Model> models;
    private Context context;
    private OnListListener onListListener;

    public BeerStatisticsRecycleViewAdapter(List<? extends Model> models, Context context, OnListListener onListListener) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if (models.get(position) instanceof Match) {
            holder.tv_title.setText(((Match) models.get(position)).toStringNameWithOpponent());
            int beerNumber = ((Match) models.get(position)).returnNumberOfBeersInMatch();
            int liquorNumber = ((Match) models.get(position)).returnNumberOfLiquorsInMatch();
            holder.tv_text.setText("Počet pivek v zápase: " + beerNumber + ", počet panáků: " + liquorNumber + ", dohromady: " + (beerNumber + liquorNumber));
        }
        else if (models.get(position) instanceof Player) {
            int beerNumber = ((Player) models.get(position)).getNumberOfBeersInMatches();
            int liquorNumber = ((Player) models.get(position)).getNumberOfLiquorsInMatches();
            holder.tv_title.setText((models.get(position)).getName());
            holder.tv_text.setText("Počet piv: " + beerNumber + ", počet panáků: " + liquorNumber + ", dohromady: " + (beerNumber + liquorNumber));
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
