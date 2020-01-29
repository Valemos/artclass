package com.app.artclass.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;

public class AddNewStudentDialog extends androidx.fragment.app.DialogFragment {
    private StudentsRecyclerAdapter outerAdapter;

    public AddNewStudentDialog(StudentsRecyclerAdapter outerAdapter) {
        this.outerAdapter = outerAdapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogCreationHandler dialogCreationHandler = new DialogCreationHandler();
        return dialogCreationHandler.AddNewStudent(getParentFragment().getContext(), outerAdapter);
    }
}
