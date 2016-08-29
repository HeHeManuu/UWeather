

package com.example.uweather.util;

import com.example.uweather.db.UWeatherDB;
import com.example.uweather.model.City;
import com.example.uweather.model.County;
import com.example.uweather.model.Province;

import android.text.TextUtils;

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

}
