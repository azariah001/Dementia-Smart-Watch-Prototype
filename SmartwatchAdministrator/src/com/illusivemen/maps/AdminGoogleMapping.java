package com.illusivemen.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
	private LatLng patientLocation;
	
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
		// Retrieve patient location information
		new RetrieveTask().execute();		
	}
	
	/**
     * Load map. If map is not created it will create it for you.
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // set type of map to use
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // setup UI controlls
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
        new RetrieveTask().execute();
    }
    
    private void subscribeForLocations() {
		//TODO: Display locations of patients.
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
    
    private void trackPatient(LatLng latlng) {
    	
    	// Starting locations retrieve task    	
    	Marker patient = googleMap.addMarker(new MarkerOptions()
	       		.position(latlng)
	       		.title("Patient Location"));
    }
	
	private class RetrieveTask extends AsyncTask<Void, Void, String>{
		
		@Override
		protected String doInBackground(Void... params) {
			String strUrl = "http://agile.azarel-howard.me/retrieve.php";
			URL url = null;
			StringBuffer sb = new StringBuffer();
			try {
				url = new URL(strUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line = "";
                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }
 
                reader.close();
                iStream.close();
 
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new ParserTask().execute(result);
		}
	}
		
	// Background thread to parse the JSON data retrieved from MySQL server
	private class ParserTask extends AsyncTask<String, Void, List<HashMap<String, String>>>{
		@Override
	    protected List<HashMap<String,String>> doInBackground(String... params) {
			MarkerJSONParser markerParser = new MarkerJSONParser();
	        JSONObject json = null;
	        try {
	            json = new JSONObject(params[0]);
	        } catch (JSONException e) {
	        	e.printStackTrace();
	        }
	        List<HashMap<String, String>> markersList = markerParser.parse(json);
	        return markersList;
		}
	        
	    @Override
	    protected void onPostExecute(List<HashMap<String, String>> result) {
	     	for(int i=0; i<result.size();i++){
	     		HashMap<String, String> marker = result.get(i);
	     		LatLng latlng = new LatLng(Double.parseDouble(marker.get("lat")), Double.parseDouble(marker.get("lng")));
	     		trackPatient(latlng);
	     	}
	    }
	}
}