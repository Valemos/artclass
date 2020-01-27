package com.app.artclass.recycler_adapters;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.R;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.fragments.StudentCard;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.LocalAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// code for opening student card
//

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsRecyclerAdapter extends RecyclerView.Adapter<StudentsRecyclerAdapter.StudentViewHolder> implements LocalAdapter, Filterable {

    private Integer nameViewId;
    private Integer parameterTextId;
    private Integer elementLayout;
    private Fragment mFragment;
    private SparseBooleanArray itemCheckedStates = new SparseBooleanArray();
    private final boolean isStudentSelectionAdapter;
    private List<Student> studentList;
    private Filter studentQueryFilter;


    public StudentsRecyclerAdapter(Fragment fragment, Integer elementLayout, Integer nameViewId, Integer parameterTextId, boolean isStudentSelectionAdapter, List<Student> data) {
        this.isStudentSelectionAdapter = isStudentSelectionAdapter;
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

    public void deleteCheckedFromLesson(LocalDate date, GroupType groupType){
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                toDelete.add(studentList.get(i));
            }
        }

        StudentsRepository.getInstance().deleteLessonsForStudentsList(date, groupType, toDelete);
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

    public List<Student> getSelectedStudents(){
        List<Student> selected = new ArrayList<>();
        for(int i = 0; i < studentList.size(); i++){
            if(itemCheckedStates.get(i,false)){
                selected.add(studentList.get(i));
            }
        }

        return selected;
    }

    @Override
    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if(studentQueryFilter==null){
            studentQueryFilter = new StudentQueryFilter();
        }
        return studentQueryFilter;
    }

    private class StudentQueryFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
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

            if(isStudentSelectionAdapter){
                itemView.setOnClickListener(checkboxListener);
            }
            else{
                nameView.setOnClickListener(this);
                checkBox.setOnClickListener(checkboxListener);

                if(parameterTextId != null) {
                    parameterView = itemView.findViewById(parameterTextId);
                    parameterView.setOnClickListener(checkboxListener);
                }
            }
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

        @Override
        public void onClick(View v) {
            if(getAdapterPosition()!=RecyclerView.NO_POSITION) {
                StudentCard studentCard = new StudentCard(studentList.get(getAdapterPosition()));
                mFragment.getFragmentManager().beginTransaction().replace(R.id.main_content_id, studentCard).addToBackStack(null).commit();
            }
        }

        @SuppressLint("DefaultLocale")
        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemCheckedStates.get(position, false)) {
                checkBox.setChecked(false);}
            else {
                checkBox.setChecked(true);
            }
            nameView.setText(studentList.get(position).getName());

            if(parameterView == null){
                return;
            }
            if(parameterTextId == R.id.hours_left_view){
                parameterView.setText(String.format("%d h", studentList.get(position).getHoursBalance()));
            }else if(parameterTextId == R.id.balance_view){
                parameterView.setText(DatabaseConverters.getMoneyFormat().format(studentList.get(position).getMoneyBalance()));
            }
        }
    }


}