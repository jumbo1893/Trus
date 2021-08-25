package com.jumbo.trus.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.OnPlusButtonListener;
import com.jumbo.trus.R;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FinesRecycleViewAdapter extends RecyclerView.Adapter<FinesRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "BeerRecycleViewAdapter";

    private Context context;
    private OnPlusButtonListener onPlusButtonListener;
    private List<ReceivedFine> fines;

    public FinesRecycleViewAdapter(List<ReceivedFine> fines, Context context, OnPlusButtonListener onPlusButtonListener) {
        this.fines = fines;
        this.context = context;
        this.onPlusButtonListener = onPlusButtonListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_add, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onPlusButtonListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.tv_title.setText(fines.get(position).getFine().getName());
        holder.et_number.setText(String.valueOf(fines.get(position).getCount()));
    }

    @Override
    public int getItemCount() {
        return fines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_title;
        EditText et_number;
        ImageButton btn_add, btn_remove;
        RelativeLayout layout_parent;
        OnPlusButtonListener onPlusButtonListener;

        public ViewHolder(@NonNull View itemView, OnPlusButtonListener onPlusButtonListener) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            et_number = itemView.findViewById(R.id.et_number);
            btn_add = itemView.findViewById(R.id.btn_add);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            itemView.setOnClickListener(this);
            btn_remove.setOnClickListener(this);
            btn_add.setOnClickListener(this);
            this.onPlusButtonListener = onPlusButtonListener;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add: {
                    //players.get(getAdapterPosition()).addBeer();
                    onPlusButtonListener.onPlusClick(getAdapterPosition());
                    et_number.setText(String.valueOf(fines.get(getAdapterPosition()).getCount()));
                    break;
                }
                case R.id.btn_remove: {
                    //players.get(getAdapterPosition()).removeBeer();
                    onPlusButtonListener.onMinusClick(getAdapterPosition());
                    et_number.setText(String.valueOf(fines.get(getAdapterPosition()).getCount()));
                }
            }
        }
    }

}
