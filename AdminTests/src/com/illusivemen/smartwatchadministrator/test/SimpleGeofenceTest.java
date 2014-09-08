package com.illusivemen.smartwatchadministrator.test;

import junit.framework.TestCase;

import com.google.android.gms.location.Geofence;
import com.illusivemen.maps.SimpleGeofence;

import android.test.suitebuilder.annotation.SmallTest;

public class SimpleGeofenceTest extends TestCase {
	
	private SimpleGeofence geofence;
	private SimpleGeofence testfence;
	
	private String geofenceId = "18834";
	private double latitude = -27.18423523453;
	private double longitude = 153.14564354546345;
	private float radius = 383;
	private long expiration = Geofence.NEVER_EXPIRE;
	private int transition = Geofence.GEOFENCE_TRANSITION_ENTER + Geofence.GEOFENCE_TRANSITION_EXIT;;
	
	public SimpleGeofenceTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		geofence = new SimpleGeofence(geofenceId, latitude, longitude, radius, expiration, transition);
	}
	
	@Override
	public void tearDown() {
		
	}
	
    /**
     * Test the getter for ID.
     */
    @SmallTest
    public void testGetter_id() {
	    assertEquals(geofence.getId(), geofenceId);
	}
    
    /**
     * Test the getter for latitude.
     */
    @SmallTest
    public void testGetter_latitude() {
	    assertEquals(geofence.getLatitude(), latitude);
	}
    
    /**
     * Test the getter for longitude.
     */
    @SmallTest
    public void testGetter_longitude() {
	    assertEquals(geofence.getLongitude(), longitude);
	}
    
    /**
     * Test the getter for radius.
     */
    @SmallTest
    public void testGetter_radius() {
	    assertEquals(geofence.getRadius(), radius);
	}
    
    /**
     * Test the getter for expiration.
     */
    @SmallTest
    public void testGetter_expiration() {
	    assertEquals(geofence.getExpirationDuration(), expiration);
	}
    
    /**
     * Test the getter for transition.
     */
    @SmallTest
    public void testGetter_duration() {
	    assertEquals(geofence.getTransitionType(), transition);
	}

}
