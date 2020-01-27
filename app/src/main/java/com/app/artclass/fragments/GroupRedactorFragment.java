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
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Student;
import com.app.artclass.recycler_adapters.GroupRedactorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupRedactorFragment extends Fragment {

    private GroupType mGroupType;
    private final List<Student> mStudentsList;

    public GroupRedactorFragment(@NonNull GroupType groupType, List<Student> students) {
        this.mGroupType = groupType;
        mStudentsList = students;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_redactor, container, false);

        // set date and time
        TextView groupNameText = view.findViewById(R.id.group_name_view);
        TextView studentsAmountTextView = view.findViewById(R.id.students_count_view);
        groupNameText.setText(mGroupType.getName());

        GroupRedactorAdapter groupRedactorAdapter = new GroupRedactorAdapter(
                this,
                R.layout.item_all_students_list,
                R.id.name_view, R.id.balance_view,
                studentsAmountTextView,
                mGroupType,
                mStudentsList);
        RecyclerView groupLessonsListView = view.findViewById(R.id.group_redactor_list);
        groupLessonsListView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        groupLessonsListView.setAdapter(groupRedactorAdapter);

        FloatingActionButton btn_add_dialog = view.findViewById(R.id.fab_add_students);
        btn_add_dialog.setOnClickListener(v ->
                DialogHandler.getInstance().AddStudentsToGroup(this, groupRedactorAdapter, mGroupType, groupRedactorAdapter.getStudents()));

        FloatingActionButton btn_delete_selected = view.findViewById(R.id.fab_delete_selected);
        btn_delete_selected.setOnClickListener(v -> groupRedactorAdapter.deleteCheckedItems());

        return view;
    }
}
