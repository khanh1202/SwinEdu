package com.example.mac.swinedu.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mac.swinedu.Models.Classroom;
import com.example.mac.swinedu.R;

import java.util.ArrayList;

/**
 * Created by mac on 11/5/17.
 */

public class ClassListAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Classroom> mClasses;

    public ClassListAdapter(Context context, ArrayList<Classroom> classes)
    {
        mContext = context;
        mClasses = classes;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return mClasses.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mClasses.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = mInflater.inflate(R.layout.class_list_item, parent, false);
        Classroom classRoom = mClasses.get(position);

        TextView subjectText = (TextView) row.findViewById(R.id.subject_textview);
        TextView nummembersText = (TextView) row.findViewById(R.id.nummembers_textview);

        subjectText.setText(classRoom.getSubject());
        String members = String.valueOf(classRoom.getNumofmembers()) + " Members";
        nummembersText.setText(members);

        return row;
    }
}
