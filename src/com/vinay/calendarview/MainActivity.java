package com.vinay.calendarview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends Activity implements OnDateChangeListener,OnCheckedChangeListener {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	private String mydata="";
	private SharedPreferences mypreferences;
	private SharedPreferences.Editor prefseditor;
	private static final String TAG = "MainActivity";
	StringBuilder mybuilder = new StringBuilder();
	List<String> plist = new ArrayList<String>();
	HashMap<String, String> map = new HashMap<String, String>(); //create a hasmap here
	
	
	Boolean cb1=false,cb2=false,cb3=false,cb4=false,cb5=false;
	ListView list;
	TextView tv;
	Intent intent;
	
	int num=0;
	
	//creates on app startup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        intent = new Intent(this, myService.class);
        startService(intent);
//        Toast.makeText(MainActivity.this,"activity: starting service",Toast.LENGTH_SHORT).show();
    
        //gets the hashmap content from file
        deserial();
 
        ImageView imgView = (ImageView)findViewById(R.id.imageView1);
        imgView.setVisibility(View.VISIBLE);
        
        TextView tv=(TextView)findViewById(R.id.textView2);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date date = new Date();
	    String today= dateFormat.format(date);
	    String newdate=mydateformat(date);
        tv.setText(newdate + "\n" + "Today's Menu: \t" +map.get(today));
//        Toast.makeText(getApplicationContext(), map.toString(), Toast.LENGTH_LONG).show();	
        CalendarView cal=(CalendarView)findViewById(R.id.mycalendar);
   	
        cal.setOnDateChangeListener(this);
 
        mypreferences = getSharedPreferences("com.vinay.calendarview", MODE_PRIVATE);
        prefseditor = mypreferences.edit();
        
 //       Toast.makeText(getApplicationContext(), plist.toString(), Toast.LENGTH_LONG).show();
        String todaykey=map.get(today);
 //       Toast.makeText(getApplicationContext(), todaykey, Toast.LENGTH_SHORT).show();
        
        //check if matches preferences        
        List<String> str=getSavedItems();
 //       Toast.makeText(getApplicationContext(), str.toString(), Toast.LENGTH_LONG).show();                       
        
        ArrayList<HashMap<String, String>> newlist = new ArrayList<HashMap<String,String>>();
        
    	newlist.add(map);
     
    	
    	for (Map.Entry<String,String> entry : map.entrySet()) {
    		  String key = entry.getKey();
    		  String value = entry.getValue();
    		  map.keySet().toArray();

    		}
                               
       
    }


    //matches with the saved preferences and returns the status 
public boolean matchprefs(List list, String str )
{
	Boolean status=false;
	List<String> mylist=getSavedItems();
	ImageView imgView = (ImageView)findViewById(R.id.imageView1);
	imgView .setVisibility(View.INVISIBLE);
//	Toast.makeText(MainActivity.this,list.toString(),Toast.LENGTH_SHORT).show();
	for(int i=0;i<list.size();i++)
	{
//		Toast.makeText(MainActivity.this,list.get(i).toString(),Toast.LENGTH_SHORT).show();
		if(str.contains(list.get(i).toString()))
		{
//			Toast.makeText(MainActivity.this,"exists",Toast.LENGTH_SHORT).show();
	        
        	imgView .setVisibility(View.VISIBLE);
		}
	}

	return status;
	
}


// when a date from the calendar is selected 
@Override
public void onSelectedDayChange(CalendarView view, int year, int month,int dayOfMonth)
	{
    	num=1;
	    String date= year + "-" + (month+1) + "-" + dayOfMonth; 
	    getMonth(dayOfMonth,month,year);		// convert date format

	    String newdate=getMonth(dayOfMonth,month,year);

	    
	    String myformat = myformat(date);
	    TextView tv=(TextView)findViewById(R.id.textView2);
	    tv.setText(newdate + "\n"+ "Today's Menu: " + map.get(myformat));
//	    Toast.makeText(getApplicationContext(),plist.toString(), Toast.LENGTH_SHORT).show();
	    matchprefs(plist,map.get(myformat));
   
	}

// convert calendar date to match hashmap key entries
private String myformat(String date) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	String ndate="";
	
	try {
		Date newdate = dateFormat.parse(date);
		
		ndate = dateFormat.format(newdate);
		return ndate;
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return ndate;
}


//converts month number to name
public String getMonth(int day , int month, int year) {
    String d= new DateFormatSymbols().getMonths()[month];
 //   Toast.makeText(getApplicationContext(), day + " "+ d + " "+ year, Toast.LENGTH_LONG).show();
    return(day + " "+ d + " "+ year);
    
}

//my custom date format
public String mydateformat(Date date)
{

	SimpleDateFormat myformat = new SimpleDateFormat("dd MMMM yyyy");
	String mydate= myformat.format(date);
	
	return mydate;
}

//saves the state of the app even when recreating the views
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
}

//restores the state of the app
@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
}



