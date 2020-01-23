package com.app.artclass.fragments;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.entity.Abonement;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;
import com.app.artclass.recycler_adapters.GroupsRecyclerAdapter;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DialogHandler {

    private static DialogHandler instance;

    private StudentsRepository studentsRepository;
    private final Application mApplication;

    private Runnable proc_positive = null;
    private Runnable proc_negative = null;

    public static DialogHandler getInstance(Application application){
        if(instance == null){
            instance = new DialogHandler(application);
        }

        return instance;
    }

    public static DialogHandler getInstance(){return instance;}

    private DialogHandler(Application application) {
        this.studentsRepository = StudentsRepository.getInstance(application);
        mApplication = application;
    }

    // default listeners
    private DialogInterface.OnClickListener defaultPositiveClickListener = (dialog, which) -> {
        if(proc_positive!=null)
            proc_positive.run();
    };

    private DialogInterface.OnClickListener defaultNegativeClickListener = (dialog, which) -> {
        if(proc_negative!=null)
            proc_negative.run();
    };

    // Dialog. --------------------------------------------------------------

    public void ConfirmDeleteObject(Context context,String objectName, Runnable positiveProcedure, Runnable negativeProcedure) {

        proc_positive = positiveProcedure;
        proc_negative = negativeProcedure;

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(context.getString(R.string.title_confirm_delete_student));
        dialog.setMessage(context.getString(R.string.message_confirm_delete) + "\n" +objectName);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.label_OK),defaultPositiveClickListener);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),defaultNegativeClickListener);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    public void AlertDialog(Context context,String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.label_OK,null)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .create().show();
    }

    public void AddNewStudent(Context context,StudentsRecyclerAdapter outerAdapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_new_student, null);

        Spinner spinnerAbonement = dialogView.findViewById(R.id.dialogadd_spinner_abonement);
        Spinner spinnerDay= dialogView.findViewById(R.id.dialogadd_spinner_day);
        Spinner spinnerTime= dialogView.findViewById(R.id.dialogadd_spinner_time);

        // abonement type spinner
        SpinnerAdapter abonAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getAllAbonements());
        spinnerAbonement.setAdapter(abonAdapter);

        // group time spinner
        SpinnerAdapter timeAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getAllGroupTypes());
        spinnerTime.setAdapter(timeAdapter);

        // weekdays spinner
        final SpinnerAdapter dayAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getWeekdayLabels());
        spinnerDay.setAdapter(dayAdapter);

        dialogView.setTag(outerAdapter);
        final View dialogViewFinalized = dialogView;
        DialogInterface.OnClickListener addStudentListener = (dialog, which) -> {
            EditText studNameField = ((AlertDialog)dialog).findViewById(R.id.dialogadd_fullname);
            EditText phoneNumberField = ((AlertDialog)dialog).findViewById(R.id.dialogadd_phone);
            Spinner spinnerAbonement1 = ((AlertDialog)dialog).findViewById(R.id.dialogadd_spinner_abonement);
            Spinner spinnerDay1 = ((AlertDialog)dialog).findViewById(R.id.dialogadd_spinner_day);
            Spinner spinnerTime1 = ((AlertDialog)dialog).findViewById(R.id.dialogadd_spinner_time);

            if(studNameField.getText().length()>0) {
                Abonement curAbonement = (Abonement) spinnerAbonement1.getSelectedItem();

                Student studentNew = new Student(studNameField.getText().toString(), 0, curAbonement); // get the value of list with abonements

                if(phoneNumberField.getText().length()>0){
                    studentNew.getPhoneList().add(phoneNumberField.getText().toString());
                }

                studentsRepository.addStudent(studentNew);

                // get first day to start adding lessons
                // Calendar calendar = Calendar.getInstance();
                LocalDate currentDate = LocalDate.now();

                // we need to start with a first weekday that matches
                int weekdayCurrent = currentDate.getDayOfWeek().getValue();
                int weekdayChosen = UserSettings.getInstance().getWeekdayIndex(spinnerDay1.getSelectedItem().toString());

                int shift = weekdayChosen - weekdayCurrent;
                if(shift < 0) shift += 7;

                currentDate = currentDate.plusDays(shift);


                // setup lessons
                int lessonDuration=UserSettings.getDefaultLessonHours();
                int hoursToWorkIterator = studentNew.getHoursBalance();
                GroupType curGroupType = (GroupType) spinnerTime1.getSelectedItem();

                for(int i = hoursToWorkIterator; i > 0; i-=lessonDuration){
                    studentsRepository.addLesson(new Lesson(currentDate, studentNew, curGroupType));
                    currentDate = currentDate.plusDays(7);
                }

                StudentsRecyclerAdapter studAdapter = (StudentsRecyclerAdapter) dialogViewFinalized.getTag();
                studAdapter.addStudent(studentNew);
                studAdapter.notifyDataSetChanged();

                // init lessons in database
            }
        };

        builder
                .setView(dialogView)
                .setTitle(R.string.title_add_student_togroup)
                .setPositiveButton(context.getString(R.string.label_add), addStudentListener)
                .setNegativeButton(context.getString(R.string.label_cancel), (dialog, which) -> dialog.cancel())
                .create().show();
    }

    public void MoveLessonToAnotherDay(Context context,Lesson lesson){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_move_lesson, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreateNewGroup(@NonNull Fragment fragment,
                               final RecyclerView.Adapter outerAdapter,
                               @Nullable LocalDate date,
                               @Nullable GroupType groupType,
                               @Nullable List<Student> excludedStudents){

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());

        View dialogView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.dialog_create_group, null);
        RecyclerView listSelectStudents = dialogView.findViewById(R.id.dialogcreate_select_students_list);

        // setup spinners for group specification
        // group time spinner
        Spinner spinnerTime= dialogView.findViewById(R.id.dialogcreate_spinner_time);

        final SpinnerAdapter timeAdapter = new ArrayAdapter<>(fragment.getContext(),
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getGroupLabels());
        spinnerTime.setAdapter(timeAdapter);

        if(groupType != null) {
            spinnerTime.setSelection(UserSettings.getInstance().getGroupLabels().indexOf(groupType.getGroupName()));
            spinnerTime.setClickable(false);
            spinnerTime.setFocusable(View.NOT_FOCUSABLE);
            spinnerTime.setBackgroundColor(fragment.getContext().getColor(R.color.colorNonAccent));
        }
        // date picker for choosing date

        TextView datePickerView = dialogView.findViewById(R.id.dialogcreate_datepicker_text);
        String datePickerDateText;
        if(date == null) {
            datePickerDateText = LocalDate.now().format(DatabaseConverters.getDateFormatter());
            datePickerView.setOnClickListener(new View.OnClickListener() {

                private long mLastClickTime = 0;

                @Override
                public void onClick(final View textView) {

                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    LocalDate date = LocalDate.parse(
                            ((TextView)textView).getText().toString(),
                            DatabaseConverters.getDateFormatter());
                    DialogHandler.getInstance().DatePicker(fragment.getContext(),(TextView) textView, date);
                }
            });
        }else {
            datePickerDateText = date.format(DatabaseConverters.getDateFormatter());
            datePickerView.setClickable(false);
            datePickerView.setFocusable(false);
            datePickerView.setBackgroundColor(fragment.getContext().getColor(R.color.colorNonAccent));
        }
        datePickerView.setText(datePickerDateText);


        // setup students list
        // all students by default
        // if date and time null (we are adding students to existing group)
        // then delete students that already in the group

        StudentsRecyclerAdapter studentsAdapter = new StudentsRecyclerAdapter(
                fragment,
                R.layout.item_dialog_student_selector,
                R.id.name_view,
                null,
                new ArrayList<>());
        listSelectStudents.setAdapter(studentsAdapter);
        listSelectStudents.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        dialogView.setTag(R.id.adapter,studentsAdapter);

        studentsRepository.getAllStudents().observe(fragment, studentList -> {

            if(date != null && excludedStudents != null && excludedStudents.size()>0){
                studentList.removeAll(excludedStudents);
            }

            studentsAdapter.setStudents(studentList);
        });

        dialogView.setTag(R.id.tag_spinner_time,spinnerTime);
        dialogView.setTag(R.id.tag_date_picker,datePickerView);

        DialogInterface.OnClickListener createGroupClickListener = (dialog, which) -> {
            TextView dateText = ((AlertDialog)dialog).findViewById(R.id.dialogcreate_datepicker_text);
            Spinner timeSpinner = ((AlertDialog)dialog).findViewById(R.id.dialogcreate_spinner_time);
            StudentsRecyclerAdapter innerStudentsAdapter = (StudentsRecyclerAdapter)
                (
                (RecyclerView)
                ((AlertDialog)dialog)
                .findViewById(R.id.dialogcreate_select_students_list)
                )
                .getAdapter();

            LocalDate groupDate = LocalDate.parse(dateText.getText().toString(), DatabaseConverters.getDateFormatter());
            GroupType curGroupType = UserSettings.getInstance().getGroupType((String) timeSpinner.getSelectedItem());

            assert studentsAdapter != null;
            studentsRepository.getStudentsForNames(studentsAdapter.getSelectedStudentNames()).observe(fragment,studentList -> {

                studentsRepository.addGroup(LocalDateTime.of(groupDate, curGroupType.getTime()), studentList);

                if (outerAdapter != null) {
                    if (outerAdapter.getClass().equals(GroupsRecyclerAdapter.class)) {
                        ((GroupsRecyclerAdapter) outerAdapter).addGroup(new GroupsRecyclerAdapter.GroupData(groupDate, curGroupType));
                    } else if (outerAdapter.getClass().equals(StudentsRecyclerAdapter.class)) {
                        ((StudentsRecyclerAdapter) outerAdapter).setStudents(studentList);
                    }
                }
            });
        };

        builder.setView(dialogView)
                .setPositiveButton(R.string.label_add, createGroupClickListener)
                .setNegativeButton(R.string.label_cancel, defaultNegativeClickListener)
                .create().show();
    }

    private void DatePicker(Context context,final TextView textView, LocalDate date) {

        final DatePickerDialog datePickerDialog = getDatePickerDialog(textView.getContext(), date,null);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.label_OK), (dialog, which) ->
                textView.setText(
                LocalDate.of(
                datePickerDialog.getDatePicker().getYear(),
                datePickerDialog.getDatePicker().getMonth()+1,
                datePickerDialog.getDatePicker().getDayOfMonth())
                .format(DatabaseConverters.getDateFormatter())
                )
        );
        datePickerDialog.show();
    }

    public DatePickerDialog getDatePickerDialog(Context context, LocalDate curDate, DatePickerDialog.OnDateSetListener listenerOnDate){
        return new DatePickerDialog(context, listenerOnDate, curDate.getYear(), curDate.getMonthValue()-1, curDate.getDayOfMonth());
    }
}