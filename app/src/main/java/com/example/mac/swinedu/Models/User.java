package com.example.mac.swinedu.Models;

import java.util.Map;

/**
 * Created by mac on 11/4/17.
 */

public class User
{
    private String email;
    private String name;
    private String role;
    private Map<String, Boolean> classes;
    private String id;

    public User()
    {

    }

    public User(String userId, String useremail, String username, String userrole, Map<String, Boolean> listclasses)
    {
        id = userId;
        email = useremail;
        name = username;
        role = userrole;
        classes = listclasses;
    }

    public String getEmail()
    {
        return email;
    }

    public String getName()
    {
        return name;
    }

    public String getRole()
    {
        return role;
    }

    public Map<String, Boolean> getClasses()
    {
        return classes;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "User{name='" + name + "'" + ",role='" + role + "',classes='" + classes + "',email='" + email +"'";
    }
}
