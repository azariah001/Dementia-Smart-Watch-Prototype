package com.illusivemen.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.illusivemen.smartwatchadministrator.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class AdminGoogleMapping extends Activity {
	
	public final static String MAP_PURPOSE = "com.illusivemen.maps.EXTRAS_PAYLOAD_KEY";
	private String purpose;
	
	private GoogleMap googleMap;
	private static final float INITIAL_ZOOM = 11;
	private static final float LOCATED_ZOOM = 17;
	private static final LatLng BRISBANE = new LatLng(-27.5,153);
	private Marker patient;
	private Polyline track;
	private SimpleDateFormat mySQLFormat;
	private String positionTimestamp = null;
	private String connectionTimestamp = null;
	
	/**
     * Factory method to create a launch Intent for this activity.
     *
     * @param context the context that intent should be bound to
     * @param payload the payload data that should be added for this intent
     * @return a configured intent to launch this activity with a String payload.
     */
    public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, AdminGoogleMapping.class).putExtra(MAP_PURPOSE, payload);
    }
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_google_mapping);
		
		// What is the Function of this Map?
		purpose = getIntent().getStringExtra(MAP_PURPOSE);
		
		// Title by Function
		switch (purpose) {
		case "TrackPatients":
			this.setTitle("Patient's Location");
			break;
		}
		
		// used for parsing timestamps from mysql
		mySQLFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mySQLFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		// Create Map
		initilizeMap();
		// Location Updater
		subscribeForLocations();
	}
	
	/**
     * Load map. If map is not created it will create it for you.
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // set type of map to use
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // setup UI controls
            setupUi();
            
            // make sure map is created
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Unable to open map.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
        new RetrieveLocation().execute();
    }
    
    private void subscribeForLocations() {
		// retrieve patient location in loop
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new RetrieveUpdateTask(), 2000, 2000);
    }
    
    /**
     * Change the interface of google maps.
     */
    private void setupUi() {
    	// show location, explicitly allow advanced controls
    	googleMap.setMyLocationEnabled(true);
    	googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        // open map at Brisbane city by default
     	googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BRISBANE, INITIAL_ZOOM));
    }
    
    private void updateLocation(LatLng latlng, String positionAge) {
    	
    	if (patient != null) {
    		// update previous location
    		patient.setPosition(latlng);
    		patient.setSnippet(positionAge);
    		
    		// update information tooltip if open
    		if (patient.isInfoWindowShown()) {
    			patient.hideInfoWindow();
    			patient.showInfoWindow();
    		}
    	} else {
    		// add initial marker
    		patient = googleMap.addMarker(new MarkerOptions()
    				.position(latlng)
    				.title("Patient's Location"));
    		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, LOCATED_ZOOM));
    	}
    }
    
    /**
     * Only updates location age, not location. Used when location cannot be updated
     * due to network error.
     * @param positionAge how long ago the position has been updated.
     */
    private void updateLocation(String positionAge) {
    	if (patient != null) {
    		// Update age of previously retrieved location.
    		patient.setSnippet(positionAge);
    		
    		// update information tooltip if open
    		if (patient.isInfoWindowShown()) {
    			patient.hideInfoWindow();
    			patient.showInfoWindow();
    		}
    	}
    }
    
    private void updateTrack(String[] pointSet) {
    	List<LatLng> points = new ArrayList<LatLng>();
    	for (int point = 0; point < pointSet.length; point++) {
    		String[] latLng = pointSet[point].split(",");
    		points.add(new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1])));
    	}
    	
    	if (track != null) {
    		// update previous track
    		track.setPoints(points);
    	} else {
    		// add initial polyline
    		track = googleMap.addPolyline(new PolylineOptions()
    	     		.width(5)
    	     		.color(Color.RED));
    		track.setPoints(points);
    	}
    }
	
    /**
     * Returns how long ago previousTime was compared to now.
     * 
     * @param previousTime the time to determine the age of in format "YYYY-MM-DD HH:MM:SS"
     * @return human readable difference in time
     */
	@SuppressLint("SimpleDateFormat")
	private String getTimeAge(String previousTime) {
    	String age;
    	long difference = 0;
    	try {
			Date previousDate = mySQLFormat.parse(previousTime);
			Date currentDate = new Date();
			difference = currentDate.getTime()/1000 - previousDate.getTime()/1000;
		} catch (ParseException e) {
			System.out.println("Date Parse Error.");
		}
    	
    	// negative, clocks aren't synchronized
    	if (difference <= 0) {
    		age = "0 seconds";
    	} else if (difference < 60) {
    		// under a minute, show in seconds
    		age = String.valueOf(difference) + " second(s)";
    	} else if (difference < 60*60) {
    		// under an hour, show in minutes
    		age = String.valueOf(difference/60) + " minutes(s)";
    	} else if (difference < 24*60*60) {
    		// under a day, show in hours
    		age = String.valueOf(difference/60/60) + " hour(s)";
    	} else {
    		// more than a day, show in days
    		age = String.valueOf(difference/60/60/24) + " day(s)";
    	}
    	
    	return age;
    }
    
	/**
	 * TimerTask which updates patients' positions from each tick.
	 */
    class RetrieveUpdateTask extends TimerTask {
    	   public void run() {
    	       new RetrieveLocation().execute();
    	   }
    	}
    
    private class RetrieveLocation extends AsyncTask<Void, Void, String>{
    	
    	private static final String strUrl = "http://agile.azarel-howard.me/retrieveLastLocations.php";
    	
		@Override
		protected String doInBackground(Void... params) {
	    	URL url;
	    	BufferedReader reader = null;
	    	InputStream iStream = null;
			StringBuffer sb = new StringBuffer();
			try {
				url = new URL(strUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                iStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(iStream));
                String line = "";
                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }
  
            } catch (MalformedURLException e) {
            	// thrown by URL constructor
            	System.out.println("Patient Location Retrieve Internal Error");
            } catch (IOException e) {
            	System.out.println("Patient Location Retrieve Network Error");
            } finally {
            	try {
					reader.close();
				} catch (Exception e) {
					// reader was not opened
					System.out.println("Patient Location Retrieve Read Error");
				}
            	try {
					iStream.close();
				} catch (Exception e) {
					// iStream was not opened
					System.out.println("Patient Location Retrieve Connection Error");
				}
            }
            return sb.toString();
        }
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			// network errors may result in a null result
			// server errors may result in unexpected output
			try {
				// list of recent points
				String[] latestPoints = result.split(";");
				// first recent point is the latest position
				String[] location = latestPoints[0].split(",");
				// parsing
				LatLng position = new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
				positionTimestamp = location[2];
				connectionTimestamp = mySQLFormat.format(new Date());
				// processing
				updateLocation(position, "Updated " + getTimeAge(positionTimestamp) + " ago.");
				updateTrack(latestPoints);
			} catch (Exception e) {
				System.out.println("Patient Location Retrieve Parse Error");
				// only update age of the retrieved location
				if (positionTimestamp != null) {
					updateLocation("Location from " + getTimeAge(positionTimestamp)
							+ " ago. Connection failed for " + getTimeAge(connectionTimestamp) + ".");
				}
			}
		}
	}
}