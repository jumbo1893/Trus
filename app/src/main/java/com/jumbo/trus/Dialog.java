package com.jumbo.trus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;


import java.util.Calendar;

public abstract class Dialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "Dialog";

    protected Flag flag;
    protected Model model;

    public Dialog(Flag flag) {
        this.flag = flag;
    }

    public Dialog(Flag flag, Model model) {
        this.flag = flag;
        this.model = model;
    }

    public Dialog(Model model) {
        this.model = model;
    }

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
        Log.d(TAG, "zobrazuji kalendář");
    }

    protected void displayDeleteConfirmationDialog(final Model model, final IFragment iFragment, String title) {
        Log.d(TAG, "displayDeleteConfirmationDialog zobrazen");
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(title);

        alert.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (iFragment.deleteModel(model)) {
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
}


