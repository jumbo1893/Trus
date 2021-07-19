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

import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;

public class FinesPlayerNumberRecycleViewAdapter extends RecyclerView.Adapter<FinesPlayerNumberRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "FinesPlayerNumberRecycleViewAdapter";

    private Context context;
    private OnListListener onListListener;
    private List<Player> players;
    private ReceivedFine receivedFine;

    public FinesPlayerNumberRecycleViewAdapter(List<Player> players, ReceivedFine receivedFine, Context context, OnListListener onListListener) {
        this.players = players;
        this.receivedFine = receivedFine;
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
        holder.tv_title.setText(players.get(position).getName());
        holder.tv_text.setText("Dostal " + players.get(position).getNumberOfReceviedFine(receivedFine) + " pokut " +
        " v celkové výši " + players.get(position).getAmountOfReceviedFine(receivedFine) + " Kč");
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
