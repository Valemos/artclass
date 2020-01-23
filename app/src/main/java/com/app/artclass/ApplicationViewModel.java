package com.app.artclass;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.StudentsRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationViewModel extends ViewModel {

    //Live data
    private LocalDate groupsDate;
    private Map<GroupType, LiveData<List<Lesson>>> allTodayGroupsMap;

    public ApplicationViewModel() {
    }

    public Map<GroupType, LiveData<List<Lesson>>> getTodayGroupsMap(LocalDate date) {
        if(allTodayGroupsMap == null || !groupsDate.equals(date)){
            allTodayGroupsMap = new HashMap<>();
            for (GroupType groupType : UserSettings.getInstance().getAllGroupTypes()) {
                allTodayGroupsMap.put(groupType,StudentsRepository.getInstance().getLessonList(date,groupType));
            }
            groupsDate = date;
        }
        return allTodayGroupsMap;
    }

    @Nullable
    public Map<GroupType, LiveData<List<Lesson>>> getTodayGroupsMap() {
        return allTodayGroupsMap;
    }
}
