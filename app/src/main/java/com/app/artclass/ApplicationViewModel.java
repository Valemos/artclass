package com.app.artclass;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.app.artclass.database.StudentsRepository;

import java.time.LocalDate;

public class ApplicationViewModel extends ViewModel {

    //Live data
    private AllGroupTypesMap allTodayGroupsMap;

    public ApplicationViewModel(Application application) {
    }

    public AllGroupTypesMap getTodayGroupsMap(LocalDate date, Fragment fragment) {
        if(allTodayGroupsMap == null){
            allTodayGroupsMap = new AllGroupTypesMap(date,fragment);
        }
        return allTodayGroupsMap;
    }
}
