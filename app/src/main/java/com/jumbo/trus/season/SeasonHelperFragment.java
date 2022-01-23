package com.jumbo.trus.season;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.jumbo.trus.CustomAddFragment;
import com.jumbo.trus.R;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.validator.DateTextWatcher;
import com.jumbo.trus.validator.MultiDateFieldValidator;
import com.jumbo.trus.validator.NameTextWatcher;
import com.jumbo.trus.validator.TextFieldValidator;

import java.util.List;
import java.util.Objects;

public class SeasonHelperFragment extends CustomAddFragment {

    private static final String TAG = "SeasonHelperFragment";

    protected TextInputLayout textCalendarBeginning, textCalendarEnding, textName;
    private AutoCompleteTextView tvCalendarBeginning, tvCalendarEnding;
    private ProgressBar progress_bar;
    protected AppCompatButton btnCommit, btnDelete;

    protected SeasonsViewModel seasonsViewModel;

    private TextFieldValidator nameValidator;
    private TextFieldValidator dateValidatorBeg;
    private TextFieldValidator dateValidatorEnd;
    private MultiDateFieldValidator multiDateFieldValidator;

    protected Season season;
    private List<Season> seasonList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_season_plus, container, false);
        progress_bar = view.findViewById(R.id.progress_bar);
        btnCommit = view.findViewById(R.id.btnCommit);
        btnDelete = view.findViewById(R.id.btnDelete);
        textName = view.findViewById(R.id.textName);
        textCalendarBeginning = view.findViewById(R.id.textCalendarBeginning);
        tvCalendarBeginning = view.findViewById(R.id.tvCalendarBeginning);
        textCalendarEnding = view.findViewById(R.id.textCalendarEnding);
        tvCalendarEnding = view.findViewById(R.id.tvCalendarEnding);
        nameValidator = new TextFieldValidator(textName);
        dateValidatorBeg = new TextFieldValidator(textCalendarBeginning);
        dateValidatorEnd = new TextFieldValidator(textCalendarEnding);
        multiDateFieldValidator = new MultiDateFieldValidator(textCalendarBeginning, textCalendarEnding);
        Objects.requireNonNull(textName.getEditText()).addTextChangedListener(new NameTextWatcher(nameValidator));
        Objects.requireNonNull(textCalendarBeginning.getEditText()).addTextChangedListener(new DateTextWatcher(dateValidatorBeg));
        Objects.requireNonNull(textCalendarEnding.getEditText()).addTextChangedListener(new DateTextWatcher(dateValidatorEnd));
        btnCommit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        nullValidationsErrors(textName, textCalendarBeginning, textCalendarEnding);
        setEditTextBold(textName.getEditText(), textCalendarBeginning.getEditText(), textCalendarEnding.getEditText());
        textCalendarBeginning.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCalendarDialog(textCalendarBeginning.getEditText());
            }
        });
        textCalendarEnding.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCalendarDialog(textCalendarEnding.getEditText());
            }
        });
        setTodaysDate(textCalendarBeginning.getEditText());
        tvCalendarBeginning.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayCalendarDialog(textCalendarBeginning.getEditText());
                }
            }
        });
        tvCalendarEnding.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayCalendarDialog(textCalendarEnding.getEditText());
                }
            }
        });
        seasonsViewModel = new ViewModelProvider(requireActivity()).get(SeasonsViewModel.class);
        seasonsViewModel.init();
        multiDateFieldValidator = new MultiDateFieldValidator(textCalendarBeginning, textCalendarEnding);
        seasonsViewModel.getSeasons().observe(getViewLifecycleOwner(), new Observer<List<Season>>() {
            @Override
            public void onChanged(List<Season> seasons) {
                seasonList = seasons;
            }
        });

        seasonsViewModel.isUpdating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        seasonsViewModel.getAlert().observe(getViewLifecycleOwner(), new Observer<String>() {
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

    @Override
    public void onStart() {
        nullValidationsErrors(textName, textCalendarBeginning, textCalendarEnding);
        super.onStart();
    }

    protected void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progress_bar.setVisibility(View.GONE);
    }



    private boolean checkFieldsValidation(String name, String dateBeginning, String dateEnding) {
        boolean nameCheck = nameValidator.checkNameField(name);
        boolean dateCheckBeg = dateValidatorBeg.checkDateField(dateBeginning);
        boolean dateCheckEnd = dateValidatorEnd.checkDateField(dateEnding);
        if (!dateCheckBeg || !dateCheckEnd) {
            return false;
        }
        boolean dateBeforeCheck = multiDateFieldValidator.checkIfDateStartIsBeforeEnd(dateBeginning, dateEnding);
        if (!dateBeforeCheck) {
            return false;
        }

        boolean dateOver = multiDateFieldValidator.checkSeasonOverlap(dateBeginning, dateEnding, seasonList, season);
        return nameCheck && dateOver;
    }

    protected void onCommitValidationTrue(final String name, final String dateBegin, final String dateEnd) {

    }


    @Override
    protected void commitClicked() {
        String name = textName.getEditText().getText().toString();
        String dateBeg = textCalendarBeginning.getEditText().getText().toString();
        String dateEnd = textCalendarEnding.getEditText().getText().toString();
        if (checkFieldsValidation(name, dateBeg, dateEnd)) {
            onCommitValidationTrue(name, dateBeg, dateEnd);
        }
    }
}
