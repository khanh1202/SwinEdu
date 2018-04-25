package com.example.mac.swinedu.teacheractivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mac.swinedu.ListAdapters.ClassListAdapter;
import com.example.mac.swinedu.LoginActivity;
import com.example.mac.swinedu.Models.Classroom;
import com.example.mac.swinedu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherMainActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private String userKey;
    private String userRole;
    private ListView mClassList;
    private ArrayList<Classroom> mClasses;
    private FloatingActionButton addClassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        InitializeUI();
    }

    private void InitializeUI()
    {
        mToolbar = (Toolbar) findViewById(R.id.teacher_toolbar);
        setSupportActionBar(mToolbar);

        //get intent from previous activity
        getData();

        initiateElements();

    }

    /**
     * get data from previous intent
     */
    private void getData()
    {
        Intent intent = getIntent();
        userKey = intent.getStringExtra("key");
        userRole = intent.getStringExtra("role");
    }

    /**
     * Get the views in this activity
     */
    private void initiateElements()
    {
        mClassList = (ListView) findViewById(R.id.list_classes);
        mClasses = new ArrayList<>();
        addClassButton = (FloatingActionButton) findViewById(R.id.fab);

        handleButtons();
        handleListItemClicked();
    }

    /**
     * Handle click of the buttons in this activity
     */
    private void handleButtons()
    {
        addClassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showAddClassDialog();
            }
        });
    }

    /**
     * Handle list item when it is clicked
     */
    private void handleListItemClicked()
    {
        mClassList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Classroom classroom = mClasses.get(position);
                startTeachingClass(classroom);
            }
        });
    }

    /**
     * Start teaching by starting a new intent
     * @param classroom the classroom object to get the ID
     */
    private void startTeachingClass(Classroom classroom)
    {
        Intent intent = new Intent(getApplicationContext(), TeacherClassActivity.class);
        intent.putExtra("userkey", userKey);
        intent.putExtra("classkey", classroom.getId());
        intent.putExtra("subjectname", classroom.getSubject());
        startActivity(intent);
    }

    /**
     * Present a alert dialog to get the information of a new class
     */
    private void showAddClassDialog()
    {
        AlertDialog.Builder dialogBuider = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.add_class_dialog, null);
        dialogBuider.setView(dialogView);

        dialogBuider.setTitle("Add New Class");

        final AlertDialog alertDialog = dialogBuider.create();
        alertDialog.show();

        //get the dialog views
        final EditText subjectText = (EditText) dialogView.findViewById(R.id.subject_edittext);
        Button addButton = (Button) dialogView.findViewById(R.id.addclass_button);

        //Handle button click
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String subjectName = subjectText.getText().toString();
                if (subjectName.isEmpty())
                {
                    subjectText.setError("Subject Name Required");
                }
                else
                {
                    addNewClass(subjectName);
                }
                alertDialog.dismiss();
            }
        });


    }

    /**
     * add new class to the database
     * @param subjectName the subject name
     */
    private void addNewClass(String subjectName)
    {

        DatabaseReference ref_1 = FirebaseDatabase.getInstance().getReference("classes");
        DatabaseReference ref_2 = FirebaseDatabase.getInstance().getReference("users").child(userKey).child("classes");

        String classId = ref_1.push().getKey();
        Classroom newClass = new Classroom(classId,subjectName, userKey);

        ref_1.child(classId).setValue(newClass);
        ref_2.child(classId).setValue(true);

        Toast.makeText(getApplicationContext(), "Add Class Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        if (id == R.id.sign_out)
        {
            performSignout();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a dialog box to ask for confirmation
     */
    private void performSignout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage("Do you want to signout?")
                .setTitle("Sign Out");


        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                FirebaseAuth.getInstance().signOut();
                dialog.dismiss();

                //start Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Override the onStart to retrieve the classes from the database
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        mClasses.clear();
        DatabaseReference ref_1 = FirebaseDatabase.getInstance().getReference("users").child(userKey).child("classes");
        ref_1.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                String classKey = dataSnapshot.getKey();
                Log.w("Class key: ", classKey);
                DatabaseReference ref_2 = FirebaseDatabase.getInstance().getReference("classes");
                ref_2.orderByKey().equalTo(classKey).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        Classroom newClass = firstChild.getValue(Classroom.class);
                        Log.w("The class name: ", newClass.getSubject());
                        mClasses.add(newClass);
                        ClassListAdapter adapter = new ClassListAdapter(getApplicationContext(), mClasses);
                        mClassList.setAdapter(adapter);
                        Log.w("Num of classes", String.valueOf(mClasses.size()));
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
