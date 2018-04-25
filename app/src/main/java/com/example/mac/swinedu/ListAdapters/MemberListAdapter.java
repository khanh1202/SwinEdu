package com.example.mac.swinedu.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mac.swinedu.Models.User;
import com.example.mac.swinedu.R;

import java.util.ArrayList;

/**
 * Created by mac on 11/6/17.
 */

public class MemberListAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<User> mUsers;

    public MemberListAdapter(Context context, ArrayList<User> users)
    {
        mContext = context;
        mUsers = users;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = mInflater.inflate(R.layout.member_list_item, parent, false);
        User user = mUsers.get(position);

        TextView nameText = (TextView) row.findViewById(R.id.name_textview);
        TextView roleText = (TextView) row.findViewById(R.id.role_textview);

        nameText.setText(user.getName());
        roleText.setText(user.getRole());

        return row;
    }
}
