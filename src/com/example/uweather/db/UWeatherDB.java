

package com.example.uweather.db;

import java.util.ArrayList;
import java.util.List;

import com.example.uweather.model.City;
import com.example.uweather.model.County;
import com.example.uweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月28日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class UWeatherDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME="u_weather";
	
	/**
	 * 数据库版本
	 */
	public static final int  VERSION=1;
	
	private static UWeatherDB uWeatherDB;
	private SQLiteDatabase db;
	
	/**
	 * 构造方法
	 */
	private UWeatherDB(Context context){
		UWeatherDBHelper dbHelper=new UWeatherDBHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	/**
	 * 获取uweatherDb实例
	 */
	public synchronized static UWeatherDB getInstance(Context context){
		if (uWeatherDB==null) {
			uWeatherDB=new UWeatherDB(context);
		}
		return uWeatherDB;
	}
	
	/**
	 * 将province 实例存到数据库中
	 */
	public void saveProvince(Province province){
		if (province!=null) {
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code",province.getProvinceCode());
			db.insert("Province", null, values);
		
		}
		
	}
	/**
	 * 从数据库中 读取 所有省份的信息
	 */
	public List<Province> loadProvinces() {
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);	
			} while (cursor.moveToNext());
			
		}
		return list;
	}
	
	/**
	 * 保存city到数据库中
	 */
	public void saveCity(City city){
		if (city!=null) {
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id",city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	/**
	 * 读取某省下所有城市的信息
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);	
			} while (cursor.moveToNext());
			
		}
		return list;
	}
	/**
	 * 将county实例存到数据库中
	 */
	public void  saveCounty(County county) {
		if (county!=null) {
			ContentValues values=new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	/**
	 * 读取某城市下县城信息
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);	
			} while (cursor.moveToNext());
			
		}
		return list;
	}

}
