package com.app.artclass;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.database.DatabaseManager;
import com.app.artclass.database.Student;

import java.util.ArrayList;
import java.util.List;

// code for opening student card
//

public class StudentsRecyclerAdapter extends RecyclerView.Adapter<StudentsRecyclerAdapter.StudentViewHolder> {

    private Integer nameViewId;

    private Integer parameterTextId;
    private Integer elementLayout;
    private FragmentManager fragmentManager;
    private SparseBooleanArray itemCheckedStates = new SparseBooleanArray();
    private List<Student> studentList;


    public void addStudent(Student student) {
        studentList.add(student);
        studentList.sort(Student.getStudentComparator());
        notifyDataSetChanged();
    }

    public void addStudents(List<Student> students) {
        for(Student st : students){
            studentList.add(st);
        }
        studentList.sort(Student.getStudentComparator());
        notifyDataSetChanged();
    }
    //handle null fr manager

    public StudentsRecyclerAdapter(Integer elementLayout, Integer nameViewId, Integer parameterTextId, List<Student> data, FragmentManager fragmentManager) {
        studentList = data;
        studentList.sort(Student.getStudentComparator());
        this.elementLayout = elementLayout;
        this.nameViewId = nameViewId;
        this.parameterTextId = parameterTextId;

        this.fragmentManager = fragmentManager;
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

            if(fragmentManager != null) {
                nameView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String studentName = ((TextView)v).getText().toString();
                        Student student = DatabaseManager.getInstance(v.getContext()).getStudent(studentName);
                        StudentCard studentCard = new StudentCard(student);
                        fragmentManager.beginTransaction().replace(R.id.contentmain, studentCard).addToBackStack(null).commit();
                    }
                });
            }

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
                parameterView.setText(String.valueOf(studentList.get(position).getBalance()));
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

    public void deleteCheckedFromLesson(int date, String time){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                toDelete.add(studentList.get(i));
            }
        }

        for (Student st : toDelete) {
            databaseManager.deleteStudentFromLesson(date , time, st);
            studentList.remove(st);
        }

        itemCheckedStates = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void deleteCheckedStudents(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                toDelete.add(studentList.get(i));
            }
        }

        for (Student st : toDelete) {
            databaseManager.deleteStudent(st);
            studentList.remove(st);
        }

        itemCheckedStates = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public List<String> getSelectedStudentNames(){
        List<String> selected = new ArrayList<>();
        for(int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                selected.add(studentList.get(i).getFullName());
            }
        }

        return selected;
    }
}