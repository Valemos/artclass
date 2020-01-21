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
import com.app.artclass.database.GroupType;
import com.app.artclass.recycler_adapters.GroupsRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupListFragment extends Fragment {

    private FragmentManager fragmentManager;
    private StudentsRepository studentsRepository;

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

        studentsRepository = StudentsRepository.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        List<GroupsRecyclerAdapter.GroupData> groupsData = new ArrayList<>(); //labels to add to adapter

        //form list of lists for tagging the groups
        List<GroupType> groupTypes = UserSettings.getInstance().getAllGroupTypes();

        //how much days to get
        //get groups for that days

        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(UserSettings.getGroupDaysInitAmount());

        for (int i = 0; currentDate.isBefore(endDate); i++) {

            //check if group exists for current day
            for (GroupType curGroupType: groupTypes) {
                LocalDate finalCurrentDate = currentDate;
                studentsRepository.getLessonList(currentDate,curGroupType).observe(getViewLifecycleOwner(), lessons -> {
                    if(lessons!=null)
                        groupsData.add(new GroupsRecyclerAdapter.GroupData(finalCurrentDate, curGroupType));
                });
            }
            currentDate = currentDate.plusDays(1);
        }
        // setup groups recycler view

        //add adapter to update if groups are deleted
        final GroupsRecyclerAdapter adapter = new GroupsRecyclerAdapter(this, studentsRepository, groupsData);
        mainView.setTag(adapter);

        RecyclerView groupsList = mainView.findViewById(R.id.groups_list);
        groupsList.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsList.setAdapter(adapter);

        // set add and delete buttons

        FloatingActionButton btn_floating_addnewgroup = mainView.findViewById(R.id.fab_add_new_group);
        btn_floating_addnewgroup.setTag(R.id.adapter,adapter);
        btn_floating_addnewgroup.setOnClickListener(view -> DialogHandler.getInstance().CreateNewGroup(this,(RecyclerView.Adapter) view.getTag(R.id.adapter), null, null,null));

        FloatingActionButton btn_floating_helpbutton = mainView.findViewById(R.id.fab_help);
        btn_floating_helpbutton.setOnClickListener(v ->
                DialogHandler.getInstance()
                        .AlertDialog(this.getContext(),getString(R.string.title_help),getString(R.string.message_group_delete_help))
        );

        return mainView;
    }
}
