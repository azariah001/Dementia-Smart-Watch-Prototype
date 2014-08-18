package com.illusivemen.smartwatchclient;

public class PositionUncertainty {

	//TODO Private Fields
	
	//TODO Constructor
	public PositionUncertainty() {
		GoogleMapping googleMap = new GoogleMapping();
	}
	
	//TODO Set current location
	public void setCurrentLocation() {
		
	}
	
	//TODO Set previous location
	public void setPreviousLocation() {
		
	}
	
	//TODO Set certainty radius
	public void setCertaintyRadius() {
		
	}
	
	//TODO Check if current location is in the radius of the previous location
	public boolean isCertain() {
		boolean certain;
		
		//Checks whether the position is within a radius of the old position
		if() {
			certain = true;
		} else {
			certain = false;
		}
		
		return certain;
	}
}
