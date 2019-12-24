package com.app.artclass;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.DatabaseManager;
import com.app.artclass.database.GroupType;
import com.app.artclass.database.Lesson;

import java.time.LocalDate;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsPresentList extends Fragment {

    DatabaseManager dataManager;
    ListView listOfTodayStudents;
    TextView groupTimeTextView;

    LocalDate groupsDate;

    public StudentsPresentList() {
        this.dataManager = DatabaseManager.getInstance();
        this.groupsDate = LocalDate.now();
    }

    public StudentsPresentList(LocalDate date) {
        this.dataManager = DatabaseManager.getInstance();
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
        dateView.setText(groupsDate.format(DatabaseConverters.getDateFormatter()));

        //setup of buttons

        Button[] btn_groups = {
                view.findViewById(R.id.select_group1_btn),
                view.findViewById(R.id.select_group2_btn),
                view.findViewById(R.id.select_group3_btn)};

        List<GroupType> groupTypes = UserSettings.getInstance().getGroupTypes();


        for (int i = 0; i < btn_groups.length; i++) {
            List<Lesson> lessonsForToday = dataManager.getLessonList(groupsDate, groupTypes.get(i).getTime());
            if (lessonsForToday.size() != 0) {
                btn_groups[i].setText(groupTypes.get(i).getGroupName());
                btn_groups[i].setTag(R.id.group_type, groupTypes.get(i));
                btn_groups[i].setTag(R.id.lessons_list, lessonsForToday);
                btn_groups[i].setOnClickListener(innerView -> {

                    groupTimeTextView.setText(((GroupType)innerView.getTag(R.id.group_type)).getGroupName());

                    List<Lesson> lessons = (List<Lesson>) innerView.getTag(R.id.lessons_list);
                    LessonsAdapter adapter = new LessonsAdapter(getContext(),R.layout.item_students_presentlist, lessons);
                    listOfTodayStudents.setAdapter(adapter);
                });
                btn_groups[i].setVisibility(View.VISIBLE);
            }
        }

        //set group type
        GroupType curGroupType = null;

        //find first visible button
        for(int i = 0; i < btn_groups.length;i++){
            if(btn_groups[i].getVisibility()==View.VISIBLE){
                curGroupType = (GroupType) btn_groups[i].getTag(R.id.group_type);
                break;
            }
        }

        groupTimeTextView.setText(curGroupType.getGroupName());

        //setup list contents
        List<Lesson> lessons = dataManager.getLessonList(groupsDate, curGroupType.getTime());
        LessonsAdapter adapter = new LessonsAdapter(getContext(),R.layout.item_students_presentlist, lessons);
        listOfTodayStudents.setAdapter(adapter);

        return view;
    }
}
