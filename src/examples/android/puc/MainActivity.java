package examples.android.puc;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private static final String PREF_FILE = "preferences";
	private static final String STATE = "state";
	private ToggleButton toggleButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		SharedPreferences prefs = getSharedPreferences(PREF_FILE, 0);
		toggleButton.setChecked(prefs.getBoolean(STATE, false));
		
		TextView tv = (TextView) findViewById(R.id.textView);
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			tv.setText("GPS habilitado. Cuidado com o nível de sua bateria");
		else
			tv.setText(getNetworkInfoString());
		

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

	@Override
	protected void onStop() {
		super.onStop();
		// Obtendo objeto de preferências
		SharedPreferences prefs = getSharedPreferences(PREF_FILE, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(STATE, toggleButton.isChecked());
		editor.commit();
	}
	
	private String getNetworkInfoString() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			int netType = info.getType();
			int netSubtype = info.getSubtype();
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			if (info.isConnected()) {
				if (netType == ConnectivityManager.TYPE_WIFI) {
				    return "Sinal wifi.";
				} else if (netType == ConnectivityManager.TYPE_MOBILE) {
					if (telephonyManager.isNetworkRoaming())
						return "Atenção: celular em roaming. Este aplicativo pode gerar altos custos com sua operadora.";
					else if (netSubtype == TelephonyManager.NETWORK_TYPE_UMTS ||
							  netSubtype == TelephonyManager.NETWORK_TYPE_HSDPA ||
							  netSubtype == TelephonyManager.NETWORK_TYPE_HSPA ||
							  netSubtype == 15 ||
							  netSubtype == TelephonyManager.NETWORK_TYPE_HSUPA)
						return "Sinal 3G.";
					else
						return "Sinal ruim. Ative o GPS para melhor desempenho.";
				}
			} else
				return "Localização não disponível. Ative o GPS.";
		}
		return " ";
	}
}
