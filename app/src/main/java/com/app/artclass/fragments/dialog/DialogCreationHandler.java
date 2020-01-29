package com.app.artclass.fragments.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DialogCreationHandler {

    private Runnable proc_positive = null;
    private Runnable proc_negative = null;

    public DialogCreationHandler() {
    }

    // default listeners
    private DialogInterface.OnClickListener defaultPositiveClickListener = (dialog, which) -> {
        if(proc_positive!=null) {
            proc_positive.run();
            Logger.getInstance().appendLog(getClass(),"handled positive runnable");
        }
    };

    private DialogInterface.OnClickListener defaultNegativeClickListener = (dialog, which) -> {
        if(proc_negative!=null) {
            proc_negative.run();
            Logger.getInstance().appendLog(getClass(),"handled negative runnable");
        }
    };

    public AlertDialog ConfirmDeleteObject(Context context, String objectName, Runnable positiveProcedure, Runnable negativeProcedure) {

        proc_positive = positiveProcedure;
        proc_negative = negativeProcedure;

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(context.getString(R.string.title_confirm_delete_student));
        dialog.setMessage(String.format(context.getString(R.string.message_confirm_delete_placeholder),objectName));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.label_OK),defaultPositiveClickListener);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),defaultNegativeClickListener);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        return dialog;
    }

    public AlertDialog AlertDialog(Context context, String title, String message, Runnable positive_action){
        proc_positive = positive_action;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setPositiveButton(R.string.label_OK,defaultPositiveClickListener)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .create();
    }

    public AlertDialog AddNewStudent(Context context, StudentsRecyclerAdapter outerAdapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_new_student, null);

        Spinner spinnerDay= dialogView.findViewById(R.id.dialogadd_spinner_day);
        Spinner spinnerTime= dialogView.findViewById(R.id.dialogadd_spinner_group_type);

        /**
         * all spinners must have element at position 0
         * it indicates that user not selected any options
         */

        // group time spinner
        SpinnerAdapter groupTypeAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                R.id.text_spinner_view,
                UserSettings.getInstance().getAllGroupTypesWithDefault(context));
        spinnerTime.setAdapter(groupTypeAdapter);
        spinnerTime.setSelection(0,false);

        // weekdays spinner
        List<WEEKDAY> weekdayList = new ArrayList<>();
        weekdayList.add(WEEKDAY.getNoWeekday(context));
        weekdayList.addAll(WEEKDAY.getValues());
        final SpinnerAdapter weekdayAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                R.id.text_spinner_view,
                weekdayList);
        spinnerDay.setAdapter(weekdayAdapter);
        spinnerDay.setSelection(0, false);

        dialogView.setTag(outerAdapter);
        final View dialogViewFinalized = dialogView;
        DialogInterface.OnClickListener addStudentListener = (dialog, which) -> {

        };

        return builder.setView(dialogView)
                .setTitle(R.string.title_add_student_togroup)
                .setPositiveButton(context.getString(R.string.label_add), addStudentListener)
                .setNegativeButton(context.getString(R.string.label_cancel), (dialog, which) -> dialog.cancel())
                .create();
    }

    /**
     *  @param fragment
     * @param outerAdapter
     * this adapter will be updated using LocalAdapter:update()
     * @param groupType
     * @param excludedStudents
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public AlertDialog AddStudentsToGroup(@NonNull Fragment fragment, LocalAdapter outerAdapter , @Nullable GroupType groupType, @Nullable List<Student> excludedStudents){

        View dialogView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.dialog_create_group, null);

        Spinner spinnerWeekday = dialogView.findViewById(R.id.spinner_weekday);
        TextView timeSelector = dialogView.findViewById(R.id.time_selector);
        EditText groupTypeNameField = dialogView.findViewById(R.id.new_group_name_view);

        SearchView searchView = dialogView.findViewById(R.id.dialog_search_view);
        RecyclerView listSelectStudents = dialogView.findViewById(R.id.dialogcreate_select_students_list);

        // if group needs to be created
        if(groupType == null) {
            spinnerWeekday.setAdapter(new ArrayAdapter<>(
                    fragment.getContext(),
                    R.layout.item_spinner,
                    R.id.text_spinner_view,
                    UserSettings.getInstance().getWeekdaysWithDefault(fragment.getContext())
                            .stream().map(WEEKDAY::getName).collect(Collectors.toList())
            ));
            timeSelector.setText(fragment.getContext().getString(R.string.no_time_selected));
            timeSelector.setOnClickListener(v -> {
                TimePickerFragment timePickerFragment = new TimePickerFragment(
                        LocalTime.of(LocalTime.now().getHour(),0),
                        timeSelector,
                        (view, hourOfDay, minute) ->
                                timeSelector.setText(
                                        LocalTime.of(hourOfDay,minute).format(DatabaseConverters.getTimeFormatter())
                                ));

                if (fragment.getFragmentManager() != null) {
                    timePickerFragment.show(fragment.getFragmentManager(), fragment.getContext().getString(R.string.time_picker_dialog_tag));
                }
            });
        }
        else{
            spinnerWeekday.setAdapter(new ArrayAdapter<>(
                    fragment.getContext(),
                    R.layout.item_spinner,
                    R.id.text_spinner_view,
                    new ArrayList<String>(){{add(groupType.getWeekday().getName());}}
            ));
            groupTypeNameField.setBackgroundColor(fragment.getContext().getResources().getColor(R.color.colorPrimary, null));
            groupTypeNameField.setText(groupType.getName());
            groupTypeNameField.setTextColor(fragment.getContext().getResources().getColor(R.color.secondary_text_color,null));
            groupTypeNameField.setClickable(false);
            groupTypeNameField.setFocusable(false);
        }

        // setup students list
        // all students by default
        // if date and time null (we are adding students to existing group)
        // then delete students that already in the group

        StudentsRecyclerAdapter studentsAdapter = new StudentsRecyclerAdapter(
                fragment,
                R.layout.item_dialog_student_selector,
                R.id.name_view,
                null,
                true,
                new ArrayList<>());
        listSelectStudents.setAdapter(studentsAdapter);
        listSelectStudents.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        dialogView.setTag(R.id.adapter,studentsAdapter);

        // filter students by query in adapter
        FilterInteractionListener filterInteractionListener = new FilterInteractionListener(studentsAdapter);
        searchView.setOnQueryTextListener(filterInteractionListener);

        // get all required students
        StudentsRepository.getInstance().getAllStudents().observe(fragment, studentList -> {
            if(excludedStudents != null && excludedStudents.size()>0){
                studentList.removeAll(excludedStudents);
            }
            studentsAdapter.setStudents(studentList);
        });


        DialogInterface.OnClickListener createGroupClickListener = (dialog, which) -> {
            GroupType curGroupType = groupType;
            if(curGroupType==null){
                curGroupType = new GroupType(
                        LocalTime.now(),
                        WEEKDAY.getValues().stream()
                                .filter(weekday -> weekday.getName().equals(spinnerWeekday.getSelectedItem()))
                                .findFirst().orElse(WEEKDAY.NO_WEEKDAY),
                        groupTypeNameField.getText().toString());

                StudentsRepository.getInstance().addGroupTypeWithStudents(curGroupType, studentsAdapter.getSelectedStudents());
                outerAdapter.update();
            }else{
                StudentsRepository.getInstance().addStudentsToGroup(curGroupType, studentsAdapter.getSelectedStudents());
                outerAdapter.update();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        return builder.setView(dialogView)
                .setPositiveButton(R.string.label_add, createGroupClickListener)
                .setNegativeButton(R.string.label_cancel, defaultNegativeClickListener)
                .create();
    }

    public TimePickerDialog TimePicker(Context context, TextView timeSelectorView, LocalTime time, TimePickerDialog.OnTimeSetListener timeSetListener) {
        return new TimePickerDialog(context, timeSetListener, time.getHour(), time.getMinute(), true);
    }

    public DatePickerDialog DatePicker(Context context, final TextView outputTextView, @Nullable LocalDate startDate) {
        if(startDate == null)
            startDate = LocalDate.now();

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, null, startDate.getYear(), startDate.getMonthValue()-1, startDate.getDayOfMonth());
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.label_OK), (dialog, which) -> {
                LocalDate outputDate = LocalDate.of(
                        datePickerDialog.getDatePicker().getYear(),
                        datePickerDialog.getDatePicker().getMonth()+1,
                        datePickerDialog.getDatePicker().getDayOfMonth());
                outputTextView.setText(outputDate.format(DatabaseConverters.getDateFormatter()));
                outputTextView.setTag(R.id.date,outputDate);
            });

        Logger.getInstance().appendLog(getClass(),"DatePicker showed dialog");

        return datePickerDialog;
    }

    private class FilterInteractionListener implements SearchView.OnQueryTextListener {

        private final StudentsRecyclerAdapter studentsAdapter;

        public FilterInteractionListener(StudentsRecyclerAdapter studentsAdapter) {
            this.studentsAdapter = studentsAdapter;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            studentsAdapter.getFilter().filter(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            studentsAdapter.getFilter().filter(newText);
            return false;
        }
    }
}