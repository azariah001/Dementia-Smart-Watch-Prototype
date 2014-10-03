package com.illusivemen.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.illusivemen.db.OnLoopRetrievedListener;
import com.illusivemen.db.RetrieveLoopThread;
import com.illusivemen.maps.AdminGoogleMapping;
import com.illusivemen.smartwatchadministrator.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.SparseBooleanArray;

public class BackgroundServices extends Service implements OnLoopRetrievedListener {
	
	// used for creating notifications
	private NotificationManager notificationManager;
	// how often to check for patients outsite their fences (millisec)
	private static final int GEOFENCE_POLL_PERIOD = 10000;
	// patient position not updated alert threshold
	private static final long POS_TIMEOUT = 5 * 60;
	// access to the worker thread
	RetrieveLoopThread geofenceRetrieveThread;
	// geofence and timeout state storage
	private SparseBooleanArray geofenceStatus;
	private SparseBooleanArray timeoutStatus;
	// database time format
	private SimpleDateFormat mySQLFormat;
	
	/**
	 * Setup method for this service.
	 * When the service is started, this method is called.
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate() {
		geofenceStatus = new SparseBooleanArray();
		timeoutStatus = new SparseBooleanArray();
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// used for parsing timestamps from mysql
		mySQLFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mySQLFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		geofenceRetrieveThread = new RetrieveLoopThread("/geofenceDistances.php", GEOFENCE_POLL_PERIOD);
		startGeofenceQuery();
	}
	
	/**
	 * Prevent service from starting again once application closed.
	 * XXX: THIS SHOULD NOT BE SET IN RELEASES
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
	
	/**
	 * Tear-down method for this service.
	 * When this service is stopped, this method should stop all loops.
	 */
	@Override
	public void onDestroy() {
		geofenceRetrieveThread.safeStop();
	}
	
	/**
	 * Used to bind service to an application.
	 */
	@Override
	public IBinder onBind(Intent workIntent) {
		return null;
	}
	
	/**
	 * The processed output of who is within their closest geofence 
	 * is returned to this method.
	 */
	@Override
	public void onLocationRetrieved(String locations) {
		
		// TODO: debugging purposes only.
		System.out.println(locations);
		
		String[] patients = locations.split(";");
		for (String patient : patients) {
			
			String patientName;
			int patientId;
			Boolean inGeofence;
			Boolean timedOut;
			String[] details = patient.split(",");
			
			try {
				patientName = details[0];
				patientId = Integer.parseInt(details[1]);
				inGeofence = (Double.parseDouble(details[2]) <= 0);
				timedOut = (new Date().getTime()/1000 
						- mySQLFormat.parse(details[3]).getTime()/1000)
						> POS_TIMEOUT;
			} catch (NumberFormatException | IndexOutOfBoundsException | ParseException e) {
				// incorrect input format
				return;
			}
			
			// GEOFENCE PROCESSING IM-61
			if (geofenceStatus.indexOfKey(patientId) < 0) {
				// initial value
				geofenceStatus.put(patientId, inGeofence);
			} else if (geofenceStatus.get(patientId) != inGeofence) {
				// patient has transitioned in/out of a geofence
				
				// update value
				geofenceStatus.put(patientId, inGeofence);
				
				if (!inGeofence) {
					// patient has left geofences, send a notification
					sendNotification(patientName, patientId, 
							"Geofence Transition", " is not in a geofence.",
							R.raw.left_fence);
				} else {
					// TODO: possibly remove an existing notification
				}
			}
			
			// TIMESTAMP PROCESSING IM-18
			if (timeoutStatus.indexOfKey(patientId) < 0) {
				// initial value
				timeoutStatus.put(patientId, timedOut);
			} else if (timeoutStatus.get(patientId) != timedOut) {
				// patient has either timed out or sent a location after timing out
				
				// update value
				timeoutStatus.put(patientId, timedOut);
				
				if (timedOut) {
					// patient's location has not been updated in a while (timeout)
					sendNotification(patientName, patientId, 
							"Position Timeout", " has not sent a recent location update.",
							R.raw.pos_timeout);
				}
			}
			
			// PANIC STATE PROCESSING IM-37
		}
	}
	
	private void startGeofenceQuery() {
		// retrieve patient location in loop
		geofenceRetrieveThread.setListener(this);
		geofenceRetrieveThread.start();
	}
	
	private void sendNotification(String patientName, int patientId,
			String title, String content, int sound) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(patientName + content)
				.setSound(Uri.parse(
						"android.resource://com.illusivemen.smartwatchadministrator/" 
						+ sound));
		
		// intent to show the position
		Intent resultIntent = new Intent(this, AdminGoogleMapping.class);
		
		// can ensure that navigating backward from intent Activity goes to home screen
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AdminGoogleMapping.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		
		// close the notification once it has been clicked
		// NOTE: very useful: Notification.DEFAULT_LIGHTS will play the notification in a loop until viewed 
		//         - use this for patient panic, separated by |
		Notification n = mBuilder.build();
		n.flags = Notification.FLAG_AUTO_CANCEL;
		
		// first id allows you to update the notification later on
		notificationManager.notify(patientId, n);
	}
}
