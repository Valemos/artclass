package com.app.artclass.recycler_adapters;

import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;
import com.app.artclass.fragments.StudentCard;
import com.app.artclass.list_adapters.LocalAdapter;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupRedactorAdapter extends RecyclerView.Adapter<GroupRedactorAdapter.LessonViewHolder> implements LocalAdapter {

    private final Fragment mFragment;
    private final int elementLayout;
    private final int balanceTextId;
    private final int nameViewId;
    private TextView studentsCountView;

    private SparseBooleanArray itemCheckedStates = new SparseBooleanArray();
    private List<LessonViewHolder> viewHolders = new ArrayList<>();

    private GroupType mGroupType;
    private List<Student> mStudentsList;

    public GroupRedactorAdapter(@NonNull Fragment fragment, int elementLayout, int nameViewId, int balanceTextViewId, TextView studentsCountView, GroupType groupType, List<Student> studentsList) {
        this.mFragment = fragment;
        this.elementLayout = elementLayout;
        this.balanceTextId = balanceTextViewId;
        this.nameViewId = nameViewId;
        mGroupType = groupType;
        mStudentsList = studentsList;

        this.studentsCountView = studentsCountView;
        this.studentsCountView.setText(String.format(mFragment.getContext().getString(R.string.groupredctor_amount_label), mStudentsList.size()));
    }

    public List<Student> getStudents() {
        return mStudentsList;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elementView = LayoutInflater.from(parent.getContext())
                .inflate(elementLayout, parent, false);
        LessonViewHolder vh = new LessonViewHolder(elementView);
        viewHolders.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mStudentsList != null? mStudentsList.size():0;
    }

    public void deleteCheckedItems(){
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < mStudentsList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                toDelete.add(mStudentsList.get(i));
            }
        }

        StudentsRepository.getInstance().deleteStudentsFromGroup(mGroupType,toDelete);
        mStudentsList.removeAll(toDelete);
        itemCheckedStates = new SparseBooleanArray();
        viewHolders.forEach(lessonViewHolder -> lessonViewHolder.setCheckBox(false));
        update();
    }

    public void addStudentToGroup(Student studentNew){
        StudentsRepository.getInstance().addStudentToGroup(mGroupType, studentNew);
        mStudentsList.add(studentNew);
        notifyDataSetChanged();
    }

    @Override
    public void update() {
        StudentsRepository.getInstance().getStudentsForGroup(mGroupType).observe(mFragment.getViewLifecycleOwner(),groupTypeWithStudents -> {
            mStudentsList.clear();
            mStudentsList.addAll(groupTypeWithStudents.studentList);
            studentsCountView.setText(String.format(mFragment.getContext().getString(R.string.groupredctor_amount_label), mStudentsList.size()));
            notifyDataSetChanged();
        });
    }

    protected class LessonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameView;
        private TextView balanceView;
        private CheckBox checkBox;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(nameViewId);
            checkBox = itemView.findViewById(R.id.checkBox);
            balanceView = itemView.findViewById(balanceTextId);

            nameView.setOnClickListener(this);
            balanceView.setOnClickListener(checkboxListener);
            checkBox.setOnClickListener(checkboxListener);
        }

        View.OnClickListener checkboxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                if (!itemCheckedStates.get(adapterPosition, false)) {
                    checkBox.setChecked(true);
                    itemCheckedStates.put(adapterPosition, true);
                }else{
                    checkBox.setChecked(false);
                    itemCheckedStates.put(adapterPosition, false);
                }
            }
        };

        public void setCheckBox(boolean state){
            checkBox.setChecked(state);
        }

        @Override
        public void onClick(View v) {
            StudentCard studentCard = new StudentCard(mStudentsList.get(getAdapterPosition()));
            mFragment.getFragmentManager().beginTransaction().replace(R.id.main_content_id, studentCard).addToBackStack(null).commit();
        }

        void bind(int pos) {
            Student student = mStudentsList.get(pos);
            nameView.setText(student.getName());
            balanceView.setText(String.format(UserSettings.getInstance().getHoursTextFormat(),student.getHoursBalance()));
        }
    }
}
