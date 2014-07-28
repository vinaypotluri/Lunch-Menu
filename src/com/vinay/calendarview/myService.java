package com.vinay.calendarview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.Toast;

public class myService extends Service {


	private String mydata="";
	   private String TAG_feed = "feed";
	   private String TAG_entry = "entry";
	   private String TAG_title = "title";
	   private String TAG_gd$when = "gd$when";
	   private String t = "$t";
	   private String starttime = "startTime";
	   private String update = "updated";
	   private String urlString = null;
	   
	   HashMap<String, String> map = new HashMap<String, String>();		//hashmap
	   public volatile boolean parsingComplete = true;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
//		Toast.makeText(getApplicationContext(), "service: on bind", Toast.LENGTH_SHORT).show();
		return null;
	}
	
	@Override
    public void onCreate() {
        super.onCreate();

//        Toast.makeText(getApplicationContext(), "service: on create", Toast.LENGTH_SHORT).show();
    }
	
	
	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		
//		Toast.makeText(getApplicationContext(), "service: on start", Toast.LENGTH_SHORT).show();
				
		
		boolean status;
		status = isNetworkAvailable();
		if(status)
		{
//			Toast.makeText(getApplicationContext(), "network available", Toast.LENGTH_SHORT).show();
			fetchJson();   // fetches Json only when network in available 

		}
		else
		{
//			Toast.makeText(getApplicationContext(), "network unavailable", Toast.LENGTH_SHORT).show();

		}
		
		//runs the parJson even if there is no network
		 parseJson();
		
		//start sticky so that it runs even on startup
		return Service.START_STICKY;
	}
	
	
	
	public void fetchJson() {
		//asynctask to fetchJson data using asyncTask
    	fetchTask task = new fetchTask();
    	task.execute();

	}
	
	
	
	//async fetch task
private class fetchTask extends AsyncTask<String, String, String>
    
    {
	
	protected void onPreExecute() {
        super.onPreExecute();
        
        
	}
	

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
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
		
			
//		 Toast.makeText(getApplicationContext(), "on progress: working", Toast.LENGTH_SHORT).show();	
//		 tv.setText(text[0]);
		 mydata=text[0];
	
//		 Toast.makeText(getApplicationContext(), mydata, Toast.LENGTH_SHORT).show();
	}
	
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

//function to parseJon and store hashmap
private String parseJson() {

//	Toast.makeText(getApplicationContext(), "accessing the text file", Toast.LENGTH_SHORT).show();

	char[] inputBuffer = new char[10000000];
	String dataToRead = "";
	int charRead;
	String str="";
	FileInputStream fis;
	
	try { //open the text file here
		fis = openFileInput("text.txt");
//		 Toast.makeText(getApplicationContext(), "found text file", Toast.LENGTH_SHORT).show();	
		InputStreamReader isr = new InputStreamReader(fis);

		
		//read Json contents of the file and store inside dataToRead
		while ((charRead = isr.read(inputBuffer)) > 0)
			{
				String readString = String.copyValueOf(inputBuffer, 0,charRead);
				dataToRead += readString;
//				dataToRead="0";
				inputBuffer = new char[10000000];
			}

		parse(dataToRead);
		
		
	}
	 catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		 Toast.makeText(getApplicationContext(), "unable to open / file not found", Toast.LENGTH_SHORT).show();			
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "IO exception", Toast.LENGTH_SHORT).show();	
		}
	
	return null;
}


public String parse(String Jsondata)
{
	
	String start="";
	//this part to parse the data
    
		try {
			
			JSONObject object = new JSONObject(Jsondata);
			JSONObject feed = object.getJSONObject(TAG_feed);
					
//			JSONObject a = feed.getJSONObject(update);
//		    update =  a.getString(t);
		    JSONArray entry = feed.getJSONArray(TAG_entry);
		    
		    for(int j=0; j<entry.length();j++)
 			{
			    
			    JSONObject c = entry.getJSONObject(j);
			    JSONObject title = c.getJSONObject(TAG_title);
			    t = title.getString("$t");  //menu list
			    
			    JSONArray gd$when = c.getJSONArray(TAG_gd$when);
			    
			    for(int k=0;k<gd$when.length();k++)
			    {
			    JSONObject b = gd$when.getJSONObject(k);
			    start = b.getString(starttime);
			    }
			    
			    map.put(start,t);
			    
 			}   
		    
		    // method serialize hashmap 		    
		    serializeFunc();

		    pushNotifications();
	    
//		    Toast.makeText(getApplicationContext(), map.get(2014-07-13), Toast.LENGTH_LONG).show();

		    

		    
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;


}



private void pushNotifications() {
	// get current date + get current day's menu + get saved preferences + compare if string exists + push notification
	
//	Toast.makeText(getApplicationContext(), "push notifications here", Toast.LENGTH_SHORT).show();
	
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    String today= dateFormat.format(date);
    
    String newdate=mydateformat(date);
    String str=(newdate + "\n" + "Your Favorite is available today: \t" +map.get(today));
    
 //   Boolean status=matchprefs(list,map.get(today));


    //creates a notification here
	 Intent intent = new Intent();
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		Notification noti = new Notification.Builder(this)
		.setTicker("Ticker Title")
		.setContentTitle("Content Title")
		.setContentText(str)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent).getNotification();
		noti.flags=Notification.FLAG_AUTO_CANCEL;
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, noti); 

	
}
// my custom date format
public String mydateformat(Date date)
{

	SimpleDateFormat myformat = new SimpleDateFormat("dd MMMM yyyy");
	String mydate= myformat.format(date);
	
	return mydate;
}

//convert calendar date to match hashmap
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

//function to serialize hashmap object
private void serializeFunc() {

	File file = new File(getDir("data", MODE_PRIVATE), "map");
	
	ObjectOutputStream outputStream;
	try {
		outputStream = new ObjectOutputStream(new FileOutputStream(file));
    	outputStream.writeObject(map);
    	outputStream.flush();
    	outputStream.close();
    	
 //   	Toast.makeText(getApplicationContext(), "Serialization success", Toast.LENGTH_SHORT).show();	
    	
	} catch (FileNotFoundException e) {
		
		e.printStackTrace();
	} catch (IOException e) {
		
		e.printStackTrace();
	}
	

}

	
//function to check network connectivity
	
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

	
	
	// function to convert input stream to string
	public String convertStreamToString(java.io.InputStream is) {
		 java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	     return s.hasNext() ? s.next() : "";
	}

}
