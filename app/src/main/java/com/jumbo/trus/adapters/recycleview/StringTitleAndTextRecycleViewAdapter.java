package com.jumbo.trus.adapters.recycleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.R;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.statistics.player.ListTexts;

import java.util.List;

public class StringTitleAndTextRecycleViewAdapter extends RecyclerView.Adapter<StringTitleAndTextRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "StringTitleAndTextRecycleViewAdapter";

    private List<ListTexts> listTexts;
    private Context context;
    private OnListListener onListListener;

    public StringTitleAndTextRecycleViewAdapter(List<ListTexts> listTexts, Context context, OnListListener onListListener) {
        this.listTexts = listTexts;
        this.context = context;
        this.onListListener = onListListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view, onListListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_title.setText(listTexts.get(position).getTitle());
        holder.tv_text.setText(listTexts.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return listTexts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
