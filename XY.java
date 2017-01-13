package com.rajkin2.cse491;

import java.util.StringTokenizer;

public class XY{
    int x,y;
    public XY(String s){
        s = s.replace("(","");s = s.replace(")","");s = s.replace(","," ");
        StringTokenizer ss  =  new StringTokenizer(s);
        x = Integer.valueOf(ss.nextToken()); y = Integer.valueOf(ss.nextToken());
    }
}
