package com.example.mac.swinedu.Fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.swinedu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherOverviewFragment extends Fragment
{
    private String subjectName;
    private String classKey;
    private TextView welcomeText;
    private TextView classKeyText;
    private View thisView;

    public TeacherOverviewFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        thisView = inflater.inflate(R.layout.fragment_teacher_overview, container, false);
        // Inflate the layout for this fragment
        getDataFromPrevious();
        initiateElements();
        changeTextViews();

        return thisView;
    }

    /**
     * Get data passed from the activity
     */
    private void getDataFromPrevious()
    {
        subjectName = getArguments().getString("subjectname");
        classKey = getArguments().getString("classkey");
    }

    /**
     * Initiate the text views
     */
    private void initiateElements()
    {
        welcomeText = (TextView) thisView.findViewById(R.id.welcome_textview);
        classKeyText = (TextView) thisView.findViewById(R.id.classname_textview);
    }

    /**
     * Set the text of the text views
     */
    private void changeTextViews()
    {
        String welcome = "Welcome to " + subjectName;
        welcomeText.setText(welcome);
        classKeyText.setText(classKey);

        //register click listener to the code text view
        classKeyText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("classcode", classKey);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Code added to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
