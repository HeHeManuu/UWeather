

package com.example.uweather.activity;

import java.io.File;

import com.example.uweather.R;
import com.example.uweather.service.AutoUpdateService;
import com.example.uweather.util.HttpCallbackListener;
import com.example.uweather.util.HttpUtil;
import com.example.uweather.util.Utility;

import android.R.string;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月29日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class WeatherActivity extends Activity implements OnClickListener {
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
	
	private Button switchCity;
	
	private Button refreshWeather;
	
	/**
	 * 未来几天的天气显示控件的相关
	 */
	private ImageView nextImag;
	private ImageView next2Imag;
	private ImageView next3Imag;
	private ImageView next4Imag;
	
	private TextView nextWeatherText;
	private TextView next2WeatherText;
	private TextView next3WeatherText;
	private TextView next4WeatherText;
	
	private TextView nextTempText;
	private TextView next2TempText;
	private TextView next3TempText;
	private TextView next4TempText;
	
	private TextView nextdayText;
	private TextView next2dayText;
	private TextView next3dayText;
	private TextView next4dayText;
	
	private ImageView[] image={nextImag,next2Imag,next3Imag,next4Imag};
	
	private TextView[][] text={{nextWeatherText,nextTempText,nextdayText},
			{next2WeatherText,next2TempText,next2dayText},
			{next3WeatherText,next3TempText,next3dayText},
			{next4WeatherText,next4TempText,next4dayText}
	};
	
	
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
		
		nextImag=(ImageView)findViewById(R.id.next_weather_icon);
		next2Imag=(ImageView)findViewById(R.id.next2_weather_icon);
		next3Imag=(ImageView)findViewById(R.id.next3_weather_icon);
		next4Imag=(ImageView)findViewById(R.id.next4_weather_icon);
		
		nextWeatherText=(TextView)findViewById(R.id.next_weather_desp);
		next2WeatherText=(TextView)findViewById(R.id.next2_weather_desp);
		next3WeatherText=(TextView)findViewById(R.id.next3_weather_desp);
		next4WeatherText=(TextView)findViewById(R.id.next4_weather_desp);
		
		nextTempText=(TextView)findViewById(R.id.next_temp);
		next2TempText=(TextView)findViewById(R.id.next2_temp);
		next3TempText=(TextView)findViewById(R.id.next3_temp);
		next4TempText=(TextView)findViewById(R.id.next4_temp);
		
		nextdayText=(TextView)findViewById(R.id.next_day);
		next2dayText=(TextView)findViewById(R.id.next2_day);
		next3dayText=(TextView)findViewById(R.id.next3_day);
		next4dayText=(TextView)findViewById(R.id.next4_day);
		

		ImageView [] tets={nextImag,next2Imag,next3Imag,next4Imag};
		image=tets;
		
		TextView[][] tViews={{nextWeatherText,nextTempText,nextdayText},
				{next2WeatherText,next2TempText,next2dayText},
				{next3WeatherText,next3TempText,next3dayText},
				{next4WeatherText,next4TempText,next4dayText}
		};
		text=tViews;
		
		
		//temp2Text=(TextView)findViewById(R.id.temp2);
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
		
		switchCity=(Button)findViewById(R.id.switch_city);
		refreshWeather=(Button)findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
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
		String address="http://m.weather.com.cn/mweather/"+weatherCode+".shtml";
		
		
		
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


			@Override
			public void onFinsh(Bitmap bitmap, int i) {
				// TODO Auto-generated method stub
				
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
	//temp2Text.setText(preferences.getString("temp2", ""	));
	weatherDespText.setText(preferences.getString("weather_desp", ""));
	publishText.setText("Today"+preferences.getString("publish_time", "")+"发布");
	currentDateText.setText(preferences.getString("current_date", ""));
	
	/**
	 * 解析将来四天的天气信息
	 */
	String nextDaysInfo=preferences.getString("next_days_temp", "");
	String [] nextDays=nextDaysInfo.split("##");
	for (int i = 0; i < nextDays.length; i++) {
		String[] dayInfo=nextDays[i].split("\\$\\$");
		/*String path=Environment.getExternalStorageDirectory()+File.separator+dayInfo[i];
		 Bitmap bm = BitmapFactory.decodeFile(path); 
		 image[i].setImageBitmap(bm);*/
		 // image[i].setImageBitmap(HttpUtil.returnBitMap(dayInfo[0]));


		
		text[i][0].setText(dayInfo[1]);
		text[i][1].setText(dayInfo[2]);
		text[i][2].setText(dayInfo[3]);
		
		
		 
		
	}
	weatherInfoLayout.setVisibility(View.VISIBLE);
	cityNameText.setVisibility(View.VISIBLE);
	
	Intent intent=new Intent(this,AutoUpdateService.class);
	startService(intent);
	
}

 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=preferences.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
		
	}

	

}
