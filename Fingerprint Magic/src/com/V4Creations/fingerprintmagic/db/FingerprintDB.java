package com.V4Creations.fingerprintmagic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Vishal Vijay
 */

public class FingerprintDB {

	public static final String DATABASE_NAME = "opensudoku";
	public static final int DATABASE_VERSION = 8;
	public static final String TABLE_NAME = "namecollection";
	public static final String COL_NAME = "name";
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + COL_NAME + " VARCHAR(50) primary key);";
	public static final String DROP_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;
	private final Context mContext;

	private DatabaseHelper mDatabaseHelper;

	public FingerprintDB(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(mContext);
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public long insertName(String name) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_NAME, name);

		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		return db.insert(TABLE_NAME, COL_NAME, contentValues);
	}

	public int deleteName(String name) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		return db.delete(TABLE_NAME, COL_NAME + "= '" + name + "'", null);
	}

	public int deleteAllNames() {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		return db.delete(TABLE_NAME, null, null);
	}

	public Cursor getAllNames() {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		return db.query(TABLE_NAME, new String[] { COL_NAME }, null, null,
				null, null, null);
	}
}
