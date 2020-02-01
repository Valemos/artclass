package com.app.artclass.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.artclass.Logger;

public class ConfirmDeleteObjectDialog extends DialogFragment {
    private String objectName;
    private Runnable positiveAction;
    private Runnable negativeAction;

    public ConfirmDeleteObjectDialog(String objectName, Runnable positiveAction, Runnable negativeAction) {
        this.objectName = objectName;
        this.positiveAction = positiveAction;
        this.negativeAction = negativeAction;
        Logger.getInstance().appendLog(getClass(),"init dialog");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogCreationHandler dialogCreationHandler = new DialogCreationHandler();
        return dialogCreationHandler.ConfirmDeleteObject(getParentFragment().getContext(), objectName, positiveAction, negativeAction);
    }
}
