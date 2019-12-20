package com.app.artclass;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.artclass.database.DatabaseManager;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentCard extends Fragment {

    private DatabaseManager databaseManager;
    private Student student;
    private LocalAdapter outerAdapter;
    private List<Lesson> allLessonsList;

    public StudentCard() {
        // Required empty public constructor
    }

    public StudentCard(Student student) {
        this.student = student;
        databaseManager = DatabaseManager.getInstance(getContext());
        allLessonsList = databaseManager.getLessonsForStudent(student);
    }

    public StudentCard(Student student, LocalAdapter adapter) {
        this.student = student;
        outerAdapter = adapter;
        databaseManager = DatabaseManager.getInstance(getContext());
        allLessonsList = databaseManager.getLessonsForStudent(student);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_card, container, false);

        updateStudentFields(view);

        // redact balance listeners

        ImageButton addMoney_btn = view.findViewById(R.id.addMoney_button);
        EditText moneyAmountEditText = view.findViewById(R.id.addCustomCash_view);
        addMoney_btn.setTag(moneyAmountEditText);
        addMoney_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText moneyText = (EditText) v.getTag();
                student.balance += Integer.valueOf(moneyText.getText().toString());
                updateBalance();
                moneyText.setText("");
            }
        });

        Button makeZeroMoney = view.findViewById(R.id.make_zero_money);
        makeZeroMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student.balance = 0;
                updateBalance();
            }
        });

        // custom increments

        Button add1_btn = view.findViewById(R.id.add1_btn);
        Button add2_btn = view.findViewById(R.id.add2_btn);
        Button add3_btn = view.findViewById(R.id.add3_btn);

        SparseArray<String> buttonIncrements = UserSettings.getInst(getContext()).getButtonIncrements();

        add1_btn.setText(buttonIncrements.valueAt(0));
        add2_btn.setText(buttonIncrements.valueAt(1));
        add3_btn.setText(buttonIncrements.valueAt(2));
        add1_btn.setTag(buttonIncrements.keyAt(0));
        add2_btn.setTag(buttonIncrements.keyAt(1));
        add3_btn.setTag(buttonIncrements.keyAt(2));

        View.OnClickListener incrBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student.balance += (Integer) v.getTag();
                updateBalance();
            }
        };
        add1_btn.setOnClickListener(incrBtnClickListener);
        add2_btn.setOnClickListener(incrBtnClickListener);
        add3_btn.setOnClickListener(incrBtnClickListener);


        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int y, int m, int d) {
                String groupsTime;
                final int fullDate = Converters.getDateInt(y,m+1,d);

                StringBuilder groupsTimeBuilder = new StringBuilder();
                for(Lesson curLesson:allLessonsList){
                    if(curLesson.getFullDate()==fullDate){
                        groupsTimeBuilder.append(curLesson.getTime()).append(" - ").append(curLesson.getHoursWorked()).append("\n");
                    }
                }
                groupsTime = groupsTimeBuilder.toString();

                if(groupsTime == "") groupsTime = getString(R.string.message_student_not_worked);

                groupsTime = Converters.getDateString(y,m+1,d, UserSettings.dateSeparator)+"\n"+groupsTime;

                Toast.makeText(getContext(), groupsTime, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void updateBalance(){
        ((TextView)getView().findViewById(R.id.balance_view)).setText(String.valueOf(student.balance));
        databaseManager.updateStudent(student);
    }

    private void updateStudentFields(View view) {
        ((TextView)view.findViewById(R.id.student_name_view)).setText(student.fullName);
        ((TextView)view.findViewById(R.id.balance_view)).setText(String.valueOf(student.balance));
        ((TextView)view.findViewById(R.id.abonement_studcard_view)).setText(student.getAbonementType());
        ((TextView)view.findViewById(R.id.hours_studcard_view)).setText(String.format("%d h", student.getHoursToWork()));

        List<Pair<String,String>> phoneNumbers = databaseManager.getAllSettingsFieldString(DatabaseManager.SETTING_PHONE_NUMBER);
        if(phoneNumbers.size()>0){
            StringBuilder numbers = new StringBuilder();
            for(Pair<String,String> number : phoneNumbers){
                numbers.append(number.second).append("\n");
            }
            ((TextView)view.findViewById(R.id.phone_view)).setText(numbers.toString());
        }
    }

}
