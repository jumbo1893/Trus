package com.jumbo.trus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class CustomAddFragment extends CustomUserFragment implements View.OnClickListener {

    protected void displayCalendarDialog(final EditText et_calendar) {

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                et_calendar.setText(dayOfMonth + "/" + month + "/" + year);

            }
        }, year, month, day);
        datePickerDialog.show();
    }

    protected void setTodaysDate(final EditText et_calendar) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        et_calendar.setText(day + "/" + month + "/" + year);
    }

    protected void displayDeleteConfirmationDialog(final Model model, final IFragment iFragment, String title, String message) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(title);
        alert.setMessage(message);

        alert.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (iFragment.deleteModel(model)) {
                    dialog.dismiss();
                    openPreviousFragment();
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

    protected void setEditTextBold(EditText... editTexts) {
        for (EditText editText : editTexts ) {
            editText.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    protected void nullValidationsErrors(TextInputLayout... textInputLayouts) {
        for (TextInputLayout textInputLayout : textInputLayouts) {
            textInputLayout.setError(null);
        }
    }

    protected void commitClicked() {

    }

    protected void cancelClicked() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCommit:
                commitClicked();
                break;
            case R.id.btnDelete:
                cancelClicked();
                break;
        }
    }
}
