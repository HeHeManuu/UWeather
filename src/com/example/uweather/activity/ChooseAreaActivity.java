

package com.example.uweather.activity;


import java.util.ArrayList;
import java.util.List;

import com.example.uweather.R;
import com.example.uweather.db.UWeatherDB;
import com.example.uweather.model.City;
import com.example.uweather.model.County;
import com.example.uweather.model.Province;
import com.example.uweather.util.HttpCallbackListener;
import com.example.uweather.util.HttpUtil;
import com.example.uweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月28日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class ChooseAreaActivity extends Activity {
	
	public static final int  LEVEL_PROVINCE=0;
	public static final int  LEVEL_CITY=1;
	public static final int  LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private UWeatherDB uWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	
	private List<City> cityList;
	
	private List<County> countyList;
	
	/**
	 * 当前选中的省份
	 */
    private Province selectedProvince;
    
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.choose_area);
    	listView=(ListView)findViewById(R.id.list_view);
    	titleView =(TextView)findViewById(R.id.title_text);
    	
    	/*SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
    	if (preferences.getBoolean("city_selected", false)) {
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}*/
    	
    	adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
    	listView.setAdapter(adapter);//设置适配器
    	uWeatherDB=UWeatherDB.getInstance(this);
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				if (currentLevel==LEVEL_PROVINCE) {
					selectedProvince=provinceList.get(index);
					queryCities();
				}else if (currentLevel==LEVEL_CITY) {
					selectedCity=cityList.get(index);
					queryCounties();
				}else if (currentLevel==LEVEL_COUNTY) {
					String countyCode=countyList.get(index).getCountyCode();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
				
			}
    		
		});
    	queryProvinces();
    	
    }
    
    /**
     * 加载所有省的 数据，优先从数据库中 没有从 服务器查询
     */
    private void  queryProvinces() {
		provinceList=uWeatherDB.loadProvinces();
		if (provinceList.size()>0) {
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
    /**
     * 查询选中省的所有市，优先数据库否则 服务器查询
     */
    private void queryCities(){
    	cityList=uWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
    }
    
    private void queryCounties(){
    	countyList=uWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size()>0) {
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
    }
    
    /**
     * 根据传入的代号和类型从服务器上面查询市县的数据
     */
    private void queryFromServer(final String code,final String type){
    	String address;
    	if (!TextUtils.isEmpty(code)) {
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else  {
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
    	
    	showProgressDialog();
    	HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result=false;
				if ("province".equals(type)) {
					result=Utility.handleProvincesResponse(uWeatherDB, response);
				}else if ("city".equals(type)) {
					result=Utility.handleCitiesResponse(uWeatherDB, response, selectedProvince.getId());
					
				}else if ("county".equals(type)) {
					result=Utility.handleCountiesResponse(uWeatherDB, response, selectedCity.getId());
				}  
				if (result) {
					//通过runOnUiThread方法回到主线程处理  从子线程切换到主线程  异步消息处理机制
					runOnUiThread( new Runnable() {
						public void run() {
                          closeProgressDialog();
                          if ("province".equals(type)) {
							queryProvinces();
						}else if ("city".equals(type)) {
							queryCities();
						}else if ("county".equals(type)) {
							queryCounties();
						} 
						}
					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new  Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		});
    	
    	
    }

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if (progressDialog==null) {
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog(){
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获back按键，根据当前的级别来判断，此时应该返回 市，省列表，还是直接退出
	 */
    @Override
    public void onBackPressed() {
    	if (currentLevel==LEVEL_COUNTY) {
			queryCities();
		}else if (currentLevel==LEVEL_CITY) {
			queryProvinces();
		}else {
			finish();
		}
    }

}
