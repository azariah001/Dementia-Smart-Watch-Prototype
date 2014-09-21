package com.illusivemen.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.graphics.Color;

public class GeofenceVisualisation {
	// static constants
	private static final int GEOFENCE_COLOUR = 0x40FFFF00;
	
	// reference to map
	private GoogleMap googleMap;
	// map resources
	private Marker marker;
	private Circle circle;
	
	// instance variables
	private String mId;
	private double mLatitude;
	private double mLongitude;
	private float mRadius;
	private long mExpirationDuration;
	private int mTransitionType;

	/**
	 * @param geofenceId The Geofence's request ID
	 * @param latitude Latitude of the Geofence's center.
	 * @param longitude Longitude of the Geofence's center.
	 * @param radius Radius of the geofence circle.
	 * @param expiration Geofence expiration duration
	 * @param transition Type of Geofence transition.
	 */
	public GeofenceVisualisation(
			GoogleMap googleMap,
			String id,
			double latitude,
			double longitude,
			float radius,
			long expiration,
			int transition) {
		this.googleMap = googleMap;
		// set the instance fields from the constructor
		this.mId = id;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mExpirationDuration = expiration;
		this.mTransitionType = transition;
		
		// draw a representation on the map
		updateVisualisation();
	}
	
	/**
	 * Construct initial loading visualisation.
	 * Useful to create a different visualisation while saving information to database.
	 */
	public GeofenceVisualisation(
			GoogleMap googleMap,
			double latitude,
			double longitude,
			float radius,
			long expiration,
			int transition) {
		this.googleMap = googleMap;
		// set the instance fields from the constructor
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mExpirationDuration = expiration;
		this.mTransitionType = transition;
		
		// draw temporary representation on the map
		loadingVisualisation("Saving...");
	}
	
	private void loadingVisualisation(String status) {
		
		LatLng latlng = new LatLng(mLatitude, mLongitude);
		
		if (marker != null && circle != null) {
			marker.setTitle(status);
			marker.setSnippet("Connecting to Database");
			circle.setFillColor(0x40000000);
			
			marker.hideInfoWindow();
			marker.showInfoWindow();
		} else {
			// add initial marker/circle
			marker = googleMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title(status)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
					.snippet("Connecting to Database"));
			marker.showInfoWindow();
			
			circle = googleMap.addCircle(new CircleOptions()
					.center(latlng)
					.radius(mRadius)
					.fillColor(0x40000000)
					.strokeColor(Color.TRANSPARENT)
					.strokeWidth(2));
		}
	}
	
	/**
	 * When created without id, this will set the visualisation to active.
	 */
	public void initialise(String id) {
		this.mId = id;
		marker.setTitle("Geofence");
		marker.setSnippet("Radius: " + mRadius + "m");
		circle.setFillColor(GEOFENCE_COLOUR);
		
		// update view
		marker.hideInfoWindow();
		marker.showInfoWindow();
	}
	
	public void updateLoad(String status) {
		loadingVisualisation(status);
	}
	
	/**
	 * Destroy the visualisation.
	 * Can be useful to use the initialisation of a late ID was unsuccessful.
	 */
	public void tearDown() {
		circle.remove();
		marker.remove();
	}
	
	/**
	 * Change the location of the fence marker/circle on the map.
	 * @param latlng the new position
	 */
	private void updateVisualisation() {
		
		LatLng latlng = new LatLng(mLatitude, mLongitude);
		
		if (marker != null && circle != null) {
			// update previous location
			marker.setPosition(latlng);
			marker.setSnippet("Radius: " + mRadius);
			circle.setCenter(latlng);
			circle.setRadius(mRadius);
		} else {
			// add initial marker/circle
			marker = googleMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title("Geofence")
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
					.snippet("Radius: " + mRadius + "m"));
			marker.showInfoWindow();
			
			circle = googleMap.addCircle(new CircleOptions()
					.center(latlng)
					.radius(mRadius)
					.fillColor(GEOFENCE_COLOUR)
					.strokeColor(Color.TRANSPARENT)
					.strokeWidth(2));
		}
	}
	
	public void hideInfo() {
		marker.hideInfoWindow();
	}
	
	// Instance field setters
	public void setRadius(float newRadius) {
		this.mRadius = newRadius;
		
		// update the radius on the map
		updateVisualisation();
	}
	
	public void setPosition(LatLng newPosition) {
		this.mLatitude = newPosition.latitude;
		this.mLongitude = newPosition.longitude;
		
		// update the position on the map
		updateVisualisation();
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
	public Marker getMarker() {
		return marker;
	}
}
