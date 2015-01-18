package fudan.se.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

	private MyLocation myLocation = null;

	public MyLocationListener(MyLocation location) {
		// TODO Auto-generated constructor stub
		myLocation = location;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		System.out.println("onLocationChanged" + location);
		myLocation
				.setLatAndLon(location.getLatitude(), location.getLongitude());
		System.out.println("onLocationChanged2" + location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		System.out.println("onStatusChanged");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		System.out.println("onProviderEnabled");
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		System.out.println("onProviderDisabled");
	}

}
