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
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.fragments.GroupRedactorFragment;
import com.app.artclass.R;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.list_adapters.LocalAdapter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupsListRecyclerAdapter extends RecyclerView.Adapter<GroupsListRecyclerAdapter.GroupViewHolder> implements LocalAdapter {

    private Fragment fragment;
    private List<GroupViewHolder> viewHolders;

    /**
     * holds lessons for certain GroupData and assures groups has no duplicates
     * every time map updates keys array must be updated to to function properly
     */
    private TreeMap<GroupData, List<Lesson>> groupDataMap;
    private GroupData[] groupDataKeysArray;

    public GroupsListRecyclerAdapter(Fragment fragment, List<GroupData> groups) {
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

    public void addGroup(GroupData groupData, List<Lesson> lessons){
        if(lessons.size()>0){
            groupDataMap.put(groupData, lessons);
            groupDataKeysArray = groupDataMap.keySet().toArray(new GroupData[0]);
            notifyDataSetChanged();
        }
    }

    public void addGroups(List<GroupData> groupDataList, List<List<Lesson>> allGroupLessons){
        for (int i = 0; i < groupDataList.size() && i < allGroupLessons.size(); i++) {
            List<Lesson> lessons = allGroupLessons.get(i);
            if(lessons.size()>0) {
                GroupData groupData = groupDataList.get(i);
                groupDataMap.put(groupData, lessons);
            }
        }
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
    public void onBindViewHolder(@NonNull GroupsListRecyclerAdapter.GroupViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(groupDataMap == null)
            return 0;
        else
            return groupDataMap.size();
    }

    @Override
    public void update() {
        notifyDataSetChanged();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView dateTextField;
        TextView timeTextField;
        GroupData groupData;

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
                    .ConfirmDeleteObject(fragment.getContext(),fragment.getContext().getString(R.string.group_str) + " for " + groupDataKeysArray[deletePos].dateLabel +" "+ timeTextField.getText(),() -> {
                        try {
                            StudentsRepository.getInstance()
                                    .deleteLessons(groupData.date, groupDataKeysArray[deletePos].groupType);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            groupDataMap.remove(groupDataKeysArray[deletePos]);
                            groupDataKeysArray = groupDataMap.keySet().toArray(new GroupData[0]);
                            notifyDataSetChanged();
                        }
                    },null);

            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onClick(View v) {
            GroupRedactorFragment groupRedactorFragment =
                    new GroupRedactorFragment(groupData.date, groupData.groupType, groupDataMap.get(groupData));

            fragment.getFragmentManager().beginTransaction().replace(R.id.contentmain, groupRedactorFragment).addToBackStack(null).commit();
        }

        void bind(int pos) {

            GroupData bindData = groupDataKeysArray[pos];
            groupData = bindData;

            if (pos == 0) {
                dateTextField.setText(bindData.dateLabel);
                timeTextField.setText(bindData.timeLabel);
                dateTextField.setVisibility(View.VISIBLE);
            }else{
                if(groupDataKeysArray[pos-1].date!=groupDataKeysArray[pos].date){
                    dateTextField.setVisibility(View.VISIBLE);
                    dateTextField.setText(bindData.dateLabel);
                    timeTextField.setText(bindData.timeLabel);
                }else {
                    dateTextField.setVisibility(View.GONE);
                    timeTextField.setText(bindData.timeLabel);
                }
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
            this.timeLabel = groupType.getName();
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
                        this.groupType.getTime().compareTo(gr.groupType.getTime());
            }catch (ClassCastException e){
                return 0;
            }

        }
    }
}
