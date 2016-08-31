

package com.example.uweather.util;

import android.graphics.Bitmap;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月28日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public interface HttpCallbackListener {
	void onFinish(String response);
	
	void onError(Exception e);
	
	void onFinsh(Bitmap bitmap,int i);

}
