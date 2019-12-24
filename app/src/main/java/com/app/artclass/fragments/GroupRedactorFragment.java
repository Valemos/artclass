package com.app.artclass.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.artclass.DialogHandler;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.DatabaseManager;
import com.app.artclass.database.GroupType;
import com.app.artclass.database.Lesson;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupRedactorFragment extends Fragment {

    private LocalDate dateValue;
    private GroupType groupType;
    private List<Lesson> lessonList;
    private FragmentManager fragmentManager;
    private DatabaseManager databaseManager;

    public GroupRedactorFragment() {
        // Required empty public constructor
    }

    public GroupRedactorFragment(LocalDate date, GroupType groupType, FragmentManager fragmentManager) {
        this.dateValue = date;
        this.groupType = groupType;
        this.databaseManager = DatabaseManager.getInstance();
        this.lessonList = databaseManager.getLessonList(LocalDateTime.of(date,groupType.getTime()));
        this.fragmentManager = fragmentManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_redactor, container, false);

        // set date and time
        TextView dateText = view.findViewById(R.id.date_redactor_view);
        TextView timeText = view.findViewById(R.id.time_redactor_view);

        dateText.setText(dateValue.format(DatabaseConverters.getDateFormatter()));
        timeText.setText(groupType.getGroupName());

        StudentsRecyclerAdapter adapter =
                new StudentsRecyclerAdapter(
                        R.layout.item_group_redactor,
                        R.id.name_view,
                        R.id.hours_left_view,
                        databaseManager.getStudentsList(dateValue, groupType.getTime()),
                        fragmentManager);

        RecyclerView list = view.findViewById(R.id.group_redactor_list);
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list.setAdapter(adapter);

        FloatingActionButton btn_add_dialog = view.findViewById(R.id.fab_add_students);
        btn_add_dialog.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                DialogHandler dialogHandler = new DialogHandler(getContext());
                dialogHandler.CreateNewGroup((RecyclerView.Adapter) v.getTag(), dateValue, groupType, ((StudentsRecyclerAdapter) v.getTag()).getItems(), null);
            }
        });
        btn_add_dialog.setTag(adapter);

        FloatingActionButton btn_delete_selected = view.findViewById(R.id.fab_delete_selected);
        btn_delete_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentsRecyclerAdapter adapter = (StudentsRecyclerAdapter) v.getTag();
                adapter.deleteCheckedFromLesson(LocalDateTime.of(dateValue,groupType.getTime()));
            }
        });
        btn_delete_selected.setTag(adapter);

        return view;
    }
}
