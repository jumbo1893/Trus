package com.jumbo.trus.notification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;

import java.util.List;

public class NotificationFragment extends Fragment implements OnListListener {

    private static final String TAG = "NotificationFragment";

    private RecyclerView rc_notifications;
    private NotificationRecycleViewAdapter adapter;
    private ProgressBar progress_bar;
    private NotificationViewModel notificationViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: notifikace");
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        rc_notifications = view.findViewById(R.id.rc_notifications);
        rc_notifications.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), 180));
        progress_bar = view.findViewById(R.id.progress_bar);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.init();
        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                Log.d(TAG, "onChanged: nacetli se notifikace " + notifications);
                Log.d(TAG, "onChanged: adapter " + adapter);
                if (adapter == null) {
                    initNotificationRecycleView();
                }
                setAdapter();
                adapter.notifyDataSetChanged(); //TODO notifyItemInserted
            }
        });
        notificationViewModel.isUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProgressBar();
                }
                else {
                    hideProgressBar();
                }
            }
        });
        notificationViewModel.getAlert().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void initNotificationRecycleView() {
        Log.d(TAG, "initNotificationRecycleView: iniciuji rv");
        adapter = new NotificationRecycleViewAdapter(notificationViewModel.getNotifications().getValue(), getActivity(), this);
    }

    private void setAdapter() {
        rc_notifications.setAdapter(adapter);
        rc_notifications.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position) {
        Notification notification = notificationViewModel.getNotifications().getValue().get(position);
        Log.d(TAG, "onHracClick: kliknuto na pozici " + position + ", object: " + notificationViewModel.getNotifications().getValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(notification.getTitle())
                .setMessage(notification.getText())
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
        builder.create();
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
