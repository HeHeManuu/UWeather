

package com.example.uweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author   Manuuuuu 
 * Date:     2016年8月31日
 * Copyright (c) 2016, HeHeManuu@126.com All Rights Reserved.
*/
public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent2=new Intent(context,AutoUpdateReceiver.class);
		context.startService(intent2);

	}

}
