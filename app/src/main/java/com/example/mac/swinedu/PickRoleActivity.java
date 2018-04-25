package com.example.mac.swinedu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mac.swinedu.Models.User;
import com.example.mac.swinedu.studentactivities.StudentMainActivity;
import com.example.mac.swinedu.teacheractivities.TeacherMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PickRoleActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private Button teacherButton;
    private Button studentButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_role);
        InitializeUI();
    }

    private void InitializeUI()
    {
        mToolbar = (Toolbar) findViewById(R.id.pickrole_toolbar);
        setSupportActionBar(mToolbar);

        setupFirebase();
        InitiateElements();
    }

    /**
     * Perform some setups with Firebase Authentication
     */
    private void setupFirebase()
    {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Initiate 2 buttons in the activity
     */
    private void InitiateElements()
    {
        teacherButton = (Button) findViewById(R.id.teacher_button);
        studentButton = (Button) findViewById(R.id.student_button);

        RegisterButtons();
    }

    /**
     * Handle 2 buttons click
     */
    private void RegisterButtons()
    {
        teacherButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SignUp("teacher");
            }
        });

        studentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SignUp("student");
            }
        });
    }

    /**
     * Sign up method. It receives the intent and call method to sign up
     */
    private void SignUp(String role)
    {
        Intent received = getIntent();

        String name = received.getStringExtra("name");
        String email = received.getStringExtra("email");

        addUser(name, email, role);
    }


    private void addUser(String name, String email, String role)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        String id = mDatabase.push().getKey();

        User newUser = new User(id, email, name, role, new HashMap<String, Boolean>());
        mDatabase.child(id).setValue(newUser);
        startMain(role, id);
    }

    private void startMain(String role, String userKey)
    {
        if (role.equals("teacher"))
        {
            Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("key", userKey);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("key", userKey);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Prevent users from turning to main activity by pressing back button
     */
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
