package com.example.mac.swinedu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity
{
    private Toolbar mToolBar;
    private EditText firstnameText;
    private EditText lastnameText;
    private EditText emailText;
    private EditText passwordText;
    private Button signupButton;
    private TextView loginNow;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        InitializeUI();
    }

    private void InitializeUI()
    {
        //Set up the action bar elements
        mToolBar = (Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(mToolBar);

        //Set up Firebase
        setupFirebase();

        //Initiate elements in the form
        InitiateElememts();
    }

    private void setupFirebase()
    {
        mAuth = FirebaseAuth.getInstance();
    }

    private void InitiateElememts()
    {
        firstnameText = (EditText) findViewById(R.id.firstname_editting);
        lastnameText = (EditText) findViewById(R.id.lastname_edittext);
        emailText = (EditText) findViewById(R.id.email_edittext_signup);
        passwordText = (EditText) findViewById(R.id.password_edittext_signup);
        loginNow = (TextView) findViewById(R.id.login_now);
        signupButton = (Button) findViewById(R.id.signup_button);

        //RegisterListener
        RegisterLoginNow();
        RegisterSignupButton();
    }

    /**
     * Handle the text view click. It starts the LoginActivity
     */
    private void RegisterLoginNow()
    {
        loginNow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Handle Signup button click. It signs up the account with Firebase authentication
     */
    private void RegisterSignupButton()
    {
        signupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (validForm())
                    signupWithFireBase(firstnameText.getText().toString(), lastnameText.getText().toString(),
                            emailText.getText().toString(), passwordText.getText().toString());
            }
        });
    }

    /**
     * Sign up an account in Firebase with provided names, email and password
     * @param firstname the user's first name from the form
     * @param lastname the user 's last name from the form
     * @param email the provided email address
     * @param password the password preferred
     */
    private void signupWithFireBase(final String firstname, final String lastname, final String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.w("User: ", user.getEmail());
                            Intent intent = new Intent(getApplicationContext(), PickRoleActivity.class);
                            intent.putExtra("name", firstname + " " + lastname);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Sign up unsuccessful. Your email might be taken from another user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * validate the form after the signup button is clicked
     * @return true if all fields are filled and the email follows the email pattern
     */
    private boolean validForm()
    {
        boolean result = true;

        String firstname = firstnameText.getText().toString();
        String lastname = lastnameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (firstname.isEmpty())
        {
            result = false;
            firstnameText.setError("You must fill this field");
        }

        if (lastname.isEmpty())
        {
            result = false;
            lastnameText.setError("You must fill this field");
        }

        if (password.isEmpty())
        {
            result = false;
            passwordText.setError("You must fill this field");
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            result = false;
            emailText.setError("You must enter correct email pattern");
        }

        return result;
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
