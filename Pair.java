package com.rajkin2.cse491;

public class Pair implements Comparable{
    String airport;
    long dollar;
    public Pair(String airport,long dollar){
        this.airport =  airport;
        this.dollar = dollar;
    }
    
    public int compareTo(Object o) {
        return (int)(this.dollar - ((Pair)o).dollar);
    }
    
}
