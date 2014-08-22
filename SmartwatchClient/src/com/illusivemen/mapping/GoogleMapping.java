package com.illusivemen.mapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.illusivemen.smartwatchclient.MainMenu;
import com.illusivemen.smartwatchclient.R;
import com.illusivemen.smartwatchclient.R.drawable;
import com.illusivemen.smartwatchclient.R.id;
import com.illusivemen.smartwatchclient.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract.Helpers;
import android.widget.Toast;

public class GoogleMapping extends Activity {
	
	private GoogleMap googleMap;
	private LocationManager locationManager;
	private Marker myMarker;
	private static final long MIN_UPDATE_MILLISEC = 250;
	private static final float MIN_UPDATE_METRES = 1;
	private static final float INITIAL_ZOOM= 18;
	private String purpose;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_mapping);
		
		// What is the Function of this Map?
		Intent source = getIntent();
		purpose = source.getStringExtra(MainMenu.ACTIVITY_MESSAGE);
		
		// Title by Function
		switch (purpose) {
		case "LIVE_MAP":
			this.setTitle("Current Location");
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			break;
		}
		
		// Create Map
		initilizeMap();
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
            // disable unnecessary gestures
            disableAdvancedUi();
            
            // location updates
            subscribeForLocation();
 
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
    }
    
    private void subscribeForLocation() {
		// start by using last known location
		//TODO: CRASHES if previous location hasn't been found yet. In future use the PASSIVE_PROVIDER from the main application/service.
		Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
			
		// position map and current location marker
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM));
		
		// create custom icon for current position
		myMarker = googleMap.addMarker(new MarkerOptions()
		        .position(latLng)
		        .title("Current Location")
		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
		
		// listen for location updates on all sources - should be LocationManager.PASSIVE_PROVIDER in future
		locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_UPDATE_MILLISEC, MIN_UPDATE_METRES, locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_UPDATE_MILLISEC, MIN_UPDATE_METRES, locationListener);
	
		
    }
    
    /**
     * Disable functions that manipulate the map other than the zoom levels.
     */
    private void disableAdvancedUi() {
    	googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }
	
	private final LocationListener locationListener = new LocationListener() {
	    @Override
	    public void onLocationChanged(final Location location) {
	    	// move to location
	        LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
	        googleMap.animateCamera(CameraUpdateFactory.newLatLng(newPosition));
	        myMarker.setPosition(newPosition);
	    }

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
			
		}
	};
	
	private void sendToServer(LatLng latlng) {
		new SaveTask().execute(latlng);
	}
	
	// Background thread to save the location in remote MySQL server
	private class SaveTask extends AsyncTask<LatLng, Void, Void> {
		@Override
		protected Void doInBackground(LatLng... params) {
			String lat = Double.toString(params[0].latitude);
            String lng = Double.toString(params[0].longitude);
            String strUrl = "http://agile.azarel-howard.me/";
            URL url = null;
            try {
            	url = new URL(strUrl);
            	
            	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            	connection.setRequestMethod("POST");
            	connection.setDoOutput(true);
            	OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            	
            	outputStreamWriter.write("lat=" + lat + "&lng="+lng);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new
                InputStreamReader(iStream));
 
                StringBuffer sb = new StringBuffer();
 
                String line = "";
 
                while( (line = reader.readLine()) != null) {
                    sb.append(line);
                }
 
                reader.close();
                iStream.close();
 
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
			return null;			
		}
	}
}
