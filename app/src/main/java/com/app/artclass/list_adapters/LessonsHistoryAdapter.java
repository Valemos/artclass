package com.app.artclass.list_adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.artclass.R;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.entity.Lesson;

import java.util.List;

public class LessonsHistoryAdapter extends ArrayAdapter<Lesson> implements LocalAdapter {

    private List<Lesson> lessonList;
    private int mResource;

    public LessonsHistoryAdapter(@NonNull Context context, int resource, @NonNull List<Lesson> lessonList) {
        super(context, resource, lessonList);
        this.lessonList = lessonList;
        this.lessonList.sort(Lesson.getDateComparator());
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(mResource,null);

        TextView groupNameView = convertView.findViewById(R.id.group_name_view);
        TextView dateView = convertView.findViewById(R.id.date_view);
        TextView weekdayView = convertView.findViewById(R.id.weekday_view);

        groupNameView.setText(lessonList.get(position).getGroupType().getName());
        dateView.setText(lessonList.get(position).getDate().format(DatabaseConverters.getDateFormatter()));
        weekdayView.setText(lessonList.get(position).getGroupType().getWeekday().getName());

        return convertView;
    }

    public List<Lesson> getItems(){return lessonList;}

    @Override
    public void update() {
        this.lessonList.sort(Lesson.getDateComparator());
        notifyDataSetChanged();
    }
}
