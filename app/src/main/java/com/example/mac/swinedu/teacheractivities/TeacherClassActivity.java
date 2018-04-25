package com.example.mac.swinedu.teacheractivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mac.swinedu.Fragments.TeacherMembersFragment;
import com.example.mac.swinedu.Fragments.TeacherOverviewFragment;
import com.example.mac.swinedu.LoginActivity;
import com.example.mac.swinedu.R;
import com.google.firebase.auth.FirebaseAuth;

public class TeacherClassActivity extends AppCompatActivity
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String userKey;
    private String classKey;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getExtraPrevious();


    }

    /**
     * get the data passed from previous activity
     */
    private void getExtraPrevious()
    {
        Intent intent = getIntent();
        userKey = intent.getStringExtra("userkey");
        classKey = intent.getStringExtra("classkey");
        subjectName = intent.getStringExtra("subjectname");
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    TeacherOverviewFragment overviewFragment = new TeacherOverviewFragment();
                    Bundle args = new Bundle();
                    args.putString("classkey", classKey);
                    args.putString("subjectname", subjectName);
                    overviewFragment.setArguments(args);
                    return overviewFragment;
                case 1:
                    TeacherMembersFragment membersFragment1 = new TeacherMembersFragment();
                    Bundle args1 = new Bundle();
                    args1.putString("userkey", userKey);
                    args1.putString("classkey", classKey);
                    membersFragment1.setArguments(args1);
                    return membersFragment1;
                case 2:
                    TeacherMembersFragment membersFragment2 = new TeacherMembersFragment();
                    Bundle args2 = new Bundle();
                    args2.putString("userkey", userKey);
                    args2.putString("classkey", classKey);
                    membersFragment2.setArguments(args2);
                    return membersFragment2;


            }
            return null;
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Overview";
                case 1:
                    return "Members";
                case 2:
                    return "Assignments";

            }
            return null;
        }
    }
}
