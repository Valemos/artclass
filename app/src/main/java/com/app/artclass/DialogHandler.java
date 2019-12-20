package com.app.artclass;

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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.database.DatabaseManager;
import com.app.artclass.database.Lesson;
import com.app.artclass.database.Student;

import java.time.LocalDate;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DialogHandler {
    
    private DatabaseManager databaseManager;
    private Context mContext;
    
    private Runnable proc_positive = null;
    private Runnable proc_negative = null;


    public DialogHandler(Context context) {
        mContext = context;
        this.databaseManager = DatabaseManager.getInstance(context);
    }

    // default listeners
    private DialogInterface.OnClickListener defaultPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(proc_positive!=null)
                proc_positive.run();
        }
    };

    private DialogInterface.OnClickListener defaultNegativeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(proc_negative!=null)
                proc_negative.run();
        }
    };

    // Dialog. --------------------------------------------------------------

    public void ConfirmDeleteObject(String objectName, Runnable positiveProcedure, Runnable negativeProcedure) {

        proc_positive = positiveProcedure;
        proc_negative = negativeProcedure;

        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(mContext.getString(R.string.title_confirm_delete_student));
        dialog.setMessage(mContext.getString(R.string.message_confirm_delete) + "\n" +objectName);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.label_OK),defaultPositiveClickListener);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.label_cancel),defaultNegativeClickListener);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    public void AlertDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setPositiveButton(R.string.label_OK,null)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .create().show();
    }

    public void AddNewStudent(StudentsRecyclerAdapter outerAdapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_new_student, null);

        Spinner spinnerAbonement = dialogView.findViewById(R.id.dialogadd_spinner_abonement);
        Spinner spinnerDay= dialogView.findViewById(R.id.dialogadd_spinner_day);
        Spinner spinnerTime= dialogView.findViewById(R.id.dialogadd_spinner_time);

        // abonement type spinner
        SpinnerAdapter abonAdapter = new ArrayAdapter<>(mContext,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInst().getAbonementLabels());
        spinnerAbonement.setAdapter(abonAdapter);

        // group time spinner
        SpinnerAdapter timeAdapter = new ArrayAdapter<>(mContext,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInst().getGroupLabels());
        spinnerTime.setAdapter(timeAdapter);

        // weekdays spinner
        final SpinnerAdapter dayAdapter = new ArrayAdapter<>(mContext,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInst().getWeekdayLabels());
        spinnerDay.setAdapter(dayAdapter);

        dialogView.setTag(outerAdapter);
        final View dialogViewFinalized = dialogView;
        DialogInterface.OnClickListener addStudentListener = new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText studNameField = ((AlertDialog)dialog).findViewById(R.id.dialogadd_fullname);
                EditText phoneNumberField = ((AlertDialog)dialog).findViewById(R.id.dialogadd_phone);
                Spinner spinnerAbonement= ((AlertDialog)dialog).findViewById(R.id.dialogadd_spinner_abonement);
                Spinner spinnerDay= ((AlertDialog)dialog).findViewById(R.id.dialogadd_spinner_day);
                Spinner spinnerTime= ((AlertDialog)dialog).findViewById(R.id.dialogadd_spinner_time);

                if(studNameField.getText().length()>0) {
                    String abonementStr = spinnerAbonement.getSelectedItem().toString();

                    Student studentNew = new Student(studNameField.getText().toString(), 0, abonementStr,
                            UserSettings.getInst(mContext).getAbonement(abonementStr).getAbonementAmount()); // get the value of list with abonements

                    if(phoneNumberField.getText().length()>0){
                        studentNew.phone_numbers.add(phoneNumberField.getText().toString());
                    }

                    databaseManager.addStudent(studentNew);

                    // get first day to start adding lessons
                    // Calendar calendar = Calendar.getInstance();
                    LocalDate currentDate = LocalDate.now();

                    // we need to start with a first weekday that matches
                    int weekdayCurrent = currentDate.getDayOfWeek().getValue();
                    int weekdayChosen = UserSettings.getInst().getWeekdayIndex(spinnerDay.getSelectedItem().toString());

                    int shift = weekdayChosen - weekdayCurrent;
                    if(shift < 0) shift += 7;

                    currentDate = currentDate.plusDays(shift);


                    // setup lessons
                    int lessonDuration=UserSettings.getDefaultLessonHours();
                    int hoursToWorkIterator = studentNew.hours_balance;
                    String timeString = spinnerTime.getSelectedItem().toString();

                    for(int i = hoursToWorkIterator; i > 0; i-=lessonDuration){
                        databaseManager.addLesson(new Lesson(currentDate, UserSettings.getInst().getGroupTime(timeString), studentNew.getName()));
                        currentDate = currentDate.plusDays(7);
                    }

                    StudentsRecyclerAdapter studAdapter = (StudentsRecyclerAdapter) dialogViewFinalized.getTag();
                    studAdapter.addStudent(studentNew);
                    studAdapter.notifyDataSetChanged();

                    // init lessons in database
                }
            }
        };

        builder
                .setView(dialogView)
                .setTitle(R.string.title_add_student_togroup)
                .setPositiveButton(mContext.getString(R.string.label_add), addStudentListener)
                .setNegativeButton(mContext.getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    public void MoveLessonToAnotherDay(Lesson lesson){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_move_lesson, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreateNewGroup(final RecyclerView.Adapter outerAdapter, @Nullable Integer date, @Nullable String time, @Nullable List<Student> excludedStudents, @Nullable FragmentManager fragmentManager){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_create_group, null);
        RecyclerView listSelectStudents = dialogView.findViewById(R.id.dialogcreate_select_students_list);

        // setup spinners for group specification
        // group time spinner
        Spinner spinnerTime= dialogView.findViewById(R.id.dialogcreate_spinner_time);

        final SpinnerAdapter timeAdapter = new ArrayAdapter<>(mContext,
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getGroupLabels());
        spinnerTime.setAdapter(timeAdapter);

        if(time != null) {
            spinnerTime.setSelection(UserSettings.getGroupLabels().indexOf(time));
            spinnerTime.setClickable(false);
            spinnerTime.setFocusable(View.NOT_FOCUSABLE);
            spinnerTime.setBackgroundColor(mContext.getColor(R.color.colorNonAccent));
        }
        // date picker for choosing date

        TextView datePickerView = dialogView.findViewById(R.id.dialogcreate_datepicker_text);
        String datePickerDateText;
        if(date==null) {
            datePickerDateText = Converters.getDateString(LocalDate.now(), UserSettings.dateSeparator);
            datePickerView.setOnClickListener(new View.OnClickListener() {

                private long mLastClickTime = 0;

                @Override
                public void onClick(final View textView) {

                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    int date = Converters.parceDate(((TextView)textView).getText().toString(), mContext.getString(R.string.date_separator));
                    DialogHandler dialogHandler = new DialogHandler(mContext);
                    dialogHandler.DatePicker((TextView) textView, date);
                }
            });
        }else {
            datePickerDateText = Converters.getDateString(date, UserSettings.dateSeparator);
            datePickerView.setClickable(false);
            datePickerView.setFocusable(false);
            datePickerView.setBackgroundColor(mContext.getColor(R.color.colorNonAccent));
        }
        datePickerView.setText(datePickerDateText);


        // setup students list
        // all students by default
        // if date and time null (we are adding students to group)
        // then delete students that already in the group

        List<Student> studentList = databaseManager.getAllStudents();

        if(date != null && time != null && excludedStudents != null && excludedStudents.size()>0){
            studentList.removeAll(excludedStudents);
        }

        //adapter
        StudentsRecyclerAdapter adapter = new StudentsRecyclerAdapter(
                R.layout.item_dialog_student_selector,
                R.id.name_view,
                null,
                studentList,
                fragmentManager);
        listSelectStudents.setAdapter(adapter);
        listSelectStudents.setLayoutManager(new LinearLayoutManager(mContext));

        dialogView.setTag(R.id.tag_spinner_time,spinnerTime);
        dialogView.setTag(R.id.tag_date_picker,datePickerView);
        dialogView.setTag(R.id.tag_adapter_objects,adapter);

        DialogInterface.OnClickListener createGroupClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView dateText = ((AlertDialog)dialog).findViewById(R.id.dialogcreate_datepicker_text);
                Spinner timeSpinner = ((AlertDialog)dialog).findViewById(R.id.dialogcreate_spinner_time);
                StudentsRecyclerAdapter studentsAdapter = (StudentsRecyclerAdapter)((RecyclerView)((AlertDialog)dialog).findViewById(R.id.dialogcreate_select_students_list)).getAdapter();

                int groupDate = Converters.parceDate(dateText.getText().toString(),UserSettings.dateSeparator);
                String timeStr = (String) timeSpinner.getSelectedItem();

                databaseManager.addGroup(groupDate, timeStr, studentsAdapter.getSelectedStudentNames());

                try {
                    ((GroupsRecyclerAdapter) outerAdapter).addGroup(groupDate, timeStr);
                }catch (ClassCastException e){
                    ((StudentsRecyclerAdapter)outerAdapter).addStudents(databaseManager.getStudentsForNames(studentsAdapter.getSelectedStudentNames(),databaseManager.getReadableDatabase()));
                }
            }
        };

        builder.setView(dialogView)
                .setPositiveButton(R.string.label_add, createGroupClickListener)
                .setNegativeButton(R.string.label_cancel, defaultNegativeClickListener)
                .create().show();
    }

    private void DatePicker(final TextView textView, int date) {

        final DatePickerDialog datePickerDialog = getDatePickerDialog(textView.getContext(),date,null);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.label_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(
                        Converters.getDateString(
                                datePickerDialog.getDatePicker().getYear(),
                                datePickerDialog.getDatePicker().getMonth(),
                                datePickerDialog.getDatePicker().getDayOfMonth(),
                                UserSettings.dateSeparator));
            }
        });
        datePickerDialog.show();
    }

    public DatePickerDialog getDatePickerDialog(Context context,int curDate, DatePickerDialog.OnDateSetListener listenerOnDate){
        if(context == null)
            context = mContext;
        return new DatePickerDialog(context, listenerOnDate, Converters.extrYear(curDate), Converters.extrMonth(curDate), Converters.extrDay(curDate));
    }
}