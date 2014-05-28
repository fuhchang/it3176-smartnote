package com.SQLLite.it3176;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class mySQLLite {
	
	public static final String note_id = "_id";
	public static final String note_name = "note_name";
	public static final String note_content = "note_content";
	public static final String note_active = "active";
	
	
	private static final String database_name = "notedb";
	private static final String database_table = "noteTable";
	private static final int database_version = 1;
	
	private DBHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	//help to create database or upgrade database
	private static class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context) {
			super(context, database_name, null, database_version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated  stub
			
			db.execSQL(
				"CREATE TABLE " + database_table + " ("	
				+ note_id + " INTEGER AUTOINCREMENT, " 
				+ note_name + " varchar(50) NOT NULL, "
				+ note_content + "varchar(255), "
				+ note_active + "varchar(50) NOT NULL, "
				+ "note_time datetime NOT NULL NOW()); "
				
			);
			
			//db.execSQL("Alter TABLE " + database_table + "ADD active varchar(50) NOT NULL, category varchar(50) NOT NULL, note_time datetime NOT NULL NOW() ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + database_name);
			onCreate(db);
			
		}
		
	}
	
	public mySQLLite(Context c){
		ourContext = c;
	}
	
	public mySQLLite open() throws SQLException{
		ourHelper = new DBHelper(ourContext);
		
		ourDatabase = ourHelper.getWritableDatabase();
		
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}
	public long createEntry(String name, String content){
		ContentValues cv = new ContentValues();
		cv.put(note_name, name);
		cv.put(note_content, content);
		return ourDatabase.insert(database_table, null, cv);
	}
	
	
}
