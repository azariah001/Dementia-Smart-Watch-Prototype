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
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.location.Geofence;
import com.illusivemen.db.DBConn;
import com.illusivemen.smartwatchadministrator.R;
import com.illusivemen.smartwatchadministrator.R.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class AdminGoogleMapping extends Activity implements OnMapLongClickListener {
	
	public final static String MAP_PURPOSE = "com.illusivemen.maps.EXTRAS_PAYLOAD_KEY";
	private String purpose;
	
	private Menu menu;
	private GoogleMap googleMap;
	private SimpleGeofence testFence;
	private List<Geofence> geofenceList;
	private static final float INITIAL_ZOOM = 11;
	private static final float LOCATED_ZOOM = 17;
	private static final LatLng BRISBANE = new LatLng(-27.5,153);
	private static final long GEOFENCE_EXPIRATION_TIME = 0;
	private Marker patient;
	private Polyline track;
	private SimpleDateFormat mySQLFormat;
	private String positionTimestamp = null;
	private String connectionTimestamp = null;
	// database retrieval related
	private static final String DB_LOCATIONS = "http://agile.azarel-howard.me/retrieveLastLocations.php";
	private DBConn conn;
	
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
		
		// local geofence store
		geofenceList = new ArrayList<Geofence>();
		
		// database retrieval class
		conn = new DBConn(DB_LOCATIONS);
		
		// Create Map
		initilizeMap();
		// Location Updater
		subscribeForLocations();
	}
	
	/**
	 * Menu Creation
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.admin_google_mapping, this.menu);
		return true;
	}
	
	/**
	 * Menu Selection Handling
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.map_map:
	        	setMenuMapType(item);
	            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	            return true;
	        case R.id.map_satellite:
	        	setMenuMapType(item);
	            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	            return true;
	        case R.id.map_hybrid:
	        	setMenuMapType(item);
	        	googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	        	return true;
	        case R.id.map_terrain:
	        	setMenuMapType(item);
	        	googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void setMenuMapType(MenuItem selection) {
		// first, un-check all options before selecting a new one
		menu.findItem(R.id.map_map).setChecked(false);
		menu.findItem(R.id.map_satellite).setChecked(false);
		menu.findItem(R.id.map_hybrid).setChecked(false);
		menu.findItem(R.id.map_terrain).setChecked(false);
		
		// check the selected option
		selection.setChecked(true);
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
            // act out on long clicks
            googleMap.setOnMapLongClickListener(this);
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
        //initilizeMap();
        //subscribeForLocations();
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
    	
		@Override
		protected String doInBackground(Void... params) {
			conn.execute();
			System.out.println("Received Location");
			return conn.getResult();
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

    /**
     * This method runs when the map is pressed for a longer time.
     * @param point The point where the user pressed.
     */
	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		System.out.println(point.latitude + ", " + point.longitude);
		setGeofence(point);
		
	}
	
	public void setGeofence(LatLng point) {
		testFence = new SimpleGeofence(
                "1",
                Double.valueOf(point.latitude),
                Double.valueOf(point.longitude),
                Float.valueOf("25"),
                Geofence.NEVER_EXPIRE,
                // This geofence records only entry transitions
                // to have enter/exit try adding?
                Geofence.GEOFENCE_TRANSITION_ENTER);

		geofenceList.add(testFence.toGeofence());
		// display visually
		addMarkerForFence(testFence);

	}
	
	public void addMarkerForFence(SimpleGeofence fence){
		if(fence == null){
		    // display an error message and return
		   return;
		}
		
		// TODO: modify geofence when you click on the marker details
		googleMap.addMarker( new MarkerOptions()
		  .position( new LatLng(fence.getLatitude(), fence.getLongitude()) )
		  .title("Fence " + fence.getId())
		  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
		  .snippet("Radius: " + fence.getRadius()) ).showInfoWindow();
		 
		// Instantiates a new CircleOptions object + center/radius
		CircleOptions circleOptions = new CircleOptions()
		  .center( new LatLng(fence.getLatitude(), fence.getLongitude()) )
		  .radius( fence.getRadius() )
		  // AARRGGBB
		  .fillColor(0x40ff0000)
		  .strokeColor(Color.TRANSPARENT)
		  .strokeWidth(2);
		 
		// Get back the mutable Circle
		Circle circle = googleMap.addCircle(circleOptions);
		// more operations on the circle...
		 
	}
	
	
	// TODO: this code should be on the client and should modify the database which then gets retrieved here
	/*
     * Create a PendingIntent that triggers an IntentService
     * when a geofence transition occurs.
     */
    private PendingIntent getTransitionPendingIntent() {
        // Create an explicit Intent
        Intent intent = new Intent(this,
                ReceiveTransitionsIntentService.class);
        /*
         * Return the PendingIntent
         */
        return PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}