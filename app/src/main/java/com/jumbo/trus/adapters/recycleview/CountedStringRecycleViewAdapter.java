package com.jumbo.trus.adapters.recycleview;

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

public class CountedStringRecycleViewAdapter extends RecyclerView.Adapter<CountedStringRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "StringRecycleViewAdapter";

    private List<String> list;
    private Context context;
    private OnListListener onListListener;

    public CountedStringRecycleViewAdapter(List<String> list, Context context, OnListListener onListListener) {
        this.list = list;
        this.context = context;
        this.onListListener = onListListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_list_counted, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onListListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "StringRecycleViewAdapter: " + this.list.get(1));
        holder.tv_text.setText(list.get(position));
        holder.tv_number.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnListListener onListListener;
        RelativeLayout layout_parent;
        TextView tv_text, tv_number;

        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            this.onListListener = onListListener;
            layout_parent = itemView.findViewById(R.id.layout_parent);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_number = itemView.findViewById(R.id.tv_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListListener.onItemClick(getAdapterPosition());
        }
    }

}
