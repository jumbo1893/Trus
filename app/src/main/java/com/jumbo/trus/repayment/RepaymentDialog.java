package com.jumbo.trus.repayment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jumbo.trus.OnListListener;
import com.jumbo.trus.R;
import com.jumbo.trus.adapters.SimpleRecycleViewAdapter;
import com.jumbo.trus.player.Player;

public class RepaymentDialog extends DialogFragment implements View.OnClickListener, OnListListener {

    private static final String TAG = "RepaymentDialog";

    //widgety
    private RecyclerView rc_players;
    private TextView tv_title;
    private Button btn_cancel, btn_commit;
    private EditText et_amount, et_note;

    //vars
    private IRepaymentFragment iRepaymentFragment;
    private SimpleRecycleViewAdapter adapter;
    private Player player;


    public RepaymentDialog(Player player) {
        this.player = player;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_repayment, container, false);
        rc_players = view.findViewById(R.id.rc_players);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_commit = view.findViewById(R.id.btn_commit);
        et_amount = view.findViewById(R.id.et_amount);
        et_note = view.findViewById(R.id.et_note);
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText("Platba u hráče " + player.getName());
        btn_cancel.setOnClickListener(this);
        btn_commit.setOnClickListener(this);

        initRecycleView();
        setAdapter();
        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit: {
                int amount;
                if (!et_amount.getText().toString().isEmpty()) {
                    amount = Integer.parseInt(et_amount.getText().toString());
                }
                else {
                    amount = 0;
                }
                String note = et_note.getText().toString();
                if (iRepaymentFragment.createNewRepayment(amount, note, player)) {
                    getDialog().dismiss();
                }
                break;
            }
            case R.id.btn_cancel: {
                getDialog().dismiss();
                break;
            }
        }
    }


    private void initRecycleView() {
        adapter = new SimpleRecycleViewAdapter(player.getRepayments(), getActivity(), this);
    }


    private void setAdapter() {
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void displayDeleteConfirmationDialog(final Repayment repayment) {
        Log.d(TAG, "displayDeleteConfirmationDialog zobrazen");
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Smazat");
        alert.setMessage("Opravdu chcete smazat tuto transakci?");

        alert.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (iRepaymentFragment.deleteRepayment(repayment, player)) {
                    getDialog().dismiss();

                }
            }
        });
        alert.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iRepaymentFragment = (IRepaymentFragment) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e);
        }
    }

    @Override
    public void onItemClick(int position) {
        displayDeleteConfirmationDialog(player.getRepayments().get(position));
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
