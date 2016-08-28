

package com.example.uweather.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月28日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class UWeatherDBHelper extends SQLiteOpenHelper {
    
	
	
	public UWeatherDBHelper(Context context, String name, CursorFactory factory, int version
			) {
		super(context, name, factory, version);
		
	}

	/**
	 * Province 建表
	 */
	public static final String CREATE_PROVINCE ="create table Province("
			+"id integer primary key autoincrement,"
			+"province_name text,"
			+"province_code text)";
	
	/**
	 * City 建表语句
	 */
	public static final String CREATE_CITY ="create table City("
			+"id integer primary key autoincrement,"
			+"city_name text,"
			+"city_code text,"
			+"province_id integer)";
	
	/**
	 * County建表
	 */
	public static final String CREATE_COUNTY ="create table County("
			+"id integer primary key autoincrement,"
			+"county_name text,"
			+"county_code text,"
			+"city_id integer)";
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_PROVINCE);
		database.execSQL(CREATE_CITY);
		database.execSQL(CREATE_COUNTY);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
