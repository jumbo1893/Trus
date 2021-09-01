package com.jumbo.trus.notification;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRecycleViewAdapter extends RecyclerView.Adapter<NotificationRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "NotificationRecycleView";

    private List<Notification> notifications;
    private Context context;
    private OnListListener onListListener;
    //private TextView senderProfilePic;

    public NotificationRecycleViewAdapter(List<Notification> notifications, Context context, OnListListener onListListener) {
        this.notifications = notifications;
        this.context = context;
        this.onListListener = onListListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notificationlist, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onListListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        String senderFirstLetter = (String) notifications.get(position).getUser().getName().subSequence(0, 1);
        holder.senderProfilePic.setText(senderFirstLetter);
        holder.senderProfilePic.setTextColor(notifications.get(position).getUser().getCharColor());
        Log.d(TAG, "onBindViewHolder: " + notifications.get(position).getUser().getName() + notifications.get(position).getUser().getCharColor());
        holder.tv_title.setText(notifications.get(position).getTitle());
        holder.tv_text.setText(notifications.get(position).getText());
        holder.tv_timestamp.setText(notifications.get(position).getTimestampInStringFormat());
        holder.tv_user.setText(notifications.get(position).getUser().getName());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_text, tv_timestamp, tv_user, tv_title;
        CircleImageView circleImageView;
        RelativeLayout layout_parent;
        OnListListener onListListener;
        TextView senderProfilePic;

        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_user = itemView.findViewById(R.id.tv_user);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
            senderProfilePic = itemView.findViewById(R.id.circle_image);
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
