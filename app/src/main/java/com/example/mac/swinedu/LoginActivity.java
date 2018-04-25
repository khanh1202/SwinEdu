package com.example.mac.swinedu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.mac.swinedu.Models.User;
import com.example.mac.swinedu.studentactivities.StudentMainActivity;
import com.example.mac.swinedu.teacheractivities.TeacherMainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private Toolbar mToolBar;
    private FirebaseAuth mFirebaseAuth;
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;
    private TextView createOne;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitializeUI();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null)
        {
//            verifyingUserTask verify = new verifyingUserTask();
//            verify.execute(currentUser);
            checkUserAlreadyExists(currentUser);
//            mFirebaseAuth.signOut();
        }
    }

    public void InitializeUI()
    {
        //Set up the action bar elements
        mToolBar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolBar);

        //Set up google signin actions
        setupGoogleSignin();

        //Set up Firebase signin actions
        setupFirebase();

        InitiateElements();
        RegisterLoginButton();
    }

    /**
     * Setting up google API Client and Sign-in Option
     */
    public void setupGoogleSignin()
    {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /*Fragment Activity*/ , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        registerSigninButton();
    }

    /**
     * Handle Google sign-in button click
     */
    public void registerSigninButton()
    {
        SignInButton signin = (SignInButton) findViewById(R.id.sign_in_button);
        signin.setSize(SignInButton.SIZE_WIDE);
        signin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signIn();
            }
        });
    }

    /**
     * Set up before using Firebase for storing Google sign-in authentication
     */
    public void setupFirebase()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Initiate the edit text fields
     */
    public void InitiateElements()
    {
        emailText = (EditText) findViewById(R.id.email_edittext);
        passwordText = (EditText) findViewById(R.id.password_edittext);
        createOne = (TextView) findViewById(R.id.create_one);
        RegisterCreateOne();
    }

    /**
     * Handle Login button click
     */
    public void RegisterLoginButton()
    {
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (formValid())
                {
//                    signInNormal();
                    verifyingUserTask verify = new verifyingUserTask();
                    verify.execute();
                }
            }
        });
    }

    /**
     * Handle the "Create new account" click
     */
    public void RegisterCreateOne()
    {
        createOne.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void signInNormal()
    {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            checkUserAlreadyExists(mFirebaseAuth.getCurrentUser());
                        } else
                        {
                            Toast.makeText(getApplicationContext(), "Incorrect Input", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signIn()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 9001);
    }


    @Override
    public void onBackPressed()
    {
        //Disable going back to main activity
        moveTaskToBack(true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("SigninActivity", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSigninResult(result);
        }
    }

    public void handleSigninResult(GoogleSignInResult result)
    {
        Log.d("Signin", "handleSigninResult: " + result.isSuccess());
        if (result.isSuccess())
        {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        }
    }

    /**
     * Sign in to the application with Firebase
     * @param account GoogleSignInAccount object returned from the Google sign-in activity
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account)
    {
        Log.d("Sign in", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d("Signin", "signInWithCredential:sucess");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            checkUserAlreadyExists(user);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * check if the user logged by Google has already exists in the database
     * @param user the Firebase user that was successfully logged in
     */
    private void checkUserAlreadyExists(final FirebaseUser user)
    {
        mRef = FirebaseDatabase.getInstance().getReference("users");
        Log.w("current one:", user.getEmail());
        mRef.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean hasChild = dataSnapshot.getChildren().iterator().hasNext();

                if (hasChild)
                {
                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                    String firstChildKey = firstChild.getKey();
                    User newUser = firstChild.getValue(User.class);
                    startMain(newUser.getRole(), firstChildKey);
                }
                else
                    startPickRole(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    /**
     * Start Main Activity
     */
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
     * Start PickRole Activity
     * @param user the Firebase user that was successfully logged in
     */
    private void startPickRole(FirebaseUser user)
    {
        Intent intent = new Intent(getApplicationContext(), PickRoleActivity.class);
        intent.putExtra("name", user.getDisplayName());
        intent.putExtra("email", user.getEmail());
        startActivity(intent);
        finish();
    }

    private boolean formValid()
    {
        boolean result = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailText.setError("You must enter correct email pattern");
            result = false;
        }
        else
            emailText.setError(null);
        if (password.isEmpty())
        {
            passwordText.setError("You must fill the password");
            result = false;
        }
        else
            passwordText.setError(null);

        return result;
    }


    /**
     * Verifying is a long class which takes quite a long time. This class helps to show a
     * progress dialog while the system is verifying
     */
    private class verifyingUserTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog progress;

        public verifyingUserTask()
        {

        }
        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(LoginActivity.this);
            progress.setMessage("Verifying...");
            progress.setIndeterminate(true);
//            progress.setIndeterminateDrawable(getResources().getDrawable(R.drawable.spinner));
            progress.show();
            Log.w("Executing", "now");
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            signInNormal();
            return null;
        }

        @Override
        protected void onPostExecute(Void list)
        {
            progress.dismiss();
            Log.w("abcd", "congatrong");
        }
    }

}
