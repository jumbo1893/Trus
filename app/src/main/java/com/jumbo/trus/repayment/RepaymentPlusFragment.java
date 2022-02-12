package com.jumbo.trus.repayment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomAddFragment;
import com.jumbo.trus.IFragment;
import com.jumbo.trus.Model;
import com.jumbo.trus.R;
import com.jumbo.trus.SimpleDividerItemDecoration;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.validator.AmountTextWatcher;
import com.jumbo.trus.validator.NoteTextWatcher;
import com.jumbo.trus.validator.TextFieldValidator;

import java.util.List;
import java.util.Objects;

public class RepaymentPlusFragment extends CustomAddFragment implements IFragment {

    private static final String TAG = "RepaymentDialog";

    //widgety
    private RecyclerView rc_players;
    private TextInputLayout textAmount, textNote;
    private AutoCompleteTextView tvPlayer;
    private Button btnCommit;
    private ProgressBar progress_bar;
    //vars
    private SimpleRecycleViewAdapter adapter;

    private RepaymentPlusViewModel repaymentPlusViewModel;

    private TextFieldValidator noteValidator;
    private TextFieldValidator amountValidator;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repayment_plus, container, false);
        rc_players = view.findViewById(R.id.rc_players);
        textAmount = view.findViewById(R.id.textAmount);
        textNote = view.findViewById(R.id.textNote);
        tvPlayer = view.findViewById(R.id.tvPlayer);
        rc_players = view.findViewById(R.id.rc_players);
        progress_bar = view.findViewById(R.id.progress_bar);
        rc_players.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        noteValidator = new TextFieldValidator(textNote);
        amountValidator = new TextFieldValidator(textAmount);
        Objects.requireNonNull(textNote.getEditText()).addTextChangedListener(new NoteTextWatcher(noteValidator));
        Objects.requireNonNull(textAmount.getEditText()).addTextChangedListener(new AmountTextWatcher(amountValidator));
        btnCommit = view.findViewById(R.id.btnCommit);
        btnCommit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repaymentPlusViewModel = new ViewModelProvider(requireActivity()).get(RepaymentPlusViewModel.class);
        repaymentPlusViewModel.init();
        repaymentPlusViewModel.setPickedPlayer(sharedViewModel.getPickedPlayerForEdit().getValue());
        repaymentPlusViewModel.getRepayments().observe(getViewLifecycleOwner(), new Observer<List<Repayment>>() {
            @Override
            public void onChanged(List<Repayment> repayments) {
                initRecycleView(repayments);
                setAdapter();
            }
        });
        repaymentPlusViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        repaymentPlusViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //podmínka aby se upozornění nezobrazovalo vždy když se mění fragment
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        repaymentPlusViewModel.closeFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b && getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED) {
                    openPreviousFragment();
                }
            }
        });
        repaymentPlusViewModel.getPlayer().observe(getViewLifecycleOwner(), new Observer<Player>() {
            @Override
            public void onChanged(Player player) {
                setTextsToEditPlayer(player);
            }
        });
    }

    private void setTextsToEditPlayer(Player player) {
        tvPlayer.setText(player.getName());
    }


    private void initRecycleView(List<Repayment> repayments) {
        adapter = new SimpleRecycleViewAdapter(repayments, getActivity(), this);
    }


    private void setAdapter() {
        rc_players.setAdapter(adapter);
        rc_players.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    protected void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }

    private boolean checkFieldsValidation(String note, String amount) {
        boolean nameCheck = noteValidator.checkNoteField(note);
        boolean dateCheck = amountValidator.checkAmount(amount);
        return nameCheck && dateCheck;
    }

    @Override
    protected void commitClicked() {
        String note = textNote.getEditText().getText().toString();
        String amount = textAmount.getEditText().getText().toString();
        if (checkFieldsValidation(note, amount)) {
            repaymentPlusViewModel.AddRepaymentToPlayerInRepository(Integer.parseInt(amount), note, user);
        }
    }

    @Override
    protected void itemClick(int position) {
        displayDeleteConfirmationDialog(repaymentPlusViewModel.getRepayments().getValue().get(position),this, "Smazat platbu", "Opravdu chcete hráči tuto platbu?");
    }


    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public boolean deleteModel(Model model) {
        repaymentPlusViewModel.removePlayerRepaymentsInRepository((Repayment) model, user);
        return true;
    }
}
