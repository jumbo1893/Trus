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
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repayment.Repayment;
import com.jumbo.trus.user.User;

import java.util.List;

public class SimpleRecycleViewAdapter extends RecyclerView.Adapter<SimpleRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "SimpleRecycleViewAdapter";

    private List<? extends Model> models;
    private Context context;
    private OnListListener onListListener;

    public SimpleRecycleViewAdapter(List<? extends Model> models, Context context, OnListListener onListListener) {
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
            holder.tv_text.setText("Sezona: " + ((Match) models.get(position)).getSeason().getName() + ", datum zápasu: " + ((Match) models.get(position)).getDateOfMatchInStringFormat());
        }
        else if (models.get(position) instanceof Player) {
            holder.tv_title.setText(models.get(position).getName());
            if (((Player) models.get(position)).isFan()) {
                holder.tv_text.setText("Fanoušek, datum narození: " + ((Player) models.get(position)).getBirthdayInStringFormat());
            }
            else {
                holder.tv_text.setText("Hráč, datum narození: " + ((Player) models.get(position)).getBirthdayInStringFormat());
            }
        }
        else if (models.get(position) instanceof ReceivedFine) {
            holder.tv_title.setText(((ReceivedFine) models.get(position)).getFine().getName());
            holder.tv_text.setText("á " + ((ReceivedFine) models.get(position)).getFine().getAmount() +
                    " Kč v počtu " + ((ReceivedFine) models.get(position)).getCount() + " a celkové výši " +
                    ((ReceivedFine) models.get(position)).getAmountOfAllFines() + " Kč");
        }
        else if (models.get(position) instanceof Fine) {
            holder.tv_title.setText(models.get(position).getName());
            holder.tv_text.setText("Pokuta ve výši: " + ((Fine) models.get(position)).getAmount() + " Kč");

        }
        else if (models.get(position) instanceof Repayment) {
            holder.tv_title.setText("Uhrazená částka: " + ((Repayment) models.get(position)).getAmount() + " Kč");
            holder.tv_text.setText("dne: " + ((Repayment) models.get(position)).getDateOfTimestampInStringFormat() + "\n" + ((Repayment) models.get(position)).getNote());

        }
        else if (models.get(position) instanceof User) {
            //holder.tv_title.setText("Uživatel " + models.get(position).getName());
            holder.tv_text.setText("Uživatel " + models.get(position).getName() + ", registrován dne " + ((User) models.get(position)).returnRegistrationDateInStringFormat());

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
            onListListener.onItemClick(getBindingAdapterPosition());
        }
    }

}
