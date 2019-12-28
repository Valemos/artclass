package com.app.artclass;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.GroupType;
import com.app.artclass.database.Lesson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsPresentList extends Fragment {

    ListView listOfTodayStudents;
    TextView groupTimeTextView;

    LocalDate groupsDate;

    public StudentsPresentList() {
        this.groupsDate = LocalDate.now();
    }

    public StudentsPresentList(LocalDate date) {
        this.groupsDate = date;
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

        TextView dateView = view.findViewById(R.id.dateWeekday_view);
        dateView.setText(LocalDateTime.of(groupsDate, LocalTime.now()).format(DatabaseConverters.getDateFormatter()));

        //setup of buttons

        Button[] btn_groups = {
                view.findViewById(R.id.select_group1_btn),
                view.findViewById(R.id.select_group2_btn),
                view.findViewById(R.id.select_group3_btn)};

        List<GroupType> groupTypes = UserSettings.getInstance().getGroupTypes();


        int counterGroupsAvailable = groupTypes.size() < btn_groups.length ? groupTypes.size() : btn_groups.length;
        System.out.println(counterGroupsAvailable);

        for (int i = 0; i < counterGroupsAvailable; i++) {
            Button cur_group_button = btn_groups[i];
            int finalI = i;
            StudentsRepository.getInstance().getLessonList(groupsDate, groupTypes.get(i).getTime()).observe(this, lessonsForToday -> {
                if (lessonsForToday.size() != 0) {
                    cur_group_button.setText(groupTypes.get(finalI).getGroupName());
                    cur_group_button.setTag(R.id.group_type, groupTypes.get(finalI));
                    cur_group_button.setTag(R.id.lessons_list, lessonsForToday);
                    cur_group_button.setOnClickListener(innerView -> {

                        groupTimeTextView.setText(((GroupType)innerView.getTag(R.id.group_type)).getGroupName());

                        List<Lesson> lessons = (List<Lesson>) innerView.getTag(R.id.lessons_list);

                        // change data - not adapter
                        LessonsAdapter adapter = new LessonsAdapter(this,R.layout.item_students_presentlist, lessons);
                        listOfTodayStudents.setAdapter(adapter);
                    });
                    cur_group_button.setVisibility(View.VISIBLE);
                }
            });
        }

        //set group type
        GroupType curGroupType = null;

        //find first available group
        for(int i = 0; i < counterGroupsAvailable;i++){
            if(btn_groups[i].getVisibility()==View.VISIBLE){
                curGroupType = (GroupType) btn_groups[i].getTag(R.id.group_type);

                groupTimeTextView.setText(curGroupType.getGroupName());

                //setup list contents
                StudentsRepository.getInstance().getLessonList(groupsDate, curGroupType.getTime()).observe(this,lessons -> {
                    LessonsAdapter adapter = new LessonsAdapter(this,R.layout.item_students_presentlist, lessons);
                    listOfTodayStudents.setAdapter(adapter);
                });

                break;
            }
        }

        return view;
    }
}
