package com.app.artclass.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.artclass.Logger;

import java.time.LocalDate;

public class DatePickerFragment extends DialogFragment {
    private LocalDate startDate;
    private TextView outputTextView;

    public DatePickerFragment(LocalDate startDate, TextView outputTextView) {
        this.startDate = startDate;
        this.outputTextView = outputTextView;
        Logger.getInstance().appendLog(getClass(),"init dialog");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogCreationHandler dialogCreationHandler = new DialogCreationHandler();
        return dialogCreationHandler.DatePicker(getContext(), outputTextView, startDate);
    }
}
