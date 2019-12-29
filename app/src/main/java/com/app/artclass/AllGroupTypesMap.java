package com.app.artclass;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.app.artclass.database.GroupType;
import com.app.artclass.database.Lesson;
import com.app.artclass.database.StudentsRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllGroupTypesMap extends LiveData<Map<GroupType, LiveData<List<Lesson>>>> {

    private LocalDate date;
    private final Fragment mFragment;
    private Map<GroupType, LiveData<List<Lesson>>> map;

    public AllGroupTypesMap(LocalDate date, Fragment fragment) {
        this.date = date;
        this.mFragment = fragment;
        map = new HashMap<>();
        initStudetsLists(date);
    }

    private void initStudetsLists(LocalDate date) {
        UserSettings.getInstance().getAllGroupTypes().forEach(groupType ->
                map.put(groupType, StudentsRepository.getInstance().getLessonList(date,groupType)));
    }

    @Nullable
    @Override
    public Map<GroupType, LiveData<List<Lesson>>> getValue() {
        return map;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
        initStudetsLists(date);
    }
}
