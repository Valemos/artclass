package com.app.artclass.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.Logger;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.WEEKDAY;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.LocalAdapter;
import com.app.artclass.recycler_adapters.GroupsListRecyclerAdapter;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddToGroupDialog extends DialogFragment {

    private LocalAdapter outerAdapter;
    private GroupType mGroupType;
    private List<Student> excludedStudents;

    private StudentsRecyclerAdapter dialogStudentsAdapter;
    private Spinner spinnerWeekday;
    private List<String> weekdayNames;
    private TextView timeSelector;
    private EditText groupTypeNameField;
    private SearchView studentFilterView;



    public AddToGroupDialog(LocalAdapter outerAdapter, GroupType mGroupType, List<Student> excludedStudents) {
        this.outerAdapter = outerAdapter;
        this.mGroupType = mGroupType;
        this.excludedStudents = excludedStudents;
        Logger.getInstance().appendLog(getClass(),"init dialog");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_to_group, null);

        spinnerWeekday = dialogView.findViewById(R.id.spinner_weekday);
        timeSelector = dialogView.findViewById(R.id.time_selector);
        groupTypeNameField = dialogView.findViewById(R.id.new_group_name_view);

        studentFilterView = dialogView.findViewById(R.id.dialog_search_view);
        RecyclerView listSelectStudents = dialogView.findViewById(R.id.dialogcreate_select_students_list);

        // if group needs to be created


        weekdayNames = UserSettings.getInstance().getWeekdaysWithDefault(getContext())
                .stream().map(WEEKDAY::getName).collect(Collectors.toList());
        if(mGroupType == null) {
            spinnerWeekday.setAdapter(new ArrayAdapter<>(
                    getContext(),
                    R.layout.item_spinner,
                    R.id.text_spinner_view,
                    weekdayNames
            ));
            timeSelector.setText(getContext().getString(R.string.no_time_selected));
            timeSelector.setOnClickListener(v -> {
                TimePickerFragment timePickerFragment = new TimePickerFragment(
                        LocalTime.of(LocalTime.now().getHour(),0),
                        timeSelector,
                        (view, hourOfDay, minute) -> {
                            timeSelector.setText(LocalTime.of(hourOfDay,minute).format(DatabaseConverters.getTimeFormatter()));
                            timeSelector.setTag(R.id.time,LocalTime.of(hourOfDay,minute));
                        });

                if (getFragmentManager() != null) {
                    timePickerFragment.show(getFragmentManager(), getContext().getString(R.string.time_picker_dialog_tag));
                }
            });
        }
        else{
            spinnerWeekday.setAdapter(new ArrayAdapter<>(
                    getContext(),
                    R.layout.item_spinner,
                    R.id.text_spinner_view,
                    new ArrayList<String>(){{add(mGroupType.getWeekday().getName());}}
            ));

            timeSelector.setText(mGroupType.getTime().format(DatabaseConverters.getTimeFormatter()));

            groupTypeNameField.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary, null));
            groupTypeNameField.setText(mGroupType.getName());
            groupTypeNameField.setTextColor(getContext().getResources().getColor(R.color.secondary_text_color,null));
            groupTypeNameField.setClickable(false);
            groupTypeNameField.setFocusable(false);
        }

        // setup students list
        // all students by default
        // if date and time null (we are adding students to existing group)
        // then delete students that already in the group

        dialogStudentsAdapter = new StudentsRecyclerAdapter(
                this,
                R.layout.item_dialog_student_selector,
                R.id.name_view,
                null,
                true,
                new ArrayList<>());
        listSelectStudents.setAdapter(dialogStudentsAdapter);
        listSelectStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        dialogView.setTag(R.id.adapter,dialogStudentsAdapter);

        // filter students by query in adapter
        FilterInteractionListener filterInteractionListener = new FilterInteractionListener(dialogStudentsAdapter);
        studentFilterView.setOnQueryTextListener(filterInteractionListener);

        // get all required students
        StudentsRepository.getInstance().getAllStudents().observe(this, studentList -> {
            if(excludedStudents != null && excludedStudents.size()>0){
                studentList.removeAll(excludedStudents);
            }
            dialogStudentsAdapter.setStudents(studentList);
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton(R.string.label_add, null)
                .setNegativeButton(R.string.label_cancel, (d, which) -> d.dismiss())
                .create();
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog dialog = (AlertDialog) getDialog();
        if(dialog!=null){
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if(mGroupType==null){
                    if(timeSelector.getTag(R.id.time) == null ||
                       spinnerWeekday.getSelectedItemPosition()==0 ||
                       groupTypeNameField.getText().length()==0){
                        TextView messageField = dialog.findViewById(R.id.dialog_not_correct_message);
                        messageField.setVisibility(View.VISIBLE);
                    }
                    else {
                        mGroupType = new GroupType(
                                (LocalTime) timeSelector.getTag(R.id.time),
                                WEEKDAY.get(weekdayNames.indexOf(spinnerWeekday.getSelectedItem())-1),
                                groupTypeNameField.getText().toString());
                        List<Student> studentsSelected = dialogStudentsAdapter.getCheckedStudents();
                        StudentsRepository.getInstance().addGroupTypeWithStudents(mGroupType, studentsSelected);
                        UserSettings.getInstance().addGroupType(mGroupType);

                        try{
                            GroupsListRecyclerAdapter groupsAdapter = (GroupsListRecyclerAdapter) outerAdapter;
                            groupsAdapter.addGroup(mGroupType, studentsSelected);
                        }catch (Exception e){
                            Logger.getInstance().appendLog(getClass(),"outer adapter is not a group adapter");
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }
                else {
                    StudentsRepository.getInstance().addStudentsToGroup(mGroupType, dialogStudentsAdapter.getCheckedStudents());
                    outerAdapter.update();
                    dialog.dismiss();
                }
            });
        }
    }

    private class FilterInteractionListener implements SearchView.OnQueryTextListener {

        private final StudentsRecyclerAdapter studentsAdapter;

        public FilterInteractionListener(StudentsRecyclerAdapter studentsAdapter) {
            this.studentsAdapter = studentsAdapter;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            studentsAdapter.getFilter().filter(query);
            studentFilterView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            studentsAdapter.getFilter().filter(newText);
            return true;
        }
    }
}
