package com.rajkin2.cse491;

import java.util.GregorianCalendar;

public class IntegerFive{
    String destinationAirport;
    String timeDiff;
    GregorianCalendar slotTime;// in hour,minute,second 3 of them
    Time duration;
    int expense;
    public IntegerFive(String destinationAirport,String timeDiff,GregorianCalendar slotTime,Time duration,int expense){
        this.destinationAirport = destinationAirport;
        this.timeDiff = timeDiff;
        this.duration = duration;
        this.slotTime = slotTime;
        this.expense = expense;
    }
}
