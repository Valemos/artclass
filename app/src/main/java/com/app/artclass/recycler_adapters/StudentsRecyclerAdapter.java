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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// code for opening student card
//

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsRecyclerAdapter extends RecyclerView.Adapter<StudentsRecyclerAdapter.StudentViewHolder> implements LocalAdapter, Filterable {

    private Integer nameViewId;
    private Integer parameterTextId;
    private Integer elementLayout;
    private Fragment mFragment;
    private Map<Student,Boolean> studentCheckedStates = new HashMap<>();
    private final boolean isStudentSelectionAdapter;
    private List<Student> mStudentFilteredList;
    private List<Student> studentListInitial;
    private Filter studentQueryFilter;


    public StudentsRecyclerAdapter(Fragment fragment, Integer elementLayout, Integer nameViewId, Integer parameterTextId, boolean isStudentSelectionAdapter, List<Student> data) {
        this.isStudentSelectionAdapter = isStudentSelectionAdapter;
        studentListInitial = data;
        mStudentFilteredList = new ArrayList<>(data);
        mStudentFilteredList.sort(null);
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
        if (mStudentFilteredList == null) {
            return 0;
        }
        return mStudentFilteredList.size();
    }

    public List<Student> getItems() {
        return mStudentFilteredList;
    }

    public void addStudent(Student student) {
        mStudentFilteredList.add(student);
        mStudentFilteredList.sort(null);
        notifyDataSetChanged();
    }

    public void setStudents(List<Student> students) {
        studentListInitial = students;
        mStudentFilteredList = new ArrayList<>(studentListInitial);
        mStudentFilteredList.sort(null);
        notifyDataSetChanged();
    }

    public void deleteCheckedFromLesson(LocalDate date, GroupType groupType){
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < mStudentFilteredList.size(); i++){
            if(studentCheckedStates.getOrDefault(i,false)){
                toDelete.add(mStudentFilteredList.get(i));
            }
        }

        StudentsRepository.getInstance().deleteLessonsForStudentsList(date, groupType, toDelete);
        mStudentFilteredList.removeAll(toDelete);

        studentCheckedStates = new HashMap<>();
        notifyDataSetChanged();
    }

    public void deleteCheckedStudents(){
        List<Student> toDelete = new ArrayList<>();
        for (int i = 0; i < mStudentFilteredList.size(); i++){
            if(studentCheckedStates.getOrDefault(mStudentFilteredList.get(i),false)){
                toDelete.add(mStudentFilteredList.get(i));
            }
        }

        StudentsRepository.getInstance().deleteStudents(toDelete);
        mStudentFilteredList.remove(toDelete);

        studentCheckedStates = new HashMap<>();
        notifyDataSetChanged();
    }

    public List<Student> getSelectedStudents(){
        List<Student> selected = new ArrayList<>();
        for(int i = 0; i < mStudentFilteredList.size(); i++){
            if(studentCheckedStates.getOrDefault(mStudentFilteredList.get(i),false)){
                selected.add(mStudentFilteredList.get(i));
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
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Student> notFilteredList = studentListInitial;

            int count = notFilteredList.size();
            final List<Student> filteredValues = new ArrayList<>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = notFilteredList.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    filteredValues.add(notFilteredList.get(i));
                }
            }

            results.values = filteredValues;
            results.count = filteredValues.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mStudentFilteredList.clear();
            mStudentFilteredList.addAll((Collection<? extends Student>) results.values);
            notifyDataSetChanged();
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
                if (!studentCheckedStates.getOrDefault(mStudentFilteredList.get(adapterPosition), false)) {
                    checkBox.setChecked(true);
                    studentCheckedStates.put(mStudentFilteredList.get(adapterPosition), true);
                }else{
                    checkBox.setChecked(false);
                    studentCheckedStates.put(mStudentFilteredList.get(adapterPosition), false);
                }
            }
        };

        @Override
        public void onClick(View v) {
            if(getAdapterPosition()!=RecyclerView.NO_POSITION) {
                StudentCard studentCard = new StudentCard(mStudentFilteredList.get(getAdapterPosition()));
                mFragment.getFragmentManager().beginTransaction().replace(R.id.main_content_id, studentCard).addToBackStack(null).commit();
            }
        }

        @SuppressLint("DefaultLocale")
        void bind(int position) {
            // use the sparse boolean array to check
            if (!studentCheckedStates.getOrDefault(mStudentFilteredList.get(position), false)) {
                checkBox.setChecked(false);}
            else {
                checkBox.setChecked(true);
            }
            nameView.setText(mStudentFilteredList.get(position).getName());

            if(parameterView == null){
                return;
            }
            if(parameterTextId == R.id.hours_left_view){
                parameterView.setText(String.format("%d h", mStudentFilteredList.get(position).getHoursBalance()));
            }else if(parameterTextId == R.id.balance_view){
                parameterView.setText(DatabaseConverters.getMoneyFormat().format(mStudentFilteredList.get(position).getMoneyBalance()));
            }
        }
    }
}