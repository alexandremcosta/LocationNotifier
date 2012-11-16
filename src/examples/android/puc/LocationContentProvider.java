package examples.android.puc;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LocationContentProvider extends ContentProvider {
	private LocationDatabaseHelper database;

	private static final int ALL_LOCATIONS = 10;
	private static final int LOCATION = 20;

	private static final String AUTHORITY = "examples.android.puc";
	private static final String BASE_PATH = "locations";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/locations";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/location";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, ALL_LOCATIONS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", LOCATION);
	}

	@Override
	public boolean onCreate() {
		database = new LocationDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		//Verify if required attributes correspond to table attributes
		checkColumns(projection);

		queryBuilder.setTables(LocationTable.TABLE);

		Cursor cursor = null;

		//Match URI to build query
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ALL_LOCATIONS:
			break;
		case LOCATION:
			queryBuilder.appendWhere(LocationTable.ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		//Execute query and return its cursor
		SQLiteDatabase db = database.getReadableDatabase();
		cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = database.getWritableDatabase();

		//Match URI to build insert
		int uriType = sURIMatcher.match(uri);
		long id = 0;
		switch (uriType) {
		case ALL_LOCATIONS:
			id = db.insert(LocationTable.TABLE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;

		//Match URI to build delete
		switch (uriType) {
		case ALL_LOCATIONS:
			rowsDeleted = sqlDB.delete(LocationTable.TABLE, selection, selectionArgs);
			break;
		case LOCATION:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(LocationTable.TABLE, LocationTable.ID + "=" + id, null);
			}
			else {
				rowsDeleted = sqlDB.delete(LocationTable.TABLE, LocationTable.ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;

		//Match URI to build insert
		switch (uriType) {
		case ALL_LOCATIONS:
			rowsUpdated = db.update(LocationTable.TABLE, values, selection, selectionArgs);
			break;
		case LOCATION:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(LocationTable.TABLE, values, LocationTable.ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(LocationTable.TABLE, values, LocationTable.ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { LocationTable.ACTIVE, LocationTable.LATITUDE, LocationTable.LONGITUDE,	LocationTable.NAME, LocationTable.ID };
		if (projection != null) {
			HashSet<String> requestedColumns =
					new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns =
					new HashSet<String>(Arrays.asList(available));

			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown Attributes to Location Provider");
			}
		}
	}
}
