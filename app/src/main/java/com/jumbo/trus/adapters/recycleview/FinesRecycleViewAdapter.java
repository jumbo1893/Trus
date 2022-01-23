package com.jumbo.trus.adapters.recycleview;

import android.annotation.SuppressLint;
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

import com.jumbo.trus.listener.OnPlusButtonListener;
import com.jumbo.trus.R;
import com.jumbo.trus.fine.ReceivedFine;

import java.util.ArrayList;
import java.util.List;

public class FinesRecycleViewAdapter extends RecyclerView.Adapter<FinesRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "FinesRecycleViewAdapter";

    private Context context;
    private OnPlusButtonListener onPlusButtonListener;
    private List<ReceivedFine> fines;
    private List<Integer> finesNumber;

    public FinesRecycleViewAdapter(List<ReceivedFine> fines, Context context, OnPlusButtonListener onPlusButtonListener) {
        this.fines = fines;
        this.context = context;
        this.onPlusButtonListener = onPlusButtonListener;
        initFinesNumber();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_add, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onPlusButtonListener);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.tv_title.setText(fines.get(position).getFine().getName() + " (" + fines.get(position).getFine().getAmount() + " Kč)");
        holder.et_number.setText(String.valueOf(finesNumber.get(position)));
    }

    @Override
    public int getItemCount() {
        return fines.size();
    }

    private void initFinesNumber() {
        finesNumber = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            finesNumber.add(fines.get(i).getCount());
        }
    }
    private int plusFineNumber(int position) {
        int number = finesNumber.get(position);
        finesNumber.set(position, number+1);
        return finesNumber.get(position);
    }

    private int minusFineNumber(int position) {
        int number = finesNumber.get(position);
        if (number > 0) {
            finesNumber.set(position, number - 1);
        }
        return finesNumber.get(position);
    }

    public List<Integer> getFinesNumber() {
        return finesNumber;
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
                    et_number.setText(String.valueOf(plusFineNumber(getBindingAdapterPosition())));
                    notifyDataSetChanged();
                    break;
                }
                case R.id.btn_remove: {
                    et_number.setText(String.valueOf(minusFineNumber(getBindingAdapterPosition())));
                    notifyDataSetChanged();
                }
            }
        }
    }

}
