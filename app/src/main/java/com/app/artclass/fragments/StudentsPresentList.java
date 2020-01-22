package com.app.artclass.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.app.artclass.ApplicationViewModel;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.list_adapters.LessonsAdapter;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseConverters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsPresentList extends Fragment {

    ListView lessonsTodayList;
    Spinner spinnerCurGroup;

    LocalDate groupsDate;

    public  StudentsPresentList() {
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
        ApplicationViewModel mAppViewModel = ViewModelProviders.of(this).get(ApplicationViewModel.class);;

        SpinnerInteractionListener spinnerListener = new SpinnerInteractionListener(mAppViewModel, groupsDate, lessonsAdapter);
        spinnerCurGroup.setOnItemSelectedListener(spinnerListener);
        spinnerCurGroup.setSelection(0,false);

        return view;
    }

    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener {

        Map<GroupType, LiveData<List<Lesson>>> todayGroups;
        private LessonsAdapter lessonsAdapter;

        public SpinnerInteractionListener(ApplicationViewModel mAppViewModel, LocalDate groupsDate, LessonsAdapter lessonsAdapter) {
            this.lessonsAdapter = lessonsAdapter;
            todayGroups = mAppViewModel.getTodayGroupsMap(groupsDate, UserSettings.getInstance().getAllGroupTypes());
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            todayGroups.forEach((groupType, listLiveData) -> listLiveData.removeObservers(getViewLifecycleOwner()));
            todayGroups.getOrDefault(parent.getItemAtPosition(pos),null).observe(getViewLifecycleOwner(), lessons -> {
                if(lessons.size()>0){
                    lessonsAdapter.updateRepository();
                    lessonsAdapter.changeData(lessons);
                }else{
                    lessonsAdapter.clear();
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            lessonsAdapter.clear();
            lessonsAdapter.notifyDataSetChanged();
        }
    }
}
