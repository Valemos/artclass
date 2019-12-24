package com.app.artclass.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

import com.app.artclass.LocalAdapter;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.DatabaseManager;
import com.app.artclass.database.Lesson;
import com.app.artclass.database.Student;

import java.time.LocalDate;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentCard extends Fragment {

    private DatabaseManager databaseManager;
    private Student student;
    private LocalAdapter outerAdapter;
    private List<Lesson> studLessonsList;

    public StudentCard() {
        // Required empty public constructor
    }

    public StudentCard(Student student) {
        this.student = student;
        databaseManager = DatabaseManager.getInstance(getContext());
        studLessonsList = databaseManager.getLessonList(student);
    }

    public StudentCard(Student student, LocalAdapter adapter) {
        this.student = student;
        outerAdapter = adapter;
        databaseManager = DatabaseManager.getInstance(getContext());
        studLessonsList = databaseManager.getLessonList(student);
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
                student.incrementBalance(Integer.valueOf(moneyText.getText().toString()));
                updateBalance();
                moneyText.setText("");
            }
        });

        Button makeZeroMoney = view.findViewById(R.id.make_zero_money);
        makeZeroMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student.setBalance(0);
                updateBalance();
            }
        });

        // custom increments

        Button add1_btn = view.findViewById(R.id.add1_btn);
        Button add2_btn = view.findViewById(R.id.add2_btn);
        Button add3_btn = view.findViewById(R.id.add3_btn);

        SparseArray<String> buttonIncrements = UserSettings.getInstance(getContext()).getBalanceIncrements();

        add1_btn.setText(buttonIncrements.valueAt(0));
        add2_btn.setText(buttonIncrements.valueAt(1));
        add3_btn.setText(buttonIncrements.valueAt(2));
        add1_btn.setTag(buttonIncrements.keyAt(0));
        add2_btn.setTag(buttonIncrements.keyAt(1));
        add3_btn.setTag(buttonIncrements.keyAt(2));

        View.OnClickListener incrBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student.incrementBalance((Integer) v.getTag());
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
                String groupsTimeWorked;
                final LocalDate curDate = LocalDate.of(y,m,d);

                StringBuilder groupsTimeBuilder = new StringBuilder();
                for(Lesson curLesson: studLessonsList){
                    if(curLesson.getDateTime().toLocalDate()==curDate){
                        groupsTimeBuilder
                                .append(
                                curLesson.getDateTime().format(DatabaseConverters.getDateTimeFormatter()))
                                .append(" - ")
                                .append(curLesson.getHoursWorked())
                                .append("\n");
                    }
                }
                groupsTimeWorked = groupsTimeBuilder.toString();

                if(groupsTimeWorked == "") groupsTimeWorked = getString(R.string.message_student_not_worked);

                String workString = String.format(
                        "%s\n%s",
                        curDate.format(DatabaseConverters.getDateFormatter()),
                        groupsTimeWorked);

                Toast.makeText(getContext(), workString, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void updateBalance(){
        ((TextView)getView().findViewById(R.id.balance_view)).setText(String.valueOf(student.getBalance()));
        databaseManager.update(student);
    }

    private void updateStudentFields(View view) {
        ((TextView)view.findViewById(R.id.student_name_view)).setText(student.getName());
        ((TextView)view.findViewById(R.id.balance_view)).setText(String.valueOf(student.getBalance()));
        ((TextView)view.findViewById(R.id.abonement_studcard_view)).setText(student.getAbonementType().getName());
        ((TextView)view.findViewById(R.id.hours_studcard_view)).setText(String.format("%d h", student.getHoursBalance()));
        ((TextView)view.findViewById(R.id.phone_view)).setText(String.join("\n", student.getPhoneList()));
    }

}
