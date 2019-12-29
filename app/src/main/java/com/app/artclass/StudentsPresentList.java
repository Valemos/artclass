package com.app.artclass;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsPresentList extends Fragment {

    ListView lessonsTodayList;
    Spinner spinnerCurGroup;

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
        TextView dateView = view.findViewById(R.id.dateWeekday_view);
        lessonsTodayList = view.findViewById(R.id.studentsPresentList);
        spinnerCurGroup = view.findViewById(R.id.spinner_select_group);

        // output start date
        dateView.setText(groupsDate.format(DatabaseConverters.getDateFormatter()));

        //setup the adapters
        LessonsAdapter lessonsAdapter = new LessonsAdapter(this,R.layout.item_students_presentlist, new ArrayList<>());
        lessonsTodayList.setAdapter(lessonsAdapter);

        SpinnerAdapter spinnerGroupAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getContext()),
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getAllGroupTypes());
        spinnerCurGroup.setAdapter(spinnerGroupAdapter);

        //setup the data when ready
        ApplicationViewModel mAppViewModel = new ApplicationViewModel(StudentsRepository.getInstance().getMainApplication());

        mAppViewModel.getTodayGroupsMap(groupsDate, this).observe(getViewLifecycleOwner(), groupsMap -> {

            // handle adding new group type in settings
            // can cause exception

            UserSettings.getInstance().getAllGroupTypes().forEach(groupType -> {
                if(spinnerCurGroup.getSelectedItem().equals(groupType)){
                    //observe live data when list available change it
                   groupsMap.get(groupType).observe(getViewLifecycleOwner(),lessons -> {
                       lessonsAdapter.clear();
                       lessonsAdapter.addAll(lessons);
                   });
                }
            });


            System.out.println("updated groups");
        });

        return view;
    }

}
