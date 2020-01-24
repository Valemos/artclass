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
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;
import com.app.artclass.fragments.StudentCard;
import com.app.artclass.list_adapters.LocalAdapter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupRedactorAdapter extends RecyclerView.Adapter<GroupRedactorAdapter.LessonViewHolder> implements LocalAdapter {

    private final Fragment mFragment;
    private final int elementLayout;
    private final int balanceTextId;
    private final int nameViewId;

    private SparseBooleanArray itemCheckedStates = new SparseBooleanArray();
    private List<LessonViewHolder> viewHolders = new ArrayList<>();

    private final LocalDate groupDate;
    private final GroupType groupType;
    private List<Lesson> lessonsList;

    public GroupRedactorAdapter(@NonNull Fragment fragment, int elementLayout, int nameViewId, int balanceTextViewId, @NonNull List<Lesson> lessons) {
        this.mFragment = fragment;
        this.elementLayout = elementLayout;
        this.balanceTextId = balanceTextViewId;
        this.nameViewId = nameViewId;
        lessonsList = lessons;

        groupDate = lessons.get(0).getDate();
        groupType = lessons.get(0).getGroupType();
    }

    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        lessonsList.forEach(lesson -> students.add(lesson.getStudent()));
        return students;
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
        return lessonsList!=null?lessonsList.size():0;
    }

    public void deleteCheckedItems(){
        List<Lesson> toDelete = new ArrayList<>();
        for (int i = 0; i < lessonsList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                toDelete.add(lessonsList.get(i));
            }
        }

        StudentsRepository.getInstance().deleteLessons(toDelete);
        lessonsList.removeAll(toDelete);
        itemCheckedStates = new SparseBooleanArray();
        viewHolders.forEach(lessonViewHolder -> lessonViewHolder.setCheckBox(false));
        notifyDataSetChanged();
    }

    public void addStudentToGroup(Student studentNew){
        Lesson lessonNew = new Lesson(groupDate, studentNew, groupType);
        StudentsRepository.getInstance().addLesson(lessonNew);
        lessonsList.add(lessonNew);
        notifyDataSetChanged();
    }

    @Override
    public void update() {
        StudentsRepository.getInstance().getLessonList(groupDate,groupType).observe(mFragment.getViewLifecycleOwner(),lessons -> {
            lessonsList.clear();
            lessonsList.addAll(lessons);
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
            if(getAdapterPosition()!=RecyclerView.NO_POSITION) {
                StudentCard studentCard = new StudentCard(lessonsList.get(getAdapterPosition()).getStudent());
                mFragment.getFragmentManager().beginTransaction().replace(R.id.contentmain, studentCard).addToBackStack(null).commit();
            }
        }

        void bind(int pos) {
            Student student = lessonsList.get(pos).getStudent();
            nameView.setText(student.getName());
            balanceView.setText(DatabaseConverters.getMoneyFormat().format(student.getMoneyBalance()));
        }
    }
}
