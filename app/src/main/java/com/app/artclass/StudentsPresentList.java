package com.app.artclass;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.artclass.database.DatabaseManager;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentsPresentList extends Fragment {

    DatabaseManager dataManager;
    ListView listOfTodayStudents;
    TextView groupTimeTextView;

    public StudentsPresentList() {
        // Required empty public constructor
    }

    public StudentsPresentList(DatabaseManager manager) {
        this.dataManager = manager;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_students_presentlist, container, false);

        //memorise list of students
        listOfTodayStudents = view.findViewById(R.id.studentsPresentList);
        groupTimeTextView = view.findViewById(R.id.group_present_time_view);
        listOfTodayStudents.setTag(getFragmentManager());

        // output start date

        Calendar cal = Calendar.getInstance();
        TextView dateView = view.findViewById(R.id.dateWeekday_view);
        dateView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(cal.getTime()));

        //setup of buttons

        Button[] btn_groups = {
                view.findViewById(R.id.select_group1_btn),
                view.findViewById(R.id.select_group2_btn),
                view.findViewById(R.id.select_group3_btn)};

        int dateOfGroups = Converters.getDateInt(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));

        List<String> timeLabels = UserSettings.getInst(getContext()).getGroupLabels();

        if(timeLabels.size()!=0){
            for (int i = 0; i < btn_groups.length; i++) {

                List<Lesson> lessonsForToday = dataManager.getLessonStudentsList(dateOfGroups, timeLabels.get(i));
                if (lessonsForToday.size() != 0) {
                    btn_groups[i].setText(timeLabels.get(i));
                    btn_groups[i].setTag(new Pair(timeLabels.get(i), lessonsForToday));
                    btn_groups[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            groupTimeTextView.setText((String) ((Pair) view.getTag()).first);

                            List<Lesson> lessons = (List<Lesson>) ((Pair) view.getTag()).second;// use tag for list of students
                            LessonsAdapter adapter = new LessonsAdapter(getContext(),R.layout.item_students_presentlist, lessons);
                            listOfTodayStudents.setAdapter(adapter);
                        }
                    });
                    btn_groups[i].setVisibility(View.VISIBLE);
                }
            }
        }

        //set group time
        String timeText = "";
        //find first visible button
        for(int i = 0; i < btn_groups.length;i++){
            if(btn_groups[i].getVisibility()==View.VISIBLE){
                timeText = (String) btn_groups[i].getText();
                break;
            }
        }


        groupTimeTextView.setText(timeText);

        // list contents
        ListView list = view.findViewById(R.id.studentsPresentList);
        List<Lesson> lessons = dataManager.getLessonStudentsList(dateOfGroups, timeText);
        LessonsAdapter adapter = new LessonsAdapter(getContext(),R.layout.item_students_presentlist, lessons);
        list.setAdapter(adapter);

        return view;
    }
}
