package com.example.mac.swinedu.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mac.swinedu.ListAdapters.MemberListAdapter;
import com.example.mac.swinedu.Messaging;
import com.example.mac.swinedu.Models.User;
import com.example.mac.swinedu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentMemberFragment extends Fragment
{
    private String userKey;
    private String classKey;
    private ListView mMemberList;
    private ArrayList<User> mMembers;
    private View thisFragment;
    private Context parentActivity;

    public StudentMemberFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        thisFragment = inflater.inflate(R.layout.fragment_members, container, false);
        getDataFromMain();
        initiateElements();
        listMembers();
        // Inflate the layout for this fragment
        return thisFragment;
    }

    /**
     * get data from the parent activity
     */
    private void getDataFromMain()
    {
        userKey = getArguments().getString("userkey");
        classKey = getArguments().getString("classkey");
    }

    /**
     * initiate elements in the fragment
     */
    private void initiateElements()
    {
        parentActivity = getActivity();
        mMemberList = (ListView) thisFragment.findViewById(R.id.members_listview);
        mMembers = new ArrayList<>();
        registerListItemClick();
    }

    /**
     * handle users list item click
     */
    private void registerListItemClick()
    {
        mMemberList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                User chosen = mMembers.get(position);
                if (!chosen.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                {
                    Intent intent = new Intent(getActivity(), Messaging.class);
                    intent.putExtra("currentuserkey", userKey);
                    intent.putExtra("chosenkey", chosen.getId());
                    intent.putExtra("chosenemail", chosen.getEmail());
                    startActivity(intent);
                }
                else
                    Toast.makeText(getActivity(), "You cannot select yourselves", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * Query the database to retrieve the members of the class
     */
    private void listMembers()
    {
        listTeacher();
        listStudents();
    }

    /**
     * List the teacher
     */
    private void listTeacher()
    {
        //Reference the database
        DatabaseReference refStudsInClass = FirebaseDatabase.getInstance().getReference("classes").child(classKey);
        final DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("users");

        //Execute queries
        refStudsInClass.orderByKey().equalTo("teacher").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                String userKeys = dataSnapshot.getValue(String.class);
                refUsers.orderByKey().equalTo(userKeys).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        DataSnapshot userSnapShot = dataSnapshot.getChildren().iterator().next();
                        User mUser = userSnapShot.getValue(User.class);
                        if (mUser != null)
                            mMembers.add(mUser);
                        MemberListAdapter adapter = new MemberListAdapter(getActivity(), mMembers);
                        mMemberList.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    /**
     * List students in the class
     */
    private void listStudents()
    {
        //Reference the database
        DatabaseReference refStudsInClass = FirebaseDatabase.getInstance().getReference("classes").child(classKey).child("students");
        final DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("users");

        //Execute queries
        refStudsInClass.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                String userKeys = dataSnapshot.getKey();
                refUsers.orderByKey().equalTo(userKeys).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        DataSnapshot userSnapShot = dataSnapshot.getChildren().iterator().next();
                        User mUser = userSnapShot.getValue(User.class);
                        if (mUser != null)
                            mMembers.add(mUser);
                        MemberListAdapter adapter = new MemberListAdapter(parentActivity, mMembers);
                        mMemberList.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

}
