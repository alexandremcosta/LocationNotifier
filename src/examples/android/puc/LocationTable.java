package examples.android.puc;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationTable {
	public static final String TABLE = "LEMBRETE";
	public static final String ID = "_id";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ACTIVE = "active";
	public static final String NAME = "name";

	private static final String DATABASE_CREATE = "create table " + TABLE
			+ "(" + ID + " integer primary key autoincrement, "
			+ LATITUDE + " real not null, " + LONGITUDE + " real not null, "
			+ NAME + " text not null, "
			+ ACTIVE + " integer not null" + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, 
			int oldVersion,
			int newVersion) {
		Log.w(LocationTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}

}
