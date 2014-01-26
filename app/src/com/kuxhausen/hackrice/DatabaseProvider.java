package com.kuxhausen.hackrice;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseProvider extends ContentProvider {

	DatabaseHelper mOpenHelper;

	/**
	 * A projection map used to select columns from the database
	 */
	private static HashMap<String, String> sPicturesProjectionMap;
	/**
	 * A UriMatcher instance
	 */
	private static final UriMatcher sUriMatcher;
	/**
	 * Constants used by the Uri matcher to choose an action based on the
	 * pattern of the incoming URI
	 */
	private static final int Pictures = 1;

	/**
	 * A block that instantiates and sets static objects
	 */
	static {

		/*
		 * Creates and initializes the URI matcher
		 */
		// Create a new instance
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		{
			// Add a pattern that routes URIs terminated with "groups" to a
			// GROUPS operation
			sUriMatcher.addURI(DatabaseHelper.AUTHORITY, DatabaseHelper.PictureDB.PATH_PICTURES, Pictures);
			// Creates a new projection map instance. The map returns a column
			// name
			// given a string. The two are usually equal.
			sPicturesProjectionMap = new HashMap<String, String>();

			// Maps the string "_ID" to the column name "_ID"
			sPicturesProjectionMap.put(BaseColumns._ID, BaseColumns._ID);

			sPicturesProjectionMap.put(DatabaseHelper.PictureDB.FILE_PATH, DatabaseHelper.PictureDB.FILE_PATH);
			sPicturesProjectionMap.put(DatabaseHelper.PictureDB.PICTURE_ID, DatabaseHelper.PictureDB.PICTURE_ID);
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Constructs a new query builder and sets its table name
		String table = null;

		/**
		 * Choose the projection and adjust the "where" clause based on URI
		 * pattern-matching.
		 */
		switch (sUriMatcher.match(uri)) {

		case Pictures:
			table = (DatabaseHelper.PictureDB.TABLE_NAME);
			break;
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.delete(table, selection, selectionArgs);

		this.getContext().getContentResolver().notifyChange(uri, null);

		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		// Constructs a new query builder and sets its table name
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		/**
		 * Choose the projection and adjust the "where" clause based on URI
		 * pattern-matching.
		 */
		switch (sUriMatcher.match(uri)) {
		case Pictures:
			qb.setTables(DatabaseHelper.PictureDB.TABLE_NAME);
			qb.setProjectionMap(sPicturesProjectionMap);
			break;
		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		long insertId = db.insert(qb.getTables(), null, values);
		if (insertId == -1) {
			// insert failed, do update?
		}

		this.getContext().getContentResolver().notifyChange(uri, null);

		return null;
	}

	@Override
	public boolean onCreate() {
		// Creates a new helper object. Note that the database itself isn't
		// opened until
		// something tries to access it, and it's only created if it doesn't
		// already exist.
		mOpenHelper = new DatabaseHelper(getContext());

		// Assumes that any failures will be reported by a thrown exception.
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Constructs a new query builder and sets its table name
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (sUriMatcher.match(uri)) {
		case Pictures:
			qb.setTables(DatabaseHelper.PictureDB.TABLE_NAME);
			qb.setProjectionMap(sPicturesProjectionMap);
			
			break;
		default:
			// If the URI doesn't match any of the known patterns
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		
		/*
		 * Performs the query. If no problems occur trying to read the database,
		 * then a Cursor object is returned; otherwise, the cursor variable
		 * contains null. If no records were selected, then the Cursor object is
		 * empty, and Cursor.getCount() returns 0.
		 */
		Cursor c = qb.query(db,
				projection, // The columns to return from the query
				selection, // The columns for the where clause
				selectionArgs, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder 
				);
		
		// Tells the Cursor what URI to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;

		// Does the update based on the incoming URI pattern
		switch (sUriMatcher.match(uri)) {
			case Pictures:
				// Does the update and returns the number of rows updated.
				count = db.update(DatabaseHelper.PictureDB.TABLE_NAME, // The database table name.
						values, // A map of column names and new values to use.
						selection, // The where clause column names.
						selectionArgs // The where clause column values to select on.
						);
				break;
			// If the incoming pattern is invalid, throws an exception.
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		/*
		 * Gets a handle to the content resolver object for the current context,
		 * and notifies it that the incoming URI changed. The object passes this
		 * along to the resolver framework, and observers that have registered
		 * themselves for the provider are notified.
		 */
		getContext().getContentResolver().notifyChange(uri, null);

		// Returns the number of rows updated.
		return count;
	}

}
