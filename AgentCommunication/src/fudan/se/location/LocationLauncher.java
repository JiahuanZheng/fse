package fudan.se.location;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationLauncher {
	/*
	 * 传来的location引用指向的实例中的坐标会随着位置的变化而发生变化。
	 * 所以，如果希望一个Mylocation类的实例中的坐标代表的是使用者
	 * 当前的坐标，则可以将其传入这个方法中。
	 * */
	public static void launchLocationService(Context context,MyLocation location) {
		LocationManager locationManager = null;
		LocationListener locationListener = new MyLocationListener(location);
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);

	}
}
