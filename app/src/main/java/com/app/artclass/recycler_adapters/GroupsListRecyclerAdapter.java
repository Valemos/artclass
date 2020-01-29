package com.app.artclass.recycler_adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.database.entity.Student;
import com.app.artclass.fragments.dialog.ConfirmDeleteObjectDialog;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.fragments.GroupRedactorFragment;
import com.app.artclass.R;
import com.app.artclass.list_adapters.LocalAdapter;

import java.util.List;
import java.util.TreeMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupsListRecyclerAdapter extends RecyclerView.Adapter<GroupsListRecyclerAdapter.GroupViewHolder> implements LocalAdapter {

    private Fragment fragment;

    /**
     * holds lessons for certain GroupData and assures groups has no duplicates
     * every time map updates it`s keys groupTypeKeysArray must be updated to function properly
     */
    private TreeMap<GroupType, List<Student>> groupTypeMap;
    private GroupType[] groupTypeKeysArray;

    public GroupsListRecyclerAdapter(Fragment fragment, List<GroupType> groups) {
        this.fragment = fragment;
        groupTypeMap = new TreeMap<>();

        for (GroupType groupType : groups) {
            final GroupType finGroupType = groupType;
            StudentsRepository.getInstance().getStudentsList(groupType).observe(fragment.getViewLifecycleOwner(), groupTypeWithStudents ->{
                    if(groupTypeWithStudents!=null)
                        groupTypeMap.put(finGroupType, groupTypeWithStudents.studentList);
                });
        }

        groupTypeKeysArray = groupTypeMap.keySet().toArray(new GroupType[0]);
    }

    public void addGroup(GroupType groupType, List<Student> students){
        if(students.size()>0){
            groupTypeMap.put(groupType, students);
            groupTypeKeysArray = groupTypeMap.keySet().toArray(new GroupType[0]);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elementView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent,false);
        return new GroupViewHolder(elementView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsListRecyclerAdapter.GroupViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(groupTypeMap == null)
            return 0;
        else
            return groupTypeMap.size();
    }

    @Override
    public void update() {
        notifyDataSetChanged();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        LinearLayout dateWrapper;
        TextView weekDayField;
        TextView groupTypeField;
        TextView studentsCountField;
        private GroupType curGroupType;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupTypeField = itemView.findViewById(R.id.time_view);
            weekDayField = itemView.findViewById(R.id.weekday_view);
            studentsCountField = itemView.findViewById(R.id.students_count_view);
            dateWrapper = itemView.findViewById(R.id.date_wrapper_layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            final int deletePos = getAdapterPosition();
            ConfirmDeleteObjectDialog confirmDeleteObjectDialog = new ConfirmDeleteObjectDialog(
                    String.format(fragment.getContext().getString(R.string.group_name_placeholder), groupTypeKeysArray[deletePos].getWeekday().getName(), groupTypeField.getText()),
                    () -> {
                        StudentsRepository.getInstance().delete(groupTypeKeysArray[deletePos]);
                        groupTypeMap.remove(groupTypeKeysArray[deletePos]);
                        groupTypeKeysArray = groupTypeMap.keySet().toArray(new GroupType[0]);
                        update();
                    },null);
            confirmDeleteObjectDialog.show(fragment.getFragmentManager(), "ConfirmDeleteObject");
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onClick(View v) {
            GroupRedactorFragment groupRedactorFragment =
                    new GroupRedactorFragment(curGroupType, groupTypeMap.get(curGroupType));

            fragment.getFragmentManager().beginTransaction().replace(R.id.main_content_id, groupRedactorFragment).addToBackStack(null).commit();
        }

        void bind(int pos){
            curGroupType = groupTypeKeysArray[pos];
            // set date view to group by date
            if (pos == 0) {
                weekDayField.setText(curGroupType.getWeekday().getName());
                dateWrapper.setVisibility(View.VISIBLE);
            }else{
                if(groupTypeKeysArray[pos-1].getWeekday()!= groupTypeKeysArray[pos].getWeekday()){
                    weekDayField.setText(curGroupType.getWeekday().getName());
                    dateWrapper.setVisibility(View.VISIBLE);
                }else {
                    dateWrapper.setVisibility(View.GONE);
                }
            }

            groupTypeField.setText(curGroupType.getName());
            studentsCountField.setText(String.valueOf(groupTypeMap.get(curGroupType).size()));
        }
    }
}
