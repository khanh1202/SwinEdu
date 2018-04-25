package com.example.mac.swinedu.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 11/5/17.
 */

public class Classroom
{
    private String subject;
    private int numofmembers;
    private String teacher;
    private HashMap<String, Boolean> students = new HashMap<>();
    private String id;

    public Classroom()
    {

    }

    public Classroom(String id, String subject, String teacher)
    {
        this.id = id;
        this.subject = subject;
        this.numofmembers = 1;
        this.teacher = teacher;
        this.students = new HashMap<>();
    }

    public String getSubject()
    {
        return subject;
    }

    public int getNumofmembers()
    {
        return numofmembers;
    }

    public String getTeacher()
    {
        return teacher;
    }

    public Map<String, Boolean> getStudents()
    {
        return students;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        String now = "{id=" + id + ",subject=" + subject + ",num=" + numofmembers + ",teacher=" + teacher + ",students=";
        for (boolean value: students.values())
        {
            now += value + "}";
        }
        return now;
    }
}
