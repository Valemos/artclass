package com.app.artclass.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.artclass.Logger;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.fragments.dialog.AddNewStudentDialog;
import com.app.artclass.fragments.dialog.AddToGroupDialog;
import com.app.artclass.fragments.dialog.AlertDialogFragment;
import com.app.artclass.fragments.dialog.DialogCreationHandler;
import com.app.artclass.recycler_adapters.GroupsListRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupListFragment extends Fragment {

    public GroupListFragment() {
        Logger.getInstance().appendLog(getClass(),"init fragment");
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_group_list, container, false);

        //add adapter to update if groups are deleted
        final GroupsListRecyclerAdapter groupsRecyclerAdapter = new GroupsListRecyclerAdapter(this, new ArrayList<>());

        StudentsRepository.getInstance().getAllGroupTypes().observe(getViewLifecycleOwner(),groupTypeList -> {

            UserSettings.getInstance().setAllGroupTypes(groupTypeList);

            for (GroupType groupType : UserSettings.getInstance().getAllGroupTypes()) {
                final GroupType finGroupType = groupType;
                StudentsRepository.getInstance().getStudentsList(groupType).observe(getViewLifecycleOwner(), groupTypeWithStudents ->{
                    if(groupTypeWithStudents!=null)
                        groupsRecyclerAdapter.addGroup(finGroupType, groupTypeWithStudents.studentList);
                });
            }
        });

        RecyclerView groupsList = mainView.findViewById(R.id.groups_list);
        groupsList.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsList.setAdapter(groupsRecyclerAdapter);

        // set add and delete buttons
        FloatingActionButton btn_floating_addnewgroup = mainView.findViewById(R.id.fab_add_new_group);
        btn_floating_addnewgroup.setTag(R.id.adapter,groupsRecyclerAdapter);
        btn_floating_addnewgroup.setOnClickListener(view ->{
                    AddToGroupDialog addNewStudentDialog = new AddToGroupDialog(groupsRecyclerAdapter, null, null);
                    addNewStudentDialog.show(getFragmentManager(), "AddToGroup");
                });

        FloatingActionButton btn_floating_helpbutton = mainView.findViewById(R.id.fab_secondary);
        btn_floating_helpbutton.setOnClickListener(v ->{
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment(getString(R.string.title_help),getString(R.string.message_group_delete_help), null);
            alertDialogFragment.show(getFragmentManager(), "AlertDialog");
        });

        return mainView;
    }
}
