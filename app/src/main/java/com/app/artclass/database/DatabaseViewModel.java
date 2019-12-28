package com.app.artclass.database;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class DatabaseViewModel extends ViewModel {

    private StudentsRepository studentsRepository;

    //Live data
    private LiveData<List<Student>> allStudsListData;
    private LiveData<List<Student>> allLessonsListData;

    public DatabaseViewModel(Application application) {
        studentsRepository = StudentsRepository.getInstance(application);

    }
}
