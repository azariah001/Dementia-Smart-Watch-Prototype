package com.illusivemen.service;

import com.illusivemen.db.DBConn;
import com.illusivemen.login.LogIn;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

public class LocationPush extends Service {
	
	private LocationManager locationManager;
	// don't want locations that are too close together
	private static final long MIN_UPDATE_MILLISEC = 1000;
	private static final float MIN_UPDATE_METRES = 1;
	// last GPS point, how long to wait before accepting a WiFi location
	private Long gpsTimestamp;
	private final static int GPS_TIMEOUT = 1000 * 8;
	// script which stores locations in the database
	private static final String LOCATION_DUMP_SCRIPT = "/saveNewLocation.php";
	LogIn login;
	
	@Override
	public void onCreate() {
		listenForLocation();
	}
	
	/**
	 * Prevent service from starting again once application closed.
	 * XXX: THIS SHOULD NOT BE SET IN RELEASES
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// listener may still be running and processing data after service is stopped
		locationManager.removeUpdates(locationListener);
	}
	
	/**
	 * Used to bind a running application for a client/server interface?
	 */
	@Override
	public IBinder onBind(Intent workIntent) {
		
		return null;
	}
	
	private void listenForLocation() {
		// for receiving location
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		// listen for location updates on all sources - should be LocationManager.PASSIVE_PROVIDER in future
		locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_UPDATE_MILLISEC, MIN_UPDATE_METRES, locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_UPDATE_MILLISEC, MIN_UPDATE_METRES, locationListener);
	}
	
	
	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			
			// location quality determination
			if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
				gpsTimestamp = location.getTime();
			} else if (gpsTimestamp != null && 
					(location.getTime()) < (gpsTimestamp + GPS_TIMEOUT)) {
				// don't accept a low accuracy estimate of a location
				return;
			}
			
			// push new location to database
			new SaveTask().execute(location);
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
	
	/**
	 * Background thread to save the location in remote MySQL server.
	 */
	private class SaveTask extends AsyncTask<Location, Void, Void> {
		@Override
		protected Void doInBackground(Location... params) {
			
			login = new LogIn();
			String patientId = login.GetId(getApplicationContext());
			
			// store parameters
			String[] parameters = {
                    "patient=" + patientId,
					"lat=" + params[0].getLatitude(),
					"lng=" + params[0].getLongitude(),
					"acc=" + params[0].getAccuracy()
             };
			
			// post information
			DBConn conn = new DBConn(LOCATION_DUMP_SCRIPT);
			conn.execute(parameters);
			return null;
		}
	}
}
