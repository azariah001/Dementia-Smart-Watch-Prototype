package com.illusivemen.maps;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {
	// Instance variables
	private final String mId;
	private final double mLatitude;
	private final double mLongitude;
	private final float mRadius;
	private long mExpirationDuration;
	private int mTransitionType;
    private int mActive;

	/**
	 * @param geofenceId The Geofence's request ID
	 * @param latitude Latitude of the Geofence's center.
	 * @param longitude Longitude of the Geofence's center.
	 * @param radius Radius of the geofence circle.
	 * @param expiration Geofence expiration duration
	 * @param transition Type of Geofence transition.
	 */
    public SimpleGeofence(
            String geofenceId,
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition) {
        // Overloads the active value to int 1 (bool true) if not defined in function call.
        this(geofenceId, latitude, longitude, radius, expiration, transition, 1);
    }
	public SimpleGeofence(
			String geofenceId,
			double latitude,
			double longitude,
			float radius,
			long expiration,
			int transition,
            int active) {
		// Set the instance fields from the constructor
		this.mId = geofenceId;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mExpirationDuration = expiration;
		this.mTransitionType = transition;
        this.mActive = active;
	}
	
	// Instance field getters
	public String getId() {
		return mId;
	}
	public double getLatitude() {
		return mLatitude;
	}
	public double getLongitude() {
		return mLongitude;
	}
	public float getRadius() {
		return mRadius;
	}
	public long getExpirationDuration() {
		return mExpirationDuration;
	}
	public int getTransitionType() {
		return mTransitionType;
	}
    public int getActiveStatus() { return  mActive; }
	
	/**
	 * Creates a Location Services Geofence object from a
	 * SimpleGeofence.
	 *
	 * @return A Geofence object
	 */
	public Geofence toGeofence() {
		// Build a new Geofence object
		return new Geofence.Builder()
		.setRequestId(getId())
		.setTransitionTypes(mTransitionType)
		.setCircularRegion(
				getLatitude(), getLongitude(), getRadius())
				.setExpirationDuration(mExpirationDuration)
				.build();
	}
}
