package com.app.artclass.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.app.artclass.Logger;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.LessonsAdapter;
import com.app.artclass.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsPresentList extends Fragment {

    private LessonsAdapter lessonsAdapter;
    private LocalDate mDate;
    private GroupType mGroupType;
    private List<Student> mStudentList;

    public StudentsPresentList(LocalDate date, GroupType groupType, List<Student> studentList) {
        this.mDate = date;
        mGroupType = groupType;
        this.mStudentList = studentList;
        Logger.getInstance().appendLog(getClass(),"init fragment");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_students_presentlist, container, false);

        //memorise list of students
        TextView dateView = view.findViewById(R.id.date_view);
        TextView studentsCountView = view.findViewById(R.id.students_count_view);
        ListView lessonsTodayList = view.findViewById(R.id.students_present_list);

        dateView.setText(mDate.format(DatabaseConverters.getDateFormatter()));
        studentsCountView.setText(String.valueOf(mStudentList.size()));

        List<Lesson> mLessonList = new ArrayList<>();
        mStudentList.forEach(student -> mLessonList.add(new Lesson(mDate,mGroupType,student)));

        lessonsAdapter = new LessonsAdapter(
                this,
                R.layout.item_students_presentlist,
                mLessonList);
        lessonsTodayList.setAdapter(lessonsAdapter);

        return view;
    }
}
