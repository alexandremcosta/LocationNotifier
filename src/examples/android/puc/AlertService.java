package examples.android.puc;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class AlertService extends Service {

	private LocationManager locationManager;
	private static final String TAG = "AlertService";
	private ArrayList<PendingIntent> pendingIntents;
	private ArrayList<LocationReceiver> locationReceivers;
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		pendingIntents = new ArrayList<PendingIntent>();
		locationReceivers = new ArrayList<LocationReceiver>();
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String name = intent.getStringExtra("name");
		Double latitude = intent.getDoubleExtra("latitude", 0);
		Double longitude = intent.getDoubleExtra("longitude", 0);
		Log.d(TAG, "onStartCommand: " + name );
		String action = "examples.android.puc.LOCATION_RECEIVER/" + name;
		IntentFilter filter = new IntentFilter(action);
		LocationReceiver lr = new LocationReceiver();
		locationReceivers.add(lr);
		registerReceiver(lr, filter);
		Intent receiverIntent = new Intent(action);
		receiverIntent.putExtra("id", intent.getIntExtra("id", 0));
		receiverIntent.putExtra("name", name);
		receiverIntent.putExtra("latitude", latitude);
		receiverIntent.putExtra("longitude", longitude);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		locationManager.addProximityAlert(latitude, 
										  longitude, 
										  1500, 
										  -1, 
										  proximityIntent);
		pendingIntents.add(proximityIntent);
		return START_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy: " + pendingIntents.size() + " proximity alerts");
		for(PendingIntent pendingIntent : pendingIntents) {
			locationManager.removeProximityAlert(pendingIntent);
		}
		for(LocationReceiver locationReceiver : locationReceivers) {
			unregisterReceiver(locationReceiver);
		}
	}
	
}
