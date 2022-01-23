package com.jumbo.trus.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.Flag;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;
import com.jumbo.trus.listener.OnListListener;

import java.util.List;
import java.util.Objects;

public class UserListDialog extends DialogFragment implements View.OnClickListener, OnListListener {

    private static final String TAG = "FactDialog";

    //widgety
    private Button btn_commit;
    private TextView tv_title;
    private RecyclerView rc_list;

    private List<User> userList;
    private Flag flag;
    private IUserInteraction iUserInteraction;

    private SimpleRecycleViewAdapter simpleRecycleViewAdapter;


    public UserListDialog(List<User> userList, Flag flag, IUserInteraction iUserInteraction) {
        this.userList = userList;
        this.flag = flag;
        this.iUserInteraction = iUserInteraction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fact, container, false);
        btn_commit = view.findViewById(R.id.btn_commit);
        tv_title = view.findViewById(R.id.tv_title);
        rc_list = view.findViewById(R.id.rc_list);
        rc_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        btn_commit.setOnClickListener(this);
        chooseTitle();
        initUserRecycleView();
        setAdapter();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void chooseTitle() {
        if (flag == Flag.USER_APPROVE) {
            if (userList.isEmpty()) {
                tv_title.setText("Momentálně žádný uživatel nečeká na schválení");
            }
            else {
                tv_title.setText("Schválení nových uživatelů");
            }
        }
        else if (flag == Flag.USER_RESET_PASSWORD) {
            if (userList.isEmpty()) {
                tv_title.setText("Momentálně žádný uživatel nečeká na reset hesla");
            }
            else {
                tv_title.setText("Reset hesla uživatelů");
            }
        }
        else if (flag == Flag.USER_ALL) {
            if (userList.isEmpty()) {
                tv_title.setText("Momentálně žádný uživatel není k dispo");
            }
            else {
                tv_title.setText("Změna stavu uživatelů");
            }
        }
    }


    private void initUserRecycleView() {
        simpleRecycleViewAdapter = new SimpleRecycleViewAdapter(userList, getActivity(), this);
    }

    private void setAdapter() {
        if (simpleRecycleViewAdapter != null) {
            rc_list.setAdapter(simpleRecycleViewAdapter);
        }
        rc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                getDialog().dismiss();
            }
        }
    }


    @Override
    public void onItemClick(int position) {
        final User user = userList.get(position);
        if (flag == Flag.USER_APPROVE) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("schválit uživatele");
            alert.setMessage("Schválit uživatele " + user.getName() + " a udělit mu přístupy?");

            alert.setPositiveButton("Schválit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (iUserInteraction.approveNewUser(user, true)) {
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                    else {
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                }
            });
            alert.setNegativeButton("Zamítnout", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (iUserInteraction.approveNewUser(user, false)) {
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                }
            });
            alert.setNeutralButton("Zavřít", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            });
            alert.show();
        }
        else if (flag == Flag.USER_RESET_PASSWORD) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("resetovat heslo ");
            alert.setMessage("Resetovat heslo uživatele " + user.getName() + " ?");

            alert.setPositiveButton("Resetovat", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (iUserInteraction.resetPassword(user)) {
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                }
            });
            alert.setNegativeButton("Zavřít", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            });
            alert.show();
        }
        else if (flag == Flag.USER_ALL) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("změna uživatele " + user.getName());
            alert.setMessage("Změna statusu z " + user.getStatus() + " a pravomocí " + user.getPermission());

            if (user.getStatus() == User.Status.DENIED) {
                alert.setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (iUserInteraction.changeUserStatus(user, User.Status.APPROVED)) {
                            Objects.requireNonNull(getDialog()).dismiss();
                        }
                    }
                });
            }
            else {
                alert.setPositiveButton("DENIED", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (iUserInteraction.changeUserStatus(user, User.Status.DENIED)) {
                            Objects.requireNonNull(getDialog()).dismiss();
                        }
                    }
                });
            }
            if (user.getPermission() == User.Permission.USER) {
                alert.setNegativeButton("READ_ONLY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (iUserInteraction.changeUserPermission(user, User.Permission.READ_ONLY)) {
                            Objects.requireNonNull(getDialog()).dismiss();
                        }
                    }
                });
            }
            else {
                alert.setNegativeButton("USER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (iUserInteraction.changeUserPermission(user, User.Permission.USER)) {
                            Objects.requireNonNull(getDialog()).dismiss();
                        }
                    }
                });
            }
            alert.setNeutralButton("zavřít", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            });
            alert.show();
        }
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
