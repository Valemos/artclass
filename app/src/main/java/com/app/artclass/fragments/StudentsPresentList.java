package com.app.artclass.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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
import com.app.artclass.list_adapters.LessonsAdapter;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseConverters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsPresentList extends Fragment {

    private TextView studentsCount;
    private LessonsAdapter lessonsAdapter;
    private LocalDate groupsDate;

    public  StudentsPresentList() {
        this.groupsDate = LocalDate.now();
    }

    public StudentsPresentList(LocalDate date) {
        this.groupsDate = date;
    }

    @Override
    public void onDestroy() {
        lessonsAdapter.updateRepository();
        super.onDestroy();
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
        TextView dateView = view.findViewById(R.id.date_view);
        studentsCount = view.findViewById(R.id.students_count_view);
        ListView lessonsTodayList = view.findViewById(R.id.students_present_list);
        Spinner spinnerCurGroup = view.findViewById(R.id.spinner_select_group);

        // output start date
        dateView.setText(groupsDate.format(DatabaseConverters.getDateFormatter()));

        //setup the adapters
        lessonsAdapter = new LessonsAdapter(this,R.layout.item_students_presentlist, new ArrayList<>());
        lessonsTodayList.setAdapter(lessonsAdapter);

        SpinnerAdapter spinnerGroupAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getContext()),
                R.layout.item_spinner,
                R.id.text_spinner_item,
                UserSettings.getInstance().getAllGroupTypes());
        spinnerCurGroup.setAdapter(spinnerGroupAdapter);

        //setup the data when ready
        ApplicationViewModel mAppViewModel = ViewModelProviders.of(this).get(ApplicationViewModel.class);

        SpinnerInteractionListener spinnerListener = new SpinnerInteractionListener(mAppViewModel, groupsDate, lessonsAdapter);

        spinnerCurGroup.setOnItemSelectedListener(spinnerListener);
        spinnerCurGroup.setSelection(0,false);

        return view;
    }

    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener {

        private final ApplicationViewModel mAppViewModel;
        private final LocalDate date;
        private LessonsAdapter lessonsAdapter;

        SpinnerInteractionListener(ApplicationViewModel mAppViewModel, LocalDate date, LessonsAdapter lessonsAdapter) {
            this.mAppViewModel = mAppViewModel;
            this.date = date;
            this.lessonsAdapter = lessonsAdapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            mAppViewModel.getTodayGroupsMap(date).forEach((groupType, listLiveData) -> listLiveData.removeObservers(getViewLifecycleOwner()));
            mAppViewModel.getTodayGroupsMap(date).get(parent.getItemAtPosition(pos)).observe(getViewLifecycleOwner(), lessons -> {
                if(lessons.size()>0){
                    lessonsAdapter.updateRepository();
                    lessonsAdapter.changeData(lessons, mAppViewModel, (GroupType)parent.getItemAtPosition(pos));
                    studentsCount.setText(String.valueOf(lessons.size()));
                }else{
                    lessonsAdapter.clear();
                    studentsCount.setText("0");
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
