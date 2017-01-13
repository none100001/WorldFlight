package com.rajkin2.cse491;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.StringTokenizer;


import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.rajkin2.cse491.R;

public class MainActivity extends Activity {
    Map map;
    String Source = "Blank";
    String Destination = "Blank";
    String Option = "Blank";
    ListView list1;
    ListView list2;                                                           
    ListView list3;
    String [] listArray;
    BufferedReader AirportLocation;
    BufferedReader AirportInformation;
    
    public GregorianCalendar realCalender;
    public static Integer Number_Of_Airport = 1168; //Total number of Airport in the World
    public HashMap<Integer,GregorianCalendar> Clock;   //The Clock of each Airport
    public HashMap<String,Integer> Airport;  //airport mapped to representative integer
    public HashMap<String,Coordinate> Coordinate; // airport mapped to their co-ordinates
    public HashMap<String,ArrayList<IntegerFive>> AirportInfo;  // airport mapped to their information
    public ArrayList<String> shortestPath; // the list of coordinates indicates the way to shortest path 
    public HashMap<Integer,String> AirportName; //index mapped to the Airport
    String [] listAirport; // holds the Airports name 
    int [] Parent;  // For getting the ShortestPath way Parent keep track the Child
    String [] List;// To described the detailed information
    
    
    @SuppressLint("UseSparseArrays")
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            AirportLocation = new BufferedReader(
                                                 new InputStreamReader(getAssets().open("Airport-Location.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            AirportInformation = new BufferedReader(
                                                 new InputStreamReader(getAssets().open("Airport-Information.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        listArray = new String[1168];
        AirportName = new HashMap<Integer,String>();
        Airport = new HashMap<String,Integer>();
        Coordinate = new HashMap<String,Coordinate>();
        AirportInfo = new HashMap<String,ArrayList<IntegerFive>>();
        shortestPath = new ArrayList<String>();
        Clock = new HashMap<Integer,GregorianCalendar>();
        listAirport = new String[Number_Of_Airport];
        Parent = new int[Number_Of_Airport];
        List = new String[Number_Of_Airport];
        String [] options = {"Shortest Path According to TIME","Shortest Path According to MONEY"};
        
        
        
        String line1;
        int c = 0;// location detector
        try{
            while((line1 = AirportLocation.readLine())!=null){
                StringTokenizer s = new StringTokenizer(line1);
                String airport = s.nextToken();
                listAirport[c] = airport;
                Airport.put(airport,c);
                XY a = new XY(s.nextToken()); // setting the coordinates in exact form
                int x = a.x;int y = a.y;
                AirportName.put(c,airport); 
                Coordinate.put(airport, new Coordinate(x,y));
                c++;// next Airport
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
        String line2;
        try{
            while((line2 = AirportInformation.readLine())!=null){
                String airportName = line2;
                ArrayList<IntegerFive> list = new ArrayList<IntegerFive>();
                for(int t = 0;t<3;t++){
                    StringTokenizer info = new StringTokenizer(AirportInformation.readLine());
                    String destinationAirport = info.nextToken();
                    String timeDiff = info.nextToken().replace("Minute", "");
                    ConvertCalender cal = new ConvertCalender(info.nextToken());
                    GregorianCalendar slotTime = new GregorianCalendar(cal.year,cal.month,cal.date,cal.hour,cal.minute,cal.second);
                    Time duration = new Time(info.nextToken());
                    int expense = Integer.valueOf(info.nextToken().replace("$",""));
                    list.add(new IntegerFive(destinationAirport,timeDiff,slotTime,duration,expense));
                }
                AirportInfo.put(airportName,list);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        Arrays.sort(listAirport);
        
        
        list1 = (ListView) findViewById(R.id.list1);
        list2 = (ListView) findViewById(R.id.list2);
        list3 = (ListView) findViewById(R.id.list3);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listAirport);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listAirport);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options);
        list1.setAdapter(adapter1);
        list2.setAdapter(adapter2);
        list3.setAdapter(adapter3);
        list1.setOnItemClickListener(new SourceSelection());
        list2.setOnItemClickListener(new DestinationSelection());
        list3.setOnItemClickListener(new OptionSelection());
        
    }
    
    public void shortestPathTime(){
        
        int [] shortestpath = new int[Number_Of_Airport];
        Arrays.fill(shortestpath,1000000000);
        PriorityQueue<Pair> q = new PriorityQueue<Pair>();
        q.add(new Pair(Source,0));
        shortestpath[Airport.get(Source)] = 0;
        
        long inMili = Long.MAX_VALUE;
        
        int uYear = (int)(inMili/3.15569e10);
        inMili -= (int)(inMili/3.15569e10);
        
        int uMonth = (int)(inMili/2.62974e9);
        inMili -= (int)(inMili/2.62974e9);
        
        int uDay = (int)(inMili/86400000);
        inMili -= (int)(inMili/86400000);
        
        int uHour = (int)(inMili/3600000);
        inMili -= (int)(inMili/3600000);
        
        int uMinute = (int)(inMili/60000);
        inMili -= (int)(inMili/60000);
        
        int uSecond = (int)(inMili/1000);
        long second = 0;
        
        GregorianCalendar starting = new GregorianCalendar();
        Clock.put(Airport.get(Source),starting);
        while(!q.isEmpty()){
            String u = q.poll().airport;
            if(!u.equals(Destination)){
                ArrayList<IntegerFive> listOfInfo = AirportInfo.get(u);
                for(int i = 0;i<listOfInfo.size();i++){
                    String airport = listOfInfo.get(i).destinationAirport;
                    int expense = listOfInfo.get(i).expense;
                    GregorianCalendar slotTime = listOfInfo.get(i).slotTime;
                    Time duration = listOfInfo.get(i).duration;
                    if(shortestpath[Airport.get(airport)]>shortestpath[Airport.get(u)]+expense){
                        Parent[Airport.get(airport)] = Airport.get(u);
                        slotTime.add(GregorianCalendar.HOUR, duration.hour);
                        slotTime.add(GregorianCalendar.MINUTE, duration.minute);
                        slotTime.add(GregorianCalendar.SECOND, duration.second);
                        Clock.put(Airport.get(airport),slotTime);
                        shortestpath[Airport.get(airport)] = shortestpath[Airport.get(u)]+expense;
                        q.add(new Pair(airport,shortestpath[Airport.get(airport)]));
                    }
                }
            }
            else{
                break;
            }
        }
    }

    
    
    
    
    
  
    
    public void shortestPathMoney(){
        
        int [] shortestpath = new int[Number_Of_Airport];
        Arrays.fill(shortestpath,1000000000);
        PriorityQueue<Pair> q = new PriorityQueue<Pair>();
        q.add(new Pair(Source,0));
        shortestpath[Airport.get(Source)] = 0;
        
        GregorianCalendar starting = new GregorianCalendar();
        Clock.put(Airport.get(Source),starting);
        while(!q.isEmpty()){
            String u = q.poll().airport;
            if(!u.equals(Destination)){
                ArrayList<IntegerFive> listOfInfo = AirportInfo.get(u);
                for(int i = 0;i<listOfInfo.size();i++){
                    String airport = listOfInfo.get(i).destinationAirport;
                    int expense = listOfInfo.get(i).expense;
                    GregorianCalendar slotTime = listOfInfo.get(i).slotTime;
                    Time duration = listOfInfo.get(i).duration;
                    if(shortestpath[Airport.get(airport)]>shortestpath[Airport.get(u)]+expense){
                        Parent[Airport.get(airport)] = Airport.get(u);
                        slotTime.add(GregorianCalendar.HOUR, duration.hour);
                        slotTime.add(GregorianCalendar.MINUTE, duration.minute);
                        slotTime.add(GregorianCalendar.SECOND, duration.second);
                        Clock.put(Airport.get(airport),slotTime);
                        shortestpath[Airport.get(airport)] = shortestpath[Airport.get(u)]+expense;
                        q.add(new Pair(airport,shortestpath[Airport.get(airport)]));
                    }
                }
            }
            else{
                break;
            }
        }       
    }

	public void getTheShortestPath(){
	    int start = Airport.get(Destination);
	    while(start!=Airport.get(Source)){
	        shortestPath.add(AirportName.get(start));
	        start = Parent[start];
	    }
	    shortestPath.add(AirportName.get(start));
	}
    
    
	public void Draw(){
        map = new Map(this);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(map);
        setContentView(scrollView); 
    } 
    
    
    public void AnotherFrame(){
    	String [] LIST = new String[shortestPath.size()];
    	for(int c = LIST.length-1;c>=0;c--){
    		String AirportName = shortestPath.get(c);
    		GregorianCalendar U = Clock.get(Airport.get(AirportName));
    		LIST[c] = "Place  "+AirportName+" Clock "+U.get(GregorianCalendar.YEAR)+":"+U.get(GregorianCalendar.MONTH)+":"+U.get(GregorianCalendar.DATE)+U.get(GregorianCalendar.HOUR)+":"+U.get(GregorianCalendar.MINUTE)+":"+U.get(GregorianCalendar.SECOND);
    	}
    	
    	List = new String[LIST.length];
    	int d = List.length-1;
    	for(int u = 0;u<List.length;u++){
    		List[u] = LIST[d--];
    	}
    }
    
    class SourceSelection implements AdapterView.OnItemClickListener{
        
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView temp = (TextView) arg1;
            Source = temp.getText()+"";
            if(!Source.equals("Blank") && !Destination.equals("Blank") && !Option.equals("Blank")){
            	if(Option.equals("Shortest Path According to TIME")){
            		shortestPathTime();
            	}
            	else{
            		shortestPathMoney();
            	}
            	getTheShortestPath();
            	AnotherFrame();
                Draw();
            }
        }
    }
    
    class OptionSelection implements AdapterView.OnItemClickListener{
        
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView temp = (TextView) arg1;
            Option = temp.getText()+"";
            
            if(Source.equals("Blank") && Destination.equals("Blank") && Option.equals("Blank")){
            	Toast.makeText(getApplicationContext(), "Please select source, destination AND Middle-option",
						Toast.LENGTH_LONG).show();
            }
            else if(Source.equals("Blank") && Destination.equals("Blank") && !Option.equals("Blank")){
            	Toast.makeText(getApplicationContext(), "Please select source AND destination",
						Toast.LENGTH_LONG).show();
            }
            else if(Source.equals("Blank") && !Destination.equals("Blank") && Option.equals("Blank")){
            	Toast.makeText(getApplicationContext(), "Please select source AND Middle-option",
						Toast.LENGTH_LONG).show();
            }
            else if(Source.equals("Blank") && !Destination.equals("Blank") && !Option.equals("Blank")){
            	Toast.makeText(getApplicationContext(), "Please select source",
						Toast.LENGTH_LONG).show();
            }
            else if(!Source.equals("Blank") && Destination.equals("Blank") && Option.equals("Blank")){
            	Toast.makeText(getApplicationContext(), "Please select destination AND Middle-option",
						Toast.LENGTH_LONG).show();
            }
            else if(!Source.equals("Blank") && Destination.equals("Blank") && !Option.equals("Blank")){
            	Toast.makeText(getApplicationContext(), "Please select destination",
						Toast.LENGTH_LONG).show();
            }
            else if(!Source.equals("Blank") && !Destination.equals("Blank") && Option.equals("Blank")){
            	Toast.makeText(getApplicationContext(), "Please select Middle-option",
						Toast.LENGTH_LONG).show();
            }
            else if(!Source.equals("Blank") && !Destination.equals("Blank") && !Option.equals("Blank")){
            	if(Option.equals("Shortest Path According to TIME")){
            		shortestPathTime();
            	}
            	else{
            		shortestPathMoney();
            	}
            	getTheShortestPath();
            	AnotherFrame();
                Draw();
            }
        }
    }
    
    class DestinationSelection implements AdapterView.OnItemClickListener{
        
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView temp = (TextView) arg1;
            Destination = temp.getText()+"";
            if(!Source.equals("Blank") && !Destination.equals("Blank") && !Option.equals("Blank")){
            	if(Option.equals("Shortest Path According to TIME")){
            		shortestPathTime();
            	}
            	else{
            		shortestPathMoney();
            	}
            	getTheShortestPath();
            	AnotherFrame();
                Draw();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    public class Map extends View {
        Paint paint = new Paint();
        public Map(Context context) {
            super(context);
        }
        @Override
        public void onDraw(Canvas canvas) {
        	paint.setColor(Color.RED);
        	int yy = 15;
        	for(int c = 0;c<List.length;c++){
        		canvas.drawText(List[c], 10, yy, paint);
        		yy+=30;
        	}
        		
            paint.setColor(Color.BLUE);
            for(int c = 0;c<listAirport.length;c++){
        		String AirportName = listAirport[c];
        		int x = Coordinate.get(AirportName).x;
        		int y = Coordinate.get(AirportName).y;
        		y += yy;
        		canvas.drawCircle(x+5, y, 8, paint);  		
        	}
            paint.setColor(Color.MAGENTA);
            for(int c = 0;c<shortestPath.size();c++){
        		int x = Coordinate.get(shortestPath.get(c)).x;
        		int y = Coordinate.get(shortestPath.get(c)).y;
        		y += yy;
        		canvas.drawCircle(x+5, y, 8, paint);  	
        	}
        	int c = 0;
        	while(c<shortestPath.size()-1){
        		int d = c+1;
        		String u = shortestPath.get(c);
        		String v = shortestPath.get(d);
        		int x1 = Coordinate.get(u).x;
        		int y1 = Coordinate.get(u).y;
        		int x2 = Coordinate.get(v).x;
        		int y2 = Coordinate.get(v).y;
        		paint.setColor(Color.RED);
        		canvas.drawText(u, x1+5, y1+yy-20, paint);
        		canvas.drawText(v, x2+5, y2+yy-20, paint);
        		paint.setColor(Color.GREEN);
                canvas.drawLine(x1+5, y1+yy, x2+5, y2+yy,paint);
                c++;
        	}
        	
        }
		@Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 17000;
            setMeasuredDimension(width, height);
        }
    }
}


















