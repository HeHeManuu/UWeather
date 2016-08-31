

package com.example.uweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.R.string;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月28日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					String response=null;
					StringBuilder url=new StringBuilder();
					url.append(address);
					HttpClient httpClient=new DefaultHttpClient();
					HttpGet httpGet=new HttpGet(url.toString());
					httpGet.addHeader("Accept-Language","zh-CN");
					HttpResponse httpResponse=httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode()==200) {
						HttpEntity entity=httpResponse.getEntity();
						response=EntityUtils.toString(entity,"utf-8");
						}
					if (listener!=null) {
						listener.onFinish(response);
					}
				} catch (Exception e) {
					if (listener!=null) {
						listener.onError(e);
					}
					
				}
				/*HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
					StringBuilder response=new StringBuilder();
					String line;
					while ((line=reader.readLine())!=null) {
						response.append(line);
						
					}
					if (listener!=null) {
						//回调
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (listener!=null) {
						listener.onError(e);
					}
				}finally {
					if (connection!=null) {
			            connection.disconnect();
					}
				}*/
				
			}
		}).start();
	}
	
	/**
	 * 通过网络路径返回图片
	 */
	public static void returnBitMap(final String url,final int i,final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL myFileUrl = null;
				Bitmap bitmap = null;
				try {
				   myFileUrl = new URL(url);
				  } catch (MalformedURLException e) {
				     e.printStackTrace();
				   }
				HttpURLConnection connection=null;
				try {
				    connection = (HttpURLConnection) myFileUrl.openConnection();
				    connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
				    bitmap = BitmapFactory.decodeStream(in);
				    in.close();
				
				if (listener!=null) {
					//回调
					listener.onFinsh(bitmap,i);
				}
				} catch (Exception e) {
				    e.printStackTrace();
			
				if (listener!=null) {
					//回调
					listener.onError(e);
				}
				}finally {
					connection.disconnect();
				}
				
			}
		}).start();
		
		}

}
