package com.V4Creations.fingerprintmagic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Vishal Vijay
 */

public class DatabaseHelper extends SQLiteOpenHelper {

	String TAG = "DatabaseHelper";

	DatabaseHelper(Context context) {
		super(context, FingerprintDB.DATABASE_NAME, null,
				FingerprintDB.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FingerprintDB.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(FingerprintDB.DROP_TABLE);
	}
}