//menu options

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	
	switch (item.getItemId())
	{
	case R.id.Information:info();
	break;
	case R.id.preferences:preferences();
	break;
	case R.id.about:aboutus();
	break;
	case R.id.update:update();
	break;
	case R.id.eventlist:menulist();
	}
	return false;
}



//sets a msg on screen orientation change

@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    // Checks the orientation of the screen for landscape and portrait
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
    } 
}

//dialog for general info
public void info()
{
	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//	dialog.setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener);
	dialog.setTitle(Html.fromHtml("<b><font color='#FF7F27' size='30'> Quick Info</font></b>"));
	dialog.setMessage(Html.fromHtml("<font color='#708090'><b>This is a Lunch Menu Calendar App for <br> TROY SCHOOL DISTRICT ELEMENTARY <br><br> Click on the dates in the calendar for the corresponding Lunch Menu List</b>"));
	dialog.setPositiveButton(Html.fromHtml("<b>BACK</b>"), null);
	dialog.setCancelable(false);

	dialog.show();
}


// List view of all the Menu items for all the dates - pulled from the hashmap
public void menulist() {

    ArrayList<HashMap<String, String>> newlist = new ArrayList<HashMap<String,String>>();
    
	newlist.add(map);
	

    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(Html.fromHtml("<b><font color='#FF7F27' size='10'> Menu List </font></b>"));
    dialog.setPositiveButton(Html.fromHtml("<b>OK</b>"), null);
    ListView modeList = new ListView(this);
    ArrayAdapter<HashMap<String, String>>modeAdapter = new ArrayAdapter<HashMap<String, String>>(this,R.layout.mylist,newlist);
    modeList.setAdapter(modeAdapter);
    dialog.setView(modeList);
    final Dialog d = dialog.create();
    d.show();
      
}

//about our info - dialogue box
public void aboutus()
{
	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	dialog.setTitle(Html.fromHtml("<b><font color='#FF7F27' size='30'> About Me </font></b>"));
	dialog.setMessage(Html.fromHtml("<font color='#708090'><b><br> Vinay Potluri <br> vinaypotluri8@gmail.com<br>http://www.about.me/vinaypotluri<br></b>"));
	dialog.setPositiveButton(Html.fromHtml("<b>HiRE Me</b>"), null);
	dialog.setCancelable(false);
	dialog.create().show();
}


// manual update by the user
public void update()
{
	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	dialog.setTitle(Html.fromHtml("<b><font color='#FF7F27' size='30'> Update Lunch Menu </font></b>"));
	dialog.setMessage(Html.fromHtml("<font color='#708090'><b>The application updates automatically when a network connection is available. <br> Click on 'update' button to manually update content </b>"));
	dialog.setPositiveButton(Html.fromHtml("<b>OK</b>"), null);
	dialog.setNegativeButton(Html.fromHtml("<b>Update</b>"), new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			
			boolean status;
			status = isNetworkAvailable();
			if(status)
			{
				Toast.makeText(getApplicationContext(), "updating", Toast.LENGTH_LONG).show();
				manualupdate();   // fetches Json only when network in available 

			}
			else
			{
				Toast.makeText(getApplicationContext(), "network unavailable \n please try again later", Toast.LENGTH_SHORT).show();

			}
			
		}});
	
	dialog.setCancelable(false);
	dialog.create().show();
}

//to check network status
public boolean isNetworkAvailable() {
    // Get Connectivity Manager class object from Systems Service
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
 
    // Get Network Info from connectivity Manager
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
 
    // if no network is available networkInfo will be null
    // otherwise check if we are connected
    if (networkInfo != null && networkInfo.isConnected()) {
        return true;
    }
    return false;
}

// function to invoke on manual update by the user
public void manualupdate() {
	fetchTask task = new fetchTask();
	task.execute();	
}


// preferences or favorities menu
public void preferences()
{
	
	 View checkBoxView = View.inflate(this, R.layout.checkbox, null);
	 AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	 dialog.setView(checkBoxView);
	 dialog.setTitle(Html.fromHtml("<b>"+"<font color='#FF7F27' size='30'>"+"Personal Preferences"+"</font>"+"</b>"));

	 dialog.setPositiveButton(Html.fromHtml("<b>SAVE</b>"), null);	
	 dialog.setCancelable(false);
	
	 CheckBox checkBox1 = (CheckBox) checkBoxView.findViewById(R.id.checkBox1);
	 CheckBox checkBox2 = (CheckBox) checkBoxView.findViewById(R.id.checkBox2);
	 CheckBox checkBox3 = (CheckBox) checkBoxView.findViewById(R.id.checkBox3);
	 CheckBox checkBox4 = (CheckBox) checkBoxView.findViewById(R.id.checkBox4);
	 CheckBox checkBox5 = (CheckBox) checkBoxView.findViewById(R.id.checkBox5);
	 checkBox1.setChecked(loadSavedPreferences("cbox1"));
 	 checkBox2.setChecked(loadSavedPreferences("cbox2"));
 	 checkBox3.setChecked(loadSavedPreferences("cbox3"));
 	 checkBox4.setChecked(loadSavedPreferences("cbox4"));
	 checkBox5.setChecked(loadSavedPreferences("cbox5"));
	 checkBox1.setOnCheckedChangeListener(this);
	 checkBox2.setOnCheckedChangeListener(this);
	 checkBox3.setOnCheckedChangeListener(this);
	 checkBox4.setOnCheckedChangeListener(this);
	 checkBox5.setOnCheckedChangeListener(this);
 
	 dialog.show();
}


