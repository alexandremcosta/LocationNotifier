package examples.android.puc;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class EditLocationActivity extends MapActivity {
	private EditText reminderEditText;
	private EditText latEditText;
	private EditText longEditText;
	private CheckBox activeCheckBox;
	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;
	private IOverlay tapOverlay;
	private Uri todoUri;
	private Drawable drawable;
	
	Handler h = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();

			int latitude = data.getInt("latitude");
			int longitude = data.getInt("longitude");
			GeoPoint p = new GeoPoint(latitude, longitude);
			OverlayItem overlayItem = new OverlayItem(p, "Pin", "Lembrete aqui!");
			tapOverlay.addOverlay(overlayItem);
			mapView.getOverlays().add(tapOverlay);
			
			Double lat = latitude / 1E6;
			Double lon = longitude / 1E6;
			latEditText.setText(String.valueOf(lat));
			longEditText.setText(String.valueOf(lon));
		}
	};

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.editlocation);
		
		setMapViewUp();
		
		reminderEditText = (EditText) findViewById(R.id.nameEditText);
		latEditText = (EditText) findViewById(R.id.latEditText);
		longEditText = (EditText) findViewById(R.id.longEditText);
		activeCheckBox = (CheckBox) findViewById(R.id.activeCheckBox);

		Button confirmButton = (Button) findViewById(R.id.confirmButton);

		Bundle extras = getIntent().getExtras();

		todoUri = (bundle == null) ? null : (Uri) bundle.getParcelable(LocationContentProvider.CONTENT_ITEM_TYPE);

		if (extras != null) {
			todoUri = extras.getParcelable(LocationContentProvider.CONTENT_ITEM_TYPE);
			fillData(todoUri);
		}

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(reminderEditText.getText().toString()) ||
						TextUtils.isEmpty(latEditText.getText().toString()) ||
						TextUtils.isEmpty(longEditText.getText().toString())) {
					Toast.makeText(EditLocationActivity.this, "Don't leave blank fields", Toast.LENGTH_LONG).show();
				}
				else {
					setResult(RESULT_OK);
					finish();
				}
			}

		});
	}

	private void setMapViewUp() {
		drawable = this.getResources().getDrawable(R.drawable.mmarker);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		tapOverlay = new IOverlay(drawable, h);
		mapView.getOverlays().add(tapOverlay);
		
//		myLocationOverlay = new MyLocationOverlay(this, mapView);
//		mapView.getOverlays().add(myLocationOverlay);
//		myLocationOverlay.enableCompass();
//		myLocationOverlay.enableMyLocation();
//		myLocationOverlay.runOnFirstFix(new Runnable() {
//			@Override
//			public void run() {
//				mapView.getController().animateTo(myLocationOverlay.getMyLocation());
//			}
//		});
		
	}

	private void fillData(Uri uri) {
		String[] projection = {LocationTable.NAME, LocationTable.LATITUDE, LocationTable.LONGITUDE, LocationTable.ACTIVE};
		Cursor cursor =	getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			Double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationTable.LATITUDE));
			Double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationTable.LONGITUDE));
			reminderEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.NAME)));
			latEditText.setText(latitude.toString());
			longEditText.setText(longitude.toString());
			
			if (cursor.getInt(cursor.getColumnIndexOrThrow(LocationTable.ACTIVE)) == 0) 
				activeCheckBox.setChecked(false);
			else 
				activeCheckBox.setChecked(true);
			
			cursor.close();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(LocationContentProvider.CONTENT_ITEM_TYPE, todoUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
		
		String reminder = reminderEditText.getText().toString();
		String latString = latEditText.getText().toString();
		String longString = longEditText.getText().toString();
		int active;
		String activeToast;
		if (activeCheckBox.isChecked()) {
			active = 1;
			activeToast = "Active";
		} else {
			active = 0;
			activeToast = "Inactive";
		}
		
		double latitude, longitude;
		
		if (latString == null || latString.isEmpty())
			latitude = 0.0;
		else {
			latitude = Double.parseDouble(latString);
		}
		if (latString == null || latString.isEmpty())
			longitude = 0.0;
		else
			longitude = Double.parseDouble(longString);

		
		ContentValues values = new ContentValues();
		values.put(LocationTable.NAME, reminder);
		values.put(LocationTable.LATITUDE, latitude);
		values.put(LocationTable.LONGITUDE, longitude);
		values.put(LocationTable.ACTIVE, active);

		if (todoUri == null) {
			// New todo
			todoUri = getContentResolver().insert(LocationContentProvider.CONTENT_URI,	values);
			
		}
		else {
			// Update todo
			getContentResolver().update(todoUri, values, null, null);
		}
		

		Toast.makeText(EditLocationActivity.this, "Notification " + activeToast, Toast.LENGTH_LONG).show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
