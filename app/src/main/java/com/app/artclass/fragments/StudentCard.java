package com.app.artclass.fragments;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.artclass.Logger;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.LessonsHistoryAdapter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentCard extends Fragment {

    private Student student;

    public StudentCard(Student student) {
        this.student = student;
        Logger.getInstance().appendLog(getClass(),"init fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_student_card, container, false);

        ImageButton addMoney_btn = mainView.findViewById(R.id.add_money_button);
        EditText moneyAmountEditText = mainView.findViewById(R.id.add_custom_money_view);
        addMoney_btn.setTag(moneyAmountEditText);

        // custom increments

        Button add1_btn = mainView.findViewById(R.id.add1_btn);
        Button add2_btn = mainView.findViewById(R.id.add2_btn);
        Button add3_btn = mainView.findViewById(R.id.add3_btn);
        Button makeZeroMoney = mainView.findViewById(R.id.make_zero_money);

        SparseArray<String> buttonIncrements = UserSettings.getInstance().getBalanceIncrements();

        add1_btn.setText(buttonIncrements.valueAt(0));
        add2_btn.setText(buttonIncrements.valueAt(1));
        add3_btn.setText(buttonIncrements.valueAt(2));
        add1_btn.setTag(buttonIncrements.keyAt(0));
        add2_btn.setTag(buttonIncrements.keyAt(1));
        add3_btn.setTag(buttonIncrements.keyAt(2));


        addMoney_btn.setOnClickListener(v -> {
            EditText moneyText = (EditText) v.getTag();
            student.incrementMoneyBalance(Integer.valueOf(moneyText.getText().toString()));
            updateBalance();
            moneyText.setText("");
            Logger.getInstance().appendLog(getClass().getName()+": used custom money");
        });
        makeZeroMoney.setOnClickListener(v -> {
            student.setMoneyBalance(0);
            updateBalance();
        });
        View.OnClickListener incrBtnClickListener = v -> {
            student.incrementMoneyBalance((Integer) v.getTag());
            updateBalance();
        };
        add1_btn.setOnClickListener(incrBtnClickListener);
        add2_btn.setOnClickListener(incrBtnClickListener);
        add3_btn.setOnClickListener(incrBtnClickListener);

        EditText notesTextView = mainView.findViewById(R.id.notes_view);
        notesTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                student.setNotes(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateStudentFields(mainView, student);
        return mainView;
    }

    private void updateBalance(){
        ((TextView)getView().findViewById(R.id.balance_view)).setText(DatabaseConverters.getMoneyFormat().format(student.getMoneyBalance()));
        ((TextView)getView().findViewById(R.id.hours_studcard_view)).setText(String.format(UserSettings.getInstance().getHoursTextFormat(), student.getHoursBalance()));

        StudentsRepository.getInstance().update(student);
    }

    @SuppressLint("DefaultLocale")
    private void updateStudentFields(View view, @NotNull Student curStudent) {
        ((TextView)view.findViewById(R.id.student_name_view)).setText(curStudent.getName());
        ((TextView)view.findViewById(R.id.balance_view)).setText(DatabaseConverters.getMoneyFormat().format(curStudent.getMoneyBalance()));
        ((TextView)view.findViewById(R.id.hours_studcard_view)).setText(String.format(UserSettings.getInstance().getHoursTextFormat(), curStudent.getHoursBalance()));
        TextView notesView = view.findViewById(R.id.notes_view);
        notesView.setText(curStudent.getNotes());

        LessonsHistoryAdapter lessonsHistoryAdapter = new LessonsHistoryAdapter(this.getContext(), R.layout.item_lessons_history, new ArrayList<>());
        ListView lessonsHistoryView = view.findViewById(R.id.student_lessons_list_view);
        lessonsHistoryView.setAdapter(lessonsHistoryAdapter);

        StudentsRepository.getInstance().getLessonList(student).observe(getViewLifecycleOwner(),lessons -> {
            lessonsHistoryAdapter.clear();
            lessonsHistoryAdapter.addAll(lessons.stream().filter(lesson -> lesson.getHoursWorked()>0).collect(Collectors.toList()));
            lessonsHistoryAdapter.update();
        });
    }

    @Override
    public void onDestroy() {
        StudentsRepository.getInstance().update(student);
        super.onDestroy();
    }

}
