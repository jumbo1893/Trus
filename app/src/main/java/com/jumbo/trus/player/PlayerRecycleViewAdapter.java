package com.jumbo.trus.player;

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

import java.util.List;

public class PlayerRecycleViewAdapter extends RecyclerView.Adapter<PlayerRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "HracRecycleViewAdapter";

    private List<Player> hraci;
    private Context context;
    private OnListListener onListListener;

    public PlayerRecycleViewAdapter(List<Player> hraci, Context context, OnListListener onListListener) {
        this.hraci = hraci;
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

        holder.tv_title.setText(hraci.get(position).getName());
        if (hraci.get(position).isFan()) {
            holder.tv_text.setText("Fanoušek, datum narození: " + hraci.get(position).getBirthdayInStringFormat());
        }
        else {
            holder.tv_text.setText("Hráč, datum narození: " + hraci.get(position).getBirthdayInStringFormat());
        }
    }

    @Override
    public int getItemCount() {
        return hraci.size();
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
