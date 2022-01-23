package com.jumbo.trus.adapters.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.R;

import java.util.List;

public class SimpleStringRecycleViewAdapter extends RecyclerView.Adapter<SimpleStringRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "StringRecycleViewAdapter";

    private List<String> list;
    private Context context;

    public SimpleStringRecycleViewAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_text.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout_parent;
        TextView tv_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            tv_text = itemView.findViewById(R.id.tv_text);
        }
    }

}