//gets the saved preferences
private Boolean loadSavedPreferences(String key)
{
	mypreferences = getApplicationContext().getSharedPreferences(PREFS_NAME,  android.content.Context.MODE_PRIVATE);
    return mypreferences.getBoolean(key, false);  
}

//saves the user preferences
private void savePreferences(String key, boolean value)
{
	mypreferences = getApplicationContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
	prefseditor = mypreferences.edit();
	prefseditor.putBoolean(key, value);
	prefseditor.commit();
}

//on checkbox checked the values are set
@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
{	
	switch(buttonView.getId())
	{
		case R.id.checkBox1:savePreferences("cbox1",isChecked);		
	    break;
		case R.id.checkBox2:savePreferences("cbox2",isChecked);		
	    break;
		case R.id.checkBox3:savePreferences("cbox3",isChecked);		
	    break;
		case R.id.checkBox4:savePreferences("cbox4",isChecked);		
	    break;
		case R.id.checkBox5:savePreferences("cbox5",isChecked);	
	    break;
	}	
}


// to get the saved user preferences 
private List<String> getSavedItems()
	{
	String savedItems = "";
	StringBuilder builder = new StringBuilder();
	
	cb1=loadSavedPreferences("cbox1");
	cb2=loadSavedPreferences("cbox2");
	cb3=loadSavedPreferences("cbox3");
	cb4=loadSavedPreferences("cbox4");
	cb5=loadSavedPreferences("cbox5");
	
	if(cb1==true)
	{
		builder.append("cb1");
		mybuilder.append("apple");
		plist.add("apple");
	}
	if(cb2==true)
	{
		builder.append("cb2");
		mybuilder.append("orange");
		plist.add("orange");
	}
	if(cb3==true)
	{
		builder.append("cb3");
		mybuilder.append("banana");
		plist.add("banana");
	}
	if(cb4==true)
	{
		builder.append("cb4");
		mybuilder.append("grapes");
		plist.add("grapes");
	}
	if(cb5==true)
	{
		builder.append("cb5");
		mybuilder.append("carrot");
		plist.add("carrot");
	}
	
	return plist;
}


// deserializes the hashmap stored in a file by the service class
public void deserial()
{
//	Toast.makeText(getApplicationContext(), "deserilize here", Toast.LENGTH_LONG).show();
	File file = new File(getDir("data", MODE_PRIVATE), "map"); 
	ObjectInputStream inputStream;
	
	try {
		inputStream = new ObjectInputStream(new FileInputStream(file));
		map = (HashMap<String, String>) inputStream.readObject();
		inputStream.close();
		
		
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date date = new Date();
		    String today= dateFormat.format(date);
		    
//		    Toast.makeText(getApplicationContext(), "today's menu "+today + "\n" + map.get(today), Toast.LENGTH_LONG).show();
		    
//		Toast.makeText(getApplicationContext(), map.toString(), Toast.LENGTH_SHORT).show();	
		
	} catch (StreamCorruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

@Override
	public void onResume()
	{
		super.onResume();
	}

@Override
	public void onPause()
	{
		super.onPause();
	}


//asynctask running off the main thread
private class fetchTask extends AsyncTask<String, String, String>

{

	protected void onPreExecute() {
        super.onPreExecute();
        
        
	}
	
	// runs in the background without interrupting the main thread
	@Override
	protected String doInBackground(String... params) {

		String link="http://www.google.com/calendar/feeds/lunchmenu123@gmail.com/public/full?alt=json";	
		try {
			//set connection
			 URL url = new URL(link);
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			 conn.setReadTimeout(1500);
	         conn.setConnectTimeout(1000);
	         conn.setRequestMethod("GET");
	         conn.setDoInput(true);
	         conn.connect();
			 
	         //get the input stream
	         InputStream stream = conn.getInputStream();
	         String data = convertStreamToString(stream);
	         stream.close();         

	         publishProgress(data);
	         
	         
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... text)
	{
		
		 mydata=text[0];
	
//		 Toast.makeText(getApplicationContext(), mydata, Toast.LENGTH_SHORT).show();
	}
	
	//on completion of asynchronous background service 
	protected void onPostExecute(String result) {
		
		//create a file and save Json content
		 try {
			 
				FileOutputStream fos = openFileOutput("text.txt",MODE_WORLD_WRITEABLE);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				osw.write(mydata);
				osw.flush();
				osw.close();
				
//				Toast.makeText(getApplicationContext(), "created a text file", Toast.LENGTH_SHORT).show();
				
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 

	}
	
}

//converts the input stream to string
public String convertStreamToString(java.io.InputStream is) {
	 java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
}

}
