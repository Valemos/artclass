package com.app.artclass.fragments.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalTime;

public class TimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private TextView timeOutputView;
    private LocalTime startTime;

    public TimePickerFragment(LocalTime startTime, TextView timeOutputView, TimePickerDialog.OnTimeSetListener timeSetListener) {
        this.timeSetListener = timeSetListener;
        this.timeOutputView = timeOutputView;
        this.startTime = startTime;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogCreationHandler dialogCreationHandler = new DialogCreationHandler();
        return dialogCreationHandler.TimePicker(getContext(), timeOutputView, startTime, timeSetListener);
    }
}
