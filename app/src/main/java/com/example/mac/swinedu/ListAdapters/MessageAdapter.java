package com.example.mac.swinedu.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mac.swinedu.Models.Messages;
import com.example.mac.swinedu.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mac on 11/7/17.
 */

public class MessageAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Messages> mMessages;

    public MessageAdapter(Context context, ArrayList<Messages> messages)
    {
        mContext = context;
        mMessages = messages;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;
        Messages message = mMessages.get(position);
        if (message.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
            row = mInflater.inflate(R.layout.message_item_sender, parent, false);
        else
            row = mInflater.inflate(R.layout.message_item, parent, false);


        TextView timeText = (TextView) row.findViewById(R.id.time_text);
        TextView messageText = (TextView) row.findViewById(R.id.message_text);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        timeText.setText(format.format(new Date(message.getTime())));
        messageText.setText(message.getMessage());

        return row;
    }
}
