package com.app.artclass.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.SystemClock;
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

import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.entity.Abonement;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.LocalAdapter;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DialogHandler {

    private static DialogHandler instance;

    private StudentsRepository studentsRepository;

    private Runnable proc_positive = null;
    private Runnable proc_negative = null;

    public static DialogHandler getInstance(){
        if(instance == null){
            instance = new DialogHandler();
        }
        return instance;
    }

    private DialogHandler() {
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

    public void AlertDialog(Context context, String title, String message, Runnable positive_action){
        proc_positive = positive_action;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.label_OK,defaultPositiveClickListener)
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
        spinnerAbonement.setSelection(SpinnerAdapter.NO_SELECTION);

        // group time spinner
        SpinnerAdapter timeAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getAllGroupTypes());
        spinnerTime.setAdapter(timeAdapter);
        spinnerTime.setSelection(SpinnerAdapter.NO_SELECTION);

        // weekdays spinner
        final SpinnerAdapter dayAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getWeekdays());
        spinnerDay.setAdapter(dayAdapter);
        spinnerDay.setSelection(SpinnerAdapter.NO_SELECTION);

        dialogView.setTag(outerAdapter);
        final View dialogViewFinalized = dialogView;
        DialogInterface.OnClickListener addStudentListener = (dialog, which) -> {
            EditText studNameField = ((AlertDialog)dialog).findViewById(R.id.dialogadd_fullname);
            EditText notesTextField = ((AlertDialog)dialog).findViewById(R.id.dialogadd_notes);

            if(studNameField.getText().length()>0) {
                if(spinnerAbonement.getSelectedItemPosition() != SpinnerAdapter.NO_SELECTION) {
                    Abonement curAbonement = (Abonement) spinnerAbonement.getSelectedItem();

                    Student studentNew = new Student(studNameField.getText().toString(), 0, curAbonement); // get the value of list with abonements

                    studentNew.setNotes(notesTextField.getText().toString());

                    StudentsRepository.getInstance().addStudent(studentNew);

                    // get first day to start adding lessons
                    // Calendar calendar = Calendar.getInstance();
                    LocalDate currentDate = LocalDate.now();

                    if(spinnerDay.getSelectedItemPosition()!=SpinnerAdapter.NO_SELECTION &&
                       spinnerTime.getSelectedItemPosition()!=SpinnerAdapter.NO_SELECTION)
                    {
                        //shift to one week
                        // when weekday was before on this week
                        int weekdayCurrent = currentDate.getDayOfWeek().getValue();
                        int weekdayChosen = UserSettings.getInstance().getWeekdayIndex((UserSettings.WEEKDAY) spinnerDay.getSelectedItem());
                        int shift = weekdayChosen - weekdayCurrent;
                        if (shift < 0) shift += 7;
                        currentDate = currentDate.plusDays(shift);

                        // setup lessons
                        int lessonDuration = UserSettings.getDefaultLessonHours();
                        int hoursToWorkIterator = studentNew.getHoursBalance();
                        GroupType curGroupType = (GroupType) spinnerTime.getSelectedItem();

                        List<Lesson> lessonsNew = new ArrayList<>();
                        for (int i = hoursToWorkIterator; i > 0; i -= lessonDuration) {
                            lessonsNew.add(new Lesson(currentDate, studentNew, curGroupType));
                            //repeats every week
                            currentDate = currentDate.plusDays(7);
                        }
                        StudentsRepository.getInstance().addLessons(lessonsNew);

                        StudentsRecyclerAdapter studAdapter = (StudentsRecyclerAdapter) dialogViewFinalized.getTag();
                        studAdapter.addStudent(studentNew);
                        studAdapter.notifyDataSetChanged();
                    }
                }
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

    /**
     *
     * @param fragment
     * @param outerAdapter
     * this adapter will be updated using LocalAdapter:update()
     * @param date
     * @param groupType
     * @param excludedStudents
     * these students will not be in list when available students will be shown
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void AddStudentsToGroup(@NonNull Fragment fragment,LocalAdapter outerAdapter,@Nullable LocalDate date,@Nullable GroupType groupType,@Nullable List<Student> excludedStudents){

        View dialogView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.dialog_create_group, null);
        RecyclerView listSelectStudents = dialogView.findViewById(R.id.dialogcreate_select_students_list);
        SearchView searchView = dialogView.findViewById(R.id.dialog_search_view);

        Spinner spinnerGroupType= dialogView.findViewById(R.id.dialogcreate_spinner_time);
        if(groupType == null) {
            SpinnerAdapter groupAdapter = new ArrayAdapter<>(fragment.getContext(),
                    R.layout.item_spinner,
                    R.id.text_spinner_item,
                    UserSettings.getInstance().getAllGroupTypes());
            spinnerGroupType.setAdapter(groupAdapter);
            spinnerGroupType.setSelection(0);
        }
        else{
            SpinnerAdapter groupAdapter = new ArrayAdapter<>(fragment.getContext(),
                    R.layout.item_spinner,
                    R.id.text_spinner_item,
                    new ArrayList<GroupType>() {{
                        add(groupType);
                    }});
            spinnerGroupType.setAdapter(groupAdapter);
            spinnerGroupType.setSelection(0);
            //disable spinner
            spinnerGroupType.setClickable(false);
            spinnerGroupType.setFocusable(View.NOT_FOCUSABLE);
            spinnerGroupType.setBackgroundColor(fragment.getContext().getColor(R.color.colorNonAccent));
        }

        // date picker for choosing date
        TextView datePickerTextView = dialogView.findViewById(R.id.dialogcreate_datepicker_text);
        String datePickerDateText;
        if(date == null) {
            datePickerDateText = LocalDate.now().format(DatabaseConverters.getDateFormatter());
            datePickerTextView.setOnClickListener(new View.OnClickListener() {

                private long mLastClickTime = 0;

                @Override
                public void onClick(final View textView) {

                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    DialogHandler.getInstance().DatePicker(fragment.getContext(),(TextView) textView, date);
                }
            });
            datePickerTextView.setTag(R.id.date, LocalDate.now());
        }
        else {
            datePickerDateText = date.format(DatabaseConverters.getDateFormatter());
            datePickerTextView.setClickable(false);
            datePickerTextView.setFocusable(false);
            datePickerTextView.setBackgroundColor(fragment.getContext().getColor(R.color.colorNonAccent));
            datePickerTextView.setTag(R.id.date, date);
        }
        datePickerTextView.setText(datePickerDateText);


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
            if(date != null && excludedStudents != null && excludedStudents.size()>0){
                studentList.removeAll(excludedStudents);
            }
            studentsAdapter.setStudents(studentList);
        });

        dialogView.setTag(R.id.tag_spinner_time,spinnerGroupType);
        dialogView.setTag(R.id.tag_date_picker,datePickerTextView);

        DialogInterface.OnClickListener createGroupClickListener = (dialog, which) -> {
            TextView dateText = ((AlertDialog)dialog).findViewById(R.id.dialogcreate_datepicker_text);
            Spinner timeSpinner = ((AlertDialog)dialog).findViewById(R.id.dialogcreate_spinner_time);

            LocalDate groupDate = (LocalDate) dateText.getTag(R.id.date);
            GroupType curGroupType = (GroupType) timeSpinner.getSelectedItem();

            StudentsRepository.getInstance().addGroup(groupDate, curGroupType, studentsAdapter.getSelectedStudents());
            outerAdapter.update();
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setView(dialogView)
                .setPositiveButton(R.string.label_add, createGroupClickListener)
                .setNegativeButton(R.string.label_cancel, defaultNegativeClickListener)
                .create().show();
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

    private void DatePicker(Context context,final TextView textView, @Nullable LocalDate startDate) {
        if(startDate == null)
            startDate = LocalDate.now();

        DatePickerDialog datePickerDialog = getDatePickerDialog(textView.getContext(), startDate, null);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.label_OK), (dialog, which) -> {
                LocalDate outputDate = LocalDate.of(
                        datePickerDialog.getDatePicker().getYear(),
                        datePickerDialog.getDatePicker().getMonth()+1,
                        datePickerDialog.getDatePicker().getDayOfMonth());
                textView.setText(outputDate.format(DatabaseConverters.getDateFormatter()));
                textView.setTag(R.id.date,outputDate);
            });
        datePickerDialog.show();
    }

    public DatePickerDialog getDatePickerDialog(Context context, LocalDate curDate, DatePickerDialog.OnDateSetListener listenerOnDate){
        return new DatePickerDialog(context, listenerOnDate, curDate.getYear(), curDate.getMonthValue()-1, curDate.getDayOfMonth());
    }
}