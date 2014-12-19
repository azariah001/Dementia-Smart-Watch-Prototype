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
import android.util.SparseIntArray;

public class NotificationService extends Service implements OnLoopRetrievedListener {
	
	// used for creating notifications
	private NotificationManager notificationManager;
	private final static boolean REPEAT = true;
	private final static boolean NO_REPEAT = false;
	// how often to check for patients outsite their fences (millisec)
	private static final int GEOFENCE_POLL_PERIOD = 10000;
	// number of polls before notification is sent due to timeout
	private static final int GEOFENCE_TIMEOUT = 2;
	// patient position not updated alert threshold
	private static final long POS_TIMEOUT = 5 * 60;
	// access to the worker thread
	RetrieveLoopThread geofenceRetrieveThread;
	// geofence, timeout and panic state storage
	private SparseBooleanArray geofenceStatus;
	private SparseIntArray geofenceTimeout;
	private SparseBooleanArray timeoutStatus;
	private SparseBooleanArray panicStatus;
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
		geofenceTimeout = new SparseIntArray();
		timeoutStatus = new SparseBooleanArray();
		panicStatus = new SparseBooleanArray();
		
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
			Boolean inGeofence = null;
			Boolean timedOut = null;
			Boolean isPaniced;
			String[] details = patient.split(",");
			
			try {
				// items that cannot be NULL (returned as ''):
				patientName = details[0];
				patientId = Integer.parseInt(details[1]);
				isPaniced = (Integer.parseInt(details[4]) > 0);
				// items that may be NULL depending on the situation:
				try {
					inGeofence = (Double.parseDouble(details[2]) <= 0);
					timedOut = (new Date().getTime()/1000 
							- mySQLFormat.parse(details[3]).getTime()/1000)
							> POS_TIMEOUT;
				} catch (NumberFormatException | ParseException e) {
					// quite possible to legitimately not be set yet
				}
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				// incorrect input format
				return;
			}
			
			// GEOFENCE PROCESSING IM-61
			if (inGeofence != null && geofenceStatus.indexOfKey(patientId) < 0) {
				// initial value
				geofenceStatus.put(patientId, inGeofence);
			} else if (inGeofence != null) {
				// patient has at least one fence
				boolean transitioned = geofenceStatus.get(patientId) != inGeofence;
				
				// update value but first reset timout counter if now within geofence
				if (inGeofence && geofenceTimeout.indexOfKey(patientId) >= 0) {
					geofenceTimeout.put(patientId, 0);
				}
				geofenceStatus.put(patientId, inGeofence);
				
				if (!inGeofence && (Integer.valueOf(details[5]) == 1)) {
					if (geofenceTimeout.indexOfKey(patientId) < 0) {
						if (transitioned) {
							// patient that was in a geofence now isn't
							geofenceTimeout.put(patientId, 0);
						}
					} else {
						// increment timout value
						geofenceTimeout.put(patientId, geofenceTimeout.get(patientId) + 1);
					}
					
					if (geofenceTimeout.get(patientId) == GEOFENCE_TIMEOUT) {
						// patient has left geofences for the period of timout cycles
						sendNotification(patientName, patientId, 
								"Geofence Transition", " is not in a geofence.",
								R.raw.left_fence, NO_REPEAT);
					}
					
				} else {
					// TODO: possibly remove an existing notification
				}
			}
			
			// TIMESTAMP PROCESSING IM-18
			if (timedOut != null && timeoutStatus.indexOfKey(patientId) < 0) {
				// initial value
				timeoutStatus.put(patientId, timedOut);
			} else if (timedOut != null && timeoutStatus.get(patientId) != timedOut) {
				// patient has either timed out or sent a location after timing out
				
				// update value
				timeoutStatus.put(patientId, timedOut);
				
				if (timedOut) {
					// patient's location has not been updated in a while (timeout)
					sendNotification(patientName, patientId, 
							"Position Timeout", " has not sent a recent location update.",
							R.raw.pos_timeout, NO_REPEAT);
				}
			}
			
			// PANIC STATE PROCESSING IM-37
			if (panicStatus.indexOfKey(patientId) < 0) {
				// initial value
				panicStatus.put(patientId, isPaniced);
			} else if (panicStatus.get(patientId) != isPaniced) {
				// patient has either entered the panic state or it has been dismissed
				
				// update value
				panicStatus.put(patientId, isPaniced);
				
				if (isPaniced) {
					// patient has switched to the panic state
					sendNotification(patientName, patientId,
							"PATIENT PANIC", " needs assistance.",
							R.raw.panic_alarm, REPEAT);
				}
			}
		}
	}
	
	private void startGeofenceQuery() {
		// retrieve patient location in loop
		geofenceRetrieveThread.setListener(this);
		geofenceRetrieveThread.start();
	}
	
	private void sendNotification(String patientName, int patientId,
			String title, String content, int sound, boolean repeatSound) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(patientName + content)
				.setSound(Uri.parse(
						"android.resource://com.illusivemen.smartwatchadministrator/" 
						+ sound));
		
		// intent to show the position
		Intent resultIntent = new Intent(this, AdminGoogleMapping.class).putExtra(AdminGoogleMapping.PATIENT_TO_TRACK, String.valueOf(patientId));
		
		// can ensure that navigating backward from intent Activity goes to home screen
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AdminGoogleMapping.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		
		// close the notification once it has been clicked, optionally repeat sound? (default lights)
		Notification n = mBuilder.build();
		if (repeatSound) {
			n.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
		} else {
			n.flags = Notification.FLAG_AUTO_CANCEL;
		}
		
		// first id allows you to update the notification later on
		notificationManager.notify(patientId, n);
	}
}
