package com.illusivemen.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class AdminGoogleMapping extends Activity {
	
	public final static String MAP_PURPOSE = "com.illusivemen.maps.EXTRAS_PAYLOAD_KEY";
	private String purpose;
	
	private GoogleMap googleMap;
	private static final float INITIAL_ZOOM= 11;
	private static final LatLng BRISBANE = new LatLng(-27.5,153);
	private Marker patient;
	
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
    
    private void updateLocation(LatLng latlng) {
    	// remove previous location
    	if (patient != null) {
    		patient.remove();
    	}
    	
    	// add new location
    	patient = googleMap.addMarker(new MarkerOptions()
	       		.position(latlng)
	       		.title("Patient's Location"));
    }
	
    class RetrieveUpdateTask extends TimerTask {
    	   public void run() {
    	       new RetrieveLocation().execute();
    	   }
    	}
    
    private class RetrieveLocation extends AsyncTask<Void, Void, String>{
    	
		@Override
		protected String doInBackground(Void... params) {
			String strUrl = "http://agile.azarel-howard.me/retrieveLastLocation.php";
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
				String[] location = result.split(",");
				LatLng position = new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
				updateLocation(position);
			} catch (Exception e) {
				System.out.println("Patient Location Retrieve Parse Error");
			}
		}
	}
}