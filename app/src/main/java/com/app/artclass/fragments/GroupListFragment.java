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

import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.list_adapters.LocalAdapter;
import com.app.artclass.recycler_adapters.GroupsListRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupListFragment extends Fragment {

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_group_list, container, false);

        //how much days to get
        //get groups for that days
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(UserSettings.getGroupDaysInitAmount());


        //add adapter to update if groups are deleted
        final GroupsListRecyclerAdapter groupsRecyclerAdapter = new GroupsListRecyclerAdapter(this, new ArrayList<>());
        mainView.setTag(groupsRecyclerAdapter);

        // setup groups recycler view
        while(currentDate.isBefore(endDate)) {
            for (GroupType groupType : UserSettings.getInstance().getAllGroupTypes()) {
                LocalDate finalCurrentDate = currentDate;
                StudentsRepository.getInstance().getLessonList(currentDate, groupType).observe(getViewLifecycleOwner(), lessons -> {
                    groupsRecyclerAdapter.addGroup(new GroupsListRecyclerAdapter.GroupData(finalCurrentDate, groupType), lessons);
                });
            }
            currentDate = currentDate.plusDays(1);
        }

        RecyclerView groupsList = mainView.findViewById(R.id.groups_list);
        groupsList.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsList.setAdapter(groupsRecyclerAdapter);

        // set add and delete buttons
        FloatingActionButton btn_floating_addnewgroup = mainView.findViewById(R.id.fab_add_new_group);
        btn_floating_addnewgroup.setTag(R.id.adapter,groupsRecyclerAdapter);
        btn_floating_addnewgroup.setOnClickListener(view ->
                DialogHandler.getInstance().AddStudentsToGroup(this,(LocalAdapter) view.getTag(R.id.adapter), null, null,null));

        FloatingActionButton btn_floating_helpbutton = mainView.findViewById(R.id.fab_secondary);
        btn_floating_helpbutton.setOnClickListener(v ->
                DialogHandler.getInstance()
                        .AlertDialog(this.getContext(),getString(R.string.title_help),getString(R.string.message_group_delete_help), null)
        );

        return mainView;
    }
}
