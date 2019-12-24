package com.app.artclass.recycler_adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.DialogHandler;
import com.app.artclass.UserSettings;
import com.app.artclass.database.GroupType;
import com.app.artclass.fragments.GroupRedactorFragment;
import com.app.artclass.R;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.DatabaseManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsRecyclerAdapter.GroupViewHolder>{

    private Context mContext;
    private FragmentManager fragmentManager;
    private DatabaseManager databaseManager;
    private List<GroupViewHolder> viewHolders;
    private List<GroupData> groupDataList;


    public GroupsRecyclerAdapter(Context context,FragmentManager fragmentManager, List<GroupData> groups) {
        this.fragmentManager = fragmentManager;
        mContext = context;
        databaseManager = DatabaseManager.getInstance();

        groupDataList = groups;
        groupDataList.sort(GroupData.getGroupsListComparator());

        this.viewHolders = new ArrayList<>();

    }

    public void addGroup(LocalDate dateTime, GroupType groupType){
        GroupData groupDataNew = new GroupData(dateTime, groupType);

        boolean exists = false;
        for (GroupData data:groupDataList) {
            if (data.equals(groupDataNew)){
                exists=true;
            }
        }

        if(!exists)groupDataList.add(groupDataNew);

        groupDataList.sort(GroupsRecyclerAdapter.GroupData.getGroupsListComparator());
        this.notifyDataSetChanged();
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
        if(groupDataList == null)
            return 0;
        else
            return groupDataList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView dateTextField;
        TextView timeTextField;
        LocalDate groupDate;
        GroupType groupType;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextField = itemView.findViewById(R.id.time_view);
            dateTextField = itemView.findViewById(R.id.date_view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            final int deletePos = getAdapterPosition();

            DialogHandler dialogHandler = new DialogHandler(mContext);
            dialogHandler.ConfirmDeleteObject(mContext.getString(R.string.group_str) +
                            " for " + groupDataList.get(deletePos).dateLabel +" "+ timeTextField.getText(),
                    () -> {
                        try {
                            databaseManager.deleteLessons(
                                    LocalDateTime.of(
                                    groupDate,
                                    groupDataList
                                            .get(deletePos)
                                            .groupType
                                            .getTime()
                                    )
                            );
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            groupDataList.remove(deletePos);
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
                    new GroupRedactorFragment(groupDate, curGroupType, fragmentManager);

            fragmentManager.beginTransaction().replace(R.id.contentmain, groupRedactorFragment).addToBackStack(null).commit();
        }

        void bind(int position){
            if(position==0){
                dateTextField.setText(groupDataList.get(position).dateLabel);
                dateTextField.setVisibility(View.VISIBLE);
            }else {
                if (groupDataList.get(position - 1).dateLabel !=
                    groupDataList.get(position).dateLabel)
                {
                    dateTextField.setText(groupDataList.get(position).dateLabel);
                    dateTextField.setVisibility(View.VISIBLE);
                } else {
                    dateTextField.setVisibility(View.GONE);
                }
            }

            timeTextField.setText(groupDataList.get(position).timeLabel);
            groupDate = groupDataList.get(position).date;
        }

    }

    public static class GroupData{

        private String dateLabel;
        private String timeLabel;
        private LocalDate date;
        private GroupType groupType;

        public boolean equals(@Nullable GroupData obj) {
            assert obj != null;
            return (date ==obj.date && groupType==obj.groupType);
        }

        public GroupData(LocalDate date, GroupType groupType) {
            this.date = date;
            this.dateLabel = date.format(DatabaseConverters.getDateFormatter());
            this.timeLabel = groupType.getGroupName();
        }

        private static final Comparator<GroupData> groupsListComparator = new Comparator<GroupData>() {
            @Override
            public int compare(GroupData o1, GroupData o2) {
                return o1.date.compareTo(o2.date);
            }
        };


        public String getTimeLabel() {
            return timeLabel;
        }

        public String getDateLabel() {
            return dateLabel;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDateLabel(String dateLabel) {
            this.dateLabel = dateLabel;
        }

        public void setTimeLabel(String timeLabel) {
            this.timeLabel = timeLabel;
        }

        public static Comparator<GroupData> getGroupsListComparator() {
            return groupsListComparator;
        }

    }
}
