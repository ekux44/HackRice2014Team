package com.kuxhausen.hackrice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

	
	public static class PictureDB{
		public static final String TABLE_NAME = "daily_pics";
		public static final String FILE_PATH = "file_path";
		public static final String PICTURE_ID = "picture_id";
		
		/** The scheme part for this provider's URI */
		private static final String SCHEME = "content://";

		public static final String PATH_PICTURES = "pictures";
		
		/** The content:// style URL for this table */
		public static final Uri PICTURES_URI = Uri.parse(SCHEME + AUTHORITY + "/"+ PATH_PICTURES);

	}
	
	public static final String AUTHORITY = "com.kuxhausen.provider.hackrice.database";

	
	private static final String DATABASE_NAME = "hackrice.db";
	private static final int DATABASE_VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + PictureDB.TABLE_NAME + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY," + PictureDB.FILE_PATH
				+ " TEXT," + PictureDB.PICTURE_ID + " TEXT" + ");");

		this.onUpgrade(db, 1, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch(oldVersion){
			case 1:
			{		
			}
		}
	}
}
