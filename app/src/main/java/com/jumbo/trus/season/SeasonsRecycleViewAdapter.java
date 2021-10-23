package com.jumbo.trus.season;

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

import java.util.ArrayList;
import java.util.List;

public class SeasonsRecycleViewAdapter extends RecyclerView.Adapter<SeasonsRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "SeasonsRecycleViewAdapter";

    private List<Season> seasons;
    private Context context;
    private OnListListener onListListener;

    public SeasonsRecycleViewAdapter(List<Season> seasons1, Context context, OnListListener onListListener) {
        this.seasons = new ArrayList<>();
        //vyloučíme v seznamu zobrazení sezony ostatní aby nešla editovat
        for (int i = 0; i < seasons1.size(); i++) {
            if (seasons1.get(i).getSeasonStart() != 999999999) {
                this.seasons.add(seasons1.get(i));
            }
        }
        //this.seasons.remove(seasons.size()-1);
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

        holder.tv_title.setText(seasons.get(position).getName());
        holder.tv_text.setText("Začátek sezony: " + seasons.get(position).getSeasonStartInStringFormat() + ", konec sezony: " +
                seasons.get(position).getSeasonEndInStringFormat());
    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_title, tv_text;
        RelativeLayout layout_parent;
        OnListListener onListListener;

        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_title = itemView.findViewById(R.id.tv_title);
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
