package com.app.artclass.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.LocalAdapter;

import java.util.List;

public class AddToGroupDialog extends DialogFragment {
    private LocalAdapter outerAdapter;
    private GroupType mGroupType;
    private List<Student> excludedStudents;

    public AddToGroupDialog(LocalAdapter outerAdapter, GroupType mGroupType, List<Student> excludedStudents) {
        this.outerAdapter = outerAdapter;
        this.mGroupType = mGroupType;
        this.excludedStudents = excludedStudents;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogCreationHandler dialogCreationHandler = new DialogCreationHandler();
        return dialogCreationHandler.AddStudentsToGroup(this, outerAdapter, mGroupType, excludedStudents);
    }
}
