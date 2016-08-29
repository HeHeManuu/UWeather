

package com.example.uweather.activity;

import com.example.uweather.R;
import com.example.uweather.util.HttpCallbackListener;
import com.example.uweather.util.HttpUtil;
import com.example.uweather.util.Utility;

import android.R.string;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月29日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class WeatherActivity extends Activity {
	private LinearLayout weatherInfoLayout;
	/**
	 * 先是城市名
	 */
	private TextView cityNameText;
	
	/**
	 * 发布时间
	 */
	private TextView publishText;
	
	private  TextView weatherDespText;
	
	private TextView temp1Text;
	
	private TextView temp2Text;
	
	private TextView currentDateText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText=(TextView)findViewById(R.id.city_name);
		publishText=(TextView)findViewById(R.id.publish_text);
		weatherDespText=(TextView)findViewById(R.id.weather_desp);
		temp1Text=(TextView)findViewById(R.id.temp1);
		temp2Text=(TextView)findViewById(R.id.temp2);
		currentDateText=(TextView)findViewById(R.id.current_date);
		String countyCode=getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			//县代号去查询天气
			publishText.setText("同步中。。。");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
	}
	
	/**
	 * 查询县级别的天气代号
	 */
	private void queryWeatherCode(String countyCode){
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	
	/**
	 * 查询代号对应的天气
	 */
	private void  queryWeatherInfo(String weatherCode) {
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address, "weatherCode");
	}
	/**
	 * 查询代号后面的对应的天气
	 */
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array=response.split("\\|");
						if (array!=null&&array.length==2) {
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					//处理返回的天气情况
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
					
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败");
					}
				});
				
			}
		});
	}
	/**
	 * 从SharedPreference文件中读取天气信息
	 */
 private void  showWeather() {
	SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
	cityNameText.setText(preferences.getString("city_name",""));
	temp1Text.setText(preferences.getString("temp1", ""));
	temp2Text.setText(preferences.getString("temp2", ""	));
	weatherDespText.setText(preferences.getString("weather_desp", ""));
	publishText.setText("Today"+preferences.getString("publish_time", "")+"发布");
	currentDateText.setText(preferences.getString("current_date", ""));
	weatherInfoLayout.setVisibility(View.VISIBLE);
	cityNameText.setVisibility(View.VISIBLE);
	
}

}
