package examples.android.puc;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	public static LocationManager locationManager;
	private PendingIntent broadcastIntent;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	Intent intent = new Intent("examples.android.puc.LOCATION_RECEIVER");
    	broadcastIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	
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
    	Toast.makeText(MainActivity.this, "Location Notifier Started", Toast.LENGTH_SHORT).show();
    	
    	long minTime = 30 * 1000;
    	float minDistance = 10;

    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, broadcastIntent);
    }
    
    private void stopBroadcastReceiver() {
    	Toast.makeText(MainActivity.this, "Location Notifier Stopped", Toast.LENGTH_SHORT).show();
    	locationManager.removeUpdates(broadcastIntent);
    }
}
