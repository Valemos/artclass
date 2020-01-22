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
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.fragments.StudentCard;
import com.app.artclass.database.entity.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// code for opening student card
//

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsRecyclerAdapter extends RecyclerView.Adapter<StudentsRecyclerAdapter.StudentViewHolder>{

    private Integer nameViewId;
    private Integer parameterTextId;
    private Integer elementLayout;
    private Fragment mFragment;
    private SparseBooleanArray itemCheckedStates = new SparseBooleanArray();
    private List<Student> studentList;


    public StudentsRecyclerAdapter(Fragment fragment, Integer elementLayout, Integer nameViewId, Integer parameterTextId, List<Student> data) {
        studentList = data;
        studentList.sort(null);
        this.elementLayout = elementLayout;
        this.nameViewId = nameViewId;
        this.parameterTextId = parameterTextId;

        mFragment = fragment;
    }

    @NonNull
    @Override
    public StudentsRecyclerAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elementView = LayoutInflater.from(parent.getContext())
                .inflate(elementLayout, parent, false);
        return new StudentViewHolder(elementView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (studentList == null) {
            return 0;
        }
        return studentList.size();
    }

    public List<Student> getItems() {
        return studentList;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        // each data item is just a string in this case
        TextView nameView;
        TextView parameterView;
        CheckBox checkBox;

        StudentViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(nameViewId);
            checkBox = itemView.findViewById(R.id.checkBox);

            if(parameterTextId != null) {
                parameterView = itemView.findViewById(parameterTextId);
            }

            nameView.setOnClickListener(v -> {
                String studentName = ((TextView)v).getText().toString();

                StudentsRepository.getInstance().getStudent(studentName).observe(mFragment.getViewLifecycleOwner(),student -> {
                    StudentCard studentCard = new StudentCard(student);
                    mFragment.getFragmentManager().beginTransaction().replace(R.id.contentmain, studentCard).addToBackStack(null).commit();
                });

            });

            checkBox.setOnClickListener(this);
        }


        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemCheckedStates.get(position, false)) {
                checkBox.setChecked(false);}
            else {
                checkBox.setChecked(true);
            }
            nameView.setText(String.valueOf(studentList.get(position).getName()));

            if(parameterView == null){
                return;
            }
            if(parameterTextId == R.id.hours_left_view){
                parameterView.setText(String.valueOf(studentList.get(position).getHoursBalance())+" h");
            }else if(parameterTextId == R.id.balance_view){
                parameterView.setText(String.valueOf(studentList.get(position).getMoneyBalance()));
            }
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!itemCheckedStates.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemCheckedStates.put(adapterPosition, true);
            }
            else  {
                checkBox.setChecked(false);
                itemCheckedStates.put(adapterPosition, false);
            }
        }
    }

    public void addStudent(Student student) {
        studentList.add(student);
        studentList.sort(null);
        notifyDataSetChanged();
    }

    public void setStudents(List<Student> students) {
        studentList = students;
        studentList.sort(null);
        notifyDataSetChanged();
    }

    public void deleteCheckedFromLesson(LocalDateTime dateTime){
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                toDelete.add(studentList.get(i));
            }
        }


        StudentsRepository.getInstance().deleteLessonsForStudentsList(dateTime, toDelete);
        studentList.removeAll(toDelete);

        itemCheckedStates = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void deleteCheckedStudents(){
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                toDelete.add(studentList.get(i));
            }
        }

        StudentsRepository.getInstance().deleteStudents(toDelete);
        studentList.remove(toDelete);

        itemCheckedStates = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public List<String> getSelectedStudentNames(){
        List<String> selected = new ArrayList<>();
        for(int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                selected.add(studentList.get(i).getName());
            }
        }

        return selected;
    }
}