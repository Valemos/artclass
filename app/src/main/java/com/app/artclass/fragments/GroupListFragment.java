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

import com.app.artclass.DialogHandler;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseManager;
import com.app.artclass.database.GroupType;
import com.app.artclass.recycler_adapters.GroupsRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupListFragment extends Fragment {

    private FragmentManager fragmentManager;

    public GroupListFragment() {
        // Required empty public constructor
    }

    public GroupListFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_group_list, container, false);

        DatabaseManager databaseManager = DatabaseManager.getInstance(mainView.getContext());
        List<GroupsRecyclerAdapter.GroupData> groupsData = new ArrayList<>(); //labels to add to adapter



        //form list of lists for tagging the groups
        List<GroupType> groupTypes = UserSettings.getInstance().getGroupTypes();

        //how much days to get
        //get groups for that days

        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(UserSettings.getGroupDaysInitAmount());

        for (int i = 0; currentDate.isBefore(endDate); i++) {

            //check if group exists for current day
            for (GroupType curGroupType: groupTypes) {
                if (databaseManager.isGroupExists(currentDate,curGroupType.getTime())) {
                    groupsData.add(new GroupsRecyclerAdapter.GroupData(currentDate, curGroupType));
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        // setup groups recycler view

        //add adapter to update if groups are deleted
        final GroupsRecyclerAdapter adapter = new GroupsRecyclerAdapter(getContext(),fragmentManager, groupsData);
        mainView.setTag(adapter);

        RecyclerView groupsList = mainView.findViewById(R.id.groups_list);
        groupsList.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsList.setAdapter(adapter);

        // set add and delete buttons

        FloatingActionButton btn_floating_addnewgroup = mainView.findViewById(R.id.fab_add_new_group);
        btn_floating_addnewgroup.setTag(adapter);
        btn_floating_addnewgroup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                DialogHandler dialogHandler = new DialogHandler(getContext());
                dialogHandler.CreateNewGroup((RecyclerView.Adapter) view.getTag(), null, null,null, null);
            }
        });

        FloatingActionButton btn_floating_helpbutton = mainView.findViewById(R.id.fab_help);
        btn_floating_helpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsRecyclerAdapter adapter = (GroupsRecyclerAdapter)((View)v.getParent().getParent()).getTag();
                DialogHandler dialogHandler = new DialogHandler(getContext());
                dialogHandler.AlertDialog(getString(R.string.title_help),getString(R.string.message_group_delete_help));
            }
        });

        return mainView;
    }
}
