package com.jumbo.trus.adapters.recycleview;

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

import com.jumbo.trus.R;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.player.Player;

import java.util.List;

public class PlayerRepaymentRecycleViewAdapter extends RecyclerView.Adapter<PlayerRepaymentRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "PlayerRepaymentRecycleViewAdapter";

    private List<Player> players;
    private Context context;
    private OnListListener onListListener;

    public PlayerRepaymentRecycleViewAdapter(List<Player> players, Context context, OnListListener onListListener) {
        this.players = players;
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
        holder.tv_title.setText(players.get(position).getName());
        holder.tv_text.setText("Dlužná částka na pokutách: " + players.get(position).returnAmountOwed() + " Kč");

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
            this.onListListener = onListListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListListener.onItemClick(getAdapterPosition());
        }
    }

}
