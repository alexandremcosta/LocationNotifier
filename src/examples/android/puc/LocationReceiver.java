package examples.android.puc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
	    Location loc = (Location) b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
	    double latitude = 0;
	    double longitude = 0;
	    double distanceFromLastUpdate = 0;
	    
	    if (loc == null) {
	    	loc = getLastKnowLocation();
	    } else {	    
			Position.setLatitude(loc.getLatitude());
			Position.setLongitude(loc.getLongitude());
	    }
	    
	    if (loc != null) {
	    	latitude = loc.getLatitude();
	    	longitude = loc.getLongitude();
	    	distanceFromLastUpdate = Position.getDistance();
	    }
	    	
		String display = "Latitude: " + latitude + "\n" +
						 "Longitude: " + longitude + "\n" +
						 "Distance: " + distanceFromLastUpdate;
		
		Toast.makeText(context, display, Toast.LENGTH_LONG).show(); 

	}
	

	public Location getLastKnowLocation() {
		return MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
}
