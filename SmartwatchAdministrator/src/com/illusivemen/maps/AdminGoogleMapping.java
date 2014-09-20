package com.illusivemen.maps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.location.Geofence;
import com.illusivemen.db.DBConn;
import com.illusivemen.db.OnLoopRetrievedListener;
import com.illusivemen.db.RetrieveLoopThread;
import com.illusivemen.smartwatchadministrator.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class AdminGoogleMapping extends Activity implements OnMapLongClickListener, OnLoopRetrievedListener {
	
	public final static String MAP_PURPOSE = "com.illusivemen.maps.EXTRAS_PAYLOAD_KEY";
	private String purpose;
	
	// options menu
	private Menu menu;
	// the map fragment and its constants
	private GoogleMap googleMap;
	private static final float INITIAL_ZOOM = 11;
	private static final float LOCATED_ZOOM = 18.8f;
	// initial map location
	private static final LatLng BRISBANE = new LatLng(-27.5,153);
	// the patient's location and history
	private Marker patient;
	private Polyline track;
	private final static int UPDATE_PERIOD = 2000;
	// database time format
	private SimpleDateFormat mySQLFormat;
	// last retrieved time and db connection time
	private String positionTimestamp = null;
	private String connectionTimestamp = null;
	// local geofence storage and default properties
	ArrayList<GeofenceVisualisation> geofences = new ArrayList<GeofenceVisualisation>();
	private static final int GEOFENCE_RADIUS = 30;
	
	
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
	
	/**
	 * Setup of the activity.
	 */
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
		// load geofence visualisations
		new RetrieveGeofences().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
	
	/**
	 * Makes sure only selection is checked in the map type menu.
	 * @param selection the currently selected map display type
	 */
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
	 * Load map. Create it if it doesn't exist.
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
	}
	
	private void subscribeForLocations() {
		// retrieve patient location in loop
		RetrieveLoopThread retrieveThread = new RetrieveLoopThread("/retrieveLastLocations.php", new String[]{"patient=0"}, UPDATE_PERIOD);
		retrieveThread.setListener(this);
		retrieveThread.start();
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
	
	/**
	 * Change the location of the patient marker.
	 * @param latlng the new position
	 * @param positionAge how long ago the data was retrieved/stored
	 */
	private void updateLocation(LatLng latlng, String positionAge) {
		
		if (patient != null) {
			// update previous location
			patient.setPosition(latlng);
			patient.setSnippet(positionAge);
			
			updatePatientLocationTooltip();
		} else {
			// add initial marker
			patient = googleMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title("Patient's Location")
					.snippet(positionAge));
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
			
			updatePatientLocationTooltip();
		}
	}
	
	/**
	 * Updates the information tooltip for the patient location marker.
	 * If the marker is out of the visible range, zoom to fit the new location.
	 */
	private void updatePatientLocationTooltip() {
		// update information tooltip if open
		if (patient.isInfoWindowShown()) {
			patient.hideInfoWindow();
			// before opening the info, make sure the marker is on the screen
			// TODO: camera update camera factory zoom to fit location of marker
			patient.showInfoWindow();
		}
	}
	
	/**
	 * Updates the patient history line using a set of points which are joined together.
	 * @param pointSet the lat/lng pairs which are used to draw a line
	 */
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
	
	// ---------- BEGIN GEOFENCE RELATED ITEMS ----------
	
	/**
	 * This method runs when the map is pressed for a longer time.
	 * @param point The point where the user pressed.
	 */
	@Override
	public void onMapLongClick(LatLng point) {
		// create geofence details in database and display on map
		new SaveNewGeofence().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, point);
		//new SaveGeofence(point).start();
	}
	
	/**
	 * Stores a newly created geofence in the database.
	 */
	private class SaveNewGeofence extends AsyncTask<LatLng, Void, String>{
		
		Double lat = null;
		Double lng = null;
		GeofenceVisualisation newFence;
		
		@Override
		protected String doInBackground(LatLng... params) {
			lat = params[0].latitude;
			lng = params[0].longitude;
			
			// create visualisation before while connecting for a better feedback response
			System.out.println("Displaying new POINT...");
			publishProgress();
			
			// store parameters
			String[] parameters = {"patient=0",
					"lat=" + lat,
					"lng=" + lng,
					"radius=" + GEOFENCE_RADIUS,
					"expiration=" + Geofence.NEVER_EXPIRE,
					"transition=" + Geofence.GEOFENCE_TRANSITION_EXIT};
			
			// post information
			DBConn conn = new DBConn("/saveNewGeofence.php");
			conn.execute(parameters);
			
			return conn.getResult();
		}
		
		@Override
		protected void onProgressUpdate(Void... mVoid) {
			
			// draw initial visualisation
			newFence = new GeofenceVisualisation(
					googleMap,
					lat,
					lng,
					GEOFENCE_RADIUS,
					Geofence.NEVER_EXPIRE,
					// This geofence records only entry transitions
					// to have enter/exit try adding?
					Geofence.GEOFENCE_TRANSITION_ENTER);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result == "") {
				// database save unsuccessful
				System.out.println("DB SAVE ERROR");
				newFence.tearDown();
				Toast.makeText(getApplicationContext(),
						"Network Failure while Creating Geofence", Toast.LENGTH_SHORT)
						.show();
			} else {
				// update visualisation now that the id is available from the database
				System.out.println("DB SAVE SUCCESS");
				newFence.initialise(result);
			}
		}
	}
	
	// ---------- BEGIN GEOFENCE RETRIEVE ITEMS ----------
	
	private class RetrieveGeofences extends AsyncTask<Void, Void, String>{
		
		@Override
		protected String doInBackground(Void... params) {
			// store parameters
			String[] parameters = {"patientid=0"};
			
			// post information
			DBConn conn = new DBConn("/retrieveGeofences.php");
			conn.execute(parameters);
			
			return conn.getResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			// network errors may result in a null result
			// server errors may result in unexpected output
			try {
				// list of recent points
				String[] geofences = result.split(";");
				// parsing
				for (String geofence : geofences) {
					
					String[] geofenceParams = geofence.split(",");
					
					new GeofenceVisualisation(
							googleMap,
							geofenceParams[0],
							Double.valueOf(geofenceParams[2]),
							Double.valueOf(geofenceParams[3]),
							Float.valueOf(geofenceParams[4]),
							Long.valueOf(geofenceParams[5]),
							Integer.valueOf(geofenceParams[6])).hideInfo();
				}
			} catch (Exception e) {
				System.out.println("GEOFENCE RETRIEVE ERROR");
			}
		}
	}
	
	/**
	 * Listener for patient location updates.
	 */
	@Override
	public void onLocationRetrieved(String locations) {
		
		// access to the main thread
		Handler handler = new Handler(Looper.getMainLooper());
		
		// network errors may result in a null result
		// server errors may result in unexpected output
		try {
			// list of recent points
			final String[] latestPoints = locations.split(";");
			// first recent point is the latest position
			String[] location = latestPoints[0].split(",");
			
			// process latest position
			final LatLng position = new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
			positionTimestamp = location[2];
			connectionTimestamp = mySQLFormat.format(new Date());
			
			// update the UI in the main thread
			handler.post(new Runnable(){
				@Override
				public void run() {
					updateLocation(position, "Updated " + getTimeAge(positionTimestamp) + " ago.");
					updateTrack(latestPoints);
				} 
			});
			
		} catch (NumberFormatException e) {
			System.out.println("Patient Location Retrieve Parse Error");
			// only update age of the retrieved location
			if (positionTimestamp != null) {
				// update the UI in the main thread
				handler.post(new Runnable(){
					@Override
					public void run() {
						updateLocation("Location from " + getTimeAge(positionTimestamp)
								+ " ago. Connection failed for " + getTimeAge(connectionTimestamp) + ".");
					} 
				});
			}
		}
		
	}
	
	// ---------- BEGIN GEOFENCE MODIFY ITEMS ----------
	
	
	
}