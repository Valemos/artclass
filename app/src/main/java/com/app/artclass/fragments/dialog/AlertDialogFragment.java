package com.app.artclass.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.artclass.Logger;

public class AlertDialogFragment extends DialogFragment {
    private String title;
    private String message;
    private Runnable positive_action;

    public AlertDialogFragment(String title, String message, Runnable positive_action) {
        this.title = title;
        this.message = message;
        this.positive_action = positive_action;
        Logger.getInstance().appendLog(getClass(),"init dialog");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogCreationHandler dialogCreationHandler = new DialogCreationHandler();
        return dialogCreationHandler.AlertDialog(getContext(), title, message, positive_action);
    }
}
