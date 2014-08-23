package com.illusivemen.maps;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class AdminGoogleMapping extends Activity {
	
	public final static String MAP_PURPOSE = "com.illusivemen.mapping.EXTRAS_PAYLOAD_KEY";
	private String purpose;
	
	private GoogleMap googleMap;
	private static final float INITIAL_ZOOM= 18;
	
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
		//purpose = getIntent().getStringExtra(MAP_PURPOSE);
		
		// Title by Function
		//switch (purpose) {
		//case "TrackPatients":
		//	this.setTitle("Patients' Locations");
		//	break;
		//}
		
		// Create Map
		//initilizeMap();
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
    }
    
    private void subscribeForLocations() {
		//TODO: Display locations of patients.
    }
    
    /**
     * Disable functions that manipulate the map other than the zoom levels.
     */
    private void setupUi() {
    	googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }
}