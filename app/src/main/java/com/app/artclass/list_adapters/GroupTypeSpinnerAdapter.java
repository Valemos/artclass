package com.app.artclass.list_adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.artclass.R;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.entity.GroupType;

import java.util.List;

public class GroupTypeSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context mContext;
    private int mResource;
    private List<GroupType> mGroupTypeList;

    public GroupTypeSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<GroupType> groupTypeList) {
        this.mContext = context;
        this.mResource = resource;
        mGroupTypeList = groupTypeList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(mContext).inflate(mResource, null);

        TextView groupNameView = rowView.findViewById(R.id.group_name_view);
        TextView timeView = rowView.findViewById(R.id.time_view);
        TextView weekdayView = rowView.findViewById(R.id.weekday_view);

        groupNameView.setText(mGroupTypeList.get(position).getName());
        timeView.setText(mGroupTypeList.get(position).getTime().format(DatabaseConverters.getTimeFormatter()));
        weekdayView.setText(mGroupTypeList.get(position).getWeekday().getName());

        return rowView;
    }

    @Override
    public int getCount() {
        return mGroupTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(mContext).inflate(mResource, null);

        TextView groupNameView = rowView.findViewById(R.id.group_name_view);
        TextView timeView = rowView.findViewById(R.id.time_view);
        TextView weekdayView = rowView.findViewById(R.id.weekday_view);

        groupNameView.setText(mGroupTypeList.get(position).getName());
        timeView.setText(mGroupTypeList.get(position).getTime().format(DatabaseConverters.getTimeFormatter()));
        weekdayView.setText(mGroupTypeList.get(position).getWeekday().getName());

        return rowView;
    }
}
