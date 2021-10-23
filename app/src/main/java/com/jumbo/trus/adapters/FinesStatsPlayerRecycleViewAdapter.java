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

import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;

public class FinesStatsPlayerRecycleViewAdapter extends RecyclerView.Adapter<FinesStatsPlayerRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "FinesStatsPlayerRecycleViewAdapter";

    private Context context;
    private OnListListener onListListener;
    private List<Player> players;
    private List<Match> matches;

    public FinesStatsPlayerRecycleViewAdapter(List<Player> players, List<Match> matches, Context context, OnListListener onListListener) {
        this.players = players;
        this.matches = matches;
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
        holder.tv_title.setText(((Match) matches.get(position)).toStringNameWithOpponent());
        holder.tv_text.setText("Počet pokut pro hráče: " + players.get(position).returnNumberOfAllReceviedFines() +
        " v celkové výši " + players.get(position).returnAmountOfAllReceviedFines() + " Kč");
    }

    @Override
    public int getItemCount() {
        return players.size();
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
            itemView.setOnClickListener(this);
            this.onListListener = onListListener;
        }

        @Override
        public void onClick(View v) {
            onListListener.onItemClick(getAdapterPosition());
        }
    }

}
