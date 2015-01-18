package fudan.se.location;

import java.util.Date;

public class MyLocation {
	private double latitude;
	private double longitude;

	public synchronized void setLatAndLon(double latitude,double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	@Override
	public synchronized String toString(){
		return latitude+":"+longitude;
	}
	
}
