package com.app.artclass.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.GroupTypeSpinnerAdapter;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;

public class AddNewStudentDialog extends androidx.fragment.app.DialogFragment {

    private StudentsRecyclerAdapter outerAdapter;
    private EditText studentNameField;
    private EditText notesField;
    private Spinner spinnerGroupType;


    public AddNewStudentDialog(StudentsRecyclerAdapter outerAdapter) {
        this.outerAdapter = outerAdapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_new_student, null);

        studentNameField = dialogView.findViewById(R.id.dialogadd_fullname);
        notesField = dialogView.findViewById(R.id.dialogadd_notes);
        spinnerGroupType = dialogView.findViewById(R.id.dialogadd_spinner_group_type);

        /**
         * all spinners must have element at position 0
         * it indicates that user not selected any options
         */

        // group type spinner
        SpinnerAdapter groupTypeAdapter = new GroupTypeSpinnerAdapter(getContext(),R.layout.item_spinner_group_type,
                UserSettings.getInstance().getAllGroupTypesWithDefault(getContext()));

        spinnerGroupType.setAdapter(groupTypeAdapter);
        spinnerGroupType.setSelection(0,false);


        return new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle(R.string.title_add_student_togroup)
                .setPositiveButton(getContext().getString(R.string.label_add), null)
                .setNegativeButton(getContext().getString(R.string.label_cancel), (dialog, which) -> dialog.dismiss())
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog dialog = (AlertDialog) getDialog();
        if(dialog!=null){
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if(studentNameField.getText().length()==0){
                    TextView alertMessage = dialog.findViewById(R.id.fill_name_message);
                    alertMessage.setVisibility(View.VISIBLE);
                }else{
                    StudentsRepository.getInstance().addStudent(
                            new Student(studentNameField.getText().toString(), notesField.getText().toString(), 0));
                    dialog.dismiss();
                }
            });
        }
    }
}
