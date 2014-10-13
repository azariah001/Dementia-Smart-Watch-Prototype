package com.illusivemen.maps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.location.Geofence;
import com.illusivemen.db.DBConn;
import com.illusivemen.db.OnLoopRetrievedListener;
import com.illusivemen.db.RetrieveLoopThread;
import com.illusivemen.patients.CurrentPatient;
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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminGoogleMapping extends Activity implements OnMapLongClickListener, OnInfoWindowClickListener, OnLoopRetrievedListener {
	
	public final static String PATIENT_TO_TRACK = "com.illusivemen.maps.EXTRAS_PAYLOAD_KEY";
	private int patientBeingTracked;
	
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
	private Circle patientUncertainty;
	private Polyline track;
	private boolean trackPatient = false;
	private final static int UPDATE_PERIOD = 2000;
	// database time format
	private SimpleDateFormat mySQLFormat;
	// last retrieved time and db connection time
	private String positionTimestamp = null;
	private String connectionTimestamp = null;
	// local geofence storage and default properties
	ArrayList<GeofenceVisualisation> geofences = new ArrayList<GeofenceVisualisation>();
	private static final int GEOFENCE_RADIUS = 30;
	private HashMap<Marker, Integer[]> geofenceMarkerMap;
	// the patient currently being watched
	CurrentPatient currentPatient;
	
	
	/**
	 * Factory method to create a launch Intent for this activity.
	 *
	 * @param context the context that intent should be bound to
	 * @param payload the payload data that should be added for this intent
	 * @return a configured intent to launch this activity with a String payload.
	 */
	public static Intent makeIntent(Context context, String payload) {
		return new Intent(context, AdminGoogleMapping.class).putExtra(PATIENT_TO_TRACK, payload);
	}
	
	/**
	 * Setup of the activity.
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_google_mapping);
		
		// TODO: this should be used for which patient to show first
		try {
			patientBeingTracked = Integer.parseInt(getIntent().getStringExtra(PATIENT_TO_TRACK));
		} catch (NumberFormatException e) {
			// this activity has been called badly
			System.out.println("ACTIVITY STARTED WITH BAD PAYLOAD");
		}
		
		// used for parsing timestamps from mysql
		mySQLFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mySQLFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		// Create Map
		initilizeMap();
		// Location Updater
		subscribeForLocations();
		// load geofence visualisations
		geofenceMarkerMap = new HashMap<Marker, Integer[]>();
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
			case R.id.action_trackPosition:
				togglePositionTracking();
				return true;
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
	 * Called via the action button.
	 * Set's whether the patient's location should remain centered.
	 */
	private void togglePositionTracking() {
		// toggle icon, screen dim-able, check state
		if (trackPatient) {
			// turning off patient position tracking
			menu.findItem(R.id.action_trackPosition).setIcon(R.drawable.track_off);
			menu.findItem(R.id.action_trackPosition).setChecked(false);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			// turning on patient position tracking
			menu.findItem(R.id.action_trackPosition).setIcon(R.drawable.track_on);
			menu.findItem(R.id.action_trackPosition).setChecked(true);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			// initial move to patient position
			try {
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(patient.getPosition()));
			} catch (NullPointerException e) {
				// patient marker hasn't been created yet
			}
		}
		
		// update the value
		trackPatient = !trackPatient;
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
			// act out on long clicks and marker infowindow clicks
			googleMap.setOnMapLongClickListener(this);
			googleMap.setOnInfoWindowClickListener(this);
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
		currentPatient = new CurrentPatient();
		String patientId = currentPatient.GetId(getApplicationContext()); // the patient currently being watched IM-15
		RetrieveLoopThread retrieveThread = new RetrieveLoopThread("/retrieveLastLocations.php", new String[]{"patient=" + patientId}, UPDATE_PERIOD);
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
	private void updateLocation(LatLng latlng, int uncertainty, String positionAge) {
		
		if (patient != null) {
			// update previous location
			patient.setPosition(latlng);
			patient.setSnippet(positionAge);
			patientUncertainty.setCenter(latlng);
			patientUncertainty.setRadius(uncertainty);
			
			// move view to focus on patient's new location
			if (trackPatient) {
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
			}
			
			updatePatientLocationTooltip();
		} else {
			// add initial marker
			patient = googleMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title("Patient's Location")
					.snippet(positionAge));
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, LOCATED_ZOOM));
			// add initial uncertainty circle
			patientUncertainty = googleMap.addCircle(new CircleOptions()
					.center(latlng)
					.radius(uncertainty)
					.fillColor(0x20FF0000)
					.strokeColor(Color.RED)
					.strokeWidth(2));
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
			final int uncertainty = Integer.parseInt(location[3]);
			positionTimestamp = location[2];
			connectionTimestamp = mySQLFormat.format(new Date());
			
			// update the UI in the main thread
			handler.post(new Runnable(){
				@Override
				public void run() {
					updateLocation(position, uncertainty, "Updated " + getTimeAge(positionTimestamp) + " ago.");
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
			publishProgress();
			
			currentPatient = new CurrentPatient();
			String patientId = currentPatient.GetId(getApplicationContext()); // the patient currently being watched IM-15
			
			// store parameters
			String[] parameters = {"patient=" + patientId,
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
				newFence.tearDown();
				Toast.makeText(getApplicationContext(),
						"Network Failure while Creating Geofence", Toast.LENGTH_SHORT)
						.show();
			} else {
				// update visualisation now that the id is available from the database
				newFence.initialise(result);
				geofences.add(newFence);
				geofenceMarkerMap.put(newFence.getMarker(), new Integer[]{Integer.valueOf(newFence.getId()), (int) newFence.getRadius()});
			}
		}
	}
	
	// ---------- BEGIN GEOFENCE RETRIEVE ITEMS ----------
	
	private class RetrieveGeofences extends AsyncTask<Void, Void, String>{
		
		@Override
		protected String doInBackground(Void... params) {
			// store parameters
			currentPatient = new CurrentPatient(); // the patient currently being watched IM-15
			String patientId = currentPatient.GetId(getApplicationContext());
			String[] parameters = {"patientid=" + patientId};
			
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
				String[] allFences = result.split(";");
				// parsing
				for (String geofence : allFences) {
					
					String[] geofenceParams = geofence.split(",");
					
					GeofenceVisualisation newVisualisation = new GeofenceVisualisation(
							googleMap,
							geofenceParams[0],
							Double.valueOf(geofenceParams[2]),
							Double.valueOf(geofenceParams[3]),
							Float.valueOf(geofenceParams[4]),
							Long.valueOf(geofenceParams[5]),
							Integer.valueOf(geofenceParams[6]));
					newVisualisation.hideInfo();
					geofences.add(newVisualisation);
					geofenceMarkerMap.put(newVisualisation.getMarker(), new Integer[]{Integer.valueOf(newVisualisation.getId()), (int) newVisualisation.getRadius()});
				}
			} catch (NumberFormatException e) {
				System.out.println("GEOFENCE FORMAT ERROR");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(result);
			}
		}
	}
	
	// ---------- BEGIN GEOFENCE MODIFY ITEMS ----------
	
	/**
	 * Edit geofence activity should be opened when clicking a geofence marker info window.
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		
		// geofence infowindow click
		if (marker.getTitle().toString().equals("Geofence")) {
			// store what geofence ID is being modified
			int id = geofenceMarkerMap.get(marker)[0];
			TextView hiddenId = (TextView) findViewById(R.id.geofenceModId);
			hiddenId.setText(String.valueOf(id));
			
			// set the current radius
			int radius = geofenceMarkerMap.get(marker)[1];
			EditText editRadius = (EditText) findViewById(R.id.editRadius);
			editRadius.setText("");
			editRadius.append(String.valueOf(radius));
			
			// show modification dialogue
			findViewById(R.id.overlay).setVisibility(View.VISIBLE);;
		}
	}
	
	private void hideModifyFence() {
		// close keyboard
		EditText editRadius = (EditText) findViewById(R.id.editRadius);
		editRadius.clearFocus();
		
		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editRadius.getWindowToken(), 0);
		
		// close modification dialogue
		findViewById(R.id.overlay).setVisibility(View.GONE);
	}
	
	public void hideModifyFence(View view) {
		hideModifyFence();
	}
	
	public void deleteGeofence(View view) {
		TextView hiddenId = (TextView) findViewById(R.id.geofenceModId);
		String target = String.valueOf(hiddenId.getText());
		
		// delete from the database
		new UpdateGeofenceDB().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 
				new Integer[]{Integer.valueOf(target), -1});
	}
	
	public void updateGeofence(View view) {
		TextView hiddenId = (TextView) findViewById(R.id.geofenceModId);
		String target = String.valueOf(hiddenId.getText());
		
		EditText editRadius = (EditText) findViewById(R.id.editRadius);
		int newRadius = Integer.valueOf(editRadius.getText().toString());
		
		// update in database
		new UpdateGeofenceDB().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 
				new Integer[]{Integer.valueOf(target), newRadius});
	}
	
	/**
	 * Changes an existing geofence by deleting or updating it.
	 */
	private class UpdateGeofenceDB extends AsyncTask<Integer[], Void, String>{
		
		int geofenceId;
		int newRadius;
		GeofenceVisualisation previousVisualisation;
		
		@Override
		protected String doInBackground(Integer[]... params) {
			geofenceId = params[0][0];
			newRadius = params[0][1];
			
			// provide progress feedback
			publishProgress();
			
			// store parameters
			String[] parameters = {"id=" + geofenceId,
					"radius=" + newRadius};
			
			DBConn conn;
			// post information
			if (newRadius < 0) {
				conn = new DBConn("/deleteGeofence.php");
			} else {
				conn = new DBConn("/updateGeofence.php");
			}
			conn.execute(parameters);
			
			return conn.getResult();
		}
		
		@Override
		protected void onProgressUpdate(Void... mVoid) {
			
			// visual feedback that the geofence is in the process of being deleted
			for (GeofenceVisualisation geofence : geofences) {
				if (geofence.getId().equals(String.valueOf(geofenceId))) {
					previousVisualisation = geofence;
					break;
				}
			}
			if (newRadius < 0) {
				previousVisualisation.updateLoad("Deleting");
			} else {
				previousVisualisation.updateLoad("Updating...");
			}
			hideModifyFence();
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result.equals("1")) {
				// database delete/update success
				if (newRadius < 0) {
					// delete from local storage as well
					geofenceMarkerMap.remove(previousVisualisation.getMarker());
					previousVisualisation.tearDown();
					geofences.remove(previousVisualisation);
				} else {
					previousVisualisation.setRadius(newRadius);
					previousVisualisation.initialise(String.valueOf(geofenceId));
					
					// update marker hash table for new radius
					geofenceMarkerMap.remove(previousVisualisation.getMarker());
					geofenceMarkerMap.put(previousVisualisation.getMarker(), new Integer[]{geofenceId, newRadius});
				}
			} else {
				// delete/update fail
				previousVisualisation.initialise(String.valueOf(geofenceId));
			}
		}
	}
	
}