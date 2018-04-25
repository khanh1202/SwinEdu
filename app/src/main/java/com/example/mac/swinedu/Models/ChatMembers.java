package com.example.mac.swinedu.Models;

import java.util.HashMap;

/**
 * Created by mac on 11/7/17.
 */

public class ChatMembers
{
    private HashMap<String, Boolean> members = new HashMap<>();

    public ChatMembers()
    {

    }

    public HashMap<String, Boolean> getMembers()
    {
        return members;
    }

    public void addMember(String member)
    {
        members.put(member, true);
    }

    public void setMembers(HashMap<String, Boolean> members)
    {
        this.members = members;
    }
}
