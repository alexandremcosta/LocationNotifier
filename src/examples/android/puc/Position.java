package examples.android.puc;

public class Position {
	private static double latitude;
	private static double longitude;
	
	private static double lastLatitude;
	private static double lastLongitude;
	
	public static double getLatitude() {
		return latitude;
	}
	public static void setLatitude(double latitude) {
		Position.lastLatitude = Position.latitude;
		Position.latitude = latitude;
	}
	public static double getLongitude() {
		return longitude;
	}
	public static void setLongitude(double longitude) {
		Position.lastLongitude = Position.longitude;
		Position.longitude = longitude;
	}
	public static double getLastLatitude() {
		return lastLatitude;
	}
	public static double getLastLongitude() {
		return lastLongitude;
	}
	public static double getDistance() {
		if(latitude != 0 && longitude != 0 && lastLatitude != 0 && lastLongitude != 0) {
			return distFrom(latitude, longitude, lastLatitude, lastLongitude);
		}
		return 0;
	}
	
	public static double distFrom(double latitude2, double longitude2, double lastLatitude2, double lastLongitude2) {
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lastLatitude2-latitude2);
	    double dLng = Math.toRadians(lastLongitude2-longitude2);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(latitude2)) * Math.cos(Math.toRadians(lastLatitude2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return dist * meterConversion;
	}
}
