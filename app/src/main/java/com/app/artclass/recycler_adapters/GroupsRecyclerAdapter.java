package com.app.artclass.recycler_adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.database.entity.Lesson;
import com.app.artclass.fragments.DialogHandler;
import com.app.artclass.UserSettings;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.fragments.GroupRedactorFragment;
import com.app.artclass.R;
import com.app.artclass.database.DatabaseConverters;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsRecyclerAdapter.GroupViewHolder>{

    private Fragment fragment;
    private List<GroupViewHolder> viewHolders;

    /**
     * holds lessons for certain GroupData and assures groups has no duplicates
     */
    private TreeMap<GroupData, List<Lesson>> groupDataMap;
    private GroupData[] groupDataKeysArray;

    public GroupsRecyclerAdapter(Fragment fragment, List<GroupData> groups) {
        this.fragment = fragment;
        groupDataMap = new TreeMap<>();
        for (GroupData groupData : groups) {
            final GroupData finGroupData = groupData;
            StudentsRepository.getInstance().getLessonList(groupData.date, groupData.groupType)
                .observe(fragment.getViewLifecycleOwner(), lessons ->
                    groupDataMap.put(finGroupData, lessons));
        }

        groupDataKeysArray = groupDataMap.keySet().toArray(new GroupData[0]);

        viewHolders = new ArrayList<>();
    }

    public void addGroup(GroupData groupData){
        groupDataMap.put(groupData, null);
        StudentsRepository.getInstance().getLessonList(groupData.date,groupData.groupType).observe(fragment.getViewLifecycleOwner(),lessons ->
                groupDataMap.put(groupData, lessons.size() > 0 ? lessons : null));
        groupDataKeysArray = groupDataMap.keySet().toArray(new GroupData[0]);
        notifyDataSetChanged();
    }

    public void addGroups(List<GroupData> groupDataList){
        groupDataList.forEach(groupData -> {
            groupDataMap.put(groupData, null);
            StudentsRepository.getInstance().getLessonList(groupData.date,groupData.groupType).observe(fragment.getViewLifecycleOwner(),lessons ->
                    groupDataMap.put(groupData, lessons.size() > 0 ? lessons : null));
        });

        groupDataKeysArray = groupDataMap.keySet().toArray(new GroupData[0]);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elementView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent,false);
        GroupViewHolder viewHolder = new GroupViewHolder(elementView);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsRecyclerAdapter.GroupViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(groupDataMap == null)
            return 0;
        else
            return groupDataMap.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView dateTextField;
        TextView timeTextField;
        LocalDate groupDate;
        GroupType groupType;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextField = itemView.findViewById(R.id.time_view);
            dateTextField = itemView.findViewById(R.id.date_view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            final int deletePos = getAdapterPosition();
            DialogHandler.getInstance()
                    .ConfirmDeleteObject(fragment.getContext(),fragment.getContext().getString(R.string.group_str) + " for " + groupDataKeysArray[deletePos].dateLabel +" "+ timeTextField.getText(),
                    () -> {
                        try {
                            assert StudentsRepository.getInstance()!=null;
                            StudentsRepository.getInstance().deleteLessons(
                                    LocalDateTime.of(
                                    groupDate,
                                    groupDataKeysArray[deletePos].groupType.getTime()
                                    )
                            );
                        }catch (Exception | AssertionError e){
                            e.printStackTrace();
                        }finally {
                            groupDataMap.remove(deletePos);
                            notifyDataSetChanged();
                        }
                    },null);

            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onClick(View v) {

            GroupType curGroupType = UserSettings.getInstance().getGroupType(timeTextField.getText().toString());
            GroupRedactorFragment groupRedactorFragment =
                    new GroupRedactorFragment(groupDate, curGroupType, fragment.getFragmentManager());

            fragment.getFragmentManager().beginTransaction().replace(R.id.contentmain, groupRedactorFragment).addToBackStack(null).commit();
        }

        void bind(int position) {

            GroupData groupDataToBind = groupDataKeysArray[position];

            //needs fixes
            if (groupDataMap.get(groupDataToBind) != null) {

            }else{

            }
        }
    }

    /**
     * Class holds only data about group type
     * Lessons list loads asynchronously with process of creating new item in groups list
     */
    public static class GroupData implements Comparable{

        String dateLabel;
        String timeLabel;
        LocalDate date;
        public GroupType groupType;

        public GroupData(LocalDate date, GroupType groupType) {
            this.date = date;
            this.dateLabel = date.format(DatabaseConverters.getDateFormatter());
            this.timeLabel = groupType.getGroupName();
            this.groupType = groupType;
        }


        @Override
        public boolean equals(@Nullable Object obj) {
            try{
                GroupData grd = (GroupData)obj;

                return grd != null && (date == grd.date && groupType == grd.groupType);

            }catch (ClassCastException | AssertionError e){
                return false;
            }
        }

        @Override
        public int compareTo(@NotNull Object o) {
            try{
                GroupData gr = (GroupData) o;
                return this.date.compareTo(gr.date)*10+
                        this.groupType.getGroupName().compareTo(gr.groupType.getGroupName());
            }catch (ClassCastException e){
                return 0;
            }

        }
    }
}
