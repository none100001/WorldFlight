package com.rajkin2.cse491;

import java.util.StringTokenizer;

public class Time{
    int hour,minute,second;
    int totalSecond;
    public Time(String s){
        s = s.replace("Hour"," ");s = s.replace("Minute"," ");s = s.replace("Second"," ");
        StringTokenizer ss = new StringTokenizer(s);
        this.hour = Integer.valueOf(ss.nextToken());
        this.minute = Integer.valueOf(ss.nextToken());
        this.second = Integer.valueOf(ss.nextToken());
        this.totalSecond = (this.second) + ((this.minute)*60) + ((this.hour)*3600); 
    }
}
