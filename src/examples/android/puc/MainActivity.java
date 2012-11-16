package examples.android.puc;

import java.util.ArrayList;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	public static LocationManager locationManager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        final Button manageButton = (Button) findViewById(R.id.manageButton);
        
        manageButton.setOnClickListener(new OnClickListener() {
			
        	@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, LocationListActivity.class);
				startActivity(i);
			}
		});
        
        toggleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					startBroadcastReceiver();
				} else {
					stopBroadcastReceiver();
				}
			}
		});
    }

	private void startBroadcastReceiver() {
		float radius = 100;
		
		String[] projection = {	LocationTable.ID, 
								LocationTable.NAME , 
								LocationTable.LATITUDE, 
								LocationTable.LONGITUDE, 
								LocationTable.ACTIVE
		};
		
		Cursor location = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, "active = 1", null, null);
		
		if (location.getCount() > 0) {
			location.moveToFirst();
//			do {
//				String name = location.getString(location.getColumnIndexOrThrow(LocationTable.NAME));
//				Double latitude = location.getDouble(location.getColumnIndexOrThrow(LocationTable.LATITUDE));
//				Double longitude = location.getDouble(location.getColumnIndexOrThrow(LocationTable.LONGITUDE));
//				Intent intent = new Intent("examples.android.puc.LOCATION_RECEIVER");
//				intent.putExtra("name", name);
//				intent.putExtra("latitude", latitude);
//				intent.putExtra("longitude", longitude);
//				PendingIntent broadcastIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//				locationManager.addProximityAlert(
//		                latitude,
//		                longitude,
//		                radius, // the radius of the central point of the alert region, in meters
//		                -1, // -1 to indicate no expiration 
//		                broadcastIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
//		    	);
//				
//			} while (location.moveToNext());
			Toast.makeText(MainActivity.this, "Active Locations: " + location.getCount(), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(MainActivity.this, "There are no active locations", Toast.LENGTH_SHORT).show();
		}
    }
    
    private void stopBroadcastReceiver() {
    	Toast.makeText(MainActivity.this, "Location Notifier Stopped", Toast.LENGTH_SHORT).show();
    }
}
