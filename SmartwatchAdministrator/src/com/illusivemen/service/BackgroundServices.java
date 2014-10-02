package com.illusivemen.service;

import java.util.HashMap;

import com.illusivemen.db.OnLoopRetrievedListener;
import com.illusivemen.db.RetrieveLoopThread;
import com.illusivemen.smartwatchadministrator.MainMenu;
import com.illusivemen.smartwatchadministrator.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class BackgroundServices extends Service implements OnLoopRetrievedListener {
	
	private NotificationManager notificationManager;
	private static final int GEOFENCE_POLL_PERIOD = 10000;
	private HashMap<Integer, Boolean> geofenceStatus;
	
	@Override
	public void onCreate() {
		geofenceStatus = new HashMap<Integer, Boolean>();
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		startGeofenceQuery();
	}
	
	@Override
	public IBinder onBind(Intent workIntent) {
		return null;
	}

	@Override
	public void onLocationRetrieved(String locations) {
		String[] patients = locations.split(";");
		for (String patient : patients) {
			
			String patientName;
			int patientId;
			Boolean inGeofence;
			String[] details = patient.split(",");
			
			try {
				patientName = details[0];
				patientId = Integer.parseInt(details[1]);
				inGeofence = (Double.parseDouble(details[2]) <= 0);
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				// incorrect input format
				return;
			}
			
			if (geofenceStatus.get(patientId) == null) {
				// initial value
				geofenceStatus.put(patientId, inGeofence);
			} else if (geofenceStatus.get(patientId) != inGeofence) {
				// patient has transitioned in/out of a geofence
				
				// update value
				geofenceStatus.put(patientId, inGeofence);
				
				if (!inGeofence) {
					// patient has left geofences, send a notification
					sendNotification(patientName);
				}
			}
			
			if (!inGeofence) {
				// patient is not within a geofence
				System.out.println(patientName + " IS NOT IN A GEOFENCE!!!!");
			}
		}
	}
	
	private void startGeofenceQuery() {
		// retrieve patient location in loop
		RetrieveLoopThread geofenceRetrieveThread = new RetrieveLoopThread("/geofenceDistances.php", GEOFENCE_POLL_PERIOD);
		geofenceRetrieveThread.setListener(this);
		geofenceRetrieveThread.start();
	}
	
	private void sendNotification(String patientName) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Patient left geofence")
				.setContentText(patientName + " is not in a geofence.");
		
		// TODO: make the notification open up the activity
		// XXX: current code requires android api v16+
		// Creates an explicit intent for an Activity in your app
		//Intent resultIntent = new Intent(this, MainMenu.class);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		//TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		//stackBuilder.addParentStack(MainMenu.class);
		// Adds the Intent that starts the Activity to the top of the stack
		//stackBuilder.addNextIntent(resultIntent);
		//PendingIntent resultPendingIntent =
		//		stackBuilder.getPendingIntent(
		//				0,
		//				PendingIntent.FLAG_UPDATE_CURRENT
		//				);
		//mBuilder.setContentIntent(resultPendingIntent);
		
		// mId allows you to update the notification later on.
		notificationManager.notify(0, mBuilder.build());

	}

}
