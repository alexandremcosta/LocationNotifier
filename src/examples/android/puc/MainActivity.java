package examples.android.puc;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
					startAllActiveNotifications();
				} else {
					stopAllActiveNotifications();
				}
			}
		});
    }

	private void startAllActiveNotifications() {
		String[] projection = {	LocationTable.ID, 
								LocationTable.NAME , 
								LocationTable.LATITUDE, 
								LocationTable.LONGITUDE, 
								LocationTable.ACTIVE
		};
		
		Cursor location = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, "active = 1", null, null);
		
		if (location.getCount() > 0) {
			location.moveToFirst();
			do {
				int id = location.getInt(location.getColumnIndexOrThrow(LocationTable.ID));
				String name = location.getString(location.getColumnIndexOrThrow(LocationTable.NAME));
				Double latitude = location.getDouble(location.getColumnIndexOrThrow(LocationTable.LATITUDE));
				Double longitude = location.getDouble(location.getColumnIndexOrThrow(LocationTable.LONGITUDE));
				Intent intent = new Intent(this, AlertService.class);
				intent.putExtra("id", id);
				intent.putExtra("name", name);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				startService(intent);
			} while (location.moveToNext());
			Toast.makeText(MainActivity.this, "Active Locations: " + location.getCount(), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(MainActivity.this, "There are no active locations", Toast.LENGTH_SHORT).show();
		}
    }
    
    private void stopAllActiveNotifications() {
    	Toast.makeText(MainActivity.this, "Notifier Stopped", Toast.LENGTH_SHORT).show();
    	stopService(new Intent(this, AlertService.class));
    }
}
