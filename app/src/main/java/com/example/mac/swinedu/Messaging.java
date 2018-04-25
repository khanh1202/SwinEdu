package com.example.mac.swinedu;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mac.swinedu.ListAdapters.MessageAdapter;
import com.example.mac.swinedu.Models.ChatMembers;
import com.example.mac.swinedu.Models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class Messaging extends AppCompatActivity
{
    private Toolbar mToolbar;
    private FloatingActionButton sendButton;
    private EditText messageText;
    private ListView messageList;
    private String chosenKey;
    private String currentUserKey;
    private String chosenEmail;
    private ArrayList<Messages> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        InitializeUI();
    }

    private void InitializeUI()
    {
        mToolbar = (Toolbar) findViewById(R.id.messaging_toolbar);
        setSupportActionBar(mToolbar);

        getDataFromPrevious();
        initiateElements();
    }

    private void initiateElements()
    {
        sendButton = (FloatingActionButton) findViewById(R.id.send);
        messageList = (ListView) findViewById(R.id.message_listview);
        messageText = (EditText) findViewById(R.id.message_input);
        mMessages = new ArrayList<>();


        handleSendButtonClick();
    }

    private void getDataFromPrevious()
    {
        Intent intent = getIntent();
        chosenKey = intent.getStringExtra("chosenkey");
        currentUserKey = intent.getStringExtra("currentuserkey");
        chosenEmail = intent.getStringExtra("chosenemail");

    }

    private void handleSendButtonClick()
    {
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String newMess = messageText.getText().toString();
                if (!newMess.isEmpty())
                {
                    messageText.setText("");
                    addNewMessage(newMess);
                }
            }
        });
    }

    private void addNewMessage(final String newMess)
    {
        //reference the database
        final DatabaseReference refMembers = FirebaseDatabase.getInstance().getReference("members");
        //create new Message object
        final Messages newMessage = new Messages(newMess, new Date().getTime(), FirebaseAuth.getInstance().getCurrentUser().getEmail());


        //check if two persons has chatted before
        refMembers.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot converseSnap : dataSnapshot.getChildren())
                {
                    String conversationKey = converseSnap.getKey();
                    ChatMembers members = converseSnap.getValue(ChatMembers.class);
                    //if the conversation has been created before
                    if (members.getMembers().containsKey(currentUserKey) && members.getMembers().containsKey(chosenKey))
                    {
                        //add new message to the existing conversation
                        DatabaseReference refAddMessages = FirebaseDatabase.getInstance().getReference("messages").child(conversationKey);
                        String conversationId = refAddMessages.push().getKey();


                        //perform adding new message
                        refAddMessages.child(conversationId).setValue(newMessage);
                        return;
                    }

                }
                //if there is no conversation before
                String converId = refMembers.push().getKey();
                DatabaseReference refToMessages = FirebaseDatabase.getInstance().getReference("messages").child(converId);
                String messageId = refToMessages.push().getKey();

                ChatMembers newMembers = new ChatMembers();
                newMembers.addMember(currentUserKey);
                newMembers.addMember(chosenKey);

                refMembers.child(converId).setValue(newMembers);
                refToMessages.child(messageId).setValue(newMessage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mMessages.clear();

        //reference the database
        final DatabaseReference refMembers = FirebaseDatabase.getInstance().getReference("members");

        refMembers.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot converseSnap : dataSnapshot.getChildren())
                {
                    String conversationKey = converseSnap.getKey();
                    ChatMembers members = converseSnap.getValue(ChatMembers.class);
                    //if the conversation has been created before
                    if (members.getMembers().containsKey(currentUserKey) && members.getMembers().containsKey(chosenKey))
                    {
                        DatabaseReference refToMessages = FirebaseDatabase.getInstance().getReference("messages").child(conversationKey);
                        refToMessages.orderByChild("time").addChildEventListener(new ChildEventListener()
                        {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s)
                            {
                                Messages retrievedMess = dataSnapshot.getValue(Messages.class);
                                mMessages.add(retrievedMess);
                                MessageAdapter adapter = new MessageAdapter(getApplicationContext(), mMessages);
                                messageList.setAdapter(adapter);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


    }
}
