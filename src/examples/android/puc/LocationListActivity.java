package examples.android.puc;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LocationListActivity extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private SimpleCursorAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationlist);
		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.insert, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createLocation();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			// Create URI from selected element, and delete
			Uri uri = Uri.parse(LocationContentProvider.CONTENT_URI + "/" + info.id);

			getContentResolver().delete(uri, null, null);
			fillData();        
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createLocation() {
		Intent i = new Intent(this, EditLocationActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, EditLocationActivity.class);

		// Create URI from selected element, and edit
		Uri locationUri = Uri.parse(LocationContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(LocationContentProvider.CONTENT_ITEM_TYPE, locationUri);

		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	// Get elements from content provider and populate fields
	private void fillData() {
		String[] from = new String[] {LocationTable.NAME};
		int[] to = new int[] {R.id.label};
		String[] projection = {LocationTable.ID, LocationTable.NAME };

		Cursor location = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);

		adapter = new SimpleCursorAdapter(this, R.layout.row, location, from, to);
		setListAdapter(adapter);
	}
}