package examples.android.puc;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class EditLocationActivity extends Activity {
	private EditText nameEditText;
	private EditText latEditText;
	private EditText longEditText;
	private CheckBox activeCheckBox;

	private Uri todoUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.editlocation);

		nameEditText = (EditText) findViewById(R.id.nameEditText);
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
				if (TextUtils.isEmpty(nameEditText.getText().toString()) ||
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

	private void fillData(Uri uri) {
		String[] projection = {LocationTable.NAME, LocationTable.LATITUDE, LocationTable.LONGITUDE, LocationTable.ACTIVE};
		Cursor cursor =	getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			Double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationTable.LATITUDE));
			Double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationTable.LONGITUDE));
			nameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.NAME)));
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
		
		String name = nameEditText.getText().toString();
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
		values.put(LocationTable.NAME, name);
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

}
