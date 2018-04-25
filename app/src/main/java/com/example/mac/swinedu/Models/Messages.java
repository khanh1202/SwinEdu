package com.example.mac.swinedu.Models;

/**
 * Created by mac on 11/7/17.
 */

public class Messages
{
    private String message;
    private long time;
    private String name;

    public Messages()
    {

    }

    public Messages(String message, long time, String name)
    {
        this.message = message;
        this.time = time;
        this.name = name;
    }

    public String getMessage()
    {
        return message;
    }

    public long getTime()
    {
        return time;
    }

    public String getName()
    {
        return name;
    }
}
