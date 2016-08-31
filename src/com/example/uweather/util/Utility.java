

package com.example.uweather.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.example.uweather.db.UWeatherDB;
import com.example.uweather.model.City;
import com.example.uweather.model.County;
import com.example.uweather.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
	 *//*
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
	}*/
	/**
	 * 解析返回html文档，提取相关信息
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			
		       
			Pattern pattern = Pattern.compile(Pattern.quote("{")+"\"nameen\":.*date.*"+Pattern.quote("}"));
		    String weatherInfo = null;
		    Matcher matcher = pattern.matcher(response);
			 while (matcher.find()) {
				 weatherInfo=matcher.group();
			}

			
			JSONObject jsonObject=new JSONObject(weatherInfo);
			String cityName=jsonObject.getString("cityname");
			String weatherCode=jsonObject.getString("city");
			String temp1=jsonObject.getString("temp")+"℃";
			String weatherDesp=jsonObject.getString("weather");
			String publishTime=jsonObject.getString("time");
			
			/**
			 * 添加最近四天的天气情况
			 */
			Document doc = Jsoup.parse(response);
			Elements nearDays=doc.select(".days7");
			String [] week={"周日","周一","周二","周三","周四 ","周五 ","周六 "};
			String days_temp=null;
			
			Calendar c = Calendar.getInstance();
			c.setTime(new Date(System.currentTimeMillis()));
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			for (int i = 0; i < 4; i++) {
				String select="div.days7 > ul > li:nth-child("+(i+2)+")";
				String icon=nearDays.select(select+"> i > img:nth-child(1)").get(0).attr("src");
				//icon=icon.substring(icon.indexOf("images/")+7);
				String weather=nearDays.select(select+"> i > img:nth-child(1)").get(0).attr("alt");
				String temp=nearDays.select(select+"> span").text();
				String day_of_week=week[(dayOfWeek+i)%7];
				String todaytemp=icon+"$$"+weather+"$$"+temp+"$$"+day_of_week;
				if (days_temp==null) {
					days_temp=todaytemp;
				}else {
					days_temp=days_temp+"##"+todaytemp;
				}
				
			}
			
			
			/*Document doc = Jsoup.parse(response);
			String cityName=doc.select("#sk > h1 > span > a:nth-child(1)").text();
			//String weatherCode=response.m;
			String temp1=doc.select("#wd").text();
			
			
			String weatherDesp=doc.select("#fl").text()+"  "+doc.select("#fx").text();
			String publishTime=doc.select("#sk > h2").text();*/
			saveWeatherInfo(context,cityName,weatherCode,temp1,days_temp,weatherDesp,publishTime);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 将返回的数据保存到SharedPreference文件中
	 */
	public static void  saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String nextDays,String weatherDesp,String publishTIme) {
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("next_days_temp", nextDays);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTIme);
		editor.putString("current_date", simpleDateFormat.format(new Date()));
		editor.commit();
	}
	
	

}
