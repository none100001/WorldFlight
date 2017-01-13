package com.rajkin2.cse491;

import java.util.StringTokenizer;

public class ConvertCalender{
	int year,month,date,hour,minute,second;
	public ConvertCalender(String information){
		information = information.replace("Year"," ") ;
		information = information.replace("Month"," ") ;
		information = information.replace("Day"," ") ;
		information = information.replace("Hour"," ") ;
		information = information.replace("Minute"," ") ;
		information = information.replace("Second"," ") ;
		StringTokenizer  s = new StringTokenizer(information);
		this.year = Integer.valueOf(s.nextToken());
		this.month = Integer.valueOf(s.nextToken());
		this.date = Integer.valueOf(s.nextToken());
		this.hour = Integer.valueOf(s.nextToken());
		this.minute = Integer.valueOf(s.nextToken());
		this.second = Integer.valueOf(s.nextToken());
	}
}
