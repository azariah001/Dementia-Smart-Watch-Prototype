package com.illusivemen.mapping;

import java.io.*;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.illusivemen.db.DBConn;
import com.illusivemen.smartwatchclient.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class GoogleMapping extends Activity {
	
	public final static String MAP_PURPOSE = "com.illusivemen.mapping.EXTRAS_PAYLOAD_KEY";
	private String purpose;
	
	private GoogleMap googleMap;
	private Geocoder geocoder;
	private List<Address> addresses;
	private TextView address;
	private LocationManager locationManager;
	private Marker myMarker;
	private static final long MIN_UPDATE_MILLISEC = 250;
	private static final float MIN_UPDATE_METRES = 1;
	private static final float INITIAL_ZOOM= 18;
	private static final String DUMP_SCRIPT = "/saveNewLocation.php";
	
	/**
     * Factory method to create a launch Intent for this activity.
     *
     * @param context the context that intent should be bound to
     * @param payload the payload data that should be added for this intent
     * @return a configured intent to launch this activity with a String payload.
     */
    public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, GoogleMapping.class).putExtra(MAP_PURPOSE, payload);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_mapping);
		
		// What is the Function of this Map?
		purpose = getIntent().getStringExtra(MAP_PURPOSE);
		
		// Title by Function
		switch (purpose) {
		case "ClientStart":
			this.setTitle("Current Location");
			break;
		}
		// for receiving location
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// for resolving address
		geocoder = new Geocoder(this, Locale.getDefault());
		address = (TextView) findViewById(R.id.address);
		
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
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
	        // resolve address
	        try {
				addresses = geocoder.getFromLocation(newPosition.latitude, newPosition.longitude, 1);
				address.setText(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1));
			} catch (IOException e) {
				System.out.println("Download Address from Location Network Failiure");
			}
	        // comment to disable
	        sendToServer(location);
	    }

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
			/* This is called when the GPS status alters */
		    switch (status) {
			    case 0:
			        showToast("Status Changed: Out of Service");
			        break;
			    case 1:
			    	showToast("Status Changed: Temporarily Unavailable");
			        break;
			    case 2:
			        showToast("Status Changed: Available");
			        break;
		    }
		}
	};
	
	private void showToast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	private void sendToServer(Location location) {
		new SaveTask().execute(location);
	}
	
	// Background thread to save the location in remote MySQL server
	private class SaveTask extends AsyncTask<Location, Void, Void> {
		@Override
		protected Void doInBackground(Location... params) {
			
			// store parameters
			String[] parameters = {"patient=0",
					"lat=" + params[0].getLatitude(),
					"lng=" + params[0].getLongitude(),
					"acc=" + params[0].getAccuracy()};
			
			// post information
			DBConn conn = new DBConn(DUMP_SCRIPT);
			conn.execute(parameters);
			return null;
		}
	}
}