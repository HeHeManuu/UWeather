

package com.example.uweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import com.example.uweather.db.UWeatherDB;
import com.example.uweather.model.City;
import com.example.uweather.model.County;
import com.example.uweather.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Window;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月28日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class Utility {
	/**
	 * 解析和处理服务器返回的数据
	 */
	public synchronized static boolean handleProvincesResponse(UWeatherDB uWeatherDB,String response) {
		if (!TextUtils.isEmpty(response)) {
			String []allProvinces=response.split(",");
			if (allProvinces!=null&&allProvinces.length>0) {
				for(String p:allProvinces){
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//解析出来的数据存储到province表中
					uWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析返回的市级数据
	 */
	public  static boolean handleCitiesResponse(UWeatherDB uWeatherDB,String response,int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String []allCities=response.split(",");
			if (allCities!=null&&allCities.length>0) {
				for(String p:allCities){
					String[] array=p.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//解析出来的数据存储到province表中
					uWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析返回的县级数据
	 */
	public  static boolean handleCountiesResponse(UWeatherDB uWeatherDB,String response,int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String []allCounties=response.split(",");
			if (allCounties!=null&&allCounties.length>0) {
				for(String p:allCounties){
					String[] array=p.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//解析出来的数据存储到province表中
					uWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的json数据，并且保存到本地
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 将返回的数据保存到SharedPreference文件中
	 */
	public static void  saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTIme) {
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTIme);
		editor.putString("current_date", simpleDateFormat.format(new Date()));
		editor.commit();
	}

}
