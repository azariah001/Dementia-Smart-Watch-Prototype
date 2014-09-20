package com.illusivemen.db;

/**
 * For notifying listeners of a monitor,
 * an on change method implementing interface is required.
 * This interface is used by listeners of the RetrieveLoopListener class.
 */
public interface OnLoopRetrievedListener {
	
	public void onLocationRetrieved(String locations);
	
}
