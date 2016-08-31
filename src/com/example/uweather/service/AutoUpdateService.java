

package com.example.uweather.service;

import com.example.uweather.receiver.AutoUpdateReceiver;
import com.example.uweather.util.HttpCallbackListener;
import com.example.uweather.util.HttpUtil;
import com.example.uweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月31日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
				
			}
		}).start();
		AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
		int anHour=60*60*1000;// eight hours
		long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
		Intent i=new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pIntent=PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pIntent);
		
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 * 更新天气信息
	 */
	private void updateWeather(){
		SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode=preferences.getString("weather_code", "");
		String address="http://m.weather.com.cn/mweather/"+weatherCode+".shtml";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinsh(Bitmap bitmap, int i) {
				
			}
			
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
				
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				
			}
		});
	}

}
