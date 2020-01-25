package com.app.artclass.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.artclass.R;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.recycler_adapters.GroupRedactorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupRedactorFragment extends Fragment {

    private LocalDate dateValue;
    private GroupType groupType;
    private final List<Lesson> lessonsList;

    public GroupRedactorFragment(@NonNull LocalDate date, @NonNull GroupType groupType, List<Lesson> lessons) {
        this.dateValue = date;
        this.groupType = groupType;
        lessonsList = lessons;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_redactor, container, false);

        // set date and time
        TextView dateText = view.findViewById(R.id.date_redactor_view);
        TextView timeText = view.findViewById(R.id.time_redactor_view);
        TextView studentsAmountTextView = view.findViewById(R.id.students_count_view);

        dateText.setText(dateValue.format(DatabaseConverters.getDateFormatter()));
        timeText.setText(groupType.getName());

        GroupRedactorAdapter groupRedactorAdapter = new GroupRedactorAdapter(this, R.layout.item_all_students_list, R.id.name_view, R.id.balance_view, studentsAmountTextView, lessonsList);
        RecyclerView groupLessonsListView = view.findViewById(R.id.group_redactor_list);
        groupLessonsListView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        groupLessonsListView.setAdapter(groupRedactorAdapter);

        FloatingActionButton btn_add_dialog = view.findViewById(R.id.fab_add_students);
        btn_add_dialog.setOnClickListener(v ->
                DialogHandler.getInstance().AddStudentsToGroup(this, groupRedactorAdapter,
                        dateValue, groupType,
                        groupRedactorAdapter.getStudents()));

        FloatingActionButton btn_delete_selected = view.findViewById(R.id.fab_delete_selected);
        btn_delete_selected.setOnClickListener(v -> groupRedactorAdapter.deleteCheckedItems());

        return view;
    }
}
