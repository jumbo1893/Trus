package com.jumbo.trus.user.interaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.CustomUserFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;
import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.user.User;

import java.util.List;

public class ResetPasswordsFragment extends CustomUserFragment implements OnListListener {

    private static final String TAG = "FactDialog";

    //widgety
    private RecyclerView rc_list;

    private UsersInteractionsViewModel viewModel;

    private SimpleRecycleViewAdapter simpleRecycleViewAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UsersInteractionsViewModel.class);
        viewModel.init();
        viewModel.getWaitingForPasswordResetUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                initUserRecycleView(users);
                setAdapter();
            }
        });
    }

    private void initUserRecycleView(List<User> userList) {
        simpleRecycleViewAdapter = new SimpleRecycleViewAdapter(userList, getActivity(), this);
    }

    private void setAdapter() {
        if (simpleRecycleViewAdapter != null) {
            rc_list.setAdapter(simpleRecycleViewAdapter);
        }
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onItemClick(int position) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            final User user = viewModel.getWaitingForPasswordResetUsers().getValue().get(position);
            alert.setTitle("resetovat heslo ");
            alert.setMessage("Resetovat heslo uživatele " + user.getName() + " ?");

            alert.setPositiveButton("Resetovat", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    viewModel.changeUserStatus(user, User.Status.PASSWORD_RESET);
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("Zavřít", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            alert.show();

    }

    @Override
    public void onItemLongClick(int position) {

    }
}
